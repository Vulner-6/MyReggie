package com.myreggie.controller;

import com.myreggie.common.R;
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
import java.util.UUID;

@Slf4j
@RestController   // 等同于 @Controller + @ResponseBody
@RequestMapping("/common")
public class CommonController
{
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file)
    {
        // file 是前端传给后端的临时文件，需要转存，否则一次请求后会被清除
        log.info(file.toString());

        // 原始文件名
        String originalFilename=file.getOriginalFilename();
        // 文件后缀
        String suffix=originalFilename.substring(originalFilename.lastIndexOf("."));

        // 使用 UUID 重新生成文件名，防止文件重复造成文件覆盖
        String fileName= UUID.randomUUID().toString()+suffix;

        // 创建一个目录对象
        File dir=new File(basePath);
        // 判断目录是否存在，若不存在则创建
        if(!dir.exists())
        {
            dir.mkdir();
        }

        // 将临时文件转存到指定位置
        try
        {
            file.transferTo(new File(basePath+fileName));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        // 返回文件名称
        return R.success(fileName);
    }

    /**
     * 文件下载功能
     * @param name
     * @param httpServletResponse
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse httpServletResponse)
    {
        try
        {
            // 输入流，通过输入流读取文件内容至内存
            FileInputStream fileInputStream=new FileInputStream(new File(basePath+name));
            // 输出流，通过输出流，将内存中读取的数据输出到前端
            ServletOutputStream servletOutputStream=httpServletResponse.getOutputStream();
            // 设置返回包的数据类型
            httpServletResponse.setContentType("image/jpeg");
            // 设置每次传送的字节长度，并用于存储读取的字节
            byte[] bytes=new byte[1024];
            // 用于判断是否读取结束
            int len=0;
            // 开始读取并输出,len 的值不等于 -1 则说明还存在数据
            while ((len=fileInputStream.read(bytes))!=-1)
            {
                servletOutputStream.write(bytes,0,len);
                // flush() 的功能是强制将缓冲区数据输出，而不用等到缓冲区满了再输出
                servletOutputStream.flush();
            }

            // 关闭资源
            servletOutputStream.close();
            fileInputStream.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
