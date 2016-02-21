package net.sail.uhc.commands.hostsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by brand on 1/17/2016.
 */
public class GiveAll implements SubCommand {

    private final GameStatus gameStatus;

    public GiveAll(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {

        int amount = 1;
        ItemStack item = null;

        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "errm no, try /host giveall (item) (amount)");
            return false;
        } else if (args.length == 2) {
            amount = Integer.parseInt(args[1]);
        }

        if (Material.valueOf(args[0]) == null) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "No item was found with this name... I hate you");
        }

        item = new ItemStack(Material.valueOf(args[0]), amount);

        for (UUID pUUID : gameStatus.getGameManager().getPlayersInGame()) {
            if (Bukkit.getPlayer(pUUID) != null) {
                Player player = Bukkit.getPlayer(pUUID);
                player.getInventory().addItem(item);
                player.sendMessage(Messaging.Tag.ALERT.getTag() + "LOOK! A gift from the gods. You got " + Integer.toString(amount) + " " + item.getType().toString());
            }
        }

        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "All players have been given " + item.toString());

        return false;
    }

    public String help(Player p) {
        return (ChatColor.BLUE + "" + ChatColor.BOLD + " - " + ChatColor.GRAY + "/host giveall, heals all players.");
    }


}
