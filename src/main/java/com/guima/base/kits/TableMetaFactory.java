package com.guima.base.kits;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.guima.kits.Kit;

import java.util.*;

public class TableMetaFactory
{
    private static TableMetaFactory _this = null;
    private Map<String, List<MetaInfo>> metaMap = null;

    private TableMetaFactory()
    {
        metaMap = new LinkedHashMap<>();
        List<Record> metas = Db.find(SysMsg.SqlList.get("GET_ALL_TABLE_METADATA"), OsConfig.domainName);
        for (Record rec : metas)
        {
            String tableName = StrKit.firstCharToUpperCase(StrKit.toCamelCase(rec.getStr("TABLE_NAME")));
            List<MetaInfo> tableMetas = metaMap.get(tableName);
            if (tableMetas == null)
                tableMetas = new ArrayList<>();
            MetaInfo info = new MetaInfo();
            String columnName = rec.getStr("COLUMN_NAME");
            if (columnName.equals("id")) continue;
            info.baseFiledName = columnName;
            info.fieldName = StrKit.toCamelCase(columnName);
            info.remark = Kit.strTrim(rec.getStr("COLUMN_COMMENT"));
            tableMetas.add(info);
            metaMap.put(tableName, tableMetas);
        }
    }

    public synchronized static TableMetaFactory instance()
    {
        if (_this == null)
            _this = new TableMetaFactory();
        return _this;
    }

    public List<MetaInfo> getTabMetaInfo(String tableName)
    {
        return metaMap.containsKey(tableName) ? metaMap.get(tableName) : new ArrayList<>();
    }

    public MetaInfo getMetaInfo(String tableName, String fieldName)
    {
        List<MetaInfo> metaInfos = metaMap.get(tableName);
        Optional<MetaInfo> optional = metaInfos.stream().filter(meta -> meta.fieldName.equals(fieldName))
                .findFirst();
        return optional.isPresent() ? optional.get() : null;
    }
}
