package com.wildspirit.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/** 程序生成的芯片音效与背景音乐（无需外部素材）。 */
public final class AudioEngine {
    private static final int RATE = 22050;
    private static final double TWO_PI = Math.PI * 2;

    private final Map<String, Sound> sounds = new HashMap<>();
    private File tempDir;
    private Music regionMusic;
    private String currentTheme;

    public void dispose() {
        if (regionMusic != null) {
            regionMusic.stop();
            regionMusic.dispose();
            regionMusic = null;
        }
        for (Sound s : sounds.values()) {
            s.dispose();
        }
        sounds.clear();
    }

    /** 切换区域主题音乐（forest/desert/snow）。 */
    public void playRegionTheme(String theme) {
        if (theme.equals(currentTheme) && regionMusic != null) {
            return;
        }
        if (regionMusic != null) {
            regionMusic.stop();
            regionMusic.dispose();
            regionMusic = null;
        }
        currentTheme = theme;
        float vol;
        double[] wav;
        switch (theme) {
            case "desert":
                wav = musicDesert();
                vol = 0.40f;
                break;
            case "snow":
                wav = musicSnow();
                vol = 0.38f;
                break;
            default:
                wav = musicForest();
                vol = 0.42f;
                break;
        }
        String file = storeWav(wav);
        if (file != null) {
            regionMusic = Gdx.audio.newMusic(Gdx.files.absolute(file));
            regionMusic.setLooping(true);
            regionMusic.setVolume(vol);
            regionMusic.play();
        }
    }

    public void select() {
        sound(0.12f, 880.0, 660.0, 0.55f);
    }

    public void hit() {
        sound(0.16f, 320.0, 150.0, 0.65f);
    }

    public void hurt() {
        sound(0.18f, 430.0, 110.0, 0.60f);
    }

    public void capture() {
        notes(new double[]{523.0, 659.0, 784.0}, 0.09, 0.60f);
    }

    public void levelup() {
        notes(new double[]{392.0, 523.0, 659.0, 784.0}, 0.12, 0.55f);
    }

    public void itemPickup() {
        notes(new double[]{880.0, 1174.0}, 0.08, 0.55f);
    }

    public void heal() {
        notes(new double[]{659.0, 784.0, 1046.0}, 0.14, 0.50f);
    }

    public void encounter() {
        notes(new double[]{330.0, 262.0, 330.0}, 0.12, 0.55f);
    }

    public void escape() {
        notes(new double[]{659.0, 523.0, 392.0}, 0.12, 0.50f);
    }

    public void win() {
        notes(new double[]{523.0, 659.0, 784.0, 1046.0}, 0.16, 0.55f);
    }

    public void lose() {
        notes(new double[]{392.0, 330.0, 262.0, 196.0}, 0.22, 0.50f);
    }

    private void notes(double[] freqs, double each, float vol) {
        int n = (int) (freqs.length * each * RATE);
        int total = (int) (freqs.length * each * RATE + RATE * 0.05);
        double[] samples = new double[total];
        int pos = 0;
        for (int i = 0; i < freqs.length; i++) {
            int len = (int) (each * RATE);
            for (int j = 0; j < len && pos < total; j++, pos++) {
                double t = j / (double) RATE;
                double env = 1.0 - (double) j / len;
                samples[pos] += square(freqs[i], t) * env * 0.5;
            }
        }
        play(name(freqs), samples, vol);
    }

    private void sound(float dur, double pitch, float vol) {
        sound(dur, pitch, pitch, vol);
    }

    private void sound(float durSec, double start, double end, float vol) {
        int total = (int) (durSec * RATE);
        double[] samples = new double[total];
        for (int i = 0; i < total; i++) {
            double t = i / (double) RATE;
            double f = start + (end - start) * (i / (double) total);
            double env = 1.0 - (i / (double) total);
            samples[i] = square(f, t) * env * 0.55;
        }
        play("s", samples, vol);
    }

    private void play(String name, double[] samples, float vol) {
        Sound s = sounds.get(name);
        if (s == null) {
            String file = storeWav(samples);
            if (file == null) {
                return;
            }
            s = Gdx.audio.newSound(Gdx.files.absolute(file));
            sounds.put(name, s);
        }
        s.play(vol);
    }

    private String name(double[] freqs) {
        StringBuilder sb = new StringBuilder("n");
        for (double f : freqs) {
            sb.append((int) f).append('_');
        }
        return sb.toString();
    }

