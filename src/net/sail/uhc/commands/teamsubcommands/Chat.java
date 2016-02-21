package net.sail.uhc.commands.teamsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 1/24/2016.
 */
public class Chat implements SubCommand {

    private final TeamManager teamManager;

    public Chat(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {

        boolean value;

        if (!teamManager.playerHasTeam(p.getUniqueId()) && !teamManager.playerIsMemberOfTeam(p.getUniqueId())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You must be in a team to enable team chat.");
            return false;
        }

        if (args.length == 0) {
            if (teamManager.isInTeamChat(p.getUniqueId())) {
                value = false;
            } else {
                value = true;
            }
        } else {
            if (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("enable")) {
                value = true;
            } else if (args[0].equalsIgnoreCase("false") || args[0].equalsIgnoreCase("disable")) {
                value = false;
            } else {
                p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid argument, try /team chat (value)");
                return false;
            }
        }

        if (teamManager.isInTeamChat(p.getUniqueId()) && value) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You already have team chat enabled!");
            return false;
        } else if (!teamManager.isInTeamChat(p.getUniqueId()) && !value) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You already have team chat disabled!");
            return false;
        }

        if (value) {
            teamManager.addToInTeamChat(p.getUniqueId());
            p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You are now in team chat mode, all messages will only be shown to your teammates, type '/team chat' again to disbale.");
        } else {
            teamManager.removeFromInTeamChat(p.getUniqueId());
            p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have disabled team chat, all messages are public. Type '/team chat' again to re-enable.");
        }

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/team chat, Toggles team chat");
    }

}
