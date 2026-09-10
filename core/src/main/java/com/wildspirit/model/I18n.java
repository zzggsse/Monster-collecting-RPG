package com.wildspirit.model;

import java.util.LinkedHashSet;

/** 收集所有要显示的中文字，供中文字体渲染。 */
public final class I18n {
    private static final LinkedHashSet<Character> CHARS = new LinkedHashSet<>();

    private I18n() {
    }

    public static void register(String... strings) {
        for (String s : strings) {
            if (s == null) continue;
            for (int i = 0; i < s.length(); i++) {
                CHARS.add(s.charAt(i));
            }
        }
    }

    public static String characters() {
        StringBuilder sb = new StringBuilder();
        for (Character c : CHARS) {
            sb.append(c);
        }
        return sb.toString();
    }
}
