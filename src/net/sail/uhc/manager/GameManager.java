package net.sail.uhc.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import javafx.util.Pair;
import net.sail.uhc.UHCCore;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.*;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import java.security.Key;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by brand on 1/3/2016.
 */
public class GameManager {

    private final GameSettings gameSettings;
    private final TeamManager teamManager;
    private final UHCCore uhcCore;
    private final GameStatus gameStatus;
    private final GameUtils gameUtils;

    private final Random randomInstance;

    private BukkitTask finalHealLoop = null;
    private boolean finalHealLoopActive = false;
    private boolean gameEnded = false;

    private final World map;
    private ArrayList<UUID> inGamePlayers = new ArrayList<UUID>();
    private HashMap<Player, Integer> topKills = new HashMap<Player, Integer>();
    private HashMap<Player, Location> playerMapSpawns = null;

    private int peacefulTimeLeft;
    private int gameTime;
    private int finalHealTimeRemaining;

    public GameManager(GameSettings gameSettings, TeamManager teamManager, GameStatus gameStatus, UHCCore uhcCore, GameUtils gameUtils, World map) {
        this.gameSettings = gameSettings;
        this.teamManager = teamManager;
        this.uhcCore = uhcCore;
        this.gameStatus = gameStatus;
        this.gameUtils = gameUtils;
        this.map = map;

        this.randomInstance = new Random();

        finalHealTimeRemaining = gameSettings.getFinalHealTime();

        freezePlayers();
        loadPlayerSpawns();

        new BukkitRunnable() {
            @Override
            public void run() {
                teleportTeams();
            }
        }.runTaskLater(UHCCore.getPlugin(), 100);

        new BukkitRunnable() {
            @Override
            public void run() {
                unFreezePlayers();
            }
        }.runTaskLater(UHCCore.getPlugin(), 300);

        startPeacefulTime();

        setupHosts();

        if (gameSettings.isPermDay()) {
            startPermDay();
        }

        if (gameStatus.isFinalHealEnabled()) {
            startFinalHeal(gameSettings.getFinalHealTime());
        }

        addCustomCrafting();
    }

