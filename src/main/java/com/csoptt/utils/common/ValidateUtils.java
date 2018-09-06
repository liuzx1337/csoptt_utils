package com.csoptt.utils.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证工具类
 *
 * @author qishao
 * @date 2018-09-06
 */
public class ValidateUtils {
    /**
     * 无法创建对象
     */
    private ValidateUtils() {
    }
    
    /**
     * 验证一个字符串是否符合指定的正则表达式
     * @param regex
     * @param string
     * @return 
     * @author qishao
     * date 2018-09-06
     */
    public static boolean match(String regex, String string) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }
    
    /**
     * 验证一个字符串是否符合数字格式
     * @param string
     * @return 
     * @author qishao
     * date 2018-09-06
     */
    public static boolean isNumber(String string) {
        String numberRegex = "^([+-]?)\\d*\\.?\\d+$";
        return match(numberRegex, string);
    }
    
    /**
     * 验证一个字符串是否是整数格式
     * @param string
     * @return 
     * @author qishao
     * date 2018-09-06
     */
    public static boolean isInt(String string) {
        String intRegex = "^([+-]?)\\d+$";
        return match(intRegex, string);
    }
    
    /**
     * 验证一个字符串是否是邮箱格式
     * @param string
     * @return 
     * @author qishao
     * date 2018-09-06
     */
    public static boolean isEmail(String string) {
        String emailRegex = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
        return match(emailRegex, string);
    }
    
    /**
     * 验证一个字符串是否是字母格式
     * @param string
     * @return 
     * @author qishao
     * date 2018-09-06
     */
    public static boolean isLetter(String string) {
        String letterRegex = "^[A-Za-z]+$";
        return match(letterRegex, string);
    }
    
    /**
     * 验证一个字符串是否是大写字母格式
     * @param string
     * @return 
     * @author qishao
     * date 2018-09-06
     */
    public static boolean isUppercaseLetter(String string) {
        String uppercaseLetterRegex = "^[A-Z]+$";
        return match(uppercaseLetterRegex, string);
    }
    
    /**
     * 验证一个字符串是否是小写字母格式
     * @param string
     * @return 
     * @author qishao
     * date 2018-09-06
     */
    public static boolean isLowercaseLetter(String string) {
        String lowercaseLetterRegex = "^[a-z]+$";
        return match(lowercaseLetterRegex, string);
    }
    
    /**
     * 验证一个字符串是否是手机号格式
     * @param string
     * @return 
     * @author qishao
     * date 2018-09-06
     */
    public static boolean isMobilePhone(String string) {
        String mobilePhoneRegex = "^(1)[0-9]{10}$";
        return match(mobilePhoneRegex, string);
    }
    
    /**
     * 验证一个字符串是否是中文格式
     * @param string
     * @return 
     * @author qishao
     * date 2018-09-06
     */
    public static boolean isChinese(String string) {
        String chineseRegex = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$";
        return match(chineseRegex, string);
    }
}
