package mlgrush.listeners;

import mlgrush.MlgRushGame;
import mlgrush.TeamManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.http.WebSocket;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        MlgRushGame.unQueuePlayer(p);

        if (!MlgRushGame.isPlayerInGame(p)) {
            return;
        }

        MlgRushGame.finalizeGame(TeamManager.getOpponent(TeamManager.getPlayerTeam(p)));
        TeamManager.reload();
    }
}
