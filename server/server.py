#!flask/bin/python
from flask import Flask, request, jsonify
from flask_restful import reqparse
from pymongo import MongoClient
from datetime import datetime
from OpenSSL import SSL
import json
from bson import json_util
import requests
from datadog import statsd

app = Flask(__name__)
client = MongoClient()
db = client.test

# Increment a counter for datadog metrics.
statsd.increment('page.views')
base_url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?'
key = 'AIzaSyBuoo0QB2PhkrJpNww_yTq4dGwiJnWL-AQ'

places = requests.get('https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=43.0481,-76.1474&radius=500&type=restaurant&key=AIzaSyBuoo0QB2PhkrJpNww_yTq4dGwiJnWL-AQ')

@app.route('/places', methods=['GET'])
def places():
    return app.response_class(places.content, content_type='application/json')

@app.route('/get_places', methods=['POST'])
def get_places():
    latitude = request.json['lat']
    longitude = request.json['lng']
    radius = request.json['radius']
    keyword = request.json['keyword']
    url = base_url + 'location=' + latitude + ',' + longitude + '&radius=' + radius + '&key=' + key + '&keyword=' + keyword
    places = requests.get(url)
    return app.response_class(places.content, content_type='application/json')

# gets directions for tour from Google Maps Directions API
directions_url = 'https://maps.googleapis.com/maps/api/directions/json?'
@app.route('/get_directions', methods=['GET'])
def get_directions():
    origin = request.args.get('origin')
    dest = request.args.get('destination')
    waypoints = request.args.get('waypoints')
    travel_mode = request.args.get('mode')
    url = directions_url + 'origin=' + origin + '&destination=' + dest + '&mode=' + travel_mode
    if waypoints != 'none':
        url = url + '&waypoints=' + waypoints
    else:
        print '\n\nNO WAYPOINTS\n\n'
    url = url + '&key=' + key
    directions = requests.get(url)
    print 'url', url
    return app.response_class(directions.content, content_type='application/json')

# clears the accounts database (NOT FOR PRODUCTION ONLY FOR TESTING PRUPOSES, WE SHOULD DELETE ONCE WE GET EVERYTHING WORKING!!!!!!!!)
# TODO: delete a single user (if they deactivate their account)
@app.route('/delete_accounts', methods=['PUT'])
def delete_users():
    auth = db.auth
    auth.remove( { } )
    return jsonify(response = {'result' : 'removed'})

# clears single account from database
# used when user deactivates account
@app.route('/delete_user', methods=['PUT'])
def delete_user():
    auth = db.auth
    username = request.args.get('user')
    password = request.args.get('pw')
    # TODO: remove this user from collection
    auth.remove( { } )
    return jsonify(response = {'result' : 'removed'})

#################### LOGIN RELATED API CALLS ####################
# login to an existing account
@app.route('/login/auth', methods=['GET'])
def login():
    username = request.args.get('user')
    password = request.args.get('pw')
    auth = db.auth
    user = auth.find_one({
        'username'  : username
    })
    output = []
    if user:
        # output = 'Attempt login for %s' % username
        # return output
        if password == user['password']:
            # get all user info and format
            output.append({
                'result' : 'login successful',
                '_id'       : str(user['_id']),
                'username'  : user['username'],
                'password'  : user['password'],
                'email'     : user['email'],
                'radius'    : user['radius'],
            	'rating'  	: user['rating'],
            	'travelled' : user['travelled'],
            	'created'	: user['created'],
            	'guided'	: user['guided'],
            	'shopping_cart'	: user['shopping_cart']
            })
        else:
            output.append({
                'result' : 'incorrect password',
                '_id'       : '',
                'username'  : '',
                'password'  : '',
                'email'     : '',
                'radius'    : '',
            	'rating'  	: '',
            	'travelled' : '',
            	'created'	: '',
            	'guided'	: '',
            	'shopping_cart'	: ''
            })
    else:
        output.append({
            'result' : 'User not found',
            '_id'       : '',
            'username'  : '',
            'password'  : '',
            'email'     : '',
            'radius'    : '',
            'rating'  	: '',
            'travelled' : '',
            'created'	: '',
            'guided'	: '',
            'shopping_cart'	: ''
        })
    return jsonify(response = output)

