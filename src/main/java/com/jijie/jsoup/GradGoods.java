package com.jijie.jsoup;

import com.jijie.jsoup.util.JJUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class GradGoods {

    public List<String> drugInfos = new ArrayList<>();
    public static final String PRE_FIX_URL = "https://www.goodrx.com/";

    public static void main(String[] args) {
        GradGoods gradGoods = new GradGoods();
        CollectData collect = new CollectData();
        String initUrl = "https://www.goodrx.com/drugs";
        boolean grabStepOneSuccessFlag = false;
        while (!grabStepOneSuccessFlag) {
            grabStepOneSuccessFlag = gradGoods.getAllUrl(initUrl);
        }
        System.out.println("所有需要采集的url数量为："+gradGoods.drugInfos.size());
        for (String drugInfo : gradGoods.drugInfos) {
            System.out.println(drugInfo);
        }
        //采集数据
        boolean grabStepTwoSuccessFlag = false;
        while (!grabStepTwoSuccessFlag) {
            JJUtil.threadSleep(10);
            grabStepTwoSuccessFlag = collect.collecData(gradGoods.drugInfos);
        }


    }

    public boolean getAllUrl(String initUrl) {
        System.getProperties().setProperty("webdriver.ie.driver","C:\\IEDriverServer_x64_3.14.0\\IEDriverServer.exe");
        WebDriver webDriver = new InternetExplorerDriver();

        try{

            webDriver.get(initUrl);
            //JJUtil.threadSleep(10);
            webDriver.manage().window().maximize();
            JJUtil.threadSleep(3);

            for (int i = 0; i < 2; i++) {
                //加载azs最大等待时间30秒，超时则重试！
                WebDriverWait waitAZS = new WebDriverWait(webDriver, Duration.ofSeconds(30));
                List<WebElement> azs = waitAZS.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("letterLink-3cdW1")));
                WebElement element = azs.get(i);
                ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(false);",element);
                Actions clickElement = new Actions(webDriver);
                clickElement.moveToElement(element).click().build().perform();
                JJUtil.threadSleep(5);

                //加载i页面最大等待时长60秒，超时重试！
                WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(60));
                List<WebElement> as = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("linkContainer-gJwsn")));
                JJUtil.threadSleep(3);
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
                        drugInfos.clear();
                        webDriver.quit();
                        return false;
                    }
                }
                webDriver.navigate().back();
                JJUtil.threadSleep(3);
            }
            webDriver.quit();
            return true;
        }catch (Exception e) {
            System.out.println(e);
            drugInfos.clear();
            webDriver.quit();
            return false;
        }

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
