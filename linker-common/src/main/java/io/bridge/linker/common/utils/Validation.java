package io.bridge.linker.common.utils;

import java.util.regex.Pattern;

/**
 * @author : canghai
 * @version V1.0
 * @Project: econtract
 * @Package io.bridge.econtract.common.utils
 * @Description: TODO
 * @date Date : 2020年05月22日 12:11
 */
public class Validation {

    static Pattern patternMailBox  = Pattern .compile( "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    static Pattern patternTell = Pattern.compile("^1[0-9]\\d{9}$");

    public static boolean validMobile(String mobile) {
        return patternTell.matcher(mobile).matches();
    }

    public static boolean validEmail(String email) {
        return patternMailBox.matcher(email).matches();
    }

}
