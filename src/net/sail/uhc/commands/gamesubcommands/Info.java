package net.sail.uhc.commands.gamesubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by brand on 1/3/2016.
 */
public class Info implements SubCommand {

    private final GameSettings gameSettings;

    public Info(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        p.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Sail" + ChatColor.AQUA + "" + ChatColor.BOLD + "MC" + ChatColor.YELLOW + "" + ChatColor.BOLD + " UHC" + ChatColor.DARK_GRAY + ", developed by " + ChatColor.GREEN + "" + ChatColor.BOLD + "bman7842");
        p.sendMessage(ChatColor.GRAY + "  - team size: " + ChatColor.WHITE + gameSettings.getTeamSize().toString());
        p.sendMessage(ChatColor.GRAY + "  - lobby wait time: " + ChatColor.WHITE + gameSettings.getLobbyWaitTime().toString() + " seconds.");
        p.sendMessage(ChatColor.GRAY + "  - time until next game: " + ChatColor.WHITE + gameSettings.getTimeUntilNextGame().toString());
        if (gameSettings.getBorderShrinkTime() == null) {
            p.sendMessage(ChatColor.GRAY + "  - border shrink is disabled.");
        } else {
            p.sendMessage(ChatColor.GRAY + "  - border shrink time: " + ChatColor.WHITE + gameSettings.getBorderShrinkTime().toString() + " seconds.");
            p.sendMessage(ChatColor.GRAY + "  - border shrink amount: " + ChatColor.WHITE + gameSettings.getBorderShrinkAmount().toString());
        }
        p.sendMessage(ChatColor.GRAY + "  - peaceful time: " + ChatColor.WHITE + gameSettings.getPeacefulTime().toString() + " seconds.");
        p.sendMessage(ChatColor.GRAY + "  - arena size: " + ChatColor.WHITE + gameSettings.getPeacefulTime().toString() + " (blocks, radius)");
        p.sendMessage(ChatColor.GRAY + "  - min/max players: " + ChatColor.WHITE + gameSettings.getMinPlayers() + "/" + gameSettings.getMaxPlayers());
        p.sendMessage(ChatColor.GRAY + "  - Permanent day: " + ChatColor.WHITE + Boolean.toString(gameSettings.isPermDay()));
        if (gameSettings.getHost() != null) {
            if (Bukkit.getPlayer(gameSettings.getHost()) != null) {
                p.sendMessage(ChatColor.GRAY + "  - Hosts: " + ChatColor.WHITE + Bukkit.getPlayer(gameSettings.getHost()).getName());
            } else {
                p.sendMessage(ChatColor.GRAY + "  - Hosts: " + ChatColor.WHITE + Bukkit.getOfflinePlayer(gameSettings.getHost()).getName());
            }
        } else {
            p.sendMessage(ChatColor.GRAY + "  - Hosts: " + ChatColor.WHITE + "NONE");
        }
        p.sendMessage("");
        p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Version " + Bukkit.getPluginManager().getPlugin("SailUHC").getDescription().getVersion());
        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/game setbordershrinktime, sets the server's border shrink time!");
    }

}
