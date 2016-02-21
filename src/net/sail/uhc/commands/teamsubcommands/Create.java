package net.sail.uhc.commands.teamsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 12/30/2015.
 */
public class Create implements SubCommand {

    private final TeamManager teamManager;

    public Create(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid usage, type /team create (name)");
            return false;
        }

        String name = args[0];

        if (teamManager.teamExists(name)) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "The team you entered is already taken, if you are trying to join do /team join " + name);
            return false;
        }

        if (teamManager.playerHasTeam(p.getUniqueId()) || teamManager.playerIsMemberOfTeam(p.getUniqueId())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You already are in or have a team.");
            return false;
        }

        if (name.length() > 11) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Your team name cannot be greater than 11 characters!");
            return false;
        }
        teamManager.createTeam(p.getUniqueId(), name);
        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "The team '" + name + "' has been created! Type /team invite (username) to invite players.");

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/team create, creates a team with a specific name.");
    }

}