    private String storeWav(double[] samples) {
        try {
            if (tempDir == null) {
                tempDir = new File(System.getProperty("java.io.tmpdir"), "wildspirit_audio");
                tempDir.mkdirs();
            }
            short[] buf = new short[samples.length];
            for (int i = 0; i < samples.length; i++) {
                double v = Math.max(-1.0, Math.min(1.0, samples[i]));
                buf[i] = (short) (v * Short.MAX_VALUE);
            }
            byte[] wav = wavBytes(buf);
            File f = File.createTempFile("ws_", ".wav", tempDir);
            Files.write(f.toPath(), wav);
            return f.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] wavBytes(short[] pcm) {
        int dataLen = pcm.length * 2;
        ByteBuffer out = ByteBuffer.allocate(44 + dataLen).order(ByteOrder.LITTLE_ENDIAN);
        out.put("RIFF".getBytes());
        out.putInt(36 + dataLen);
        out.put("WAVE".getBytes());
        out.put("fmt ".getBytes());
        out.putInt(16);
        out.putShort((short) 1);
        out.putShort((short) 1);
        out.putInt(RATE);
        out.putInt(RATE * 2);
        out.putShort((short) 2);
        out.putShort((short) 16);
        out.put("data".getBytes());
        out.putInt(dataLen);
        for (short s : pcm) {
            out.putShort(s);
        }
        return out.array();
    }

    private static double square(double freq, double t) {
        double phase = (freq * t) % 1.0;
        return phase < 0.5 ? 1.0 : -1.0;
    }

    // ---------- 区域主题 ----------

    private double[] musicForest() {
        // C 大调轻快旋律
        int[] lead = {72, 76, 79, 76, 74, 76, 79, 81, 84, 81, 79, 76, 74, 76, 74, 72,
                72, 76, 79, 76, 79, 81, 84, 86, 84, 81, 79, 76, 74, 72, 71, 72};
        int[] bass = {48, 0, 52, 0, 55, 0, 52, 0, 48, 0, 52, 0, 55, 0, 57, 0,
                60, 0, 57, 0, 55, 0, 52, 0, 48, 0, 43, 0, 48, 0, 47, 0};
        double beat = 0.22;
        return tune(lead, bass, beat, 0.14, 0.09);
    }

    private double[] musicDesert() {
        // A 小调异域感
        int[] lead = {69, 72, 76, 72, 67, 72, 76, 81, 84, 81, 76, 72, 69, 72, 69, 67,
                69, 72, 76, 72, 67, 72, 76, 79, 81, 79, 76, 72, 67, 69, 67, 64};
        int[] bass = {45, 0, 48, 0, 52, 0, 55, 0, 45, 0, 52, 0, 55, 0, 57, 0,
                60, 0, 57, 0, 55, 0, 52, 0, 45, 0, 48, 0, 45, 0, 40, 0};
        double beat = 0.22;
        return tune(lead, bass, beat, 0.14, 0.09);
    }

    private double[] musicSnow() {
        // 悠扬慢板
        int[] lead = {72, 0, 79, 0, 84, 0, 79, 0, 88, 0, 84, 0, 79, 0, 76, 0,
                79, 0, 84, 0, 88, 0, 84, 0, 79, 0, 76, 0, 72, 0, 71, 0};
        int[] bass = {48, 0, 55, 0, 60, 0, 55, 0, 48, 0, 55, 0, 64, 0, 55, 0,
                60, 0, 64, 0, 72, 0, 64, 0, 60, 0, 55, 0, 48, 0, 47, 0};
        double beat = 0.28;
        return tune(lead, bass, beat, 0.13, 0.08);
    }

    private double[] tune(int[] lead, int[] bass, double beat, double leadVol, double bassVol) {
        double dur = lead.length * beat * 2;
        double[] samples = new double[(int) (dur * RATE)];
        for (int i = 0; i < lead.length; i++) {
            addNote(samples, midi(lead[i]), i * beat, beat, leadVol);
            if (i % 2 == 0) {
                addNote(samples, midi(bass[i]), i * beat, beat * 2, bassVol);
            }
        }
        for (int i = 0; i < lead.length; i++) {
            addNote(samples, midi(lead[i]), (lead.length + i) * beat, beat, leadVol);
            if (i % 2 == 0) {
                addNote(samples, midi(bass[i]), (lead.length + i) * beat, beat * 2, bassVol);
            }
        }
        return samples;
    }

    private double midi(int note) {
        if (note == 0) {
            return 0;
        }
        return 440.0 * Math.pow(2.0, (note - 69) / 12.0);
    }

    private void addNote(double[] out, double freq, double start, double len, double vol) {
        int s = (int) (start * RATE);
        int n = (int) (len * RATE);
        if (freq <= 0) {
            return;
        }
        for (int i = 0; i < n && s + i < out.length; i++) {
            double t = (s + i) / (double) RATE;
            double env = 1.0 - (i / (double) n) * 0.6;
            out[s + i] += square(freq, t) * env * vol;
        }
    }
}
