package com.jijie.jsoup;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GrabGoodsStepOne {

    public static Map<Integer,String> a_z = new HashMap<>(26);
    public static final String PRE_FIX_URL = "https://www.goodrx.com/";
    public static final String FILE_PATH = "C:\\jijie\\jijie.txt";
    public static Set<Map<String,String>> drugs = new HashSet<>();

    public static void main(String[] args) throws IOException {
        GrabGoodsStepOne one = new GrabGoodsStepOne();
        Set<Map<String,String>> maps = one.collectDrugs();
        File file = new File(FILE_PATH);
        FileWriter fwriter = null;
        if (!file.exists()) {
            file.createNewFile();
        }

        try {
            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
            fwriter = new FileWriter(FILE_PATH);
            for (Map<String, String> map : maps) {
                fwriter.write(map.get("url"));
                fwriter.write("#");
                fwriter.write(map.get("passFlag"));
                fwriter.write(";");
                fwriter.write("\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Set<Map<String,String>> collectDrugs() {
        String initUrl = "https://www.goodrx.com/drugs";
        int countOne = 0;
        while(countOne < 2) {
            if (countOne > 0) {
                NmpaGrabberUtil.sleep(10);
            }
            countOne = getAllUrl(initUrl,countOne,drugs);
        }
        System.out.println("所有需要采集的url数量为："+drugs.size());
        return drugs;
    }

    public int getAllUrl(String initUrl,int count,Set<Map<String,String>> drugs) {
        System.getProperties().setProperty("webdriver.ie.driver","C:\\IEDriverServer_x64_3.14.0\\IEDriverServer.exe");
        WebDriver webDriver = new InternetExplorerDriver();

        try{
            webDriver.get(initUrl);
            webDriver.manage().window().maximize();
            NmpaGrabberUtil.sleep(3);
            //验证
            try{
                WebElement human = webDriver.findElement(By.cssSelector("div[role='main']"));
                WebElement p = human.findElement(By.tagName("p"));
                System.out.println(p.getText());
                Actions clickHuman = new Actions(webDriver);
                clickHuman.clickAndHold(p).build().perform();
                NmpaGrabberUtil.sleep(15);
                clickHuman.release();
            }catch (NoSuchElementException e){
                System.out.println("没有人机校验，通过！");
            }
            try{
                WebElement mainTitle = webDriver.findElement(By.id("mainTitle"));
                if (mainTitle.getText().equals("无法访问此页面")){
                    webDriver.quit();
                    return count;
                }
            }catch (Exception e) {
                System.out.println("every thing is good");
            }
            NmpaGrabberUtil.sleep(10);
            List<WebElement> azs = webDriver.findElements(By.className("letterLink-3cdW1"));
            WebElement element = azs.get(count);
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(false);",element);
            NmpaGrabberUtil.sleep(2);
            Actions clickElement = new Actions(webDriver);
            clickElement.moveToElement(element).click().build().perform();
            NmpaGrabberUtil.sleep(8);
            //加载i页面最大等待时长60秒，超时重试！
            List<WebElement> as = webDriver.findElements(By.className("linkContainer-gJwsn"));
            NmpaGrabberUtil.sleep(3);
            System.out.println(count+"分类下，药品数量："+as.size());
            //采集i页面有多少药品链接
            for (WebElement drug : as) {
                try{
                    WebElement span = drug.findElement(By.tagName("span"));
                    String text = span.getText();
                    /*String s = a_z.get(count);
                    if (!text.substring(0,1).equals(s)){
                        webDriver.quit();
                        return count;
                    }*/
                    //对字符串做处理
                    handleDrug(text,drugs);
                }catch (StaleElementReferenceException e){
                    webDriver.quit();
                    return count;
                }
            }
            webDriver.quit();
            count = count + 1;
            return count;
        }catch (Exception e) {
            webDriver.quit();
            return count;
        }
    }

    private void handleDrug(String str,Set<Map<String,String>> drugs){
        StringBuilder stringBuilder = new StringBuilder();
        Map<String,String> map = new HashMap<>();
        //1.用“-”代替空格,并转小写
        String replace = str.replace(" / ","-")
                .replace("/","-")
                .replace("(","-")
                .replace(" ", "-").toLowerCase();

        //2.添加前缀，拼接url,并返回
        String url = stringBuilder.append(PRE_FIX_URL).append(replace).toString();
        map.put("url",url);
        map.put("passFlag","false");
        System.out.println(url);
        drugs.add(map);
//        drugInfos.add(url);
    }

}
