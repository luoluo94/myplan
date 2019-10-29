package com.guima.base.kits;

import com.guima.kits.Kit;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

public class QueryParam
{
    private List<QueryItem> items;
    private List<OrderItem> orderItems;
    private String resultFields;

    private QueryParam()
    {
        this.items = new ArrayList<>();
        this.orderItems = new ArrayList<>();
    }

    public static QueryParam Builder()
    {
        return new QueryParam();
    }

    // getter ------------------------------------------------------------------
    public List<QueryItem> getItems()
    {
        return items;
    }

    public List<OrderItem> getOrderItems()
    {
        return orderItems;
    }

    public String getResultFields()
    {
        return resultFields;
    }

    // 查询条件 --------------------------------------------------------------------

    /**
     * 设置查询结果列，默认为*，若指定，则为 select xxx,yyy from tab
     *
     * @param fields 结果列集合
     * @return QueryParam
     */
    public QueryParam setResultField(String fields)
    {
        this.resultFields = fields;
        return this;
    }

    /**
     * 相等
     *
     * @param field 字段
     * @param value 值
     * @return QueryParam
     */
    public QueryParam equalsTo(String field, String value)
    {
        addQueryItem(QueryType.EQUAL, field, value);
        return this;
    }

    /**
     * 不相等
     *
     * @param field 字段
     * @param value 值
     * @return QueryParam
     */
    public QueryParam notEquals(String field, String value)
    {
        addQueryItem(QueryType.NOT_EQUAL, field, value);
        return this;
    }

    /**
     * 任意匹配
     *
     * @param field 字段
     * @param value 值
     * @return QueryParam
     */
    public QueryParam like(String field, String value)
    {
        addQueryItem(QueryType.LIKE, field, value);
        return this;
    }

    /**
     * 以值开头的匹配
     *
     * @param field 字段
     * @param value 值
     * @return QueryParam
     */
    public QueryParam start(String field, String value)
    {
        addQueryItem(QueryType.START_WITH, field, value);
        return this;
    }

    /**
     * 以值结尾的匹配
     *
     * @param field 字段
     * @param value 值
     * @return QueryParam
     */
    public QueryParam end(String field, String value)
    {
        addQueryItem(QueryType.END_WITH, field, value);
        return this;
    }

    /**
     * 大于
     *
     * @param field 字段
     * @param value 值
     * @return QueryParam
     */
    public QueryParam gt(String field, String value)
    {
        addQueryItem(QueryType.GREAT_THAN, field, value);
        return this;
    }

    /**
     * 大于等于
     *
     * @param field 字段
     * @param value 值
     * @return QueryParam
     */
    public QueryParam gte(String field, String value)
    {
        addQueryItem(QueryType.GREAT_EQUAL_THAN, field, value);
        return this;
    }

    /**
     * 小于
     *
     * @param field 字段
     * @param value 值
     * @return QueryParam
     */
    public QueryParam lt(String field, String value)
    {
        addQueryItem(QueryType.LESS_THAN, field, value);
        return this;
    }

    /**
     * 小于等于
     *
     * @param field 字段
     * @param value 值
     * @return QueryParam
     */
    public QueryParam lte(String field, String value)
    {
        addQueryItem(QueryType.LESS_EQUAL_THAN, field, value);
        return this;
    }

    /**
     * 不等于空字符串
     *
     * @param field 字段
     * @return QueryParam
     */
    public QueryParam notNull(String field)
    {
        addQueryItem(QueryType.NOT_EQUAL, field, "");
        return this;
    }

    private void addQueryItem(QueryType type, String field, String value)
    {
        QueryItem item = new QueryItem();
        item.type = type;
        item.fieldName = field;
        item.value = value;
        items.add(item);
    }

    /**
     * in查询
     *
     * @param field  字段
     * @param values 值集合
     * @return QueryParam
     */
    public QueryParam in(String field, String[] values)
    {
        QueryItem item = new QueryItem();
        item.type = QueryType.IN;
        item.fieldName = field;
        item.values = values;
        items.add(item);
        return this;
    }

