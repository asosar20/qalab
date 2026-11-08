package com.nttdata.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "com.nttdata",
        tags = "@store",
        plugin = {"pretty",
                "html:target/cucumber/cucumber-report.html",
                "json:target/cucumber/cucumber.json",
                "com.nttdata.report.PdfReportPlugin"}
)
public class RunnerTest {
}