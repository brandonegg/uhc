package net.sail.uhc.commands.gamesubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 12/30/2015.
 */
public class SetMinandMaxPlayers implements SubCommand {

    private final GameSettings gameSettings;

    public SetMinandMaxPlayers(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (args.length <= 1) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid usage, type /game setminandmaxplayers (Min #) (Max #)");
            return false;
        }

        Integer min = Integer.parseInt(args[0]);
        Integer max = Integer.parseInt(args[1]);

        gameSettings.setMinPlayers(min);
        gameSettings.setMaxPlayers(max);

        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have set the maximum players to " + max.toString() + " and the minimum to " + min.toString());

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/game setminandmaxplayers, sets the server's min and max players needed.");
    }

}
