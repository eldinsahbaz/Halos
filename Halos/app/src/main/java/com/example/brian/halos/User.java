package com.example.brian.halos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by brian on 2/9/17.
 * Class meant to store User information such as the tours
 * they created and bought but wasn't implemented since
 * we pulled data about User through requests to server instead.
 * Contains commented python code that hasn't been converted to Java.
 */

public class User implements Parcelable {
//    class User():
//    __Name = None
//            __UserName = None
//    __Password = None
//            __ProfilePicture = None
//    __Rating = None
//            __Traveled = None
//    __Created = None
//            __Guided = None
//    __Cart = None
//            __Radius = None
    private static String name;
    private static String password;        // TODO: all passwords should be encypted
    private static String email;
    // TODO: why are name and username different things?
    // TODO: profile picture
    // TODO: why does user have a rating variable, what are they rating
    protected static Map<Tour, Boolean> travelled;
    protected static Map<Tour, Boolean> created;       // TODO: maybe change these list to hash tables?
    protected static Map<Tour, Boolean> guided;
    protected static ShoppingCart userShoppingCart;
    protected static int rating;
    protected static int radius;
    protected static String category;
    protected static String keyword;
    protected static String openNow;
    protected static String rankBy;
    protected static double minPrice;
    protected static double maxPrice;
    protected static String mode;
    protected static String id;



//    def __init__(self):
//    self.__Name = ""
//    self.__UserName = ""
//    self.__Password = ""
//    self.__ProfilePicture = Image.new("RGB", (512,512), "white")
//    self.__Rating = Rating()
//    self.__Traveled = OrderedDict()
//    self.__Curated = OrderedDict()
//    self.__Guided = OrderedDict()
//    self.__ShoppingCart = ShoppingCart()
//    self.__Radius = 0     //TODO: makes no sense to initialize radius to 0, then they cant search for anything
    public User() {
        name = "";
        // TODO: add variables in todo above
        password = "";
        email = "";
        travelled = new HashMap<Tour, Boolean>();
        created = new HashMap<Tour, Boolean>();
        guided = new HashMap<Tour, Boolean>();
        userShoppingCart = new ShoppingCart();
        category = "";
        keyword = "";
        openNow = "true";
        minPrice = 0.00;
        maxPrice = 99.99;
        rating = 0;
        radius = 3;    // measured in meters   ~1610 meters in a mile
    }

    public User(String n, String p, String e) {
        name = n;
        // TODO: add variables in todo above
        password = p;
        email = e;
        travelled = new HashMap<Tour, Boolean>();
        created = new HashMap<Tour, Boolean>();
        guided = new HashMap<Tour, Boolean>();
        userShoppingCart = new ShoppingCart();
        category = "";
        keyword = "";
        openNow = "true";
        minPrice = 0.00;
        maxPrice = 99.99;
        rating = 0;
        radius = 3;    // measured in meters   ~1610 meters in a mile
    }

//    def SetName(self, name):
//    self.__Name = str(name)
    public void setName(String n) {
        name = n;
    }

//    def GetName(self):
//            return self.__Name
    public static String getName() {
        return name;
    }

//    TODO: username stuff
//    def SetUserName(self, name):
//    self.__UserName = str(name)

//    def GetUserName(self):
//            return self.__UserName
//
//    def SetPassword(self, password):
//    self.__Password = base64.b64encode(XOR.new(SecretKey).encrypt(str(password)))
    public void setPassword(String p) {
        // TODO: have to encrypt
        password = p;
    }

//    def GetPassword(self):
//            return self.__Password
    public String getPassword() {
        return password;
    }

    public void setEmail(String e) {
        email = e;
    }

