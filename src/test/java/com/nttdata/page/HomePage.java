package com.nttdata.page;

import org.openqa.selenium.By;

public class HomePage {
    public static By category(String name) {
        return By.xpath("//ul[@id='top-menu']//a[@data-depth='0' and contains(normalize-space(.),'" + name + "')]");
    }

    public static By subcategory(String name) {
        return By.xpath("//ul[@id='top-menu']//a[@data-depth='1' and normalize-space(.)='" + name + "']");
    }
}