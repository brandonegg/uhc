package net.sail.uhc.utils;

import net.sail.uhc.settings.GameSettings;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by brand on 1/31/2016.
 */
public class GameUtils {

    private final GameSettings gameSettings;

    public GameUtils(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    public void setupForUHC(Player p) {
        p.setHealth(20);
        p.setTotalExperience(0);
        p.setLevel(0);
        p.setFoodLevel(20);
        p.setGameMode(GameMode.SURVIVAL);
        p.setFlying(false);
        p.getInventory().clear();
    }

    public void setupForLobby(Player p) {
        teleportToSpawn(p);
        p.setGameMode(GameMode.ADVENTURE);
        p.setFlying(false);
        p.setHealth(20);
        p.setExp(0);
        p.setFoodLevel(20);
        p.setTotalExperience(0);
        p.setLevel(0);
        p.setWalkSpeed((float).2);
        p.getInventory().clear();
    }

    public void teleportToSpawn(Player p) {
        if (gameSettings.getLobbySpawn() != null) {
            p.teleport(gameSettings.getLobbySpawn());
            for (ItemStack item : p.getInventory().getContents()) {
                p.getInventory().remove(item);
            }
        }
    }
}
