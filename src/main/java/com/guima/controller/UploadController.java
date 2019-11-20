package com.guima.controller;

import com.guima.base.controller.BaseController;
import com.guima.base.interceptor.AppInterceptor;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.ServiceManager;
import com.guima.domain.Dictionary;
import com.guima.kits.Constant;
import com.guima.kits.FileKit;
import com.guima.kits.MapKit;
import com.guima.services.*;
import com.jfinal.aop.Before;
import com.jfinal.upload.UploadFile;
import com.oreilly.servlet.multipart.FilePart;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ran on 2018/3/28.
 */
@Before(AppInterceptor.class)
public class UploadController extends BaseController {

    private DictionaryService dictionaryService;

    public UploadController()
    {
        dictionaryService = ((DictionaryService) ServiceManager.instance().getService("dictionary"));
    }

    /**
     * 可用于后台管理系统，上传速度不重要的情况
     */
    public void uploadImage2()
    {
        try
        {
            if(!FileKit.checkSize(getRequest())){
                doRenderError("上传附件过大");
                return;
            }
            UploadFile uploadFile=getFile();
            String filePath=getPara("type");
            if(!FileKit.isAcceptImg(FileKit.getFileSuffix(uploadFile.getFileName()))){
                doRenderError("图片格式不正确，允许的格式为"+ SysMsg.Config.get("IMAGE_SUFFIX"));
            }
            doRenderSuccess(FileKit.upload(uploadFile,filePath));
        } catch (Exception e)
        {
            doRenderError(e);
        }
    }

    /**
     * 上传速度快
     */
    public void uploadImage(String filePath)
    {
        try
        {
            if(!FileKit.checkSize(getRequest())){
                doRenderError("上传附件不允许超过"+SysMsg.Config.get("UPLOAD_MAX_DESC"));
                return;
            }
            FilePart file=FileKit.uploadOss(getRequest());
            if(!FileKit.isAcceptImg(FileKit.getFileSuffix(file.getFileName()))){
                doRenderError("图片格式不正确，允许的格式为"+ SysMsg.Config.get("IMAGE_SUFFIX"));
                return;
            }
            doRenderSuccess(FileKit.upload(file,filePath));
        } catch (Exception e)
        {
            doRenderError(e);
        }
    }

    public void uploadAnnexImage(){
        uploadImage("annex_images");
    }

    public void uploadSignImage(){
        uploadImage("sign_images");
    }

    public void uploadBase64()
    {
        try
        {
            String fileByte=getPara("file_byte");
            String fileByteArr[]=fileByte.split(",");
            byte[] bytes = Base64.getDecoder().decode(fileByteArr[1]);
            String fileName=((((fileByteArr[0].split(":"))[1].split("/"))[1]).split(";"))[0];
            if(FileKit.isAcceptImg(fileName)){
                doRenderSuccess(FileKit.uploadByte(bytes,fileName));
            }else{
                doRenderError("图片格式不正确，允许的格式为"+ SysMsg.Config.get("IMAGE_SUFFIX"));
            }
        } catch (Exception e)
        {
            doRenderError();
        }
    }
}
