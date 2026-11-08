package com.nttdata.page;

import org.openqa.selenium.By;

public class LoginPage {
    public static By signInLink   = By.cssSelector("#_desktop_user_info a");
    public static By emailInput   = By.id("field-email");
    public static By passInput    = By.id("field-password");
    public static By submitButton = By.id("submit-login");
    public static By logoutLink   = By.cssSelector("a.logout");
    public static By loginError   = By.cssSelector(".alert-danger");
}