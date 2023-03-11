package com.blog.controller;

import com.blog.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/*
*   -   服务端要接收客户端页面上传的文件，通常都会使用 Apache 的两个组件:
    -   `commons-fileupload`
    -   `commons-io`
    * Spring 框架在 spring-web 包中对文件上传进行了封装，大大简化了服务端代码
    * 我们只需要在 Controller 的方法中声明一个 MultipartFile 类型的参数即可接收上传的文件
    *
    * 流程：
    * 1. 从配置文件中获取保存文件的位置，没有则创建一个文件夹
    * 2. 上传文件前先对文件名进行修改（UUID）避免重复，然后上传
    *
    * =============================================================================
    *
    *   -   文件下载，也称为了 download，是指将文件从服务器传输到本地计算机的过程
        -   通过浏览器进行文件下载，通常有两种表现形式
            1.  以附件形式下载，弹出保存对话框，将文件保存到指定磁盘目录
            2.  直接在浏览器中打开
       -   通过浏览器进行文件下载，本质上就是服务端将文件以流的形式写回浏览器的过程
    *
    *
* */


@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    //file是个临时文件，我们在断点调试的时候可以看到，但是执行完整个方法之后就消失了
    public Result<String> upload(MultipartFile file) {
        log.info("获取文件：{}", file.toString());
//        判断一下当前目录是否存在，不存在则创建
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }


        //获取一下传入的原文件名,file.getOriginalFilename()是得到上传时的文件名。
        String originalFilename = file.getOriginalFilename();
        //我们只需要获取一下格式后缀，取子串，
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //为了防止出现重复的文件名，我们需要使用UUID
        String fileName = UUID.randomUUID() + suffix;
        try {
            //我们将其转存到我们的指定目录下
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //将文件名返回给前端，便于后期的开发
        return Result.success(fileName);

    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {


        FileInputStream fis = null;
        ServletOutputStream os = null;
        try {
            //        输入流，通过输入流读取文件内容
            fis = new FileInputStream(basePath + name);
            //        输出流，通过输出流将文件写回浏览器，在浏览器展示图片了
            os = response.getOutputStream();
            //设置响应给浏览器的文件是什么类型
            response.setContentType("image/jpeg");
            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1){
                os.write(buffer, 0, len);
//                todo
                os.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            //关闭资源
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
