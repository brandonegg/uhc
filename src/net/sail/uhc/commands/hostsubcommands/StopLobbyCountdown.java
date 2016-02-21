package net.sail.uhc.commands.hostsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 1/24/2016.
 */
public class StopLobbyCountdown implements SubCommand {

    private final GameStatus gameStatus;
    private final GameSettings gameSettings;

    public StopLobbyCountdown(GameStatus gameStatus, GameSettings gameSettings) {
        this.gameStatus = gameStatus;
        this.gameSettings = gameSettings;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {

        if (!gameStatus.isLobbyCountdownActive()) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "The lobby countdown is not active, type '/host startlobbycountdown' to start the countdown.");
            return false;
        }

        gameStatus.stopCountDown();

        return false;
    }

    public String help(Player p) {
        return (ChatColor.BLUE + "" + ChatColor.BOLD + " - " + ChatColor.GRAY + "/host stoplobbycountdown, stops lobby countdown.");
    }

}
