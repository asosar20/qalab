package com.nttdata.core;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class Waits {
    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    public static WebElement visible(WebDriver driver, By by) {
        return new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public static WebElement clickable(WebDriver driver, By by) {
        return new WebDriverWait(driver, TIMEOUT)
                .until(ExpectedConditions.elementToBeClickable(by));
    }

    public static boolean isVisible(WebDriver driver, By by) {
        try {
            visible(driver, by);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}