package net.sail.uhc.commands.gamesubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 12/30/2015.
 */
public class SetLobbyWaitTime implements SubCommand {

    private final GameSettings gameSettings;
    private final GameStatus gameStatus;

    public SetLobbyWaitTime(GameSettings gameSettings, GameStatus gameStatus) {
        this.gameSettings = gameSettings;
        this.gameStatus = gameStatus;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid usage, type /game setlobbywaittime (amount, in seconds)");
            return false;
        }

        Integer amount = Integer.parseInt(args[0]);

        gameSettings.setLobbyWaitTime(amount);
        gameStatus.updateLobbyWaitTime(amount);

        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have set the lobby wait time to " + amount.toString());

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/game setlobbywaittime, sets the server's lobby countdown time.");
    }

}
