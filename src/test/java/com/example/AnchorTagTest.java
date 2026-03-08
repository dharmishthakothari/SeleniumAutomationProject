package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AnchorTagTest {

    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Running in headless mode to not open an active browser window during testing
        // options.addArguments("--headless");
        // options.addArguments("--no-sandbox");
        // options.addArguments("--disable-dev-shm-usage");

     driver = new RemoteWebDriver(
        new URL("http://host.docker.internal:4444/wd/hub"),
        options
);
        driver = new ChromeDriver(options);
    }

    @Test
    public void testAllAnchorTags() {
        System.out.println("Navigating to https://www.guru99.com/software-testing.html");
        driver.get("https://www.guru99.com/software-testing.html");

        List<WebElement> links = driver.findElements(By.tagName("a"));
        System.out.println("Total anchor tags found: " + links.size());

        for (WebElement link : links) {
            String url = link.getAttribute("href");
            verifyLink(url);
        }
    }

    private void verifyLink(String urlString) {
        if (urlString == null || urlString.isEmpty()) {
            System.out.println("URL is either not configured for anchor tag or it is empty.");
            return;
        }

        // We skip javascript:void(0) or mailto: links
        if (!urlString.startsWith("http")) {
            System.out.println(urlString + " - Skipped (not an HTTP/HTTPS link)");
            return;
        }

        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnect = (HttpURLConnection) url.openConnection();
            httpURLConnect.setConnectTimeout(5000);
            httpURLConnect.connect();

            int responseCode = httpURLConnect.getResponseCode();
            if (responseCode >= 400) {
                System.out.println(urlString + " - " + httpURLConnect.getResponseMessage() + " [BROKEN LINK]");
            } else {
                System.out.println(urlString + " - " + httpURLConnect.getResponseMessage() + " [OK]");
            }
        } catch (Exception e) {
            System.out.println(urlString + " - " + "Exception occurred: " + e.getMessage() + " [BROKEN LINK]");
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
