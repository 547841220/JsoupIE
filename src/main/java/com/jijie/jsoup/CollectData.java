package com.jijie.jsoup;

import com.jijie.jsoup.entity.DrugInfo;
import com.jijie.jsoup.util.JJUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CollectData {


    public Connection connection ;
    public String PRODUCT_NAME_RAW;
    public List<DrugInfo> drugInfos = new ArrayList<>();
    public DrugInfo tempDrugInfo = new DrugInfo();

    public static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String JDBC_URL = "jdbc:mysql://localhost:3306/gbi";
    public static final String JDBC_USER_NAME = "root";
    public static final String JDBC_PASSWORD = "jj789632145";

    public static final String OPTION_3JFJD = "option-3JfJd";
    public static final String ARIA_EXPANDED = "aria-expanded";
    public static final String UAT_DROPDOWN_CONTAINER_BRAND = "uat-dropdown-container-brand";

    public static final String UAT_DROPDOWN_CONTAINER_FORM = "uat-dropdown-container-form";
    public static final String UAT_DROPDOWN_CONTAINER_DOSAGE = "uat-dropdown-container-dosage";
    public static final String UAT_DROPDOWN_CONTAINER_QUANTITY = "uat-dropdown-container-quantity";
    public static final String DRUG_INFO_BTN = "drug_info_btn";

    public boolean collecData(List<String> drugInfos) {
        int currentCount = 0;

        System.out.println("所有需要采集的url数量为：" + drugInfos.size());
        for (String drugInfoUrl : drugInfos) {
            try{
                System.out.println("采集的数据link为：" + drugInfoUrl);
                System.getProperties().setProperty("webdriver.ie.driver", "C:\\IEDriverServer_x64_3.14.0\\IEDriverServer.exe");
                WebDriver webDriver = new InternetExplorerDriver();
                webDriver.get(drugInfoUrl);
                JJUtil.threadSleep(10);
                webDriver.manage().window().maximize();
                JJUtil.threadSleep(3);
                //药品详情
                Actions clickDrugInfo = new Actions(webDriver);
                WebElement drugInfoElement = webDriver.findElement(By.cssSelector("li[data-qa='" + DRUG_INFO_BTN + "']"));
                clickDrugInfo.moveToElement(drugInfoElement).click().build().perform();
                JJUtil.threadSleep(10);
                Document drugInfoDoc = Jsoup.parse(webDriver.getPageSource());
                if (drugInfoDoc == null) {
                    System.out.println("药品信息数据采集过程中出错，重新采集");
//                    dbSource.drop(GRAB_GOOD_RX_INFO);
                    return false;
                }
                collectDrugInfoDoc(drugInfoDoc);
                webDriver.navigate().back();
                JJUtil.threadSleep(5);
                //嵌套第一层
                WebElement brandElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_BRAND));
                Actions action = new Actions(webDriver);
                action.moveToElement(brandElement).click().build().perform();
                JJUtil.threadSleep(2);
                String brandAttribute = brandElement.getAttribute(ARIA_EXPANDED);
                System.out.println(brandAttribute);

                if (brandAttribute.equals("false")) {
                    //直接点击form
                    WebElement formElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_FORM));
                    Actions clickForm = new Actions(webDriver);
                    clickForm.moveToElement(formElement).click().build().perform();
                    JJUtil.threadSleep(2);
                    String formAttribute = formElement.getAttribute(ARIA_EXPANDED);
                    if (formAttribute.equals("false")) {
                        //直接点击dosage
                        WebElement dosageElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_DOSAGE));
                        Actions clickDosage = new Actions(webDriver);
                        clickDosage.moveToElement(dosageElement).click().build().perform();
                        JJUtil.threadSleep(2);
                        String dosageAttribute = dosageElement.getAttribute(ARIA_EXPANDED);
                        if (dosageAttribute.equals("false")){
                            //直接点击最后一个quantity
                            WebElement quantityElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_QUANTITY));
                            Actions clickQuantity = new Actions(webDriver);
                            clickQuantity.moveToElement(quantityElement).click().build().perform();
                            JJUtil.threadSleep(2);
                            String quantityAttribute = quantityElement.getAttribute(ARIA_EXPANDED);
                            if (quantityAttribute.equals("false")) {
                                //采集数据开始
                                System.out.println("四个都不可点击，采集数据开始！！！！----------------------");
                                String detailUrl = webDriver.getCurrentUrl();
                                Document priceDoc = Jsoup.parse(webDriver.getPageSource());

                                //采集数据
                                collecDrugInfo(currentCount,priceDoc,detailUrl);

                                //采集完毕！
                                System.out.println("四个都不可点击，采集完毕！！");
                            }else {
                                List<WebElement> quantitys = webDriver.findElements(By.className(OPTION_3JFJD));
                                System.out.println(quantitys.size());
                                while (currentCount < quantitys.size()) {
                                    JJUtil.threadSleep(3);
                                    //点击quantity
                                    currentCount = clickFour(currentCount, webDriver);
                                }
                            }
                        }else {
                            List<WebElement> dosages = webDriver.findElements(By.className(OPTION_3JFJD));
                            System.out.println(dosages.size());
                            while (currentCount < dosages.size()) {
                                JJUtil.threadSleep(3);
                                //点击dosage
                                System.out.println("点击dosage");
                                currentCount = clickThree(currentCount, webDriver);
                            }
                        }
                    }else {
                        List<WebElement> forms = webDriver.findElements(By.className(OPTION_3JFJD));
                        System.out.println(forms.size());
                        while (currentCount < forms.size()) {
                            JJUtil.threadSleep(3);
                            //点击form
                            System.out.println("点击form");
                            currentCount = clickTwo(currentCount, webDriver);
                        }
                    }
                }else {
                    List<WebElement> brands = webDriver.findElements(By.className(OPTION_3JFJD));
                    System.out.println(brands.size());
                    while (currentCount < brands.size()) {
                        JJUtil.threadSleep(3);
                        //点击brand
                        System.out.println("点击brand");
                        currentCount = clickOne(currentCount, webDriver);
                    }
                }

                webDriver.quit();
                System.out.println(drugInfoUrl+"数据采集完毕！！");
            }catch (Exception e) {
                System.out.println("数据采集过程中出错，重新采集");
                dropData();
                //dbSource.drop(GRAB_GOOD_RX_INFO);
                return false;
            }

        }
        System.out.println("全部数据采集完毕！！");
        return true;
    }



    public int clickOne(int currentCount, WebDriver webDriver) {
        Actions action = new Actions(webDriver);
        int nextCount = 0;

        //true
        if (currentCount > 0) {
            WebElement brandElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_BRAND));
            action.moveToElement(brandElement).click().perform();
            JJUtil.threadSleep(2);
        }
        List<WebElement> brands = webDriver.findElements(By.className("option-3JfJd"));
        System.out.println(brands.size());
        WebElement brand = brands.get(currentCount);
        action.moveToElement(brand).click().build().perform();
        JJUtil.threadSleep(3);
        try{
            WebElement collecFlag = webDriver.findElement(By.cssSelector("div[data-qa='discontinued_drug_ctn']"));
            System.out.println(collecFlag);
            if (collecFlag != null) {
                currentCount = currentCount + 1;
                return currentCount;
            }
        }catch (NoSuchElementException e){
            System.out.println("可以采集！采集第"+currentCount+"个");
        }

        //点击form
        WebElement formElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_FORM));
        action.moveToElement(formElement).click().build().perform();
        JJUtil.threadSleep(2);
        String formAttribute = formElement.getAttribute(ARIA_EXPANDED);
        System.out.println(formAttribute);

        if (formAttribute.equals("false")) {
            //直接点击dosage
            WebElement dosageElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_DOSAGE));
            Actions clickDosage = new Actions(webDriver);
            clickDosage.moveToElement(dosageElement).click().build().perform();
            JJUtil.threadSleep(2);
            String dosageAttribute = dosageElement.getAttribute(ARIA_EXPANDED);
            if (dosageAttribute.equals("false")){
                //直接点击最后一个quantity
                WebElement quantityElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_QUANTITY));
                Actions clickQuantity = new Actions(webDriver);
                clickQuantity.moveToElement(quantityElement).click().build().perform();
                JJUtil.threadSleep(2);
                String quantityAttribute = quantityElement.getAttribute(ARIA_EXPANDED);
                if (quantityAttribute.equals("false")) {
                    //采集数据开始
                    System.out.println("采集数据开始！！！！----------------------");
                    String detailUrl = webDriver.getCurrentUrl();
                    Document priceDoc = Jsoup.parse(webDriver.getPageSource());

                    //采集数据
                    collecDrugInfo(currentCount,priceDoc,detailUrl);

                    //采集完毕！
                    System.out.println("采集完毕！！！");
                }else {
                    List<WebElement> quantitys = webDriver.findElements(By.className(OPTION_3JFJD));
                    System.out.println(quantitys.size());
                    while (nextCount < quantitys.size()) {
                        JJUtil.threadSleep(3);
                        //点击quantity
                        System.out.println("点击quantity");
                        nextCount = clickFour(nextCount, webDriver);
                    }
                }
            }else {
                List<WebElement> dosages = webDriver.findElements(By.className(OPTION_3JFJD));
                System.out.println(dosages.size());
                while (nextCount < dosages.size()) {
                    JJUtil.threadSleep(3);
                    //点击dosage
                    System.out.println("点击dosage");
                    nextCount = clickThree(nextCount, webDriver);
                }
            }

        }else {
            List<WebElement> forms = webDriver.findElements(By.className(OPTION_3JFJD));
            System.out.println(forms.size());
            while (nextCount < forms.size()) {
                JJUtil.threadSleep(3);
                //点击form
                System.out.println("点击form");
                nextCount = clickTwo(nextCount, webDriver);
            }
        }

        currentCount = currentCount + 1;
        return currentCount;
    }

    public int clickTwo(int currentCount, WebDriver webDriver) {
        int nextCount = 0;
        Actions action = new Actions(webDriver);

        if (currentCount > 0) {
            WebElement formElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_FORM));
            action.moveToElement(formElement).click().build().perform();
            JJUtil.threadSleep(2);
        }
        //真正点击form
        List<WebElement> forms = webDriver.findElements(By.className("option-3JfJd"));
        System.out.println(forms.size());
        WebElement form = forms.get(currentCount);
        action.moveToElement(form).click().build().perform();
        JJUtil.threadSleep(3);

        //点击dosage
        WebElement dosageElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_DOSAGE));
        Actions clickDosage = new Actions(webDriver);
        clickDosage.moveToElement(dosageElement).click().build().perform();
        JJUtil.threadSleep(2);
        String dosageAttribute = dosageElement.getAttribute(ARIA_EXPANDED);
        if (dosageAttribute.equals("false")){
            //直接点击最后一个quantity
            WebElement quantityElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_QUANTITY));
            Actions clickQuantity = new Actions(webDriver);
            clickQuantity.moveToElement(quantityElement).click().build().perform();
            JJUtil.threadSleep(2);
            String quantityAttribute = quantityElement.getAttribute(ARIA_EXPANDED);
            if (quantityAttribute.equals("false")) {
                //采集数据开始
                System.out.println("采集数据开始！！！！----------------------");
                String detailUrl = webDriver.getCurrentUrl();
                Document priceDoc = Jsoup.parse(webDriver.getPageSource());

                //采集数据
                collecDrugInfo(currentCount,priceDoc,detailUrl);

                //采集完毕！
                System.out.println("采集完毕！！！");
            }else {
                List<WebElement> quantitys = webDriver.findElements(By.className(OPTION_3JFJD));
                System.out.println(quantitys.size());
                while (nextCount < quantitys.size()) {
                    JJUtil.threadSleep(3);
                    //点击quantity
                    System.out.println("点击quantity");
                    nextCount = clickFour(nextCount, webDriver);
                }
            }
        }else {
            List<WebElement> dosages = webDriver.findElements(By.className(OPTION_3JFJD));
            System.out.println(dosages.size());
            while (nextCount < dosages.size()) {
                JJUtil.threadSleep(3);
                //点击dosage
                System.out.println("点击dosage");
                nextCount = clickThree(nextCount, webDriver);
            }
        }

        currentCount = currentCount + 1;
        return currentCount;
    }

    public int clickThree(int currentCount, WebDriver webDriver) {
        int nextCount = 0;
        Actions action = new Actions(webDriver);

        if (currentCount > 0) {
            WebElement dosageElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_DOSAGE));
            action.moveToElement(dosageElement).click().build().perform();
            JJUtil.threadSleep(2);
        }
        //真正点击dosage
        List<WebElement> dosages = webDriver.findElements(By.className(OPTION_3JFJD));
        System.out.println(dosages.size());
        WebElement dosage = dosages.get(currentCount);
        action.moveToElement(dosage).click().build().perform();
        JJUtil.threadSleep(3);

        //直接点击最后一个quantity
        WebElement quantityElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_QUANTITY));
        Actions clickQuantity = new Actions(webDriver);
        clickQuantity.moveToElement(quantityElement).click().build().perform();
        JJUtil.threadSleep(2);
        String quantityAttribute = quantityElement.getAttribute(ARIA_EXPANDED);
        if (quantityAttribute.equals("false")) {
            //采集数据开始
            System.out.println("采集数据开始！！！！----------------------");
            String detailUrl = webDriver.getCurrentUrl();
            Document priceDoc = Jsoup.parse(webDriver.getPageSource());

            //采集数据
            collecDrugInfo(currentCount,priceDoc,detailUrl);

            //采集完毕！
            System.out.println("采集完毕！！！");
        }else {
            List<WebElement> quantitys = webDriver.findElements(By.className(OPTION_3JFJD));
            System.out.println(quantitys.size());
            while (nextCount < quantitys.size()) {
                //点击quantity
                JJUtil.threadSleep(3);
                System.out.println("点击quantity");
                nextCount = clickFour(nextCount, webDriver);
            }
        }

        currentCount = currentCount + 1;
        return currentCount;
    }



    public int clickFour(int currentCount, WebDriver webDriver) {
        System.out.println("终于进来第四层了，开始采集信息");
        Actions action = new Actions(webDriver);

        if (currentCount > 0) {
            WebElement quantityElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_QUANTITY));
            action.moveToElement(quantityElement).click().build().perform();
            JJUtil.threadSleep(2);
        }

        //真正点击quantity
        List<WebElement> quantitys = webDriver.findElements(By.className(OPTION_3JFJD));
        System.out.println(quantitys.size());
        WebElement quantity = quantitys.get(currentCount);
        action.moveToElement(quantity).click().build().perform();
        JJUtil.threadSleep(3);
        //采集数据开始
        System.out.println("采集数据开始！！！！----------------------");
        String detailUrl = webDriver.getCurrentUrl();
        Document priceDoc = Jsoup.parse(webDriver.getPageSource());

        //采集数据
        collecDrugInfo(currentCount,priceDoc,detailUrl);

        //采集完毕！
        System.out.println("采集完毕！！！");

        currentCount = currentCount + 1;
        return currentCount;
    }

    private void collecDrugInfo(int currentCount,Document priceDoc,String detailUrl) {
        DrugInfo drugInfo = new DrugInfo();
        //链接
        drugInfo.setDetailUrl(detailUrl);
        //productNameRaw
        Elements productNameRaws = priceDoc.select("h1[id='uat-drug-title']>a");
        if (productNameRaws != null && productNameRaws.size() > 0) {
            String productNameRaw = handleElements(productNameRaws);
            PRODUCT_NAME_RAW = productNameRaw;
            drugInfo.setProductNameRaw(productNameRaw);
        }

        //brandName
        Elements brandNames = priceDoc.select("div[id='uat-drug-alternatives']>a");
        if (brandNames != null && brandNames.size() > 0) {
            String brandName = handleElements(brandNames);
            drugInfo.setBrandName(brandName);
        }

        //brandOrGeneric
        Element brandOrGeneric = priceDoc.select("div[id='uat-dropdown-brand']").first();
        if (brandOrGeneric != null) {
            drugInfo.setBrandOrGeneric(brandOrGeneric.text());
        }

        //formulation
        Element formulation = priceDoc.select("div[id='uat-dropdown-form']").first();
        if (formulation != null) {
            drugInfo.setFormulation(formulation.text());
        }

        //specification
        Element specification = priceDoc.select("div[id='uat-dropdown-dosage']").first();
        if (specification != null) {
            drugInfo.setSpecification(specification.text());
        }

        //specificationPackage
        Element specificationPackage = priceDoc.select("div[id='uat-dropdown-quantity']").first();
        if (specificationPackage != null) {
            drugInfo.setSpecificationPackage(specificationPackage.text());
        }

        //freeCouponsPrice
        Element freeCouponsPrice = priceDoc.select("div[data-qa='coupons_tab_subtitle']").first();
        if (freeCouponsPrice != null) {
            drugInfo.setFreeCouponsPrice(freeCouponsPrice.text().replace("Prices as low as ", ""));
        }

        //savingClubsPrice
        Element savingClubsPrice = priceDoc.select("div[data-qa='savingsClubs_tab_subtitle']").first();
        if (savingClubsPrice != null) {
            drugInfo.setSavingClubsPrice(savingClubsPrice.text().replace("Prices as low as ", ""));
        }

        //mailOrderPrice
        Element mailOrderPrice = priceDoc.select("div[data-qa='mailOrder_tab_subtitle']").first();
        if (mailOrderPrice != null) {
            drugInfo.setMailOrderPrice(mailOrderPrice.text().replace("Prices as low as ", ""));
        }

        //commonBrands,type,pharmacokinetics,indication
        drugInfo.setCommonBrands(tempDrugInfo.getCommonBrands());
        drugInfo.setType(tempDrugInfo.getType());
        drugInfo.setPharmacokinetics(tempDrugInfo.getPharmacokinetics());
        drugInfo.setIndication(tempDrugInfo.getIndication());

        //保存到数据库
        saveDataToDB(drugInfo);

        drugInfos.add(drugInfo);
    }

    private void collectDrugInfoDoc(Document drugInfoDoc) {
        //commonBrands,type,pharmacokinetics
        Elements ps = drugInfoDoc.select("p[class='mb-component-1']");
        if (ps != null && ps.size() > 0) {
            String commonBrands = handlePs(ps,1);
            String type = handlePs(ps,2);
            String pharmacokinetics = handlePs(ps,8);
            tempDrugInfo.setCommonBrands(commonBrands);
            tempDrugInfo.setType(type);
            tempDrugInfo.setPharmacokinetics(pharmacokinetics);
        }

        //indication
        Elements us = drugInfoDoc.select("ul[class='list-disc my-component-4']");
        if (us != null && us.size() > 0) {
            String indication = handleUs(us);
            tempDrugInfo.setIndication(indication);
        }



    }


    private void saveDataToDB(DrugInfo drugInfo) {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER_NAME, JDBC_PASSWORD);
            Statement statement = connection.createStatement();
            String sql = "insert into drug_info(`detail_url`,`product_name_raw`,`brand_name`,`brand_or_generic`,`formulation`,`specification`," +
                    "`specification_package`,`free_coupons_price`,`saving_clubs_price`,`mailOrder_price`,`common_brands`," +
                    "`type`,`pharmacokinetics`,`indication`) values (\""+drugInfo.getDetailUrl()+"\",\""+drugInfo.getProductNameRaw()+"\",\""+drugInfo.getBrandName()+"\"," +
                    "\""+drugInfo.getBrandOrGeneric()+"\",\""+drugInfo.getFormulation()+"\",\""+drugInfo.getSpecification()+"\"," +
                    "\""+drugInfo.getSpecificationPackage()+"\",\""+drugInfo.getFreeCouponsPrice()+"\",\""+drugInfo.getSavingClubsPrice()+"\"," +
                    "\""+drugInfo.getMailOrderPrice()+"\",\""+drugInfo.getCommonBrands()+"\",\""+drugInfo.getType()+"\",\""+drugInfo.getPharmacokinetics()+"\",\""+drugInfo.getIndication()+"\");";
            System.out.println("==========================================");
            System.out.println(sql);
            statement.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void dropData() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER_NAME, JDBC_PASSWORD);
            Statement statement = connection.createStatement();
            String sql = "delete from drug_info";
            statement.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String handleElements(Elements elements) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < elements.size(); i++) {
            if (i == elements.size() - 1) {
                builder.append(elements.get(i).text());
            }else {
                builder.append(elements.get(i).text()).append(",");
            }
        }
        return builder.toString();
    }

    private String handlePs(Elements elements,int target){
        Element element = elements.get(target);
        return element.text();
    }

    private String handleUs(Elements elements) {
        Element first = elements.first();
        Elements lis = first.select("li[class='mb-component-2 ml-bulleted-list']");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lis.size(); i++) {
            if (i == lis.size() - 1) {
                builder.append(lis.get(i).text());
            }else {
                builder.append(lis.get(i).text()).append(",");
            }
        }
        return builder.toString();
    }

}
