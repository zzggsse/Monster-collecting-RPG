package com.wildspirit.model;

import java.util.List;

/** 一个区域（森林/沙漠/雪山）的地图数据。 */
public class Region {
    public final String key;
    public final String name;
    public final String[] rows;
    public final int width;
    public final int height;
    public final int startX;
    public final int startY;

    public Region(String key, String name, String[] rows, int width) {
        this.key = key;
        this.name = name;
        this.width = width;
        this.height = rows.length;
        this.rows = rows;

        int sx = 0;
        int sy = 0;
        for (int y = 0; y < rows.length; y++) {
            char[] line = new char[width];
            String r = rows[y];
            for (int x = 0; x < width; x++) {
                char c = (x < r.length()) ? r.charAt(x) : '#';
                line[x] = c;
                if (c == 'P') {
                    sx = x;
                    sy = y;
                }
            }
            this.rows[y] = new String(line);
        }
        this.startX = sx;
        this.startY = sy;
        I18n.register(name);
    }

    public char at(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) return '#';
        return rows[y].charAt(x);
    }

    public boolean blocked(int x, int y) {
        char c = at(x, y);
        return c == '#' || c == '~';
    }

    public boolean isBoss(int x, int y) {
        return at(x, y) == 'B';
    }

    public boolean isExit(int x, int y) {
        return at(x, y) == 'E';
    }

    public boolean isEncounter(int x, int y) {
        return at(x, y) == '.';
    }

    public boolean isPickup(int x, int y) {
        return at(x, y) == 'F';
    }

    public boolean isHeal(int x, int y) {
        return at(x, y) == 'H';
    }

    public boolean isNpc(int x, int y) {
        return at(x, y) == 'n';
    }


    public int wildCount() {
        return Dex.wildPool(key).size();
    }

    public List<Species> wildPool() {
        return Dex.wildPool(key);
    }

    public Species boss() {
        return Dex.regionBoss(key);
    }

    public String bossBadge() {
        return key + "Boss";
    }
}
