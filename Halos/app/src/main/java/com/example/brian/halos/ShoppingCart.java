package com.example.brian.halos;

import java.util.LinkedList;
/**
 * Created by brian on 2/9/17.
 */

public class ShoppingCart {
    //
//    class ShoppingCart():
//    __ShoppingCart = None
   static LinkedList<Tour> shoppingCart;

    public void ShoppingCart() {
        shoppingCart = new LinkedList<Tour>();
    }

//    def __init__(self):
//    self.__ShoppingCart = OrderedDict()


//    def AddToCart(self, tour, count):
//            if isinstance(tour, type(Tour())):
//            if tour in self.__ShoppingCart:
//    self.__ShoppingCart[tour] += count
//    else:
//    self.__ShoppingCart[tour] = count
//    else:
//            return Exception("input is not of type Tour")
    public void addToCart(Tour t) {
        shoppingCart.add(t);
    }


//    def RemoveFromCart(self, tour):
//            if isinstance(tour, type(Tour())):
//            if tour in self.__ShoppingCart:
//    self.__ShoppingCart[tour] -= 1
//
//            if self.__ShoppingCart[tour] <= 0:
//    del (self.__ShoppingCart[tour])
//    else:
//            return Exception("input is not of type Tour")
    public void removeFromCart(Tour t) {
        if (shoppingCart.size() <= 0) {
            return;         // no tour is in the list the remove
        }
        // TODO: need a way to distinguish tours (python code show no name or id for tours)

    }


//    def GetCart(self):
//            return self.__ShoppingCart
    public LinkedList<Tour> getShoppingCart() {
        return shoppingCart;
    }
}
