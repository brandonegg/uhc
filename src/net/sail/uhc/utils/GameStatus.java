package net.sail.uhc.utils;

import me.Hippo.Plugin.API;
import net.sail.uhc.UHCCore;
import net.sail.uhc.manager.GameManager;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.settings.GameSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by brand on 12/30/2015.
 */
public class GameStatus {

    public enum Status {
        IN_LOBBY(ChatColor.GREEN, "In lobby..."), LOBBY_COUNTDOWN(ChatColor.GREEN, "Countdown begins,"), STARTING(ChatColor.YELLOW, "Sorting players and teleporting"), IN_GAME(ChatColor.RED, "In-Game"), COUNTDOWN_UNTIL_LOBBY(ChatColor.RED, "Waiting until next game...");

        private String name;
        private ChatColor color;

        Status(ChatColor color, String name) {
            this.color = color;
            this.name = name;
        }

        public String getName() {
            return color + name;
        }
    }
    private BukkitTask lobbyCountDownRunnable = null;
    private BukkitTask borderShrinkCountDownRunnable = null;

    private final Messaging messaging;
    private final TeamManager teamManager;
    private final UHCCore uhcCore;
    private final GameSettings gameSettings;
    private final GameUtils gameUtils;
    private final WorldGenerator worldGenerator;
    private World uhcMap = null;

    private GameManager gameManager = null;

    private boolean lobbyCountdownActive = false;
    private boolean autoShrinkEnabled = false;
    private boolean finalHealEnabled = true;
    private Integer lobbyCountdownTime;
    private Status currentStatus = Status.COUNTDOWN_UNTIL_LOBBY;
    private Integer gameCountdownTime;

    private Integer currentBorderSize;
    private Integer timeUntilBorderShrink = 0;

    private ArrayList<UUID> spectators = new ArrayList<UUID>();

    public GameStatus(UHCCore plugin, TeamManager teamManager, GameSettings gameSettings, Messaging messaging, GameUtils gameUtils) {
        this.uhcCore = plugin;
        this.teamManager = teamManager;
        this.gameSettings = gameSettings;
        this.messaging = messaging;
        this.gameUtils = gameUtils;
        this.worldGenerator = new WorldGenerator();

        currentBorderSize = gameSettings.getArenaSize();
        gameCountdownTime = gameSettings.getTimeUntilNextGame();
        lobbyCountdownTime = gameSettings.getLobbyWaitTime();

        new BukkitRunnable() {
            @Override
            public void run() {
                setStatus(Status.COUNTDOWN_UNTIL_LOBBY);
            }
        }.runTaskLater(UHCCore.getPlugin(), 80);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (getGameCountdownTime() == null) {
                    return;
                }
                if (getGameCountdownTime() == 0) {
                    setStatus(Status.IN_LOBBY);
                    setGameCountdownTime(null);
                    genMap();
                    return;
                } else {
                    setGameCountdownTime(getGameCountdownTime()-1);
                }
            }
        },20L, 20L);
    }

    private void genMap() {
        uhcMap = worldGenerator.generateWorld("uhc_map");
        uhcMap.setPVP(true);
        uhcMap.setTime(0);
        uhcMap.setDifficulty(Difficulty.HARD);
    }

    public Integer getBorderSize() { return currentBorderSize; }

    public void setBorderSize(Integer amount) { currentBorderSize = amount; }

    public void setGameCountdownTime(Integer time) {
        gameCountdownTime = time;
    }

    public Integer getGameCountdownTime() {
        return gameCountdownTime;
    }

    public void setLobbyCountdownTime(Integer time) {
        lobbyCountdownTime = time;
    }

    public Integer getLobbyCountdownTime() {
        return lobbyCountdownTime;
    }

    public boolean isLobbyCountdownActive() { return lobbyCountdownActive; }

    public boolean isFinalHealEnabled() { return finalHealEnabled; }

    public boolean isAutoShrinkEnabled() { return borderShrinkCountDownRunnable != null; }

    public boolean isSpectator(UUID p) { return spectators.contains(p); }

    public void startCountdown(int amount) {
        if (lobbyCountdownActive == true) {
            return;
        }

        if (lobbyCountDownRunnable != null) {
            lobbyCountDownRunnable.cancel();
            lobbyCountDownRunnable = null;
        }

        lobbyCountdownTime = amount;
        lobbyCountdownActive = true;

        Bukkit.broadcastMessage(Messaging.Tag.IMPORTANT.getTag() + "Game countdown started!");
        setStatus(Status.LOBBY_COUNTDOWN);

        final GameStatus gameStatusInstance = this;
        lobbyCountDownRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (lobbyCountdownTime == 0) {
                    if (uhcMap == null) {
                        genMap();
                    }
                    lobbyCountdownActive = false;
                    setStatus(Status.STARTING);
                    teamManager.sortUnTeamedPlayers();
                    gameManager = new GameManager(gameSettings, teamManager, gameStatusInstance, uhcCore, gameUtils, uhcMap);
                    setStatus(Status.IN_GAME);
                    lobbyCountDownRunnable.cancel();
                } else {
                    lobbyCountdownTime--;
                }
            }
        }.runTaskTimer(UHCCore.getPlugin(), 20L, 20L);
    }

    public void stopCountDown() {
        if (lobbyCountdownActive) {
            lobbyCountdownActive = false;
        }
        setStatus(Status.IN_LOBBY);
        lobbyCountDownRunnable.cancel();
        Bukkit.broadcastMessage(Messaging.Tag.ALERT.getTag() + "Lobby countdown has stopped.");
    }

    public void startBorderShrink() {
        if (borderShrinkCountDownRunnable != null) {
            borderShrinkCountDownRunnable.cancel();
            borderShrinkCountDownRunnable = null;
        }

        timeUntilBorderShrink = gameSettings.getBorderShrinkTime();

        borderShrinkCountDownRunnable = new BukkitRunnable() {

            @Override
            public void run() {
                if (timeUntilBorderShrink == 0) {
                    currentBorderSize = currentBorderSize - gameSettings.getBorderShrinkAmount();
                    timeUntilBorderShrink = gameSettings.getBorderShrinkTime();
                } else {
                    timeUntilBorderShrink--;
                }
            }

        }.runTaskTimer(UHCCore.getPlugin(), 20L, 20L);
    }

    public void stopBorderShrink() {
        borderShrinkCountDownRunnable.cancel();
    }

    public void setSpectator(UUID p) { spectators.add(p); }

    public void setFinalHealEnabled(boolean value) { finalHealEnabled = value; }

    public void removeSpectator(UUID p) { spectators.remove(p); }

    public Integer getCurrentShrinkTime() {
        return timeUntilBorderShrink;
    }

    public Status getStatus() {
        return currentStatus;
    }

    public void setStatus(Status status) {
        Bukkit.getLogger().info("Setting status to " + status.getName());
        currentStatus = status;
        API.setStatus(gameSettings.getGameid(), status.getName());
    }

    public GameManager getGameManager() { return gameManager; }

    public void updateLobbyWaitTime(int amount) {
        if (lobbyCountdownActive == true) {
            lobbyCountdownTime = amount;
        }
    }

}
