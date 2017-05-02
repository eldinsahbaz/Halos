//
//  ViewController.swift
//  HALOS
//
//  Created by Brice Buccolo and Vincent Perez Jr on 1/29/17.
//  Copyright © 2017 adminbbccolo. All rights reserved.
//

import UIKit
import AlamofireObjectMapper
import Alamofire
import SwiftyJSON


class ViewController: UIViewController {
    
    @IBOutlet var email : UITextField? //var for email text field
    @IBOutlet var password : UITextField? //var for password text field
    var currentUser : User? = nil //sets the current user
    var arrUser : [User] = [] //initialize an array of users
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.setNavigationBarHidden(true, animated: animated) //hide the navigation bar
        super.viewWillAppear(animated)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.navigationController?.setNavigationBarHidden(false, animated: animated) //show the navigation bar 
        super.viewWillDisappear(animated)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //Connected to the login button to verify login credentials.
    @IBAction func login(_ button: UIButton) {
        //make the http request
        Alamofire.request("http://lcs-vc-esahbaz.syr.edu:12344/login/auth?user=" + email!.text! + "&pw=" + password!.text!).responseJSON { response in
            let json = JSON(response.result.value!) //get the JSON response
            let jArray = json["response"] //parse the response to get to the next value
            let usernameReq = jArray[0]["username"] //get the user name
            globalUserName.sharedInstance.setUserName(name: usernameReq.string!) //set the global username
            let idReq = jArray[0]["_id"] //get the id
            globalUserName.sharedInstance.setUserId(id: idReq.string!) //set the global id
            let createdReq = jArray[0]["created"] //get whether the account was created
            let emailReq = jArray[0]["email"] //get the email address
            globalUserName.sharedInstance.setEmail(email: emailReq.string!) //set the global email
            let passReq = jArray[0]["password"] //get the password
            globalUserName.sharedInstance.setPassword(password: passReq.string!) //set the global password
            let travelledReq = jArray[0]["travelled"] //get the tours travelled
            let radiusReq = jArray[0]["radius"] //get the radius
            globalUserName.sharedInstance.setRadius(radius: "") //set the radius
            let ratingReq = jArray[0]["rating"] //get the rating
            globalUserName.sharedInstance.setRating(rating: ratingReq.doubleValue) //set the rating
            let statusReq = jArray[0]["result"] //get the result
            globalUserName.sharedInstance.setResult(result: statusReq.string!) //set the result
            let cartReq = jArray[0]["shopping_cart"] //get the shopping cart
            let guidedReq = jArray[0]["guided"] //get the guided tours
            let dict = ["username":usernameReq, "_id":idReq, "created":createdReq, "email":emailReq, "password":passReq,"travelled":travelledReq, "radius":radiusReq, "rating":ratingReq, "result":statusReq, "shopping_cart":cartReq, "guided":guidedReq] //create a dictionary with all of the user information
            self.currentUser = User(json: jArray[0]) //set the current user to the user in zero
            
            //Logic to see if the segue should be performed
            if self.shouldPerformSegue(withIdentifier: "LoginSuccess", sender: self) {
                self.performSegue(withIdentifier: "LoginSuccess", sender: self)
            }
        }
    }
    
    //function to prepare for the segue. This passes the user information to the home screen.
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "LoginSuccess" {
            
            let nav = segue.destination as! UINavigationController
            let homeVC = nav.viewControllers.first as! HomeVC
            
            homeVC.currentUserHomeVC = self.currentUser
        }
    }
    
    //Function to check if the segue should be performed.
    override func shouldPerformSegue(withIdentifier identifier: String, sender: Any?) -> Bool {
        //if login is successful, check if status is equal login
        if identifier == "LoginSuccess" {
            if currentUser!.status! == "login successful" {
                return true
            } else if currentUser!.status! == "incorrect password"{ //else incorrect password
                let message = "Incorrect Password" //create message
                let option = "OK" //create option
                let alert = UIAlertController(title: "Warning", message: message, preferredStyle: UIAlertControllerStyle.alert) //make the alert
                alert.addAction(UIAlertAction(title: option, style: UIAlertActionStyle.default, handler: nil)) //add action to alert
                self.present(alert, animated: true, completion: nil) //present the alert
                return false
            } else if currentUser!.status! == "User not found" {
                let message = "Invalid username" //create message
                let option = "OK" //create option
                let alert = UIAlertController(title: "Warning", message: message, preferredStyle: UIAlertControllerStyle.alert) //make the alert
                alert.addAction(UIAlertAction(title: option, style: UIAlertActionStyle.default, handler: nil)) //add action to alert
                self.present(alert, animated: true, completion: nil) //present the alert
                return false
            }
        }
        return true
    }

}
