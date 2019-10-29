package com.guima.kits;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.guima.base.kits.SysMsg;

public class TreeKit
{
    public static String[] generateTreePathAndCode(boolean isRoot, String tabName, String parentCode)
            throws Exception
    {
        return isRoot ? generateRoot(tabName) : generateChild(tabName, parentCode);
    }

    private static String[] generateChild(String tabName, String parentCode) throws Exception
    {
        Record parent = findTreeNodeByCode(tabName, parentCode);
        if (parent == null)
        {
            String msg = String.format(SysMsg.OsMsg.get("PARENT_TREE_NODE_NOT_FOUND"), parentCode);
            throw new Exception(msg);
        }

        String sql = String.format(SysMsg.SqlList.get("GET_CHILD_TREE_NODE_MAX_CODE"), tabName);
        String maxCode = Db.find(sql, parentCode + "%", parentCode).get(0).getStr("maxCode");
        Record treeNode = findTreeNodeByCode(tabName, maxCode);
        String[] result;
        if (treeNode == null)
            result = new String[]{parent.getStr("tree_path") + ".1", parent.getStr("code") + "01"};
        else
        {
            String curCode = treeNode.getStr("code").replaceFirst(parent.getStr("code"), "");
            int code = Integer.parseInt(curCode) + 1;
            result = new String[]{treeNode.getStr("tree_path"), parentCode + String.format("%02d", code)};
        }
        return result;
    }

    private static Record findTreeNodeByCode(String tabName, String code)
    {
        String sql = String.format(SysMsg.SqlList.get("GET_TREE_NODE_CODE"), tabName);
        return Db.findFirst(sql, code);
    }

    private static String[] generateRoot(String tabName)
    {
        String sql = String.format(SysMsg.SqlList.get("GET_ROOT_TREE_NODE_MAX_CODE"), tabName);
        String maxCode = Db.find(sql).get(0).getStr("maxCode");
        Record treeNode = findTreeNodeByCode(tabName, maxCode);
        String[] result;
        if (treeNode == null)
            result = new String[]{"1", "01"};
        else
        {
            int code = Integer.parseInt(treeNode.getStr("code")) + 1;
            int treePath = Integer.parseInt(treeNode.getStr("tree_path")) + 1;
            result = new String[]{String.valueOf(treePath), String.format("%02d", code)};
        }
        return result;
    }
}
