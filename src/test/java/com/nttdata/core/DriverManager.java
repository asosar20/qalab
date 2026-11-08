package com.nttdata.core;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class DriverManager {
    private static WebDriver driver;
    private static Scenario scenario;

    @Before(order = 0)
    public void setUp(Scenario sc) {
        scenario = sc;
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @After
    public void quitDriver(Scenario sc) {
        if (sc.isFailed()) screenShot();
        driver.quit();
    }

    public static WebDriver getDriver() { return driver; }

    public static void screenShot() {
        byte[] evidencia = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        scenario.attach(evidencia, "image/png", "evidencias");
    }
}