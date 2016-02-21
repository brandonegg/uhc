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
public class SetTimeUntilNextGame implements SubCommand {

    private final GameSettings gameSettings;
    private final GameStatus gameStatus;

    public SetTimeUntilNextGame(GameSettings gameSettings, GameStatus gameStatus) {
        this.gameSettings = gameSettings;
        this.gameStatus = gameStatus;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid usage, type /game settimeuntilnextgame (amount, in seconds)");
            return false;
        }

        Integer amount = Integer.parseInt(args[0]);

        gameSettings.setTimeUntilNextGame(amount);
        gameStatus.setGameCountdownTime(amount);

        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "The game wait time has been set to " + amount.toString() + " seconds!");

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/game settimeuntilnextgame, sets the server's time until the lobby opens.");
    }

}
