#!flask/bin/python
from flask import Flask, request, jsonify
from flask_restful import reqparse
from pymongo import MongoClient
from datetime import datetime
from OpenSSL import SSL
import json
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


@app.route('/login/auth', methods=['get'])
def create_cm():
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
            output = {
                'result'    :   'correct password'
            }
        else:
            output = {
                'result'    : 'incorrect password'
            }
    else:
        output = {
            'result'        : 'User not found'
        }
    return jsonify(output)

# if the account has already been created
@app.route('/login/auth', methods=['GET'])
# def show_user_profile(username):
#     auth = db.auth
#     user = auth.find_one({
#         'username'  :    username
#     })
#     if user:
#         output = {
#             'username'  : user['username'],
#             'passowrd'  : user['password'],
#             'email'     : user['email']
#         }
#     else:
#         output = 'User: %s not found', username
#     return jsonify({'result' :   output})
def authenticate_user(args):
    args = request.args
    print (args) # For debugging
    username = args['user']
    password = args['pw']
    return jsonify(dict(data=[username, password])) # or whatever is required

    # parser = reqparse.RequestParser()
    # parser.add_argument('user',  required=True)
    # parser.add_argument('pw', required=True)
    # args = parser.parse_args()

    # username = args['user']
    # password = args['pw']
    # auth = db.auth
    # user = auth.find_one({
    #     'username'  : username
    # })
    # if user:
    #     # output = 'Attempt login for %s' % username
    #     # return output
    #     if password == user['password']:
    #         output = {
    #             'result'    :   'correct password'
    #         }
    #     else:
    #         output = {
    #             'result'    : 'incorrect password'
    #         }
    # else:
    #     output = 'User: %s not found' % username
    #     return output

# this method is for creating a new account
@app.route('/login/new', methods=['POST'])
def new_login():
    # TODO: check that username or email is not in login collection
    auth = db.auth
    username = request.json['username']
    password = request.json['password']
    email = request.json['email']
    # radius = request.json['radius']
    # insert_one function throws an error with the return type (not sure why!)
    user_id = auth.insert({
        'username' : username,
        'password' : password,
        'email'    : email,
        'radius'   : radius
    })
    new_user = auth.find_one({'_id' : user_id})
    output = {
        'username'  : new_user['username'],
        'password'  : new_user['password'],
        'email'     : new_user['email']
        # 'radius'    : new_user['radius']
    }
    return jsonify({
        'result'    : output
    })

@app.route('/login', methods=['GET'])
def get_all_users():
    auth = db.auth
    output = []
    for user in auth.find():
        output.append({
            'username'  : user['username'],
            'password'  : user['password'],
            'email'     : user['email']
            # 'radius'    : user['radius']
        })
    return jsonify({
        'result'    : output
    })

@app.route('/addtour', methods=['POST'])
def add_tour():
    tours = db.tours
    # Add tour stuff





## TOTURIAL
@app.route('/star', methods=['GET'])
def get_all_stars():
  star = db.stars
  output = []
  for s in star.find():
    output.append({'name' : s['name'], 'distance' : s['distance']})
  return jsonify({'result' : output})

@app.route('/star/', methods=['GET'])
def get_one_star(name):
  star = mongo.db.stars
  s = star.find_one({'name' : name})
  if s:
    output = {'name' : s['name'], 'distance' : s['distance']}
  else:
    output = "No such name"
  return jsonify({'result' : output})

@app.route('/addstar', methods=['POST'])
def add_star():
  star = db.stars
  name = request.json['name']
  distance = request.json['distance']
  star_id = star.insert({'name': name, 'distance': distance})
  new_star = star.find_one({'_id': star_id })
  output = {'name' : new_star['name'], 'distance' : new_star['distance']}
  return jsonify({'result' : output})
  ## END TUTORIAL





if __name__ == '__main__':
    app.run(host='0.0.0.0', port=12344, debug=True)
    # app.run(host='127.0.0.1',port='12344',
    #     debug = False/True, ssl_context=context)
