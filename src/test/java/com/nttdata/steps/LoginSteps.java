package com.nttdata.steps;

import com.nttdata.core.Waits;
import com.nttdata.page.LoginPage;
import org.openqa.selenium.WebDriver;

public class LoginSteps {
    private final WebDriver driver;

    public LoginSteps(WebDriver driver) { this.driver = driver; }

    public void login(String email, String password) {
        Waits.clickable(driver, LoginPage.signInLink).click();
        Waits.visible(driver, LoginPage.emailInput).sendKeys(email);
        Waits.visible(driver, LoginPage.passInput).sendKeys(password);
        Waits.clickable(driver, LoginPage.submitButton).click();
    }

    public boolean isLoggedIn() {
        return Waits.isVisible(driver, LoginPage.logoutLink);
    }

    public String getLoginError() {
        return driver.findElements(LoginPage.loginError).stream()
                .findFirst()
                .map(e -> e.getText())
                .orElse("sin mensaje de error visible");
    }
}