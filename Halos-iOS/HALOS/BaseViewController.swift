//
//  BaseViewController.swift
//  HALOS
//
//  Created by Brice Buccolo and Vincent Perez Jr on 1/29/17.
//  Copyright © 2017 adminbbccolo. All rights reserved.
//

import UIKit

class BaseViewController: UIViewController, SlideMenuDelegate {
    
    var currentUserBaseVC : User? = nil //sets the current user
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //function to determine which button is clicked in the menu
    func slideMenuItemSelectedAtIndex(_ index: Int32) {
        let topViewController : UIViewController = self.navigationController!.topViewController! //sets the view controller as top view controller
        print("View Controller is : \(topViewController) \n", terminator: "")
        switch(index){ //switch case for each possible button click
        case 0: //case to open the home page
            print("Home\n", terminator: "")
            self.openViewControllerBasedOnIdentifier("Home")
            break
        case 1: //case to open the user profile page
            print("User Profile\n", terminator: "")
            self.openViewControllerBasedOnIdentifier("UserProfileViewController")
            break
        case 2: //case to open the store
            print("Store\n", terminator: "")
            self.openViewControllerBasedOnIdentifier("StoreViewController")
            break
        case 3: //case to open the settings
            print("Settings\n", terminator:"")
            self.openViewControllerBasedOnIdentifier("SettingsViewController")
            break
        case 4: //case to bring you back to the main page
            print("Logout\n", terminator:"")
            self.openViewControllerBasedOnIdentifier("ViewController")
            break
        default:
            print("default\n", terminator: "")
        }
    }
    
    //function to open the view controller based on the identifier
    func openViewControllerBasedOnIdentifier(_ strIdentifier:String){
        let destViewController : UIViewController = self.storyboard!.instantiateViewController(withIdentifier: strIdentifier) //destination view controller is the view controller with matching identifier
        
        let topViewController : UIViewController = self.navigationController!.topViewController! //top view is brought to front of navigation controller stack
        
        if (topViewController.restorationIdentifier! == destViewController.restorationIdentifier!){
            print("Same VC") //It's the same view controller
        } else {
            self.navigationController!.pushViewController(destViewController, animated: true) //navigate to the destination view controller
        }
    }
    
    //function to create the slide menu bar
    func addSlideMenuButton(){
        let btnShowMenu = UIButton(type: UIButtonType.system) //create the button style
        btnShowMenu.setImage(self.defaultMenuImage(), for: UIControlState()) //set image
        btnShowMenu.frame = CGRect(x: 0, y: 0, width: 30, height: 30) //set frame dimensions
        btnShowMenu.addTarget(self, action: #selector(BaseViewController.onSlideMenuButtonPressed(_:)), for: UIControlEvents.touchUpInside) //create the button target
        let customBarItem = UIBarButtonItem(customView: btnShowMenu) //make a custom item
        self.navigationItem.leftBarButtonItem = customBarItem; // add custom item to the menu
    }

    //function to create a default image. Essentially setting a blank image for formatting purposes
    func defaultMenuImage() -> UIImage {
        var defaultMenuImage = UIImage()
        
        UIGraphicsBeginImageContextWithOptions(CGSize(width: 30, height: 22), false, 0.0)
        
        UIColor.black.setFill()
        UIBezierPath(rect: CGRect(x: 0, y: 3, width: 30, height: 1)).fill()
        UIBezierPath(rect: CGRect(x: 0, y: 10, width: 30, height: 1)).fill()
        UIBezierPath(rect: CGRect(x: 0, y: 17, width: 30, height: 1)).fill()
        
        UIColor.white.setFill()
        UIBezierPath(rect: CGRect(x: 0, y: 4, width: 30, height: 1)).fill()
        UIBezierPath(rect: CGRect(x: 0, y: 11,  width: 30, height: 1)).fill()
        UIBezierPath(rect: CGRect(x: 0, y: 18, width: 30, height: 1)).fill()
        
        defaultMenuImage = UIGraphicsGetImageFromCurrentImageContext()!
        
        UIGraphicsEndImageContext()
       
        return defaultMenuImage;
    }
    
    
    //Function to determine which button is pressed
    func onSlideMenuButtonPressed(_ sender : UIButton){
        if (sender.tag == 10) //checking if the button tag is 10
        {
            // To Hide Menu If it already there
            self.slideMenuItemSelectedAtIndex(-1);
            
            sender.tag = 0; //set tag to 0
            
            let viewMenuBack : UIView = view.subviews.last! //view pressed is last in stack
            
            //create the animation for the menu disappearing and the next screen appearing
            UIView.animate(withDuration: 0.3, animations: { () -> Void in
                var frameMenu : CGRect = viewMenuBack.frame //
                frameMenu.origin.x = -1 * UIScreen.main.bounds.size.width
                viewMenuBack.frame = frameMenu
                viewMenuBack.layoutIfNeeded()
                viewMenuBack.backgroundColor = UIColor.clear
                }, completion: { (finished) -> Void in
                    viewMenuBack.removeFromSuperview()
            })
            
            return
        }
        
        sender.isEnabled = false //disable the sender
        sender.tag = 10 //reset the tag back to 10
        
        let menuVC : MenuViewController = self.storyboard!.instantiateViewController(withIdentifier: "MenuViewController") as! MenuViewController
        menuVC.btnMenu = sender //set button menu as sender
        menuVC.delegate = self //set self as delegate
        self.view.addSubview(menuVC.view) //display the subview
        self.addChildViewController(menuVC) //add the button menu to the view controller
        menuVC.view.layoutIfNeeded()
        
        
        menuVC.view.frame=CGRect(x: 0 - UIScreen.main.bounds.size.width, y: 0, width: UIScreen.main.bounds.size.width, height: UIScreen.main.bounds.size.height); //set the menu size
        
        UIView.animate(withDuration: 0.3, animations: { () -> Void in
            menuVC.view.frame=CGRect(x: 0, y: 0, width: UIScreen.main.bounds.size.width, height: UIScreen.main.bounds.size.height);
            sender.isEnabled = true
            }, completion:nil) //animate the menu when clicked
    }
}
