package com.nttdata.steps;

import com.nttdata.core.Waits;
import com.nttdata.page.CategoryPage;
import org.openqa.selenium.WebDriver;

public class CategorySteps {
    private final WebDriver driver;

    public CategorySteps(WebDriver driver) { this.driver = driver; }

    public boolean isProductListVisible() {
        return Waits.isVisible(driver, CategoryPage.productList);
    }
    public String getFirstProductName() {
        return Waits.visible(driver, CategoryPage.firstProductLink).getText();
    }

    public void openFirstProduct() {
        Waits.clickable(driver, CategoryPage.firstProductLink).click();
    }

    public String getFirstProductUrl() {
        return Waits.visible(driver, CategoryPage.firstProductLink).getAttribute("href");
    }

    public void clickFirstProduct() {
        Waits.clickable(driver, CategoryPage.firstProductLink).click();
    }
}