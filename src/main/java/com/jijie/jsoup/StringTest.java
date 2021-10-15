package com.jijie.jsoup;

public class StringTest {
    public static final String PRE_FIX_URL = "https://www.goodrx.com/";

    public static void main(String[] args) {

        int count = 0;
        while(count < 26) {
            count = handleCount(count);
        }
        StringTest stringTest = new StringTest();
        System.out.println(stringTest.handleDrug("Diltiazem ER (Cardizem CD)"));
        //Buprenorphine / Naloxone
    }

    public static int handleCount(int i) {
        System.out.println(i);
        int count = i+1;
        return count;
    }

    private String handleDrug(String str){
        StringBuilder stringBuilder = new StringBuilder();
        //1.用“-”代替空格,并转小写
        String replace = str.replace(" / ","-")
                .replace("/","-")
                .replace(" (","-")
                .replace(" ", "-")
                .replace(")","").toLowerCase();

        //2.添加前缀，拼接url,并返回
        String url = stringBuilder.append(PRE_FIX_URL).append(replace).toString();
        //2.添加前缀，拼接url,并返回
        return url;

    }
}
