package com.guima.kits;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.guima.domain.InterfaceConfig;
import com.guima.services.InterfaceConfigService;
import com.jfinal.kit.LogKit;
import com.guima.base.kits.QueryParam;
import com.guima.base.service.ServiceManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class OssKit
{
    private final String endPoint;
    private final String accessKeyId;
    private final String accessKeySecret;
    private final String bucketName;
    private final InterfaceConfigService service;

    private OssKit()
    {
        service = (InterfaceConfigService) ServiceManager.instance()
                .getService("interfaceconfig");
        List<InterfaceConfig> configs = service.list("aliyun_oss");
        this.endPoint = service.getConfigValue(configs, "end_point");
        this.accessKeyId = EncryptionKit.decryptByAES(service.getConfigValue(configs, "access_key_id"));
        this.accessKeySecret = EncryptionKit.decryptByAES(service.getConfigValue(configs, "access_key_secret"));
        this.bucketName = service.getConfigValue(configs, "bucket_name");
    }

    public static OssKit init()
    {
        return new OssKit();
    }

    public String upload(String key, byte[] fileByte)
    {
        return upload(key,null,fileByte);
    }

    public String upload(String key, InputStream inputStream){
        return upload(key,inputStream,null);
    }

    private String upload(String key, InputStream inputStream,byte[] fileByte){
        OSSClient ossClient = null;
        String cloudPath = "ERROR";
        try
        {
            inputStream=inputStream!=null?inputStream:new ByteArrayInputStream(fileByte);
            ossClient = new OSSClient("http://" + endPoint, accessKeyId, accessKeySecret);
            ossClient.putObject(bucketName, key, inputStream);
            cloudPath = "http://" + bucketName + "." + endPoint + "/" + key;
        } catch (OSSException oe)
        {
            LogKit.error("资源文件" + key + "上传失败，msg=" + oe.getErrorMessage()
                    + "，code=" + oe.getErrorCode());
        } catch (ClientException ce)
        {
            LogKit.error("资源文件" + key + "上传失败，msg=" + ce.getErrorMessage()
                    + "，code=" + ce.getErrorCode());
        } finally
        {
            try {
                if (ossClient != null)
                    ossClient.shutdown();
                if(inputStream!=null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cloudPath;
    }

    public byte[] download(String ossUrl)
    {
        String key = ossUrl.replace("http://" + bucketName + "." + endPoint + "/", "");

        OSSClient ossClient = null;
        byte[] fileByte = null;
        try
        {
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            ossClient = new OSSClient("http://" + endPoint, accessKeyId, accessKeySecret);
            OSSObject ossObject = ossClient.getObject(bucketName, key);
            InputStream inStream = ossObject.getObjectContent();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) != -1)
            {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            fileByte = outSteam.toByteArray();
        } catch (Exception e)
        {
            LogKit.error("从OSS下载文件" + key + "时出错", e);
        } finally
        {
            if (ossClient != null)
                ossClient.shutdown();
        }
        return fileByte;
    }

    public static String getThumb(String ossUrl,String thumbFullName)
    {
        InputStream thumbInputStream;
        try{
            ossUrl=ossUrl+"?x-oss-process=image/resize,w_120,h_120,limit_0";
            URL url = new URL(ossUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(1 * 1000);
            thumbInputStream=conn.getInputStream();
            return OssKit.init().upload(thumbFullName, thumbInputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
