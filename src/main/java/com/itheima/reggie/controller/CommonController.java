package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/*
* 文件上传和下载
* */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Value("${reggie.path}")
    private String filepath;
    /*
    * 文件上传
    * */
    @RequestMapping("/upload")
    public R<String> upload(MultipartFile file){
        log.info("进入了文件上传controller");
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + suffix;
        //创建一个目录对象
        File dir = new File(filepath);
        //如果路径不存在时
        if (!dir.exists()){
            //创建相应的目录
            dir.mkdirs();
        }
        try {
            file.transferTo(new File(filepath+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(filename);
    }
    /*
    * 文件下载
    * */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            //输入流，获取文件内容
            FileInputStream fileInputStream = new FileInputStream(new File(filepath+name));
            //输出流，向浏览器写回数据
            ServletOutputStream outputStream = response.getOutputStream();
            //设置写回数据的类型
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ( (len = fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            //关闭资源
            outputStream.close();
            fileInputStream.close();
        }catch (Exception e){
        e.printStackTrace();
        }


    }
}
