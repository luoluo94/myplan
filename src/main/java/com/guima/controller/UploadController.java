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
    private UserService userService;

    public UploadController()
    {
        dictionaryService = ((DictionaryService) ServiceManager.instance().getService("dictionary"));
        userService=((UserService) ServiceManager.instance().getService("user"));
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

    public void uploadFileToOss(){
        System.out.println("开始上传：");
        Integer isBackground=Integer.valueOf(getPara("is_background"));
        File file=new File(getPara("path"));
        String singerName="";
        String singerId="";
        String musicName="";
        String videoUrl="";
        String musicThemeId="";
        String musicThemeName="";
        Dictionary theme=null;
        if(file.isDirectory()){
            File musicThme[]=file.listFiles();
            //音乐主题
            for (File dic:musicThme){
                if(dic.getName().equals(".DS_Store")){
                    continue;
                }
                if(dic.isDirectory()){
                    musicThemeName=dic.getName();
                    theme=dictionaryService.get(Constant.MUSIC_THEME,musicThemeName);
                    if(theme==null){
                        theme=new Dictionary(musicThemeName,Constant.MUSIC_THEME,1,isBackground);
                        boolean isSuccess=theme.save();
                        if(!isSuccess){
                            System.out.println(dic.getName()+"创建失败");
                            doRender(false);
                            return;
                        }else{
                            musicThemeId=theme.getId();
                        }
                    }else{
                        musicThemeId=theme.getId();
                    }

                    //音乐
                    File musicA[]=dic.listFiles();
                    for (File music:musicA){
                        String name=music.getName();
                        if(name.contains(".mp3")){
                            try {
                                name=FileKit.getFileName(name);
                                String singerOrmusic[]=name.split(" - ");
                                if(singerOrmusic.length>1){
                                    singerName=singerOrmusic[0];
                                    musicName=singerOrmusic[1];
                                }else {
                                    musicName=name;
                                }
                                videoUrl=FileKit.uploadInputStream(new FileInputStream(music),null,"mp3");
                                if(videoUrl==null){
                                    System.out.println("上传失败："+musicName);
                                    doRender(false);
                                    return;
                                }
                                Map map=new HashMap<>();
                                MapKit.setValueToMap(map,"name",musicName);
                                MapKit.setValueToMap(map,"singer_id",singerId);
                                MapKit.setValueToMap(map,"singer_name",singerName);
                                MapKit.setValueToMap(map,"music_theme",musicThemeId);
                                MapKit.setValueToMap(map,"video_url",videoUrl);
                                MapKit.setValueToMap(map,"hot_index","0");
//                                if(videoService.create(map)){
//                                    System.out.println("歌曲："+name+"创建成功！");
//                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                doRender(false);
                            }
                        }
                    }

                }
            }
        }
        System.out.println("上传结束：");
        doRenderSuccess("");
    }
}
