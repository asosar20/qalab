package com.nttdata.steps;

import com.nttdata.core.Waits;
import com.nttdata.page.HomePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class HomeSteps {
    private final WebDriver driver;

    public HomeSteps(WebDriver driver) { this.driver = driver; }

    public void openStore() {
        driver.get("https://qalab.bensg.com/store/pe/");
    }

    public boolean categoryExists(String name) {
        return Waits.isVisible(driver, HomePage.category(name));
    }

    public void hoverCategory(String name) {
        WebElement category = Waits.visible(driver, HomePage.category(name));
        new Actions(driver).moveToElement(category).perform();
    }

    public boolean subcategoryExists(String name) {
        return Waits.isVisible(driver, HomePage.subcategory(name));
    }

    public void openSubcategory(String name) {
        Waits.clickable(driver, HomePage.subcategory(name)).click();
    }
}