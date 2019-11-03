package com.guima.controller.admin;

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
    private UserService userService;

    public UploadController()
    {
        dictionaryService = ((DictionaryService) ServiceManager.instance().getService("dictionary"));
        userService=((UserService) ServiceManager.instance().getService("user"));
    }

    public void uploadVideo()
    {
        try
        {
            FilePart file=FileKit.uploadOss(getRequest());
            if(FileKit.isAcceptAudio(FileKit.getFileSuffix(file.getFileName()))){
                doRenderSuccess(FileKit.upload(file));
            }else{
                doRenderError("音频格式不正确，允许的格式为"+ SysMsg.Config.get("AUDIO_SUFFIX"));
            }
        } catch (Exception e)
        {
            doRenderError();
        }
    }

    /**
     * admin后台管理上传图片
     */
    public void uploadImage()
    {
        try
        {
            FilePart file=FileKit.uploadOss(getRequest());
            if(FileKit.isAcceptImg(FileKit.getFileSuffix(file.getFileName()))){
                doRenderError("图片格式不正确，允许的格式为"+ SysMsg.Config.get("IMAGE_SUFFIX"));
            }
            doRenderSuccess(FileKit.upload(file,Constant.FILE_DICTIONARY_ADMIN));
        } catch (Exception e)
        {
            doRenderError(e);
        }
    }

}
