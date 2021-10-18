package com.jijie.jsoup;

import org.checkerframework.checker.units.qual.A;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectData {

    public static final String PRE_FIX_URL = "https://www.goodrx.com/";
    public static final String UAT_DROPDOWN_CONTAINER_BRAND = "uat-dropdown-container-brand";

    public static final String UAT_DROPDOWN_CONTAINER_FORM = "uat-dropdown-container-form";
    public static final String UAT_DROPDOWN_CONTAINER_DOSAGE = "uat-dropdown-container-dosage";
    public static final String UAT_DROPDOWN_CONTAINER_QUANTITY = "uat-dropdown-container-quantity";

    public static void main(String[] args) {
        CollectData co = new CollectData();
        co.collecData();
    }

    private boolean checkBrand(WebDriver webDriver,int brandCount) {
        Actions action = new Actions(webDriver);
        List<WebElement> brands = getElements(webDriver, UAT_DROPDOWN_CONTAINER_BRAND);
        if (brands.size() > 0) {
            List<WebElement> elements = webDriver.findElements(By.className("option-3JfJd"));
            WebElement webElement = elements.get(brandCount);
            action.moveToElement(webElement).click().build().perform();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try{
                //第一步独有判断
                WebElement collecFlag = webDriver.findElement(By.cssSelector("div[data-qa='discontinued_drug_ctn']"));
                System.out.println(collecFlag);
                if (collecFlag != null) {
                    System.out.println("collectFlag不为空");
                    return false;
                }
            }catch (NoSuchElementException e){
                System.out.println("数据可以正常采集！！");
            }
        }
        return true;
    }

    public int clickThree(int dosageCount,WebDriver webDriver) {
        int quantityCount = 0;
        Actions action = new Actions(webDriver);

        List<WebElement> dosages = getElements(webDriver, UAT_DROPDOWN_CONTAINER_DOSAGE);
        if (dosages.size() > 0) {
            WebElement dosage = dosages.get(dosageCount);
            //具体点击哪一个form
            action.moveToElement(dosage).click().build().perform();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //嵌套第四层
            List<WebElement> quantitys= getElements(webDriver, UAT_DROPDOWN_CONTAINER_QUANTITY);
            if (quantitys.size() > 0) {
                while(quantityCount < quantitys.size()) {
                    quantityCount = clickFour(quantityCount,webDriver);
                }
            }
        }
        dosageCount = dosageCount + 1;
        return dosageCount;
    }

    public int clickFour(int quantityCount,WebDriver webDriver){
        System.out.println("终于进来第四层了，开始采集信息");
        quantityCount = quantityCount + 1;
        return quantityCount;
    }

    public int clickTwo(int formCount,WebDriver webDriver){
        int dosageCount = 0;
        Actions action = new Actions(webDriver);

        List<WebElement> forms = getElements(webDriver, UAT_DROPDOWN_CONTAINER_FORM);
        if (forms.size() > 0) {
            commonClick(webDriver,UAT_DROPDOWN_CONTAINER_FORM);
            WebElement form = forms.get(formCount);
            //具体点击哪一个form
            action.moveToElement(form).click().build().perform();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //嵌套第三层
            List<WebElement> dosages = getElements(webDriver, UAT_DROPDOWN_CONTAINER_DOSAGE);
            if (dosages.size() > 0) {
                while(formCount < forms.size()) {
                    dosageCount = clickThree(dosageCount,webDriver);
                }
            }
        }

        dosageCount = dosageCount + 1;
        return dosageCount;
    }

    private void commonClick(WebDriver webDriver,String target) {
        Actions action = new Actions(webDriver);
        WebElement element = webDriver.findElement(By.id(target));
        action.moveToElement(element).click().build().perform();
    }

    public int clickOne(int brandCount,WebDriver webDriver) {
        Actions action = new Actions(webDriver);
        int formCount = 0;

        WebElement brandElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_BRAND));
        action.moveToElement(brandElement).click().build().perform();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String attribute = brandElement.getAttribute("aria-expanded");
        System.out.println("-------------------------------------------------");
        System.out.println(attribute);
        if (attribute.equals("false")) {
            brandCount = brandCount + 1;
            return brandCount;
        }
        List<WebElement> brands = webDriver.findElements(By.className("option-3JfJd"));
        System.out.println(brands.size());
        WebElement brand = brands.get(brandCount);
        action.moveToElement(brand).click().build().perform();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //嵌套第二层
        WebElement formElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_FORM));
        action.moveToElement(formElement).click().build().perform();
        String attribute1 = formElement.getAttribute("aria-expanded");
        if (attribute1.equals("true")) {
            List<WebElement> forms = webDriver.findElements(By.className("option-3JfJd"));
            System.out.println(forms.size());
            while(formCount < forms.size()) {
                formCount = clickTwo(formCount,webDriver);
            }
        }

        /*boolean collectFlag = checkBrand(webDriver, brandCount);
        System.out.println(collectFlag);
        if (!collectFlag) {
            brandCount = brandCount + 1;
            return brandCount;
        }*/
        //选择brand
        /*WebElement brand = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_BRAND));
        action.moveToElement(brand).click().build().perform();
        WebElement brandFlag = webDriver.findElement(By.cssSelector("div[id='"+UAT_DROPDOWN_CONTAINER_BRAND+"']"));
        String brandAttribute = brandFlag.getAttribute("aria-expanded");
        if (brandAttribute.equals("true")) {
            List<WebElement> brands = webDriver.findElements(By.className("option-3JfJd"));
            System.out.println(brands.size());
        }*/

        /*List<WebElement> brands = getElements(webDriver, UAT_DROPDOWN_CONTAINER_BRAND);
        if (brands.size() > 0) {
            commonClick(webDriver,UAT_DROPDOWN_CONTAINER_BRAND);
            WebElement brand = brands.get(brandCount);
            action.moveToElement(brand).build().perform();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //嵌套第二层
            List<WebElement> forms = getElements(webDriver, UAT_DROPDOWN_CONTAINER_FORM);
            if (forms.size() > 0) {
                while(formCount < forms.size()) {
                    formCount = clickTwo(formCount,webDriver);
                }
            }
        }*/

        brandCount = brandCount + 1;
        return brandCount;
    }

    private List<WebElement> getElements(WebDriver webDriver,String target) {
        List<WebElement> elements = new ArrayList<>();
        Actions action = new Actions(webDriver);
        WebElement form = webDriver.findElement(By.id(target));
        action.moveToElement(form).click().build().perform();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        WebElement formFlag = webDriver.findElement(By.cssSelector("div[id='"+target+"']"));
        String formAttribute = formFlag.getAttribute("aria-expanded");
        if (formAttribute.equals("true")) {
            elements = webDriver.findElements(By.className("option-3JfJd"));
        }

        return elements;
    }

    public void collecData() {
        int brandCount = 0;
        List<String> drugInfos = new ArrayList<>();
        drugInfos.add("https://www.goodrx.com/alprazolam");
        System.out.println("所有需要采集的url数量为："+drugInfos.size());
        for (String drugInfo : drugInfos) {
            System.out.println("采集的数据link为："+drugInfo);
            System.getProperties().setProperty("webdriver.ie.driver","C:\\IEDriverServer_x64_3.14.0\\IEDriverServer.exe");
            WebDriver webDriver = new InternetExplorerDriver();
            webDriver.get(drugInfo);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            List<WebElement> brands = getElements(webDriver, UAT_DROPDOWN_CONTAINER_BRAND);
            if (brands.size() > 0) {
                while(brandCount < brands.size()) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    brandCount = clickOne(brandCount,webDriver);
                }
            }
            System.out.println("数据采集完毕！！");
        }
    }
}