    /**
     * not in查询
     *
     * @param field  字段
     * @param values 值集合
     * @return QueryParam
     */
    public QueryParam notIn(String field, String[] values)
    {
        QueryItem item = new QueryItem();
        item.type = QueryType.NOT_IN;
        item.fieldName = field;
        item.values = values;
        items.add(item);
        return this;
    }

    /**
     * 子查询
     *
     * @param field      字段
     * @param subParam   子查询QueryParam对象
     * @param subTabName 子查询表名
     * @return QueryParam
     */
    public QueryParam sub(String field, QueryParam subParam, String subTabName)
    {
        QueryItem item = new QueryItem();
        item.type = QueryType.SUB_QUERY;
        item.fieldName = field;
        item.sub = subParam;
        item.subTab = subTabName;
        items.add(item);
        return this;
    }

    /**
     * 子查询
     *
     * @param field      字段
     * @param subParam   子查询QueryParam对象
     * @param subTabName 子查询表名
     * @return QueryParam
     */
    public QueryParam subNot(String field, QueryParam subParam, String subTabName)
    {
        QueryItem item = new QueryItem();
        item.type = QueryType.SUB_NOT_QUERY;
        item.fieldName = field;
        item.sub = subParam;
        item.subTab = subTabName;
        items.add(item);
        return this;
    }

    /**
     * or查询
     *
     * @param vals 值集合，索引0为field，索引1为val，以此类推，支持多组参数
     * @return QueryParam
     */
    public QueryParam or(String... vals)
    {
        if (vals == null || vals.length % 2 != 0)
            return this;

        QueryItem item = new QueryItem();
        item.type = QueryType.OR;
        item.orMap = new LinkedHashMap<>();
        for (int i = 0; i < vals.length; i = i + 2)
        {
            String field = vals[i];
            String val = vals[i + 1];
            item.orMap.put(field, val);
        }
        items.add(item);
        return this;
    }

    /**
     * or查询，每组均进行like匹配
     *
     * @param vals 值集合，索引0为field，索引1为val，以此类推，支持多组参数
     * @return QueryParam
     */
    public QueryParam orLike(String... vals)
    {
        if (vals == null || vals.length % 2 != 0)
            return this;

        QueryItem item = new QueryItem();
        item.type = QueryType.OR_LIKE;
        item.orMap = new LinkedHashMap<>();
        for (int i = 0; i < vals.length; i = i + 2)
        {
            String field = vals[i];
            String val = vals[i + 1];
            item.orMap.put(field, val);
        }
        items.add(item);
        return this;
    }
    
    /**
     * or查询，每组均进行in查询
     *
     * @param vals 值集合，索引0为field，索引1为val，以此类推，支持多组参数
     * @return QueryParam
     */
    public QueryParam orIn(String... vals)
    {
        if (vals == null || vals.length % 2 != 0)
            return this;

        QueryItem item = new QueryItem();
        item.type = QueryType.OR_IN;
        item.orMap = new LinkedHashMap<>();
        for (int i = 0; i < vals.length; i = i + 2)
        {
            String field = vals[i];
            String val = vals[i + 1];
            item.orMap.put(field, val);
        }
        items.add(item);
        return this;
    }

    /**
     * 检测是否含有指定字段的查询条件
     *
     * @param field 字段
     * @return 含有返回true，否则返回false
     */
    public boolean hasQueryItem(String field)
    {
        return !items.stream().filter(item -> Kit.strTrim(item.fieldName).equals(field))
                .collect(Collectors.toList()).isEmpty();
    }

    /**
     * 检测是否含有指定字段开头的查询条件
     *
     * @param field 字段
     * @return 含有返回true，否则返回false
     */
    public boolean containsQueryItem(String field)
    {
        return !items.stream().filter(item -> item.fieldName.startsWith(field))
                .collect(Collectors.toList()).isEmpty();
    }

    /**
     * 从查询条件列表中移除一个条件
     *
     * @param field 字段
     * @return 被移除的条件，若找不到该条件，返回null
     */
    public QueryItem removeItem(String field)
    {
        Optional<QueryItem> optional = items.stream().filter(i -> i.fieldName.equals(field)).findFirst();
        if (optional.isPresent())
        {
            QueryItem item = optional.get();
            items.remove(item);
            return item;
        }
        return null;
    }

