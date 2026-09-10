package com.wildspirit.gfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.wildspirit.model.I18n;
import com.wildspirit.model.Region;

import java.util.HashMap;
import java.util.Map;

/** 贴图与中文字体。 */
public final class Assets {
    public static final int TILE = 32;

    private static final Map<String, String> SPRITE = new HashMap<>();

    static {
        SPRITE.put("pip", "lisu");
        SPRITE.put("pip2", "lisu");
        SPRITE.put("leaf", "yeya");
        SPRITE.put("leaf2", "yeya");
        SPRITE.put("bug", "jiachong");
        SPRITE.put("bug2", "jiachong");
        SPRITE.put("forestBoss", "tengwang");
        SPRITE.put("hound", "yanquan");
        SPRITE.put("hound2", "yanquan");
        SPRITE.put("rockturtle", "yanguai");
        SPRITE.put("rockturtle2", "yanguai");
        SPRITE.put("desertBoss", "shabao");
        SPRITE.put("fox", "xuehu");
        SPRITE.put("fox2", "xuehu");
        SPRITE.put("fish", "quanyu");
        SPRITE.put("fish2", "quanyu");
        SPRITE.put("snowBoss", "shuanglong");
        SPRITE.put("starter", "starter");
        SPRITE.put("starter2", "bison");
        SPRITE.put("mushroom", "mushroom");
        SPRITE.put("sandscorp", "scorpion");
        SPRITE.put("sandscorp2", "scorpion");
        SPRITE.put("snowowl", "owl");
        SPRITE.put("leafdragon", "dragon");
        SPRITE.put("leafdragon2", "dragon");
    }

    private enum TileStyle { WALL, WATER, GROUND, PATH, EXIT, BOSS, PICKUP, HEAL, NPC }

    private final Map<Character, Texture> tiles = new HashMap<>();
    private final Map<String, Texture> sprites = new HashMap<>();
    private Texture heroTexture;
    private BitmapFont fontSmall;
    private BitmapFont fontBig;

    public Assets() {
    }

    public void buildTiles(Region region) {
        for (Texture t : tiles.values()) {
            t.dispose();
        }
        tiles.clear();

        Color wallDark, wallLight, waterBase, waterLight, groundBase, groundDark, pathBase, pathDark;
        if ("forest".equals(region.key)) {
            wallDark = new Color(0.10f, 0.30f, 0.16f, 1f);
            wallLight = new Color(0.18f, 0.45f, 0.24f, 1f);
            waterBase = new Color(0.18f, 0.40f, 0.72f, 1f);
            waterLight = new Color(0.42f, 0.66f, 0.94f, 1f);
            groundBase = new Color(0.62f, 0.85f, 0.42f, 1f);
            groundDark = new Color(0.48f, 0.70f, 0.32f, 1f);
            pathBase = new Color(0.72f, 0.60f, 0.42f, 1f);
            pathDark = new Color(0.55f, 0.44f, 0.30f, 1f);
        } else if ("desert".equals(region.key)) {
            wallDark = new Color(0.65f, 0.47f, 0.20f, 1f);
            wallLight = new Color(0.85f, 0.66f, 0.36f, 1f);
            waterBase = new Color(0.20f, 0.50f, 0.76f, 1f);
            waterLight = new Color(0.50f, 0.75f, 0.95f, 1f);
            groundBase = new Color(0.98f, 0.90f, 0.66f, 1f);
            groundDark = new Color(0.88f, 0.77f, 0.50f, 1f);
            pathBase = new Color(0.80f, 0.70f, 0.52f, 1f);
            pathDark = new Color(0.66f, 0.56f, 0.40f, 1f);
        } else {
            wallDark = new Color(0.52f, 0.55f, 0.62f, 1f);
            wallLight = new Color(0.76f, 0.79f, 0.86f, 1f);
            waterBase = new Color(0.45f, 0.68f, 0.88f, 1f);
            waterLight = new Color(0.72f, 0.88f, 0.98f, 1f);
            groundBase = new Color(0.96f, 0.97f, 1.00f, 1f);
            groundDark = new Color(0.84f, 0.87f, 0.95f, 1f);
            pathBase = new Color(0.70f, 0.72f, 0.78f, 1f);
            pathDark = new Color(0.56f, 0.58f, 0.64f, 1f);
        }

        tiles.put('#', makeTile(TileStyle.WALL, wallDark, wallLight));
        tiles.put('~', makeTile(TileStyle.WATER, waterBase, waterLight));
        tiles.put('.', makeTile(TileStyle.GROUND, groundBase, groundDark));
        tiles.put(';', makeTile(TileStyle.PATH, pathBase, pathDark));
        tiles.put('P', tiles.get('.'));
        tiles.put('E', makeTile(TileStyle.EXIT, new Color(0.95f, 0.78f, 0.30f, 1f),
                new Color(0.65f, 0.50f, 0.15f, 1f)));
        tiles.put('B', makeTile(TileStyle.BOSS, new Color(0.78f, 0.45f, 0.30f, 1f),
                new Color(0.50f, 0.25f, 0.16f, 1f)));
        tiles.put('F', makeTile(TileStyle.PICKUP, groundBase, groundDark));
        tiles.put('H', makeTile(TileStyle.HEAL,
                new Color(0.96f, 0.64f, 0.75f, 1f), new Color(0.80f, 0.42f, 0.60f, 1f)));
        tiles.put('n', makeTile(TileStyle.NPC, groundBase, groundDark));
    }

