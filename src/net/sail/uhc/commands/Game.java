package net.sail.uhc.commands;

import net.sail.uhc.commands.gamesubcommands.*;
import net.sail.uhc.commands.gamesubcommands.Info;
import net.sail.uhc.manager.ScenarioManager;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import net.sail.uhc.utils.Permissions;
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
public class Game implements CommandExecutor {

    private final GameSettings gameSettings;
    private final TeamManager teamManager;
    private final GameStatus gameStatus;
    private final ScenarioManager scenarioManager;

    private HashMap<String, SubCommand> cmds;

    public Game(GameSettings gameSettings, TeamManager teamManager, GameStatus gameStatus, ScenarioManager scenarioManager) {
        this.gameSettings = gameSettings;
        this.teamManager = teamManager;
        this.gameStatus = gameStatus;
        this.scenarioManager = scenarioManager;

        this.cmds = new HashMap<String, SubCommand>();
        cmds.put("setlobbyspawn", new SetLobbySpawn(gameSettings));
        cmds.put("setbordershrinktime", new SetBorderShrinkTime(gameSettings));
        cmds.put("setlobbywaittime", new SetLobbyWaitTime(gameSettings, gameStatus));
        cmds.put("setminandmaxplayers", new SetMinandMaxPlayers(gameSettings));
        cmds.put("setteamsize", new SetTeamSize(gameSettings));
        cmds.put("settimeuntilnextgame", new SetTimeUntilNextGame(gameSettings, gameStatus));
        cmds.put("info", new Info(gameSettings));
        cmds.put("sethost", new SetHost(gameSettings, teamManager, gameStatus));
        cmds.put("setpermday", new SetPermDay(gameSettings));
        cmds.put("setfinalhealtime", new SetFinalHealTime(gameSettings));
        cmds.put("setscenario", new SetScenario(scenarioManager));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command is only for players you console peasant");
            return false;
        }

        Player p = (Player) sender;

        if (!gameSettings.isHost(p.getUniqueId()) && !p.hasPermission(Permissions.GAME_ADMIN.getPermission())) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "You must be a host or admin to edit game settings.");
            return false;
        }

        if (cmd.getName().equalsIgnoreCase("game")) {
            if (args.length == 0) {
                p.sendMessage(Messaging.Tag.ERROR.getTag() + "Type '/game help' to find help for the game related commands.");
            } else {

                String sub = args[0];

                if (sub.equalsIgnoreCase("help")) {
                    p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "GAMEMODE " + ChatColor.DARK_RED + "" + ChatColor.BOLD + "HELP:");
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
//                try {
//                    cmds.get(sub).onCommand(p, args);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    p.sendMessage(Messaging.Tag.ERROR.getTag() + "Could not find the command '" + sub + "'!");
//                    return true;
//                }
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