# this method is for creating a new account
@app.route('/login/new', methods=['POST'])
def new_login():
    auth = db.auth
    username = request.json['username']
    password = request.json['password']
    email = request.json['email']
    radius = 1000   # radius in meters ~.6 miles
    # TODO: check that username or email is not in login collection
    username_exists = auth.find_one({'username' : username})
        if username_exists:
        return jsonify({'result' : 'username is taken'})
    email_in_use = auth.find_one({'email' : email})
    if email_in_use:
        return jsonify({'result' : 'email is already linked to an account'})
    # insert_one function throws an error with the return type (not sure why!)
    user_id = auth.insert({
        'username' : username,
        'password' : password,
        'email'    : email,
        'radius'   : radius,
    	'rating'  	: 0,
    	'travelled' : [],
    	'created'	: [],
    	'guided'	: [],
    	'shopping_cart'	: []
    })
    new_user = auth.find_one({'_id' : user_id})
    return jsonify(response = {
        'result'    : 'account created successfully'
    })

#returns a list of all the users in the database
@app.route('/users', methods=['GET'])
def get_all_users():
    auth = db.auth
    output = []
    for user in auth.find():
        output.append({
            '_id'       : str(user['_id']),
            'username'  : user['username'],
            'password'  : user['password'],
            'email'     : user['email'],
            'radius'    : user['radius'],
        	'rating'  	: user['rating'],
        	'travelled' : user['travelled'],
        	'created'	: user['created'],
        	'guided'	: user['guided'],
        	'shopping_cart'	: user['shopping_cart']
        })
    return jsonify(response = {
        'result'    : output
    })

# use SendGrid to send user an email if they forgot their password
@app.route('/retrieve_pw', methods=['GET'])
def get_pw():
    auth = db.auth
    output = []
    for user in auth.find():
        output.append({
            '_id'       : str(user['_id']),
            'username'  : user['username'],
            'password'  : user['password'],
            'email'     : user['email'],
            'radius'    : user['radius'],
        	'rating'  	: user['rating'],
        	'travelled' : user['travelled'],
        	'created'	: user['created'],
        	'guided'	: user['guided'],
        	'shopping_cart'	: user['shopping_cart']
        })
    return jsonify(response = {
        'result'    : output
    })

#################### GEOCODING RELATED API CALLS ####################
#return the geocode
@app.route('/geocode', methods=['GET'])
def get_geocode():
    #get latitude and longitude
    lat = str(request.args.get('lat'))
    lng = str(request.args.get('lng'))
    #get the geocode
    results = requests.get('https://maps.googleapis.com/maps/api/geocode/json?latlng=' + lat + ',' + lng + '&key=AIzaSyDGN5Mu7yTL6x_lP2hZzhP9T0f4uqIXUYI').json()
    #return the geocode
    return jsonify(response = {
        'result' : results['results'][0]['formatted_address']
    })

#################### SETTINGS RELATED API CALLS ####################
#used to update the settings
@app.route('/set_settings', methods=['POST'])
def put_settings():
    #get data from request
    username = request.json['username']
    radius = request.json['radius']
    category = request.json['category']
    rankby = request.json['rankBy']
    opennow = request.json['openNow']
    keyword = request.json['keyword']
    minprice = request.json['minprice']
    maxprice = request.json['maxprice']

    #find the user
    auth = db.auth
    user = auth.find_one({
        'username'  : username
    })

    #if the user exists
    if user:
        #update the user's settings
        auth.update_one(
            {'username': username},
            {
                '$set': {
                    'radius': radius,
                    'category' : category,
                    'rankby'   : rankby,
                    'opennow'  : opennow,
                    'keyword'  : keyword,
                    'minprice' : minprice,
                    'maxprice' : maxprice
                    }
            }
        )
        output = {'result' : 'Settings updated'}
    else:
        output = {'result' : 'Error updating settings, user not found'}
    return jsonify(response = output)

