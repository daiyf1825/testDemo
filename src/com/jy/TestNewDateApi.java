package com.jy;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestNewDateApi {


    @Test
    public void test() {
        DateTimeFormatter dft = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime ldt = LocalDateTime.now();
        String startTime = ldt.format(dft);
        System.out.println(startTime);

        DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        String strDate = dtf2.format(ldt);
        System.out.println("年月日"+strDate);
    }
}
