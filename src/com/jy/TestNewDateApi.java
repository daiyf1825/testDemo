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
    }
}
