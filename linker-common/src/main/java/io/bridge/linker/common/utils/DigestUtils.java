/**
 * 杭州天谷信息科技有限公司源代码，版权归杭州天谷信息科技有限公司所有 <br>
 * 项目名称：esignpro-demo <br>
 * 文件名：DigestUtil.java <br>
 * 包：cn.tsign.www.esignpro.demo.util <br>
 * 描述：TODO <br>
 * 修改历史： <br>
 * 1.[2017年6月22日上午10:51:12]创建文件 by Administrator
 */
package io.bridge.linker.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 类名：DigestUtil.java <br>
 * 功能说明：TODO <br>
 * 修改历史： <br>
 * 1.[2017年6月22日上午10:51:12]创建类 by flh
 */
public class DigestUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DigestUtils.class);
    private static final char HEX_DIGITS[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
    public static final String SHA256 = "SHA-256";

    public static String sha256DigestB64(byte[] orgin) {
        String result = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            messageDigest.update(orgin);
            byte[] byteArray = messageDigest.digest();
            result = Base64Utils.encode(byteArray);
            result = result.replaceAll("\r|\n", "");
        } catch (Exception e) {
            LOG.error("digest error!", e);
        }
        return result;
    }

    public static String sha256Digest(String origin) {
        String result = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            messageDigest.update(origin.getBytes("UTF-8"));
            byte[] byteArray = messageDigest.digest();
            result = Base64Utils.encode(byteArray);
        } catch (Exception e) {
            LOG.error("digest error!", e);
        }
        return result;
    }

    public static String sign(String origin, String key) {
        String hexString = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
            mac.init(secretKey);
            byte[] postData = origin.getBytes("UTF-8");
            byte[] sign = mac.doFinal(postData);
            hexString = binaryEncode(sign);
        } catch (Throwable e) {
            LOG.error("digest error!", e);
        }
        return hexString;
    }

    public static String binaryEncode(byte[] data) {
        final char hexDigits[] = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        StringBuilder builder = new StringBuilder();
        for (byte i : data) {
            builder.append(hexDigits[i >>> 4 & 0xf]);
            builder.append(hexDigits[i & 0xf]);
        }
        return builder.toString();
    }

    /**
     * 根据文件计算出文件的MD5
     *
     * @param data
     * @return
     */
    public static String getFileMD5Base64(byte[] data) {
        if (null == data) {
            return null;
        }
        String base64 = null;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(data);
            Base64.Encoder encoder = Base64.getEncoder();
            base64 = encoder.encodeToString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return base64;
    }

    public static String sha256Digest(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest provider = MessageDigest.getInstance(SHA256);
        provider.update(bytes);
        return toHexString(provider.digest());
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length << 1);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        InputStream is = new FileInputStream(new File("D:\\byte-of-python-chinese-edition.pdf"));
        byte[] data = new byte[is.available()];
        is.read(data);
        String fileMD5Base64 = getFileMD5Base64(data);
        System.out.println("长度:" + data.length + " md5:" + fileMD5Base64);
        is.close();
    }
}
