package net.sail.uhc.commands.teamsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.utils.Messaging;
import net.sail.uhc.utils.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by brand on 12/30/2015.
 */
public class Info implements SubCommand {

    private final TeamManager teamManager;

    public Info(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid usage, type /team info (name)");
            return false;
        }

        String name = args[0];
        UUID ownerUUID;
        try {
            ownerUUID = Bukkit.getPlayer(name).getUniqueId();
        } catch (Exception e) {
            ownerUUID = null;
        }

        if (teamManager.getTeamFromOwner(ownerUUID) == null && teamManager.getTeamFromName(name) == null) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "No team was found with this name!");
        } else if (teamManager.getTeamFromName(name) == null) {
            sendTeamInfo(p, teamManager.getTeamFromOwner(ownerUUID));
        } else {
            sendTeamInfo(p, teamManager.getTeamFromName(name));
        }

        return false;
    }

    public void sendTeamInfo(Player p, UHCTeam team) {
        p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + team.getName() + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ":");
        p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "  OWNER" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ": " + ChatColor.WHITE + "" + ChatColor.BOLD + Bukkit.getPlayer(team.getOwner()).getName());
        p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "  MEMBERS" + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + ":");
        for (UUID pUUID : team.getMembers()) {
            p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "      - " + ChatColor.WHITE + "" + ChatColor.BOLD + Bukkit.getPlayer(pUUID).getName());
        }
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/team info, provides the info of a specific team.");
    }

}
