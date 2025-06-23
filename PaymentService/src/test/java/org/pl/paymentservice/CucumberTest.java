package org.pl.paymentservice;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "org.pl.paymentservice.steps",
        plugin = {"pretty", "html:target/cucumber-reports.html"}
)
public class CucumberTest {
}
