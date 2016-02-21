package net.sail.uhc.settings;

import net.sail.uhc.utils.WorldGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by brand on 12/30/2015.
 */
public class GameSettings {

    private final ConfigManager settingsManager;

    private Integer teamSize = 3;
    private Integer lobbyWaitTime = 120;
    private Integer timeUntilNextGame = 10;
    private Integer borderShrinkTime = 60;
    private Integer borderShrinkAmount = 10;
    private Integer peacefulTime = 600;
    private Integer arenaSize = 1000;
    private Integer maxPlayers = 60;
    private Integer minPlayers = 20;
    private Integer finalHealTime = 300;
    private String gameid = null;
    private Location lobbySpawn = null;
    private boolean permDay = true;
    private UUID host = null;

    public GameSettings(ConfigManager settingsManager) {
        this.settingsManager = settingsManager;

        if (settingsManager.get("teamsize") == null) {
            settingsManager.set("teamsize", teamSize);
        } else {
            teamSize = settingsManager.get("teamsize");
        }

        if (settingsManager.get("lobbywaittime") == null) {
            settingsManager.set("lobbywaittime", lobbyWaitTime);
        } else {
            lobbyWaitTime = settingsManager.get("lobbywaittime");
        }

        if (settingsManager.get("timeuntilnextgame") == null) {
            settingsManager.set("timeuntilnextgame", timeUntilNextGame);
        } else {
            timeUntilNextGame = settingsManager.get("timeuntilnextgame");
        }

        if (settingsManager.get("bordershrinktime") == null) {
            settingsManager.set("bordershrinktime", borderShrinkTime);
        } else {
            borderShrinkTime = settingsManager.get("bordershrinktime");
        }

        if (settingsManager.get("bordershrinkamount") == null) {
            settingsManager.set("bordershrinkamount", borderShrinkAmount);
        } else {
            borderShrinkAmount = settingsManager.get("bordershrinkamount");
        }

        if (settingsManager.get("peacefultime") == null) {
            settingsManager.set("peacefultime", peacefulTime);
        } else {
            peacefulTime = settingsManager.get("peacefultime");
        }

        if (settingsManager.get("arenasize") == null) {
            settingsManager.set("arenasize", arenaSize);
        } else {
            arenaSize = settingsManager.get("arenasize");
        }

        if (settingsManager.get("players.max") == null) {
            settingsManager.set("players.max", maxPlayers);
        } else {
            maxPlayers = settingsManager.get("players.max");
        }

        if (settingsManager.get("players.min") == null) {
            settingsManager.set("players.min", minPlayers);
        } else {
            minPlayers = settingsManager.get("players.min");
        }

        if (settingsManager.get("game_id") == null) {
            settingsManager.set("game_id", "UHC-0");
            gameid = "UHC-0";
        } else {
            gameid = settingsManager.get("game_id");
        }

        if (settingsManager.get("lobbyspawn") == null) {
        } else {
            lobbySpawn = parseLocation("lobbyspawn");
        }

        if (settingsManager.get("finalhealtime") == null) {
            settingsManager.set("finalhealtime", finalHealTime);
        } else {
            finalHealTime = settingsManager.get("finalhealtime");
        }

        if (settingsManager.get("permday") == null) {
            settingsManager.set("permday", "true");
        } else {
            if (settingsManager.get("permday").equals("true")) {
                permDay = true;
            } else {
                permDay = false;
            }
        }

        if (settingsManager.get("seeds") == null) {
        } else {
            for (String list : settingsManager.getStringList("seeds")){
                WorldGenerator.addSeed(Long.parseLong(list));
            }
        }
    }

    public void saveSettings() {
        settingsManager.set("teamsize", teamSize);
        settingsManager.set("lobbywaittime", lobbyWaitTime);
        settingsManager.set("timeuntilnextgame", timeUntilNextGame);
        settingsManager.set("bordershrinktime", borderShrinkTime);
        settingsManager.set("bordershrinkamount", borderShrinkAmount);
        settingsManager.set("peacefultime", peacefulTime);
        settingsManager.set("arenasize", arenaSize);
        settingsManager.set("players.max", maxPlayers);
        settingsManager.set("players.min", minPlayers);
        settingsManager.set("permday", Boolean.toString(permDay));
        settingsManager.set("finalhealtime", finalHealTime);
        if (lobbySpawn != null) {
            saveLocation(lobbySpawn, "lobbyspawn");
        }
    }


    public Integer getTeamSize() {
        return teamSize;
    }

    public Integer getLobbyWaitTime() {
        return lobbyWaitTime;
    }

    public Integer getTimeUntilNextGame() {
        return timeUntilNextGame;
    }

    public Integer getBorderShrinkTime() {
        return borderShrinkTime;
    }

    public Integer getBorderShrinkAmount() { return borderShrinkAmount; }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public Integer getMinPlayers() {
        return minPlayers;
    }

    public Location getLobbySpawn() {
        return lobbySpawn;
    }

    public Integer getFinalHealTime() { return finalHealTime; }

    public Integer getPeacefulTime() { return peacefulTime; }

    public Integer getArenaSize() { return arenaSize; }

    public String getGameid() { return gameid; }

    public boolean isHost(UUID p) { return (host == p); }

    public UUID getHost() { return host; }

    public void setLobbySpawn(Location location) {
        lobbySpawn = location;
    }

    public boolean isPermDay() { return permDay; }

    public void setMinPlayers(Integer amount) {
        minPlayers = amount;
    }

    public void setMaxPlayers(Integer amount) {
        maxPlayers = amount;
    }

    public void setTeamSize(Integer amount) {
        teamSize = amount;
    }

    public void setBorderShrinkTime(Integer amount) {
        borderShrinkTime = amount;
    }

    public void setBorderShrinkAmount(Integer amount) { borderShrinkAmount = amount; }

    public void setLobbyWaitTime(Integer amount) {
        lobbyWaitTime = amount;
    }

    public void setTimeUntilNextGame(Integer amount) {
        timeUntilNextGame = amount;
    }

    public void setPeacefulTime(Integer amount) { peacefulTime = amount; }

    public void setArenaSize(Integer amount) { arenaSize = amount; }

    public void setHost(UUID p) {
        host = p;
    }

    public void setFinalHealTime(Integer amount) { finalHealTime = amount; }

    public void setPermDay(boolean value) { permDay = value; }

    public void saveLocation(Location location, String section) {
        settingsManager.set(section + ".world", location.getWorld().getName());
        settingsManager.set(section + ".x", location.getX());
        settingsManager.set(section + ".y", location.getY());
        settingsManager.set(section + ".z", location.getZ());
    }

    public Location parseLocation(String location) throws IllegalArgumentException {
        World world = Bukkit.getWorld((String) settingsManager.get(location + ".world"));
        double x = settingsManager.get(location+".x");
        double y = settingsManager.get(location+".y");
        double z = settingsManager.get(location + ".z");
        return new Location(world, x, y, z);
    }
}
