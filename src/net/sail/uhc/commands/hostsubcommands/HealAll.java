package net.sail.uhc.commands.hostsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 1/17/2016.
 */
public class HealAll implements SubCommand {

    private final GameStatus gameStatus;

    public HealAll(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {

        gameStatus.getGameManager().getPlayersInGame().forEach(inGameP -> {
            if (Bukkit.getPlayer(inGameP) != null) {
                Player player = Bukkit.getPlayer(inGameP);
                player.setHealth(20);
                player.sendMessage(Messaging.Tag.ALERT.getTag() + "The doctor is in the house, you have been healed!");
            }
        });

        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "All players in-game have been healed.");

        return false;
    }

    public String help(Player p) {
        return (ChatColor.BLUE + "" + ChatColor.BOLD + " - " + ChatColor.GRAY + "/host healall, heals all players.");
    }

}
