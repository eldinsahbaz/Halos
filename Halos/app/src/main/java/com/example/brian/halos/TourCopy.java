package com.example.brian.halos;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by raych on 4/2/2017.
 * A Copy of the Tour Class that doesn't use static variables so we don't contain the same
 * variable data for each Tour. This is useful for adding TourCopy object into each
 * List when data is retrieved from server rather than the Tour Object.
 */

public class TourCopy implements Serializable {

    protected   String name  = "";
    protected HashMap<String, String> contactInfo = new HashMap<String, String>();
    protected  LinkedList<User> guides = new LinkedList<User>();
    protected   LinkedList<User> tourists = new LinkedList<User>();
    protected  int minOccupancy = 0;
    protected  int maxOccupancy = 15;
    protected  int occupancy = 1;
    protected  LinkedList<Landmark> landmarks = new LinkedList<Landmark>();;
    protected  int radius = 3000;
    //    TODO:DURATIONS AND TIMES AND COVER PHOTO
//    TODO: add ratings functions
    protected int ratings;
    protected  String creator;
    protected  String description;
    protected double price = 0.0;

    public void Tour() {

    }

    //Getter and Setter Methods for class variables.

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


    public HashMap<String, String> getContactInfo() {
        return new HashMap<>(); // TODO: finish this
    }


    public void addGuide(User guide) {
        guides.add(guide);
    }


    public void addGuides(LinkedList<User> g) {
        guides.addAll(g);
    }


    public void removeGuide(User g) {
        guides.remove(g);
    }

    public void removeGuides(LinkedList<User> guideList) {
        for (User guideToRemove : guideList) {
            guides.remove(guideToRemove);
        }
    }


    public LinkedList<User> getGuides() {
        return guides;
    }

    public void addTourist(User t) {
        tourists.add(t);
    }


    public void addTourists(LinkedList<User> t) {
        tourists.addAll(t);
    }


    public void removeTourist(User t) {
        tourists.remove(t);
    }


    public void removeTourists(LinkedList<User> t) {
        for(User touristToRemove : t) {
            tourists.remove(touristToRemove);
        }
    }

    public LinkedList<User> getTourists() {
        return tourists;
    }


    public void setMaxOccupancy(int macOcc) {
        maxOccupancy = macOcc;
    }


    public int getMaxOccupancy() {
        return maxOccupancy;
    }


    public void setMinOccupancy(int minOcc) {
        minOccupancy = minOcc;
    }


    public int getMinOccupancy() {
        return minOccupancy;
    }


    public int getOccupancy() {
        return occupancy;
    }


    public void addLandmark(Landmark l) {
        Log.v("Tour Class", String.valueOf(l == null));
        landmarks.add(l);
    }

    public void addLandmarks(LinkedList<Landmark> ls) {
        landmarks.addAll(ls);
    }

    public void removeLandmark(Landmark l) {
        landmarks.remove(l);
    }

    public void removeLandmarks(LinkedList<Landmark> ls) {
        for (Landmark l : ls) {
            landmarks.remove(l);
        }
    }

    public LinkedList<Landmark> getLandmarks() {
        return landmarks;
    }


    public void setRadius(int r) {
        radius = r;
    }

    public void setPrice(double p) throws Exception {
        if (p < 0) {
            throw new Exception("Invalid price: cannot be less than 0");
        }
        else {
            price = p;
        }
    }

    public double getPrice() {
        return price;
    }




}
