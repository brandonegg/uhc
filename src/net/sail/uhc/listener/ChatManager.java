package net.sail.uhc.listener;

import javafx.scene.layout.Priority;
import me.Hippo.Plugin.API;
import me.Hippo.Plugin.Main;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by brand on 1/24/2016.
 */
public class ChatManager implements Listener {

    private final TeamManager teamManager;
    private final GameSettings gameSettings;

    public ChatManager(TeamManager teamManager, GameSettings gameSettings) {
        this.teamManager = teamManager;
        this.gameSettings = gameSettings;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerChats(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        if (p.getUniqueId().equals(gameSettings.getHost())) {
            e.setFormat(ChatColor.GRAY + "[" + ChatColor.WHITE + "HOST" + ChatColor.GRAY + "] " + API.chatformat());
            return;
        }

        UHCTeam team = null;

        if (teamManager.playerIsMemberOfTeam(p.getUniqueId())) {
            team = teamManager.getTeamFromMember(p.getUniqueId());
        } else if (teamManager.playerHasTeam(p.getUniqueId())) {
            team = teamManager.getTeamFromOwner(p.getUniqueId());
        }

        HashSet<Player> removeRecipients = new HashSet<Player>();
        if (teamManager.isInTeamChat(p.getUniqueId())) {
            for (Player player : e.getRecipients()) {
                Bukkit.getLogger().info(team.getName());
                if (player.getUniqueId() != team.getOwner() && !team.getMembers().contains(player.getUniqueId())) {
                    removeRecipients.add(player);
                }
            }
            for (Player player : removeRecipients) {
                Bukkit.getLogger().info(player.getName());
                e.getRecipients().remove(player);
            }

            e.setFormat(ChatColor.GRAY + "[" + ChatColor.GOLD + "" + ChatColor.BOLD + "Team: " + ChatColor.WHITE + API.chatformat() + ChatColor.GRAY + "]");
        } else {
            e.setFormat(API.chatformat());
        }
    }

}
