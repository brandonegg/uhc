package net.sail.uhc.commands.hostsubcommands;

import net.sail.uhc.UHCCore;
import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by brand on 1/24/2016.
 */
public class StartLobbyCountdown implements SubCommand {

    private final GameStatus gameStatus;
    private final GameSettings gameSettings;

    public StartLobbyCountdown(GameStatus gameStatus, GameSettings gameSettings) {
        this.gameStatus = gameStatus;
        this.gameSettings = gameSettings;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {

        if (gameStatus.isLobbyCountdownActive()) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "The lobby countdown has already started, type /host stoplobbycountdown");
            return false;
        }

        Integer amount;

        if (args.length == 0) {
            amount = gameSettings.getLobbyWaitTime();
        } else {
            amount = Integer.parseInt(args[0]);
        }

        gameStatus.startCountdown(amount);

        return false;
    }

    public String help(Player p) {
        return (ChatColor.BLUE + "" + ChatColor.BOLD + " - " + ChatColor.GRAY + "/host startlobbycountdown (amount), starts lobby countdown for a specified amount of seconds, can be default.");
    }

}
