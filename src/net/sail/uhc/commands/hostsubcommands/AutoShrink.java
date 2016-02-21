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
 * Created by brand on 1/23/2016.
 */
public class AutoShrink implements SubCommand {

    private final GameStatus gameStatus;
    private final GameSettings gameSettings;

    public AutoShrink(GameStatus gameStatus, GameSettings gameSettings) {
        this.gameStatus = gameStatus;
        this.gameSettings = gameSettings;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {

        boolean value = false;

        if (args.length == 0) {
            value = !gameStatus.isAutoShrinkEnabled();
        } else {
            value = Boolean.parseBoolean(args[0]);
        }

        if (value == gameStatus.isAutoShrinkEnabled()) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Auto shrink is already set to this.");
            return false;
        }

        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have updated auto shrink!");
        if (value) {
            gameStatus.startBorderShrink();
            Bukkit.broadcastMessage(Messaging.Tag.IMPORTANT.getTag() + "Border auto shrink has been enabled, look at your scoreboard to view shrink time.");
        } else {
            gameStatus.stopBorderShrink();
            Bukkit.broadcastMessage(Messaging.Tag.IMPORTANT.getTag() + "Border auto shrink has been disabled, the border will no longer shrink automatically.");
        }

        return false;
    }

    public String help(Player p) {
        return (ChatColor.BLUE + "" + ChatColor.BOLD + " - " + ChatColor.GRAY + "/host autoshrink (value), True or false, enabled or disables autoshrink.");
    }

}
