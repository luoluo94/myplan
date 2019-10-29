package com.guima.base.service;

import com.guima.base.kits.ModelWrapper;
import com.guima.base.kits.OsResult;
import com.guima.base.kits.QueryParam;
import com.guima.base.kits.SysMsg;
import com.guima.kits.Constant;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.guima.cache.CacheModel;
import com.guima.cache.RedisCacheManager;
import com.guima.kits.Kit;
import com.guima.kits.MapKit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BaseService_<M extends Model<M>>
{
    public Class<?> getClass(List<M> mList)
    {
        if (mList.isEmpty()) return null;

        return mList.get(0).getClass();
    }

    /**
     * 创建并持久化一个具体的对象
     *
     * @param map 该对象的参数，无需指定id
     * @return OsResult
     */
    @SuppressWarnings("unchecked")
    public boolean create(Map<String, String[]> map)
    {
        M m = getConreteObject();
        if(!map.containsKey("id") || Kit.strTrim(MapKit.getValueFromMap(map, "id")).equals(""))
        {
            String id = UUID.randomUUID().toString();
            MapKit.setValueToMap(map, "id", id);
        }
        MapKit.setValueToMap(map, "status", Constant.ACTIVE);
        ModelWrapper<M> wrapper = getWrapper(m);
        wrapper.fill(map);
        return new CacheModel<>(m).save();
    }

    /**
     * 更新指定的对象
     *
     * @param map 更新对象的参数，必需含有id字段
     * @return OsResult
     */
    public boolean update(Map<String, String[]> map) throws Exception
    {
        M m = findById(map);
        if (m != null)
        {
            ModelWrapper<M> wrapper = new ModelWrapper<>(m);
            wrapper.fill(map);
            return new CacheModel<>(m).update();
        }
        return false;
    }

    /**
     * 删除指定的对象
     *
     * @param map 必需含有id字段，该字段可能为数组，支持批量删除
     * @return OsResult
     */
    public OsResult<M> delete(Map<String, String[]> map) throws Exception
    {
        OsResult<M> result = new OsResult<>();
        final String[] idArray = map.get("id");
        boolean success = Db.tx(() -> {
            for (String id : idArray)
            {
                M m = findById(id);
                if (m == null || !new CacheModel<>(m).delete())
                    return false;
            }
            return true;
        });
        result.set(success, null);
        return result;
    }

    /**
     * 以组装好的查询条件，对当前Service中的Model进行查询
     *
     * @param param 查询及排序条件，若为空则查询所有记录且无排序
     * @return Model集合
     */
    @SuppressWarnings("unchecked")
    public List<M> list(QueryParam param)
    {
        param = param == null ? QueryParam.Builder() : param;
        String tabName = Kit.camel2Line(getDao().getClass().getSimpleName());
        if (getEnableCache())
            return RedisCacheManager.instance().list(getCacheName(), param);
        else
        {
            String sql = param.genSql(tabName);
            return getDao().find(sql, param.getParams());
        }
    }

    public List<M> list(QueryParam param,String resultFields)
    {
        param = param == null ? QueryParam.Builder() : param;
        if(Kit.isNotNull(resultFields)){
            param.setResultField(resultFields);
        }
        String tabName = Kit.camel2Line(getDao().getClass().getSimpleName());
        if (getEnableCache())
            return RedisCacheManager.instance().list(getCacheName(), param);
        else
        {
            String sql = param.genSql(tabName);
            return getDao().find(sql, param.getParams());
        }
    }

    public List<String> listOnlyId(QueryParam param)
    {
        List<M> ms=list(param,"id");
        List<String> ids=new ArrayList<>();
        for (M m :ms){
            ids.add(m.get("id"));
        }
        return ids;
    }

    /**
     * 以组装好的查询条件，查找当前Service中第一个符合条件的Model
     *
     * @param param 查询及排序条件，若为空则查询所有记录且无排序
     * @return Model
     */
    @SuppressWarnings("unchecked")
    public M findFirst(QueryParam param)
    {
        param = param == null ? QueryParam.Builder() : param;
        String tabName = Kit.camel2Line(getDao().getClass().getSimpleName());
        if (getEnableCache())
            return (M) RedisCacheManager.instance().findFirst(getCacheName(), param);
        else
        {
            String sql = param.genSql(tabName);
            return getDao().findFirst(sql, param.getParams());
        }
    }

    /**
     * 以指定的查询条件，返回分页后的对象列表
     *
     * @param param         查询及排序条件，若为空则查询所有记录且无排序
     * @param pageNumberStr 第几页
     * @param pageSizeStr   每页多少条数据
     * @return 以Page封装的对象列表，通过Page的若干Setter方法可以获取到分页数据等相关信息
     */
    @SuppressWarnings("unchecked")
    public Page<M> pageList(QueryParam param, String pageNumberStr, String pageSizeStr)
    {
        param = param == null ? QueryParam.Builder() : param;
        String tabName = Kit.camel2Line(getDao().getClass().getSimpleName());
        String sql = param.genSql(tabName);

        int pageNumber = Kit.isNull(pageNumberStr) ? 1 : Integer.parseInt(pageNumberStr);
        int pageSize = Kit.isNull(pageSizeStr) ? 10 : Integer.parseInt(pageSizeStr);

        if (getEnableCache())
            return (Page<M>) RedisCacheManager.instance().pageList(getCacheName(), param, pageNumber, pageSize);
        else
        {
            String sqlExceptSelect = sql.replaceFirst("select \\*", "");
            String select = "select *";
            return getDao().paginate(pageNumber, pageSize, select, sqlExceptSelect, param.getParams());
        }
    }

    /**
     * 以指定的id查询对象
     *
     * @param id 对象id
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public M findById(String id)
    {
        M m = null;
        if (Kit.isNotNull(id))
        {
            M dao = getDao();
            m = getEnableCache() ? (M) RedisCacheManager.instance().findById(getCacheName(), id)
                    : dao.findById(id);
        }
        return m;
    }

    /**
     * 以指定的Map查询对象
     *
     * @param map 必需含有id字段
     * @return 对象
     */
    public M findById(Map<String, String[]> map)
    {
        String id = MapKit.getValueFromMap(map, "id");
        return findById(id);
    }

    /**
     * 获取具体的对象，用于create操作
     */
    protected abstract M getConreteObject();

    /**
     * 获取对象的包裹器，用于让调用者返回个性化的json对象等操作，一般用于findById后返回详细的对象信息
     */
    public abstract ModelWrapper<M> getWrapper(M m);

    /**
     * 获取列表对象的包裹器，用于list()、pageList()后渲染返回给客户端的对象，子service若有特殊需求应覆盖该方法
     */
    public ModelWrapper<M> getListWrapper(M m)
    {
        return new ModelWrapper<>(m);
    }

    /**
     * 获取Model的数据源，用于findById和list操作
     */
    public abstract M getDao();

    /**
     * 获取对象导出excel时需要忽略的字段，空列表为全部导出
     *
     * @return 忽略列表，值为数据库中的字段名称
     */
    public List<String> getExcelIgnore()
    {
        List<String> ignore = new ArrayList<>();
        ignore.add("ssds");
        ignore.add("ssxj");
        ignore.add("ssbz");
        ignore.add("ssxl");
        ignore.add("sbzb");
        return ignore;
    }

    // 缓存 ----------------------------------------------------------------------
    protected boolean enableCache = false;

    public void setEnableCache()
    {
        this.enableCache = true;
    }

    public void disableCache()
    {
        this.enableCache = false;
    }

    public boolean getEnableCache()
    {
        return SysMsg.Config.getBoolean("use_cache") && this.enableCache;
    }

    private String getCacheName()
    {
        String tabName = Kit.camel2Line(getDao().getClass().getSimpleName());
        // TODO 应该保持下划线命名法，但需要与Service保持一致
        return tabName.contains("_") ? tabName.replaceAll("_", "") : tabName;
    }

}
