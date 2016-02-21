package net.sail.uhc.listener;

import net.sail.uhc.manager.ScenarioManager;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by brand on 1/10/2016.
 */
public class PlayerBreakBlock implements Listener {

    private final ScenarioManager scenarioManager;
    private final GameSettings gameSettings;
    private final GameStatus gameStatus;

    public PlayerBreakBlock(ScenarioManager scenarioManager, GameSettings gameSettings, GameStatus gameStatus) {
        this.scenarioManager = scenarioManager;
        this.gameSettings = gameSettings;
        this.gameStatus = gameStatus;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void BlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();

//        Bukkit.getLogger().info("ItemBreak");
//        Bukkit.getLogger().info(e.getBlock().toString());
//        Bukkit.getLogger().info(p.getLocation().getWorld().getName());
//        Bukkit.getLogger().info(scenarioManager.getCurrentScenario().getName());

        if (!e.getBlock().getLocation().getWorld().getName().equals("uhc_map") && !p.hasPermission(Permissions.GAME_ADMIN.getPermission())) {
            e.setCancelled(true);
        }

        if (p.getLocation().getWorld().getName().equals("uhc_map")) {
            if (gameSettings.isHost(p.getUniqueId()) || gameStatus.isSpectator(p.getUniqueId())) {
                e.setCancelled(true);
            }
            if (scenarioManager.getCurrentScenario().equals(ScenarioManager.Scenario.CUTCLEAN)) {
                if (getSmeltedType(e.getBlock().getType(), p.getItemInHand().getType()) != null) {
                    p.getWorld().dropItemNaturally(e.getBlock().getLocation(),new ItemStack(getSmeltedType(e.getBlock().getType(), p.getItemInHand().getType())));
                    e.getBlock().setType(Material.AIR);
                }
                if (e.getBlock().getType().equals(Material.GRAVEL)) {
                    e.getBlock().setType(Material.AIR);
                    p.getWorld().dropItemNaturally(e.getBlock().getLocation(),new ItemStack(Material.FLINT));
                }
            } else if (scenarioManager.getCurrentScenario().equals(ScenarioManager.Scenario.DIAMONDLESS)) {
                if (e.getBlock().getType().equals(Material.DIAMOND_ORE)) {
                    e.getBlock().setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    public void placeBlockEvent(BlockPlaceEvent e) {
        Player p = e.getPlayer();

        if (e.getBlockPlaced().getLocation().getWorld().getName().equals("uhc_map")) {

        }
    }

    public Material getSmeltedType(Material type, Material itemInHand) {
        if (type.equals(Material.IRON_ORE)) {
            if (itemInHand.equals(Material.DIAMOND_PICKAXE) || itemInHand.equals(Material.GOLD_PICKAXE) || itemInHand.equals(Material.IRON_PICKAXE) || itemInHand.equals(Material.STONE_PICKAXE)) {
                return Material.IRON_INGOT;
            } else {
                return null;
            }
        } else if (type.equals(Material.GOLD_ORE)) {
            if (itemInHand.equals(Material.DIAMOND_PICKAXE) || itemInHand.equals(Material.GOLD_PICKAXE) || itemInHand.equals(Material.IRON_PICKAXE)) {
                return Material.GOLD_INGOT;
            } else {
                return null;
            }
        } else if (type.equals(Material.COAL_ORE)) {
            if (itemInHand.equals(Material.DIAMOND_PICKAXE) || itemInHand.equals(Material.GOLD_PICKAXE) || itemInHand.equals(Material.IRON_PICKAXE) || itemInHand.equals(Material.STONE_PICKAXE) || itemInHand.equals(Material.WOOD_PICKAXE)) {
                return Material.COAL;
            } else {
                return null;
            }
        }
        return null;
    }

}
