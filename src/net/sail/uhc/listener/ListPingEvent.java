package net.sail.uhc.listener;

import net.sail.uhc.utils.GameStatus;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Created by brand on 1/3/2016.
 */
public class ListPingEvent implements Listener {

    private final GameStatus gameStatus;

    public ListPingEvent(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    @EventHandler
    public void playerPingsServerEvent(ServerListPingEvent event) {
        if (gameStatus.getStatus().equals(GameStatus.Status.COUNTDOWN_UNTIL_LOBBY)) {
            event.setMotd(ChatColor.RED + "" + ChatColor.BOLD + "Game is not open yet!");
        } else if (gameStatus.getStatus().equals(GameStatus.Status.IN_LOBBY) || gameStatus.getStatus().equals(GameStatus.Status.LOBBY_COUNTDOWN)) {
            event.setMotd(ChatColor.GREEN + "" + ChatColor.BOLD + "Server is now in lobby mode, join now!");
        } else {
            event.setMotd(ChatColor.YELLOW + "" + ChatColor.BOLD + "Server is in-game, only donators can join now to spectate.");
        }
    }
}
