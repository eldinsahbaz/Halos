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

# context = SSL.Context(SSL.SSLv23_METHOD)
# context.use_privatekey_file('yourserver.key')
# context.use_certificate_file('yourserver.crt')

client = MongoClient()
db = client.test

# Increment a counter for datadog metrics.
statsd.increment('page.views')

# radius = 10
# key = 'AIzaSyBuoo0QB2PhkrJpNww_yTq4dGwiJnWL-AQ'
# location = '44.0124,-78.63421'
# type = 'restaurant'
# keyword = 'italian'
# url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=AIzaSyBuoo0QB2PhkrJpNww_yTq4dGwiJnWL-AQ'
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
    radius = request.json['radius']	#str(3000)   # later change this, just have to decide which way to get radius
    keyword = request.json['keyword']
    url = base_url + 'location=' + latitude + ',' + longitude + '&radius=' + radius + '&key=' + key + '&keyword=' + keyword
    places = requests.get(url)
    return app.response_class(places.content, content_type='application/json')

#@app.route('/get_places', methods=['POST'])
#def get_places():
#    latitude = request.json['lat']
#    longitude = request.json['lng']
#    radius = request.json['radius'] 
#    keyword = request.json['keyword']
#    url = base_url + 'location=' + latitude + ',' + longitude + '&radius=' + radius + '&key=' + key + '&keyword=' + keyword
#    places = requests.get(url)
#    print '\n'
#    print 'radius', radius
#    print 'keyword', keyword
#    print '\n'
#    return app.response_class(places.content, content_type='application/json')

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
@app.route('/geocode', methods=['GET'])
def get_geocode():
    lat = str(request.args.get('lat'))
    lng = str(request.args.get('lng'))
    results = requests.get('https://maps.googleapis.com/maps/api/geocode/json?latlng=' + lat + ',' + lng + '&key=AIzaSyDGN5Mu7yTL6x_lP2hZzhP9T0f4uqIXUYI').json()
    return jsonify(response = {
        'result' : results['results'][0]['formatted_address']
    })

#################### SETTINGS RELATED API CALLS ####################
@app.route('/set_settings', methods=['POST'])
def put_settings():
    username = request.json['username']
    radius = request.json['radius']
    category = request.json['category']
    rankby = request.json['rankBy']
    opennow = request.json['openNow']
    keyword = request.json['keyword']
    minprice = request.json['minprice']
    maxprice = request.json['maxprice']

    auth = db.auth
    user = auth.find_one({
        'username'  : username
    })
    if user:
        #  TODO: the update method works, except it will always fall to the else clause (even though settings are updated, not sure why? time delay?)
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
        # if (user['radius'] == radius):
        output = {'result' : 'Settings updated'}    # when the surrounding code is uncommented, this is the if statement, so indent
        # else:
        #     # print user['radius']
        #     output = {'result' : 'Settings could not be updated'}
    else:
        output = {'result' : 'Error updating settings, user not found'}
    return jsonify(response = output)

#################### TOUR RELATED API CALLS ####################
#Edited by Ray and Eldin
@app.route('/addtour', methods=['POST'])
def add_tour():
    tours = db.tours

    # get all tour arguments from url parameters
    # TODO: NAME?
    #contact_info = request.json['contact']
    tour_id = request.json['tourid']
    creator = request.json['created-by']
    describe = request.json['description']
    #guides = request.json['guides']
    #tourists = request.json['tourists']
    #min_occ = request.json['min']
    #max_occ = request.json['max']
    #occ     = request.json['occ']
    Latitude = request.json['Lat']
    Longitude = request.json['Long']
    #radius = request.json['radius']
    #est_dur = request.json['dur']
    #start_date = request.json['start-date']
    #end_date = request.json['end-date']
    #start_time = request.json['start-time']
    #end_time = request.json['end-time']
    price = request.json['price']
    #cover_photo = request.json['photo'] # TODO: not sure how to get image file over the network
    #  put these params into the collection
    Lat = list()
    Lat.extend([float(x) for x in Latitude.split("|")])
    Long = list()
    Long.extend([float(x) for x in Longitude.split("|")])
    tour = tours.find_one({'tour_id'  : tour_id})
    if tour:
        output = {'result' : 'tour by this name already exists'}
    else:
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
    
    if user:
        auth.update_one(
            {'username': creator},
                {
                '$push': {
                    'created': tour_id
                    }
                }
            )

    return jsonify(response = output)

@app.route('/get_created', methods=['GET'])
def get_created():
    tours = db.tours
    tour_id = request.args.get('tour_id')
    
    return jsonify(response=tours.find({'tour_id': tour_id}))

@app.route('/bought', methods=['POST'])
def bought():
    username = reqest.json['username']
    tour_id = request.json['tour_id']
    auth = db.auth
    tours = db.tours
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
    
    return jsonify(response={'result':'successfully added to account'})
        
    

@app.route('/get_tour_by_user', methods=['GET'])
def get_tour_by_user():
    tours = db.tours
    auth = db.auth
    user = request.args.get('username')
    profile = auth.find_one({'username':user})
    created = set(tours.find({'created-by' : user}))
    bought = set(profile['created'])
    bought = list(bought-created)
    created = list(created)
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

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=12344, debug=True)
    # app.run(host='127.0.0.1',port='12344',
    #     debug = False/True, ssl_context=context)