    public void teleportTeams() {
        Bukkit.broadcastMessage(Messaging.Tag.ALERT.getTag() + "Teleporting players!");
        if (playerMapSpawns == null) {
            Bukkit.getLogger().info("Teleport failed, teleport locations have not been loaded yet.");
            Messaging.sendHostMessage("Teleport failed, teleport locations have not been loaded yet. Report this to a developer.");
            return;
        }

        int loopamount = 1;
        for (Player p : playerMapSpawns.keySet()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(p.isOnline()) {
                        p.teleport(playerMapSpawns.get(p));
                        if (!map.getChunkAt(playerMapSpawns.get(p).getBlockX(), playerMapSpawns.get(p).getBlockZ()).isLoaded()) {
                            map.loadChunk(playerMapSpawns.get(p).getBlockX(), playerMapSpawns.get(p).getBlockZ());
                        }
                        gameUtils.setupForUHC(p);
                        p.setWalkSpeed((float) -1);
                        inGamePlayers.add(p.getUniqueId());
                    }
                }
            }.runTaskLater(uhcCore, 5*loopamount);
            loopamount++;
        }
    }

    public void loadPlayerSpawns() {
        HashMap<Player, Location> playerSpawns = new HashMap<Player, Location>();
        HashSet<Chunk> chunksAlreadyUsed = new HashSet<Chunk>();
        Bukkit.getLogger().info("Loading player spawn locations");

        for (UHCTeam team : teamManager.getTeams()) {
            Chunk tpChunk = getRandomChunk();

            boolean taken = true;
            int numOfTry = 0;
            while (taken) {
                if (numOfTry > 32) {
                    break;
                    //Safety switch
                }
                tpChunk = getRandomChunk();
                if (chunksAlreadyUsed.contains(tpChunk)) {
                    taken = true;
                    Bukkit.getLogger().warning("Chunk was taken, finding new one.");
                } else {
                    taken = false;
                }
                numOfTry++;
            }

            chunksAlreadyUsed.add(tpChunk);

            if (!tpChunk.isLoaded()) {
                tpChunk.load();
            }

            if (Bukkit.getPlayer(team.getOwner()) == null) {
                if (team.getTeamSize() > 1) {
                    team.setOwner(team.getMembers().get(0));
                    team.removeMember(team.getMembers().get(0));
                } else {
                    teamManager.deleteTeamFromOwner(team.getOwner());
                }
            }

            for (UUID player : team.getMembers()) {
                Player p = Bukkit.getPlayer(player);

                if (p == null) {
                    team.removeMember(player);
                } else {
                    Location tpLoc = getRandomLocationInChunk(tpChunk);
                    playerSpawns.put(p, tpLoc);
                }
            }
            Player owner = Bukkit.getPlayer(team.getOwner());
            Location tpLoc = getRandomLocationInChunk(tpChunk);
            playerSpawns.put(owner, tpLoc);
        }

        playerMapSpawns = playerSpawns;
    }

    public void startPeacefulTime() {
        peacefulTimeLeft = gameSettings.getPeacefulTime();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(uhcCore, new Runnable() {
            @Override
            public void run() {
                if (peacefulTimeLeft == 0) {
                    Bukkit.broadcastMessage(Messaging.Tag.ALERT.getTag() + "Peaceful mode is off, let the games begin.");
                    startGameTime();
                    peacefulTimeLeft = -1;
                    return;
                } else if (peacefulTimeLeft == -1) {
                    return;
                }
                peacefulTimeLeft--;
            }
        }, 20L, 20L);
    }

    public void addCustomCrafting() {
        ItemStack completedItem = new ItemStack(Material.GOLDEN_APPLE, 1);
        ShapedRecipe goldenApple = new ShapedRecipe(completedItem);

        goldenApple.shape("%%%","%B%","%%%");

        goldenApple.setIngredient('%', Material.GOLD_INGOT);
        goldenApple.setIngredient('B', Material.SKULL_ITEM);

        Bukkit.getServer().addRecipe(goldenApple);
    }

    public void startGameTime() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(uhcCore, new Runnable() {
            @Override
            public void run() {
                gameTime++;
            }
        }, 20L, 20L);
    }

    private ArrayList<Chunk> badChunks = new ArrayList<Chunk>();

    public Chunk getRandomChunk() {
        int x = randomInstance.nextInt(gameSettings.getArenaSize());
        int z = randomInstance.nextInt(gameSettings.getArenaSize());
//TODO: Fix biome detection
        while (badChunks.contains(map.getChunkAt(x,z))) {
            Bukkit.getLogger().info("looped finding random chunk");
            x = randomInstance.nextInt(gameSettings.getArenaSize());
            z = randomInstance.nextInt(gameSettings.getArenaSize());
        }

        if (map.getBiome(x, z).equals(Biome.DEEP_OCEAN) || map.getBiome(x, z).equals(Biome.OCEAN)) {
            Bukkit.getLogger().info(map.getBiome(x, z).toString());
            Bukkit.getLogger().warning("searching for new chunk");
            badChunks.add(map.getChunkAt(x,z));
            return getRandomChunk();
        }

        return map.getChunkAt(x, z);
    }

    private HashMap<Integer, Integer> usedLocations = new HashMap<Integer, Integer>();

    public Location getRandomLocationInChunk(Chunk chunk) {
        int x = randomInstance.nextInt(16)-8;
        int z = randomInstance.nextInt(16)-8;
        int y = map.getHighestBlockYAt(x, z);

        while (usedLocations.containsKey(x) && usedLocations.containsValue(y)) {
            Bukkit.getLogger().info("Looped finding a location");
            Bukkit.getLogger().info("Location checked already, finding new location");
            x = randomInstance.nextInt(16)-8;
            z = randomInstance.nextInt(16)-8;
        }

        usedLocations.put(x, z);

        Location loc = new Location(map, chunk.getX()+x, y, chunk.getZ()+z);
        if (map.getBlockAt(loc.getBlockX(), loc.getBlockY()-1, loc.getBlockZ()).getType().equals(Material.STATIONARY_WATER)
                || map.getBlockAt(loc.getBlockX(), loc.getBlockY()-1, loc.getBlockZ()).getType().equals(Material.STATIONARY_LAVA)
                || map.getBlockAt(loc.getBlockX(), loc.getBlockY()-1, loc.getBlockZ()).getType().equals(Material.AIR)
                || map.getBlockAt(loc).getType().equals(Material.AIR)) {
            Bukkit.getLogger().info(map.getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ()).getType().toString());
            Bukkit.getLogger().warning("searching for new location");
            return getRandomLocationInChunk(chunk);
        }

        usedLocations.remove(x, z);
        return loc;
    }

    public int getPeacefulTime() {
        return peacefulTimeLeft;
    }

    public int getGameTime() {
        return gameTime;
    }

    public boolean inPeacefulTime() {
        return (peacefulTimeLeft > -1);
    }

    public World getMap() {
        return map;
    }

    public void freezePlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!gameSettings.isHost(p.getUniqueId())) {
                p.addPotionEffect(PotionEffectType.JUMP.createEffect(400, 128));
                p.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(1000, 2));
                p.setWalkSpeed((float) -1);
                p.setGameMode(GameMode.SURVIVAL);
                p.setFlying(false);
                p.setHealth(20);
                p.setFoodLevel(20);
                for (ItemStack item : p.getInventory().getContents()) {
                    p.getInventory().remove(item);
                }
            }
        }
    }

    public void unFreezePlayers() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!gameSettings.isHost(p.getUniqueId())) {
                p.setWalkSpeed((float) 0.2);
            }
        }
    }

    public void setupHosts() {
        for (Player hostP : Bukkit.getOnlinePlayers()) {
            if (gameSettings.isHost(hostP.getUniqueId()) || gameStatus.isSpectator(hostP.getUniqueId())) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.hidePlayer(hostP);
                }
                hostP.sendMessage(Messaging.Tag.ALERT.getTag() + "You are a game spectator.");
                hostP.teleport(map.getSpawnLocation());
                hostP.setGameMode(GameMode.CREATIVE);
                hostP.setFlying(true);
            } else {
                inGamePlayers.add(hostP.getUniqueId());
            }
        }
    }

    public void endGame() {
        if (gameEnded) {
            return;
        }
        gameEnded = true;
        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "-----------------------------------");
        Bukkit.broadcastMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "           Sail" + ChatColor.AQUA + "" + ChatColor.BOLD + "MC" + ChatColor.GREEN + " recap:");
        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "-----------------------------------");
        if (topKills.size() > 0) {
            Bukkit.broadcastMessage(ChatColor.BLUE + "Top kills: " + ChatColor.WHITE);
        }
        UHCTeam team = null;
        for (UUID p : inGamePlayers) {
            if (teamManager.playerHasTeam(p)) {
                team = teamManager.getTeamFromOwner(p);
            } else {
                team = teamManager.getTeamFromMember(p);
            }
        }
        if (team != null) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Winning team: " + ChatColor.WHITE + team.getName());
        }
        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "-----------------------------------");
        new BukkitRunnable() {

            @Override
            public void run() {
                Bukkit.broadcastMessage(Messaging.Tag.IMPORTANT.getTag() + "Server restarting, teleporting players back to lobby.");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    UHCCore.sendToServer(p, "HUB-1");
                }
            }

        }.runTaskLater(uhcCore, 100L);
        new BukkitRunnable() {
            @Override
            public void run() {
                uhcCore.restart();
            }
        }.runTaskLater(uhcCore, 200L);
    }

    public boolean isInGame(UUID p) {
        return inGamePlayers.contains(p);
    }

    public void addInGamePlayer(UUID p) {
        inGamePlayers.add(p);
    }

    public void removeInGame(UUID p) {
        inGamePlayers.remove(p);
    }

    public void setTopKills(Player p, Integer amount) {
        if (topKills.containsKey(p)) {
            topKills.put(p, topKills.get(p)+amount);
        } else {
            topKills.put(p, amount);
        }
    }

    public void startPermDay() {
        map.setTime(0);

        new BukkitRunnable() {
            @Override
            public void run() {
                map.setTime(0);
            }
        }.runTaskTimer(UHCCore.getPlugin(), 1200, 1200);
    }

    public void startFinalHeal(int time) {
        finalHealTimeRemaining = time;

        finalHealLoopActive = true;
        finalHealLoop = new BukkitRunnable() {
            @Override
            public void run() {
                if (finalHealTimeRemaining == 0) {
                    Bukkit.broadcastMessage(Messaging.Tag.ALERT.getTag() + "Players have been healed!");
                    for (UUID uuid : getPlayersInGame()) {
                        if (Bukkit.getPlayer(uuid) != null) {
                            Bukkit.getPlayer(uuid).setHealth(20);
                        }
                    }
                    finalHealLoopActive = false;
                    finalHealLoop.cancel();
                } else {
                    finalHealTimeRemaining--;
                }
            }
        }.runTaskTimer(UHCCore.getPlugin(), 20, 20);
    }

    public void stopFinalHeal() {
        finalHealLoop.cancel();
        finalHealLoopActive = false;
    }

    public Location getPlayerSpawn(Player p) {
        return (playerMapSpawns.containsKey(p) ? playerMapSpawns.get(p) : null);
    }
    public Location getPlayerSpawn(UUID player) {
        Player p = null;
        if (Bukkit.getPlayer(player) != null) {
            p = Bukkit.getPlayer(player);
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
            if (offlinePlayer.getPlayer() != null) {
                p = offlinePlayer.getPlayer();
            }
        }
        return (playerMapSpawns.containsKey(p) ? playerMapSpawns.get(p) : null);
    }

    public boolean isFinalHealLoopActive() { return finalHealLoopActive; }

    public int getFinalHealTimeRemaining() { return finalHealTimeRemaining; }

    public ArrayList<UUID> getPlayersInGame() { return inGamePlayers; }
}
