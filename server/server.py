#!flask/bin/python
from flask import Flask, request, jsonify
from flask_restful import reqparse
from pymongo import MongoClient
from datetime import datetime
from OpenSSL import SSL
import json
from bson import json_util
import requests

app = Flask(__name__)

# context = SSL.Context(SSL.SSLv23_METHOD)
# context.use_privatekey_file('yourserver.key')
# context.use_certificate_file('yourserver.crt')

client = MongoClient()
db = client.test


# radius = 10
# key = 'AIzaSyBuoo0QB2PhkrJpNww_yTq4dGwiJnWL-AQ'
# location = '44.0124,-78.63421'
# type = 'restaurant'
# keyword = 'italian'
# url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=AIzaSyBuoo0QB2PhkrJpNww_yTq4dGwiJnWL-AQ'

places = requests.get('https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=43.0481,-76.1474&radius=500&type=restaurant&key=AIzaSyBuoo0QB2PhkrJpNww_yTq4dGwiJnWL-AQ')

@app.route('/places', methods=['GET'])
def get_places():
    return app.response_class(places.content, content_type='application/json')

# clears the accounts database (NOT FOR PRODUCTION ONLY FOR TESTING PRUPOSES, WE SHOULD DELETE ONCE WE GET EVERYTHING WORKING!!!!!!!!)
# TODO: delete a single user (if they deactivate their account)
@app.route('/delete_accounts', methods=['PUT'])
def delete_users():
    auth = db.auth
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
    if user:
        # output = 'Attempt login for %s' % username
        # return output
        if password == user['password']:
            output = {'result' : 'login successful'}
        else:
            output = {'result' : 'incorrect password'}
    else:
        output = {'result' : 'User not found'}
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
    output = {
        '_id'       : str(new_user['_id']),
        'username'  : new_user['username'],
        'password'  : new_user['password'],
        'email'     : new_user['email'],
        'radius'    : new_user['radius'],
    	'rating'  	: new_user['rating'],
    	'travelled' : new_user['travelled'],
    	'created'	: new_user['created'],
    	'guided'	: new_user['guided'],
    	'shopping_cart'	: new_user['shopping_cart']
    }
    return jsonify({
        'result'    : output
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


#################### SETTINGS RELATED API CALLS ####################
@app.route('/settings', methods=['PUT'])
def put_settings():
    username = request.args.get('username')
    radius = request.args.get('radius')
    auth = db.auth
    user = auth.find_one({
        'username'  : username
    })
    if user:
        #  TODO: the update method works, except it will always fall to the else clause (even though settings are updated, not sure why? time delay?)
        auth.update_one(
            {'username': username},
            {
                '$set': {'radius': radius}  # add more settings to update here
            }
        )
        # if (user['radius'] == radius):
        output = {'result' : 'Settings updated'}    # when the surrounding code is uncommented, this is the if statement, so indent
        # else:
        #     # print user['radius']
        #     output = {'result' : 'Settings could not be updated'}
    else:
        output = {'result' : 'User not found'}
    return jsonify(response = output)


#################### TOUR RELATED API CALLS ####################
@app.route('/addtour', methods=['POST'])
def add_tour():
    tours = db.tours

    # get all tour arguments from url parameters
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
    #  put these params into the collection
    user_id = auth.insert({
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
        'cover_photo' : cover_photo
    })


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=12344, debug=True)
    # app.run(host='127.0.0.1',port='12344',
    #     debug = False/True, ssl_context=context)
