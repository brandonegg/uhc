package net.sail.uhc.listener;

import net.sail.uhc.UHCCore;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import net.sail.uhc.utils.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * Created by brand on 12/30/2015.
 */
public class LeaveEvent implements Listener {

    private final GameSettings gameSettings;
    private final GameStatus gameStatus;
    private final TeamManager teamManager;

    private int returnWaitTime = 300;

    public LeaveEvent(GameSettings gameSettings, GameStatus gameStatus, TeamManager teamManager) {
        this.gameSettings = gameSettings;
        this.gameStatus = gameStatus;
        this.teamManager = teamManager;
    }

    @EventHandler
    public void PlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (gameStatus.getStatus().equals(GameStatus.Status.IN_GAME)) {
            if (teamManager.getTeams().size() <= 1) {
                gameStatus.getGameManager().endGame();
            }
            new BukkitRunnable() {
                public void run() {
                    if (!p.isOnline()) {
                        gameStatus.getGameManager().removeInGame(p.getUniqueId());
                        Bukkit.getLogger().info("Player was not in-game");
                    }
                }
            }.runTaskLater(UHCCore.getPlugin(), returnWaitTime*20);
        } else {
            if (teamManager.playerIsMemberOfTeam(p.getUniqueId())) {
                UHCTeam playerTeam = teamManager.getTeamFromMember(p.getUniqueId());
                teamManager.removeMemberFromTeam(playerTeam.getOwner(), p.getUniqueId());
            }

            if (teamManager.playerHasTeam(p.getUniqueId())) {
                teamManager.deleteTeamFromOwner(p.getUniqueId());
            }

            if (Bukkit.getOnlinePlayers().size() < gameSettings.getMinPlayers()) {
                gameStatus.stopCountDown();
                Bukkit.broadcastMessage(Messaging.Tag.ALERT.getTag() + "Stopping countdown, not enough players.");
            }

            e.setQuitMessage(ChatColor.RED + p.getName() + " has left the lobby. " + ChatColor.YELLOW + "-1");
        }
    }

}
