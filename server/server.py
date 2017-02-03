#!flask/bin/python
from flask import Flask, jsonify
import requests
import urllib2
import json

app = Flask(__name__)

# radius = 10
# key = 'AIzaSyBuoo0QB2PhkrJpNww_yTq4dGwiJnWL-AQ'
# url = 'https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&keyword=cruise&key=AIzaSyBuoo0QB2PhkrJpNww_yTq4dGwiJnWL-AQ'

places = requests.get('https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=43.0481,-76.1474&radius=500&type=restaurant&key=AIzaSyBuoo0QB2PhkrJpNww_yTq4dGwiJnWL-AQ')

@app.route('/places', methods=['GET'])
def get_resp():
    return app.response_class(places.content, content_type='application/json')



if __name__ == '__main__':
    app.run(debug=True)
