package com.wildspirit.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 种族图鉴与各区域出现的精灵。 */
public final class Dex {
    public static final Map<String, Species> ALL = new LinkedHashMap<>();
    /** 各技能最大使用次数。 */
    private static final Map<String, Integer> PP = new HashMap<>();

    static {
        PP.put("抓挠", 35);
        PP.put("冲撞", 30);
        PP.put("猛撞", 20);
        PP.put("藤鞭", 28);
        PP.put("飞叶刀", 22);
        PP.put("虫咬", 28);
        PP.put("角刺", 20);
        PP.put("火花", 26);
        PP.put("烈焰", 16);
        PP.put("滚动", 35);
        PP.put("岩崩", 18);
        PP.put("冰晶", 26);
        PP.put("暴风雪", 12);
        PP.put("水枪", 26);
        PP.put("水炮", 14);
        PP.put("沙尘", 22);
        PP.put("地震", 10);
        PP.put("毒粉", 30);
        PP.put("麻痹粉", 30);
        PP.put("催眠粉", 25);
        PP.put("毒牙", 20);
        PP.put("热浪", 12);
        PP.put("龙息", 20);
        PP.put("龙之怒", 12);
    }

    public static final Move C_ZUANLIAO = mv("抓挠", Types.NORMAL, 12);
    public static final Move C_CHONGZHUANG = mv("冲撞", Types.NORMAL, 16);
    public static final Move C_MENGZHUANG = mv("猛撞", Types.NORMAL, 24);
    public static final Move C_TENGBIAN = mv("藤鞭", Types.GRASS, 20);
    public static final Move C_FEIYEDAO = mv("飞叶刀", Types.GRASS, 28);
    public static final Move C_CHONGYAO = mv("虫咬", Types.BUG, 18);
    public static final Move C_JIAOCHI = mv("角刺", Types.BUG, 26);
    public static final Move C_HUOHUA = mv("火花", Types.FIRE, 22, Status.BURN, 0.15f);
    public static final Move C_LIEYAN = mv("烈焰", Types.FIRE, 34, Status.BURN, 0.2f);
    public static final Move C_GUNLONG = mv("滚动", Types.ROCK, 18);
    public static final Move C_YANBENG = mv("岩崩", Types.ROCK, 28);
    public static final Move C_BINGJING = mv("冰晶", Types.ICE, 20, Status.FREEZE, 0.12f);
    public static final Move C_BAOFENGXUE = mv("暴风雪", Types.ICE, 38, Status.FREEZE, 0.25f);
    public static final Move C_SHUIQIANG = mv("水枪", Types.WATER, 22);
    public static final Move C_SHUIJIA = mv("水炮", Types.WATER, 34);
    public static final Move C_SHACHEN = mv("沙尘", Types.GROUND, 26);
    public static final Move C_DIZHEN = mv("地震", Types.GROUND, 40);
    public static final Move C_DUFEN = mv("毒粉", Types.GRASS, 14, Status.POISON, 0.4f);
    public static final Move C_MABIFEN = mv("麻痹粉", Types.GRASS, 12, Status.PARALYZE, 0.4f);
    public static final Move C_CUIMIAN = mv("催眠粉", Types.GRASS, 10, Status.SLEEP, 0.35f);
    public static final Move C_DUYA = mv("毒牙", Types.GROUND, 22, Status.POISON, 0.3f);
    public static final Move C_RELANG = mv("热浪", Types.FIRE, 42, Status.BURN, 0.25f);
    public static final Move C_LONGXI = mv("龙息", Types.DRAGON, 26, Status.NONE, 0f);
    public static final Move C_LONGNU = mv("龙之怒", Types.DRAGON, 36, Status.NONE, 0f);

    /** 初始精灵（区域 none，不会随机出现）。 */

    public static final Species STARTER = sp("starter", "焰苗", Types.FIRE, 44, 19, 14, 16, "none",
            new Move[]{C_HUOHUA, C_ZUANLIAO});

    public static final Species LISU = sp("pip", "栗鼠", Types.NORMAL, 35, 15, 14, 13, "all",
            new Move[]{C_ZUANLIAO, C_CHONGZHUANG});
    public static final Species LICANG = sp("pip2", "栗仓", Types.NORMAL, 50, 24, 20, 18, "all",
            new Move[]{C_CHONGZHUANG, C_MENGZHUANG});

