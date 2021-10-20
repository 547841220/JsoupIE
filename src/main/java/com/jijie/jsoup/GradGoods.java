package com.jijie.jsoup;

import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class GradGoods {

    public List<String> drugInfos = new ArrayList<>();
    public static final String PRE_FIX_URL = "https://www.goodrx.com/";

    public static void main(String[] args) {
        GradGoods gradGoods = new GradGoods();
        CollectData collect = new CollectData();
        String initUrl = "https://www.goodrx.com/drugs";
        gradGoods.getAllUrl(initUrl);

        for (String drugInfo : gradGoods.drugInfos) {
            System.out.println("所有需要采集的url数量为："+gradGoods.drugInfos.size());
            System.out.println(drugInfo);
            //todo 采集具体数据 id
            //gradGoods.collectDrugInfo(gradGoods.drugInfos);
            collect.collecData(gradGoods.drugInfos);
        }


    }

    public void getAllUrl(String initUrl) {
        System.getProperties().setProperty("webdriver.ie.driver","C:\\IEDriverServer_x64_3.14.0\\IEDriverServer.exe");
        WebDriver webDriver = new InternetExplorerDriver();
        webDriver.get(initUrl);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        webDriver.manage().window().maximize();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int i = 0; i < 26; i++) {
            if (i > 0) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //---------------------------------------------------------
            List<WebElement> azs = webDriver.findElements(By.className("letterLink-3cdW1"));
            WebElement element = azs.get(i);
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(false);",element);
            Actions clickElement = new Actions(webDriver);
            clickElement.moveToElement(element).click().build().perform();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            WebDriverWait wait = new WebDriverWait(webDriver, 5);
            List<WebElement> as = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("linkContainer-gJwsn")));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(i+"分类下，药品数量："+as.size());
            //采集i页面有多少药品链接
            for (WebElement drug : as) {
                try{
                    WebElement span = drug.findElement(By.tagName("span"));
                    String text = span.getText();
                    System.out.println(text);
                    //对字符串做处理
                    handleDrug(text);

                }catch (StaleElementReferenceException e){
                    webDriver.quit();
                }
            }
            webDriver.navigate().back();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getAllUrl(String initUrl,int i) {
        //System.getProperties().setProperty("webdriver.ie.driver","C:\\IEDriverServer_x64_3.14.0\\IEDriverServer.exe");
        WebDriver webDriver = new InternetExplorerDriver();
        webDriver.get(initUrl);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        webDriver.manage().window().maximize();

        List<WebElement> azs = webDriver.findElements(By.className("letterLink-3cdW1"));
        System.out.println("首字母分类a-z"+azs.size());
        //---------------------------------------------------------
        WebElement element = azs.get(i);
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(false);",element);
        Actions clickElement = new Actions(webDriver);
        clickElement.moveToElement(element).click().build().perform();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebDriverWait wait = new WebDriverWait(webDriver, 5);
        List<WebElement> as = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("linkContainer-gJwsn")));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(i+"分类下，药品数量："+as.size());
        //采集i页面有多少药品链接
        for (WebElement drug : as) {
            try{
                WebElement span = drug.findElement(By.tagName("span"));
                String text = span.getText();
                System.out.println(text);
                //对字符串做处理
                handleDrug(text);
            }catch (StaleElementReferenceException e){
                webDriver.quit();
                int count = i;
                return count;
            }

        }

        webDriver.quit();
        int count = i+1;
        return count;
    }

    private void handleDrug(String str){
        StringBuilder stringBuilder = new StringBuilder();
        //1.用“-”代替空格,并转小写
        String replace = str.replace(" / ","-")
                .replace("/","-")
                .replace("(","-")
                .replace(" ", "-").toLowerCase();

        //2.添加前缀，拼接url,并返回
        String url = stringBuilder.append(PRE_FIX_URL).append(replace).toString();
        drugInfos.add(url);
    }

}
