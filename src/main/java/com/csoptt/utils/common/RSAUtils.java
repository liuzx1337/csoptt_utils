package com.csoptt.utils.common;

import com.csoptt.utils.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA非对称加密解密算法
 * 基于一套公私钥，对一定长度的明文进行加密解密处理的工具类
 *
 * 可使用字符串而不是密钥文件进行加密解密操作
 *
 * @author qishao
 * @date 2018-11-16
 */
public final class RSAUtils {

    /**
     * Log4j
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RSAUtils.class);

    /**
     * 确定算法为RSA
     */
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 密钥长度，DH算法的默认密钥长度是1024
     * 密钥长度必须是64的倍数，在512到65536位之间
     */
    private static final int KEY_SIZE = 1024;

    /**
     * 无法生成对象
     */
    private RSAUtils() {
    }

    /**
     * 初始化密钥对
     * 可随机生成一套成对的公私密钥
     *
     * @return
     * @author qishao
     * date 2018-11-16
     */
    public static Map<String, Object> initKey() throws Exception {
        //实例化RSA密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        //初始化密钥生成器
        keyPairGenerator.initialize(KEY_SIZE);
        //生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        //公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        //私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //将这对密钥存储在map中
        Map<String, Object> keyMap = new HashMap<String, Object>();
        keyMap.put("RSAPublicKey", publicKey);
        keyMap.put("RSAPrivateKey", privateKey);
        return keyMap;
    }

    /**
     * 公钥加密源数据
     *
     * @param data 源数据
     *             如果需要对字符串进行加密，可直接使用getBytes()方法
     *             @see String#getBytes()
     * @param publicKeyBytes 字节格式的key
     * @return
     * @throws BaseException 使用此方法时，最好自行捕获并处理异常
     * @author qishao
     * date 2018-11-16
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] publicKeyBytes) throws BaseException {
        X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory; // RSA密钥工厂
        Key publicKey; // 公钥
        Cipher cipher; // 对数据加密
        try {
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("RSA KeyFactory not available.", e);
            throw new BaseException("-1", "RSA KeyFactory not available.");
        }
        try {
            publicKey = keyFactory.generatePublic(encodedKeySpec);
        } catch (InvalidKeySpecException e) {
            LOGGER.error("Could not generate public key.", e);
            throw new BaseException("-1", "Could not generate public key.");
        }
        try {
            cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Cannot find any provider supporting RSA.", e);
            throw new BaseException("-1", "Cannot find any provider supporting RSA.");
        } catch (NoSuchPaddingException e) {
            LOGGER.error("Padding Not Supported.", e);
            throw new BaseException("-1", "Padding Not Supported.");
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey); // 加密模式
        } catch (InvalidKeyException e) {
            LOGGER.error("Public Key invalid.", e);
            throw new BaseException("-1", "Public Key invalid. key: ");
        }
        int len = data.length; // 数据长度
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 分段加密过程
        int offSet = 0;
        byte[] cache;
        int i = 0;
        try { // 一旦加密有一部分出错，直接全停
            while (len > offSet) {
                // 以MAX_EXCRYPT_BLOCK为单位，向流中写入数据
                if (len - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, len - offSet);
                }
                out.write(cache, 0, cache.length);
                offSet = (++i) * MAX_ENCRYPT_BLOCK;
            }
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("BlockSize is Illegal.", e);
            throw new BaseException("-1", "BlockSize is Illegal.");
        } catch (BadPaddingException e) {
            LOGGER.error("Padding Not Supported.", e);
            throw new BaseException("-1", "Padding Not Supported.");
        }

        // 获得加密后数据，可通过Base64变成字符串
        byte[] encryptedData = out.toByteArray();
        try {
            out.close();
        } catch (IOException e) {
            LOGGER.error("Close outputStream failed.", e);
        }
        return encryptedData;
    }

    /**
     * 私钥解密数据
     *
     * @param encryptedData 加密后的源数据
     *                      如果加密后的数据封装成了String，需要用同样的方式转换成byte[]
     * @param privateKeyBytes 字节格式的key
     * @return
     * @throws BaseException 使用此方法时，最好自行捕获并处理异常
     * @author qishao
     * date 2018-11-16
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, byte[] privateKeyBytes) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory; // RSA密钥工厂
        Key privateKey; // 私钥
        Cipher cipher; // 对数据加密
        try {
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("RSA KeyFactory not available.", e);
            throw new BaseException("-1", "RSA KeyFactory not available.");
        }
        try {
            privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (InvalidKeySpecException e) {
            LOGGER.error("Could not generate private key.", e);
            throw new BaseException("-1", "Could not generate private key.");
        }
        try {
            cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Cannot find any provider supporting RSA.", e);
            throw new BaseException("-1", "Cannot find any provider supporting RSA.");
        } catch (NoSuchPaddingException e) {
            LOGGER.error("Padding Not Supported.", e);
            throw new BaseException("-1", "Padding Not Supported.");
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey); // 解密模式
        } catch (InvalidKeyException e) {
            LOGGER.error("Public Key invalid.", e);
            throw new BaseException("-1", "Public Key invalid. key: ");
        }
        int len = encryptedData.length; // 数据长度
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 分段解密过程
        int offSet = 0;
        byte[] cache;
        int i = 0;
        try {
            while (len > offSet) {
                if (len - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, len - offSet);
                }
                out.write(cache, 0, cache.length);
                offSet = (++i) * MAX_DECRYPT_BLOCK;
            }
        } catch (IllegalBlockSizeException e) {
            LOGGER.error("BlockSize is Illegal.", e);
            throw new BaseException("-1", "BlockSize is Illegal.");
        } catch (BadPaddingException e) {
            LOGGER.error("Padding Not Supported.", e);
            throw new BaseException("-1", "Padding Not Supported.");
        }
        /*
         * 获得解密数据，需要按原先的解码过程还原
         * 如果是String，用getBytes()方式获得byte[]，则需要采用new String(decryptedData)的方式获取想要的数据
         */
        byte[] decryptedData = out.toByteArray();
        try {
            out.close();
        } catch (IOException e) {
            LOGGER.error("Close outputStream failed.", e);
        }
        return decryptedData;
    }
}
