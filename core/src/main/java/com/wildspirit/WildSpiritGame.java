package com.wildspirit;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.wildspirit.audio.AudioEngine;
import com.wildspirit.gfx.Assets;
import com.wildspirit.model.GameState;
import com.wildspirit.screen.BattleScreen.Outcome;
import com.wildspirit.screen.ExplorationScreen;
import com.wildspirit.screen.TitleScreen;

/** 游戏主入口。 */
public class WildSpiritGame extends Game {
    public Assets assets;
    public AudioEngine audio;
    public GameState state;

    @Override
    public void create() {
        assets = new Assets();
        audio = new AudioEngine();
        if (System.getenv("WILDRUN_AUTO") != null) {
            newGame();
        } else {
            setScreen(new TitleScreen(this));
        }
        scheduleAutoCloseIfRequested();
    }

    public FileHandle saveFile() {
        return Gdx.files.external("WildSpirit/save.txt");
    }

    public boolean hasSave() {
        return saveFile().exists();
    }

    public void newGame() {
        state = new GameState();
        assets.buildTiles(state.region());
        showExploration();
    }

    public void continueGame() {
        GameState loaded = GameState.loadFrom(saveFile());
        if (loaded == null) {
            return;
        }
        state = loaded;
        assets.buildTiles(state.region());
        showExploration();
    }

    public void saveGame() {
        FileHandle fh = saveFile();
        fh.parent().mkdirs();
        state.saveTo(fh);
    }

    public void showTitle() {
        setScreen(new TitleScreen(this));
    }

    public void showExploration() {
        assets.buildTiles(state.region());
        audio.playRegionTheme(state.region().key);
        setScreen(new ExplorationScreen(this));
    }

    public int bossLevel() {
        return 9 + state.regionIndex * 7;
    }

    public int wildMaxLevel() {
        return 6 + state.regionIndex * 7;
    }

    /** 战斗结束回调。 */
    public void onBattleEnded(Outcome outcome) {
        if (outcome == Outcome.LOST) {
            state.healParty();
            state.playerX = state.region().startX;
            state.playerY = state.region().startY;
        }
        if (outcome == Outcome.WON && state.bossDefeated()) {
            saveGame();
        }
        showExploration();
    }

    /** 截屏保存到外部目录（F12 或测试环境变量触发）。 */
    public void captureScreenshot() {
        try {
            Pixmap pm = Pixmap.createFromFrameBuffer(0, 0,
                    Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
            // OpenGL 帧缓冲行序自底向上，转换为 PNG 前需上下翻转，否则截图颠倒
            int w = pm.getWidth(), h = pm.getHeight();
            for (int y = 0; y < h / 2; y++) {
                for (int x = 0; x < w; x++) {
                    int top = pm.getPixel(x, y);
                    int bottom = pm.getPixel(x, h - 1 - y);
                    pm.drawPixel(x, y, bottom);
                    pm.drawPixel(x, h - 1 - y, top);
                }
            }
            FileHandle fh = Gdx.files.external("WildSpirit/screenshot.png");
            fh.parent().mkdirs();
            PixmapIO.writePNG(fh, pm);
            pm.dispose();
        } catch (Exception ignored) {
        }
    }

    private void scheduleAutoCloseIfRequested() {
        String shotSec = System.getenv("WILDRUN_SHOT_SECONDS");
        if (shotSec != null) {
            try {
                final float after = Float.parseFloat(shotSec);
                Thread shooter = new Thread(() -> {
                    long ms = (long) (after * 1000);
                    long start = System.currentTimeMillis();
                    while (System.currentTimeMillis() - start < ms) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    Gdx.app.postRunnable(() -> captureScreenshot());
                });
                shooter.setDaemon(true);
                shooter.start();
            } catch (NumberFormatException ignored) {
            }
        }
        String sec = System.getenv("WILDRUN_SECONDS");
        if (sec == null) {
            return;
        }
        try {
            float seconds = Float.parseFloat(sec);
            Thread closer = new Thread(() -> {
                long ms = (long) (seconds * 1000);
                long start = System.currentTimeMillis();
                while (System.currentTimeMillis() - start < ms) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                Gdx.app.postRunnable(() -> Gdx.app.exit());
            });
            closer.setDaemon(true);
            closer.start();
        } catch (NumberFormatException ignored) {
        }
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().hide();
        }
        assets.dispose();
        audio.dispose();
    }
}

