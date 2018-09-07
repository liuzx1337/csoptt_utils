package com.csoptt.utils.jedis;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.SortingParams;
import redis.clients.util.SafeEncoder;

import java.util.List;
import java.util.Set;

/**
 * Redis相关工具类
 * 单例模式
 *
 * @author qishao
 * @date 2018-09-06
 */
public class JedisUtil {

    /**
     * singleton
     */
    private static final JedisUtil jedisUtil;

    /**
     * redis连接池
     */
    private static JedisPool jedisPool;

    /*
     * 将Redis连接池的属性，配置到连接池这个对象中
     */
    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(RedisConfig.maxTotal);
        jedisPoolConfig.setMaxIdle(RedisConfig.maxIdle);
        jedisPoolConfig.setMaxWaitMillis(RedisConfig.maxWait);
        jedisPoolConfig.setTestOnBorrow(RedisConfig.testOnBorrow);

        // 判断是否有密码
        if (StringUtils.isNotBlank(RedisConfig.redisPassword)) {
            jedisPool = new JedisPool(jedisPoolConfig,
                    RedisConfig.redisHost,
                    RedisConfig.redisPort,
                    RedisConfig.timeout,
                    RedisConfig.redisPassword);
        } else {
            jedisPool = new JedisPool(jedisPoolConfig,
                    RedisConfig.redisHost,
                    RedisConfig.redisPort);
        }

        jedisUtil = new JedisUtil();
    }

    private JedisUtil() {
    }

    /**
     * 获取唯一实例
     * @return the value of jedisUtil
     * @author qishao
     * date 2018-09-07
     */
    public static JedisUtil getInstance() {
        return jedisUtil;
    }

    /**
     * Gets the value of jedisPool.
     * @return
     * @author qishao
     * date 2018-09-07
     */
    public JedisPool getJedisPool() {
        return jedisPool;
    }

    /**
     * 获取jedis
     * @return
     * @author qishao
     * @date 2018-09-07
     */
    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 将jedis返回连接池
     * @param jedis
     * @author qishao
     * date 2018-09-07
     */
    public void returnJedis(Jedis jedis) {
        if (jedis != null && jedisPool != null) {
            jedis.close();
        }
    }

    /**
     * 对应Redis中的key
     *
     * @author qishao
     * date 2018-09-07
     */
    public class Keys {

        /**
         * 删除所有现有的数据库
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public String flushAll() {
            Jedis jedis = JedisUtil.this.getJedis();
            jedis.select(RedisConfig.redisDbnum); // 选择库
            String status = jedis.flushAll();
            JedisUtil.this.returnJedis(jedis);
            return status;
        }

        /**
         * 修改key的名称
         * @param oldKey
         * @param newKey
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public String rename(String oldKey, String newKey) {
            return rename(SafeEncoder.encode(oldKey), SafeEncoder.encode(newKey));
        }

        /**
         * 修改key的名称
         * @param oldKey
         * @param newKey
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public String rename(byte[] oldKey, byte[] newKey) {
            Jedis jedis = JedisUtil.this.getJedis();
            jedis.select(RedisConfig.redisDbnum); // 选择库
            String status = jedis.rename(oldKey, newKey);
            JedisUtil.this.returnJedis(jedis);
            return status;
        }

        /**
         * 新的key不存在时, 修改key的名称
         * @param oldKey
         * @param newKey
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public long renamenx(String oldKey, String newKey) {
            Jedis jedis = JedisUtil.this.getJedis();
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.renamenx(oldKey, newKey);
            JedisUtil.this.returnJedis(jedis);
            return status;
        }
        
        /**
         * 获取key到期的剩余时间（秒）
         * @param key
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public long ttl(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long time = jedis.ttl(key);
            JedisUtil.this.returnJedis(jedis);
            return time;
        }
        
        /**
         * 设置一个键的存活时间
         * 返回1代表成功，0代表失败
         * @param key
         * @param seconds
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public long expire(String key, int seconds) {
            Jedis jedis = JedisUtil.this.getJedis();
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.expire(key, seconds);
            JedisUtil.this.returnJedis(jedis);
            return status;
        }

        /**
         * 设置一个key的存活时间戳，到这个时间之后，key消失
         * 返回1代表成功，0代表失败
         * @param key
         * @param timestamp
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public long expireAt(String key, long timestamp) {
            Jedis jedis = JedisUtil.this.getJedis();
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.expireAt(key, timestamp);
            JedisUtil.this.returnJedis(jedis);
            return status;
        }
        
        /**
         * 删除key的过期时间，使此key永不过期
         * @param key
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public long persist(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.persist(key);
            JedisUtil.this.returnJedis(jedis);
            return status;
        }

        /**
         * 删除key
         * 可批量删除
         * @param keys
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public long del(String... keys) {
            Jedis jedis = JedisUtil.this.getJedis();
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.del(keys);
            JedisUtil.this.returnJedis(jedis);
            return status;
        }
        
        /**
         * 判断一个key是否存在
         * @param key
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public boolean exists(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            jedis.select(RedisConfig.redisDbnum); // 选择库
            boolean flag = jedis.exists(key);
            JedisUtil.this.returnJedis(jedis);
            return flag;
        }

        /**
         * 排序
         * @param key
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public List<String> sort(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            jedis.select(RedisConfig.redisDbnum); // 选择库
            List<String> list = jedis.sort(key);
            JedisUtil.this.returnJedis(jedis);
            return list;
        }

        /**
         * 排序
         * @param key
         * @param params
         * @return
         */
        public List<String> sort(String key, SortingParams params) {
            Jedis jedis = JedisUtil.this.getJedis();
            jedis.select(RedisConfig.redisDbnum); // 选择库
            List<String> list = jedis.sort(key, params);
            JedisUtil.this.returnJedis(jedis);
            return list;
        }

        /**
         * 查看key所存储的类型
         * @param key
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public String type(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            jedis.select(RedisConfig.redisDbnum); // 选择库
            String type = jedis.type(key);
            JedisUtil.this.returnJedis(jedis);
            return type;
        }

        /**
         * 模糊查询
         * @param pattern
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public Set<String> keys(String pattern) {
            Jedis jedis = JedisUtil.this.getJedis();
            jedis.select(RedisConfig.redisDbnum); // 选择库
            Set<String> set = jedis.keys(pattern);
            JedisUtil.this.returnJedis(jedis);
            return set;
        }
    }
}
