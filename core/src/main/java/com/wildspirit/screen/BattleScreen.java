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
import com.wildspirit.model.Creature;
import com.wildspirit.model.Dex;
import com.wildspirit.model.Move;
import com.wildspirit.model.Species;
import com.wildspirit.model.Status;
import com.wildspirit.model.Types;

import java.util.ArrayDeque;
import java.util.Deque;

/** 回合制战斗界面。 */
public class BattleScreen extends InputAdapter implements Screen {

    public enum Outcome { WON, LOST, ESCAPED }

    public interface ResultListener {
        void ended(Outcome outcome);
    }

    private enum Phase { ACTION, MOVE, ITEM, SWITCH, MESSAGE, OVER }

    private final WildSpiritGame game;
    private final Creature enemy;
    private final boolean boss;
    private final ResultListener listener;

    private OrthographicCamera cam;
    private Viewport viewport;
    private SpriteBatch batch;
    private Texture white;

    private Phase phase = Phase.MESSAGE;
    private final Deque<String> msgs = new ArrayDeque<>();
    private int actionIndex;
    private int moveIndex;
    private int switchIndex;
    private boolean needSwitch;
    private boolean gameOver;
    private Outcome outcome = Outcome.WON;
    private float overTimer;
    private float evolveFlash;
    private String evolveCaption;

    public BattleScreen(WildSpiritGame game, Creature enemy, boolean boss, ResultListener listener) {
        this.game = game;
        this.enemy = enemy;
        this.boss = boss;
        this.listener = listener;
        String regionName = game.state.region().name;
        if (boss) {
            push(regionName + "的头目 " + enemy.name + " Lv." + enemy.level + " 挡在你面前！",
                    "击败它才能继续前进！");
        } else {
            push("野生的 " + enemy.name + " Lv." + enemy.level + " 出现了！");
        }
        game.state.markSeen(enemy.species.key);
        game.audio.encounter();
    }

    private void push(String... lines) {
        for (String l : lines) {
            if (l != null && !l.isEmpty()) {
                msgs.addLast(l);
            }
        }
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
        ScreenUtils.clear(0.10f, 0.10f, 0.16f, 1f);
        if (phase == Phase.OVER) {
            overTimer -= delta;
            if (overTimer <= 0) {
                listener.ended(outcome);
            }
        }
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        drawBackground();
        drawEnemy();
        drawPlayer();
        batch.end();
        drawUi();
        if (evolveFlash > 0) {
            evolveFlash -= delta;
            batch.begin();
            float a = (0.5f + 0.5f * (float) Math.sin(evolveFlash * 12f));
            batch.setColor(1f, 1f, 1f, a * 0.85f);
            batch.draw(white, 0, 0, 960, 540);
            batch.setColor(1f, 1f, 1f, 1f);
            if (evolveCaption != null) {
                game.assets.fontBig().draw(batch, evolveCaption, 230, 200);
            }
            batch.end();
        }
    }

    private void drawBackground() {
        batch.setColor(0.55f, 0.76f, 0.45f, 1f);
        batch.draw(white, 0, 120, 960, 220);
        batch.setColor(0.35f, 0.55f, 0.42f, 1f);
        batch.draw(white, 0, 80, 960, 40);
        batch.setColor(1f, 1f, 1f, 1f);
    }

    private void drawEnemy() {
        batch.draw(game.assets.creatureIcon(enemy.species.key), 640, 250, 200, 200);
        BitmapFont f = game.assets.fontSmall();
        String label = (boss ? "头目 " : "野生的 ") + enemy.name + "  Lv." + enemy.level + statusTag(enemy);
        hpBar(620, 460, 300, 16, enemy.hp / (float) enemy.maxHp);
        f.draw(batch, label, 620, 505);
    }

    private void drawPlayer() {
        Creature a = game.state.active();
        if (a == null) {
            return;
        }
        batch.draw(game.assets.creatureIcon(a.species.key), 180, 90, 170, 170);
        BitmapFont f = game.assets.fontSmall();
        hpBar(180, 288, 280, 16, a.hp / (float) a.maxHp);
        f.draw(batch, a.name + "  Lv." + a.level + statusTag(a), 180, 330);
    }

    private String statusTag(Creature c) {
        return c.status == Status.NONE ? "" : "（" + c.status.cnName + "）";
    }

