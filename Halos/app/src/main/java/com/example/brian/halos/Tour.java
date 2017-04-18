package com.example.brian.halos;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by brian on 2/9/17.
 * Class that contains the Tour created by the user which contains a list of landmarks
 * that contains all the data on the marker points that a user selected when creating
 * this tour including their current location at the time of creation. This class also
 * contains commented python code that was meant to be converted to Java for future
 * features.
 */

public class Tour implements Parcelable {
//
//    class Tour():
//    __ContactInfo = None
//            __Guides = None
//    __Tourists = None
//            __MinOccupancy = None
//    __MaxOccupancy = None
//            __Occupancy = None
//    __Landmarks = None
//            __Radius = None
//    __EstimatedDuration = None
//            __StartDate = None
//    __EndDate = None
//            __StartTime = None
//    __EndTime = None
//            __Price = None
//    __CoverPhoto = None
    protected static   String name  = "";
    protected  static HashMap<String, String> contactInfo = new HashMap<String, String>();
    protected static LinkedList<User> guides = new LinkedList<User>();
    protected  static LinkedList<User> tourists = new LinkedList<User>();
    protected static int minOccupancy = 0;
    protected static int maxOccupancy = 15;
    protected static int occupancy = 1;
    protected static LinkedList<Landmark> landmarks = new LinkedList<Landmark>();;
    protected static int radius = 3000;
//    TODO:DURATIONS AND TIMES AND COVER PHOTO
//    TODO: add ratings functions
    protected static int ratings;
    protected static String creator;
    protected static String description;
    protected  static double price = 0.0;



    //    def __init__(self):
//    self.__ContactInfo = dict()
//    self.__Guides = set()
//    self.__Tourists = set()
//    self.__MinOccupancy = 0
//    self.__MaxOccupancy = 0
//    self.__Occupancy = 0
//    self.__Landmarks = OrderedDict()
//    self.__Radius = 0
//    self.__EstimatedDuration = datetime.time(0, 0)
//    self.__StartDate = datetime.datetime(1, 1, 1)
//    self.__EndDate = datetime.datetime(1, 1, 1)
//    self.__StartTime = datetime.time(0, 0)
//    self.__EndTime = datetime.time(0, 0)
//    self.__Price = 0.0
//    self.__CoverPhoto = Image.new("RGB", (512,512), "white")
    public void Tour() {

    }

    //Getter and Setter methods for class variables.

