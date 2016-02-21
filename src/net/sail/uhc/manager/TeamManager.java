package net.sail.uhc.manager;

import net.sail.uhc.UHCCore;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.Messaging;
import net.sail.uhc.utils.UHCTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by brand on 12/30/2015.
 */
public class TeamManager {

    private final GameSettings gameSettings;

    private ArrayList<UHCTeam> teams = new ArrayList<UHCTeam>();

    private Integer inviteCountdownTime = 60;
    private HashSet<Player> soloPlayers = new HashSet<Player>();
    private HashMap<Player, Integer> inviteCountdown = new HashMap<Player, Integer>();
    private HashMap<Player, UHCTeam> invitedTeam = new HashMap<Player, UHCTeam>();
    private ArrayList<UUID> inTeamChat = new ArrayList<UUID>();

    public TeamManager(UHCCore uhcCore, GameSettings gameSettings) {
        this.gameSettings = gameSettings;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(uhcCore, new Runnable() {
            @Override
            public void run() {
                for (Player p : inviteCountdown.keySet()) {
                    if (inviteCountdown.get(p) == 0) {
                        inviteCountdown.remove(p);
                        invitedTeam.remove(p);
                        p.sendMessage(Messaging.Tag.ALERT.getTag() + "You have declined the team invite.");
                    } else {
                        inviteCountdown.put(p, inviteCountdown.get(p) - 1);
                    }
                }
            }
        }, 20L, 20L);
    }

    public void createTeam(UUID owner, String name) {
        if (teamExists(name)) {
            return;
        }
        teams.add(new UHCTeam(owner, name));

    }

    public void deleteTeamFromOwner(UUID p) {
        UHCTeam team = getTeamFromOwner(p);

        for (UUID uuid : team.getMembers()) {
            Bukkit.getPlayer(uuid).sendMessage(Messaging.Tag.ALERT.getTag() + "Your team was disbanded, you currently are teamless :(");
        }

        teams.remove(team);
    }

    public void deleteTeamFromName(String name) {
        UHCTeam team;
        try {
            team = getTeamFromName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        for (UUID uuid : team.getMembers()) {
            Bukkit.getPlayer(uuid).sendMessage(Messaging.Tag.ALERT.getTag() + "Your team was disbanded, you currently are teamless :(");
        }

        teams.remove(team);
    }

    public void removeMemberFromTeam(UUID pOwner, UUID pUUID) {
        if (getTeamFromOwner(pOwner) == null) { return; }

        UHCTeam team = getTeamFromOwner(pOwner);
        //TODO: this will error when a player left, finding a solution...
        Bukkit.getPlayer(pUUID).sendMessage(Messaging.Tag.ALERT.getTag() + "You have been removed from the team " + team.getName() + ", you are teamless :(");
        team.removeMember(pUUID);

        for (UUID p : team.getMembers()) {
            Bukkit.getPlayer(p).sendMessage(Messaging.Tag.ALERT.getTag() + Bukkit.getPlayer(pUUID).getName() + " has been removed from your team!");

            if (isInTeamChat(p)) {
                removeFromInTeamChat(p);
            }
        }

        teams.remove(getTeamFromOwner(pOwner));
        teams.add(team);
    }

    public void removeSoloPlayer(Player p) {
        if (soloPlayers.contains(p)) {
            soloPlayers.remove(p);
        }
    }

    public void addSoloPlayer(Player p) {
        soloPlayers.add(p);
    }

    public boolean isSolo(Player p) {
        return soloPlayers.contains(p);
    }

    public boolean teamExists(String name) {
        for (UHCTeam team : teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean playerHasTeam(UUID p) {
        for (UHCTeam team : teams) {
            if (team.getOwner() == p) {
                return true;
            }
        }
        return false;
    }

    public UHCTeam getTeamFromOwner(UUID p) {
        for (UHCTeam team : teams) {
            if (team.getOwner() == p) {
                return team;
            }
        }
        return null;
    }

    public UHCTeam getTeamFromMember(UUID p) {
        for (UHCTeam team : teams) {
            if (team.getMembers().contains(p)) {
                return team;
            }
        }
        return getTeamFromOwner(p);
    }

    public UHCTeam getTeamFromName(String name) {
        for (UHCTeam team : teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return team;
            }
        }
        return null;
    }

    public boolean playerIsMemberOfTeam(UUID p) {
        for (UHCTeam team : teams) {
            if (team.getMembers().contains(p)) {
                return true;
            }
        }
        return false;
    }

    public boolean playerIsInvitedToTeam(Player p) {
        return inviteCountdown.containsKey(p);
    }

    public boolean isInTeamChat(UUID p) { return inTeamChat.contains(p); }

    public void invitePlayer(Player p, UHCTeam team) {
        invitedTeam.put(p, team);
        inviteCountdown.put(p, inviteCountdownTime);

        p.sendMessage(Messaging.Tag.ALERT.getTag() + "You have been invited to the team " + team.getName());
        p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "----------------------------------");
        p.sendMessage(ChatColor.GREEN+"/team join");
        p.sendMessage(ChatColor.RED + "/team decline");
//        new FancyMessage("ACCEPT")
//                .color(ChatColor.GREEN)
//                .command("team join")
//                .tooltip(ChatColor.WHITE + "" + ChatColor.BOLD + "CLICK TO " + ChatColor.GREEN + "" + ChatColor.BOLD + "ACCEPT")
//                .send(p);
//        new FancyMessage("DECLINE")
//                .color(ChatColor.RED)
//                .command("team decline")
//                .tooltip(ChatColor.WHITE + "" + ChatColor.BOLD + "CLICK TO " + ChatColor.RED + "" + ChatColor.BOLD + "DECLINE")
//                .send(p);
        p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "----------------------------------");
    }

