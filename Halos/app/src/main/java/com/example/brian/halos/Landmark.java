package com.example.brian.halos;

import android.graphics.Bitmap;

/**
 * Created by brian on 2/9/17.
 * This class stores information about each Google Map place's marker
 * such as Latitude and Longitude.
 */
//import datetime
//import base64
//from SecretKey import * #we can host the secret key on our database
//from collections import OrderedDict
//from Crypto.Cipher import XOR
//from PIL import Image

public class Landmark {
    protected String description;
    protected String name;
    protected double rating;
    protected Bitmap coverPhoto;
    // TODO: hours
    // TODO: types
    protected boolean openNow;
    protected double latitude;
    protected double longitude;
    protected String types;

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

    //Constructors for Landmark class.
    public Landmark() {
        description = "";
        name = "";
        latitude = 0.0;
        longitude = 0.0;
        types = "";
    }

    public Landmark(String n, double r, boolean open, double lat, double lng) {
        name = n;
        rating = r;
        openNow = open;
        latitude = lat;
        longitude = lng;
    }

    public Landmark(String n, double r, boolean open, double lat, double lng, String t) {
        name = n;
        rating = r;
        openNow = open;
        latitude = lat;
        longitude = lng;
        types = t;
    }

    //constructor if we includes pictures.
    public Landmark(String n, int r, Bitmap bm, boolean open, double lat, double lng) {
        name = n;
        rating = r;
        coverPhoto = bm;
        openNow = open;
        latitude = lat;
        longitude = lng;
    }


    //Getter and Setter Methods.
    //Also includes commented code not translated from Python
    //to Java for other un-implemented features.


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
    public void setCoverPhoto(Bitmap bm) {
        coverPhoto = bm;
    }
//    def GetCoverPhoto(self):
//            return self.__CoverPhoto
    public Bitmap getCoverPhoto() {
        return coverPhoto;
    }

//    def SetRating(self, rating):
//            (self.__Rating).AddVote(rating)
    public void setRating(double rat) {
        rating = rat;
    }

//    def GetRating(self):
//            return (self.__Rating).GetRating()
    public double getRating() {
        return rating;
    }

    public String getTypes() {
        return types;
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
        } else {
            longitude= lon;
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

    public void setOpenNow(boolean b) {
        openNow = b;
    }

    public boolean getOpenNow() {
        return openNow;
    }


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
