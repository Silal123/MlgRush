package mlgrush.listeners;

import mlgrush.MlgRushGame;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerPlaceBlockListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();

        if (!MlgRushGame.isPlayerInGame(p)) {
            e.setCancelled(true);
            return;
        }

        MlgRushGame.blocks.put(e.getBlock(), 7);
    }
}
