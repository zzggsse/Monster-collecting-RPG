package com.wildspirit.model;

/** 技能。 */
public class Move {
    public final String name;
    public final Types type;
    public final int power;
    public final int pp;
    /** 附带的状态异常（无则 NONE）。 */
    public final Status status;
    /** 触发状态的几率 0..1。 */
    public final float chance;

    public Move(String name, Types type, int power, int pp, Status status, float chance) {
        this.name = name;
        this.type = type;
        this.power = power;
        this.pp = Math.max(1, pp);
        this.status = status;
        this.chance = chance;
    }
}

