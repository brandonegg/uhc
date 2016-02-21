package net.sail.uhc.listener;

import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.GameStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Created by brand on 1/10/2016.
 */
public class PlayerDamagedEvent implements Listener {

    private final TeamManager teamManager;
    private final GameStatus gameStatus;
    private final GameSettings gameSettings;

    public PlayerDamagedEvent(TeamManager teamManager, GameStatus gameStatus, GameSettings gameSettings) {
        this.teamManager = teamManager;
        this.gameStatus = gameStatus;
        this.gameSettings = gameSettings;
    }

    @EventHandler
    public void PlayerDamaged(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player damager = (Player)e.getDamager();
            Player entity = (Player)e.getEntity();

            if (gameStatus.getStatus().equals(GameStatus.Status.COUNTDOWN_UNTIL_LOBBY) || gameStatus.getStatus().equals(GameStatus.Status.IN_LOBBY)
                    || gameStatus.getStatus().equals(GameStatus.Status.LOBBY_COUNTDOWN) || gameStatus.getStatus().equals(GameStatus.Status.STARTING)) {
                e.setCancelled(true);
            } else if (gameStatus.getStatus().equals(GameStatus.Status.IN_GAME)) {
                if (gameSettings.isHost(damager.getUniqueId()) || gameStatus.isSpectator(damager.getUniqueId())) {
                    e.setCancelled(true);
                    return;
                }
                if (gameStatus.getGameManager().inPeacefulTime()) {
                    e.setCancelled(true);
                    return;
                }
                if (teamManager.getTeamFromMember(damager.getUniqueId()).getName().equals(teamManager.getTeamFromMember(entity.getUniqueId()).getName())) {
                    e.setCancelled(true);
                }
                if (entity.getHealth() == 0) {
                    gameStatus.getGameManager().setTopKills(damager, 1);
                }
            }
        }
    }

    @EventHandler
    public void playerHurt(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && !e.getEntity().getLocation().getWorld().getName().equals("uhc_map")) {
            e.setCancelled(true);
        }
    }

}