    public void acceptInvite(Player p) {
        UHCTeam pTeam = invitedTeam.get(p);
        pTeam.addMember(p.getUniqueId());

        teams.remove(invitedTeam.get(p));
        teams.add(pTeam);

        Bukkit.getPlayer(pTeam.getOwner()).sendMessage(Messaging.Tag.ALERT.getTag() + p.getName() + " has joined your team.");
        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have joined the team " + pTeam.getName());

        if (inviteCountdown.containsKey(p)) {
            inviteCountdown.remove(p);
        }

        if (invitedTeam.containsKey(p)) {
            invitedTeam.remove(p);
        }

    }

    public void sendTeamList(Player p, int page) {
        p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "<" + ChatColor.GRAY + "" + ChatColor.BOLD + "TEAMS:" + ChatColor.WHITE + "" + ChatColor.BOLD + ">");

        if (teams.size() > 10) {
            for (int i = 1*page; i < page + 10; i++) {
                p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "  - " + teams.get(i).getName());
            }
            p.sendMessage(ChatColor.GRAY + "Page " + Integer.toString(page) +"/"+ teams.size()/10 + " (/team list #)");
        } else {
            for (UHCTeam team : teams) {
                p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "  - " + team.getName());
            }
            p.sendMessage(ChatColor.GRAY + "Page 1/1 (/team list #)");
        }
    }

    public void declineInvite(Player p) {
        UHCTeam pTeam = invitedTeam.get(p);

        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "You have declined the team " + pTeam.getName());

        if (inviteCountdown.containsKey(p)) {
            inviteCountdown.remove(p);
        }

        if (invitedTeam.containsKey(p)) {
            invitedTeam.remove(p);
        }
    }

    public void sortUnTeamedPlayers() {

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOnline()) {
                if (!gameSettings.isHost(p.getUniqueId())) {
                    if (!playerHasTeam(p.getUniqueId()) && !playerIsMemberOfTeam(p.getUniqueId()) && !isSolo(p)) {
                        if (p.getName().length() > 11) {
                            createTeam(p.getUniqueId(), p.getName().substring(0, 11));
                        } else {
                            createTeam(p.getUniqueId(), p.getName());
                        }
                    }
                }
            }
        }

        for (Player p : soloPlayers) {
            if (p.isOnline()) {
                if (!gameSettings.isHost(p.getUniqueId())) {
                    if (p.getName().length() > 11) {
                        createTeam(p.getUniqueId(), p.getName().substring(0, 11));
                    } else {
                        createTeam(p.getUniqueId(), p.getName());
                    }
                }
            }
        }
    }

    public void addToInTeamChat(UUID p) {
        inTeamChat.add(p);
    }

    public void removeFromInTeamChat(UUID p) {
        inTeamChat.remove(p);
    }

    public ArrayList<UHCTeam> getTeams() { return teams; }
}
