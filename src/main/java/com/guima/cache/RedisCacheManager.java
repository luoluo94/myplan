package com.guima.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.guima.base.kits.QueryParam;
import com.guima.base.kits.SysMsg;
import com.guima.base.service.BaseService_;
import com.guima.base.service.ServiceManager;
import com.guima.kits.Kit;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisCacheManager<M extends Model> {
	
	private static RedisCacheManager _this;

	private static JedisPool jedisPool = null;
	
	//redis操作--------------------------------------------------------------------
	/**
	 * 构建redis连接池
	 * @return
	 */
	public static JedisPool getPool() {
        if (jedisPool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
            config.setMaxTotal(Integer.valueOf(SysMsg.Redis.get("maxActive")));
            //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
            config.setMaxIdle(Integer.valueOf(SysMsg.Redis.get("maxIdle")));
            //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
            config.setMaxWaitMillis(Integer.valueOf(SysMsg.Redis.get("maxWait")));
            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
            config.setTestOnBorrow(true);
            jedisPool = new JedisPool(config, SysMsg.Redis.get("redis.ip"), SysMsg.Redis.getInt("redis.port"));
        }
        return jedisPool;
    }
	
	public void reloadAllCache()
    {
		fullAll();
        Collection<BaseService_> services = ServiceManager.instance().getAllService();
        services.forEach(this::loadCache);
    }
	
	public  static void fullAll() {
    	Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();            
            jedis.flushAll();                     
            
        } catch (Exception e) {
        	 e.getMessage();
        } finally {
        	jedisPool.returnResource(jedis);
        }
    }
	
	/**
     * 添加redis缓存
     *
     * @param key
     * @param value
     * @param timeout 过期时间秒,如果小于等于0，则永不过期
     * @return 
     */
    public  static void setObject(String key, Object value, Integer timeout) {
    	Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();            
            jedis.set(key.getBytes(), SerializeUtil.serialize(value));
            if(timeout > 0){
            	jedis.expire(key.getBytes(), timeout);        
            }
                     
            
        } catch (Exception e) {
        	 e.getMessage();
        } finally {
        	jedisPool.returnResource(jedis);
        }
    }
    
    /**
     * 获取redis缓存的值
     *
     * @param key
     * @return
     */
    public static  Object getObject(String key) {
    	Object value = null;
    	Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            value = SerializeUtil.unserialize(jedis.get(key.getBytes()));
        } catch (Exception e) {
        	 e.getMessage();
        } finally {
        	jedisPool.returnResource(jedis);
        }
        return value;
    }
    
    /**
     * 判断指定的KEY是否存在
     * 
     * @param key
     * @return
     */
    public static boolean exists(String key) {
    	Jedis jedis = null;
    	try {
    		jedis = jedisPool.getResource();
    		return jedis.exists(key);
    	} catch(Exception e) {
    		e.getMessage();
    	} finally {
    		jedisPool.returnResource(jedis);
    	}
    	return false;
    }
    
    //缓存操作 --------------------------------------------------------------------
    public static RedisCacheManager instance() {
    	if(_this ==null){
    		getPool();    		
    		_this = new RedisCacheManager();
    	}
    	return _this;
    }
    
    private RedisCacheManager(){
    	Collection<BaseService_> services = ServiceManager.instance().getAllService();
    	services.forEach(this::loadCache);
    }
    
    @SuppressWarnings("unchecked")
    public List<M> getCache(String cacheName)
    {
    	return (List<M>) getObject(cacheName);
    }
    
    public boolean hasCache(String cacheName)
    {
    	return exists(cacheName);
    }
    
    public void loadCache(BaseService_ service)
    {
        if (service.getEnableCache())
        {
            Model dao = service.getDao();
            String tabName = Kit.camel2Line(dao.getClass().getSimpleName());
            long millis = System.currentTimeMillis();
            String cacheName = tabName.contains("_") ? tabName.replaceAll("_", "") : tabName;
            setObject(cacheName, dao.find(QueryParam.Builder().genSql("s_"+tabName)), 0);

            millis = System.currentTimeMillis() - millis;
            LogKit.info("加载缓存" + tabName + "完成，共用时" + millis + "毫秒");
        }
    }
    
 // 缓存查询 --------------------------------------------------------------------
    public List<M> list(String cacheName, QueryParam param)
    {
        List<M> cacheData = getCache(cacheName);
        if (cacheData == null)
            return new ArrayList<>();
        CacheQueryFilter filter = new CacheQueryFilter();
        filter.buildQueryChain(param);
        filter.buildSort(param.getOrderItems());
        return filter.startChain(cacheData);
    }

    public Page<M> pageList(String cacheName, QueryParam param, int pageNum, int pageSize)
    {
        List<M> list = list(cacheName, param);
        if (list.isEmpty()) return new Page<>(new ArrayList<>(), 0, 0, 0, 0);

        pageNum = pageNum < 1 ? 1 : pageNum;
        pageSize = pageSize <= 0 ? 10 : pageSize;
        int totalPage = (list.size() + pageSize - 1) / pageSize;
        int startIdx = (pageNum - 1) * pageSize;
        int endIdx = pageNum * pageSize;
        if (endIdx >= (list.size() + 1))
        {
            startIdx = (totalPage - 1) * pageSize;
            endIdx = list.size();
            pageNum = totalPage;
        }
        List<M> pageList = list.subList(startIdx, endIdx);
        return new Page<>(pageList, pageNum, pageSize, totalPage, list.size());
    }

    public M findFirst(String cacheName, QueryParam param)
    {
        List<M> mList = list(cacheName, param);
        return mList.isEmpty() ? null : mList.get(0);
    }

    public M findById(String cacheName, String id)
    {
        QueryParam param = QueryParam.Builder().equalsTo("id", id);
        return findFirst(cacheName, param);
    }

    // 增删操作，更新缓存 ---------------------------------------------------------------
    public void regNewCache(String cacheName, M m)
    {
    	List<M> list = getCache(cacheName);
        if (findById(cacheName, m.getStr("id")) == null){        	
        	list.add(m);
        }else{
            for (int i = 0;i < list.size(); i++)
            {
                M m1 = list.get(i);
                if (m1.getStr("id").equals(m.getStr("id")))
                {
                	list.set(i, m);
                	break;
                }
            }
        }
        setObject(cacheName, list, 0);
    }

    public void removeCaches(String cacheName, String[] idArray)
    {
        List<M> cacheData = getCache(cacheName);
        if (cacheData == null || cacheData.isEmpty()) return;

        Iterator<M> it = cacheData.iterator();
        while (it.hasNext())
        {
            M m = it.next();
            Optional<String> optional = Arrays.stream(idArray)
                    .filter(id -> id.equals(m.getStr("id"))).findFirst();
            if (optional.isPresent())
                it.remove();
        }
        setObject(cacheName, cacheData, 0);
    }
	
}
