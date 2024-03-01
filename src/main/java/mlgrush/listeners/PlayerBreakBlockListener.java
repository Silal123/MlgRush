package mlgrush.listeners;

import mlgrush.MlgRush;
import mlgrush.MlgRushGame;
import mlgrush.TeamManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerBreakBlockListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();

        if (!MlgRushGame.isPlayerInGame(p)) {
            e.setCancelled(true);
            return;
        }

        if (e.getBlock().getType().equals(Material.SANDSTONE)) {
            MlgRushGame.blocks.remove(e.getBlock());
            return;
        }

        if (e.getBlock().getType().name().endsWith("_BED")) {
            e.setCancelled(true);

            TeamManager.Team team = TeamManager.getTeamByMaterial(e.getBlock().getType());
            if (team == null) return;
            MlgRushGame.handleBeadBreak(p, team);
            return;
        }

        if (!MlgRushGame.blocks.containsKey(e.getBlock())) {
            e.setCancelled(true);
            return;
        }

        MlgRushGame.blocks.remove(e.getBlock());
    }
}
