package com.example.brian.halos;

import java.util.LinkedList;
/**
 * Created by brian on 2/9/17.
 * This class wasn't used for final demo but its purpose it to be used
 * for storing tours that users add while browsing the store which be
 * displayed when checking out. Stores tour Objects in a LinkedList.
 */

public class ShoppingCart {

   static LinkedList<Tour> shoppingCart;

    public void ShoppingCart() {
        shoppingCart = new LinkedList<Tour>();
    }


    //Adds a Tour to Cart
    public void addToCart(Tour t) {
        shoppingCart.add(t);
    }

    //Remove a tour from cart
    public void removeFromCart(Tour t) {
        if (shoppingCart.size() <= 0) {
            return;         // no tour is in the list the remove
        }
        // TODO: need a way to distinguish tours (python code show no name or id for tours)

    }

    //Returns current cart.
    public LinkedList<Tour> getShoppingCart() {
        return shoppingCart;
    }
}
