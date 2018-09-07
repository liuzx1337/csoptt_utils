package com.csoptt.utils.jedis;

/**
 * Redis配置
 *
 * <p>通常可以通过注入等方式，为此类的各个静态变量赋值，并且加载到Redis连接池中</p>
 *
 * <b>redisHost、redisPort 必配，否则无法连接</b>
 * <b>redisPassword 可配</b>
 * <b>redisDbnum 可配，默认0</b>
 * <b>maxIdle、maxTotal、maxWait、testOfBorrow、timeout 可配，均有默认值</b>
 *
 * @see JedisUtil
 *
 * @author qishao
 * @date 2018-09-06
 */
public class RedisConfig {

    /**
     * redis服务器ip地址
     */
    public static String redisHost;

    /**
     * redis服务器端口
     */
    public static int redisPort;

    /**
     * redis连接密码
     */
    public static String redisPassword;

    /**
     * redis库编号
     */
    public static int redisDbnum;

    /**
     * redis最大idle
     */
    public static int maxIdle;

    /**
     * redis最大总连接数
     */
    public static int maxTotal;

    /**
     * redis最大超时等待时间
     */
    public static long maxWait;

    /**
     * redis是否进行有效性检查
     */
    public static boolean testOnBorrow = true;

    /**
     * redis连接池中连接超时时间
     */
    public static int timeout;
}
