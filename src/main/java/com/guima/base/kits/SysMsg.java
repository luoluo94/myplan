package com.guima.base.kits;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

public class SysMsg
{
    public static Prop Config = PropKit.use("config.properties");
    public static Prop OsMsg = PropKit.use("osmsg.txt");
    public static Prop SqlList = PropKit.use("sql.txt");
    public static Prop Ctrl = PropKit.use("controller.properties");
    public static Prop Redis = PropKit.use("redis.properties");
    public static Prop SqlConfig = PropKit.use("SqlConfig.properties");

    public static String get(String fileName, String key)
    {
        return PropKit.use(fileName).get(key);
    }
}