    private Texture makeTile(TileStyle style, Color base, Color accent) {
        Pixmap p = new Pixmap(TILE, TILE, Pixmap.Format.RGBA8888);
        p.setColor(base);
        p.fill();
        p.setColor(accent);
        int rgba = Color.rgba8888(accent.r, accent.g, accent.b, 1f);
        int rgbaLight = Color.rgba8888(
                Math.min(1f, base.r * 1.25f), Math.min(1f, base.g * 1.25f),
                Math.min(1f, base.b * 1.25f), 1f);
        int rgbaDark = Color.rgba8888(
                base.r * 0.65f, base.g * 0.65f, base.b * 0.65f, 1f);

        switch (style) {
            case WALL:
                // 大块岩石/树丛质感 + 顶部受光
                for (int y = 0; y < TILE; y++) {
                    for (int x = 0; x < TILE; x++) {
                        int key = (x * 7 + y * 13) % 11;
                        if (key == 0) p.drawPixel(x, y, rgbaDark);
                        else if (key == 5) p.drawPixel(x, y, rgbaLight);
                    }
                }
                for (int x = 0; x < TILE; x++) {
                    p.drawPixel(x, TILE - 1, Color.rgba8888(accent.r * 1.3f, accent.g * 1.3f, accent.b * 1.3f, 1f));
                    p.drawPixel(x, TILE - 2, Color.rgba8888(accent.r, accent.g, accent.b, 1f));
                }
                break;
            case WATER:
                // 水平波光
                for (int y = 0; y < TILE; y++) {
                    for (int x = 0; x < TILE; x++) {
                        if ((x * 3 + y * 5) % 9 == 0) p.drawPixel(x, y, rgbaLight);
                    }
                }
                for (int y = 3; y < TILE; y += 8) {
                    for (int x = 2; x < TILE - 2; x += 4) {
                        p.drawPixel(x, y, rgbaLight);
                        p.drawPixel(x + 1, y, rgbaLight);
                    }
                }
                break;
            case GROUND:
                // 亮色地面 + 稀疏噪点
                for (int y = 0; y < TILE; y++) {
                    for (int x = 0; x < TILE; x++) {
                        int key = (x * 5 + y * 11) % 16;
                        if (key == 0) p.drawPixel(x, y, rgbaDark);
                        else if (key == 7) p.drawPixel(x, y, rgbaLight);
                    }
                }
                break;
            case PATH:
                for (int y = 0; y < TILE; y++) {
                    for (int x = 0; x < TILE; x++) {
                        int key = (x * 9 + y * 3) % 13;
                        if (key == 0) p.drawPixel(x, y, rgbaDark);
                        else if (key == 6) p.drawPixel(x, y, rgbaLight);
                    }
                }
                break;
            case EXIT:
            case BOSS:
                // 醒目特殊块 + 深色边框
                for (int y = 0; y < TILE; y++) {
                    for (int x = 0; x < TILE; x++) {
                        int key = (x * 7 + y * 13) % 10;
                        if (key == 0) p.drawPixel(x, y, rgbaDark);
                    }
                }
                p.setColor(rgbaDark);
                p.fillRectangle(0, 0, TILE, 2);
                p.fillRectangle(0, TILE - 2, TILE, 2);
                p.fillRectangle(0, 0, 2, TILE);
                p.fillRectangle(TILE - 2, 0, 2, TILE);
                break;
            case PICKUP:
                // 地面上的树果：绿丛 + 红果
                for (int y = 0; y < TILE; y++) {
                    for (int x = 0; x < TILE; x++) {
                        int key = (x * 5 + y * 11) % 16;
                        if (key == 0) p.drawPixel(x, y, rgbaDark);
                        else if (key == 7) p.drawPixel(x, y, rgbaLight);
                    }
                }
                p.setColor(Color.rgba8888(0.17f, 0.55f, 0.22f, 1f));
                p.fillCircle(16, 13, 8);
                p.fillCircle(9, 10, 5);
                p.fillCircle(23, 11, 5);
                p.setColor(Color.rgba8888(0.95f, 0.18f, 0.18f, 1f));
                p.fillCircle(16, 16, 3);
                p.fillCircle(9, 12, 2);
                p.fillCircle(23, 13, 2);
                p.fillCircle(13, 9, 2);
                break;
            case NPC:
                // 地面上的村民：深紫小人 + 头顶对话气泡
                for (int y = 0; y < TILE; y++) {
                    for (int x = 0; x < TILE; x++) {
                        int key = (x * 5 + y * 11) % 16;
                        if (key == 0) p.drawPixel(x, y, rgbaDark);
                        else if (key == 7) p.drawPixel(x, y, rgbaLight);
                    }
                }
                p.setColor(Color.rgba8888(0.30f, 0.24f, 0.42f, 1f));
                p.fillCircle(16, 22, 5);
                p.fillRectangle(12, 10, 8, 11);
                p.setColor(Color.rgba8888(0.98f, 0.94f, 0.82f, 1f));
                p.fillCircle(24, 25, 5);
                p.setColor(Color.rgba8888(0.35f, 0.35f, 0.55f, 1f));
                p.fillRectangle(21, 24, 4, 2);
                p.fillRectangle(26, 23, 2, 4);
                p.fillRectangle(22, 29, 2, 2);
                break;

            case HEAL:
                // 治疗师帐篷：粉紫底 + 白色十字
                for (int y = 0; y < TILE; y++) {
                    for (int x = 0; x < TILE; x++) {
                        int key = (x * 7 + y * 13) % 12;
                        if (key == 0) p.drawPixel(x, y, rgbaDark);
                        else if (key == 7) p.drawPixel(x, y, rgbaLight);
                    }
                }
                p.setColor(Color.WHITE);
                p.fillRectangle(13, 9, 6, 14);
                p.fillRectangle(7, 13, 18, 6);
                break;

            default:
                break;
        }
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }

