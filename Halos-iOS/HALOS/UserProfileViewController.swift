//
//  UserProfileViewController.swift
//  HALOS
//
//  Created by Brice Buccolo and Vincent Perez Jr on 1/29/17.
//  Copyright © 2017 adminbbccolo. All rights reserved.
//

import UIKit
import Foundation
import Alamofire
import SwiftyJSON

class UserProfileViewController: BaseViewController, UITableViewDataSource, UITableViewDelegate {
    var userProfileCurrentUser : User? //var for current user
    var dataSourceArray : [String] = [] //an array for the data
    @IBOutlet var name : UITextField! //ui text field for the name
    override func viewDidLoad() {
        super.viewDidLoad()
        addSlideMenuButton() //add the menu button
        
        let tableView: UITableView = UITableView() //create tableView as a UITableView
        tableView.frame = CGRect(x: 0, y: 200, width: 500, height: 500) //set dimensions for tableView
        tableView.dataSource = self //set dataSource for tableView
        tableView.delegate = self //set delegate for tableView
        tableView.reloadData() //reload data for tableView
        name.text = globalUserName.sharedInstance.getUserName() //set name as username
        let req = ["username":(name.text)!] //get the username
        //make http request based on the username
        Alamofire.request("http://lcs-vc-esahbaz.syr.edu:12344/get_tour_by_user", method: .get, parameters: req).responseJSON {
            response in
            if(response.result.isFailure) { //if the response is a failure
                self.dataSourceArray.append("Something went wrong getting server info") //print output
            }
            else {
                var resp = JSON(response.result.value!) //get the resulting JSON
                var resp2 = resp["response"] //get the response
                let created = resp2["created"].array //get created tours in an array
                let bought = resp2["bought"].array //get bought tours in an array
                for one in created! {
                    self.dataSourceArray.append(one.stringValue) //append created tours to table view array
                    tableView.reloadData() //reload table view
                }
                for two in bought! {
                    self.dataSourceArray.append(two.stringValue) //append bought tours to table view array
                    tableView.reloadData() //reload table view
                }
            }
        }
        self.view.addSubview(tableView) //add the tableview to the view
    }

    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //function to create the number of sections in the table
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        NSLog("sections")
        return 1
    }
    
    //function to create the number of rows in the table
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        NSLog("rows")
        return dataSourceArray.count //the number of rows equals the number of elements in the array
    }
    
    //function to create the cell
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        NSLog("get cell")
        let cell = UITableViewCell(style: UITableViewCellStyle.value1, reuseIdentifier: "Cell") //create the cell
        cell.textLabel!.text = dataSourceArray[indexPath.row] //add the text to the cell
        cell.textLabel!.textAlignment = .center //center the text
        return cell //return the cell
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataSourceArray.count // Most of the time my data source is an array of something...  will replace with the actual name of the data source
    }
    
    //function for clicked cell
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let option = "OK" //set option
        let message = "\(dataSourceArray[indexPath.row].description)" //set message
        let alert = UIAlertController(title: dataSourceArray[indexPath.row], message: message, preferredStyle: UIAlertControllerStyle.alert) //create alert
        alert.addAction(UIAlertAction(title: option, style: UIAlertActionStyle.default, handler: nil)) //add option to alert
        self.present(alert, animated: true, completion: nil) //present alert
    }
}
