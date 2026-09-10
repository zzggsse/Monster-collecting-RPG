package com.wildspirit.model;

import java.util.HashMap;
import java.util.Map;

/** 地图上 NPC 的对话内容（区域:坐标 为键）。 */
public final class Dialogues {
    private static final Map<String, String[]> MAP = new HashMap<>();

    static {
        put("forest:8:2",
                "老训练师：草丛里会碰到野生精灵，战斗中用「捕捉」可以收服它们。",
                "按 I 打开图鉴，看看已经认识了哪些伙伴。",
                "回复药可以在野外树果附近找到，走上去就能拾取。");
        put("forest:12:8",
                "护林员：森林深处沉睡着头目「藤蔓王」。",
                "它守护着通往沙漠的路口，先练好等级、带足回复药再挑战吧。");
        put("desert:11:5",
                "旅行商人：烈日沙漠是火系和岩石系的天下。",
                "水系伙伴在这里会很吃力，记得根据地图特性调整队伍。");
        put("desert:16:8",
                "老向导：沙暴狮的吼声能震裂大地……",
                "用克制它的大地系反击，或者把它打累了再捕捉。");
        put("snow:10:9",
                "雪原猎手：霜龙盘踞在雪山之巅，已有百年不听风雪。",
                "火系伙伴在冰天雪地里会大放异彩。");
        put("snow:15:13",
                "神秘老人：集齐三枚徽章，你就是名副其实的荒野大师。",
                "这个世界的精灵会记住你的名字。");
    }

    private static void put(String key, String... lines) {
        MAP.put(key, lines);
    }

    public static String[] at(String key) {
        return MAP.get(key);
    }
}

