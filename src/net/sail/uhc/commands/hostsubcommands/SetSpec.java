package net.sail.uhc.commands.hostsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import org.bukkit.*;
import org.bukkit.entity.Player;

/**
 * Created by brand on 1/27/2016.
 */
public class SetSpec implements SubCommand {

    private final GameStatus gameStatus;
    private final TeamManager teamManager;

    public SetSpec(GameStatus gameStatus, TeamManager teamManager) {
        this.gameStatus = gameStatus;
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {

        Boolean value = false;

        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR + "Invalid usage, try '/host setspec (name)'");
            return false;
        } else if (args.length > 1) {
            value = Boolean.parseBoolean(args[1]);
        }

        if (Bukkit.getPlayer(args[0]) != null) {
            Player target = Bukkit.getPlayer(args[0]);

            if (gameStatus.isSpectator(target.getUniqueId()) && value) {
                p.sendMessage(Messaging.Tag.ERROR.getTag() + "This user is already a spectator, type /host setspec " + target.getName());
                return false;
            } else if (!gameStatus.isSpectator(target.getUniqueId()) && !value) {
                p.sendMessage(Messaging.Tag.ERROR.getTag() + "This user already is not a spectator.");
                return false;
            }

            if (value) {
                p.sendMessage(Messaging.Tag.SUCCESS.getTag() + target.getName() + " is now a spectator!");
                setSpec(target);
            } else {
                p.sendMessage(Messaging.Tag.SUCCESS.getTag() + target.getName() + " is no longer a spectator!");
                removeSpec(target);
            }
        } else if (Bukkit.getOfflinePlayer(args[0]) != null) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            if (gameStatus.isSpectator(target.getUniqueId()) && value) {
                p.sendMessage(Messaging.Tag.ERROR.getTag() + "This user is already a spectator, type /host setspec " + target.getName());
                return false;
            } else if (!gameStatus.isSpectator(target.getUniqueId()) && !value) {
                p.sendMessage(Messaging.Tag.ERROR.getTag() + "This user already is not a spectator.");
                return false;
            }

            if (value) {
                p.sendMessage(Messaging.Tag.SUCCESS.getTag() + target.getName() + " is now a spectator!");
                gameStatus.setSpectator(target.getUniqueId());
            } else {
                p.sendMessage(Messaging.Tag.SUCCESS.getTag() + target.getName() + " is no longer a spectator!");
                gameStatus.removeSpectator(target.getUniqueId());
            }
        }

        return false;
    }

    public void setSpec(Player p) {
        gameStatus.setSpectator(p.getUniqueId());

        if (gameStatus.getStatus().equals(GameStatus.Status.IN_GAME)) {
            p.teleport(new Location(gameStatus.getGameManager().getMap(), 0, 100, 0));
            for (Player ingameP : Bukkit.getOnlinePlayers()) {
                ingameP.hidePlayer(p);
            }
            p.sendMessage(Messaging.Tag.ALERT.getTag() + "You are now a spectator.");
            p.getInventory().clear();
            p.setGameMode(GameMode.CREATIVE);
            p.setFlying(true);
            gameStatus.getGameManager().removeInGame(p.getUniqueId());
        } else {
            p.sendMessage(Messaging.Tag.ALERT.getTag() + "You are now a spectator, when the game starts you will not be placed in-game.");
        }

        if (teamManager.playerHasTeam(p.getUniqueId()) || teamManager.playerIsMemberOfTeam(p.getUniqueId())) {
            if (teamManager.playerHasTeam(p.getUniqueId())) {
                teamManager.deleteTeamFromOwner(p.getUniqueId());
                p.sendMessage(Messaging.Tag.IMPORTANT.getTag() + "Your team was disbanded.");
            } else {
                teamManager.getTeamFromMember(p.getUniqueId()).removeMember(p.getUniqueId());
                p.sendMessage(Messaging.Tag.IMPORTANT.getTag() + "You were removed from your team.");
            }
        }
    }

    public void removeSpec(Player p) {

    }

    public String help(Player p) {
        return (ChatColor.BLUE + "" + ChatColor.BOLD + " - " + ChatColor.GRAY + "/host setspec (name), sets a player to spectator.");
    }

}
