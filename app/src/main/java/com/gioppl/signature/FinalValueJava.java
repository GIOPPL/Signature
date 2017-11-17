package com.gioppl.signature;

import java.io.UnsupportedEncodingException;

/**
 * Created by GIOPPL on 2017/11/15.
 */

public class FinalValueJava {


    public static String getB_1000() {
        byte[] b = {0x1, 0x0, 0x0, 0x0};
        String res = null;
        try {
            res = new String(b, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }
    public static String getB_0000() {
        byte[] b = {0x0, 0x0, 0x0, 0x0};
        String res = null;
        try {
            res = new String(b, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }
    public static String getB_0100() {
        byte[] b = {0x0, 0x1, 0x0, 0x0};
        String res = null;
        try {
            res = new String(b, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }
}
