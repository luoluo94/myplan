package com.guima.kits;

import com.guima.base.kits.SysMsg;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.Part;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by hmilyld on 17-2-17.
 */
public class FileKit
{
    public static String read(String filepath) throws Exception
    {
        File file = new File(filepath);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String content = "";
        String tmp;
        while ((tmp = reader.readLine()) != null)
        {
            content += (tmp + "\n");
        }
        reader.close();
        return content;
    }

    /**
     * 递归获取指定目录下的所有文件路径
     *
     * @param directoryPath
     * @return
     */
    public static List<String> getFilePath(String directoryPath) throws Exception
    {
        directoryPath = URLDecoder.decode(directoryPath, "UTF-8");
        List<String> list = new ArrayList<String>();
        File path = new File(directoryPath);
        getFilePath(path, list);
        return list;
    }

    private static void getFilePath(File path, List<String> list) throws Exception
    {
        if (!path.isDirectory())
            throw new Exception("传入参数必须为文件夹路径，请检查!");
        File[] files = path.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
                getFilePath(file, list);
            else
                list.add(file.getPath());
        }
    }

    public static void write(String filepath, Object obj) throws Exception
    {
        File file = new File(filepath);
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
        oos.flush();
        oos.close();
        fos.close();
    }

    public static FilePart getFile(HttpServletRequest r)
    {
        MultipartParser mp;
        try {
            mp = new MultipartParser(r, SysMsg.Config.getInt("UPLOAD_MAX"), true, true, "UTF-8");
            Part part;
            while ((part = mp.readNextPart()) != null) {
                if (part.isFile())
                {
                    FilePart filePart = (FilePart) part;
                    return  filePart;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static String upload(FilePart file) throws Exception
    {
        String type =getFileSuffix(file.getFileName());
        String newName= getDictionary(type) + "/" +  DateKit.getSerialNumberDay() + "_" + new Random().nextInt(10000);
        String key = newName + type;
        String cloudPath = OssKit.init().upload(key, file.getInputStream());
        if (cloudPath.equals("ERROR")){
            throw new Exception("上传失败！");
        }
        return cloudPath;
    }

    /**
     * 自定义文件夹名称
     * @param dictionaryName
     * @return
     * @throws Exception
     */
    public static String upload(String fileName,InputStream inputStream,String dictionaryName) throws Exception
    {
        String type =getFileSuffix(fileName);
        String newName= dictionaryName + "/" +  DateKit.getSerialNumberDay()+"/" +DateKit.getSerialNumberSecond()+ "_" + new Random().nextInt(100000);
        String key = newName + type;
        String cloudPath = OssKit.init().upload(key, inputStream);
        if (cloudPath.equals("ERROR")){
            throw new Exception("上传失败！");
        }
        return cloudPath;
    }

    /**
     * 自定义文件夹名称
     * @param file
     * @param dictionaryName
     * @return
     * @throws Exception
     */
    public static String upload(FilePart file,String dictionaryName) throws Exception
    {
        String type =getFileSuffix(file.getFileName());
        String newName= dictionaryName + "/" +  DateKit.getSerialNumber() + "_" + new Random().nextInt(10000);
        String key = newName + type;
        String cloudPath = OssKit.init().upload(key, file.getInputStream());
        if (cloudPath.equals("ERROR")){
            throw new Exception("上传失败！");
        }
        return cloudPath;
    }

    public static String upload(UploadFile file,String dictionaryName) throws Exception
    {
        String type =getFileSuffix(file.getFileName());
        String newName= dictionaryName + "/" +  DateKit.getSerialNumber() + "_" + new Random().nextInt(10000);
        String key = newName + type;
        String cloudPath = OssKit.init().upload(key, new FileInputStream(file.getFile()));
        if (cloudPath.equals("ERROR")){
            throw new Exception("上传失败！");
        }
        return cloudPath;
    }

    public static String uploadByte(byte[] fileByte,String type) throws Exception
    {
        if (fileByte == null || fileByte.length == 0)
            throw new Exception("上传失败！");
        String newName=getDictionary(type) + "/" +  DateKit.getSerialNumber() + "_" + new Random().nextInt(10000);
        String key = newName + "." + type;
        String cloudPath = OssKit.init().upload(key, fileByte);
        if (cloudPath.equals("ERROR")){
            throw new Exception("上传失败！");
        }
        return cloudPath;
    }

    public static String uploadInputStream(InputStream inputStream,String name,String type)
    {
        if (inputStream==null)
            return null;

        String newName= StrKit.notBlank(name)?getDictionary(type)+"/"+name:getDictionary(type) + "/" +  DateKit.getSerialNumber() + "_" + new Random().nextInt(10000);
        String key = newName + "." + type;
        String cloudPath = OssKit.init().upload(key,inputStream);
        if (cloudPath.equals("ERROR")){
            LogKit.error("上传失败");
            return null;
        }
        return cloudPath;
    }

    public static String getResUrl(String url){
        String resDomainName = SysMsg.Config.get("RES_DOMAIN_NAME");
        String urls[]=url.split("/");
        String temp="";
        for (int i=3;i<urls.length;i++){
            temp=temp+"/"+urls[i];
        }
        return resDomainName+temp;
    }

    public static boolean isAcceptImg(String fileSuffix){
        return SysMsg.Config.get("IMAGE_SUFFIX").contains(fileSuffix.toLowerCase());
    }

    public static boolean isAcceptAudio(String fileSuffix){
        return SysMsg.Config.get("AUDIO_SUFFIX").contains(fileSuffix.toLowerCase());
    }

    public static String getFileSuffix(String fileName){
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public static String getFileName(String fileName){
        return fileName.substring(0,fileName.lastIndexOf("."));
    }

    public static void main(String[] args){
//        System.out.println(getResUrl("http://online-songs.oss-cn-beijing.aliyuncs.com/image/201807211501022_5798.png"));
//        for (int i=0;i<1000;i++){
//            System.out.println(new Random().nextInt(78)+1);
//        }
        getThumbnail(null);

    }

    /**
     * 根据文件后缀名获取文件存放目录
     * @param type
     */
    public static String getDictionary(String type){
        String dictionary="other";
        if(".png,.jpg,.jpeg".contains(type)){
            dictionary="image";
        }else if(".mp3".contains(type)){
            dictionary="video";
        }
        return dictionary;
    }

    public static boolean checkSize(HttpServletRequest request){
        int length=request.getContentLength();
        if(length>SysMsg.Config.getInt("UPLOAD_MAX")){
            return false;
        }
        return true;
    }

    public static InputStream getThumbnail(InputStream inputStream){
        InputStream in=null;
        try {
            BufferedImage bufferedImage=Thumbnails.of(inputStream).scale(1f).outputQuality(0.25f).asBufferedImage();
            long b=new Date().getTime();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            in = new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return in;
    }

}
