package com.jy.util;

import org.junit.internal.Throwables;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class Util {

    /**
     * 下载
     * @param httpUrl
     * @param saveFile
     * @return
     */
    public static Boolean downLoad(String httpUrl, String saveFile){
        // 1.下载网络文件
        int byteRead;
        URL url;
        try {
            url = new URL(httpUrl);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return false;
        }

        try {
            //2.获取链接
            URLConnection conn = url.openConnection();
            //3.输入流
            InputStream inStream = conn.getInputStream();
            //3.写入文件
            FileOutputStream fs = new FileOutputStream(saveFile);

            byte[] buffer = new byte[1024];
            while ((byteRead = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteRead);
            }
            inStream.close();
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 图片上传
     * 请求参数为图片base64编码的字符串，参数名为image
     * 请求头使用 Content-Type : multipart/form-data
     *
     * @param request
     * @return
     */
    @Login
    @ResponseBody
    @PostMapping("/image/upload")
    public String updateImage(HttpServletRequest request) {
        Long uid = UserUtil.getCurrentUserId();
        MultipartRequest multipartRequest = (MultipartRequest) request;
        MultipartFile multipartFile = multipartRequest.getFile("image");
        String name = multipartFile.getOriginalFilename();
        String ext = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
        if (!ImmutableList.of("jpg", "jpeg", "png").contains(ext)) {
            throw new JsonResponseException("图片格式不正确，请求上传.jpg、.jpeg或.png后缀名的文件");
        }
        String fileName = "/images/admin/" + uid + "/" + DateUtil.dateTimeToStrs(new Date()) + "." + ext;
        String uploadPath = Constants.UPLOAD_PATH + fileName;
        File dir = new File(uploadPath.substring(0, uploadPath.lastIndexOf("/")));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            multipartFile.transferTo(new File(uploadPath));
            return Constants.STATIC_SERVER_URL + fileName;
        } catch (IOException e) {
            log.error("---->>>>上传图片异常，cause: {}", Throwables.getStackTraceAsString(e));
            throw new JsonResponseException("上传图片异常，请消息后重试！");
        }
    }

    /**
     * 视频上传
     *
     * @param multipartFile 视频文件
     * @return
     */
    @Login
    @ResponseBody
    @PostMapping("/video/upload")
    public String uploadVideo(@RequestParam(value = "file") MultipartFile multipartFile) throws JsonResponseException {
        Long uid = UserUtil.getCurrentUserId();
        String fileExt = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1, multipartFile.getOriginalFilename().length()).toLowerCase();
        if (!ImmutableList.of("flv", "mp4", "avi", "mpg", "wmv", "3gp", "mov", "asf", "asx", "vob", "wmv9", "rm", "rmvb").contains(fileExt)) {
            throw new JsonResponseException("上传视频格式不正确");
        }
        try {
            String newName = "/video/admin/" + uid + "/" + DateUtil.dateTimeToStrs(new Date()) + "." + fileExt;
            String uploadPath = Constants.UPLOAD_PATH + newName;
            String path = uploadPath.substring(0, uploadPath.lastIndexOf("/"));
            File saveDir = new File(path);//视频存放路径
            //若不存在文件夹，则自动生成
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            //文件上传
            multipartFile.transferTo(new File(uploadPath));
            return Constants.STATIC_SERVER_URL + newName;
        } catch (Exception e) {
            log.error("---->>>>上传视频异常，cause：", Throwables.getStackTraceAsString(e));
            throw new JsonResponseException("上传视频异常，请稍后重试！");
        }
    }

}
