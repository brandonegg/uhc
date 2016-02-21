package net.sail.uhc.commands.teamsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 12/30/2015.
 */
public class Disband implements SubCommand {

    private final TeamManager teamManager;

    public Disband(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (args.length > 0) {
            String name = args[0];

            if (!teamManager.teamExists(name)) {
                p.sendMessage(Messaging.Tag.ERROR.getTag() + "This team does not exists");
                return false;
            }
            if (!teamManager.getTeamFromName(name).getOwner().equals(p.getUniqueId())) {
                p.sendMessage(Messaging.Tag.ERROR.getTag() + "Only the owner can delete his team! Type /team info " + name + " to find the owner.");
                return false;
            }
            teamManager.deleteTeamFromName(name);
            p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You deleted the team " + name);
            return false;
        }

        if (!teamManager.playerHasTeam(p.getUniqueId())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You do not own a team!");
            return false;
        }

        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You deleted your team, " + teamManager.getTeamFromOwner(p.getUniqueId()).getName());
        teamManager.deleteTeamFromOwner(p.getUniqueId());
        if (teamManager.isInTeamChat(p.getUniqueId())) {
            teamManager.removeFromInTeamChat(p.getUniqueId());
        }

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/team disband, deletes a team with a specific name, if no name is provided it assumes you are deleting your own team");
    }

}
