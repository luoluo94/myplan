package com.guima.cache;

import com.jfinal.plugin.activerecord.Model;
import com.guima.base.kits.QueryParam;
import com.guima.cache.queries.*;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections4.comparators.ComparatorChain;

import java.util.*;

class CacheQueryFilter
{
    private List<ICacheQueryItem> queryItemChain = new LinkedList<>();
    private List<CacheOrderItem> orderItems = new LinkedList<>();

    @SuppressWarnings("unchecked")
    <M extends Model> List<M> startChain(List<M> cacheData)
    {
        List<M> chainData = cacheData;
        for (ICacheQueryItem queryItem : queryItemChain)
        {
            chainData = queryItem.doChain(chainData);
        }
        if (!orderItems.isEmpty())
        {
            Comparator descComparator = ComparableComparator.getInstance();
            descComparator = ComparatorUtils.nullLowComparator(descComparator);
            descComparator = ComparatorUtils.reversedComparator(descComparator);
            List sortFields = new ArrayList<>();
            for(CacheOrderItem orderItem : orderItems)
            {
                BeanComparator<M> comparator = orderItem.isDesc() ?
                        new BeanComparator<>(orderItem.getFieldName(), descComparator) :
                        new BeanComparator<>(orderItem.getFieldName());
                sortFields.add(comparator);
            }
            if(!sortFields.isEmpty())
            {
                ComparatorChain multiSort = new ComparatorChain(sortFields);
                Collections.sort(chainData, multiSort);
            }
        }
        return chainData;
    }

    // 查询条件 --------------------------------------------------------------------
    void buildQueryChain(QueryParam param)
    {
        for (QueryParam.QueryItem item : param.getItems())
        {
            if (item.type == QueryParam.QueryType.EQUAL)
                queryItemChain.add(new EqualItem(item));
            else if (item.type == QueryParam.QueryType.NOT_EQUAL)
                queryItemChain.add(new NotEqualsItem(item));
            else if (item.type == QueryParam.QueryType.LIKE)
                queryItemChain.add(new LikeItem(item));
            else if (item.type == QueryParam.QueryType.START_WITH)
                queryItemChain.add(new StartWithItem(item));
            else if (item.type == QueryParam.QueryType.END_WITH)
                queryItemChain.add(new EndWithItem(item));
            else if (item.type == QueryParam.QueryType.GREAT_THAN)
                queryItemChain.add(new GreatThenItem(item));
            else if (item.type == QueryParam.QueryType.GREAT_EQUAL_THAN)
                queryItemChain.add(new GreatEqualThenItem(item));
            else if (item.type == QueryParam.QueryType.LESS_THAN)
                queryItemChain.add(new LessThenItem(item));
            else if (item.type == QueryParam.QueryType.LESS_EQUAL_THAN)
                queryItemChain.add(new LessEqualThenItem(item));
            else if (item.type == QueryParam.QueryType.IN)
                queryItemChain.add(new InItem(item));
            else if (item.type == QueryParam.QueryType.NOT_IN)
                queryItemChain.add(new NotInItem(item));
            else if (item.type == QueryParam.QueryType.OR)
                queryItemChain.add(new OrItem(item));
            else if (item.type == QueryParam.QueryType.OR_LIKE)
                queryItemChain.add(new OrLikeItem(item));
            else if (item.type == QueryParam.QueryType.SUB_QUERY)
                queryItemChain.add(new SubQueryItem(item));
        }
    }

    // 排序条件 --------------------------------------------------------------------
    void buildSort(List<QueryParam.OrderItem> orderItems)
    {
        if (!orderItems.isEmpty())
        {
            orderItems.stream().forEach(o -> this.orderItems.add(new CacheOrderItem(o.fieldName,
                    o.type == QueryParam.OrderType.DESC)));
        }
    }
}
