package net.sail.uhc.commands.teamsubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 12/30/2015.
 */
public class List implements SubCommand {

    private final TeamManager teamManager;

    public List(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        Integer pageNum;

        if (args.length == 1) {
            pageNum = Integer.parseInt(args[0]);
        } else {
            pageNum = 1;
        }

        teamManager.sendTeamList(p, pageNum);

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/team leave, leaves a team");
    }

}
