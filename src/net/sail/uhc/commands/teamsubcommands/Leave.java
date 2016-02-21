package net.sail.uhc.commands.teamsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.utils.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 12/31/2015.
 */
public class Leave implements SubCommand {

    private final TeamManager teamManager;

    public Leave(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (teamManager.playerHasTeam(p.getUniqueId())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You are the owner of this team, use /team disband to leave.");
            return false;
        }

        if (!teamManager.playerIsMemberOfTeam(p.getUniqueId())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You must be in a team to do /team leave!");
            return false;
        }

        teamManager.removeMemberFromTeam(teamManager.getTeamFromMember(p.getUniqueId()).getOwner(), p.getUniqueId());

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/team leave, leaves a team");
    }

}