    /**
     * 从查询条件列表中移除一个以field开头的条件
     *
     * @param field 字段
     * @return 被移除的条件，若找不到该条件，返回null
     */
    public QueryItem removeItemLike(String field)
    {
        Optional<QueryItem> optional = items.stream().filter(i -> i.fieldName.startsWith(field)).findFirst();
        if (optional.isPresent())
        {
            QueryItem item = optional.get();
            items.remove(item);
            return item;
        }
        return null;
    }

    /**
     * 获取一个field的value，仅对key/value查询条件有效
     *
     * @param field 字段
     * @return 值，不存在返回空字符串
     */
    public String getQueryValue(String field)
    {
        Optional<QueryItem> optional = items.stream().filter(i -> i.fieldName.startsWith(field)).findFirst();
        return optional.isPresent() ? Kit.strTrim(optional.get().value) : "";
    }

    // 排序条件 --------------------------------------------------------------------

    /**
     * 正序排列
     *
     * @param field 字段
     * @return QueryParam
     */
    public QueryParam ascBy(String field)
    {
        if (!hasOrder(field))
        {
            OrderItem item = new OrderItem();
            item.type = OrderType.ASC;
            item.fieldName = field;
            orderItems.add(item);
        }
        return this;
    }

    /**
     * 倒序排列
     *
     * @param field 字段
     * @return QueryParam
     */
    public QueryParam descBy(String field)
    {
        if (!hasOrder(field))
        {
            OrderItem item = new OrderItem();
            item.type = OrderType.DESC;
            item.fieldName = field;
            orderItems.add(item);
        }
        return this;
    }

    private boolean hasOrder(String field)
    {
        return !orderItems.stream().filter(order -> order.fieldName.equals(field))
                .collect(Collectors.toList()).isEmpty();
    }

    // -------------------------------------------------------------------------

    /**
     * 生成预执行的sql语句
     *
     * @param tabName 表格名称
     * @return 预执行sql语句
     */
    public String genSql(String tabName)
    {
        tabName = Kit.camel2Line(tabName);
        StringBuilder sb = new StringBuilder("select ");
        int idx = 0;
        if (Kit.isNotNull(resultFields))
        {
            sb.append(resultFields);
        } else
            sb.append("*");
        sb.append(" from ").append(tabName);
        if (!items.isEmpty())
            sb.append(" where ");
        idx = 0;
        for (QueryItem item : items)
        {
            if (idx > 0)
                sb.append("and ");
            sb.append(item.fieldName == null ? "" : item.fieldName).append(item.getTypeSymbol());
            if (item.type != QueryType.SUB_QUERY && item.type != QueryType.SUB_NOT_QUERY
                    && item.type != QueryType.IN && item.type != QueryType.NOT_IN
                    && item.type != QueryType.OR && item.type != QueryType.OR_LIKE
                    && item.type != QueryType.OR_IN)
                sb.append("? ");
            idx++;
        }

        idx = 0;
        for (OrderItem item : orderItems)
        {
            if (idx == 0)
                sb.append(" order by ");
            else
                sb.append(",");
            sb.append(item.fieldName).append(item.getTypeSymbol());
            idx++;
        }

        return sb.toString();
    }

    /**
     * 获取预执行sql语句的参数列表
     *
     * @return 参数列表
     */
    public Object[] getParams()
    {
        List<Object> params = new ArrayList<>();
        for (QueryItem item : items)
        {
            Object[] values = item.getValue();
            Collections.addAll(params, values);
        }
        return params.toArray(new Object[params.size()]);
    }

    // 内部对象 --------------------------------------------------------------------
    public enum QueryType
    {
        EQUAL(" = "), NOT_EQUAL(" != "),
        LIKE(" like "), START_WITH(" like "), END_WITH(" like "),
        GREAT_THAN(" > "), GREAT_EQUAL_THAN(" >= "),
        LESS_THAN(" < "), LESS_EQUAL_THAN(" <= "),
        SUB_QUERY(" in "), SUB_NOT_QUERY(" not in "), IN(" in "), NOT_IN(" not in "),
        OR(" "), OR_LIKE(" "), OR_IN(" ");
        private String name;

