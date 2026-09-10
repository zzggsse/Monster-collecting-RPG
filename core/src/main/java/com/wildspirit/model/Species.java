package com.wildspirit.model;

/** 精灵种族定义。 */
public class Species {
    public final String key;
    public final String name;
    public final Types type;
    public final int baseHp;
    public final int baseAtk;
    public final int baseDef;
    public final int baseSpd;
    public final Move[] moves;
    /** 出现区域：all / forest / desert / snow。 */
    public final String region;
    /** 进化目标与所需等级。 */
    private Species evolvesTo;
    private int evolveLevel = -1;

    public Species(String key, String name, Types type, int baseHp, int baseAtk,
                   int baseDef, int baseSpd, String region, Move[] moves) {
        this.key = key;
        this.name = name;
        this.type = type;
        this.baseHp = baseHp;
        this.baseAtk = baseAtk;
        this.baseDef = baseDef;
        this.baseSpd = baseSpd;
        this.region = region;
        this.moves = moves;
        I18n.register(name);
    }

    public void setEvolution(Species evolvesTo, int level) {
        this.evolvesTo = evolvesTo;
        this.evolveLevel = level;
    }

    public Species evolution() {
        return evolvesTo;
    }

    public int evolveLevel() {
        return evolveLevel;
    }

    public Move strongestMove() {
        Move best = moves[0];
        for (Move m : moves) {
            if (m.power > best.power) best = m;
        }
        return best;
    }
}
