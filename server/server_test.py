#!flask/bin/python
from flask import Flask, request, jsonify
from flask_restful import reqparse
from pymongo import MongoClient
from datetime import datetime
from OpenSSL import SSL
from threading import Thread
import concurrent.futures
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

@app.route('/get_places', methods=['POST'])
def get_places():
    latitude = request.json['lat']
    longitude = request.json['lng']
    radius = request.json['radius']
    place_type = request.json['type']
    if place_type is 'none':
        add_type = ''
        # if place_type = 'Nature and Landscape'/etc
        # concurrently fetch all types in that category
    else:
        add_type = '&type=' + place_type
    keyword = request.json['keyword']
    if keyword is 'none':
        add_keyword = ''
    else:
        add_keyword = '&keyword=' + keyword
    url = base_url + 'location=' + latitude + ',' + longitude + '&radius=' + str(int(radius)*1610) + add_type + add_keyword + '&key=' + key
    places = requests.get(url)
    return app.response_class(places.content, content_type='application/json')

arts_and_entertainment = ['amusement_park', 'aquarium', 'art_gallery', 'book_store', 'bowling_alley', 'cafe', 'campground', 'casino', 'city_hall', 'embassy', 'library', 'lodging', 'movie_rental', 'movie_theater', 'night_club', 'rv_park', 'school', 'stadium', 'store', 'university', 'zoo']
nature_and_landscape = ['campground', 'cemetery', 'park', 'zoo']
food = ['bakery', 'bar', 'cafe', 'meal_delivery', 'meal_takeaway', 'restaurant']
shopping = ['bicycle_store', 'book_store', 'clothing_store', 'convenience_store', 'department_store', 'electronics_store', 'furniture_store', 'hardware_store', 'home_goods_store', 'jewelry_store', 'liquor_store', 'pet_store', 'shoe_store', 'shopping_mall']
all_types = arts_and_entertainment + nature_and_landscape + food + shopping

URLS = ['www.google.com', 'www.time.com']

# Retrieve a single page and report the URL and contents
def load_url(url, timeout):
    with urllib.request.urlopen(url, timeout=timeout) as conn:
        return conn.read()

def concurrent_fetch(type_list):
    # We can use a with statement to ensure threads are cleaned up promptly
    with concurrent.futures.ThreadPoolExecutor(max_workers=len(all_types)) as executor:
        # Start the load operations and mark each future with its URL
        future_to_url = {executor.submit(load_url, url, 60): url for url in URLS}
        for future in concurrent.futures.as_completed(future_to_url):
            url = future_to_url[future]
            try:
                data = future.result()
            except Exception as exc:
                print('%r generated an exception: %s' % (url, exc))
            else:
                print('%r page is %d bytes' % (url, len(data)))


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
    url = url + '&key=' + key
    directions = requests.get(url)
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
            	'shopping_cart'	: user['shopping_cart'],
                'category' : user['category'],
                'rankby'   : user['rankby'],
                'opennow'  : user['opennow'],
                'keyword'  : user['keyword'],
                'minprice' : user['minprice'],
                'maxprice' : user['maxprice']
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
            	'shopping_cart'	: '',
                'category' : '',
                'rankby'   : '',
                'opennow'  : '',
                'keyword'  : '',
                'minprice' : '',
                'maxprice' : ''
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
            'shopping_cart'	: '',
            'category' : '',
            'rankby'   : '',
            'opennow'  : '',
            'keyword'  : '',
            'minprice' : '',
            'maxprice' : ''
        })
    return jsonify(response = output)

# this method is for creating a new account
@app.route('/login/new', methods=['POST'])
def new_login():
    auth = db.auth
    username = request.json['username']
    password = request.json['password']
    email = request.json['email']
    radius = 3000   # radius in meters ~.6 miles
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
    	'shopping_cart'	: [],
        'category' : 'none',
        'rankby'   : 'prominence',
        'opennow'  : 'yes',
        'keyword'  : 'none',
        'minprice' : '0.0',
        'maxprice' : '999999.99'
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
        	'shopping_cart'	: user['shopping_cart'],
            'category' : user['category'],
            'rankby'   : user['rankby'],
            'opennow'  : user['opennow'],
            'keyword'  : user['keyword'],
            'minprice' : user['minprice'],
            'maxprice' : user['maxprice']
        })
    return jsonify(response = {
        'result'    : output
    })

