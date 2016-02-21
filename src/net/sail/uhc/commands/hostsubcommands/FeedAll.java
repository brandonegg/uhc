package net.sail.uhc.commands.hostsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by brand on 1/17/2016.
 */
public class FeedAll implements SubCommand {

    private final GameStatus gameStatus;

    public FeedAll(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {

        gameStatus.getGameManager().getPlayersInGame().forEach(inGameP -> {
            if (Bukkit.getPlayer(inGameP) != null) {
                Player player = Bukkit.getPlayer(inGameP);
                player.setFoodLevel(20);
                player.sendMessage(Messaging.Tag.ALERT.getTag() + "Yummy, free food!");
            }
        });

        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "All players in-game have been fed.");

        return false;
    }

    public String help(Player p) {
        return (ChatColor.BLUE + "" + ChatColor.BOLD + " - " + ChatColor.GRAY + "/host feedall, feeds all players.");
    }

}
