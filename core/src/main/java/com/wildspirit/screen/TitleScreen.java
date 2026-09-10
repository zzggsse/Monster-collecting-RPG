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

/** 主菜单。 */
public class TitleScreen extends InputAdapter implements Screen {
    private final WildSpiritGame game;
    private OrthographicCamera cam;
    private Viewport viewport;
    private SpriteBatch batch;
    private Texture white;
    private int index;
    private String message = "";
    private float messageTimer;

    public TitleScreen(WildSpiritGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        cam = new OrthographicCamera();
        viewport = new FitViewport(960, 540, cam);
        viewport.apply();
        batch = new SpriteBatch();
        white = makeWhite();
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
        ScreenUtils.clear(0.05f, 0.07f, 0.12f, 1f);
        if (messageTimer > 0) {
            messageTimer -= delta;
        }
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        drawTitle();
        drawMenu();
        if (messageTimer > 0 && !message.isEmpty()) {
            game.assets.fontSmall().draw(batch, message, 360, 120);
        }
        batch.end();
    }

    private void drawTitle() {
        BitmapFont big = game.assets.fontBig();
        big.draw(batch, "荒野精灵", 330, 420);
        game.assets.fontSmall().draw(batch, "森林 · 沙漠 · 雪山——收集与成长RPG", 300, 380);
    }

    private void drawMenu() {
        String[] items = {"开始新游戏", "继续游戏", "退出游戏"};
        int baseY = 300;
        BitmapFont f = game.assets.fontSmall();
        for (int i = 0; i < items.length; i++) {
            String prefix = (i == index) ? "▶ " : "  ";
            f.draw(batch, prefix + items[i], 420, baseY - i * 40);
        }
        f.draw(batch, "↑↓选择  回车确定", 380, baseY - items.length * 40 - 24);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            index = (index + 2) % 3;
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            index = (index + 1) % 3;
        } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
            if (index == 0) {
                game.newGame();
            } else if (index == 1) {
                if (game.hasSave()) {
                    game.continueGame();
                } else {
                    message = "还没有存档，先开始新游戏吧！";
                    messageTimer = 3f;
                }
            } else {
                Gdx.app.exit();
            }
        }
        return true;
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
