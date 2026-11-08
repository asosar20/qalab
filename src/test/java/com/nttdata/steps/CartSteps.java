package com.nttdata.steps;

import com.nttdata.core.Money;
import com.nttdata.core.Waits;
import com.nttdata.page.CartPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.time.Duration;

public class CartSteps {
    private final WebDriver driver;

    public CartSteps(WebDriver driver) { this.driver = driver; }

    public void emptyCart() {
        driver.get("https://qalab.bensg.com/store/pe/carrito?action=show");
        while (true) {
            int before = driver.findElements(CartPage.removeButtons).size();
            if (before == 0) break;
            Waits.clickable(driver, CartPage.removeButtons).click();
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(d -> d.findElements(CartPage.removeButtons).size() < before);
        }
        driver.navigate().back();
    }

    public String getTitle() {
        return Waits.visible(driver, CartPage.title).getText();
    }

    public BigDecimal getUnitPrice() {
        return Money.parse(Waits.visible(driver, CartPage.unitPrice).getText());
    }

    public int getQuantity() {
        return Integer.parseInt(
                Waits.visible(driver, CartPage.quantityInput).getDomProperty("value"));
    }

    public BigDecimal getLineTotal() {
        return Money.parse(Waits.visible(driver, CartPage.lineTotal).getText());
    }

    public BigDecimal getSubtotal() {
        return Money.parse(Waits.visible(driver, CartPage.subtotal).getText());
    }

    public BigDecimal getTotal() {
        return Money.parse(Waits.visible(driver, CartPage.total).getText());
    }
}