@app.route('/get_user', methods=['GET'])
def get_user():
    auth = db.auth
    username = request.args.get('user')
    output = []
    user = auth.find_one({
        'username'  : username
    })
    if user:
        output.append({
            'result' : 'settings updated successfully',
            '_id'       : str(user['_id']),
            'username'  : user['username'],
            'password'  : user['password'],
            'email'     : user['email'],
            'radius'    : user['radius'],
            'rating'  	: user['rating'],
            'travelled' : user['travelled'],
            'created'	: user['created'],
            'guided'	: user['guided'],
            'shopping_cart'	: user['shopping_cart'],
            'category' : user['category'],
            'rankby'   : user['rankby'],
            'opennow'  : user['opennow'],
            'keyword'  : user['keyword'],
            'minprice' : user['minprice'],
            'maxprice' : user['maxprice']
        })
    else:
        output.append({
            'result' : 'user not found'
        })
    return jsonify(response = output)

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
        output = []   # when the surrounding code is uncommented, this is the if statement, so indent
        output.append({
            'result' : 'settings updated successfully'
            # '_id'       : str(user['_id']),
            # 'username'  : user['username'],
            # 'password'  : user['password'],
            # 'email'     : user['email'],
            # 'radius'    : user['radius'],
            # 'rating'  	: user['rating'],
            # 'travelled' : user['travelled'],
            # 'created'	: user['created'],
            # 'guided'	: user['guided'],
            # 'shopping_cart'	: user['shopping_cart'],
            # 'category' : user['category'],
            # 'rankby'   : user['rankby'],
            # 'opennow'  : user['opennow'],
            # 'keyword'  : user['keyword'],
            # 'minprice' : user['minprice'],
            # 'maxprice' : user['maxprice']
        })
        # else:
        #     # print user['radius']
        #     output = {'result' : 'Settings could not be updated'}
    else:
        output = {'result' : 'Error updating settings, user not found'}
    return jsonify(response = output)

@app.route('/get_settings', methods=['GET'])
def get_settings():
    username = request.args.get('user')
    auth = db.auth
    user = auth.find_one({
        'username'  : username
    })
    if user:
        output = []   # when the surrounding code is uncommented, this is the if statement, so indent
        output.append({
            'result' : 'settings updated successfully',
            '_id'       : str(user['_id']),
            'username'  : user['username'],
            'password'  : user['password'],
            'email'     : user['email'],
            'radius'    : user['radius'],
            'rating'  	: user['rating'],
            'travelled' : user['travelled'],
            'created'	: user['created'],
            'guided'	: user['guided'],
            'shopping_cart'	: user['shopping_cart'],
            'category' : user['category'],
            'rankby'   : user['rankby'],
            'opennow'  : user['opennow'],
            'keyword'  : user['keyword'],
            'minprice' : user['minprice'],
            'maxprice' : user['maxprice']
        })
        return jsonify(response = output)

#################### TOUR RELATED API CALLS ####################
@app.route('/add_tour', methods=['POST'])
def add_tour():
    tours = db.tours
    # get all tour arguments from url parameters
    name = requests.json['name']
    contact_info = request.json['contact']
    guides = request.json['guides']
    tourists = request.json['tourists']
    min_occ = request.json['min']
    max_occ = request.json['max']
    occ     = request.json['occ']
    landmarks = request.json['landmarks']
    radius = request.json['radius']
    est_dur = request.json['dur']
    start_date = request.json['start-date']
    end_date = request.json['end-date']
    start_time = request.json['start-time']
    end_time = request.json['end-time']
    price = request.json['price']
    cover_photo = request.json['photo'] # TODO: not sure how to get image file over the network
    rating = request.json['rating']
    city = request.json['city']
    state = request.json['state']
    #  put these params into the collection
    name = tours.find_one({
        'name'  : name
    })
    if name:
        output = {'result' : 'tour name taken'}
        return jsonify(response = output)
    tour_id = auth.insert({
        'contact_info' : contact_info,
        'guides'    : [],
        'tourists'  : [],
        'min_occ'   : min_occ,
    	'max_occ'  	: max_occ,
    	'occ'       : occ,
    	'landmarks'	: [],
    	'radius'	: radius,
    	'est_dur'	: est_dur,
        'start_date': start_date,
        'end-date'  : end-date,
        'start_time': start_time,
        'end_time'  : end_time,
        'price'     : price,
        'cover_photo' : cover_photo,
        'rating'    : rating,
        'city'      : city,
        'state'     : state
    })
    output = {'result' : 'tour added successfully'}
    return jsonify(response = output)

@app.route('/get_tour', methods=['GET'])
def get_tour():
    tours = db.tours
    # find all tours with ___ in name, or in ___ city, state

@app.route('/recommend_tour', methods=['POST'])
def recommend_tour():
    tours = db.tours
    # need all landmarks near locations
    # build based on rating, types, keywords
    # add highest 5/6 scores to tour, or down to certain threshold


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=12344, debug=True)
    # app.run(host='127.0.0.1',port='12344',
    #     debug = False/True, ssl_context=context)
