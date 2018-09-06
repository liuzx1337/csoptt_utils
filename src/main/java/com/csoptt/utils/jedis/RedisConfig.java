package com.csoptt.utils.jedis;

/**
 * Redis配置
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
    public static boolean testOnBorrow;
}
