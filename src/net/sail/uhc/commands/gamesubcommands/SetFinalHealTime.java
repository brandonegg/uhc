package net.sail.uhc.commands.gamesubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 1/29/2016.
 */
public class SetFinalHealTime implements SubCommand {

    private final GameSettings gameSettings;

    public SetFinalHealTime(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid usage, type /game setfinalhealtime (amount, in seconds)");
            return false;
        }

        Integer amount = Integer.parseInt(args[0]);

        gameSettings.setFinalHealTime(amount);

        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "Game final heal time set to " + amount.toString() + " seconds.");

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/game setfinalhealtime (amount, in seconds), sets servers final heal time");
    }

}
