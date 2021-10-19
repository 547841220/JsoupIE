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
import java.util.Locale;

public class GradGoods {

    public List<String> drugInfos = new ArrayList<>();
    public static final String PRE_FIX_URL = "https://www.goodrx.com/";
    public static final String UAT_ROPDOWN_BRAND = "uat-dropdown-brand";
    public static final String UAT_DROPDOWN_FORM = "uat-dropdown-form";
    public static final String UAT_DROPDOWN_DOSAGE = "uat-dropdown-dosage";
    public static final String UAT_DROPDOWN_QUANTITY = "uat-dropdown-quantity";

    public static void main(String[] args) throws IOException {
        GradGoods gradGoods = new GradGoods();
        String initUrl = "https://www.goodrx.com/drugs";
        String url2 = "https://www.goodrx.com/alprazolam";
        //1.获取有哪些页面，拼接url
        //a-z 对应1-26
        int count = 0;
        while (count < 26) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            count = gradGoods.getAllUrl(initUrl,count);
        }

        for (String drugInfo : gradGoods.drugInfos) {
            System.out.println("所有需要采集的url数量为："+gradGoods.drugInfos.size());
            System.out.println(drugInfo);
            //todo 采集具体数据 id
            gradGoods.collectDrugInfo(gradGoods.drugInfos);
        }


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
        String replace = str.replace(" / ","-")
                .replace("/","-")
                .replace("(","-")
                .replace(" ", "-").toLowerCase();

        //2.添加前缀，拼接url,并返回
        String url = stringBuilder.append(PRE_FIX_URL).append(replace).toString();
        drugInfos.add(url);
    }

    private void collectDrugInfo(List<String> drugInfos) {
        System.out.println("所有需要采集的url数量为："+drugInfos.size());
        for (String drugInfo : drugInfos) {
            System.out.println("采集的数据link为："+drugInfo);
            //
            System.getProperties().setProperty("webdriver.ie.driver","C:\\IEDriverServer_x64_3.14.0\\IEDriverServer.exe");
            WebDriver webDriver = new InternetExplorerDriver();
            webDriver.get(drugInfo);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //uat-dropdown-brand
            Actions action = new Actions(webDriver);
            WebElement brandOrGeneric = webDriver.findElement(By.id(UAT_ROPDOWN_BRAND));
            action.moveToElement(brandOrGeneric).click().build().perform();
            //获取brand or generic
            WebDriverWait wait = new WebDriverWait(webDriver, 5);
            List<WebElement> brandOrGenerics = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("option-3JfJd")));
            //第一层for循环，获取品牌或者generic
            for (WebElement brand : brandOrGenerics) {
                //点击第一个
                action.moveToElement(brand).click().build().perform();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                WebElement collecFlag = webDriver.findElement(By.cssSelector("div[data-qa='discontinued_drug_ctn']"));
                if (collecFlag != null) {
                    continue;
                }

                WebElement form = webDriver.findElement(By.id(UAT_DROPDOWN_FORM));
                action.moveToElement(form).click().build().perform();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                WebElement formFlag = webDriver.findElement(By.cssSelector("div[id='uat-dropdown-container-form']"));
                String formAttribute = formFlag.getAttribute("aria-expanded");

                if ("true".equals(formAttribute)){
                    //form_options_ctn
                    List<WebElement> forms = webDriver.findElements(By.className("option-3JfJd"));
                    //第二层for循环
                    for (WebElement webElement : forms) {
                        //点击第一个
                        action.moveToElement(webElement).click().build().perform();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        WebElement dosage = webDriver.findElement(By.id(UAT_DROPDOWN_DOSAGE));
                        action.moveToElement(dosage).click().build().perform();
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        WebElement dosageFlag = webDriver.findElement(By.cssSelector("div[id='uat-dropdown-container-dosage']"));
                        String dosageAttribute = dosageFlag.getAttribute("aria-expanded");
                        if (dosageAttribute.equals("true")) {
                            List<WebElement> dosages = webDriver.findElements(By.className("option-3JfJd"));
                            //第三层for循环
                            for (WebElement dosageElement : dosages) {
                                //点击第一个
                                action.moveToElement(webElement).click().build().perform();
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                WebElement quantity = webDriver.findElement(By.id(UAT_DROPDOWN_QUANTITY));
                                action.moveToElement(quantity).click().build().perform();
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                WebElement quantityFlag = webDriver.findElement(By.cssSelector("div[id='uat-dropdown-container-quantity']"));
                                String quantityAttribute = quantityFlag.getAttribute("aria-expanded");
                                if (quantityAttribute.equals("true")) {
                                    List<WebElement> quantitys = webDriver.findElements(By.className("option-3JfJd"));
                                    //第四层for循环
                                    for (WebElement quantityElement : quantitys) {
                                        //点击第一个
                                        action.moveToElement(webElement).click().build().perform();
                                        //采集页面数据
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
