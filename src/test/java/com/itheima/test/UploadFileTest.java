package com.itheima.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
@Slf4j
public class UploadFileTest {
    @Test
    public void Test(){
        String string = "testfilename.jpg";
        String s = string.substring(string.lastIndexOf("."));
        System.out.println(s);
        log.info(s);
    }
}

