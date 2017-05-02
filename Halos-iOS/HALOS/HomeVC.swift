//
//  HomeVC.swift
//  HALOS
//
//  Created by Brice Buccolo and Vincent Perez Jr on 1/29/17.
//  Copyright © 2017 adminbbccolo. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation
import Alamofire
import SwiftyJSON

//Class to control the view controller containing the map
class HomeVC: BaseViewController, MKMapViewDelegate, CLLocationManagerDelegate {
    @IBOutlet weak var mapView: MKMapView! //var for map
    
    @IBOutlet weak var startTour: UIButton! //var for the start tour button
    
    var currentUserHomeVC : User? //var for current user
    
    let btn = UIButton(type: .contactAdd) //button for the annotation
    var btnTag = 0 //var for button tag

    let span = CLLocationDistance(1500) //set the span
    var locations: [Location] = [] //create array of locations
    var points : [CLLocationCoordinate2D] = [CLLocationCoordinate2D]() //create array of coordinates
    var route : [CLLocationCoordinate2D] = [CLLocationCoordinate2D]() //create array of coordinates
    var totalRoute: [CLLocationCoordinate2D] = [CLLocationCoordinate2D]() //create array of coordinates
    var routeNames: [String] = [] //create array of string for route name
    var locArray: [JSON] = [] //create array of JSON objects for location
    var name: String = "" //set name
    var pri: String = "" //set price
    var desc: String = "" //set description
    var initialCoord : CLLocationCoordinate2D? = nil //set initial coordinate
    var currentRouteLocations : [Location] = [] //set the current route locations
    var saveTour : Tour? = nil //var to save the tour
    
    let manager = CLLocationManager() //create location manager
    
    //function to control the location manager
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let location = locations[0] //get the first location from locations
        let span:MKCoordinateSpan = MKCoordinateSpanMake(0.01, 0.01) //set the span of view
        let myLocation:CLLocationCoordinate2D = CLLocationCoordinate2DMake(location.coordinate.latitude, location.coordinate.longitude) //set myLocation to the lat and long of location
        
        initialCoord = myLocation //set the initial coordinate to myLocation
        route.append(initialCoord!) //add starting point to route
        
        let region:MKCoordinateRegion = MKCoordinateRegionMake(myLocation, span) //make the region of view
        mapView.setRegion(region, animated:true) //set the region
        
