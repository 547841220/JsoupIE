package com.jijie.jsoup;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
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

    public int clickThree(int dosageCount, WebDriver webDriver) {
        int quantityCount = 0;
        Actions action = new Actions(webDriver);

        WebElement dosageElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_DOSAGE));
        action.moveToElement(dosageElement).click().build().perform();
        String dosageAttribute = dosageElement.getAttribute("aria-expanded");
        System.out.println("-------------------");
        System.out.println(dosageAttribute);
        if (dosageAttribute.equals("false")) {
            dosageCount = dosageCount + 1;
            return dosageCount;
        }
        List<WebElement> dosages = webDriver.findElements(By.className("option-3JfJd"));
        System.out.println(dosages.size());
        WebElement dosage = dosages.get(dosageCount);
        action.moveToElement(dosage).click().build().perform();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //嵌套第四层
        WebElement quantityElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_QUANTITY));
        action.moveToElement(quantityElement).click().build().perform();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String quantityAttribute = quantityElement.getAttribute("aria-expanded");
        if (quantityAttribute.equals("true")) {
            List<WebElement> quantitys = webDriver.findElements(By.className("option-3JfJd"));
            System.out.println(quantitys.size());
            while (quantityCount < quantitys.size()) {
                System.out.println("进入第四次嵌套");
                //quantityCount = clickFour(quantityCount, webDriver);
            }
        }

        dosageCount = dosageCount + 1;
        return dosageCount;
    }

    public int clickFour(int quantityCount, WebDriver webDriver) {
        System.out.println("终于进来第四层了，开始采集信息");
        quantityCount = quantityCount + 1;
        return quantityCount;
    }

    public int clickTwo(int formCount, WebDriver webDriver) {
        int dosageCount = 0;
        Actions action = new Actions(webDriver);

        WebElement formElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_FORM));
        action.moveToElement(formElement).click().build().perform();
        String formAttribute = formElement.getAttribute("aria-expanded");
        System.out.println("-------------------");
        System.out.println(formAttribute);
        if (formAttribute.equals("false")) {
            //嵌套第三层
            WebElement dosageElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_DOSAGE));
            action.moveToElement(dosageElement).click().build().perform();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String dosageAttribute = dosageElement.getAttribute("aria-expanded");
            if (dosageAttribute.equals("true")) {
                List<WebElement> dosages = webDriver.findElements(By.className("option-3JfJd"));
                System.out.println(dosages.size());
                while (dosageCount < dosages.size()) {
                    System.out.println("进入第三次嵌套");
                    dosageCount = clickThree(dosageCount, webDriver);
                }
            }
        } else {
            List<WebElement> forms = webDriver.findElements(By.className("option-3JfJd"));
            System.out.println(forms.size());
            WebElement brand = forms.get(formCount);
            action.moveToElement(brand).click().build().perform();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //嵌套第三层
            WebElement dosageElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_DOSAGE));
            action.moveToElement(dosageElement).click().build().perform();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String dosageAttribute = dosageElement.getAttribute("aria-expanded");
            if (dosageAttribute.equals("true")) {
                List<WebElement> dosages = webDriver.findElements(By.className("option-3JfJd"));
                System.out.println(dosages.size());
                while (dosageCount < dosages.size()) {
                    System.out.println("进入第三次嵌套");
                    //dosageCount = clickThree(dosageCount, webDriver);
                }
            }
        }

        formCount = formCount + 1;
        return formCount;
    }

    public int clickOne(int brandCount, WebDriver webDriver) {
        Actions action = new Actions(webDriver);
        int formCount = 0;

        WebElement brandElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_BRAND));
        action.moveToElement(brandElement).click().build().perform();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String brandAttribute = brandElement.getAttribute("aria-expanded");
        System.out.println("-------------------------------------------------");
        System.out.println(brandAttribute);
        if (brandAttribute.equals("false")) {

            //嵌套第二层
            WebElement formElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_FORM));
            action.moveToElement(formElement).click().build().perform();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String formAttribute = formElement.getAttribute("aria-expanded");
            if (formAttribute.equals("true")) {
                List<WebElement> forms = webDriver.findElements(By.className("option-3JfJd"));
                System.out.println(forms.size());
                while (formCount < forms.size()) {
                    formCount = clickTwo(formCount, webDriver);
                }
            }
        } else {
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
            WebElement collecFlag = webDriver.findElement(By.cssSelector("div[data-qa='discontinued_drug_ctn']"));
            System.out.println(collecFlag);
            if (collecFlag != null) {
                brandCount = brandCount + 1;
                return brandCount;
            }

            //嵌套第二层
            WebElement formElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_FORM));
            action.moveToElement(formElement).click().build().perform();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String formAttribute = formElement.getAttribute("aria-expanded");
            if (formAttribute.equals("true")) {
                List<WebElement> forms = webDriver.findElements(By.className("option-3JfJd"));
                System.out.println(forms.size());
                while (formCount < forms.size()) {
                    formCount = clickTwo(formCount, webDriver);
                }
            }
        }

        brandCount = brandCount + 1;
        return brandCount;
    }

    private List<WebElement> getElements(WebDriver webDriver, String target) {
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
        WebElement formFlag = webDriver.findElement(By.cssSelector("div[id='" + target + "']"));
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
        System.out.println("所有需要采集的url数量为：" + drugInfos.size());
        for (String drugInfo : drugInfos) {
            System.out.println("采集的数据link为：" + drugInfo);
            System.getProperties().setProperty("webdriver.ie.driver", "C:\\IEDriverServer_x64_3.14.0\\IEDriverServer.exe");
            WebDriver webDriver = new InternetExplorerDriver();
            webDriver.get(drugInfo);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //嵌套第一层
            WebElement brandElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_BRAND));
            Actions actions = new Actions(webDriver);
            actions.moveToElement(brandElement).click().build().perform();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String brandAttribute = brandElement.getAttribute("aria-expanded");
            System.out.println(brandAttribute);
            if (brandAttribute.equals("true")) {
                List<WebElement> brands = webDriver.findElements(By.className("option-3JfJd"));
                System.out.println(brands.size());
                while (brandCount < brands.size()) {
                    //进入第一次嵌套
                    System.out.println("进入第一次嵌套");
                    brandCount = clickOne(brandCount, webDriver);
                }
            }

            System.out.println("数据采集完毕！！");
        }
    }
}
