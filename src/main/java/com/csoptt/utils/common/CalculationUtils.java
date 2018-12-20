package com.csoptt.utils.common;

import java.math.BigDecimal;

/**
 * 提供精确计算小数的工具类
 *
 * @author qishao
 * @date 2018-09-06
 */
public final class CalculationUtils {
    /**
     * 无法创建对象
     */
    private CalculationUtils() {
    }

    /**
     * 加法运算
     * @param d1 第一个数
     * @param d2 第二个数
     * @return
     * @author qishao
     * date 2018-09-06
     */
    public static double add(double d1, double d2) {
        BigDecimal b1 = BigDecimal.valueOf(d1);
        BigDecimal b2 = BigDecimal.valueOf(d2);
        return b1.add(b2).doubleValue();
    }
    
    /**
     * 减法运算
     * @param d1 第一个数
     * @param d2 第二个数
     * @return 
     * @author qishao
     * date 2018-09-06
     */
    public static double subtract(double d1, double d2) {
        BigDecimal b1 = BigDecimal.valueOf(d1);
        BigDecimal b2 = BigDecimal.valueOf(d2);
        return b1.subtract(b2).doubleValue();
    }
    
    /**
     * 乘法运算
     * @param d1
     * @param d2
     * @return 
     * @author qishao
     * date 2018-09-06
     */
    public static double multiply(double d1, double d2) {
        BigDecimal b1 = BigDecimal.valueOf(d1);
        BigDecimal b2 = BigDecimal.valueOf(d2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 除法运算
     * 1.0.0版本只提供4位小数的精确度
     *
     * @param d1
     * @param d2
     * @return
     * @author qishao
     * date 2018-09-06
     */
    public static double divide(double d1, double d2) {
        if (d2 == 0) {
            throw new IllegalArgumentException("分母为0，无法进行除法运算");
        }
        BigDecimal b1 = BigDecimal.valueOf(d1);
        BigDecimal b2 = BigDecimal.valueOf(d2);
        return b1.divide(b2, 4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 大小比较
     * @param d1
     * @param d2
     * @return
     * @author qishao
     * date 2018-09-06
     */
    public static boolean equals(double d1, double d2) {
        return Double.doubleToLongBits(d1) == Double.doubleToLongBits(d2);
    }
}
