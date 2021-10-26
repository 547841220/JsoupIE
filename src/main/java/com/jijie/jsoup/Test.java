package com.jijie.jsoup;

public class Test {
    public static void main(String[] args) {
        String str = "http://baidu.com#false";
        String[] s = str.split("#");
        for (String s1 : s) {
            System.out.println(s1);
        }

    }
}
