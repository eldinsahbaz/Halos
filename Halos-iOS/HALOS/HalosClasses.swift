//
//  HalosClasses.swift
//  testing
//
//  Created by Brice Buccolo and Vincent Perez Jr on 2/23/17.
//  Copyright © 2017 Vincent Perez Jr. All rights reserved.
//
import Foundation
import MapKit
import Contacts
import Alamofire
import ObjectMapper
import SwiftyJSON


class User : NSObject {
    //var name : String? //string name
    var username : String? //string username
    var id : String? //string id
    //var created : [Tour] //list of tours for created
    var email : String? //string email
    var password : String? //string password
    //var travelled : [Tour] //list of tours for travelled
    var radius : String? //string radius
    var rating : String? //string rating
    var status : String? //string status
    //var shopping_cart = [Tour]() //list of tours for shopping cart
    //var guided = [Tour]() //list of tours for guided
    
    //initialize the JSON block
    init(json: JSON) {
        //self.name = "test" //set name
        self.username = json["username"].string //set username
        self.id = json["_id"].string //set id
        //self.created = json["created"][0] //set created tours
        self.email = json["email"].string //set email
        self.password = json["password"].string //set password
        //self.travelled = json["travelled"][0] //set travelled tours
        self.radius = json["radius"].string //set radius
        self.rating = json["rating"].string //set rating
        self.status = json["result"].string //set result
        //self.shopping_cart = json["shopping_cart"].string //set shopping cart
        //self.guided = json["guided"].string //set guided
    }
}

//Class to define the rating system
class Rating {
    var sum : Double //var for sum
    var votes : Int //var for votes
    var average : Double? { //var for average
        if votes == 0 {
            return nil
        }
        else {
            return sum / Double(votes)
        }
        
    }
    
    init() {
        self.sum = 0 //initialize sum
        self.votes = 0 //initialize votes
    }
    
    //function to add a vote to the votes
    func addVote(v : Double) {
        sum += sum + v
        votes += 1
    }
    
    //function to get the vote average
    func getAverage() -> Double? {
        if let a = average {
            return a
        }
        else {
            return nil
        }
    }
}

//class for location
class Location: MKMapItem, MKAnnotation {
    //"title", "subtitle", and "coordinate" are defined in the MKAnnotation protocol
    let title: String? //create the title
    let address: String //create the address
    let state: String //create the state
    let country: String //create the country
    let category: String //create the category
    var rating: String //create the rating
    var coordinate: CLLocationCoordinate2D //create the coordindate
    var subtitle: String? { //create the subtitle
        return title!
    }
    //initializer for the location
    init(title: String, address: String, state: String, country: String, category: String, rating: String, coordinate: CLLocationCoordinate2D) {
        self.title = title //set the title
        self.address = address //set the address
        self.state = state //set the state
        self.country = country //set the country
        self.category = category //set the category
        self.coordinate = coordinate //set the coordinate
        self.rating = rating //set the rating
        super.init()
    }
    
    // returns a map item for the map view to add locations to the map
    func mapItem() -> MKMapItem {
        let addressDictionary = [CNPostalAddressStreetKey: address, CNPostalAddressStateKey: state, CNPostalAddressCountryKey: country] //create a dictionary for addresses
        let placemark = MKPlacemark(coordinate: coordinate, addressDictionary: addressDictionary) //define a placemark
        let mapItem = MKMapItem(placemark: placemark) //define a map item
        
        mapItem.name = title //set the map item name
        // TODO: get the phone number for a location from google and stick it here?
        mapItem.phoneNumber = "1234567890" //set the map item phone number
        
        return mapItem //return the map item
    }
    
    //returns a color for the pin depending on the category of the ArtWork object
    func pinColor() -> UIColor {
        switch category {
        case "Statue": //case for statue
            return UIColor.blue
        case "Monument": //case for monument
            return UIColor.red
        default:
            return UIColor.green //default case
            
        }
    }
}


//class for tour
class Tour {
    
    var name : String? //var for name
    var contactInfo: String? //var for contact info
    var guides: [User] //list of users for guides
    var tourists: [User] //list of users for tourists
    var minOccupancy: Int //int for min occupancy
    var maxOccupancy: Int //int for max occupancy
    var occupancy: Int //int for current occupancy
    var landmarks: [Location] //list of locations for landmars
    var radius: Int //int for radius
    var ratings: Int //int for ratings
    var creator: String //string for creator
    var description: String //string for description
    var price: Double //double for price
    
