package net.sail.uhc.commands;

import net.sail.uhc.commands.teamsubcommands.*;
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
 * Created by brand on 12/30/2015.
 */
public class TeamCmd implements CommandExecutor {

    private final TeamManager teamManager;
    private final GameSettings gameSettings;
    private final GameStatus gameStatus;

    private HashMap<String, SubCommand> cmds;

    public TeamCmd(TeamManager teamManager, GameSettings gameSettings, GameStatus gameStatus) {
        this.teamManager = teamManager;
        this.gameSettings = gameSettings;
        this.gameStatus = gameStatus;

        this.cmds = new HashMap<String, SubCommand>();
        cmds.put("create", new Create(teamManager));
        cmds.put("decline", new Decline(teamManager));
        cmds.put("disband", new Disband(teamManager));
        cmds.put("info", new Info(teamManager));
        cmds.put("invite", new Invite(teamManager, gameSettings));
        cmds.put("join", new Join(teamManager));
        cmds.put("kick", new Kick(teamManager));
        cmds.put("leave", new Leave(teamManager));
        cmds.put("list", new List(teamManager));
        cmds.put("solo", new Solo(teamManager, gameSettings));
        cmds.put("chat", new Chat(teamManager));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only for players you console peasant");
            return false;
        }

        Player p = (Player) sender;

        if (gameSettings.isHost(p.getUniqueId())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Host cannot participate in games.");
            return false;
        }

        if (cmd.getName().equalsIgnoreCase("team")) {

            if (args.length == 0) {
                p.sendMessage(Messaging.Tag.ERROR.getTag() + "Type '/team help' to find help for the game related commands.");
            } else {

                String sub = args[0];

                if (sub.equalsIgnoreCase("help")) {
                    p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "TEAM " + ChatColor.GRAY + "" + ChatColor.BOLD + "HELP:");
                    msgHelp(p);
                    return false;
                }

                if (!sub.equalsIgnoreCase("list") && !sub.equalsIgnoreCase("chat") && !sub.equalsIgnoreCase("info") && !sub.equalsIgnoreCase("coords")) {
                    if (gameStatus.getStatus().equals(GameStatus.Status.IN_GAME) && cmds.keySet().contains(sub)) {
                        p.sendMessage(Messaging.Tag.ERROR.getTag() + "You can not use this subcommand in-game");
                        return false;
                    } else if (gameStatus.isSpectator(p.getUniqueId())) {
                        p.sendMessage(Messaging.Tag.ERROR.getTag() + "you can not use this subcommand as a spectator.");
                        return false;
                    }
                }

                Vector<String> l = new Vector<String>();
                l.addAll(Arrays.asList(args));
                l.remove(0);
                String[] subArgs = l.toArray(new String[0]);
                cmds.get(sub).onCommand(p, subArgs);
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