    public static final Species YEYA = sp("leaf", "叶芽", Types.GRASS, 40, 16, 13, 12, "forest",
            new Move[]{C_TENGBIAN, C_CHONGZHUANG});
    public static final Species TENGLING = sp("leaf2", "藤叶灵", Types.GRASS, 60, 25, 20, 17, "forest",
            new Move[]{C_TENGBIAN, C_FEIYEDAO});
    public static final Species JIACHONG = sp("bug", "甲壳虫", Types.BUG, 32, 15, 16, 14, "forest",
            new Move[]{C_CHONGYAO, C_ZUANLIAO});
    public static final Species JUJIAO = sp("bug2", "巨角虫", Types.BUG, 50, 23, 24, 19, "forest",
            new Move[]{C_CHONGYAO, C_JIAOCHI});
    public static final Species TENGWANG = sp("forestBoss", "藤蔓王", Types.GRASS, 70, 24, 20, 12, "forest",
            new Move[]{C_FEIYEDAO, C_TENGBIAN, C_CUIMIAN});

    public static final Species YANQUAN = sp("hound", "焰犬", Types.FIRE, 42, 20, 14, 16, "desert",
            new Move[]{C_HUOHUA, C_ZUANLIAO});
    public static final Species YANLANG = sp("hound2", "焰狼", Types.FIRE, 60, 28, 18, 22, "desert",
            new Move[]{C_HUOHUA, C_LIEYAN});
    public static final Species YANGUAI = sp("rockturtle", "岩龟", Types.ROCK, 55, 15, 24, 10, "desert",
            new Move[]{C_GUNLONG, C_CHONGZHUANG});
    public static final Species TIEBI = sp("rockturtle2", "铁壁龟", Types.ROCK, 75, 22, 36, 12, "desert",
            new Move[]{C_GUNLONG, C_YANBENG});
    public static final Species SHABAO = sp("desertBoss", "沙暴狮", Types.GROUND, 85, 28, 24, 18, "desert",
            new Move[]{C_SHACHEN, C_DIZHEN, C_DUYA});

    public static final Species XUEHU = sp("fox", "雪狐", Types.ICE, 40, 17, 15, 16, "snow",
            new Move[]{C_BINGJING, C_ZUANLIAO});
    public static final Species XUELANG = sp("fox2", "雪狼", Types.ICE, 55, 25, 20, 22, "snow",
            new Move[]{C_BINGJING, C_BAOFENGXUE});
    public static final Species QUANYU = sp("fish", "泉鱼", Types.WATER, 45, 16, 16, 14, "snow",
            new Move[]{C_SHUIQIANG, C_CHONGZHUANG});
    public static final Species YONGQUAN = sp("fish2", "涌泉鱼", Types.WATER, 65, 24, 24, 18, "snow",
            new Move[]{C_SHUIQIANG, C_SHUIJIA});
    public static final Species STARTER2 = sp("starter2", "焰角兽", Types.FIRE, 70, 32, 22, 24, "none",
            new Move[]{C_LIEYAN, C_RELANG, C_MENGZHUANG});
    public static final Species DUBAOGU = sp("mushroom", "毒孢菇", Types.GRASS, 48, 18, 20, 12, "forest",
            new Move[]{C_DUFEN, C_MABIFEN, C_CUIMIAN, C_TENGBIAN});
    public static final Species SHUXIE = sp("sandscorp", "沙蝎", Types.GROUND, 45, 20, 22, 14, "desert",
            new Move[]{C_DUYA, C_GUNLONG, C_CHONGZHUANG});
    public static final Species DUXIEWANG = sp("sandscorp2", "毒蝎王", Types.GROUND, 62, 30, 32, 18, "desert",
            new Move[]{C_DUYA, C_YANBENG, C_DIZHEN});
    public static final Species XUEXIAO = sp("snowowl", "雪鸮", Types.ICE, 42, 20, 16, 20, "snow",
            new Move[]{C_CUIMIAN, C_BINGJING, C_CHONGZHUANG});
    public static final Species CUJIALONG = sp("leafdragon", "翠甲龙", Types.DRAGON, 50, 22, 20, 16, "forest",
            new Move[]{C_LONGXI, C_TENGBIAN, C_CHONGZHUANG});
    public static final Species CANGMULONG = sp("leafdragon2", "苍木龙", Types.DRAGON, 66, 32, 26, 20, "forest",
            new Move[]{C_LONGXI, C_LONGNU, C_FEIYEDAO});
    public static final Species SHUANGLONG = sp("snowBoss", "霜龙", Types.ICE, 100, 30, 26, 20, "snow",
            new Move[]{C_BAOFENGXUE, C_BINGJING});

