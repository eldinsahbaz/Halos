#Halos
Halos is an application that adopts principles similar to that of Uber or Airbnb. It is essentially a market place where tourists may connect to services others provide or connect with locals! We work to enhance the tourist experience by allowing you to take mobile guided tours that either we generate based on your location, you create yourself, or you purchase from our store. Users may create and upload tours to the store for others to download! The store features free and paid tours.
 
 Android and IOS code is found in this github link : https://github.com/eldinsahbaz/Halos
 
 Instructions for running Program on Android - Requires Actual Android Device for all functionalities.
 
 1. Download Android Studio and clone this code from Github into an empty git repository through git init command.
 
 2. Open Android Studio, set it up and download the required libraries through the SDK manager which includes
     i.   Android SDK tools
     ii.  Google Play APK expansion Library
     iii. Google Play services
     iv.  Google Play Licensing Library
     v.   Google USB Driver
     vi.  Google Web Drive
     vii. Android Support Repository
 
 3. Open project and sync project with android Studio - change gradle files and package names to your own package name if required. Another possible solution is cleaning and refactoring the project.
 
 4. Click the green arrow play button on top to run the application after you connect your android device via USB to your local machine.
  
  # App Walkthrough
  
1. Login
  	upon opening the app you should find the login page. If you already have an account you may enter your credentials. If you do not have an account, then you have the option to either create a new account or logging in without any credentials. Logging in without any credentials puts you in 'guest mode.' Data in guest mode will be deleted after logging out. If you have an accout but forgot your login credentials, you may redeem them via the forgot button.
  	
2. Home page
  	Upon logging in, the user is brought to the home page. The home page is a map of the surrounding area with points of interest denoted as the red markers. The purple markers denotes your current location. You may click on any one of the red markers to get more information about the point of interest. In the info-card you will find the name, the address, the location type, as well as a rating score (rating by Google Places). If you hold down the info-card, this will add that landmark to your tour. If you hold the info-card down again, the landmark will be removed from your tour. You may add up to (and including) 9 landmarks to your tour. If you want to take the tour, you simply click on the start tour button at the bottom.From the home page you may also navigate to any other page via the drop down menu in the top right corner of the app.
  	
 3. Tour
  	On the tour page, you will see a map with the route plotted on it and your current location denoted by the landmark. your current location will update as you move so that you may track your progress throughout the tour. If you really like the tour, you may save it. This saves it to your account and also uplaods it to the in-app store. When uploading the tour, you must name your tour, give it a description, and give it a price. If you choose the price to be $0, then it's free. Otherwise, other's will be charged for purchasing the tour and you will recieve a portion of the sale. If you finish the tour or decide to end it early, you may click the cancel button to bring you back to the home page.
  	
 4. User profile
  	On the user profle, we display your name, rating, profile picture, and all the tours you've bought/created. If you click on the tour, you will be brought to a map with the route and all the landmarks plotted on it. Anything created for a user in iOS will be seen on Android and vice-versa.
  	
 5. Store
  	In the store, the user can swipe back and forth between three tabs (the 'Hot Tours' tab, 'Top Paid' tab, and 'Top Free' tab). The Hot Tours tab contains the top ranked tours among paid and free tours, the top Paid tours displays the top ranked paid tours, and the Top Free tours displas the top ranked free tours. You may click the tour card to get more information on it. If you like one of the tours and would like to purchase it, you click the green add button in the bottom to add it to your shopping cart. If you decide that you no longer want to purchase a tour in your cart, you simply click the green button on that tour's card again to remove it from you cart.
  	If you have tours in your shopping cart that you would like to purchase, you click the shopping cart button in the top right corner to see the tours in your shopping cart and may click the purchase button at the bottom of the screen. This will prompt you to enter your PayPal information. Once the transaction is processed, you will be taken back to the home page.
  	
 6. Settings Page
  	The settings page allows you to update certain settings on your account to make your experience more enjoyable. You may adjust the type of landmarks that show up, transporation method, max tour radius, etc.
  
7. Logout
  	You may logout to be taken back to the login page