        self.mapView.showsUserLocation = true //make this the user location
    }
    
    //function to occur when view loads
    override func viewDidLoad() {
        super.viewDidLoad()
        addSlideMenuButton() //add menu button
        
        mapView.delegate = self //create delegate for mapview
        
        manager.delegate = self //create delegate for  manager
        manager.desiredAccuracy = kCLLocationAccuracyBest //set the accuracy
        manager.requestWhenInUseAuthorization() //set the request for authorization to use user's location
        manager.startUpdatingLocation() //start updating the users location
        for i in 0...1000 {
            print(i)
        }
        manager.stopUpdatingLocation() //stop updating the user's location
        
        let lat = "43.041768" //set the latitude for the simulator
        let lng = "-76.136113" //set the longitude for the simulator
        let rad = "1000" //set the radius for the simulator
        let keyword = "" //initialize the keyword
        
        let param = ["lat":lat, "lng":lng, "radius":rad, "keyword": keyword] //set the parameters
        
        //create http request
        Alamofire.request("http://lcs-vc-esahbaz.syr.edu:12344/get_places", method: .post, parameters: param, encoding: JSONEncoding.default).responseJSON { response in
            let resp = JSON(response.result.value!) //get the response
            let array = resp["results"].array //set array to an array of results from resp
            for i in 0...array!.count-1 { //go through the entire array
                let resObject = array![i] //get the object at i
                self.locArray.append(array![i]) //append to the local array
                let geoObject = resObject["geometry"] //get the geometry
                let name = resObject["name"].string //get the name
                let locObject = geoObject["location"] //get the location
                let addrJSON = resObject["vicinity"].string //get the vicinity
                let radJSON = resObject["radius"].string //get the radius
                let ratingJSON = resObject["rating"].string //get the rating
                let cataArrJSON = resObject["types"].array//get the type
                let catJSON = cataArrJSON![0].string //get the category
                let latJSON = locObject["lat"].doubleValue //get the lat
                let lngJSON = locObject["lng"].doubleValue //get the long
                let build = Location(title: name!, address: addrJSON!, state: "New York", country: "USA", category: catJSON!, rating: "4.5", coordinate: CLLocationCoordinate2DMake(latJSON, lngJSON)) //build the locaton
                self.locations.append(build) //append the location
                let annotation = MKPointAnnotation() //create new annotation
                annotation.title = build.title //set annotation title
                annotation.subtitle = build.address //set annotation subtitle
                annotation.coordinate = build.coordinate //set annotation coordinate
                self.mapView.addAnnotation(build) //add the annotation to the location
            }
            self.populateMap() //populate map
        }
        
    }
    
    //function to populate the map with locations
    func populateMap() -> Void {
        if (route.count > 1) { //check how many locations in route
            for i in 0...(route.count-1)  {
                points.append(route[i]) //append the locations to points
            }
            let polyline = MKPolyline(coordinates: points, count: points.count) //create var for polyline
            for index in 0...(route.count-2) { //for loop to create polylines between locations
                let sourcePlacemark = MKPlacemark(coordinate: route[index], addressDictionary: nil) //var for source
                let destinationPlacemark = MKPlacemark(coordinate: route[index+1], addressDictionary: nil) //var for destination
                let sourceMapItem = MKMapItem(placemark: sourcePlacemark) //var for source map item
                let destinationMapItem = MKMapItem(placemark: destinationPlacemark) //var for destination map item
                let directionRequest = MKDirectionsRequest() //var for direction request
                directionRequest.source = sourceMapItem //add source to direction request
                directionRequest.destination = destinationMapItem //add destination to direction request
                directionRequest.transportType = .automobile //add transportation type
                
                let directions = MKDirections(request: directionRequest) //create directions
                
                directions.calculate(completionHandler: { //calculate the directions
                    (response, error) -> Void in
                    
                    guard let response = response else {
                        if let error = error {
                            print("haha this is where the problem is \(error)")
                        }
                        return
                    }
                    
                    let route = response.routes[0] //set route to first location
                    self.mapView.add(route.polyline, level: .aboveRoads) //create the polylines between locations
                })
            }
        }
        else {
            
        }
    }
    
    //func to load the map on a certain location
    func loadMapOnLocation(location: CLLocation, span: CLLocationDistance){
        let region = MKCoordinateRegionMakeWithDistance(location.coordinate, span*2, span*2) //var for the region
        self.mapView.setRegion(region, animated: true) //set the region on the map
    }
    
    //func to prepare for segue
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "Login Successful" { //if login successful
            
            let nav = segue.destination as! UINavigationController //set the navigation controller
            let baseVC = nav.viewControllers.first as! HomeVC //set the base view controller
            baseVC.currentUserBaseVC = self.currentUserHomeVC //relay user information
        }
    }
    
    //func for mapview
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        
        if annotation is MKUserLocation { //return nil so map view draws "blue dot" for standard user location
            return nil
        }
        
        let reuseId = "pin" //set reuse id
        
        var pinView = mapView.dequeueReusableAnnotationView(withIdentifier: reuseId) as? MKPinAnnotationView //set the pin view
        if pinView == nil { //if there is no pin view
            pinView = MKPinAnnotationView(annotation: annotation, reuseIdentifier: reuseId) //set the annotation
            pinView!.canShowCallout = true //animate the callout
            pinView!.animatesDrop = true //animate drop
            pinView!.pinTintColor = .green //set pin color
        }
        else {
            pinView!.annotation = annotation //set pin view to annotation
        }
        
        pinView?.rightCalloutAccessoryView = btn //set the callout to button
        
        return pinView //return pin view
    }
    
    //function to set the button action
    func buttonAction(sender:UIButton)
    {
        print("Button tapped: \(btn.tag)") //print the button tag
    }
    
    //func to control the annotation that has been tapped
    func mapView(_ mapView: MKMapView, annotationView view: MKAnnotationView,
                 calloutAccessoryControlTapped control: UIControl) {
        
        let selectedLoc = view.annotation //set selected location
        
        routeNames.append((selectedLoc!.title!)!) //add location to route names
        
        let selectedPlacemark = MKPlacemark(coordinate: (selectedLoc?.coordinate)!, addressDictionary: nil) //set selected placemark
        route.append(selectedPlacemark.coordinate) //add to route
        
        totalRoute.append(selectedPlacemark.coordinate) //add to total route
        for loc in locations { //for loc in locations
            if (selectedPlacemark.coordinate.latitude == loc.coordinate.latitude) &&
                (selectedPlacemark.coordinate.longitude == loc.coordinate.longitude) {
                currentRouteLocations.append(loc)//append the location to current route location
            }
        }
    }
    
    //function to render the polyline
    func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer! {
        if overlay is MKPolyline {
            let polylineRenderer = MKPolylineRenderer(overlay: overlay) //set polyline renderer
            polylineRenderer.alpha = 0.5 //set alpha
            polylineRenderer.strokeColor = UIColor.blue //set color of line
            polylineRenderer.lineWidth = 3 //set line width
            return polylineRenderer //return polyline renderer
        }
        return nil
    }
    
    //function to show alert when add button is clicked
    @IBAction func showAlertButtonTapped(_ sender: UIButton) {
        
        var tourNameTextField: UITextField? //tour name text field
        var descriptionTextField : UITextField? //description text field
        var priceTextField : UITextField? //price text field
        if totalRoute.count > 0 {
            // create the alert
            let alertController = UIAlertController(title: "Save Tour", message: "Let's save your tour", preferredStyle: .alert)
            
            let saveAction = UIAlertAction(title: "Save", style: .default, handler:{ //save action
                alert -> Void in
                self.populateMap() //populate map
                
                let firstTextField = alertController.textFields![0] as UITextField //set first text field
                let secondTextField = alertController.textFields![1] as UITextField //set second text field
                
                if let tourName = tourNameTextField!.text{
                    self.name = tourName //set the tour name
                }
                
                if let description = descriptionTextField!.text {
                    self.desc = description //set the description
                }
                
                if let price = priceTextField!.text {
                    self.pri = price //set the price
                }
                
                self.makeTour() //call make tour function
                var Lat : String = "" //set latitude
                var Long : String = "" //set longitude
                for loc in self.saveTour!.landmarks { //for location in landmarks
                    Lat = Lat + String(loc.coordinate.latitude) + "|" //set lat
                    Long = Long + String(loc.coordinate.longitude) + "|" //set long
                }
                Lat.remove(at: Lat.index(before: Lat.endIndex)) //remove lat
                Long.remove(at: Long.index(before: Long.endIndex)) //remove long
                let jsonSave : [String : Any] = ["tourid":self.saveTour!.name!, "created-by":globalUserName.sharedInstance.getUserName(), "description":self.saveTour!.description, "price":String(describing: self.saveTour!.price), "Lat":Lat, "Long":Long] //save tour as JSON
                
                //http request to add tour to db
                Alamofire.request("http://lcs-vc-esahbaz.syr.edu:12344/addtour" , method: .post , parameters: jsonSave, encoding: JSONEncoding.default).responseJSON {
                    response in
                    if(response.result.isFailure) { //if response is a failure
                        self.present(self.makeAlert(string: "Failed to connect to the server."), animated: true, completion: nil) //present the error message
                    }
                    else {
                        let json = JSON(response.result.value!) //set json to response result
                        let resp = json["response"] //set response
                        if resp["result"].stringValue == "tour successfully created" {
                        self.present(self.makeAlert(string: "Tour uploaded!"), animated: true, completion: nil) //present alert
                        }
                        else {
                        self.present(self.makeAlert(string: "Tour name already in use!"), animated: true, completion: nil) //present alert
                        }
                    }
                }
            })
            
            //clear all fields if action cancelled
            let cancelAction = UIAlertAction(title: "Cancel", style: .default, handler: { (action: UIAlertAction!) -> Void in
                self.locations = []
                self.points = []
                self.route = []
                self.totalRoute = []
                self.routeNames = []
                self.locArray = []
                self.name = ""
                self.pri = ""
                self.desc = ""
                //var creator : User
                self.currentRouteLocations = []
            })
        
            alertController.addTextField { (textField : UITextField!) -> Void in
                tourNameTextField = textField //set tour name text field
                tourNameTextField?.placeholder = "Tour name" //create placeholder
                
            }
            
            alertController.addTextField { (textField : UITextField!) -> Void in
                descriptionTextField = textField //set description text field
                descriptionTextField?.placeholder = "Description" //create placeholder
            }
            
            alertController.addTextField { (textField : UITextField!) -> Void in
                priceTextField = textField //set price text field
                priceTextField?.placeholder = "Price" //create placeholder
            }
            
            alertController.addAction(saveAction) //add save button
            alertController.addAction(cancelAction) //add cancel button
            
            self.present(alertController, animated: true, completion: nil) //present the alert
                
        }
    }
    
    //func to make tour
    func makeTour(){
         self.saveTour = Tour(name: name, contactInfo: "", guides: [], tourists: [], minOccupancy: 0, maxOccupancy: 15, occupancy: 1, landmarks: currentRouteLocations, radius: 3000, ratings: 5, creator: globalUserName.sharedInstance.getUserName(), description: desc, price: Double(pri)!) //save the tour with all params
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    //func to make alert
    func makeAlert(string: String) -> UIAlertController {
        let message = string //set message
        let option = "OK" //set option
        let alert = UIAlertController(title: "Alert", message: message, preferredStyle: UIAlertControllerStyle.alert) //create alert
        alert.addAction(UIAlertAction(title: option, style: UIAlertActionStyle.default, handler: nil)) //add action to alert
        return alert //return alert
    }
}
