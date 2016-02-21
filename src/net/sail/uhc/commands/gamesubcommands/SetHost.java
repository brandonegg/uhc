package net.sail.uhc.commands.gamesubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import net.sail.uhc.utils.Permissions;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by brand on 1/15/2016.
 */
public class SetHost implements SubCommand {

    private final GameSettings gameSettings;
    private final GameStatus gameStatus;
    private final TeamManager teamManager;

    public SetHost(GameSettings gameSettings, TeamManager teamManager, GameStatus gameStatus) {
        this.gameSettings = gameSettings;
        this.teamManager = teamManager;
        this.gameStatus = gameStatus;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (!p.hasPermission(Permissions.GAME_ADMIN.getPermission())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Only game admins can set hosts.");
            return false;
        }
        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid usage, type /game sethost (username)");
            return false;
        }

        if (Bukkit.getPlayer(args[0]) == null && Bukkit.getOfflinePlayer(args[0]) == null) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "No player could be found with this name.");
            return false;
        } else if (Bukkit.getPlayer(args[0]) != null) {
            Player targetP = Bukkit.getPlayer(args[0]);

            p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have set the server's host to " + targetP.getName());
            targetP.sendMessage(Messaging.Tag.ALERT.getTag() + "You are a host, type '/host help' for more info!");

            targetP.getInventory().clear();
            if (!targetP.getAllowFlight()) {
                targetP.setAllowFlight(true);
            }
            targetP.setFlying(true);
            targetP.setGameMode(GameMode.CREATIVE);

            if (gameStatus.getStatus().equals(GameStatus.Status.IN_GAME)) {
                for (UUID inGameP : gameStatus.getGameManager().getPlayersInGame()) {
                    if (Bukkit.getPlayer(inGameP) != null) {
                        Bukkit.getPlayer(inGameP).hidePlayer(p);
                    }
                }
            }

            gameSettings.setHost(targetP.getUniqueId());
        } else if (Bukkit.getOfflinePlayer(args[0]) != null) {
            OfflinePlayer targetP = Bukkit.getOfflinePlayer(args[0]);

            p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have set the server's host to " + targetP.getName());

            gameSettings.setHost(targetP.getUniqueId());
        }

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/game sethost, Gives player host abilities");
    }

}
