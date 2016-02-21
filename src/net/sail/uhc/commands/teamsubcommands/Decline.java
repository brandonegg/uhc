package net.sail.uhc.commands.teamsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 12/31/2015.
 */
public class Decline implements SubCommand {

    private final TeamManager teamManager;

    public Decline(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {

//        String name = args[0];

        if (!teamManager.playerIsInvitedToTeam(p)) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You have no pending team invites, maybe you took to long to decline?");
            return false;
        }

        teamManager.declineInvite(p);

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/team join, joins a team");
    }

}
