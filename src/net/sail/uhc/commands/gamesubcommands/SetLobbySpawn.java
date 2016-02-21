package net.sail.uhc.commands.gamesubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 12/30/2015.
 */
public class SetLobbySpawn implements SubCommand {

    private final GameSettings gameSettings;

    public SetLobbySpawn(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {

        gameSettings.setLobbySpawn(p.getLocation());
        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You set the new lobby spawn, players will now spawn here when they Join.");

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/game setlobbyspawn, sets the server's lobby spawn.");
    }

}
