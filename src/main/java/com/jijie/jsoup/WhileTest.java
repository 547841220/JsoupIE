package com.jijie.jsoup;

import java.util.ArrayList;
import java.util.List;

public class WhileTest {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("a");
        System.out.println(list.size());
    }

    /*public static void main(String[] args) {
        int count = 0;
        while(count < 5) {
            System.out.println("-------第一层-------");
            count = clickOne(count);
            System.out.println(count);
        }

    }
    public static int clickOne(int count){
        int count2 = 0;
        while (count2 < 5) {
            System.out.println("第二层");
            System.out.println(count2);
            count2 = clickTwo(count2);
        }
        count = count + 1;
        return count;

    }*/

    /*public static int clickTwo(int count2) {
        count2 = count2 + 1;
        return count2;
    }*/
}
