package com.wildspirit.model;

/** 状态异常。 */
public enum Status {
    NONE("", 0f),
    POISON("中毒", 0.125f),
    BURN("灼伤", 0.125f),
    PARALYZE("麻痹", 0f),
    SLEEP("睡眠", 0f),
    FREEZE("冰冻", 0f);

    public final String cnName;
    /** 每回合按最大生命比例扣除（0 表示不扣血）。 */
    public final float dotFrac;

    Status(String cnName, float dotFrac) {
        this.cnName = cnName;
        this.dotFrac = dotFrac;
        if (!cnName.isEmpty()) {
            I18n.register(cnName);
        }
    }

    /** 属性免疫：该属性的精灵不会中此状态。 */
    public boolean immuneTo(Types t) {
        switch (this) {
            case POISON: return t == Types.ROCK || t == Types.GROUND;
            case BURN: return t == Types.FIRE;
            case FREEZE: return t == Types.ICE;
            case PARALYZE: return t == Types.GROUND;
            default: return false;
        }
    }
}

