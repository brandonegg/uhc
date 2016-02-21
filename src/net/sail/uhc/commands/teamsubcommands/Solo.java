package net.sail.uhc.commands.teamsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 1/10/2016.
 */
public class Solo implements SubCommand {

    private final TeamManager teamManager;
    private final GameSettings gameSettings;

    public Solo(TeamManager teamManager, GameSettings gameSettings) {
        this.teamManager = teamManager;
        this.gameSettings = gameSettings;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (gameSettings.getTeamSize() == 1) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Teams are already set to solo, there is no teaming in this hosted UHC.");
        }

        if (teamManager.playerHasTeam(p.getUniqueId()) || teamManager.playerIsMemberOfTeam(p.getUniqueId())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You cannot go solo when you are in a team, do /team leave or /team disband (if you are the owner)");
            return false;
        }

        if (teamManager.isSolo(p)) {
            teamManager.removeSoloPlayer(p);
            p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have disabled solo mode.");
        } else {
            teamManager.addSoloPlayer(p);
            p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have enabled solo mode. Type '/team solo' to disable.");
        }

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/team solo, Lets you play solo in team uhc.");
    }

}
