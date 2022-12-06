package com.pigeoff.metro.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static String reduceStr(String str) {
        str = str.replace(" ", "");
        str = str.replace("+", "");
        str = str.toLowerCase(Locale.ROOT);
        return str;
    }

    public static ArrayList<Integer> initiateList(int n, int v) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(v);
        }
        return list;
    }

    public static ArrayList<String> initiateList(int n, String v) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(v);
        }
        return list;
    }
}
