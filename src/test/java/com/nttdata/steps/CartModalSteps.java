package com.nttdata.steps;

import com.nttdata.core.Money;
import com.nttdata.core.Waits;
import com.nttdata.page.CartModalPage;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;

public class CartModalSteps {
    private final WebDriver driver;

    public CartModalSteps(WebDriver driver) { this.driver = driver; }

    public boolean isVisible() {
        return Waits.isVisible(driver, CartModalPage.modalTitle);
    }

    public String getConfirmationMessage() {
        return Waits.visible(driver, CartModalPage.modalTitle).getText();
    }

    public String getProductName() {
        return Waits.visible(driver, CartModalPage.productName).getText();
    }

    public int getQuantity() {
        String text = Waits.visible(driver, CartModalPage.productQuantity).getText();
        return Integer.parseInt(text.replaceAll("\\D", ""));
    }

    public BigDecimal getUnitPrice() {
        return Money.parse(Waits.visible(driver, CartModalPage.productPrice).getText());
    }

    public BigDecimal getSubtotal() {
        return Money.parse(Waits.visible(driver, CartModalPage.subtotal).getText());
    }

    public BigDecimal getTotal() {
        return Money.parse(Waits.visible(driver, CartModalPage.total).getText());
    }

    public void proceedToCheckout() {
        Waits.clickable(driver, CartModalPage.checkoutButton).click();
    }
}