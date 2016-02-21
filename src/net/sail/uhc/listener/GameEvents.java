package net.sail.uhc.listener;

import net.sail.uhc.UHCCore;
import net.sail.uhc.manager.ScenarioManager;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Messaging;
import net.sail.uhc.utils.UHCTeam;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by brand on 1/10/2016.
 */
public class GameEvents implements Listener {

    private final GameSettings gameSettings;
    private final GameStatus gameStatus;
    private final UHCCore uhcCore;
    private final TeamManager teamManager;
    private final ScenarioManager scenarioManager;

    private final int spamDelayTime = 10;

    private HashMap<Player, HashSet<Location>> replacedBlocks = new HashMap<Player, HashSet<Location>>();

    private HashSet<Player> nearBorderMute = new HashSet<Player>();

    public GameEvents(GameSettings gameSettings, GameStatus gameStatus, TeamManager teamManager, UHCCore uhcCore, ScenarioManager scenarioManager) {
        this.gameSettings = gameSettings;
        this.gameStatus = gameStatus;
        this.uhcCore = uhcCore;
        this.teamManager = teamManager;
        this.scenarioManager = scenarioManager;
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location pLocation = p.getLocation();

        if (pLocation.getBlockX() >= gameStatus.getBorderSize()-2 || pLocation.getBlockZ() >= gameStatus.getBorderSize()-2
                || pLocation.getBlockX() <= -(gameStatus.getBorderSize()-2) || pLocation.getBlockZ() <= -(gameStatus.getBorderSize()-2)) {
            blockPath(p);
        } else if (pLocation.getBlockX() == gameStatus.getBorderSize()-3 || pLocation.getBlockZ() == gameStatus.getBorderSize()-3
                || pLocation.getBlockX() == -(gameStatus.getBorderSize()-3) || pLocation.getBlockZ() == -(gameStatus.getBorderSize()-3)) {
            if (replacedBlocks.containsKey(p)) {
                HashSet<Location> locationsPrevReplaced = replacedBlocks.get(p);

                for (Location l : locationsPrevReplaced) {
                    l.getBlock().setType(Material.AIR);
                }

                replacedBlocks.remove(p);
            }
        } else if (pLocation.getX() >= gameStatus.getBorderSize()-10 || pLocation.getZ() >= gameStatus.getBorderSize()-10
                || -(pLocation.getX()) >= gameStatus.getBorderSize()-10 || -(pLocation.getZ()) >= gameStatus.getBorderSize()-10) {
            if (!nearBorderMute.contains(p)) {
                p.sendMessage(Messaging.Tag.ALERT.getTag() + "You are near the border!");
                nearBorderMute.add(p);
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        nearBorderMute.remove(p);
                    }

                }.runTaskLater(uhcCore, spamDelayTime * 20);
            }
        }
        if (pLocation.getX() > gameStatus.getBorderSize()) {
            p.teleport(new Location(p.getWorld(), gameStatus.getBorderSize()-5, p.getWorld().getHighestBlockYAt(gameStatus.getBorderSize()-5, p.getLocation().getBlockZ()), p.getLocation().getZ()));
        } else if (pLocation.getZ() > gameStatus.getBorderSize()) {
            p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getWorld().getHighestBlockYAt(p.getLocation().getBlockX(), gameStatus.getBorderSize() - 5), gameStatus.getBorderSize()-5));
        } else if (-(pLocation.getZ()) > gameStatus.getBorderSize()) {
            p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getWorld().getHighestBlockYAt(p.getLocation().getBlockX(), -(gameStatus.getBorderSize() - 5)), -(gameStatus.getBorderSize()-5)));
        } else if (-(pLocation.getBlockX()) > gameStatus.getBorderSize()) {
            p.teleport(new Location(p.getWorld(), -(gameStatus.getBorderSize()-5), p.getWorld().getHighestBlockYAt(-(gameStatus.getBorderSize()-5), p.getLocation().getBlockZ()), p.getLocation().getZ()));
        }
    }

    public void blockPath(Player p) {
        Location pLocation = p.getLocation();
        Integer borderSize = gameStatus.getBorderSize();

        HashSet<Location> replaced = new HashSet<Location>();

        if (replacedBlocks.containsKey(p)) {
            HashSet<Location> locationsPrevReplaced = replacedBlocks.get(p);

            locationsPrevReplaced.stream().forEach((l) -> {
                l.getBlock().setType(Material.AIR);
            });

            replacedBlocks.remove(p);
        }

        if (pLocation.getBlockX() == gameStatus.getBorderSize()-1) {
            Location blockLocation1 = new Location(pLocation.getWorld(), pLocation.getBlockX()+1, pLocation.getBlockY(), pLocation.getBlockZ()-1);
            replaced.add(blockLocation1);
            Location blockLocation2 = new Location(pLocation.getWorld(), pLocation.getBlockX()+1, pLocation.getBlockY(), pLocation.getBlockZ());
            replaced.add(blockLocation2);
            Location blockLocation3 = new Location(pLocation.getWorld(), pLocation.getBlockX()+1, pLocation.getBlockY(), pLocation.getBlockZ()+1);
            replaced.add(blockLocation3);

            Location blockLocation4 = new Location(pLocation.getWorld(), pLocation.getBlockX()+1, pLocation.getBlockY()+1, pLocation.getBlockZ()-1);
            replaced.add(blockLocation4);
            Location blockLocation5 = new Location(pLocation.getWorld(), pLocation.getBlockX()+1, pLocation.getBlockY()+1, pLocation.getBlockZ());
            replaced.add(blockLocation5);
            Location blockLocation6 = new Location(pLocation.getWorld(), pLocation.getBlockX()+1, pLocation.getBlockY()+1, pLocation.getBlockZ()+1);
            replaced.add(blockLocation6);
        } else if (pLocation.getBlockZ() == gameStatus.getBorderSize()-1) {
            Location blockLocation1 = new Location(pLocation.getWorld(), pLocation.getBlockX()-1, pLocation.getBlockY(), pLocation.getBlockZ()+1);
            replaced.add(blockLocation1);
            Location blockLocation2 = new Location(pLocation.getWorld(), pLocation.getBlockX(), pLocation.getBlockY(), pLocation.getBlockZ()+1);
            replaced.add(blockLocation2);
            Location blockLocation3 = new Location(pLocation.getWorld(), pLocation.getBlockX()+1, pLocation.getBlockY(), pLocation.getBlockZ()+1);
            replaced.add(blockLocation3);

            Location blockLocation4 = new Location(pLocation.getWorld(), pLocation.getBlockX()-1, pLocation.getBlockY()+1, pLocation.getBlockZ()+1);
            replaced.add(blockLocation4);
            Location blockLocation5 = new Location(pLocation.getWorld(), pLocation.getBlockX(), pLocation.getBlockY()+1, pLocation.getBlockZ()+1);
            replaced.add(blockLocation5);
            Location blockLocation6 = new Location(pLocation.getWorld(), pLocation.getBlockX()+1, pLocation.getBlockY()+1, pLocation.getBlockZ()+1);
            replaced.add(blockLocation6);
        } else if (pLocation.getBlockX() == -(gameStatus.getBorderSize()-1)) {
            Location blockLocation1 = new Location(pLocation.getWorld(), pLocation.getBlockX()-1, pLocation.getBlockY(), pLocation.getBlockZ()-1);
            replaced.add(blockLocation1);
            Location blockLocation2 = new Location(pLocation.getWorld(), pLocation.getBlockX()-1, pLocation.getBlockY(), pLocation.getBlockZ());
            replaced.add(blockLocation2);
            Location blockLocation3 = new Location(pLocation.getWorld(), pLocation.getBlockX()-1, pLocation.getBlockY(), pLocation.getBlockZ()+1);
            replaced.add(blockLocation3);

            Location blockLocation4 = new Location(pLocation.getWorld(), pLocation.getBlockX()-1, pLocation.getBlockY()+1, pLocation.getBlockZ()-1);
            replaced.add(blockLocation4);
            Location blockLocation5 = new Location(pLocation.getWorld(), pLocation.getBlockX()-1, pLocation.getBlockY()+1, pLocation.getBlockZ());
            replaced.add(blockLocation5);
            Location blockLocation6 = new Location(pLocation.getWorld(), pLocation.getBlockX()-1, pLocation.getBlockY()+1, pLocation.getBlockZ()+1);
            replaced.add(blockLocation6);
        } else if (pLocation.getBlockZ() == -(gameStatus.getBorderSize()-1)) {
            Location blockLocation1 = new Location(pLocation.getWorld(), pLocation.getBlockX()-1, pLocation.getBlockY(), pLocation.getBlockZ()-1);
            replaced.add(blockLocation1);
            Location blockLocation2 = new Location(pLocation.getWorld(), pLocation.getBlockX(), pLocation.getBlockY(), pLocation.getBlockZ()-1);
            replaced.add(blockLocation2);
            Location blockLocation3 = new Location(pLocation.getWorld(), pLocation.getBlockX()+1, pLocation.getBlockY(), pLocation.getBlockZ()-1);
            replaced.add(blockLocation3);

            Location blockLocation4 = new Location(pLocation.getWorld(), pLocation.getBlockX()-1, pLocation.getBlockY()+1, pLocation.getBlockZ()-1);
            replaced.add(blockLocation4);
            Location blockLocation5 = new Location(pLocation.getWorld(), pLocation.getBlockX(), pLocation.getBlockY()+1, pLocation.getBlockZ()-1);
            replaced.add(blockLocation5);
            Location blockLocation6 = new Location(pLocation.getWorld(), pLocation.getBlockX()+1, pLocation.getBlockY()+1, pLocation.getBlockZ()-1);
            replaced.add(blockLocation6);
        }

        for (Location l : replaced) {
            if (l.getBlock().getType().equals(Material.AIR)) {
                l.getBlock().setType(Material.GLASS);
            } else {

            }
        }

        replacedBlocks.put(p, replaced);
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent e) {
        if (gameStatus.getStatus().equals(GameStatus.Status.IN_GAME)) {
            Player p = e.getEntity();
            UHCTeam pTeam;
            if (teamManager.playerHasTeam(p.getUniqueId())) {
                pTeam = teamManager.getTeamFromOwner(p.getUniqueId());
                for (UUID player : pTeam.getMembers()) {
                    Bukkit.getPlayer(player).sendMessage(Messaging.Tag.ALERT.getTag() + "Your teammate " + p.getName() + " has died, bummer.");
                }
                if (pTeam.getTeamSize() > 1) {
                    pTeam.setOwner(pTeam.getMembers().get(0));
                    pTeam.removeMember(pTeam.getMembers().get(0));
                } else {
                    teamManager.deleteTeamFromOwner(p.getUniqueId());
                }
            } else if (teamManager.playerIsMemberOfTeam(p.getUniqueId())) {
                pTeam = teamManager.getTeamFromMember(p.getUniqueId());
                Bukkit.getPlayer(pTeam.getOwner()).sendMessage(Messaging.Tag.ALERT.getTag() + "Your teammate " + p.getName() + " has died, bummer.");
                for (UUID player : pTeam.getMembers()) {
                    Bukkit.getPlayer(player).sendMessage(Messaging.Tag.ALERT.getTag() + "Your teammate " + p.getName() + " has died, bummer.");
                }
                pTeam.removeMember(p.getUniqueId());
            } else {
                p.sendMessage(Messaging.Tag.ERROR.getTag() + "Apparently you did not have a team, please alert the staff that there was an issue.");
            }
            gameStatus.getGameManager().removeInGame(p.getUniqueId());

            if (teamManager.getTeams().size() <= 1) {
                gameStatus.getGameManager().endGame();
            }

            if (scenarioManager.getCurrentScenario().equals(ScenarioManager.Scenario.DIAMONDLESS)) {
                ItemStack diamond = new ItemStack(Material.DIAMOND, 1);
                Bukkit.getWorld("uhc-map").dropItem(p.getLocation(), diamond);

                ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwner(p.getName());
                meta.setDisplayName(ChatColor.GREEN + p.getName() + "'s Head!");
                skull.setItemMeta(meta);

                Bukkit.getWorld("uhc-map").dropItem(p.getLocation(), skull);
            }
        }
    }

    @EventHandler
    public void playerSpawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        if (gameSettings.getLobbySpawn() != null) {
            e.setRespawnLocation(gameSettings.getLobbySpawn());
        }

        gameStatus.getGameManager().getPlayersInGame().forEach(inGameP -> {
            if (Bukkit.getPlayer(inGameP) != null) {
                Bukkit.getPlayer(inGameP).hidePlayer(p);
            }
        });

        p.sendMessage(Messaging.Tag.IMPORTANT.getTag() + "You have died and will be teleported back to the hub shortly.");

        new BukkitRunnable() {

            @Override
            public void run() {
                p.sendMessage(Messaging.Tag.IMPORTANT.getTag() + "Sending you the hub-1.");
                UHCCore.sendToServer(p, "HUB-1");
            }
        }.runTaskLater(uhcCore, 100L);
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        Location blockLocation = e.getBlock().getLocation();

        if (blockLocation.getBlockX() == gameStatus.getBorderSize() || blockLocation.getBlockZ() == gameStatus.getBorderSize()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void entityKilled(EntityDeathEvent e) {

        if (e.getEntity().getLocation().getWorld().getName().equals("uhc_map")) {
            if (scenarioManager.getCurrentScenario().equals(ScenarioManager.Scenario.CUTCLEAN)) {
                for (ItemStack item : e.getDrops()) {
                    if (item.getType().equals(Material.RAW_BEEF)) {
                        item.setType(Material.COOKED_BEEF);
                    } else if (item.getType().equals(Material.RAW_CHICKEN)) {
                        item.setType(Material.COOKED_CHICKEN);
                    } else if (item.getType().equals(Material.RAW_FISH)) {
                        item.setType(Material.COOKED_FISH);
                    } else if (item.getType().equals(Material.PORK)) {
                        item.setType(Material.GRILLED_PORK);
                    }
                }
                if (e.getEntity().getType().equals(EntityType.CHICKEN)) {
                    for (ItemStack item : e.getDrops()) {
                        if (item.getType().equals(Material.FEATHER)) {
                            return;
                        }
                    }
                    e.getDrops().add(new ItemStack(Material.FEATHER, 1));
                }
            }
        }
    }

    @EventHandler
    public void healthRegen(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player && e.getEntity().getWorld().getName().equals("uhc_map") && e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.REGEN)) {
            e.setCancelled(true);
        }
    }
}