#################### TOUR RELATED API CALLS ####################
#add a tour to the database
@app.route('/addtour', methods=['POST'])
def add_tour():
    tours = db.tours

    # get all tour arguments from url parameters
    tour_id = request.json['tourid']
    creator = request.json['created-by']
    describe = request.json['description']
    Latitude = request.json['Lat']
    Longitude = request.json['Long']
    price = request.json['price']

    #lat and long are formatted as a string separated by '|'
    Lat = list()
    Lat.extend([float(x) for x in Latitude.split("|")])
    Long = list()
    Long.extend([float(x) for x in Longitude.split("|")])

    #if the tour already exists, find it
    tour = tours.find_one({'tour_id'  : tour_id})
    if tour:
        output = {'result' : 'tour by this name already exists'}
    else:
        #if the tour doesn't exits then create it
        user_id = tours.insert({
            'contact_info' : '',
            'tour_id'   : tour_id,
            'guides'    : '',
            'tourists'  : [],
            'min_occ'   : 0,
    	    'max_occ'  	: 0,
    	    'occ'       : 0,
    	    'Latitude' 	: Lat,
            'Longitude'	: Long,
            'radius'	: 0,
    	    'est_dur'	: 0,
            'start_date': '',
            'end-date'  : '',
            'start_time': '',
            'end_time'  : '',
            'price'     : price,
            'cover_photo' : '',
	    'created-by':creator,
	    'description':describe})
        output = {'result' : 'tour successfully created'}
    auth = db.auth
    user = auth.find_one({'username'  : creator})
    #add the created tour to the list of tour's the user created in his/her profile
    if user:
        auth.update_one(
            {'username': creator},
                {
                '$push': {
                    'created': tour_id
                    }
                }
            )

    #return confirmation of addition/already exists
    return jsonify(response = output)

#get a tour by its ID
@app.route('/get_created', methods=['GET'])
def get_created():
    #get the tour id and find the tour in the database
    tours = db.tours
    tour_id = request.args.get('tour_id')
    created = tours.find({'tour_id' : tour_id})
    output = []
    #add the tour's Latitude and Longitude to a list
    for tour in tours.find({'tour_id' : tour_id}):
        output.append({'tour_id' : tour['tour_id'], 'Lat' : tour['Latitude'], 'Long' : tour['Longitude']})

    #return the tour
    return jsonify(response = {'result' : output})

#add bought tour to a user's account
@app.route('/bought', methods=['POST'])
def bought():
    #get the tour name and the user ID
    username = request.json['username']
    tour_ids = request.json['tour_id']
    auth = db.auth
    tours = db.tours

    #for each tour in the list of tours, find them in the tour database
    #and update the user account
    for tour_id in tour_ids:
        if tours.find({'tour_id' : tour_id}):
            if auth.find({'username':username}):
                auth.update_one(
                {'username': username},
                    {
                    '$push': {
                        'created': tour_id
                        }
                    }
                )

    #return success message
    return jsonify(response={'result':'successfully added to account'})

#get all the tours that belong to a user
@app.route('/get_tour_by_user', methods=['GET'])
def get_tour_by_user():
    tours = db.tours
    auth = db.auth
    #get the user and tours they created in the tours database
    user = request.args.get('username')
    profile = auth.find_one({'username':user})
    created = tours.find({'created-by' : user})

    #get unique created tour IDs
    ts = set()
    for t in created:
        ts.add(t['tour_id'])

    #get unique tour IDs of all tours
    bought = set(profile['created'])

    #bought = all-created
    bought = list(bought-ts)

    #convert created to list
    created = list(ts)

    #return bought and created
    return jsonify(response={'created' : created, 'bought' : bought})

@app.route('/get_tour', methods=['GET'])
def get_tour():
    tours = db.tours
    # find all tours with ___ in name, or in ___ city, state
    start = request.args.get('start')
    end = request.args.get('end')
    start = int(start)
    end = int(end)
    i = 0
    output = []
    for tour in tours.find():
        if i >= start and i <= end:
            output.append({
            'contact_info' : str(tour['contact_info']),
            'tour_id'   : str(tour['tour_id']),
            'guides'    : str(tour['guides']),
            'tourists'  : str(tour['tourists']),
            'min_occ'   : str(tour['min_occ']),
            'max_occ'   : str(tour['max_occ']),
            'occ'       : str(tour['occ']),
            'Latitude'  : str(tour['Latitude']),
            'Longitude' : str(tour['Longitude']),
            'radius'    : str(tour['radius']),
            'est_dur'   : str(tour['est_dur']),
            'start_date': str(tour['start_date']),
            'end-date'  : str(tour['end-date']),
            'start_time': str(tour['start_time']),
            'end_time'  : str(tour['end_time']),
            'price'     : str(tour['price']),
            'cover_photo' : str(tour['cover_photo']),
            'created-by': str(tour['created-by']),
            'description': str(tour['description'])})
        elif i > end:
            break

        i = i+1
    return jsonify(response={'result': output})

@app.route('/forgot', methods=['POST'])
def forgot():
    request.json['email']

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=12344, debug=True)
    # app.run(host='127.0.0.1',port='12344',
    #     debug = False/True, ssl_context=context)
