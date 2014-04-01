###############################CASTAWAY################################
####################### Directions without data #######################
#
#
#Copyright Ihsan Etwaroo, Kayhan Qaiser, and Amiel Kollek, 2014. All rights reserved.

from flask import *
import twilio.twiml
import json as simplejson
import urllib
import time

def parse(string):
	array=[]
	string=string.lower()
	first=string.partition(" from ")
	mode=first[0].lower()
	if "bus" in mode or "public" in mode or "subway" in mode:
		mode="transit"
	if "bike" in mode or "cycl" in mode:
		mode="bicycling"
	if "walk" in mode or "foot" in mode:
		mode="walking"
	if "car" in mode or "drive" in mode:
		mode="driving"
		
	second=first[2].partition(" to ")
	array.append(mode)
	array.append(second[0])
	array.append(second[2])
	return array


from HTMLParser import HTMLParser

GEOCODE_BASE_URL = 'http://maps.google.com/maps/api/directions/json'


def directions(origin,destination,mode,departure_time,number,sensor,**destination_args):
	destination_args.update({
		'origin': origin,
		'destination': destination,
		'mode': mode,
		'sensor': sensor,  
		'departure_time': departure_time
	})

	url = GEOCODE_BASE_URL + '?' + urllib.urlencode(destination_args)
	
	#print url
	result = simplejson.load(urllib.urlopen(url))
	
	if result["routes"] == []:
		return "No route found! Please try again."
	else:
		eta = result['routes'][0]['legs'][0]['duration']['text']
		tmode = mode.upper()
		#print tmode
		

		if tmode =="TRANSIT":
			start_time=result['routes'][0]['legs'][0]['departure_time']['text']

		ar = []
		#optionally add personalized response message
		#if number =="+12892538247":
		#	ar.append("Hello, Amiel!")
		
		parser = MyHTMLParser()
		ar.append(["ETA: "]+[eta])
		if tmode=="TRANSIT":
			ar.append(["Start time: "]+[start_time])

	
		for i in range(len(result['routes'][0]['legs'][0]['steps'])):
			if tmode=="TRANSIT":
				t=(result['routes'][0]['legs'][0]['steps'][i]['duration']['text'])
				(parser.feed(result['routes'][0]['legs'][0]['steps'][i]['html_instructions']))
				ar.append([str(i+1)]+[". "]+parser.return_data()+[" for "]+[str(t)])
			else:
				(parser.feed(result['routes'][0]['legs'][0]['steps'][i]['html_instructions']))
				ar.append([str(i+1)]+[". "]+parser.return_data())


		st=""
		for i in range(len(ar)):
			for j in range(len(ar[i])):
				st= st + ar[i][j]
			st=st + ".\n"
	
		return st

# create a subclass and override the handler methods
# bless Tyler and the S. S. Ticket
class MyHTMLParser(HTMLParser):
	def __init__(self):
		HTMLParser.__init__(self)
		self.data = []
	def handle_starttag(self, tag, attrs):
		None
	def handle_endtag(self, tag):
		None
	def handle_data(self, data):
		self.data.append(data)
	def return_data(self):
		data = self.data
		self.data = []
		return data


#print directions("Toronto","Montreal","false")


app = Flask(__name__)

	
	
@app.route("/", methods=['GET', 'POST'])
def return_info():
	dep_time=str(int(time.time()+60))
	from_message=parse(request.values.get('Body', None))
	mode=from_message[0]
	start_location=from_message[1]
	destination=from_message[2]
	from_number=request.values.get('From', None)
	from_city=request.values.get('FromCity', None)
	from_state=request.values.get('FromState', None)
	direc_string = directions(start_location,destination,mode,dep_time,from_number,"false")
	print (direc_string)
	message = direc_string
	#message=mode


	resp = twilio.twiml.Response()
	resp.message(message)

	return str(resp)

if __name__ == "__main__":
	app.run(debug=True)


