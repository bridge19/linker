package io.bridge.linker.common.utils;

import java.util.regex.Pattern;

public class CharUtils {
    private static final Pattern ENGLISH_PATTERN = Pattern.compile("^([a-zA-Z\\s])+$");


    // 根据Unicode编码完美的判断中文汉字
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
               // || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                //|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                //|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                ) {
            return true;
        }
        return false;
    }

    // 完整的判断中文汉字
    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!isChinese(c)&&c!=32) {
                return false;
            }
        }
        return true;
    }
    // 完整的判断英文名字 除空格外不能含其他特殊字符
    public static boolean isEnglish(String strName) {
       if (ENGLISH_PATTERN.matcher(strName).matches()){
           return true;
       }
        return false;
    }
}
