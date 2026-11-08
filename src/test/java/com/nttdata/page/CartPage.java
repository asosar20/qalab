package com.nttdata.page;

import org.openqa.selenium.By;

public class CartPage {
    public static By title         = By.cssSelector(".cart-grid-body h1.h1");
    public static By unitPrice     = By.cssSelector(".cart-item .current-price .price");
    public static By quantityInput = By.cssSelector(".cart-item input.js-cart-line-product-quantity");
    public static By lineTotal     = By.cssSelector(".cart-item .product-price strong");
    public static By subtotal      = By.cssSelector("#cart-subtotal-products .value");
    public static By total         = By.cssSelector(".cart-summary-totals .cart-total .value");
    public static By removeButtons = By.cssSelector(".cart-item a.remove-from-cart");
}