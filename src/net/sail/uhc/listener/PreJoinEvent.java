package net.sail.uhc.listener;

import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by brand on 12/30/2015.
 */
public class PreJoinEvent implements Listener {

    private final GameStatus gameStatus;
    private final GameSettings gameSettings;

    public PreJoinEvent(GameStatus gameStatus, GameSettings gameSettings) {
        this.gameStatus = gameStatus;
        this.gameSettings = gameSettings;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreJoinEvent(AsyncPlayerPreLoginEvent e) {
        GameStatus.Status currentStatus = gameStatus.getStatus();
        Player p = Bukkit.getOfflinePlayer(e.getUniqueId()).getPlayer();

        Bukkit.getLogger().info(currentStatus.getName());

        if (Bukkit.getOnlinePlayers().size() == gameSettings.getMaxPlayers() && !gameSettings.isHost(e.getUniqueId())) {
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
            e.setKickMessage(ChatColor.RED + "" + ChatColor.BOLD + "Sorry the server is currently full\n" + ChatColor.GOLD + "" + ChatColor.BOLD + "Purchase a donator rank to Join!");
        } else if (gameStatus.getStatus().equals(GameStatus.Status.IN_GAME) && !gameSettings.isHost(e.getUniqueId())) {
            if (!gameStatus.getGameManager().isInGame(e.getUniqueId()) && !gameSettings.isHost(e.getUniqueId()) && gameStatus.getGameManager().getPlayerSpawn(e.getUniqueId()) != null) {
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
                e.setKickMessage(ChatColor.RED + "" + ChatColor.BOLD + "The game has already started, try finding a different server on our network!");
            }
        }
    }

}