    public Texture hero() {
        if (heroTexture == null) {
            heroTexture = loadSprite("hero");
            if (heroTexture == null) {
                heroTexture = fallbackMarker(0.86f, 0.35f, 0.65f);
            }
        }
        return heroTexture;
    }

    public Texture creatureIcon(String speciesKey) {
        String file = SPRITE.getOrDefault(speciesKey, "lisu");
        Texture t = sprites.get(file);
        if (t == null) {
            t = loadSprite(file);
            if (t == null) {
                t = fallbackMarker(0.40f, 0.65f, 0.85f);
            }
            sprites.put(file, t);
        }
        return t;
    }

    private Texture loadSprite(String name) {
        try {
            Texture t = new Texture(Gdx.files.internal("sprites/" + name + ".png"));
            t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            return t;
        } catch (Exception e) {
            return null;
        }
    }

    private Texture fallbackMarker(float r, float g, float b) {
        Pixmap p = new Pixmap(TILE, TILE, Pixmap.Format.RGBA8888);
        p.setColor(new Color(1f, 1f, 1f, 1f));
        p.fill();
        p.setColor(new Color(r, g, b, 1f));
        p.fillCircle(16, 16, 12);
        p.setColor(new Color(1f, 0.93f, 0.75f, 1f));
        p.fillCircle(16, 16, 7);
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }

    public Texture tile(char c) {
        return tiles.getOrDefault(c, tiles.get(';'));
    }

    public BitmapFont fontSmall() {
        if (fontSmall == null) fontSmall = loadFont(20);
        return fontSmall;
    }

    public BitmapFont fontBig() {
        if (fontBig == null) fontBig = loadFont(28);
        return fontBig;
    }

    private BitmapFont loadFont(int size) {
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/simhei.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter p =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.size = size;
        p.color = new Color(1f, 1f, 1f, 1f);
        p.characters = FreeTypeFontGenerator.DEFAULT_CHARS
                + i18nResourceCharacters()
                + "、。《》：！？…—→×◆▶↑↓（）";
        BitmapFont f = gen.generateFont(p);
        gen.dispose();
        return f;
    }

    private String i18nResourceCharacters() {
        try {
            return Gdx.files.internal("i18n.txt").readString("UTF-8");
        } catch (Exception e) {
            return I18n.characters();
        }
    }

    public void dispose() {
        for (Texture t : tiles.values()) {
            t.dispose();
        }
        for (Texture t : sprites.values()) {
            t.dispose();
        }
        if (heroTexture != null) heroTexture.dispose();
        if (fontSmall != null) fontSmall.dispose();
        if (fontBig != null) fontBig.dispose();
    }
}
