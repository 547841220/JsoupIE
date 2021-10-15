package com.jijie.jsoup;

public class StringTest {
    public static final String PRE_FIX_URL = "https://www.goodrx.com/";

    public static void main(String[] args) {

        int count = 0;
        while(count < 26) {
            count = handleCount(count);
        }
        StringTest stringTest = new StringTest();
        System.out.println(stringTest.handleDrug("Amphetamine Salt Combo"));
        //Buprenorphine / Naloxone
    }

    public static int handleCount(int i) {
        System.out.println(i);
        int count = i+1;
        return count;
    }

    private String handleDrug(String str){
        StringBuilder url = new StringBuilder();
        //1.用“-”代替空格,并转小写
        String replace = str.replace(" ", "-").toLowerCase();
        //2.添加前缀，拼接url,并返回
        return url.append(PRE_FIX_URL).append(replace).toString();

    }
}
