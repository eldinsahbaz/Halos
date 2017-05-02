//
//  MenuViewController.swift
//  HALOS
//
//  Created by Brice Buccolo and Vincent Perez Jr on 1/29/17.
//  Copyright © 2017 adminbbccolo. All rights reserved.
//

import UIKit

//create protocol to conform to
protocol SlideMenuDelegate {
    func slideMenuItemSelectedAtIndex(_ index : Int32)
}

class MenuViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    
    //Array to display menu options
    @IBOutlet var tblMenuOptions : UITableView!
    
    //Transparent button to hide menu
    @IBOutlet var btnCloseMenuOverlay : UIButton!
    
    //Array containing menu options
    var arrayMenuOptions = [Dictionary<String,String>]()
    
    //Menu button which was tapped to display the menu
    var btnMenu : UIButton!
    
    //Delegate of the MenuVC
    var delegate : SlideMenuDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tblMenuOptions.tableFooterView = UIView()
        // Do any additional setup after loading the view.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated) //Animate the menu
        updateArrayMenuOptions() //call function to set the array of menu options
    }
    
    //function to create an array of menu options
    func updateArrayMenuOptions(){
        arrayMenuOptions.append(["title":"Home", "icon":""]) //add Home to array
        arrayMenuOptions.append(["title":"User Profile", "icon":""]) //add User Profile to array
        arrayMenuOptions.append(["title":"Store", "icon":""]) //add Store to array
        arrayMenuOptions.append(["title":"Settings", "icon":""]) //add Settings to array
        arrayMenuOptions.append(["title":"Logout", "icon":""]) //add Logout to array
        
        tblMenuOptions.reloadData() //reload the updated table
    }
    
    //action for when menu item is clicked
    @IBAction func onCloseMenuClick(_ button:UIButton!){
        btnMenu.tag = 0 //set button tag to 0
        
        if (self.delegate != nil) { //if a button was clicked
            var index = Int32(button.tag) //set the index to the button tag
            if(button == self.btnCloseMenuOverlay){ //if the button clicked closes the menu
                index = -1 //set index to -1
            }
            delegate?.slideMenuItemSelectedAtIndex(index) //choose the menu item at index
        }
        
        //animate the item that was pressed
        UIView.animate(withDuration: 0.3, animations: { () -> Void in
            self.view.frame = CGRect(x: -UIScreen.main.bounds.size.width, y: 0, width: UIScreen.main.bounds.size.width,height: UIScreen.main.bounds.size.height)
            self.view.layoutIfNeeded()
            self.view.backgroundColor = UIColor.clear
            }, completion: { (finished) -> Void in
                self.view.removeFromSuperview()
                self.removeFromParentViewController()
        })
    }
    
    //function to create the table view for the buttongs
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell : UITableViewCell = tableView.dequeueReusableCell(withIdentifier: "cellMenu")! //create the cell
        
        cell.selectionStyle = UITableViewCellSelectionStyle.none //set selection style
        cell.layoutMargins = UIEdgeInsets.zero //set layout margin
        cell.preservesSuperviewLayoutMargins = false //preserve the original margins
        cell.backgroundColor = UIColor.clear //set the the background color
        
        let lblTitle : UILabel = cell.contentView.viewWithTag(101) as! UILabel
        let imgIcon : UIImageView = cell.contentView.viewWithTag(100) as! UIImageView
        
        imgIcon.image = UIImage(named: arrayMenuOptions[indexPath.row]["icon"]!) //set image icon
        lblTitle.text = arrayMenuOptions[indexPath.row]["title"]! //set label title
        
        return cell //return the label title
    }
    
    //function for table view
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let btn = UIButton(type: UIButtonType.custom)
        btn.tag = indexPath.row //set the button tag
        self.onCloseMenuClick(btn) //set the button clicked to the specified button
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return arrayMenuOptions.count //return the number of rows
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1; //there is only one section
    }
}
