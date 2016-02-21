package net.sail.uhc.commands;

import net.sail.uhc.settings.GameSettings;
import org.bukkit.entity.Player;

/**
 * Created by brand_000 on 8/2/2015.
 */
public interface SubCommand {

    /**
     * @param player Command sender
     * @param args SubCommand arguments
     */
    public boolean onCommand(Player player, String[] args);

    /**
     * @param p Sends a help message of a specified SubCommand to the player
     */
    public String help(Player p);

}