    public void setName(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    public void setCreator(String n) { creator=n; }
    public String getCreator() { return creator;}
    public void setDescription(String n) { description=n;}
    public String getDescription() { return description;}


    public void setContactInfo(String name, String contact) {
        contactInfo.put(name, contact);
    }


    public void addContactInfo(String name, String contact) {
        contactInfo.put(name, contact);
    }

//    def GetContactInfo(self):
//            return self.__ContactInfo
    public HashMap<String, String> getContactInfo() {
        return new HashMap<>(); // TODO: finish this
    }

//    def AddGuide(self, guide):
//            if isinstance(guide, type(User())):
//            (self.__Guides).append(guide)
//    else:
//            return Exception("input is not of type User")
    public void addGuide(User guide) {
        guides.add(guide);
    }

//    def AddGuides(self, guides):
//            try:
//            for i in guides:
//            (self.__Guides).append(i)
//    except:
//            return Exception("invalid input")
    public void addGuides(LinkedList<User> g) {
        guides.addAll(g);
    }

//    def RemoveGuide(self, guide):
//            if isinstance(guide, type(User())):
//            (self.__Guides).remove(guide)
//    else:
//            return Exception("input is not of type User")
    public void removeGuide(User g) {
        guides.remove(g);
    }

//    def RemoveGuides(self, guides):
//            try:
//            for i in guides:
//            (self.__Guides).remove(i)
//    except:
//            return Exception("input is not of type User")
    public void removeGuides(LinkedList<User> guideList) {
        for (User guideToRemove : guideList) {
            guides.remove(guideToRemove);
        }
    }

//    def GetGuides(self):
//            return self.__Guides
    public LinkedList<User> getGuides() {
        return guides;
    }

//    def AddTourist(self, tourist):
//            if isinstance(tourist, type(User())):
//            (self.__Tourists).append(tourist)
//    else:
//            return Exception("input is not of type User")
    public void addTourist(User t) {
        tourists.add(t);
    }

//    def AddTourists(self, tourists):
//            try:
//            for i in tourists:
//            (self.__Tourists).append(i)
//    self.__Occupancy += 1
//    except:
//            return Exception("input is not of type set")
    public void addTourists(LinkedList<User> t) {
        tourists.addAll(t);
    }

//    def RemoveTourist(self, tourist):
//            if isinstance(tourist, type(User())):
//            (self.__Tourists).remove(tourist)
//    self.__Occupancy -= 1
//            else:
//            return Exception("input is not of type User")
    public void removeTourist(User t) {
        tourists.remove(t);
    }

//    def RemoveTourists(self, tourists):
//            try:
//            for i in tourists:
//            (self.__Tourists).remove(i)
//    self.__Occupancy -= 1
//    except:
//            return Exception("input is not of type User")
    public void removeTourists(LinkedList<User> t) {
        for(User touristToRemove : t) {
            tourists.remove(touristToRemove);
        }
    }

//    def GetTourists(self):
//            return self.__Tourists
    public LinkedList<User> getTourists() {
        return tourists;
    }

//    def SetMaxOccupancy(self, maxOcc):
//            if isinstance(maxOcc, type(int())):
//    self.__MaxOccupancy = maxOcc
//    else:
//            return Exception("input is not an integer")
    public void setMaxOccupancy(int macOcc) {
        maxOccupancy = macOcc;
    }

//    def GetMaxOccupancy(self):
//            return self.__MaxOccupancy
    public int getMaxOccupancy() {
        return maxOccupancy;
    }

//    def SetMinOccupancy(self, minOcc):
//            if isinstance(minOcc, type(int())):
//    self.__MinOccupancy = minOcc
//    else:
//            return Exception("input is not an integer")
    public void setMinOccupancy(int minOcc) {
        minOccupancy = minOcc;
    }

//    def GetMinOccupancy(self):
//            return self.__MinOccupancy
    public int getMinOccupancy() {
        return minOccupancy;
    }

//    def GetOccupancy(self):
//            return self.__Occupancy
    public int getOccupancy() {
        return occupancy;
    }

//    def AddLandmark(self, landmark):
//            if isinstance(landmark, type(Location.Location())):
//            (self.__Landmarks)[landmark] = True
//    else:
//            return Exception("input is not of type Location")
    public void addLandmark(Landmark l) {
        Log.v("Tour Class", String.valueOf(l == null));
        landmarks.add(l);
    }

//    def AddLandmarks(self, landmarks):
//            try:
//            for i in landmarks:
//            (self.__Landmarks)[i] = True
//    except:
//            return Exception("input is not of type Set")
    public void addLandmarks(LinkedList<Landmark> ls) {
        landmarks.addAll(ls);
    }

//    def RemoveLandmark(self, landmark):
//            if isinstance(landmark, type(Location.Location())):
//    del (self.__Landmarks)[landmark]
//            else:
//            return Exception("input is not of type Location")
    public void removeLandmark(Landmark l) {
        landmarks.remove(l);
    }

//    def RemoveLandmarks(self, landmarks):
//            try:
//            for i in landmarks:
//    del (self.__Landmarks)[i]
//    except:
//            return Exception("input is not of type Location")
    public void removeLandmarks(LinkedList<Landmark> ls) {
        for (Landmark l : ls) {
            landmarks.remove(l);
        }
    }

//    def GetLandmarks(self):
//            return self.__Landmarks
    public LinkedList<Landmark> getLandmarks() {
        return landmarks;
    }

//    def SetRadius(self, radius):
//            if isinstance(radius, type(int())):
//    self.__Radius = radius
//    else:
//            return Exception("input is not of type float")
    public void setRadius(int r) {
        radius = r;
    }

//    def GetRadius(self):
//            return self.__Radius
    public int getRadius() {
        return radius;
    }

//    def SetEstimatedDuration(self, hour, minute):
//            if isinstance(hour, type(int())) and isinstance(minute, type(int())):
//    self.__EstimatedDuration = datetime.time(hour, minute)
//            else:
//            return Exception("input is not of type float")
//
//    def GetEstimatedDuration(self):
//            return self.__EstimatedDuration
//
//    def SetStartDate(self, year, month, date):
//            if isinstance(year, type(int())) and isinstance(month, type(int())) and isinstance(date, type(int())):
//    self.__StartDate = datetime.datetime(year, month, date)
//            else:
//            return Exception("input is not of type int")
//
//    def GetStartDate(self):
//            return self.__StartDate
//
//    def SetEndDate(self, year, month, date):
//            if isinstance(year, type(int())) and isinstance(month, type(int())) and isinstance(date, type(int())):
//    self.__EndDate = datetime.datetime(year, month, date)
//            else:
//            return Exception("input is not of type int")
//
//    def GetEndDate(self):
//            return self.__EndDate
//
//    def SetStartTime(self, hour, minute):
//            if isinstance(hour, type(int())) and isinstance(minute, type(int())):
//    self.__StartTime = datetime.time(hour, minute)
//            else:
//            return Exception("input is not of type int")
//
//    def GetStartTime(self):
//            return self.__StartTime
//
//    def SetEndTime(self, hour, minute):
//            if isinstance(hour, type(int())) and isinstance(minute, type(int())):
//    self.__EndTime = datetime.time(hour, minute)
//            else:
//            return Exception("input is not of type int")
//
//    def GetEndTime(self):
//            return self.__EndTime
//
//    def SetPrice(self, price):
//            if isinstance(price, type(float())):
//    self.__Price = price
//    else:
//            return Exception("input is not of type float")
    public void setPrice(double p) throws Exception {
        if (p < 0) {
            throw new Exception("Invalid price: cannot be less than 0");
        }
        else {
            price = p;
        }
    }

//    def GetPrice(self):
//            return self.__Price
    public double getPrice() {
        return price;
    }


    public static final Parcelable.Creator<Tour> CREATOR
            = new Parcelable.Creator<Tour>() {
        public Tour createFromParcel(Parcel in) {
            return new Tour();
        }

        public Tour[] newArray(int size) {
            return new Tour[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

//    def SetCoverPhoto(self, photoPath):
//            try:
//    self.__CoverPhoto = Image.open(photoPath)
//    except IOError:
//            return ("incorrect input")
//
//    def GetCoverPhoto(self):
//            return self.__CoverPhoto


}
