package com.jijie.jsoup;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GradGoods {

    public List<String> drugInfos = new ArrayList<>();
    public static final String PRE_FIX_URL = "https://www.goodrx.com/";

    public static void main(String[] args) throws IOException {
        GradGoods gradGoods = new GradGoods();
        String initUrl = "https://www.goodrx.com/drugs";
        String url2 = "https://www.goodrx.com/alprazolam";
        //1.获取有哪些页面，拼接url
        //a-z 对应1-26
        int count = 0;
        while (count < 26) {
            count = gradGoods.getAllUrl(initUrl,count);
        }

        for (String drugInfo : gradGoods.drugInfos) {
            System.out.println("所有需要采集的url数量为："+gradGoods.drugInfos.size());
            System.out.println(drugInfo);
        }
        //getContent(initUrl);
    }

    public int getAllUrl(String initUrl,int i) {
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
        System.out.println(i+"分类下，药品数量："+as.size());
        //采集i页面有多少药品链接
        for (WebElement drug : as) {
            WebElement span = drug.findElement(By.tagName("span"));
            String text = span.getText();
            System.out.println(text);
            //对字符串做处理
            handleDrug(text);
        }

        webDriver.quit();
        int count = i+1;
        return count;
    }

    private void handleDrug(String str){
        StringBuilder stringBuilder = new StringBuilder();
        //1.用“-”代替空格,并转小写
        String replace = str.replace(" ", "-").replace(" / ","-");
        //2.添加前缀，拼接url,并返回
        String url = stringBuilder.append(PRE_FIX_URL).append(replace).toString();
        drugInfos.add(url);
    }

    public static void getContent(String initUrl) {
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
        //letterLink-3cdW1 --class
        List<WebElement> azs = webDriver.findElements(By.className("letterLink-3cdW1"));
        System.out.println("首字母分类a-z"+azs.size());
        for (WebElement element : azs) {
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
            System.out.println("a分类下，药品数量："+as.size());

            for (int i = 0; i < as.size(); i++) {
                WebElement drug = as.get(i);
                WebElement span = drug.findElement(By.tagName("span"));
                System.out.println(span.getText());
                //System.out.println(drug.getText());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(false);",drug);
                Actions clickDrug = new Actions(webDriver);
                clickDrug.moveToElement(drug).click(drug).build().perform();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Document drugInfo = Jsoup.parse(webDriver.getPageSource());
                System.out.println("第"+i+"遍打印--------------------------------------");
                System.out.println(drugInfo);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                webDriver.navigate().back();
            }
            System.out.println("打印完毕！");

            /*WebDriverWait wait2 = new WebDriverWait(webDriver, 5);
            List<WebElement> as2 = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("linkContainer-gJwsn")));
            if (as2 !=null && as2.size() >0) {
                webDriver.navigate().back();
            }*/
            //link-1SEOS linkDesktop-13xNI --class

            //返回上一界面
            //webDriver.navigate().back();

        }
    }
}
