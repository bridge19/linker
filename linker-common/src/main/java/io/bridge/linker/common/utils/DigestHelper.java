package io.bridge.linker.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Encoder;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;


/***
 *
 * @Description: 摘要加密算法辅助类
 * @Team: 公有云技术支持小组
 * @Author: 天云小生
 * @Date: 2018年01月14日
 */
@Slf4j
public class DigestHelper {
   
    /***
     * 获取请求签名值
     *
     * @param data
     *            加密前数据
     * @param key
     *            密钥H
     * @param algorithm
     *            HmacMD5 HmacSHA1 HmacSHA256 HmacSHA384 HmacSHA512
     * @param encoding
     *            编码格式
     * @return HMAC加密后16进制字符串
     * @throws Exception
     */
    public static String getSignature(String data, String key, String algorithm, String encoding) {
        Mac mac = null;
        try {
            mac = Mac.getInstance(algorithm);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(encoding), algorithm);
            mac.init(secretKey);
            mac.update(data.getBytes(encoding));
        } catch (NoSuchAlgorithmException e) {
            log.error("获取Signature签名信息异常",e);
            return null;
        } catch (UnsupportedEncodingException e) {
            log.error("获取Signature签名信息异常", e);
            return null;
        } catch (InvalidKeyException e) {
            log.error("获取Signature签名信息异常", e);
            return null;
        }
        return byte2hex(mac.doFinal());
    }

    /***
     * 计算文件的Content-MD5
     *
     * @param filePath
     * @return
     */
    public static String getContentMD5(String filePath) {
    	 Encoder encoder = Base64.getEncoder();
    	// 获取文件MD5的二进制数组（128位）
        byte[] bytes = getFileMD5Bytes128(filePath);
        // 对文件MD5的二进制数组进行base64编码（而不是对32位的16进制字符串进行编码）
        return  encoder.encodeToString(bytes);
       
    }

    /***
     * 获取文件MD5-二进制数组（128位）
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static byte[] getFileMD5Bytes128(String filePath) {
        FileInputStream fis = null;
        byte[] md5Bytes = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md5.update(buffer, 0, length);
            }
            md5Bytes = md5.digest();
            fis.close();
        } catch (Exception e) {
            log.error("文件操作失败：" , e);
        }
        return md5Bytes;
    }

    /***
     * 获取文件SHA256-十六进制字符串(原文SHA256摘要)
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String getFileSHA256(String filePath) {
        FileInputStream fis = null;
        byte[] md5Bytes = null;
        String hashSHA256 = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            MessageDigest md5 = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md5.update(buffer, 0, length);
            }
            md5Bytes = md5.digest();
            fis.close();
            hashSHA256 = byte2hex(md5Bytes);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return hashSHA256;
    }

    /***
     * 获取字符串的SHA256-十六进制字符串
     *
     * @param str
     * @return
     * @throws IOException
     */
    public static String getStringSHA256(String str) {
        byte[] md5Bytes = null;
        try {
            md5Bytes = str.getBytes("UTF-8");
            // 拿到一个SHA256转换器
            MessageDigest messageDigest;
            messageDigest = MessageDigest.getInstance("SHA-256");
            // 输入的字符串转换成字节数组
            byte[] inputByteArray = str.getBytes();
            // inputByteArray是输入字符串转换得到的字节数组
            messageDigest.update(inputByteArray);
            // 转换并返回结果，也是字节数组，包含16个元素
            md5Bytes = messageDigest.digest();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return byte2hex(md5Bytes);
    }

    /***
     * 将byte[]转成16进制字符串
     *
     * @param data
     *
     * @return 16进制字符串
     */
    public static String byte2hex(byte[] data) {
        StringBuilder hash = new StringBuilder();
        String stmp;
        for (int n = 0; data != null && n < data.length; n++) {
            stmp = Integer.toHexString(data[n] & 0XFF);
            if(stmp.length() == 1) {
                hash.append('0');
            }
            hash.append(stmp);
        }
        return hash.toString();
    }

    /**
     * BASE64编码
     *
     * @param data 要加密的数据
     * @return 加密后的字符串
     */
    private static String encryptBASE64(byte[] data) {
        return new String(Base64.getEncoder().encodeToString(data));
    }

    /**
     * BASE64解码
     *
     * @param data 已加密的数据
     * @return 加密前的数据
     */
    private static byte[] decryptBASE64(String data) {
        return Base64.getDecoder().decode(data);
    }
    /**
     * 用户中心内容加密函数
     *
     * @param str 加密字符串
     * @param key 秘钥
     * @return 密文
     * @throws IOException
     * @throws InvalidCipherTextException
     */
    public static String encryptByPublicKey(String str, String key)
            throws IOException, InvalidCipherTextException {
        AsymmetricBlockCipher engine = new RSAEngine();
        PKCS1Encoding cipher = new PKCS1Encoding(engine);
        byte[] publicInfoBytes = decryptBASE64(key);
        ASN1Object pubKeyObj = ASN1Primitive.fromByteArray(publicInfoBytes);
        AsymmetricKeyParameter pubKey =
                PublicKeyFactory.createKey(SubjectPublicKeyInfo.getInstance(pubKeyObj));

        cipher.init(true, pubKey);
        byte[] data = str.getBytes("utf-8");
        byte[] encryptDataBytes = cipher.processBlock(data, 0, data.length);
        String encryptData = encryptBASE64(encryptDataBytes);
        return encryptData;
    }


    /**
     * RSA公钥解密
     *
     * @param encryStr 加密字符串
     * @param publicKey 公钥
     * @return 铭文
     * @throws IOException
     * @throws InvalidCipherTextException
     */
    public static String decryptByPublicKey(String encryStr, String publicKey)
        throws IOException, InvalidCipherTextException {
        AsymmetricBlockCipher engine = new RSAEngine();
        PKCS1Encoding cipher = new PKCS1Encoding(engine);
        byte[] encryptDataBytes = decryptBASE64(encryStr);

        byte[] publicInfoBytes = decryptBASE64(publicKey);
        ASN1Object pubKeyObj = ASN1Primitive.fromByteArray(publicInfoBytes);
        AsymmetricKeyParameter pubKey =
            PublicKeyFactory.createKey(SubjectPublicKeyInfo.getInstance(pubKeyObj));

        cipher.init(false, pubKey);

        byte[] decryptDataBytes = cipher.processBlock(encryptDataBytes, 0, encryptDataBytes.length);
        String decryptData = new String(decryptDataBytes, "utf-8");
        return decryptData;
    }
}
