package com.jijie.jsoup.canada;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.util.List;

public class GrabDrugsInfo {

    public static void main(String[] args) throws IOException {
        GrabDrugsInfo grabDrugsInfo = new GrabDrugsInfo();
        String initUrl = "https://www.pricepropharmacy.com/products/";
        grabDrugsInfo.collectData(initUrl);
    }

    public void collectData(String initUrl) throws IOException {
        /*System.getProperties().setProperty("webdriver.chrome.driver","C:\\chromedriver_win32\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get(initUrl);
        WebElement divElement = driver.findElement(By.cssSelector("div[class='alphasearch']"));
        List<WebElement> az = divElement.findElements(By.tagName("a"));
        az.remove(26);
        System.out.println(az.size());
        for (WebElement webElement : az) {
            System.out.println(webElement.getText());
        }*/
        Connection connect = Jsoup.connect("https://www.pricepropharmacy.com/product/abilify/");
        Document document = connect.get();
        System.out.println(document.html());
        //https://www.pricepropharmacy.com/search/?drugName=A
        //https://www.pricepropharmacy.com/product/abilify/
        //drug-package-dropdown

        //drug_items ul->



    }



}
