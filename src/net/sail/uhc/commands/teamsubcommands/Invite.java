package net.sail.uhc.commands.teamsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.Messaging;
import net.sail.uhc.utils.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 12/30/2015.
 */
public class Invite implements SubCommand {

    private final TeamManager teamManager;
    private final GameSettings gameSettings;

    public Invite(TeamManager teamManager, GameSettings gameSettings) {
        this.teamManager = teamManager;
        this.gameSettings = gameSettings;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid usage, type /team invite (name)");
            return false;
        }

        if (Bukkit.getPlayer(args[0]) == null) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "No player online with this name.");
            return false;
        }

        Player invitedPlayer = Bukkit.getPlayer(args[0]);

        if (!teamManager.playerHasTeam(p.getUniqueId())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You must be the owner of a team to invite someone.");
            return false;
        }

        if (teamManager.playerIsInvitedToTeam(invitedPlayer)) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "This user already has a pending team invite.");
            return false;
        }

        UHCTeam pTeam = teamManager.getTeamFromOwner(p.getUniqueId());

        if (pTeam.getTeamSize() == gameSettings.getTeamSize()) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Your team is full, do /team kick (username) to make room.");
            return false;
        }

        if (teamManager.playerHasTeam(invitedPlayer.getUniqueId()) || teamManager.playerIsMemberOfTeam(invitedPlayer.getUniqueId())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "This player already owns or is a member of a team.");
            return false;
        }

        if (teamManager.isSolo(invitedPlayer)) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "This player is in solo mode, you cannot invite them.");
            return false;
        }

        teamManager.invitePlayer(invitedPlayer, pTeam);

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/team invite, invites a player to your team");
    }

}
