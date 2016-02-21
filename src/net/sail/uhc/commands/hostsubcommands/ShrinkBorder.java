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
public class ShrinkBorder implements SubCommand {

    private final GameStatus gameStatus;
    private final GameSettings gameSettings;

    public ShrinkBorder(GameStatus gameStatus, GameSettings gameSettings) {
        this.gameStatus = gameStatus;
        this.gameSettings = gameSettings;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {

        Integer delayAmount = 0;
        final Integer shrinkAmount;

        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid usage, try /host shrinkborder (shrink amount) (shrink delay)");
            return false;
        } else if (args.length >= 2) {
            delayAmount = Integer.parseInt(args[1]);
        }

        if (args[0].equalsIgnoreCase("default")) {
            shrinkAmount = gameSettings.getBorderShrinkAmount();
        } else {
            shrinkAmount = Integer.parseInt(args[0]);
        }

        if (delayAmount > 0) {
            Bukkit.broadcastMessage(Messaging.Tag.IMPORTANT.getTag() + "The border will be shrinking to " + Integer.toString(gameStatus.getBorderSize()-shrinkAmount) + " in " + Integer.toString(delayAmount*20) + " seconds!");
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                gameStatus.setBorderSize(gameStatus.getBorderSize()-shrinkAmount);
                Bukkit.broadcastMessage(Messaging.Tag.IMPORTANT.getTag() + "The border has shrunk to x: " + gameStatus.getBorderSize().toString() + ", z: " + gameStatus.getBorderSize().toString());
            }

        }.runTaskLater(UHCCore.getPlugin(), delayAmount*20);

        return false;
    }

    public String help(Player p) {
        return (ChatColor.BLUE + "" + ChatColor.BOLD + " - " + ChatColor.GRAY + "/host shrinkborder (shrink amount) (delay amount), Shrink the border by a specific amount at a specific time.");
    }
}
