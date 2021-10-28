package com.jijie.jsoup;

import com.gbdata.common.mongo.Entity;
import com.gbdata.common.mongo.MongoEntityClient;
import com.gbdata.common.util.Email;
import com.gbdata.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GrabGoodsStepTwo {

    private static final Logger LOGGER = LoggerFactory.getLogger(GrabGoodsStepTwo.class);

    public String PRODUCT_NAME_RAW;
    public static JSONObject jsonObjectTemp = new JSONObject();
    private static final String GRAB_GOOD_RX_INFO = "GrabGoodRxInfo";
    private MongoEntityClient dbSource =  new MongoEntityClient("SOURCE");
    public static final String JIE_JI = "cgao@generalbiologic.com";
    public static final String OPTION_3JFJD = "option-3JfJd";
    public static final int ERROR_CODE_TIMEOUT = 99999;
    public static final String ERROR_CODE_TIMEOUT_MSG = "程序超时！";

    public static final String ARIA_EXPANDED = "aria-expanded";
    public static final String UAT_DROPDOWN_CONTAINER_BRAND = "uat-dropdown-container-brand";

    public static final String UAT_DROPDOWN_CONTAINER_FORM = "uat-dropdown-container-form";
    public static final String UAT_DROPDOWN_CONTAINER_DOSAGE = "uat-dropdown-container-dosage";
    public static final String UAT_DROPDOWN_CONTAINER_QUANTITY = "uat-dropdown-container-quantity";
    public static final String DRUG_INFO_BTN = "drug_info_btn";

    public static final String FILE_PATH = "C:\\jijie\\jijie.txt";
    public static List<String> drugInfos = new ArrayList<>();
    public static int try_count = 0;
    public static int error_count = 0;
    public static final int error_code = 99999;
    public static final String SUCCESS_SUBJECT = "采集成功！";
    public static final String FAIL_SUBJECT = "数据采集过程报错！";


    public static void main(String[] args) {
        GrabGoodsStepTwo two = new GrabGoodsStepTwo();
        int count = 0;
        boolean grabFinshFlag = two.checkGrabFinsh();
        if (grabFinshFlag) {
            return;
        }
        while(count < drugInfos.size()) {
            //前置步骤，需要检查文件中的数据是否全部采集完毕，全部采集完毕则程序终止
            grabFinshFlag = two.checkGrabFinsh();
            if (grabFinshFlag) {
                return;
            }
            count = two.collecData(drugInfos, count);
        }
        if (count == error_code){
            //程序报错，需人工干预。本次采集终止
            System.exit(0);
            return;

        }
        System.out.println("采集完毕！共采集数据："+drugInfos.size()+"条");
    }

    public boolean checkGrabFinsh() {
        drugInfos.clear();
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(FILE_PATH)), "utf-8");
            String[] split = content.split(";");
            for (int i = 0; i < split.length-1; i++) {
                String[] str = split[i].split("#");
                String url = str[0].replace("\n","");
                String passFlag = str[1];
                if (passFlag.equals("false")){
                    drugInfos.add(url);
                }
            }
            if (drugInfos.size() > 0) {
                return false;
            }
            return true;
        } catch (IOException e) {
            sendEmail("文件不存在！",FAIL_SUBJECT);
            throw new RuntimeException("文件不存在！");
        }
    }

    public int collecData(List<String> drugInfos,int count) {
        int currentCount = 0;
        System.out.println("所有需要采集的url数量为：" + drugInfos.size());
        String currentUrl = drugInfos.get(currentCount);

        System.out.println("采集的数据link为：" + currentUrl);
        System.getProperties().setProperty("webdriver.ie.driver", "C:\\IEDriverServer_x64_3.14.0\\IEDriverServer.exe");
        WebDriver webDriver = new InternetExplorerDriver();

        try{
            webDriver.get(currentUrl);
            check(webDriver);
            NmpaGrabberUtil.sleep(10);
            webDriver.manage().window().maximize();
            NmpaGrabberUtil.sleep(3);
            //药品详情
            Actions clickDrugInfo = new Actions(webDriver);
            WebElement drugInfoElement = webDriver.findElement(By.cssSelector("li[data-qa='" + DRUG_INFO_BTN + "']"));
            clickDrugInfo.moveToElement(drugInfoElement).click().build().perform();
            NmpaGrabberUtil.sleep(10);
            Document drugInfoDoc = Jsoup.parse(webDriver.getPageSource());
            if (drugInfoDoc == null) {
                System.out.println("药品信息数据采集过程中出错，重新采集");
                return count;
            }
            collectDrugInfoDoc(drugInfoDoc);
            webDriver.navigate().back();
            NmpaGrabberUtil.sleep(5);
            //嵌套第一层
            WebElement brandElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_BRAND));
            Actions action = new Actions(webDriver);
            action.moveToElement(brandElement).click().build().perform();
            NmpaGrabberUtil.sleep(2);
            String brandAttribute = brandElement.getAttribute(ARIA_EXPANDED);
            System.out.println(brandAttribute);

            if (brandAttribute.equals("false")) {
                //直接点击form
                WebElement formElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_FORM));
                Actions clickForm = new Actions(webDriver);
                clickForm.moveToElement(formElement).click().build().perform();
                NmpaGrabberUtil.sleep(2);
                String formAttribute = formElement.getAttribute(ARIA_EXPANDED);
                if (formAttribute.equals("false")) {
                    //直接点击dosage
                    WebElement dosageElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_DOSAGE));
                    Actions clickDosage = new Actions(webDriver);
                    clickDosage.moveToElement(dosageElement).click().build().perform();
                    NmpaGrabberUtil.sleep(2);
                    String dosageAttribute = dosageElement.getAttribute(ARIA_EXPANDED);
                    if (dosageAttribute.equals("false")){
                        //直接点击最后一个quantity
                        WebElement quantityElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_QUANTITY));
                        Actions clickQuantity = new Actions(webDriver);
                        clickQuantity.moveToElement(quantityElement).click().build().perform();
                        NmpaGrabberUtil.sleep(2);
                        String quantityAttribute = quantityElement.getAttribute(ARIA_EXPANDED);
                        if (quantityAttribute.equals("false")) {
                            //采集数据开始
                            System.out.println("四个都不可点击，采集数据开始！！！！----------------------");
                            String detailUrl = webDriver.getCurrentUrl();
                            Document priceDoc = Jsoup.parse(webDriver.getPageSource());

                            //采集数据
                            collecDrugInfo(currentCount,priceDoc,detailUrl);

                            //采集完毕！

                            LOGGER.info("四个都不可点击，采集完毕！！");
                            System.out.println("四个都不可点击，采集完毕！！");
                        }else {
                            List<WebElement> quantitys = webDriver.findElements(By.className(OPTION_3JFJD));
                            System.out.println(quantitys.size());
                            while (currentCount < quantitys.size()) {
                                NmpaGrabberUtil.sleep(3);
                                //点击quantity
                                currentCount = clickFour(currentCount, webDriver);
                            }
                        }
                    }else {
                        List<WebElement> dosages = webDriver.findElements(By.className(OPTION_3JFJD));
                        System.out.println(dosages.size());
                        while (currentCount < dosages.size()) {
                            NmpaGrabberUtil.sleep(3);
                            //点击dosage
                            System.out.println("点击dosage");
                            currentCount = clickThree(currentCount, webDriver);
                        }
                    }
                }else {
                    List<WebElement> forms = webDriver.findElements(By.className(OPTION_3JFJD));
                    System.out.println(forms.size());
                    while (currentCount < forms.size()) {
                        NmpaGrabberUtil.sleep(3);
                        //点击form
                        System.out.println("点击form");
                        currentCount = clickTwo(currentCount, webDriver);
                    }
                }
            }else {
                List<WebElement> brands = webDriver.findElements(By.className(OPTION_3JFJD));
                System.out.println(brands.size());
                while (currentCount < brands.size()) {
                    NmpaGrabberUtil.sleep(3);
                    //点击brand
                    System.out.println("点击brand");
                    currentCount = clickOne(currentCount, webDriver);
                }
            }

            webDriver.quit();
            LOGGER.info("数据采集完毕:{}",currentUrl);
            System.out.println(currentUrl+"数据采集完毕！！");
            //文件更新！
            boolean updateFlag = updateFile(currentUrl);
            if (!updateFlag){
                sendEmail("更新文件失败！！",FAIL_SUBJECT);
            }
            checkGrabFinsh();
            count = count + 1;
            sendEmail(currentUrl+"采集完毕！",SUCCESS_SUBJECT);
            return count;
        }catch (Exception e) {
            //验证
            boolean error = errorCheck();
            if (error) {
                webDriver.quit();
                LOGGER.info("数据采集过程中出错，出错次数达到5次，需人工干预！");
                sendEmail("数据采集过程中出错，出错次数达到5次，需人工干预！",FAIL_SUBJECT);
                return error_code;
            }
            webDriver.quit();
            LOGGER.info("数据采集过程中出错，重新采集");
            System.out.println("数据采集过程中出错，重新采集");
            return count;
        }
    }

    public boolean errorCheck() {
        if (error_count > 5) {
            return true;
        }
        error_count = error_count + 1;
        return false;
    }

    public boolean updateFile(String currentUrl) {
        FileWriter fwriter = null;
        try {
            String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)), "utf-8");
            String oldStr = currentUrl + "#false";
            String newStr = currentUrl + "#true";
            if (content.contains(oldStr)){
                String replace = content.replace(oldStr, newStr);
                // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
                fwriter = new FileWriter(FILE_PATH);
                fwriter.write(replace);
                return true;
            }
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void check(WebDriver webDriver) {
        if (try_count > 5) {
            sendEmail(ERROR_CODE_TIMEOUT_MSG,FAIL_SUBJECT);
            //需要手工干预！
        }
        try{
            WebElement human = webDriver.findElement(By.cssSelector("div[role='main']"));
            WebElement p = human.findElement(By.tagName("p"));
            System.out.println(p.getText());
            Actions clickHuman = new Actions(webDriver);
            clickHuman.clickAndHold(p).build().perform();
            NmpaGrabberUtil.sleep(15);
            clickHuman.release();
            NmpaGrabberUtil.sleep(5);
            WebElement try_again = human.findElement(By.id("px-captcha"));
            if (try_again != null) {
                try_count = try_count + 1;
                //说明需要重试
                check(webDriver);
            }
        }catch (NoSuchElementException e){
            System.out.println("程序运行正常！");
        }
    }

    public void sendEmail(String errMsg,String subject) {
        Email email = new Email();
        email.to.add(JIE_JI);
        email.subject = subject;
        email.text = errMsg;
        email.send();
    }

    public int clickOne(int currentCount, WebDriver webDriver) {
        Actions action = new Actions(webDriver);
        int nextCount = 0;

        //true
        if (currentCount > 0) {
            WebElement brandElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_BRAND));
            action.moveToElement(brandElement).click().perform();
            NmpaGrabberUtil.sleep(2);
        }
        List<WebElement> brands = webDriver.findElements(By.className("option-3JfJd"));
        System.out.println(brands.size());
        WebElement brand = brands.get(currentCount);
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(false);",brand);
        NmpaGrabberUtil.sleep(2);
        action.moveToElement(brand).click().build().perform();
        NmpaGrabberUtil.sleep(3);
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
        NmpaGrabberUtil.sleep(2);
        String formAttribute = formElement.getAttribute(ARIA_EXPANDED);
        System.out.println(formAttribute);

        if (formAttribute.equals("false")) {
            //直接点击dosage
            WebElement dosageElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_DOSAGE));
            Actions clickDosage = new Actions(webDriver);
            clickDosage.moveToElement(dosageElement).click().build().perform();
            NmpaGrabberUtil.sleep(2);
            String dosageAttribute = dosageElement.getAttribute(ARIA_EXPANDED);
            if (dosageAttribute.equals("false")){
                //直接点击最后一个quantity
                WebElement quantityElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_QUANTITY));
                Actions clickQuantity = new Actions(webDriver);
                clickQuantity.moveToElement(quantityElement).click().build().perform();
                NmpaGrabberUtil.sleep(2);
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
                        NmpaGrabberUtil.sleep(3);
                        //点击quantity
                        System.out.println("点击quantity");
                        nextCount = clickFour(nextCount, webDriver);
                    }
                }
            }else {
                List<WebElement> dosages = webDriver.findElements(By.className(OPTION_3JFJD));
                System.out.println(dosages.size());
                while (nextCount < dosages.size()) {
                    NmpaGrabberUtil.sleep(3);
                    //点击dosage
                    System.out.println("点击dosage");
                    nextCount = clickThree(nextCount, webDriver);
                }
            }

        }else {
            List<WebElement> forms = webDriver.findElements(By.className(OPTION_3JFJD));
            System.out.println(forms.size());
            while (nextCount < forms.size()) {
                NmpaGrabberUtil.sleep(3);
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
            NmpaGrabberUtil.sleep(2);
        }
        //真正点击form
        List<WebElement> forms = webDriver.findElements(By.className("option-3JfJd"));
        System.out.println(forms.size());
        WebElement form = forms.get(currentCount);
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(false);",form);
        NmpaGrabberUtil.sleep(2);
        action.moveToElement(form).click().build().perform();
        NmpaGrabberUtil.sleep(3);

        //点击dosage
        WebElement dosageElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_DOSAGE));
        Actions clickDosage = new Actions(webDriver);
        clickDosage.moveToElement(dosageElement).click().build().perform();
        NmpaGrabberUtil.sleep(2);
        String dosageAttribute = dosageElement.getAttribute(ARIA_EXPANDED);
        if (dosageAttribute.equals("false")){
            //直接点击最后一个quantity
            WebElement quantityElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_QUANTITY));
            Actions clickQuantity = new Actions(webDriver);
            clickQuantity.moveToElement(quantityElement).click().build().perform();
            NmpaGrabberUtil.sleep(2);
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
                    NmpaGrabberUtil.sleep(3);
                    //点击quantity
                    System.out.println("点击quantity");
                    nextCount = clickFour(nextCount, webDriver);
                }
            }
        }else {
            List<WebElement> dosages = webDriver.findElements(By.className(OPTION_3JFJD));
            System.out.println(dosages.size());
            while (nextCount < dosages.size()) {
                NmpaGrabberUtil.sleep(3);
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
            NmpaGrabberUtil.sleep(2);
        }
        //真正点击dosage
        List<WebElement> dosages = webDriver.findElements(By.className(OPTION_3JFJD));
        System.out.println(dosages.size());
        WebElement dosage = dosages.get(currentCount);
        ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(false);",dosage);
        NmpaGrabberUtil.sleep(2);
        action.moveToElement(dosage).click().build().perform();
        NmpaGrabberUtil.sleep(3);

        //直接点击最后一个quantity
        WebElement quantityElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_QUANTITY));
        Actions clickQuantity = new Actions(webDriver);
        clickQuantity.moveToElement(quantityElement).click().build().perform();
        NmpaGrabberUtil.sleep(2);
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
                NmpaGrabberUtil.sleep(3);
                System.out.println("点击quantity");
                nextCount = clickFour(nextCount, webDriver);
            }
        }

        currentCount = currentCount + 1;
        return currentCount;
    }



    public int clickFour(int currentCount, WebDriver webDriver) {
        //webDriver
        WebElement brand = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_BRAND));
        WebElement form = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_FORM));
        WebElement dosage = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_DOSAGE));
        WebElement qq = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_QUANTITY));


        System.out.println("终于进来第四层了，开始采集信息");
        Actions action = new Actions(webDriver);

        if (currentCount > 0) {
            WebElement quantityElement = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_QUANTITY));
            action.moveToElement(quantityElement).click().build().perform();
            NmpaGrabberUtil.sleep(2);
        }
        WebElement quantity = webDriver.findElement(By.id(UAT_DROPDOWN_CONTAINER_QUANTITY));
        String quantityAttribute = quantity.getAttribute(ARIA_EXPANDED);
        if (quantityAttribute.equals("false")) {
            //采集数据开始
            System.out.println("采集数据开始！！！！----------------------");
            String detailUrl = webDriver.getCurrentUrl();
            Document priceDoc = Jsoup.parse(webDriver.getPageSource());

            //采集数据
            collecDrugInfo(currentCount,priceDoc,detailUrl);

            //采集完毕！
            System.out.println("采集完毕！！！");
        } else {
            //真正点击quantity
            List<WebElement> quantitys = webDriver.findElements(By.className(OPTION_3JFJD));
            System.out.println(quantitys.size());
            try{
                WebElement quantityE = quantitys.get(currentCount);
                ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(false);",quantityE);
                NmpaGrabberUtil.sleep(2);
                action.moveToElement(quantityE).click().build().perform();
                NmpaGrabberUtil.sleep(3);
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
            }catch (IndexOutOfBoundsException e){
                LOGGER.info("循环出错！停止本次循环，出错信息：");
                LOGGER.info("brand:{}",brand.getText());
                LOGGER.info("form:{}",form.getText());
                LOGGER.info("dosage:{}",dosage.getText());
                LOGGER.info("quantity:{}",qq.getText());

                System.out.println("brand："+brand.getText());

                WebElement element = webDriver.findElement(By.cssSelector("div[data-qa='coupons_tab_subtitle']"));
                Actions clickFreeCoupons = new Actions(webDriver);
                clickFreeCoupons.moveToElement(element).click().build().perform();

                return currentCount;
            }
        }

        currentCount = currentCount + 1;
        return currentCount;
    }



    private void collecDrugInfo(int currentCount,Document priceDoc,String detailUrl) {

        JSONObject drugInfo = new JSONObject();
        //链接
        drugInfo.put("detailUrl",detailUrl);
        //productNameRaw
        Elements productNameRaws = priceDoc.select("h1[id='uat-drug-title']>a");
        if (productNameRaws != null && productNameRaws.size() > 0) {
            String productNameRaw = handleElements(productNameRaws);
            PRODUCT_NAME_RAW = productNameRaw;
            drugInfo.put("productNameRaw",productNameRaw);
        }

        //brandName
        Elements brandNames = priceDoc.select("div[id='uat-drug-alternatives']>a");
        if (brandNames != null && brandNames.size() > 0) {
            String brandName = handleElements(brandNames);
            drugInfo.put("brandName",brandName);
        }

        //brandOrGeneric
        Element brandOrGeneric = priceDoc.select("div[id='uat-dropdown-brand']").first();
        if (brandOrGeneric != null) {
            drugInfo.put("brandOrGeneric",brandOrGeneric.text());
        }

        //formulation
        Element formulation = priceDoc.select("div[id='uat-dropdown-form']").first();
        if (formulation != null) {
            drugInfo.put("formulation",formulation.text());
        }

        //specification
        Element specification = priceDoc.select("div[id='uat-dropdown-dosage']").first();
        if (specification != null) {
            drugInfo.put("specification",specification.text());
        }

        //specificationPackage
        Element specificationPackage = priceDoc.select("div[id='uat-dropdown-quantity']").first();
        if (specificationPackage != null) {
            drugInfo.put("specificationPackage",specificationPackage.text());
        }

        //freeCouponsPrice
        Element freeCouponsPrice = priceDoc.select("div[data-qa='coupons_tab_subtitle']").first();
        if (freeCouponsPrice != null) {
            drugInfo.put("freeCouponsPrice",freeCouponsPrice.text().replace("Prices as low as ", ""));
        }

        //savingClubsPrice
        Element savingClubsPrice = priceDoc.select("div[data-qa='savingsClubs_tab_subtitle']").first();
        if (savingClubsPrice != null) {
            drugInfo.put("savingClubsPrice",savingClubsPrice.text().replace("Prices as low as ", ""));
        }

        //mailOrderPrice
        Element mailOrderPrice = priceDoc.select("div[data-qa='mailOrder_tab_subtitle']").first();
        if (mailOrderPrice != null) {
            drugInfo.put("mailOrderPrice",mailOrderPrice.text().replace("Prices as low as ", ""));
        }

        //commonBrands,type,pharmacokinetics,indication
        drugInfo.put("commonBrands",jsonObjectTemp.get("commonBrands"));
        drugInfo.put("type",jsonObjectTemp.get("type"));
        drugInfo.put("pharmacokinetics",jsonObjectTemp.get("pharmacokinetics"));
        drugInfo.put("indication",jsonObjectTemp.get("indication"));

        String mongoId = DigestUtils.md5Hex(detailUrl);

        //保存到数据库
        dbSource.save(new Entity(mongoId,GRAB_GOOD_RX_INFO,drugInfo));
        //saveDataToDB(drugInfo);


//        drugInfos.add(drugInfo);
    }

    private void collectDrugInfoDoc(Document drugInfoDoc) {

        //commonBrands,type,pharmacokinetics
        Elements ps = drugInfoDoc.select("p[class='mb-component-1']");
        if (ps != null && ps.size() > 0) {
            String commonBrands = handlePs(ps,1);
            String type = handlePs(ps,2);
            String pharmacokinetics = handlePs(ps,8);
            jsonObjectTemp.put("commonBrands",commonBrands);
            jsonObjectTemp.put("type",type);
            jsonObjectTemp.put("pharmacokinetics",pharmacokinetics);
        }

        //indication
        Elements us = drugInfoDoc.select("ul[class='list-disc my-component-4']");
        if (us != null && us.size() > 0) {
            String indication = handleUs(us);
            jsonObjectTemp.put("indication",indication);
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
