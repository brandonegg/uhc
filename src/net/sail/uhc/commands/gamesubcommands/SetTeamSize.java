package net.sail.uhc.commands.gamesubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 12/30/2015.
 */
public class SetTeamSize implements SubCommand {

    private final GameSettings gameSettings;

    public SetTeamSize(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid usage, type /game setteamsize (amount)");
            return false;
        }

        Integer amount = Integer.parseInt(args[0]);

        gameSettings.setTeamSize(amount);

        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "The maximum team size has been set to " + amount.toString());

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/game setteamsize, sets the server's max team size.");
    }

}
