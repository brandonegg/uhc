package net.sail.uhc.commands.hostsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 1/29/2016.
 */
public class ToggleAutoFinalHeal implements SubCommand {

    private final GameStatus gameStatus;
    private final GameSettings gameSettings;

    public ToggleAutoFinalHeal(GameStatus gameStatus, GameSettings gameSettings) {
        this.gameStatus = gameStatus;
        this.gameSettings = gameSettings;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {

        boolean value = false;

        if (args.length == 0) {
            value = !gameStatus.isFinalHealEnabled();
        } else {
            if (args[0].equalsIgnoreCase("true") || args[0].equalsIgnoreCase("enable")) {
                value = true;
            } else if (args[0].equalsIgnoreCase("false") || args[0].equalsIgnoreCase("disable")) {
                value = false;
            }
        }

        if (value == gameStatus.isFinalHealEnabled()) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Final heal is already set to this.");
            return false;
        }

        if (value) {
            p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "Final heal enabled!");
            if (gameStatus.getStatus().equals(GameStatus.Status.IN_GAME)) {
                gameStatus.getGameManager().startFinalHeal(gameSettings.getFinalHealTime());
            }
            gameStatus.setFinalHealEnabled(true);
        } else {
            p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "Final heal disabled!");
            if (gameStatus.getStatus().equals(GameStatus.Status.IN_GAME)) {
                if (gameStatus.getGameManager().isFinalHealLoopActive()) {
                    gameStatus.getGameManager().stopFinalHeal();
                }
            }
            gameStatus.setFinalHealEnabled(false);
        }

        return false;
    }

    public String help(Player p) {
        return (ChatColor.BLUE + "" + ChatColor.BOLD + " - " + ChatColor.GRAY + "/host toggleautofinalheal (value), enables/disables auto final heal.");
    }

}
