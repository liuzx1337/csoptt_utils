package com.csoptt.utils.jedis;

import com.csoptt.utils.common.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.SortingParams;
import redis.clients.util.SafeEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
     * Log4j
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisUtil.class);

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
        jedisPoolConfig.setMaxTotal(RedisConfig.maxTotal == 0 ? 500 : RedisConfig.maxTotal);
        jedisPoolConfig.setMaxIdle(RedisConfig.maxIdle == 0 ? 5 : RedisConfig.maxIdle);
        jedisPoolConfig.setMaxWaitMillis(RedisConfig.maxWait == 0L ? 100000L : RedisConfig.maxWait);
        jedisPoolConfig.setTestOnBorrow(RedisConfig.testOnBorrow);

        // 判断是否有密码
        if (StringUtils.isNotBlank(RedisConfig.redisPassword)) {
            jedisPool = new JedisPool(jedisPoolConfig,
                    RedisConfig.redisHost,
                    RedisConfig.redisPort,
                    RedisConfig.timeout == 0 ? 10000 : RedisConfig.timeout,
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
     * 将jedis返回连接池
     * @param jedis
     * @return 
     * @author qishao
     * date 2018-09-07
     */
    public static void returnBrokenResource(Jedis jedis) {
        if (null != jedis && null != jedisPool) {
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
            String status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.flushAll();
            } catch (Exception e) {
                LOGGER.error("Flush keys failed.", e);
                status = "-1";
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
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
            String status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.rename(oldKey, newKey);
            } catch (Exception e) {
                LOGGER.error("Rename failed.", e);
                status = "-1";
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
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
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.renamenx(oldKey, newKey);
            } catch (Exception e) {
                LOGGER.error("Rename failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
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
            long time;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                time = jedis.ttl(key);
            } catch (Exception e) {
                LOGGER.error("Get ttl failed.", e);
                time = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
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
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.expire(key, seconds);
            } catch (Exception e) {
                LOGGER.error("Set key's expire time failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
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
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.expireAt(key, timestamp);
            } catch (Exception e) {
                LOGGER.error("Set key's expire time failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
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
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.persist(key);
            } catch (Exception e) {
                LOGGER.error("Delete key's expire time failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
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
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.del(keys);
            } catch (Exception e) {
                LOGGER.error("Delete keys failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
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
            boolean flag;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                flag = jedis.exists(key);
            } catch (Exception e) {
                LOGGER.error("Judge key exists failed.", e);
                flag = false;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
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
            List<String> list;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                list = jedis.sort(key);
            } catch (Exception e) {
                LOGGER.error("Sort keys failed.", e);
                list = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
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
            List<String> list;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                list = jedis.sort(key, params);
            } catch (Exception e) {
                LOGGER.error("Sort keys failed.", e);
                list = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
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
            String type;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                type = jedis.type(key);
            } catch (Exception e) {
                LOGGER.error("Check key's type failed.", e);
                type = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
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
            Set<String> set;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                set = jedis.keys(pattern);
            } catch (Exception e) {
                LOGGER.error("Get Keys failed.", e);
                set = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return set;
        }
    }

    /**
     * 对应redis中的String
     */
    public class Strings {
        
        /**
         * 获取指定key的值
         * @param key
         * @return
         * @author qishao
         * date 2018-09-28
         */
        public String get(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            String value;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                value = jedis.get(key);
            } catch (Exception e) {
                LOGGER.error("Get String value failed.", e);
                value = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return value;
        }
        
        /**
         * 获取所有指定key的值
         * @param keys
         * @return
         * @author qishao
         * date 2018-09-28
         */
        public List<String> mget(String... keys) {
            Jedis jedis = JedisUtil.this.getJedis();
            List<String> values;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                values = jedis.mget(keys);
            } catch (Exception e) {
                LOGGER.error("Get values failed.", e);
                values = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return values;
        }

        /**
         * 设置给定key的值
         * @param key
         * @param value
         * @return
         * @author qishao
         * date 2018-09-28
         */
        public String set(String key, String value) {
            Jedis jedis = JedisUtil.this.getJedis();
            String status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.set(key, value);
            } catch (Exception e) {
                LOGGER.error("Set key's value failed.", e);
                status = "-1";
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }
        
        /**
         * 设置给定key的值
         * 与命令set相比，此命令不会覆盖原有key
         * @param key
         * @param value
         * @return
         * @author qishao
         * date 2018-09-28
         */
        public long setnx(String key, String value) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.setnx(key, value);
            } catch (Exception e) {
                LOGGER.error("Set key's value failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 为指定的key设置值及其过期时间
         * @param key
         * @param value
         * @param seconds
         * @return 
         * @author qishao
         * date 2018-09-28
         */
        public String setex(String key, String value, int seconds) {
            Jedis jedis = JedisUtil.this.getJedis();
            String status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.setex(key, seconds, value);
            } catch (Exception e) {
                LOGGER.error("Setex key's value failed.", e);
                status = "-1";
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 为多个key赋值
         * @param keysvalues
         * @return
         * @author qishao
         * date 2018-09-28
         */
        public String mset(String... keysvalues) {
            Jedis jedis = JedisUtil.this.getJedis();
            String status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.mset(keysvalues);
            } catch (Exception e) {
                LOGGER.error("Set key-values failed.", e);
                status = "-1";
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 为多个key赋值。入参为map
         * @param map
         * @return
         * @author qishao
         * date 2018-09-28
         */
        public String mset(Map<String, String> map) {
            if (CollectionUtils.isEmpty(map)) {
                LOGGER.error("No key-values");
                return null;
            }
            String[] keyvalues = new String[map.size() * 2];

            List<String> keyvalueList = new ArrayList<>();
            map.forEach((key, value) -> {
                keyvalueList.add(key);
                keyvalueList.add(value);
            });
            return mset(keyvalueList.toArray(keyvalues));
        }
        
        /**
         * 获取key对应的字符串，并根据左右偏移量截取（均为闭区间，和java.lang.String不同）
         * @see java.lang.String
         * @param key
         * @param start
         * @param end
         * @return
         * @author qishao
         * date 2018-09-28
         */
        public String getrange(String key, long start, long end) {
            Jedis jedis = JedisUtil.this.getJedis();
            String subValue;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                subValue = jedis.getrange(key, start, end);
            } catch (Exception e) {
                LOGGER.error("Get subString failed.", e);
                subValue = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return subValue;
        }

        /**
         * 用指定的字符串覆盖给定key所储存的字符串值，覆盖的位置从偏移量offset开始。
         * @param key
         * @param value
         * @param offset
         * @return
         * @author qishao
         * date 2018-09-28
         */
        public long setrange(String key, String value, long offset) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.setrange(key, offset, value);
            } catch (Exception e) {
                LOGGER.error("Setrange key's value failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 设置指定key的值，并返回key的旧值。
         * 如果key对应的类型不是String，返回null
         * @param key
         * @param value
         * @return
         * @author qishao
         * date 2018-09-28
         */
        public String getSet(String key, String value) {
            Jedis jedis = JedisUtil.this.getJedis();
            String oldValue;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                oldValue = jedis.getSet(key, value);
            } catch (Exception e) {
                LOGGER.error("GetSet key failed.", e);
                oldValue = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return oldValue;
        }

        /**
         * 为指定的key对应的值追加
         * 如果key原来不存在，此命令效果同set
         * @param key
         * @param suffix
         * @return
         * @author qishao
         * date 2018-09-28
         */
        public long append(String key, String suffix) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.append(key, suffix);
            } catch (Exception e) {
                LOGGER.error("Append key failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 为key对应的值增加一个数字
         * 如果值不能被解析为数字，返回-1
         * @param key
         * @param number
         * @return
         * @author qishao
         * date 2018-09-28
         */
        public long incrBy(String key, long number) {
            Jedis jedis = JedisUtil.this.getJedis();
            long len;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                len = jedis.incrBy(key, number);
            } catch (Exception e) {
                LOGGER.error("Increase key's value failed", e);
                len = -1L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return len;
        }

        /**
         * 为key对应的值减去一个数字
         * 如果值不能被解析为数字，返回-1
         * @param key
         * @param number
         * @return
         * @author qishao
         * date 2018-09-28
         */
        public long decrBy(String key, long number) {
            Jedis jedis = JedisUtil.this.getJedis();
            long len;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                len = jedis.decrBy(key, number);
            } catch (Exception e) {
                LOGGER.error("Decrease key's value failed", e);
                len = -1L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return len;
        }

        /**
         * 获取key存储的字符串长度。
         * @param key
         * @return 
         * @author qishao
         * date 2018-09-28
         */
        public long strlen(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            long len;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                len = jedis.strlen(key);
            } catch (Exception e) {
                LOGGER.error("Get string's length failed.", e);
                len = -1;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return len;
        }
    }

    /**
     * 对应redis中的List
     */
    public class Lists {

        /**
         * 获取key对应列表的长度
         * @param key
         * @return
         * @author qishao
         * date 2018-09-27
         */
        public long llen(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            long len;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                len = jedis.llen(key);
            } catch (Exception e) {
                LOGGER.error("Get list's length failed.", e);
                len = -1L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return len;
        }

        /**
         * 在列表内的某元素前或者后插入元素
         * @param key
         * @param isAfter true为元素pivot之后，false为之前
         * @param pivot 表内的已有某元素
         * @param value
         * @return 
         * @author qishao
         * date 2018-09-27
         */
        public long linsert(String key, boolean isAfter, String pivot, String value) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            BinaryClient.LIST_POSITION position = isAfter ? BinaryClient.LIST_POSITION.AFTER
                    : BinaryClient.LIST_POSITION.BEFORE;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.linsert(key, position, pivot, value);
            } catch (Exception e) {
                LOGGER.error("Insert member failed.", e);
                status = -1L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 通过索引来设置元素的值
         * @param key
         * @param index
         * @param value
         * @return 
         * @author qishao
         * date 2018-09-27
         */
        public String lset(String key, long index, String value) {
            Jedis jedis = JedisUtil.this.getJedis();
            String status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.lset(key, index, value);
            } catch (Exception e) {
                LOGGER.error("Set element's value failed.", e);
                status = "-1";
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }
        
        /**
         * 通过索引获取列表中的元素
         * @param key
         * @param index
         * @return 
         * @author qishao
         * date 2018-09-27
         */
        public String lindex(String key, int index) {
            Jedis jedis = JedisUtil.this.getJedis();
            String value;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                value = jedis.lindex(key, index);
            } catch (Exception e) {
                LOGGER.error("Get element failed.", e);
                value = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return value;
        }
        
        /**
         * 移除并返回列表的第一个元素
         * @param key
         * @return
         * @author qishao
         * date 2018-09-27
         */
        public String lpop(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            String value;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                value = jedis.lpop(key);
            } catch (Exception e) {
                LOGGER.error("Delete first element failed.", e);
                value = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return value;
        }

        /**
         * 移除并返回列表的最后一个元素
         * @param key
         * @return
         * @author qishao
         * date 2018-09-27
         */
        public String rpop(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            String value;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                value = jedis.rpop(key);
            }catch (Exception e){
                LOGGER.error("Delete last elememt failed.", e);
                value = null;
            }finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return value;
        }

        /**
         * 将一个或多个值插入到列表头部
         * @param key
         * @param values
         * @return 
         * @author qishao
         * date 2018-09-27
         */
        public long lpush(String key, String... values) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.lpush(key, values);
            } catch (Exception e) {
                LOGGER.error("Push elements to first failed.", e);
                status = -1L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }
        
        /**
         * 将一个或多个值插入到列表尾部
         * @param key
         * @param values
         * @return
         * @author qishao
         * date 2018-09-27
         */
        public long rpush(String key, String... values) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.rpush(key, values);
            } catch (Exception e) {
                LOGGER.error("Push elements to last failed.", e);
                status = -1L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 返回列表中指定区间内的元素
         * @param key
         * @param start
         * @param end
         * @return
         * @author qishao
         * date 2018-09-27
         */
        public List<String> lrange(String key, long start, long end) {
            Jedis jedis = JedisUtil.this.getJedis();
            List<String> list;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                list = jedis.lrange(key, start, end);
            } catch (Exception e) {
                LOGGER.error("Get elements between " + start + " and " + end + " failed.", e);
                list = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return list;
        }

        /**
         * 移除列表中与参数 VALUE 相等的元素
         * @param key
         * @param count 大于0：从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT
         *              小于0：从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值
         *              等于0：移除表中所有与 VALUE 相等的值
         * @param value
         * @return
         * @author qishao
         * date 2018-09-27
         */
        public long lrem(String key, int count, String value) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.lrem(key, count, value);
            } catch (Exception e) {
                LOGGER.error("Delete elements failed.", e);
                status = -1L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 让列表只保留指定区间内的元素
         * @param key
         * @param start
         * @param end
         * @return 
         * @author qishao
         * date 2018-09-27
         */
        public String ltrim(String key, int start, int end) {
            Jedis jedis = JedisUtil.this.getJedis();
            String status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.ltrim(key, start, end);
            } catch (Exception e) {
                LOGGER.error("Trim list failed.", e);
                status = "-1";
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }
    }

    /**
     * 对应redis中的set
     */
    public class Sets {

        /**
         * 向对应key的set中增加成员
         * @param key
         * @param member
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public long sadd(String key, String... member) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.sadd(key, member);
            } catch (Exception e) {
                LOGGER.error("Add member failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 获取此key对应set中元素的数量
         * @param key
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public long scard(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            long count;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                count = jedis.scard(key);
            } catch (Exception e) {
                LOGGER.error("Get count failed", e);
                count = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return count;
        }

        /**
         * 返回给定集合之间的差集
         * @param keys
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public Set<String> sdiff(String... keys) {
            Jedis jedis = JedisUtil.this.getJedis();
            Set<String> set;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                set = jedis.sdiff(keys);
            } catch (Exception e) {
                LOGGER.error("Get difference failed.", e);
                set = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return set;
        }

        /**
         * 将给定集合之间的差集，存入一个新的key中
         * @param newKey
         * @param keys
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public long sdiffstore(String newKey, String... keys) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.sdiffstore(newKey, keys);
            } catch (Exception e) {
                LOGGER.error("Store difference failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }
        
        /**
         * 返回给定集合之间的交集
         * @param keys
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public Set<String> sinter(String... keys) {
            Jedis jedis = JedisUtil.this.getJedis();
            Set<String> set;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                set = jedis.sinter(keys);
            } catch (Exception e) {
                LOGGER.error("Get inter failed.", e);
                set = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return set;
        }

        /**
         * 将给定集合之间的交集，存入一个新的key中
         * @param newKey
         * @param keys
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public long sinterstore(String newKey, String... keys) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.sinterstore(newKey, keys);
            } catch (Exception e) {
                LOGGER.error("Store inter failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 判断成员元素是否是此key对应集合的成员
         * @param key
         * @param member
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public boolean sismember(String key, String member) {
            Jedis jedis = JedisUtil.this.getJedis();
            boolean flag;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                flag = jedis.sismember(key, member);
            } catch (Exception e) {
                LOGGER.error("Judge is member failed.", e);
                flag = false;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return flag;
        }

        /**
         * 返回该key对应集合
         * @param key
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public Set<String> smembers(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            Set<String> set;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                set = jedis.smembers(key);
            } catch (Exception e) {
                LOGGER.error("Get members failed.", e);
                set = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return set;
        }
        
        /**
         * 将指定成员从fromKey对应集合移动到toKey对应集合
         * @param fromKey
         * @param toKey
         * @param member
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public long smove(String fromKey, String toKey, String member) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.smove(fromKey, toKey, member);
            } catch (Exception e) {
                LOGGER.error("Move member failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 移除并返回集合中的一个随机元素
         * @param key
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public String spop(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            String member;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                member = jedis.spop(key);
            } catch (Exception e) {
                LOGGER.error("Delete any member failed.", e);
                member = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return member;
        }

        /**
         * 移除集合中的一个或多个成员元素
         * @param key
         * @param members
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public long srem(String key, String... members) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.srem(key, members);
            } catch (Exception e) {
                LOGGER.error("Delete members failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }
        
        /**
         * 返回给定集合之间的并集
         * @param keys
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public Set<String> sunion(String... keys) {
            Jedis jedis = JedisUtil.this.getJedis();
            Set<String> set;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                set = jedis.sunion(keys);
            } catch (Exception e) {
                LOGGER.error("Get union failed.", e);
                set = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return set;
        }

        /**
         * 将给定集合之间的并集，存入一个新的key中
         * @param newKey
         * @param keys
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public long sunionstore(String newKey, String... keys) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.sunionstore(newKey, keys);
            } catch (Exception e) {
                LOGGER.error("Store union failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }
    }

    /**
     * 对应redis中的sortset
     */
    public class SortSet {
        
        /**
         * 将一个成员及其分数值加入key对应的有序集合
         * @param key
         * @param score
         * @param member
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public long zadd(String key, double score, String member) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.zadd(key, score, member);
            } catch (Exception e) {
                LOGGER.error("Add score failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }
        
        /**
         * 将多个成员及其分数值加入key对应的有序集合
         * @param key
         * @param scoreMembers
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public long zadd(String key, Map<String, Double> scoreMembers) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.zadd(key, scoreMembers);
            } catch (Exception e) {
                LOGGER.error("Add score failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }
        
        /**
         * 获取此key对应有序集合中元素数量
         * @param key
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public long zcard(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            long count;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                count = jedis.zcard(key);
            } catch (Exception e) {
                LOGGER.error("Get member count failed.", e);
                count = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return count;
        }
        
        /**
         * 获取此key对应有序集合中，指定分数区间的元素数量
         * @param key
         * @param min
         * @param max
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public long zcount(String key, double min, double max) {
            Jedis jedis = JedisUtil.this.getJedis();
            long count;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                count = jedis.zcount(key, min, max);
            } catch (Exception e) {
                LOGGER.error("Get count failed.", e);
                count = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return count;
        }

        /**
         * 改变指定集合中的某一成员的分数
         * @param key
         * @param increment
         * @param member
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public double zincrby(String key, double increment, String member) {
            Jedis jedis = JedisUtil.this.getJedis();
            double score;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                score = jedis.zincrby(key, increment, member);
            } catch (Exception e) {
                LOGGER.error("Change member's score failed.", e);
                score = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return score;
        }

        /**
         * 返回有序集中，指定区间内的成员（升序）
         * @param key
         * @param start
         * @param end
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public Set<String> zrange(String key, int start, int end) {
            Jedis jedis = JedisUtil.this.getJedis();
            Set<String> set;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                set = jedis.zrange(key, start, end);
            } catch (Exception e) {
                LOGGER.error("Get members between " + start + " and " + end + " failed.", e);
                set = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return set;
        }

        /**
         * 返回有序集中，指定区间内的成员（降序）
         * @param key
         * @param start
         * @param end
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public Set<String> zrevrange(String key, int start, int end) {
            Jedis jedis = JedisUtil.this.getJedis();
            Set<String> set;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                set = jedis.zrevrange(key, start, end);
            } catch (Exception e) {
                LOGGER.error("Get members between " + start + " and " + end + " failed.", e);
                set = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return set;
        }

        /**
         * 返回有序集中，指定分数区间内的成员
         * @param key
         * @param min
         * @param max
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public Set<String> zrangeByScore(String key, double min, double max) {
            Jedis jedis = JedisUtil.this.getJedis();
            Set<String> set;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                set = jedis.zrangeByScore(key, min, max);
            } catch (Exception e) {
                LOGGER.error("Get members score between " + min + " and " + max + " failed.", e);
                set = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return set;
        }
        
        /**
         * 返回有序集中指定成员的分数排名（升序）
         * 查询错误，返回-1
         * @param key
         * @param member
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public long zrank(String key, String member) {
            Jedis jedis = JedisUtil.this.getJedis();
            long rank;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                rank = jedis.zrank(key, member);
            } catch (Exception e) {
                LOGGER.error("Get member's rank failed.", e);
                rank = -1L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return rank;
        }
        
        /**
         * 返回有序集中指定成员的分数排名（降序）
         * @param key
         * @param member
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public long zrevrank(String key, String member) {
            Jedis jedis = JedisUtil.this.getJedis();
            long rank;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                rank = jedis.zrevrank(key, member);
            } catch (Exception e) {
                LOGGER.error("Get member's rank failed.", e);
                rank = -1L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return rank;
        }

        /**
         * 移除有序集合中指定的成员
         * @param key
         * @param members
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public long zrem(String key, String... members) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.zrem(key, members);
            } catch (Exception e) {
                LOGGER.error("Remove members failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }
        
        /**
         * 移除有序集合中指定排名区间的成员
         * @param key
         * @param start
         * @param end
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public long zremrangeByRank(String key, int start, int end) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.zremrangeByRank(key, start, end);
            } catch (Exception e) {
                LOGGER.error("Remove members between " + start + " and " + end + " failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 移除有序集合中指定分数区间的成员
         * @param key
         * @param min
         * @param max
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public long zremrangeByScore(String key, double min, double max) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.zremrangeByScore(key, min, max);
            } catch (Exception e) {
                LOGGER.error("Remove members between " + min + " and " + max + " failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }
        
        /**
         * 返回有序集中，成员的分数值
         * 如果没有分数，返回0
         * @param key
         * @param member
         * @return
         * @author qishao
         * date 2018-09-07
         */
        public double zscore(String key, String member) {
            Jedis jedis = JedisUtil.this.getJedis();
            Double score;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                score = jedis.zscore(key, member);
            } catch (Exception e) {
                LOGGER.error("Check member's score failed.", e);
                score = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return score != null ? score : 0.0D;
        }
    }

    /**
     * 对应redis中的hash
     */
    public class Hash {

        /**
         * 删除hash中指定field对应的键值对
         * @param key
         * @param field
         * @return 状态
         * @author qishao
         * date 2018-09-25
         */
        public long hdel(String key, String... field) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.hdel(key, field);
            } catch (Exception e) {
                LOGGER.error("Delete fields failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 删除指定key对应的hash
         * @param key
         * @return
         * @author qishao
         * date 2018-09-25
         */
        public long hdel(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.hdel(key);
            } catch (Exception e) {
                LOGGER.error("Delete hash failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }
        
        /**
         * 判断一个key对应的hash内，某一字段是否存在
         * @param key
         * @param field
         * @return 
         * @author qishao
         * date 2018-09-27
         */
        public boolean hexists(String key, String field) {
            Jedis jedis = JedisUtil.this.getJedis();
            boolean flag;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                flag = jedis.hexists(key, field);
            } catch (Exception e) {
                LOGGER.error("Judge field exists failed.", e);
                flag = false;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return flag;
        }

        /**
         * 获取一个key中一个field对应的value
         * @param key
         * @param field
         * @return
         * @author qishao
         * date 2018-09-27
         */
        public String hget(String key, String field) {
            Jedis jedis = JedisUtil.this.getJedis();
            String result;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                result = jedis.hget(key, field);
            } catch (Exception e) {
                LOGGER.error("Get value failed.", e);
                result = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return result;
        }

        /**
         * 获取hash的所有键值对
         * @param key
         * @return
         * @author qishao
         * date 2018-09-27
         */
        public Map<String, String> hgetAll(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            Map<String, String> resultMap;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                resultMap = jedis.hgetAll(key);
            } catch (Exception e) {
                LOGGER.error("Get key-values failed.", e);
                resultMap = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return resultMap;
        }
        
        /**
         * 从一个hash中获取所有的key
         * @param key
         * @return 
         * @author qishao
         * date 2018-09-27
         */
        public Set<String> hkeys(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            Set<String> keys;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                keys = jedis.hkeys(key);
            } catch (Exception e) {
                LOGGER.error("Get keys failed.", e);
                keys = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return keys;
        }

        /**
         * 从一个hash中获取所有的value
         * @param key
         * @return
         * @author qishao
         * date 2018-09-27
         */
        public List<String> hvals(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            List<String> values;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                values = jedis.hvals(key);
            } catch (Exception e) {
                LOGGER.error("Get values failed.", e);
                values = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return values;
        }
        
        /**
         * 从hash中获取多个字段的值
         * @param key
         * @param fields
         * @return 
         * @author qishao
         * date 2018-09-27
         */
        public List<String> hmget(String key, String... fields) {
            Jedis jedis = JedisUtil.this.getJedis();
            List<String> values;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                values = jedis.hmget(key, fields);
            } catch (Exception e) {
                LOGGER.error("Get values failed.", e);
                values = null;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return values;
        }
        
        /**
         * 向hash中插入/改变值
         * @param key
         * @param field
         * @param value
         * @return
         * @author qishao
         * date 2018-09-27
         */
        public long hset(String key, String field, String value) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.hset(key, field, value);
            } catch (Exception e) {
                LOGGER.error("Set key-value failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }
        
        /**
         * 向hash中插入多个键值对
         * @param key
         * @param map
         * @return
         * @author qishao
         * date 2018-09-27
         */
        public String hmset(String key, Map<String, String> map) {
            Jedis jedis = JedisUtil.this.getJedis();
            String status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.hmset(key, map);
            } catch (Exception e) {
                LOGGER.error("Set key-values failed.", e);
                status = "-1";
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 向hash中为不存在的字段赋值
         * @param key
         * @param field
         * @param value
         * @return
         * @author qishao
         * date 2018-09-27
         */
        public long hsetnx(String key, String field, String value) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.hsetnx(key, field, value);
            } catch (Exception e) {
                LOGGER.error("Setnx key-value failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }

        /**
         * 为hash中的字段值加上指定增量值
         * @param key
         * @param field
         * @param incr
         * @return 
         * @author qishao
         * date 2018-09-27
         */
        public long hincrby(String key, String field, long incr) {
            Jedis jedis = JedisUtil.this.getJedis();
            long status;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                status = jedis.hincrBy(key, field, incr);
            } catch (Exception e) {
                LOGGER.error("increase value failed.", e);
                status = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return status;
        }
        
        /**
         * 获取hash中字段数量
         * @param key
         * @return 
         * @author qishao
         * date 2018-09-27
         */
        public long hlen(String key) {
            Jedis jedis = JedisUtil.this.getJedis();
            long len;
            try {
                jedis.select(RedisConfig.redisDbnum); // 选择库
                len = jedis.hlen(key);
            } catch (Exception e) {
                LOGGER.error("Get len failed.", e);
                len = 0L;
            } finally {
                JedisUtil.this.returnJedis(jedis);
            }
            return len;
        }
    }
}
