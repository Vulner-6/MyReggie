package com.myreggie;

import org.junit.jupiter.api.Test;

/**
 * 测试文件上传中的部分代码
 */
public class UploadFileTest
{
    @Test
    public void testSuffix()
    {
        String fileName="test.jpg";
        String suffix=fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }
}
