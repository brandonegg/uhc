package net.sail.uhc.commands.teamsubcommands;

import net.sail.uhc.UHCCore;
import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import net.sail.uhc.utils.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by brand on 1/24/2016.
 */
public class Coords implements SubCommand {

    private final TeamManager teamManager;
    private final GameStatus gameStatus;

    public Coords(TeamManager teamManager, GameStatus gameStatus) {
        this.teamManager = teamManager;
        this.gameStatus = gameStatus;
    }

    private final Integer delayAmount = 10;
    private HashSet<UUID> spamDelay = new HashSet<UUID>();

    @Override
    public boolean onCommand(Player p, String[] args) {

        UHCTeam team;

        if (!teamManager.playerHasTeam(p.getUniqueId()) && !teamManager.playerIsMemberOfTeam(p.getUniqueId())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You must be in a team to send coords.");
            return false;
        } else if (teamManager.playerHasTeam(p.getUniqueId())) {
            team = teamManager.getTeamFromOwner(p.getUniqueId());
        } else {
            team  = teamManager.getTeamFromMember(p.getUniqueId());
        }

        if (!gameStatus.getStatus().equals(GameStatus.Status.IN_GAME)) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You can only use this command in-game.");
            return false;
        }

        if (spamDelay.contains(p.getUniqueId())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You must wait " + delayAmount.toString() + " seconds before you can do this again.");
            return false;
        }

        Location pLoc = p.getLocation();

        Bukkit.getPlayer(team.getOwner()).sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "" + ChatColor.BOLD + "Team: " + ChatColor.WHITE + p.getName() + "'s coords are x: " + Integer.toString(pLoc.getBlockX()) + ", y: " + Integer.toString(pLoc.getBlockY()) + ", z: " + Integer.toString(pLoc.getBlockZ()));

        spamDelay.add(p.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                spamDelay.remove(p.getUniqueId());
            }
        }.runTaskLater(UHCCore.getPlugin(), delayAmount*20);

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/team coords, displays your coords to all team members.");
    }

}
