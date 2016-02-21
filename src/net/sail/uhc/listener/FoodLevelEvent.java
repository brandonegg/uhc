package net.sail.uhc.listener;

import net.sail.uhc.utils.GameStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Created by brand on 12/30/2015.
 */
public class FoodLevelEvent implements Listener {

    private final GameStatus gameStatus;

    public FoodLevelEvent(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    @EventHandler
    public void foodChangeEvent(FoodLevelChangeEvent e) {
        if (gameStatus.getStatus().equals(GameStatus.Status.IN_LOBBY)
                || gameStatus.getStatus().equals(GameStatus.Status.LOBBY_COUNTDOWN)
                || gameStatus.getStatus().equals(GameStatus.Status.STARTING)
                || gameStatus.getStatus().equals(GameStatus.Status.COUNTDOWN_UNTIL_LOBBY)) {
            e.setCancelled(true);
        }
    }

}
