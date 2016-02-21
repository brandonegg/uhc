package net.sail.uhc.manager;

import net.sail.uhc.UHCCore;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by brand on 1/3/2016.
 */
public class ServerScoreboardManager {

    private final GameStatus gameStatus;
    private final GameSettings gameSettings;
    private final TeamManager teamManager;
    private final UHCCore uhcCore;
    private final ScoreboardManager scoreboardManager;

    private static final Random RAND = new Random();

    public ServerScoreboardManager(GameStatus gameStatus, GameSettings gameSettings, TeamManager teamManager, UHCCore uhcCore) {
        this.gameStatus = gameStatus;
        this.gameSettings = gameSettings;
        this.teamManager = teamManager;
        this.uhcCore = uhcCore;

        scoreboardManager = Bukkit.getScoreboardManager();
    }

    public void loadScoreboards() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            loadBoard(p);
        }
    }

    public void loadBoard(Player p) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        final Objective obj = board.registerNewObjective("test", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);


        ArrayList<ChatColor> colors = createColors();

        ArrayList<Team> teams = new ArrayList<Team>();
        HashMap<Team, ChatColor> teamColor = new HashMap<Team, ChatColor>();

        for (int i = 0; i < 13; i++) {
            final Team team = board.registerNewTeam("row"+Integer.toString(i));
            ChatColor chatColor = colors.get(0);
            colors.remove(chatColor);

            team.addPlayer(Bukkit.getOfflinePlayer(chatColor.toString()));
            obj.getScore(chatColor.toString()).setScore(i);
            teams.add(team);
            teamColor.put(team, chatColor);
        }

        obj.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Sail" + ChatColor.AQUA + "" + ChatColor.BOLD + "MC" + ChatColor.GRAY + " (UHC)");

        new BukkitRunnable() {
            ChatColor next = ChatColor.RED;

            @Override
            public void run() {
                ArrayList<String> newMsgs = createScoreMessages(p);

                for (Team team : teams) {
                    try {
                        int line = teams.size()-teams.indexOf(team)-1;
                        String msg = newMsgs.get(line);
                        team.setPrefix(msg);
                        obj.getScore(teamColor.get(team).toString()).setScore(teams.indexOf(team));
                    } catch (IndexOutOfBoundsException e) {
                        board.resetScores(teamColor.get(team).toString());
                    }
                }
            }

        }.runTaskTimer(UHCCore.getPlugin(), 80, 20);

        p.setScoreboard(board);
    }

    public ArrayList<ChatColor> createColors() {
        ArrayList<ChatColor> colors = new ArrayList<ChatColor>();

        colors.add(ChatColor.BLACK);
        colors.add(ChatColor.WHITE);
        colors.add(ChatColor.DARK_GRAY);
        colors.add(ChatColor.DARK_RED);
        colors.add(ChatColor.RED);
        colors.add(ChatColor.BLUE);
        colors.add(ChatColor.GREEN);
        colors.add(ChatColor.YELLOW);
        colors.add(ChatColor.GOLD);
        colors.add(ChatColor.DARK_PURPLE);
        colors.add(ChatColor.GRAY);
        colors.add(ChatColor.LIGHT_PURPLE);
        colors.add(ChatColor.AQUA);
        colors.add(ChatColor.DARK_AQUA);

        return colors;
    }

    public ArrayList<String> createScoreMessages(Player p) {
        ArrayList<String> scores = new ArrayList<String>();

        if (gameStatus.getStatus().equals(GameStatus.Status.IN_LOBBY) || gameStatus.getStatus().equals(GameStatus.Status.LOBBY_COUNTDOWN)) {
            scores.add(" ");

            scores.add(ChatColor.GREEN + "In Lobby: " + ChatColor.WHITE + Integer.toString(Bukkit.getOnlinePlayers().size()));

            if (gameStatus.getStatus().equals(GameStatus.Status.LOBBY_COUNTDOWN)) {
                scores.add(ChatColor.GREEN + "Countdown: ");
                scores.add(ChatColor.WHITE + "" + ChatColor.BOLD + Messaging.secondsToTime(gameStatus.getLobbyCountdownTime()));
            } else {
                scores.add("  ");
                scores.add(ChatColor.RED + "Waiting..." );
            }

            scores.add("   ");

            if (teamManager.playerHasTeam(p.getUniqueId())) {
                scores.add(ChatColor.RED + "" + ChatColor.BOLD + "TEAM:");
                scores.add(ChatColor.WHITE + teamManager.getTeamFromOwner(p.getUniqueId()).getName() + ChatColor.GOLD +"*");
            } else if (teamManager.playerIsMemberOfTeam(p.getUniqueId())) {
                scores.add(ChatColor.RED + "" + ChatColor.BOLD + "TEAM:");
                scores.add(ChatColor.WHITE + teamManager.getTeamFromMember(p.getUniqueId()).getName());
            } else {
                scores.add(ChatColor.RED + "" + ChatColor.BOLD + "TEAM: " + ChatColor.WHITE + "N/A");
            }
        } else if (gameStatus.getStatus().equals(GameStatus.Status.COUNTDOWN_UNTIL_LOBBY)) {
            scores.add(" ");
            scores.add(ChatColor.GREEN + "Next game:");
            scores.add(Messaging.secondsToTime(gameStatus.getGameCountdownTime()));
        } else if (gameStatus.getStatus().equals(GameStatus.Status.IN_GAME)) {

            scores.add(" ");
            scores.add(ChatColor.GREEN + "Border Size:");
            scores.add(gameStatus.getBorderSize().toString());
            if (gameStatus.isAutoShrinkEnabled()) {
                scores.add(ChatColor.GREEN + "Shrink Time:");
                scores.add(Messaging.secondsToTime(gameStatus.getCurrentShrinkTime()));
            }
            if (gameStatus.getGameManager().isFinalHealLoopActive()) {
                scores.add(ChatColor.RED + "Final heal:");
                scores.add(Messaging.secondsToTime(gameStatus.getGameManager().getFinalHealTimeRemaining()));
            } else {
                scores.add("   ");
            }
            if (gameStatus.getGameManager().getPeacefulTime() > 0) {
                scores.add(ChatColor.YELLOW + "Peaceful time:");
                scores.add(Messaging.secondsToTime(gameStatus.getGameManager().getPeacefulTime()));
            } else {
                scores.add(ChatColor.YELLOW + "Game time:");
                scores.add(Messaging.secondsToTime(gameStatus.getGameManager().getGameTime()));
            }
            scores.add("    ");
            if (teamManager.playerHasTeam(p.getUniqueId())) {
                scores.add(ChatColor.RED + "" + ChatColor.BOLD + "TEAM:");
                scores.add(ChatColor.WHITE + teamManager.getTeamFromOwner(p.getUniqueId()).getName() + ChatColor.GOLD +"*");
            } else if (teamManager.playerIsMemberOfTeam(p.getUniqueId())) {
                scores.add(ChatColor.RED + "" + ChatColor.BOLD + "TEAM:");
                scores.add(ChatColor.WHITE + teamManager.getTeamFromMember(p.getUniqueId()).getName());
            } else {
                scores.add(ChatColor.RED + "" + ChatColor.BOLD + "TEAM: " + ChatColor.WHITE + "none");
            }
        }
        scores.add(ChatColor.GRAY + "" + ChatColor.BOLD + "-----------");
        scores.add("play.sailmc.net");
        return scores;
    }
}