    static {
        ALL.put(STARTER.key, STARTER);
        ALL.put(LISU.key, LISU);
        ALL.put(LICANG.key, LICANG);
        ALL.put(YEYA.key, YEYA);
        ALL.put(TENGLING.key, TENGLING);
        ALL.put(JIACHONG.key, JIACHONG);
        ALL.put(JUJIAO.key, JUJIAO);
        ALL.put(TENGWANG.key, TENGWANG);
        ALL.put(YANQUAN.key, YANQUAN);
        ALL.put(YANLANG.key, YANLANG);
        ALL.put(YANGUAI.key, YANGUAI);
        ALL.put(TIEBI.key, TIEBI);
        ALL.put(SHABAO.key, SHABAO);
        ALL.put(XUEHU.key, XUEHU);
        ALL.put(XUELANG.key, XUELANG);
        ALL.put(QUANYU.key, QUANYU);
        ALL.put(YONGQUAN.key, YONGQUAN);
        ALL.put(SHUANGLONG.key, SHUANGLONG);
        ALL.put(STARTER2.key, STARTER2);
        ALL.put(DUBAOGU.key, DUBAOGU);
        ALL.put(SHUXIE.key, SHUXIE);
        ALL.put(DUXIEWANG.key, DUXIEWANG);
        ALL.put(XUEXIAO.key, XUEXIAO);
        ALL.put(CUJIALONG.key, CUJIALONG);
        ALL.put(CANGMULONG.key, CANGMULONG);

        I18n.register("抓挠", "冲撞", "猛撞", "藤鞭", "飞叶刀", "虫咬", "角刺",
                "火花", "烈焰", "滚动", "岩崩", "冰晶", "暴风雪", "水枪", "水炮", "沙尘", "地震", "毒粉", "麻痹粉", "催眠粉", "毒牙", "热浪", "龙息", "龙之怒");
        Types.registerI18n();

        YEYA.setEvolution(TENGLING, 12);
        JIACHONG.setEvolution(JUJIAO, 12);
        YANQUAN.setEvolution(YANLANG, 12);
        YANGUAI.setEvolution(TIEBI, 14);
        XUEHU.setEvolution(XUELANG, 13);
        QUANYU.setEvolution(YONGQUAN, 13);
        LISU.setEvolution(LICANG, 14);
        STARTER.setEvolution(STARTER2, 16);
        SHUXIE.setEvolution(DUXIEWANG, 14);
        CUJIALONG.setEvolution(CANGMULONG, 14);
    }

    public static Move mv(String name, Types type, int power) {
        return new Move(name, type, power, PP.getOrDefault(name, 20), Status.NONE, 0f);
    }

    public static Move mv(String name, Types type, int power, Status st, float chance) {
        return new Move(name, type, power, PP.getOrDefault(name, 20), st, chance);
    }

    public static Species sp(String key, String name, Types type, int hp, int atk,
                             int def, int spd, String region, Move[] moves) {
        return new Species(key, name, type, hp, atk, def, spd, region, moves);
    }

    public static Species get(String key) {
        return ALL.get(key);
    }

    public static List<Species> wildPool(String region) {
        List<Species> list = new ArrayList<>();
        for (Species s : ALL.values()) {
            if (s.key.endsWith("Boss") || "none".equals(s.region)) {
                continue;
            }
            if ("all".equals(s.region) || region.equals(s.region)) {
                list.add(s);
            }
        }
        return list;
    }

    public static Species regionBoss(String region) {
        switch (region) {
            case "forest": return TENGWANG;
            case "desert": return SHABAO;
            case "snow": return SHUANGLONG;
            default: return TENGWANG;
        }
    }

    public static Species starter() {
        return STARTER;
    }

    public static List<Species> collectible() {
        return new ArrayList<>(ALL.values());
    }
}
