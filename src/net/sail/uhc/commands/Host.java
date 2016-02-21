package net.sail.uhc.commands;

import net.sail.uhc.commands.hostsubcommands.*;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by brand on 1/17/2016.
 */
public class Host implements CommandExecutor {

    private final GameSettings gameSettings;
    private final GameStatus gameStatus;
    private final TeamManager teamManager;

    private HashMap<String, SubCommand> cmds;

    public Host(GameSettings gameSettings, GameStatus gameStatus, TeamManager teamManager) {
        this.gameSettings = gameSettings;
        this.gameStatus = gameStatus;
        this.teamManager = teamManager;

        this.cmds = new HashMap<String, SubCommand>();
        cmds.put("feedall", new FeedAll(gameStatus));
        cmds.put("healall", new HealAll(gameStatus));
        cmds.put("giveall", new GiveAll(gameStatus));
        cmds.put("autoshrink", new AutoShrink(gameStatus, gameSettings));
        cmds.put("shrinkborder", new ShrinkBorder(gameStatus, gameSettings));
        cmds.put("startlobbycountdown", new StartLobbyCountdown(gameStatus, gameSettings));
        cmds.put("stoplobbycountdown", new StopLobbyCountdown(gameStatus, gameSettings));
        cmds.put("setspec", new SetSpec(gameStatus, teamManager));
        cmds.put("toggleautofinalheal", new ToggleAutoFinalHeal(gameStatus, gameSettings));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only for players you console peasant");
            return false;
        }

        Player p = (Player) sender;

        if (!p.getUniqueId().equals(gameSettings.getHost())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You must be a host to use this command.");
            return false;
        }

        if (cmd.getName().equalsIgnoreCase("host")) {
            if (args.length == 0) {
                p.sendMessage(Messaging.Tag.ERROR.getTag() + "Type '/host help' to find help for the game related commands.");
            } else {

                String sub = args[0];

                if (sub.equalsIgnoreCase("help")) {
                    p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "HOST " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "HELP:");
                    msgHelp(p);
                    return false;
                }

                Vector<String> l = new Vector<String>();
                l.addAll(Arrays.asList(args));
                l.remove(0);
                args = l.toArray(new String[0]);
                if (cmds.keySet().contains(sub)) {
                    cmds.get(sub).onCommand(p, args);
                } else {
                    p.sendMessage(Messaging.Tag.ERROR.getTag() + "Could not find the command '" + sub + "'!");
                }
            }
        }

        return false;
    }

    public void msgHelp(Player p) {
        for (SubCommand sc : cmds.values()) {
            p.sendMessage(sc.help(p));
        }
    }

}
