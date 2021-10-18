package com.jijie.jsoup;

public class ContinueTest {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println("外层for循环，"+i);
            for (int i1 = 0; i1 < 10; i1++) {
                System.out.println("内层for循环，"+i1);
                if (i1 == 2) {
                    System.out.println("i1 = 2");
                    continue;
                }
            }
        }
    }
}