    private void hpBar(float x, float y, float w, float h, float frac) {
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(white, x, y, w, h);
        float f = Math.max(0f, Math.min(1f, frac));
        batch.setColor(f > 0.5f ? 0.25f : f > 0.25f ? 0.95f : 0.9f,
                f > 0.5f ? 0.85f : f > 0.25f ? 0.75f : 0.3f, 0.25f, 1f);
        batch.draw(white, x, y, w * f, h);
        batch.setColor(1, 1, 1, 1);
    }

    private void drawUi() {
        batch.begin();
        switch (phase) {
            case ACTION:
                drawActionMenu();
                break;
            case MOVE:
                drawMoveMenu();
                break;
            case ITEM:
                drawItemMenu();
                break;
            case SWITCH:
                drawSwitchMenu();
                break;
            case MESSAGE:
                drawMessageBox(msgs.isEmpty() ? "…" : msgs.peekFirst());
                break;
            case OVER:
                drawMessageBox(overText());
                break;
        }
        batch.end();
    }

    private String overText() {
        switch (outcome) {
            case WON:
                return "战斗胜利！";
            case LOST:
                return "战斗失败……";
            default:
                return "成功逃脱！";
        }
    }

    private void drawBox(float x, float y, float w, float h) {
        batch.setColor(0, 0, 0, 0.78f);
        batch.draw(white, x, y, w, h);
        batch.setColor(1, 1, 1, 1);
        batch.draw(white, x, y, w, 2);
        batch.draw(white, x, y + h - 2, w, 2);
        batch.draw(white, x, y, 2, h);
        batch.draw(white, x + w - 2, y, 2, h);
    }

    private void drawMessageBox(String text) {
        drawBox(60, 30, 840, 120);
        game.assets.fontSmall().draw(batch, text, 90, 110);
        if (phase == Phase.MESSAGE) {
            game.assets.fontSmall().draw(batch, "回车/空格 继续…", 90, 60);
        }
    }

    private void drawActionMenu() {
        String[] items = {"技能", "捕捉", "道具", "换宠", "逃跑"};
        drawBox(600, 30, 320, 250);
        BitmapFont f = game.assets.fontSmall();
        for (int i = 0; i < items.length; i++) {
            String prefix = (actionIndex == i) ? "▶ " : "  ";
            f.draw(batch, prefix + items[i], 620, 250 - i * 42);
        }
    }

    private void drawMoveMenu() {
        Creature own = game.state.active();
        Move[] moves = own.species.moves;
        drawBox(90, 140, 780, 280);
        BitmapFont f = game.assets.fontSmall();
        for (int i = 0; i < moves.length; i++) {
            Move m = moves[i];
            String prefix = (moveIndex == i) ? "▶ " : "  ";
            String ppTxt = own.ppLeft[i] <= 0 ? "（PP不足）" : " PP " + own.ppLeft[i] + "/" + m.pp;
            f.draw(batch, prefix + m.name + "（" + m.type.cnName() + "·威力" + m.power + "）" + ppTxt,
                    120, 380 - i * 48);
        }
        f.draw(batch, "↑↓选择  回车使用  ESC返回", 120, 380 - moves.length * 48 - 10);
    }

    private void drawItemMenu() {
        drawBox(200, 160, 560, 220);
        BitmapFont f = game.assets.fontSmall();
        f.draw(batch, "回复药 ×" + game.state.potions, 240, 330);
        f.draw(batch, "回复当前精灵 100 HP", 240, 300);
        f.draw(batch, "回车 使用    ESC 返回", 240, 260);
    }

