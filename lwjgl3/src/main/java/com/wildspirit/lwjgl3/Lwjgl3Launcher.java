package com.wildspirit.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.wildspirit.WildSpiritGame;

/** 桌面版启动器。 */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("荒野精灵");
        cfg.setWindowedMode(960, 540);
        cfg.setResizable(false);
        new Lwjgl3Application(new WildSpiritGame(), cfg);
    }
}