    public String getEmail() {
        return email;
    }

//    TODO: profile picture stuff
//    def SetProfilePicture(self, photoPath):
//            try:
//    self.__ProfilePicture = Image.open(photoPath)
//    except IOError:
//            return ("incorrect input")
//
//    def GetProfilePicture(self):
//            return self.__ProfilePicture

//    def GetRating(self):
//            return self.__Rating
    public int getRating() {
        return rating;  // what rating is this?
    }

//    def AddTraveled(self, tour, time):
//            if isinstance(tour, type(Tour())) and isinstance(time, type(datetime(1, 1, 1))):
//            if (self.__Traveled).has_key(tour):
//            (self.__Traveled)[tour].append(time)
//    else:
//            (self.__Traveled)[tour] = list(time)
//    else:
//            return Exception("invalid input")
    public void addTravelled(Tour t) {
        travelled.put(t, true);
    }

//    def RemoveTraveled(self, tour):
//            if isinstance(tour, type(Tour())):
//    del (self.__Traveled)[tour]
//            else:
//            return Exception("input not a tour")
    public void removeTravelled(Tour t) {
        for (Tour key : travelled.keySet()) {
            if (t == key) {
                travelled.remove(key);
            }
        }
    }

//    def GetTraveled(self):
//            return self.__Traveled
    public LinkedList<Tour> getTravelled() {
        LinkedList<Tour> tourList = new LinkedList<Tour>();
        // Iterating over keys only (tours are keys)
        for (Tour key : travelled.keySet()) {
            tourList.add(key);
        }
        return tourList;
    }

//    TODO: what is curated?, not mentioned above, must keep language identical
//    def AddCurated(self, tour):
//            if isinstance(tour, type(Tour())):
//            if not (self.__Curated).has_key(tour):
//            (self.__Curated)[tour] = True
//    else:
//            return Exception("invalid input")

//    def RemoveCurated(self, tour):
//            if isinstance(tour, type(Tour())):
//    del (self.__Curated)[tour]
//            else:
//            return Exception("input not a tour")
//
//    def GetCurated(self):
//            return self.__Curated

//    def AddGuided(self, tour, time):
//            if isinstance(tour, type(Tour())) and isinstance(time, type(datetime(1, 1, 1))):
//            if (self.__Guided).has_key(tour):
//            (self.__Guided)[tour].append(time)
//    else:
//            (self.__Guided)[tour] = list(time)
//    else:
//            return Exception("invalid input")
    public void addGuided(Tour t) {
        guided.put(t, true);
    }
//    def RemoveGuided(self, tour):
//            if isinstance(tour, type(Tour())):
//    del (self.__Guided)[tour]
//            else:
//            return Exception("input not a tour")
    public void removeGuided(Tour t){
        for (Tour key : guided.keySet()) {
            if (t == key) {
                guided.remove(key);
            }
        }
    }

//    def GetGuided(self):
//            return self.__Guided
    public LinkedList<Tour> getGuided() {
        LinkedList<Tour> guidedList = new LinkedList<Tour>();
        // Iterating over keys only (tours are keys)
        for (Tour key : guided.keySet()) {
            guidedList.add(key);
        }
        return guidedList;
    }

//    def GetShoppingCart(self):
//            return self.__ShoppingCart
    public ShoppingCart getUserShoppingCart() {
        return userShoppingCart;
    }

//    def SetRadius(self, radius):
//            if isinstance(radius, type(int())):
//    self.__Radius = radius
//    else:
//            return Exception("input is not an int")
    public static void setRadius(int r) throws Exception {
        if (r <= 0 || r >= Integer.MAX_VALUE) {
            throw new Exception("Invalid radius");
        }
        radius = r;
    }

//    def GetRadius(self):
//            return self.__Radius
    public static int getRadius() {
        return radius;
    }

    public static void setCategory(String c) {
        category = c;
    }

    public static String getCategory() {
        return category;
    }

    public static void setKeyword(String k) {
        keyword = k;
    }

    public static String getKeyword() {
        return keyword;
    }

    public static void setOpenNow(String o) {
        openNow = o;
    }

    public static String getOpenNow() {
        return openNow;
    }

    public static void setRankBy(String r) {
        rankBy = r;
    }

    public static String getRankBy() {
        return rankBy;
    }

    public static void setMinPrice(double p) {
        minPrice = p;
    }

    public static double getMinPrice() {
        return minPrice;
    }

    public static void setMaxPrice(double p) {
        maxPrice = p;
    }

    public static double getMaxPrice() {
        return maxPrice;
    }

    public static void setMode(String m) {
        mode = m;
    }

    public static String getMode() {
        return mode;
    }



    // return users id
    public String getId() {
        return id;
    }

    // TODO: Need to get all user information from server
    public int getUserInfo(String username) {

        return -1;                          // unsuccesful user info retrieval
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User();
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

}
