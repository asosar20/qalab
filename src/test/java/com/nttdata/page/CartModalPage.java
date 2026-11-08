package com.nttdata.page;

import org.openqa.selenium.By;

public class CartModalPage {
    public static By modalTitle      = By.cssSelector(".modal-dialog .modal-content .modal-header h4");
    public static By productName     = By.cssSelector(".modal-content .product-name");
    public static By productQuantity = By.cssSelector(".modal-content .product-quantity strong");
    public static By productPrice = By.cssSelector(".modal-content p.product-price");
    public static By subtotal     = By.cssSelector(".modal-content .cart-content .subtotal.value");
    public static By total        = By.cssSelector(".modal-content .cart-content .product-total .value");
    public static By checkoutButton = By.cssSelector(".modal-content .cart-content-btn a.btn-primary");
}