    private void drawSwitchMenu() {
        drawBox(120, 90, 720, 340);
        BitmapFont f = game.assets.fontSmall();
        int i = 0;
        for (Creature c : game.state.party) {
            String mark = (i == switchIndex) ? "▶ " : "  ";
            String hp = c.alive() ? (c.hp + "/" + c.maxHp) : "无法战斗";
            f.draw(batch, mark + c.name + "  Lv." + c.level + "   HP " + hp,
                    160, 400 - i * 40);
            i++;
        }
        f.draw(batch, "↑↓选择  回车确定  ESC返回", 160, 400 - i * 40 - 20);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (phase) {
            case ACTION:
                if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
                    actionIndex = (actionIndex + 4) % 5;
                } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
                    actionIndex = (actionIndex + 1) % 5;
                } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    doAction();
                }
                return true;
            case MOVE:
                if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
                    moveIndex = Math.max(0, moveIndex - 1);
                } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
                    moveIndex = Math.min(game.state.active().species.moves.length - 1, moveIndex + 1);
                } else if (keycode == Input.Keys.ESCAPE) {
                    phase = Phase.ACTION;
                } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    doMove();
                }
                return true;
            case ITEM:
                if (keycode == Input.Keys.ESCAPE) {
                    phase = Phase.ACTION;
                } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    doItem();
                }
                return true;
            case SWITCH:
                if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
                    switchIndex = Math.max(0, switchIndex - 1);
                } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
                    switchIndex = Math.min(game.state.party.size() - 1, switchIndex + 1);
                } else if (keycode == Input.Keys.ESCAPE) {
                    phase = Phase.ACTION;
                } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    doSwitch();
                }
                return true;
            case MESSAGE:
                if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    advanceMessage();
                }
                return true;
            case OVER:
                return true;
            default:
                return true;
        }
    }

    /** 睡眠/冰冻/麻痹是否让当前精灵无法行动。 */
    private boolean playerBlockedByStatus() {
        Creature own = game.state.active();
        switch (own.status) {
            case SLEEP:
                own.statusTurns--;
                if (own.statusTurns <= 0) {
                    own.clearStatus();
                    push(own.name + " 醒了过来！");
                    return false;
                }
                push(own.name + " 正在沉睡……");
                return true;
            case FREEZE:
                if (Math.random() < 0.4) {
                    own.clearStatus();
                    push(own.name + " 挣脱了冰冻！");
                    return false;
                }
                push(own.name + " 被冻住了，无法行动！");
                return true;
            case PARALYZE:
                if (Math.random() < 0.25) {
                    push(own.name + " 因麻痹无法行动！");
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    private void doAction() {
        Creature own = game.state.active();
        if (own == null || !own.alive()) {
            switchIndex = nextAliveIndex();
            phase = Phase.SWITCH;
            return;
        }
        if (playerBlockedByStatus()) {
            enemyTurn();
            if (!gameOver) {
                phase = Phase.MESSAGE;
            }
            return;
        }
        switch (actionIndex) {
            case 0:
                moveIndex = 0;
                phase = Phase.MOVE;
                break;
            case 1:
                tryCapture();
                break;
            case 2:
                phase = Phase.ITEM;
                break;
            case 3:
                switchIndex = nextAliveIndex();
                phase = Phase.SWITCH;
                break;
            case 4:
                tryRun();
                break;
            default:
                break;
        }
    }

    private void doMove() {
        Creature own = game.state.active();
        Move[] moves = own.species.moves;
        if (moveIndex < 0 || moveIndex >= moves.length) {
            return;
        }
        if (!own.anyPP()) {
            struggle();
            return;
        }
        Move m = moves[moveIndex];
        if (own.ppLeft[moveIndex] <= 0) {
            game.audio.select();
            push(m.name + " 的 PP 用光了，选择其他技能吧！");
            phase = Phase.MESSAGE;
            return;
        }
        own.ppLeft[moveIndex]--;
        int dmg = damage(own, enemy, m);
        enemy.hp = Math.max(0, enemy.hp - dmg);
        game.audio.hit();
        push(own.name + " 使用了 " + m.name + "！");
        push(effText(m, enemy.species.type, dmg));
        if (enemy.hp <= 0) {
            push(enemy.name + " 倒下了！");
            win();
            return;
        }
        if (m.status != Status.NONE && Math.random() < m.chance) {
            if (enemy.applyStatus(m.status)) {
                push(enemy.name + " 陷入了" + m.status.cnName + "！");
            }
        }
        tickOwn();
        if (own.hp <= 0) {
            push(own.name + " 倒下了！");
            if (anyAliveExceptActive()) {
                needSwitch = true;
            } else {
                lose();
                return;
            }
        }
        enemyTurn();
        tickEnemy();
        if (!gameOver) {
            phase = Phase.MESSAGE;
        }

    }
    /** 所有技能 PP 耗尽时的最後一击。 */
    private void struggle() {
        Creature own = game.state.active();
        int dmg = Math.max(1, own.level * 2 + 4);
        enemy.hp = Math.max(0, enemy.hp - dmg);
        int recoil = Math.max(1, own.maxHp / 8);
        own.hp = Math.max(0, own.hp - recoil);
        game.audio.hit();
        push(own.name + " 使出了最后的挣扎！");
        push("造成 " + dmg + " 伤害，自己也受到 " + recoil + " 点反伤！");
        if (enemy.hp <= 0) {
            push(enemy.name + " 倒下了！");
            win();
            return;
        }
        if (own.hp <= 0) {
            push(own.name + " 倒下了！");
            if (!anyAliveExceptActive()) {
                lose();
                return;
            }
            needSwitch = true;
            phase = Phase.MESSAGE;
            return;
        }
        enemyTurn();
        if (!gameOver) {
            phase = Phase.MESSAGE;
        }
    }

    /** 我方回合结束的中毒/灼伤掉血。 */
    private void tickOwn() {
        Creature own = game.state.active();
        if (own == null || own.hp <= 0 || own.status.dotFrac <= 0) {
            return;
        }
        int d = Math.max(1, (int) (own.maxHp * own.status.dotFrac));
        own.hp = Math.max(0, own.hp - d);
        game.audio.hurt();
        push(own.name + " 被" + own.status.cnName + "侵蚀，损失 " + d + " 体力！");
    }

    /** 敌方回合结束的中毒/灼伤掉血。 */
    private void tickEnemy() {
        if (gameOver || enemy.hp <= 0 || enemy.status.dotFrac <= 0) {
            return;
        }
        int d = Math.max(1, (int) (enemy.maxHp * enemy.status.dotFrac));
        enemy.hp = Math.max(0, enemy.hp - d);
        game.audio.hurt();
        push(enemy.name + " 被" + enemy.status.cnName + "侵蚀，损失 " + d + " 体力！");
        if (enemy.hp <= 0) {
            push(enemy.name + " 倒下了！");
            win();
        }
    }

    private void doItem() {
        Creature own = game.state.active();
        if (game.state.potions <= 0) {
            push("已经没有回复药了……");
            phase = Phase.MESSAGE;
            return;
        }
        if (own.hp >= own.maxHp) {
            push(own.name + " 体力已满，无法使用！");
            phase = Phase.MESSAGE;
            return;
        }
        game.state.potions--;
        own.hp = Math.min(own.maxHp, own.hp + 100);
        game.audio.heal();
        push("使用了回复药，" + own.name + " 恢复了100体力！");
        enemyTurn();
        if (!gameOver) {
            phase = Phase.MESSAGE;
        }
    }

    private int nextAliveIndex() {
        for (int i = 0; i < game.state.party.size(); i++) {
            if (game.state.party.get(i).alive()) {
                return i;
            }
        }
        return 0;
    }

    private void enemyTurn() {
        Creature own = game.state.active();
        if (own == null || !own.alive()) {
            needSwitch = true;
            return;
        }
        Move m = enemy.strongestMove();
        Move st = enemyStatusMove();
        if (st != null && own.status == Status.NONE && Math.random() < 0.5) {
            m = st;
        }
        int dmg = damage(enemy, own, m);
        own.hp = Math.max(0, own.hp - dmg);
        game.audio.hurt();
        push(enemy.name + " 使用了 " + m.name + "！");
        push(effText(m, own.species.type, dmg));
        if (m.status != Status.NONE && Math.random() < m.chance) {
            if (own.applyStatus(m.status)) {
                push(own.name + " 陷入了" + m.status.cnName + "！");
            }
        }
        if (own.hp <= 0) {
            push(own.name + " 倒下了！");
            if (anyAliveExceptActive()) {
                needSwitch = true;
            } else {
                lose();
            }
        }
    }

    private Move enemyStatusMove() {
        for (Move m : enemy.species.moves) {
            if (m.status != Status.NONE) {
                return m;
            }
        }
        return null;
    }


    private boolean anyAliveExceptActive() {
        for (Creature c : game.state.party) {
            if (c.alive()) {
                return true;
            }
        }
        return false;
    }

    private void tryCapture() {
        float chance = 0.30f + (1f - enemy.hp / (float) enemy.maxHp) * 0.45f;
        if (Math.random() < chance) {
            if (game.state.party.size() >= 8) {
                push("队伍已满，无法捕捉 " + enemy.name + "！");
                enemyTurn();
                if (!gameOver) {
                    phase = Phase.MESSAGE;
                }
            } else {
                Creature caught = new Creature(enemy.species, enemy.level);
                caught.hp = caught.maxHp;
                game.state.party.add(caught);
                game.state.markCaught(enemy.species.key);
                game.audio.capture();
                push("成功捕捉了 " + enemy.name + "！");
                win();
            }
        } else {
            game.audio.select();
            push("捕捉失败……" + enemy.name + " 挣脱了！");
            enemyTurn();
            if (!gameOver) {
                phase = Phase.MESSAGE;
            }
        }
    }

    private void tryRun() {
        if (boss) {
            push("头目战！无法逃跑！");
            enemyTurn();
            if (!gameOver) {
                phase = Phase.MESSAGE;
            }
        } else if (Math.random() < 0.75) {
            game.audio.escape();
            push("成功逃脱！");
            gameOver = true;
            outcome = Outcome.ESCAPED;
            finishEnd();
        } else {
            game.audio.hurt();
            push("逃跑失败……");
            enemyTurn();
            if (!gameOver) {
                phase = Phase.MESSAGE;
            }
        }
    }

    private void doSwitch() {
        Creature target = game.state.party.get(switchIndex);
        if (!target.alive()) {
            return;
        }
        if (target == game.state.active()) {
            return;
        }
        game.state.activeIndex = switchIndex;
        game.audio.select();
        push("换上了 " + target.name + "！");
        enemyTurn();
        if (!gameOver) {
            phase = Phase.MESSAGE;
        }
    }

    private void win() {
        if (boss) {
            game.state.markBossDefeated();
        }
        game.audio.win();
        pumpXp();
        push("获得了经验值！");
        gameOver = true;
        outcome = Outcome.WON;
        finishEnd();
    }

    private void pumpXp() {
        int xp = enemy.species.baseHp + enemy.level * 6;
        boolean leveled = false;
        for (Creature c : game.state.party) {
            if (c.alive()) {
                int before = c.level;
                String beforeKey = c.species.key;
                c.gainXp(xp);
                if (c.level > before) {
                    leveled = true;
                    game.audio.levelup();
                }
                if (!c.species.key.equals(beforeKey)) {
                    Species pre = Dex.get(beforeKey);
                    String oldName = pre == null ? c.name : pre.name;
                    push(oldName + " 进化成了 " + c.name + "！能力大幅提升！");
                    evolveFlash = 2.6f;
                    evolveCaption = oldName + " 正在进化成 " + c.name + "！";
                }
            }
        }
        if (leveled) {
            push("似乎有精灵升级了！");
        }
    }

    private void lose() {
        game.audio.lose();
        push("你没有可用的精灵了……");
        gameOver = true;
        outcome = Outcome.LOST;
        finishEnd();
    }

    private void finishEnd() {
        phase = Phase.OVER;
        overTimer = evolveFlash > 0 ? 3.6f : 1.3f;
    }

    private int damage(Creature atk, Creature def, Move m) {
        float eff = m.type.effectiveness(def.species.type);
        float stab = (m.type == atk.species.type) ? 1.2f : 1f;
        float raw = ((2f * atk.level / 5f + 2f) * m.power * atk.atk / (float) def.def) / 50f + 2f;
        raw *= stab * eff * (0.85f + (float) Math.random() * 0.15f);
        return Math.max(1, (int) raw);
    }

    private String effText(Move m, Types def, int dmg) {
        float eff = m.type.effectiveness(def);
        String base = "造成 " + dmg + " 点伤害！";
        if (eff > 1.2f) {
            return "效果拔群！" + base;
        }
        if (eff < 0.8f) {
            return "效果不佳……" + base;
        }
        return base;
    }

    private void advanceMessage() {
        if (phase != Phase.MESSAGE) {
            return;
        }
        msgs.pollFirst();
        if (msgs.isEmpty()) {
            if (needSwitch) {
                needSwitch = false;
                phase = Phase.SWITCH;
                switchIndex = nextAliveIndex();
            } else {
                phase = Phase.ACTION;
                actionIndex = 0;
            }
        }
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
