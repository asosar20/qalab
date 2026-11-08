package com.nttdata.page;

import org.openqa.selenium.By;

public class CategoryPage {
    public static By productList = By.id("js-product-list");
    public static By firstProductLink =
            By.cssSelector("#js-product-list article.product-miniature .product-title a");
}