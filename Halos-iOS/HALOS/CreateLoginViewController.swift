//
//  CreateLoginViewController.swift
//  HALOS
//
//  Created by Brice Buccolo and Vincent Perez Jr on 1/30/17.
//  Copyright © 2017 adminbbccolo. All rights reserved.
//

import UIKit
import AlamofireObjectMapper
import Alamofire
import SwiftyJSON

class CreateLoginViewController: UIViewController {
    
    @IBOutlet var email : UITextField? //var for email
    @IBOutlet var password : UITextField? //var for password
    @IBOutlet var checkPassword : UITextField? //var to check password
    @IBOutlet var userName: UITextField? //var for username
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
//    override func shouldPerformSegue(withIdentifier identifier: String, sender: Any?) -> Bool {
//        return false
//    }
    
    //make alert for created or not created
    func makeAlert(string: String) -> UIAlertController {
        let message = string
        let option = "OK"
        let alert = UIAlertController(title: "Alert", message: message, preferredStyle: UIAlertControllerStyle.alert)
        alert.addAction(UIAlertAction(title: option, style: UIAlertActionStyle.default, handler: nil))
        return alert
    }
    
    //action for clicking the create button
    @IBAction func create(_ sender: UIButton) {
        if(password!.text! == checkPassword!.text) { //check if the passwords match
            let parameters = ["username":userName!.text!, "password":password!.text!, "email":email!.text!, "radius":"1000"] //parameters for http request
            //make http request using parameters
            Alamofire.request("http://lcs-vc-esahbaz.syr.edu:12344/login/new" , method: .post , parameters: parameters, encoding: JSONEncoding.default).responseJSON {
                response in
                if(response.result.isFailure) { //check if connected to server
                    self.present(self.makeAlert(string: "Failed to connect to the server."), animated: true, completion: nil) //present alert
                }
                else {
                    let json = JSON(response.result.value)
                    let servResponse : String
                    if let testing = json["response"]["result"].string { //get the servers response
                        servResponse = testing
                    }
                    else {
                        servResponse = json["result"].string!
                    }
                    
                    switch(servResponse) { //switch to different server responses
                    case "account created successfully": //account created successfully
                        //present alert
                        self.present(self.makeAlert(string: "Account successfully created!"), animated: true, completion:  {
                            () -> Void in
                            //perform segue to title screen
                            self.performSegue(withIdentifier: "CreateAccount", sender: nil)
                        })
                    case "username is taken": //username taken
                        self.present(self.makeAlert(string: "Username already exists!"), animated: true, completion: nil) //present alert
                    case "email is already linked to an account": //email already taken
                        self.present(self.makeAlert(string: "E-mail is already taken by another account!"), animated: true, completion: nil) //present alert
                    default: //default case
                        self.present(self.makeAlert(string: "Something went wrong..."), animated: true, completion: nil) //present alert
                    }
                }
            }
        }
        else {
            self.present(self.makeAlert(string: "Passwords did not match!"), animated: true, completion: nil) //passwords did not match alert
        }
    }
    
    
}
