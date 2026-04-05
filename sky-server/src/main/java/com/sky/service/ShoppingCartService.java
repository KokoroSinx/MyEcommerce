package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * Add to Cart
     * @param shoppingCartDTO
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * View the cart
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * Clear the cart
     */
    void cleanShoppingCart();
}
