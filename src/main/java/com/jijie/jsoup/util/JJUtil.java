package com.jijie.jsoup.util;

import java.util.concurrent.TimeUnit;

public class JJUtil {

    //线程sleep
    public static void threadSleep(long second) {
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
