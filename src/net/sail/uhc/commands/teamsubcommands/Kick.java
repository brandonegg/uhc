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
public class Kick implements SubCommand {

    private final TeamManager teamManager;

    public Kick(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid usage, type /team kick (user)");
            return false;
        }

        String name = args[0];

        if (teamManager.getTeamFromOwner(p.getUniqueId()) == null) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You must be the owner of a team to kick a player!");
            return false;
        }

        Player pKicked = Bukkit.getPlayer(name);

        if (pKicked == null) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "No player is online with the name " + args[0]);
            return false;
        }
        if (!teamManager.getTeamFromOwner(p.getUniqueId()).containsUser(pKicked.getUniqueId())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "This user is not in your team.");
            return false;
        }

        teamManager.removeMemberFromTeam(p.getUniqueId(), pKicked.getUniqueId());
        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have removed " + pKicked.getName() + " from your team.");

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/team join, joins a team");
    }

}
