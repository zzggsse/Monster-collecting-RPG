package com.wildspirit.model;

import java.util.EnumMap;
import java.util.Map;


/** 精灵属性与克制关系。 */
public enum Types {
    NORMAL, FIRE, WATER, GRASS, BUG, ROCK, ICE, GROUND, DRAGON;

    private static final Map<Types, Map<Types, Float>> CHART = new EnumMap<>(Types.class);

    static {
        registerI18n();
        map(GRASS, WATER, 2f);
        map(GRASS, GROUND, 2f);
        map(GRASS, ROCK, 2f);
        map(GRASS, FIRE, 0.5f);
        map(GRASS, GRASS, 0.5f);
        map(GRASS, BUG, 0.5f);
        map(GRASS, DRAGON, 0.5f);

        map(FIRE, GRASS, 2f);
        map(FIRE, BUG, 2f);
        map(FIRE, ICE, 2f);
        map(FIRE, FIRE, 0.5f);
        map(FIRE, WATER, 0.5f);
        map(FIRE, ROCK, 0.5f);
        map(FIRE, DRAGON, 0.5f);

        map(WATER, FIRE, 2f);
        map(WATER, GROUND, 2f);
        map(WATER, ROCK, 2f);
        map(WATER, WATER, 0.5f);
        map(WATER, GRASS, 0.5f);
        map(WATER, DRAGON, 0.5f);

        map(BUG, GRASS, 2f);
        map(BUG, BUG, 0.5f);
        map(BUG, FIRE, 0.5f);

        map(ROCK, FIRE, 2f);
        map(ROCK, ICE, 2f);
        map(ROCK, BUG, 2f);
        map(ROCK, GROUND, 0.5f);

        map(ICE, GRASS, 2f);
        map(ICE, GROUND, 2f);
        map(ICE, DRAGON, 2f);
        map(ICE, FIRE, 0.5f);
        map(ICE, WATER, 0.5f);
        map(ICE, ICE, 0.5f);

        map(GROUND, FIRE, 2f);
        map(GROUND, ICE, 2f);
        map(GROUND, ROCK, 2f);
        map(GROUND, GRASS, 0.5f);
        map(GROUND, BUG, 0.5f);

        map(DRAGON, DRAGON, 2f);
        map(DRAGON, FIRE, 0.5f);
        map(DRAGON, WATER, 0.5f);
        map(DRAGON, GRASS, 0.5f);

        map(NORMAL, ROCK, 0.5f);
    }

    /** 中文名。 */
    public String cnName() {
        switch (this) {
            case NORMAL: return "普通";
            case FIRE: return "火";
            case WATER: return "水";
            case GRASS: return "草";
            case BUG: return "虫";
            case ROCK: return "岩石";
            case ICE: return "冰";
            case GROUND: return "地面";
            case DRAGON: return "龙";
            default: return name();
        }
    }

    public static void registerI18n() {
        I18n.register("普通", "火", "水", "草", "虫", "岩石", "冰", "地面", "龙");
    }

    private static void map(Types atk, Types def, float mult) {
        CHART.computeIfAbsent(atk, k -> new EnumMap<>(Types.class)).put(def, mult);
    }

    /** 攻击属性对防御属性的倍率。 */
    public float effectiveness(Types defender) {
        Map<Types, Float> m = CHART.get(this);
        return m == null ? 1f : m.getOrDefault(defender, 1f);
    }
}
