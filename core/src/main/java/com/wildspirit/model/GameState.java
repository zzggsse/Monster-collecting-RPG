package com.wildspirit.model;

import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 游戏进行状态（可存档）。 */
public class GameState {
    public final List<Region> regions = Regions.all();
    public int regionIndex = 0;
    public int playerX;
    public int playerY;
    public final Set<String> badges = new HashSet<>();
    public final List<Creature> party = new ArrayList<>();
    public int activeIndex = 0;

    /** 见过的野外精灵。 */
    public final Set<String> seen = new HashSet<>();
    /** 捕获数量。 */
    public final Map<String, Integer> caughtCount = new HashMap<>();
    /** 回复药数量（回复100HP）。 */
    public int potions = 3;

    /** 已拾取的地图道具（区域索引:坐标）。 */
    public final Set<String> collected = new HashSet<>();

    public GameState() {
        Region r = regions.get(regionIndex);
        playerX = r.startX;
        playerY = r.startY;
        Creature starter = new Creature(Dex.starter(), 5);
        party.add(starter);
        markSeen(starter.species.key);
        markCaught(starter.species.key);
    }

    public Region region() {
        return regions.get(regionIndex);
    }

    public Creature active() {
        if (activeIndex < 0 || activeIndex >= party.size()) {
            for (int i = 0; i < party.size(); i++) {
                if (party.get(i).alive()) {
                    activeIndex = i;
                    break;
                }
            }
        }
        return party.get(activeIndex);
    }

    public boolean bossDefeated() {
        return badges.contains(region().bossBadge());
    }

    public void markBossDefeated() {
        badges.add(region().bossBadge());
    }

    public void markSeen(String key) {
        seen.add(key);
    }

    public void markCaught(String key) {
        caughtCount.merge(key, 1, Integer::sum);
        seen.add(key);
    }

    public int caughtOf(String key) {
        return caughtCount.getOrDefault(key, 0);
    }

    public void healParty() {
        for (Creature c : party) {
            c.heal();
            c.restorePP();
            c.clearStatus();
        }
    }
    public String tileKey(int x, int y) {
        return regionIndex + ":" + x + "," + y;
    }

    public boolean isCollected(int x, int y) {
        return collected.contains(tileKey(x, y));
    }

    public void collect(int x, int y) {
        collected.add(tileKey(x, y));
    }

    public boolean partyAlive() {
        for (Creature c : party) {
            if (c.alive()) {
                return true;
            }
        }
        return false;
    }

    public boolean atLastRegion() {
        return regionIndex == regions.size() - 1;
    }

    public void moveToNextRegion() {
        if (atLastRegion()) {
            return;
        }
        regionIndex++;
        Region r = regions.get(regionIndex);
        playerX = r.startX;
        playerY = r.startY;
        healParty();
    }

    // ---------- 存档 ----------

    public void saveTo(FileHandle fh) {
        StringBuilder sb = new StringBuilder();
        sb.append("v1\n");
        sb.append(regionIndex).append('\n');
        sb.append(playerX).append('\n');
        sb.append(playerY).append('\n');
        sb.append(activeIndex).append('\n');
        sb.append("badges:").append(String.join(",", badges)).append('\n');
        sb.append("potions:").append(potions).append('\n');
        sb.append("collected:").append(String.join(",", collected)).append('\n');
        sb.append("seen:").append(String.join(",", seen)).append('\n');
        for (Map.Entry<String, Integer> e : caughtCount.entrySet()) {
            sb.append("caught:").append(e.getKey()).append('=').append(e.getValue()).append('\n');
        }
        for (Creature c : party) {
            sb.append("creature:").append(c.species.key).append('|')
                    .append(c.name).append('|')
                    .append(c.level).append('|')
                    .append(c.hp).append('|')
                    .append(c.xp).append("|pp:").append(ppString(c)).append("|st:").append(c.status.name()).append('\n');
        }
        fh.writeString(sb.toString(), false, "UTF-8");
    }

    public static GameState loadFrom(FileHandle fh) {
        String text = fh.readString("UTF-8");
        if (text == null || text.isEmpty()) {
            return null;
        }
        GameState s = new GameState();
        s.party.clear();
        s.badges.clear();
        s.seen.clear();
        s.caughtCount.clear();
        String[] lines = text.split("\\r?\\n");
        int li = 0;
        boolean versionOk = li < lines.length && lines[li].startsWith("v1");
        if (!versionOk) {
            return null;
        }
        li++;
        s.regionIndex = Math.min(s.regions.size() - 1, Math.max(0, intOr(lines[li], 0)));
        li++;
        s.playerX = intOr(lines[li], 0);
        li++;
        s.playerY = intOr(lines[li], 0);
        li++;
        s.activeIndex = intOr(lines[li], 0);
        li++;
        for (; li < lines.length; li++) {
            String line = lines[li];
            if (line.startsWith("badges:")) {
                for (String k : line.substring("badges:".length()).split(",")) {
                    if (!k.isEmpty()) s.badges.add(k);
                }
            } else if (line.startsWith("seen:")) {
                for (String k : line.substring("seen:".length()).split(",")) {
                    if (!k.isEmpty()) s.seen.add(k);
                }
            } else if (line.startsWith("potions:")) {
                s.potions = Math.max(0, intOr(line.substring("potions:".length()), 0));
            } else if (line.startsWith("collected:")) {
                for (String k : line.substring("collected:".length()).split(",")) {
                    if (!k.isEmpty()) s.collected.add(k);
                }
            } else if (line.startsWith("caught:")) {
                String body = line.substring("caught:".length());
                int eq = body.indexOf('=');
                if (eq > 0) {
                    s.caughtCount.put(body.substring(0, eq), intOr(body.substring(eq + 1), 0));
                }
            } else if (line.startsWith("creature:")) {
                String[] parts = line.substring("creature:".length()).split("\\|");
                if (parts.length >= 4) {
                    Species spec = Dex.get(parts[0]);
                    if (spec == null) continue;
                    int lv = intOr(parts[2], 5);
                    Creature c = new Creature(spec, parts[1], lv);
                    c.hp = intOr(parts[3], c.maxHp);
                    if (parts.length >= 5) c.xp = intOr(parts[4], 0);
                    if (parts.length >= 6 && parts[5].startsWith("pp:")) {
                        String[] pps = parts[5].substring(3).split(",");
                        for (int i = 0; i < pps.length && i < c.ppLeft.length; i++) {
                            c.ppLeft[i] = Math.min(c.species.moves[i].pp, intOr(pps[i].trim(), c.species.moves[i].pp));
                        }
                    }
                    if (c.hp > c.maxHp) c.hp = c.maxHp;
                    if (parts.length >= 7 && parts[6].startsWith("st:")) {
                        for (Status st0 : Status.values()) {
                            if (st0.name().equals(parts[6].substring(3))) {
                                c.status = st0;
                                break;
                            }
                        }
                    }
                    s.markSeen(c.species.key);
                    s.party.add(c);
                }
            }
        }
        if (s.party.isEmpty()) {
            s.party.add(new Creature(Dex.starter(), 5));
        }
        return s;
    }

    private static String ppString(Creature c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < c.ppLeft.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(c.ppLeft[i]);
        }
        return sb.toString();
    }

    private static int intOr(String raw, int fallback) {
        try {
            return Integer.parseInt(raw);
        } catch (Exception e) {
            return fallback;
        }
    }
}