        QueryType(String name)
        {
            this.name = name;
        }

        String getValue(QueryType type, String value)
        {
            String result = value;
            switch (type)
            {
                case LIKE:
                    result = "%" + value + "%";
                    break;
                case START_WITH:
                    result = value + "%";
                    break;
                case END_WITH:
                    result = "%" + value;
                    break;
            }
            return result;
        }
    }

    public class QueryItem
    {
        public QueryType type;
        // 普通查询条件，键/值对即可
        public String fieldName;
        public String value;
        // in查询，值为数组
        public String[] values;
        // 子查询，需要子查询QueryParam与子查询表名
        public QueryParam sub;
        public String subTab;
        // or查询，键/值对列表
        public Map<String, String> orMap;

        String getTypeSymbol()
        {
            String typeName = type.name;
            if (type == QueryType.SUB_QUERY || type == QueryType.SUB_NOT_QUERY) // 子查询处理
            {
                String subSql = sub.genSql(subTab);
                typeName += "(" + subSql + ")";
            } else if (type == QueryType.IN || type == QueryType.NOT_IN) // IN查询处理
            {
                StringBuilder args = new StringBuilder();
                for (int i = 0; i < values.length; ++i)
                {
                    if (i > 0)
                        args.append(",");
                    args.append("?");
                }
                typeName += "(" + args.toString() + ") ";
            } else if (type == QueryType.OR || type == QueryType.OR_LIKE || type == QueryType.OR_IN)
            {
                typeName = "";
            	if (orMap != null && orMap.size() > 0)
                {
                    StringBuilder args = new StringBuilder();
                    int idx = 0;
                    for (String key : orMap.keySet())
                    {
                    	if(type == QueryType.OR_IN){
                    		StringBuilder args1 = new StringBuilder();
                    		for (int i = 0; i < orMap.get(key).split(",").length; ++i)
                            {
                                if (i > 0)
                                	args1.append(",");
                                args1.append("?");
                            }
                    		if (idx > 0)
                                args.append(" or ");
                            args.append(key);
                            args.append(" in ");
                            args.append("(" + args1.toString() + ") ");
                            idx++;
                    	}else{
                    		if (idx > 0)
                                args.append(" or ");
                            args.append(key);
                            args.append(type == QueryType.OR ? " = " : " like ");
                            args.append("?");
                            idx++;
                    	}
                    }
                    typeName += "(" + args.toString() + ") ";
                }
            }
            return typeName;
        }

        Object[] getValue()
        {
            Object[] result = new Object[]{};
            if (type == QueryType.SUB_QUERY || type == QueryType.SUB_NOT_QUERY)
                result = sub.getParams();
            else if (type == QueryType.IN || type == QueryType.NOT_IN)
                result = values;
            else if (type == QueryType.OR)
            {
                if (orMap != null && orMap.size() > 0)
                    result = orMap.values().toArray();
            } else if (type == QueryType.OR_LIKE)
            {
                List<String> orParams = orMap.values().stream().map(val -> "%" + val + "%")
                        .collect(Collectors.toList());
                result = orParams.toArray();
            } else if (type == QueryType.OR_IN)
            {
                List<String[]> orParams = orMap.values().stream().map(val -> val.split(","))
                        .collect(Collectors.toList());
                String[] tmp = new String[]{};
                for(String[] p:orParams)
                	tmp = ArrayUtils.addAll(tmp, p);
                result = tmp;
            } else
                result = new String[]{type.getValue(type, value)};
            return result;
        }
    }

    public enum OrderType
    {
        ASC(" asc"), DESC(" desc");
        private String name;

        OrderType(String name)
        {
            this.name = name;
        }
    }

    public class OrderItem
    {
        public String fieldName;
        public OrderType type;

        String getTypeSymbol()
        {
            return type.name;
        }
    }
}
