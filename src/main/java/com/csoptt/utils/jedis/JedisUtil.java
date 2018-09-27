package com.csoptt.utils.jedis;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.SortingParams;
import redis.clients.util.SafeEncoder;

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
         * @author liuzixi
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
         * @author liuzixi
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
         * @author liuzixi
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
         * @author liuzixi
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
         * @author liuzixi
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
         * @author liuzixi
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
         * @author liuzixi
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
