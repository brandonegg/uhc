package net.sail.uhc.listener;

import net.sail.uhc.UHCCore;
import net.sail.uhc.manager.ServerScoreboardManager;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Random;
import java.util.UUID;

/**
 * Created by brand on 12/30/2015.
 */
public class JoinEvent implements Listener {

    private static final Random RAND = new Random();

    private final GameSettings gameSettings;
    private final GameStatus gameStatus;
    private final ServerScoreboardManager serverScoreBoardManager;
    private final GameUtils gameUtils;

    public JoinEvent(GameSettings gameSettings, GameStatus gameStatus, ServerScoreboardManager serverScoreboardManager, GameUtils gameUtils) {
        this.gameSettings = gameSettings;
        this.gameStatus = gameStatus;
        this.serverScoreBoardManager = serverScoreboardManager;
        this.gameUtils = gameUtils;
    }

    //TODO: add the repetitive code to a class in utils

    @EventHandler
    public void PlayerJoined(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (gameStatus.getStatus().equals(GameStatus.Status.IN_GAME)) {
            if (gameSettings.isHost(p.getUniqueId()) || gameStatus.isSpectator(p.getUniqueId())) {
                p.teleport(gameStatus.getGameManager().getMap().getSpawnLocation());
                gameStatus.getGameManager().getPlayersInGame().forEach(inGameP -> {
                    if (Bukkit.getPlayer(inGameP) != null) {
                        Bukkit.getPlayer(inGameP).hidePlayer(p);
                    }
                });
                p.setFlying(true);
                p.setGameMode(GameMode.CREATIVE);
                p.getInventory().clear();
                e.setJoinMessage(ChatColor.GREEN + p.getName() + " has rejoined the game! " + ChatColor.YELLOW + "+1");
            }
            if (gameStatus.getGameManager().getPlayerSpawn(p) != null && !gameStatus.getGameManager().isInGame(p.getUniqueId())) {
                if (p.getWorld().getName().equals("uhc_map")) {
                    gameStatus.getGameManager().addInGamePlayer(p.getUniqueId());
                    Messaging.sendHostMessage("USER: " + p.getName() + " joined the game with an error linked to their instance, UHC fixed issue.");
                } else {
                    p.teleport(gameStatus.getGameManager().getPlayerSpawn(p));
                    gameStatus.getGameManager().addInGamePlayer(p.getUniqueId());
                    Messaging.sendHostMessage("USER:" + p.getName() + " was in-game during player teleportation setup but must have disconnected during map gen. User was fixed.");
                }
            }
        } else if (gameStatus.getStatus().equals(GameStatus.Status.IN_LOBBY)) {
            p.sendMessage(Messaging.Tag.ALERT.getTag() + "The game is currently in lobby mode, type '/team' to find team related commands.");
            gameUtils.setupForLobby(p);
            e.setJoinMessage(ChatColor.GREEN + p.getName() + " has joined the lobby! " + ChatColor.YELLOW + "+1");
        } else if (gameStatus.getStatus().equals(GameStatus.Status.LOBBY_COUNTDOWN)) {
            p.sendMessage(Messaging.Tag.ALERT.getTag() + "The game is currently in lobby countdown mode, the time is shown on the scoreboard. Type '/team' to find team related commands.");
            gameUtils.setupForLobby(p);
            e.setJoinMessage(ChatColor.GREEN + p.getName() + " has joined the lobby! " + ChatColor.YELLOW + "+1");
        } else if (gameStatus.getStatus().equals(GameStatus.Status.COUNTDOWN_UNTIL_LOBBY)) {
            if (!gameSettings.getHost().equals(p.getUniqueId()) && !p.hasPermission(Permissions.GAME_ADMIN.getPermission())) {
                p.kickPlayer(Messaging.Tag.ERROR.getTag() + "You cannot join the game yet.");
                return;
            }
            p.sendMessage(Messaging.Tag.ALERT.getTag() + "The game is currently in countdown until lobby mode, configure game settings during this stage.");
            gameUtils.setupForLobby(p);
            e.setJoinMessage(ChatColor.GREEN + p.getName() + " has joined the lobby! " + ChatColor.YELLOW + "+1");
        }

        sendJoinMessage(p);
        serverScoreBoardManager.loadBoard(p);
    }

    public void sendJoinMessage(Player p) {
        Messaging.sendCenteredMessage(p, "§7---------------------------------------------------");
        Messaging.sendCenteredMessage(p, "§3§lSail§b§lMC §f§lUHC");
        Messaging.sendCenteredMessage(p, "§f§lStatus: §c§lALPHA");
        Messaging.sendCenteredMessage(p, "§e§lPlease report all bugs and glitches on the forums!");
        Messaging.sendCenteredMessage(p, "§7---------------------------------------------------");
    }


}
