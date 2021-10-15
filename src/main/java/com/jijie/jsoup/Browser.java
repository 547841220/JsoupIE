package com.jijie.jsoup;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

public enum Browser  {

    IE {
        public WebDriver instance() {
            System.getProperties().setProperty("webdriver.ie.driver","C:\\IEDriverServer_x64_3.14.0\\IEDriverServer.exe");
            return new InternetExplorerDriver();
        }
    };
}