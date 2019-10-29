package com.guima.base.service;

import com.guima.services.UserService;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.guima.base.kits.OsConfig;
import com.guima.base.kits.SysMsg;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServiceManager
{
    private static ServiceManager _this = null;
    private Map<String, BaseService_> services;

    private ServiceManager()
    {
        this.services = new LinkedHashMap<>();
        String sPath = PathKit.getRootClassPath() + "/com/guima/services";
        String packageName = "com.guima.services";
        File f = new File(sPath);
        if (f.isDirectory())
        {
            File[] allFile = f.listFiles();

            for (File service : allFile)
            {
                buildService(packageName, service, "");
            }
        }
    }

    private void buildService(String packageName, File service, String baseName)
    {
        String fileName = service.getName();
        if (service.isFile())
        {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            if (!fileName.endsWith("Service"))
                return;
            String sName = packageName + "." + fileName;
            try
            {
                Class cls = Class.forName(sName);
                String key = fileName.replaceAll("Service", "").toLowerCase();
                BaseService_ s = (BaseService_) cls.newInstance();
                this.services.put(key, s);
            } catch (Exception e)
            {
                String msg = String.format(SysMsg.OsMsg.get("SORRY_SERVICE_NOT_FOUND"), sName);
                LogKit.error(msg);
            }
        } else
        {
            baseName = "/" + fileName;
            packageName += "." + fileName;
            File[] sub = service.listFiles();
            for (File subFile : sub)
            {
                buildService(packageName, subFile, baseName);
            }
        }
    }

    public synchronized static ServiceManager instance()
    {
        if (_this == null)
            _this = new ServiceManager();
        return _this;
    }

    public BaseService_ getService(String serviceName)
    {
        if (services.containsKey(serviceName))
            return services.get(serviceName);
        return null;
    }

    /**
     * 使一个Service服务启用Cache
     *
     * @param serviceName service名称
     */
    public void useCache(String serviceName)
    {
        BaseService_ service = services.get(serviceName);
        if (service != null)
            service.setEnableCache();
    }

    /**
     * 获取所有Service服务
     */
    public Collection<BaseService_> getAllService()
    {
        return services.values();
    }
}
