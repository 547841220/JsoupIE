package com.jijie.jsoup;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author oliver
 */
public class NmpaGrabberUtil {

    private static final String TURN_PAGE = "第\\d页/共\\d+页\\s+共\\d+条";
    private static Pattern turnPagePattern = Pattern.compile(TURN_PAGE);
    private static final String INDEX_URL = "http://app1.nmpa.gov.cn/data_nmpa/face3/dir.html?type=ylqx";
    private static final boolean useProxy = true;




    public static void sleep(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void closeAndQuitWebdriver(WebDriver webDriver) {
        if (webDriver != null) {
            /*try {
                webDriver.close();
            } catch (Exception e) {

            }*/
            try {
                webDriver.quit();
            } catch (Exception e) {

            }
        }
    }

    public static void waitUntil(WebDriver webDriver, By by, Integer timeOutSeconds) {
        WebDriverWait wait = new WebDriverWait(webDriver, timeOutSeconds);
        wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }
}