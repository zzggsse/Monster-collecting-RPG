package com.wildspirit.model;

/** 一只已捕获/野生的精灵个体（带等级与属性）。 */
public class Creature {
    public Species species;
    public String name;
    public int level;
    public int maxHp;
    public int hp;
    public int atk;
    public int def;
    public int spd;
    public int xp;
    /** 各技能剩余使用次数。 */
    public int[] ppLeft;
    /** 状态异常（无则 NONE）。 */
    public Status status = Status.NONE;
    /** 睡眠/冰冻剩余回合数。 */
    public int statusTurns;

    public Creature(Species species, int level) {
        this.species = species;
        this.name = species.name;
        this.level = Math.max(1, level);
        recalc(level);
        initPP();
    }

    public Creature(Species species, String customName, int level) {
        this(species, level);
        this.name = customName;
    }

    void recalc(int level) {
        maxHp = species.baseHp + level * 2;
        atk = species.baseAtk + level;
        def = species.baseDef + level;
        spd = species.baseSpd + level;
        if (hp <= 0 || species.evolution() != null) {
            // 满血（新精灵或进化后）
            hp = maxHp;
        }
        if (hp > maxHp) hp = maxHp;
    }

    public boolean alive() {
        return hp > 0;
    }

    public Move strongestMove() {
        return species.strongestMove();
    }

    public int xpToNext() {
        return 80 + level * 40;
    }

    public void heal() {
        hp = maxHp;
    }

    public void gainXp(int amount) {
        xp += Math.max(1, amount);
        while (xp >= xpToNext()) {
            xp -= xpToNext();
            level++;
            recalc(level);
            maybeEvolve();
        }
    }

    private void maybeEvolve() {
        if (species.evolution() != null && level >= species.evolveLevel()) {
            species = species.evolution();
            name = species.name;
            recalc(level);
            initPP();
        }
    }

    /** 是否刚刚满足进化条件（用于消息提示）。 */
    /** 尝试附加状态；被属性免疫或已有状态则不生效。 */
    public boolean applyStatus(Status s) {
        if (s == Status.NONE || status != Status.NONE || s.immuneTo(species.type)) {
            return false;
        }
        status = s;
        statusTurns = (s == Status.SLEEP || s == Status.FREEZE)
                ? 2 + (int) (Math.random() * 2) : 0;
        return true;
    }

    public void clearStatus() {
        status = Status.NONE;
        statusTurns = 0;
    }

    public void initPP() {
        ppLeft = new int[species.moves.length];
        for (int i = 0; i < ppLeft.length; i++) {
            ppLeft[i] = species.moves[i].pp;
        }
    }

    public boolean anyPP() {
        for (int p : ppLeft) {
            if (p > 0) {
                return true;
            }
        }
        return false;
    }

    /** 治疗站：体力和技能都恢复。 */
    public void restorePP() {
        initPP();
    }


    public boolean canEvolve() {
        return species.evolution() != null && level >= species.evolveLevel();
    }
}
