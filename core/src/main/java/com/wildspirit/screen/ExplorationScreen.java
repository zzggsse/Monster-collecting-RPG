package com.wildspirit.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.wildspirit.WildSpiritGame;
import com.wildspirit.gfx.Assets;
import com.wildspirit.model.Creature;
import com.wildspirit.model.Dex;
import com.wildspirit.model.Dialogues;
import com.wildspirit.model.Region;
import com.wildspirit.model.Species;
import com.wildspirit.model.Status;

import java.util.List;

/** 探索地图界面。 */
public class ExplorationScreen extends InputAdapter implements Screen {
    private final WildSpiritGame game;
    private Region region;
    private OrthographicCamera cam;
    private Viewport viewport;
    private SpriteBatch batch;
    private Texture white;
    private int px;
    private int py;
    private String message = "";
    private float messageTimer;
    private boolean paused;
    private int pauseIndex;
    private boolean showDex;
    private boolean showTeam;
    private String[] dialogue;
    private int dialogueIndex;

    public ExplorationScreen(WildSpiritGame game) {
        this.game = game;
        this.region = game.state.region();
        this.px = game.state.playerX;
        this.py = game.state.playerY;
    }

    @Override
    public void show() {
        cam = new OrthographicCamera();
        float w = region.width * Assets.TILE;
        float h = region.height * Assets.TILE;
        viewport = new FitViewport(w, h, cam);
        batch = new SpriteBatch();
        white = makeWhite();
        cam.position.set(w / 2f, h / 2f, 0);
        viewport.apply();
        Gdx.input.setInputProcessor(this);
    }

