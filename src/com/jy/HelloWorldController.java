package com.jy;

import com.xz.demo.util.Util;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@RestController
public class HelloWorldController {

    @RequestMapping("/hello")
    public String index(){
        return "Hello World";
    }

    @GetMapping("/download")
    public Boolean download(@RequestParam String filePath) {
        String ext = filePath.substring(filePath.lastIndexOf("/") + 1);
        String saveFile = System.getProperty("user.home") + "/Documents/" + ext;
        return Util.downLoad(filePath, saveFile);
    }

    @GetMapping("/download")
    public Boolean download(@RequestParam String filePath, HttpServletResponse response){
        String ext = filePath.substring(filePath.lastIndexOf("/") + 1);
        String saveFile = System.getProperty("user.home") + "/Documents/" + ext;
        // 1.下载网络文件
        int byteRead;
        URL url;
        try {
            url = new URL(filePath);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return false;
        }
        try {
            //2.获取链接
            URLConnection conn = url.openConnection();
            //3.输入流
            InputStream inStream = conn.getInputStream();
            response.reset();
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(ext.getBytes(), "utf-8"));
            //3.写入文件
            ServletOutputStream out = response.getOutputStream();

            byte[] buffer = new byte[1024];
            while ((byteRead = inStream.read(buffer)) != -1) {
                out.write(buffer, 0, byteRead);
            }
            inStream.close();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
