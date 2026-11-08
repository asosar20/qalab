package com.nttdata.steps;

import com.nttdata.core.Waits;
import com.nttdata.page.ProductPage;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ProductSteps {
    private final WebDriver driver;

    public ProductSteps(WebDriver driver) { this.driver = driver; }

    public void setQuantity(int quantity) {
        WebElement input = Waits.visible(driver, ProductPage.quantityInput);
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), String.valueOf(quantity));
    }

    public void addToCart() {
        Waits.clickable(driver, ProductPage.addToCartButton).click();
    }

    public void open(String url) {
        driver.get(url);
    }
}