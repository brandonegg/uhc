package net.sail.uhc.commands.teamsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 12/30/2015.
 */
public class Join implements SubCommand {

    private final TeamManager teamManager;

    public Join(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {

//        String name;
//
//        if (args.length == 0) {
//            name = null;
//        } else {
//            name = args[0];
//        }

        if (teamManager.playerIsMemberOfTeam(p.getUniqueId())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You are already in a team, do /team leave to leave the team");
            return false;
        } else if (teamManager.playerHasTeam(p.getUniqueId())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You already own a team, do /team delete to delete it");
            return false;
        }

        if (!teamManager.playerIsInvitedToTeam(p)) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You have no pending team invites, maybe you took to long to accept?");
            return false;
        }

        teamManager.acceptInvite(p);

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/team join, joins a team");
    }

}