    private Texture makeWhite() {
        Pixmap p = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        p.setColor(1f, 1f, 1f, 1f);
        p.fill();
        Texture t = new Texture(p);
        p.dispose();
        return t;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.08f, 0.09f, 0.12f, 1f);
        if (messageTimer > 0 && !paused) {
            messageTimer -= delta;
        }
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        drawTiles(batch);
        batch.draw(game.assets.hero(), px * Assets.TILE, (region.height - 1 - py) * Assets.TILE);
        batch.end();
        drawHud();
    }

    private void drawTiles(SpriteBatch batch) {
        for (int y = 0; y < region.height; y++) {
            for (int x = 0; x < region.width; x++) {
                char c = region.at(x, y);
                if (c == 'F' && game.state.isCollected(x, y)) {
                    c = '.';
                }
                float sy = (region.height - 1 - y) * Assets.TILE;
                batch.draw(game.assets.tile(c), x * Assets.TILE, sy);
                if (c == 'B') {
                    if (!game.state.bossDefeated()) {
                        batch.draw(game.assets.creatureIcon(region.boss().key),
                                x * Assets.TILE, sy, Assets.TILE, Assets.TILE);
                    }
                }
            }
        }
        // 障碍物与可走地面交界处描暗影
        batch.setColor(0f, 0f, 0f, 0.45f);
        for (int y = 0; y < region.height; y++) {
            for (int x = 0; x < region.width; x++) {
                if (!region.blocked(x, y)) {
                    continue;
                }
                float sx = x * Assets.TILE;
                float sy = (region.height - 1 - y) * Assets.TILE;
                if (!region.blocked(x, y + 1)) {
                    batch.draw(white, sx, sy, Assets.TILE, 3);
                }
                if (!region.blocked(x, y - 1)) {
                    batch.draw(white, sx, sy + Assets.TILE - 3, Assets.TILE, 3);
                }
                if (!region.blocked(x - 1, y)) {
                    batch.draw(white, sx, sy, 3, Assets.TILE);
                }
                if (!region.blocked(x + 1, y)) {
                    batch.draw(white, sx + Assets.TILE - 3, sy, 3, Assets.TILE);
                }
            }
        }
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void drawHud() {
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        BitmapFont font = game.assets.fontSmall();
        String title = region.name + "（" + game.state.badges.size() + "/3 徽章）";
        font.draw(batch, title, 8, region.height * Assets.TILE - 8);
        if (!paused && !showDex && !showTeam) {
            if (messageTimer > 0 && !message.isEmpty()) {
                drawBox(16, 16, 230, 62);
                font.draw(batch, message, 24, 58);
            } else {
                font.draw(batch, "方向键/WASD 移动  空格 通行   I 图鉴   ESC 菜单", 8, 22);
            }
        }
        if (paused) {
            drawMenu();
        }
        if (showDex) {
            drawDexBoard();
        }
        if (showTeam) {
            drawParty();
        }
        if (!paused && !showDex && !showTeam
                && message.isEmpty() && messageTimer <= 0) {
            Creature a = game.state.active();
            if (a != null) {
                font.draw(batch, a.name + " Lv." + a.level,
                        8, region.height * Assets.TILE - 32);
            }
        }
        if (dialogue != null) {
            drawBox(60, 30, 700, 132);
            font.draw(batch, dialogue[dialogueIndex], 90, 124);
            font.draw(batch, "回车/空格 继续    ESC 跳过", 90, 72);
        }
        batch.end();
    }

    private void drawBox(float x, float y, float w, float h) {
        batch.setColor(0f, 0f, 0f, 0.78f);
        batch.draw(white, x, y, w, h);
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(white, x, y, w, 2);
        batch.draw(white, x, y + h - 2, w, 2);
        batch.draw(white, x, y, 2, h);
        batch.draw(white, x + w - 2, y, 2, h);
    }

    private void drawMenu() {
        String[] items = {"返回游戏", "图鉴", "队伍", "保存进度", "退出游戏"};
        drawBox(300, 170, 250, 210);
        BitmapFont font = game.assets.fontSmall();
        for (int i = 0; i < items.length; i++) {
            String prefix = (i == pauseIndex) ? "▶ " : "  ";
            font.draw(batch, prefix + items[i], 320, 350 - i * 32);
        }
    }

    private void drawParty() {
        float x = 40, y = 180, w = 880, h = 320;
        drawBox(x, y, w, h);
        BitmapFont font = game.assets.fontSmall();
        int idx = 0;
        for (Creature c : game.state.party) {
            float cy = y + h - 30 - idx * 34;
            String stTag = c.status == Status.NONE ? "" : "（" + c.status.cnName + "）";
            font.draw(batch, c.name + "  Lv." + c.level + stTag, x + 12, cy);
            float frac = (float) c.hp / c.maxHp;
            drawSmallBar(x + 380, cy - 16, 160, 10, frac);
            idx++;
        }
        font.draw(batch, "已获徽章：" + game.state.badges.size() + " / 3    回复药 ×" + game.state.potions, x + 12, y - 12);
    }

    private void drawSmallBar(float x, float y, float w, float h, float frac) {
        batch.setColor(0.2f, 0.2f, 0.2f, 1f);
        batch.draw(white, x, y, w, h);
        if (frac > 0.5f) batch.setColor(0.2f, 0.8f, 0.3f, 1f);
        else if (frac > 0.2f) batch.setColor(0.95f, 0.8f, 0.2f, 1f);
        else batch.setColor(0.9f, 0.3f, 0.25f, 1f);
        batch.draw(white, x, y, w * Math.max(0f, Math.min(1f, frac)), h);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void drawDexBoard() {
        float x = 30, y = 50, w = 900, h = 450;
        drawBox(x, y, w, h);
        BitmapFont font = game.assets.fontSmall();
        List<Species> all = Dex.collectible();
        for (int i = 0; i < all.size(); i++) {
            Species s = all.get(i);
            int col = i / 13;
            int row = i % 13;
            float bx = x + 24 + col * 430;
            float by = y + h - 30 - row * 32;
            String status;
            int cnt = game.state.caughtOf(s.key);
            if (cnt > 0) {
                status = "已捕获 ×" + cnt;
            } else if (game.state.seen.contains(s.key)) {
                status = "已见";
            } else {
                status = "未见";
            }
            font.draw(batch, s.name + "（" + s.type.cnName() + "）", bx, by);
            font.draw(batch, status, bx + 250, by);
        }
        font.draw(batch, "ESC/回车 关闭", x + w - 130, y + 18);
    }

    private void afterMove() {
        Region r = region;
        if (r.isNpc(px, py) && dialogue == null) {
            String[] lines = Dialogues.at(region.key + ":" + px + "," + py);
            if (lines != null && lines.length > 0) {
                dialogue = lines;
                dialogueIndex = 0;
                game.audio.select();
                return;
            }
        }
        if (r.isPickup(px, py) && !game.state.isCollected(px, py)) {
            game.state.collect(px, py);
            game.state.potions++;
            game.audio.itemPickup();
            showMessage("拾到树果，回复药 +1（现有 ×" + game.state.potions + "）");
            return;
        }
        if (r.isHeal(px, py)) {
            game.state.healParty();
            game.audio.heal();
            showMessage("治疗师治愈了全队，体力与技能都恢复了！");
            return;
        }
        if (r.isBoss(px, py) && !game.state.bossDefeated()) {
            startBossBattle();
            return;
        }
        if (r.isEncounter(px, py)) {
            if (Math.random() < 0.18) {
                startWildBattle();
                return;
            }
        }
        if (r.isExit(px, py)) {
            tryExit();
        }
    }

    private void startBossBattle() {
        Creature boss = new Creature(region.boss(), game.bossLevel());
        showMessage(region.boss().name + " 前来迎战！");
        game.setScreen(new BattleScreen(game, boss, true,
                (outcome) -> game.onBattleEnded(outcome)));
    }

    private void startWildBattle() {
        Region r = region;
        var pool = r.wildPool();
        Creature enemy = new Creature(pool.get((int) (Math.random() * pool.size())),
                1 + (int) (Math.random() * game.wildMaxLevel()));
        game.setScreen(new BattleScreen(game, enemy, false,
                (outcome) -> game.onBattleEnded(outcome)));
    }

    private void tryExit() {
        if (game.state.atLastRegion()) {
            if (game.state.bossDefeated()) {
                showMessage("你征服了三大区域，成为『荒野大师』！感谢游玩！");
                game.saveGame();
            } else {
                showMessage("雾障封锁了返回的路，先击败区域头目吧！");
            }
            return;
        }
        if (!game.state.bossDefeated()) {
            showMessage("通往下一区域的路被" + region.boss().name + "守护的雾障封锁！");
            return;
        }
        game.state.moveToNextRegion();
        rebuildRegion();
        showMessage("你来到了" + region.name + "！");
        game.saveGame();
    }

    private void rebuildRegion() {
        region = game.state.region();
        px = game.state.playerX;
        py = game.state.playerY;
        game.assets.buildTiles(region);
        float w = region.width * Assets.TILE;
        float h = region.height * Assets.TILE;
        viewport = new FitViewport(w, h, cam);
        cam.position.set(w / 2f, h / 2f, 0);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        cam.update();
    }

    private void showMessage(String msg) {
        message = msg;
        messageTimer = 3.2f;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (paused) {
            if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
                pauseIndex = (pauseIndex + 4) % 5;
            } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
                pauseIndex = (pauseIndex + 1) % 5;
            } else if (keycode == Input.Keys.ESCAPE) {
                paused = false;
            } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                if (pauseIndex == 0) {
                    paused = false;
                } else if (pauseIndex == 1) {
                    paused = false;
                    showDex = true;
                } else if (pauseIndex == 2) {
                    paused = false;
                    showTeam = true;
                } else if (pauseIndex == 3) {
                    game.saveGame();
                    showMessage("进度已保存！");
                    pauseIndex = 0;
                } else {
                    Gdx.app.exit();
                }
            }
            return true;
        }
        if (showDex || showTeam) {
            if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.I
                    || keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                showDex = false;
                showTeam = false;
            }
            return true;
        }

        if (dialogue != null) {
            if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                dialogueIndex++;
                if (dialogueIndex >= dialogue.length) {
                    dialogue = null;
                }
            } else if (keycode == Input.Keys.ESCAPE) {
                dialogue = null;
            }
            return true;
        }
        if (keycode == Input.Keys.ESCAPE) {
            paused = true;
            pauseIndex = 0;
            return true;
        }
        if (keycode == Input.Keys.F12) {
            game.captureScreenshot();
            showMessage("截图已保存（WildSpirit/screenshot.png）");
            return true;
        }
        if (keycode == Input.Keys.I) {
            showDex = true;
            return true;
        }
        if (keycode == Input.Keys.SPACE) {
            if (region.isExit(px, py)) {
                tryExit();
            }
            return true;
        }

        int dx = 0, dy = 0;
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) dy = 1;
        else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) dy = -1;
        else if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) dx = -1;
        else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) dx = 1;
        if (dx != 0 || dy != 0) {
            int nx = px + dx;
            int ny = py - dy;
            if (!region.blocked(nx, ny)) {
                px = nx;
                py = ny;
                game.state.playerX = px;
                game.state.playerY = py;
                afterMove();
            }
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        batch.dispose();
        white.dispose();
    }
}
