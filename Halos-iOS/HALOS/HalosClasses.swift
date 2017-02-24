//
//  HalosClasses.swift
//  testing
//
//  Created by Vincent Perez Jr on 2/23/17.
//  Copyright © 2017 Vincent Perez Jr. All rights reserved.
//

import Foundation

class User {
    var name : String
    var username : String
    var password : String
    var email : String
    // TODO: profile picture?
    var rating : Rating
    var traveled : [Tour : Bool]()
    var created : [Tour : Bool]()
    var guided : [Tour : Bool]()
    var shoppingCart : ShoppingCart?
    var radius : Double

    
    init(name : String, u username : String, p password: String, e email : String) {
        self.name = name
        self.username = username
        self.password = password
        self.email = email
        self.rating = Rating()
        self.traveled = nil
        self.guided = nil
        self.created = nil
        self.shoppingCart = nil
        self.radius = 1000
    }
    
    func setName(n : String) {
        self.name = n
    }
    
    func getName() -> String {
        return self.name
    }
    
    func setPassword(p : String) {
        self.password = p
    }
    
    func getPassword() -> String {
        return self.password
    }
    
    func getRating() -> Rating {
        return rating
    }
    
    func setRating(r : Double) -> Bool {
        if r <= 10 && r >= 0 {
            self.rating.addVote(r)
            return true
        }
        else {
            return false
        }
    }
    
    func addTraveled(t : Tour) {
        traveled[t] = true
    }
    
    func removeTraveled(t : Tour) {
        traveled[t] = nil
    }
    
    func addCreated(t : Tour) {
        created[t] = true
    }
    
    func removeCreated(t : Tour) {
        created[t] = nil
    }
    
    func addGuided(t : Tour) {
        guided[t] = true
    }
    
    func removeGuided(t : Tour) {
        guided[t] = nil
    }
    
    func getShoppingCart() -> ShoppingCart? {
        if let s = self.shoppingCart {
            return s
        }
        else {
            return nil
        }
    }
    
    func getRadius() -> Double {
        return radius
    }
    
    func setRadius(r : Double) -> Bool {
        if r <= 0 {
            return false
        }
        else {
            self.radius = r
            return true
        }
    }
}

class Rating {
    var sum : Double
    var votes : Int
    var average : Double? {
        if votes == 0 {
            return nil
        }
        else {
        return sum / Double(votes)
        }
        
    }
    
    init() {
        self.sum = 0
        self.votes = 0
    }
    
    func addVote(v : Double) {
        sum += sum + v
        votes += 1
    }
    
    func getAverage() -> Double? {
        if let a = average {
            return a
        }
        else {
            return nil
        }
    }
}

class Tour {
    var contactInfo : [String : String}()
    
}


