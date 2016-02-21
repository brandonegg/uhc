package net.sail.uhc.commands.gamesubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 12/30/2015.
 */
public class SetBorderShrinkTime implements SubCommand {

    private final GameSettings gameSettings;

    public SetBorderShrinkTime(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid usage, type /game setbordershrinktime (amount, in seconds)");
            return false;
        }

        if (args[0].equalsIgnoreCase("none") || args[0].equalsIgnoreCase("null")) {
            gameSettings.setBorderShrinkTime(null);
            p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have disabled border shrink");
        }

        Integer amount = Integer.parseInt(args[0]);

        gameSettings.setBorderShrinkTime(amount);
        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have set the border shrink time to " + amount.toString());

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/game setbordershrinktime, sets the server's border shrink time!");
    }

}
