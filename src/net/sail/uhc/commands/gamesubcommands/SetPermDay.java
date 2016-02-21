package net.sail.uhc.commands.gamesubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 1/17/2016.
 */
public class SetPermDay implements SubCommand {
    private final GameSettings gameSettings;

    public SetPermDay(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid usage, type /game setpermday true/false");
            return false;
        }

        String value = args[0];

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("enable")) {
            gameSettings.setPermDay(true);
            p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have enabled permanent day.");
            return false;
        } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("disable")) {
            gameSettings.setPermDay(false);
            p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have disabled permanent day.");
        } else {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid arrrrgggggssss, use true or false to enable or disable.");
        }

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/game setpermday, Enables or disabled permanent day time for UHC.");
    }

}