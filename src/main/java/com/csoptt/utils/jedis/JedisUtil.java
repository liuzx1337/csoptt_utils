package com.csoptt.utils.jedis;

import org.apache.commons.lang3.StringUtils;
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.sadd(key, member);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long count = jedis.scard(key);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            Set<String> set = jedis.sdiff(keys);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.sdiffstore(newKey, keys);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            Set<String> set = jedis.sinter(keys);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.sinterstore(newKey, keys);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            boolean flag = jedis.sismember(key, member);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            Set<String> set = jedis.smembers(key);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.smove(fromKey, toKey, member);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            String member = jedis.spop(key);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.srem(key, members);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            Set<String> set = jedis.sunion(keys);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.sunionstore(newKey, keys);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.zadd(key, score, member);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.zadd(key, scoreMembers);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long count = jedis.zcard(key);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long count = jedis.zcount(key, min, max);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            double score = jedis.zincrby(key, increment, member);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            Set<String> set = jedis.zrange(key, start, end);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            Set<String> set = jedis.zrevrange(key, start, end);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            Set<String> set = jedis.zrangeByScore(key, min, max);
            JedisUtil.this.returnJedis(jedis);
            return set;
        }
        
        /**
         * 返回有序集中指定成员的分数排名（升序）
         * @param key
         * @param member
         * @return 
         * @author qishao
         * date 2018-09-07
         */
        public long zrank(String key, String member) {
            Jedis jedis = JedisUtil.this.getJedis();
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long rank = jedis.zrank(key, member);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long rank = jedis.zrevrank(key, member);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.zrem(key, members);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.zremrangeByRank(key, start, end);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            long status = jedis.zremrangeByScore(key, min, max);
            JedisUtil.this.returnJedis(jedis);
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
            jedis.select(RedisConfig.redisDbnum); // 选择库
            Double score = jedis.zscore(key, member);
            JedisUtil.this.returnJedis(jedis);
            return score != null ? score : 0.0D;
        }
    }
}
