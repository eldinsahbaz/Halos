package com.example.brian.halos;

/**
 * Created by brian on 2/9/17.
 */
//import datetime
//import base64
//from SecretKey import * #we can host the secret key on our database
//from collections import OrderedDict
//from Crypto.Cipher import XOR
//from PIL import Image

public class Location {
    String description;
    String name;
    int rating;
    // TODO: Cover photo
    // TODO: hours
    // TODO: types
    double latitude;
    double longitude;

//    class Location():
//    __Description = None
//            __CoverPhoto = None
//    __Rating = None
//            __Latitude = None
//    __Longitude = None
//            __Name = None
//    __OpeningHours = None
//            __Types = None
//    __Vicinity = None
    public Location() {
        description = "";
        name = "";
        latitude = 0.0;
        longitude = 0.0;
    }

//    def __init__(self):
//    self.__Description = ""
//    self.__Rating = Rating()
//    self.__CoverPhoto = Image.new("RGB", (512,512), "white")
//    self.__Latitude = 0.0
//    self.__Longitude = 0.0
//    self.__Name = ""
//    self.__OpeningHours = OrderedDict()
//    self.__Types = set()
//    self.__Vicinity = ""

//    def SetDescription(self, description):
//            if isinstance(description, type(str())):
//    self.__Description = description
//    else:
//            return Exception("incorrect input")
    public void setDescription(String des) {
        description = des;
    }
//
//    def GetDescription(self):
//            return self.__Description
    public String getDescription() {
        return description;
    }

//    TODO: cover photo
//    def SetCoverPhoto(self, photoPath):
//            try:
//    self.__CoverPhoto = Image.open(photoPath)
//    except IOError:
//            return ("incorrect input")

//    def GetCoverPhoto(self):
//            return self.__CoverPhoto

//    def SetRating(self, rating):
//            (self.__Rating).AddVote(rating)
    public void setRating(int rat) {
        rating = rat;
    }

//    def GetRating(self):
//            return (self.__Rating).GetRating()
    public int getRating() {
        return rating;
    }

//    def SetLatitude(self, lat):
//            if isinstance(lat, type(float())):
//    self.__Latitude = lat
//    else:
//            return Exception("input is not of type float")
    public void setLatitude(double lat) throws Exception {
        if (lat > 90.0) {
            throw new Exception("Latitude is above acceptable range");
        }
        else if (lat < -90.0) {
            throw new Exception("Latitude is below acceptable range");
        }
        else {
            latitude = lat;
        }
    }

//    def GetLatitude(self):
//            return self.__Latitude
//
    public double getLatitude() {
        return latitude;
    }

//        def SetLongitude(self, long):
//            if isinstance(long, type(float())):
//    self.__Longitude = long
//    else:
//            return Exception("input is not of type float")
    public void setLongitude(double lon) throws Exception {
        if (lon > 90.0) {
            throw new Exception("Longitude is above acceptable range");
        }
        else if (lon < -90.0) {
            throw new Exception("Longitude is below acceptable range");
        }
    }

//    def GetLongitude(self):
//            return self.__Longitude
    public double getLongitude() {
        return longitude;
    }

//    def SetName(self, name):
//    self.__Name = str(name)
    public void setName(String n) {
        name = n;
    }

//    def GetName(self):
//            return self.__Name
    public String getName() {
        return name;
    }

//    TODO: hours
//    def AddOpeningHours(self, timeSlot, boolean):
//            if isinstance(boolean, type(bool())):
//            (self.__OpeningHours).update([(str(timeSlot), boolean)])

//    def GetOpeningHours(self):
//            return self.__OpeningHours

//    TODO: types
//    def AddType(self, typ):
//            (self.__Types).update(str(typ))

//    def AddTypes(self, types):
//            if isinstance(types, type(set())):
//            for i in types:
//            self.AddTypes(i)
//            else:
//            return Exception("input is not of type set")

//    def RemoveType(self, typ):
//            if (self.__Types).has_key(str(typ)):
//    del (self.__Types)[str(typ)]

//    def RemoveTypes(self, types):
//            if isinstance(types, type(set())):
//            for i in types:
//            self.RemoveType(i)
//            else:
//            return Exception("input is not of type set")

//    TODO: vicinity
//    def SetVicinity(self, vicinity):
//    self.__Vicinity = str(vicinity)

//    def GetVicinity(self):
//            return self.__Vicinity
}
