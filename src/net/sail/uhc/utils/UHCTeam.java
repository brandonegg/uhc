package net.sail.uhc.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by brand on 12/30/2015.
 */
public class UHCTeam {

    public UHCTeam(UUID owner, String name) {
        this.owner = owner;
        this.name = name;

//        ScoreboardManager manager = Bukkit.getScoreboardManager();
//        Scoreboard board = manager.getNewScoreboard();
//        Objective objective = board.registerNewObjective("team", "dummy");
//        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
//        objective.setDisplayName(name);
//        Bukkit.getPlayer(owner).setScoreboard(board);
//
//        this.team = board.registerNewTeam(name);
//        this.team.setSuffix(ChatColor.RED + name);
//        this.team.setDisplayName(name);
    }

    private UUID owner;
    private String name;
    private ArrayList<UUID> members = new ArrayList<UUID>();

    public boolean containsUser(UUID uuid) {
        return (owner == uuid ? true : members.contains(uuid));
    }

    public void removeMember(UUID uuid) {
        if (members.contains(uuid)) {
            members.remove(uuid);
        }
    }

    public void addMember(UUID uuid) {
        members.add(uuid);
    }

    public void setOwner(UUID p) { owner = p; }

    public ArrayList<UUID> getMembers() { return members; }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public Integer getTeamSize() {
        return (owner != null ? members.size()+1 : members.size());
    }
}