    //initialize the tour object
    init(name: String, contactInfo: String, guides: [User], tourists: [User], minOccupancy: Int, maxOccupancy: Int, occupancy: Int, landmarks: [Location], radius: Int, ratings: Int, creator: String, description: String, price: Double){
        self.name = name //set the name
        self.contactInfo = "" //set the contact info
        self.guides = guides //set the guides
        self.tourists = tourists //set the tourists
        self.minOccupancy = 0 //set the min occupancy
        self.maxOccupancy = 15 //set the max occupancy
        self.occupancy = 1 //set the occupancy
        self.landmarks = landmarks //set the landmarks
        self.radius = 3000 //set the radius
        self.ratings = ratings //set the ratings
        self.creator = creator //set the creator
        self.description = description //set the description
        self.price = price //set the price
    }
    
}

//class for shopping cart
class ShoppingCart {
    var shoppingCart : [Tour] //a list of tours for the shopping cart
    
    //initialize the shopping cart
    init(shoppingCart: [Tour]){
        self.shoppingCart = shoppingCart //add shopping cart to the users shopping cart
    }
    
    //function to add to cart
    func addToCart(t: Tour){
        shoppingCart.append(t) //append tour to shopping cart
    }
}


//structure to create the globalized user
struct globalUserName {
    static var sharedInstance = globalUserName() //declare the global user
    //declare the global variables to be shared
    var theUserName: String = "" //username is a string
    var theUserID: String = "" //user id is a string
    var theCreatedTours: [Tour] = [] //created tours is a list of tours
    var theEmail: String = "" //email is a string
    var thePassword: String = "" //password is a string
    var theToursTravelled: [Tour] = [] //tours travelled is a list of tours
    var theRadius: String = "" //radius is a string
    var theRating: Double = 0 //rating is a double
    var theResult: String = "" //result is a string
    var theShoppingCart: [Tour] = [] //shopping cart is a list of tours
    var theGuidedTours: [Tour] = [] //guided tours is a list of tours
    
    //mutating function to set the username
    mutating func setUserName(name: String) {
        theUserName = name
    }
    
    //function to get the username
    func getUserName() -> String{
        return theUserName
    }
    
    //mutating function to set the user id
    mutating func setUserId(id: String){
        theUserID = id
    }
    
    //function to get the user id
    func getUserID() -> String{
        return theUserID
    }
    
    //mutating function to set the created tours
    mutating func setCreatedTours(created: [Tour]){
        theCreatedTours = created
    }
    
    //function to get the created tours
    func getCreatedTours() -> [Tour]{
        return theCreatedTours
    }
    
    //mutating function to set the email
    mutating func setEmail(email: String){
        theEmail = email
    }
    
    //function to get the email
    func getEmail() -> String{
        return theEmail
    }
    
    //mutating function to set the password
    mutating func setPassword(password: String){
        thePassword = password
    }
    
    //function to get the password
    func getPassword() -> String{
        return thePassword
    }
    
    //mutating function to set the tours travelled
    mutating func setToursTravelled(travelled: [Tour]){
        theToursTravelled = travelled
    }
    
    //function to get the tours travelled
    func getToursTravelled() -> [Tour]{
        return theToursTravelled
    }
    
    //mutating function to set the radius
    mutating func setRadius(radius: String){
        theRadius = "3000"
    }
    
    //function to get the radius
    func getRadius() -> String{
        return theRadius
    }
    
    //mutating function to set the rating
    mutating func setRating(rating: Double){
        theRating = rating
    }
    
    //function to get the rating
    func getRating() -> Double{
        return theRating
    }
    
    //mutating function to set the result
    mutating func setResult(result: String){
        theResult = result
    }
    
    //function to get the result
    func getResult() -> String{
        return theResult
    }
    
    //mutating function to set the shopping cart
    mutating func setShoppingCart(shoppingCart: [Tour]){
        theShoppingCart = shoppingCart
    }
    
    //function to get the shopping cart
    func getShoppingCart() -> [Tour]{
        return theShoppingCart
    }
    
    //mutating function to set the guided tours
    mutating func setGuidedTours(guided: [Tour]){
        theGuidedTours = guided
    }
    
    //function to get the guided tours
    func getGuidedTours() -> [Tour]{
        return theGuidedTours
    }
}
