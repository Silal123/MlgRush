package mlgrush;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class MlgRushGame {

    public static List<UUID> queue = new ArrayList<>();
    public static Map<Block, Integer> blocks = new HashMap<>();
    public static GameState gameState = GameState.NO_GAME;
    public static int red_score = 0;
    public static int blue_score = 0;

    public static void secScheduler() {
        if (blocks.isEmpty()) return;
        Map<Block, Integer> savedBlocks = new HashMap<>(blocks);
        for (Map.Entry<Block, Integer> entry : savedBlocks.entrySet()) {
            if (entry.getKey().getType() == Material.AIR) {
                blocks.remove(entry.getKey());
                continue;
            }

            if (entry.getValue() == 0) {
                entry.getKey().setType(Material.AIR);
                blocks.remove(entry.getKey());
                continue;
            }

            int newTime = entry.getValue() - 1;
            blocks.remove(entry.getKey());
            blocks.put(entry.getKey(), newTime);
        }
    }
    public static void queuePlayer(Player p) {
        if (isPlayerInGame(p)) {
            p.sendMessage(MlgRush.PREFIX + "Du befindest dich §aBereits§7 in einem Spiel!");
            return;
        }

        if (queue.contains(p.getUniqueId())) return;
        queue.add(p.getUniqueId());

        for (UUID uuid : queue) {
            if (uuid.equals(p.getUniqueId())) continue;
            Player this_p = Bukkit.getPlayer(uuid);
            if (this_p == null) {
                queue.remove(uuid);
                continue;
            }

            this_p.sendMessage(MlgRush.PREFIX + "§a" + p.getName() + "§7 hat die Queue §abetreten§7!");
        }

        if (queue.size() < 2) return;

        tryStartGame();
    }

    public static void removeAllBlocks() {
        for (Map.Entry<Block, Integer> entry : blocks.entrySet()) {
            entry.getKey().setType(Material.AIR);
        }
        blocks.clear();
    }

    public static void unQueuePlayer(Player p) {
        if (isPlayerInGame(p)) {
            p.sendMessage(MlgRush.PREFIX + "Du befindest dich §aBereits§7 in einem Spiel!");
            return;
        }
        if (!queue.contains(p.getUniqueId())) return;
        queue.remove(p.getUniqueId());

        for (UUID uuid : queue) {
            if (uuid.equals(p.getUniqueId())) continue;
            Player this_p = Bukkit.getPlayer(uuid);
            if (this_p == null) {
                queue.remove(uuid);
                continue;
            }

            this_p.sendMessage(MlgRush.PREFIX + "§a" + p.getName() + "§7 hat die Queue §cverlassen§7!");
        }
    }

    public static boolean isGameRunning() {
        return gameState == GameState.INGAME;
    }

    public static boolean isPlayerInGame(Player p) {
        if (gameState == GameState.NO_GAME) return false;
        return TeamManager.getPlayersOfTeam(TeamManager.Team.BLUE).contains(p.getUniqueId()) || TeamManager.getPlayersOfTeam(TeamManager.Team.RED).contains(p.getUniqueId());
    }

    public static void giveItemsToPlayer(Player p) {
        Inventory inventory = p.getInventory();
        inventory.clear();

        ItemStack knockBackStick = new ItemStack(Material.STICK, 1);
        ItemMeta knockBackStickMeta = knockBackStick.getItemMeta();
        knockBackStickMeta.setDisplayName("§aKnockback Stick");
        knockBackStickMeta.addEnchant(Enchantment.KNOCKBACK, 2, false);
        knockBackStickMeta.setUnbreakable(true);
        knockBackStick.setItemMeta(knockBackStickMeta);

        inventory.setItem(0, knockBackStick);

        ItemStack pickaxe = new ItemStack(Material.STONE_PICKAXE, 1);
        ItemMeta pickaxeMeta = pickaxe.getItemMeta();
        pickaxeMeta.setDisplayName("§aPickaxe");
        pickaxeMeta.addEnchant(Enchantment.DIG_SPEED, 5, false);
        pickaxeMeta.setUnbreakable(true);
        pickaxe.setItemMeta(pickaxeMeta);

        inventory.setItem(1, pickaxe);

        ItemStack sandstone = new ItemStack(Material.SANDSTONE, 64);
        ItemMeta sandstoneMeta = sandstone.getItemMeta();
        sandstoneMeta.setDisplayName("§aSandstone");
        sandstone.setItemMeta(sandstoneMeta);

        inventory.setItem(2, sandstone);
        inventory.setItem(3, sandstone);
    }

    public static void clearAllInventory() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.getInventory().clear();
        }
    }

    public static void handleBeadBreak(Player destroyer, TeamManager.Team destroyed) {
        if (destroyed == null) return;
        TeamManager.Team destroyer_team = TeamManager.getPlayerTeam(destroyer);
        if (destroyed.equals(destroyer_team)) {
            destroyer.sendMessage(MlgRush.PREFIX + "Du kannst dein Bett §cNicht§7 zerstören!");
            return;
        }

        clearAllInventory();

        for (UUID uuid : TeamManager.getPlayersOfTeam(destroyed)) {
            Player team_player = Bukkit.getPlayer(uuid);
            if (team_player == null) continue;

            team_player.playSound(team_player, Sound.ENTITY_WITCH_DEATH, 0.75f, 1);
            team_player.sendTitle(destroyed.color() + String.valueOf(TeamManager.getScore(destroyed)) + "§7:" + destroyer_team.color() + String.valueOf(TeamManager.getScore(destroyer_team) + 1), "§7Dein Bett wurde §czerstört", 10, 30, 15);
            team_player.teleport(destroyed.spawnLocation());
            giveItemsToPlayer(team_player);
        }

        if (destroyer_team == null) return;

        TeamManager.setScore(destroyer_team, TeamManager.getScore(destroyer_team) + 1);

        if (TeamManager.getScore(destroyer_team) == 5) {
            finalizeGame(destroyer_team);
        }

        for (UUID uuid : TeamManager.getPlayersOfTeam(destroyer_team)) {
            Player team_player = Bukkit.getPlayer(uuid);
            if (team_player == null) continue;

            team_player.playSound(team_player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.75f, 1);
            team_player.sendTitle(destroyer_team.color() + String.valueOf(TeamManager.getScore(destroyer_team)) + "§7:" + destroyed.color() + String.valueOf(TeamManager.getScore(destroyed)) , "§7Du hast das bett deines Gegners zerstört!", 10, 30, 15);
            team_player.teleport(destroyer_team.spawnLocation());
            giveItemsToPlayer(team_player);
        }
        TeamManager.reload();
        removeAllBlocks();
    }

    public static void tryStartGame() {
        if (isGameRunning()) return;
        if (queue.size() < 2) return;

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setGameMode(GameMode.SPECTATOR);
        }

        Player p1 = Bukkit.getPlayer(queue.get(0));
        if (p1 == null) {
            queue.remove(queue.get(0));
            return;
        }
        unQueuePlayer(p1);

        Player p2 = Bukkit.getPlayer(queue.get(0));
        if (p2 == null) {
            queue.remove(queue.get(0));
            return;
        }
        unQueuePlayer(p2);

        TeamManager.Team p1Team = TeamManager.Team.BLUE;
        TeamManager.Team p2Team = TeamManager.Team.RED;

        TeamManager.reset();
        TeamManager.setPlayerTeam(p1, p1Team);
        TeamManager.setPlayerTeam(p2, p2Team);
        TeamManager.reload();

        gameState = GameState.INGAME;

        p1.setGameMode(GameMode.SURVIVAL);
        p2.setGameMode(GameMode.SURVIVAL);

        clearAllInventory();

        giveItemsToPlayer(p1);
        giveItemsToPlayer(p2);

        p1.setHealth(p1.getMaxHealth());
        p1.setFoodLevel(20);

        p2.setHealth(p2.getMaxHealth());
        p2.setFoodLevel(20);

        p1.teleport(p1Team.spawnLocation());
        p2.teleport(p2Team.spawnLocation());
        removeAllBlocks();
    }

    public static void updateGamemodes() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isPlayerInGame(p)) { p.setGameMode(GameMode.SURVIVAL); return; }
            p.setGameMode(GameMode.SPECTATOR);
        }
    }
    public static void finalizeGame(TeamManager.Team winner) {
        TeamManager.Team looser = TeamManager.getOpponent(winner);
        for (UUID uuid : TeamManager.getPlayersOfTeam(winner)) {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;

            p.playSound(p, Sound.ENTITY_ENDER_DRAGON_DEATH, 0.75f, 1);
            p.sendTitle("§6Gewonnen!", "§7Du hast das Spiel " + winner.color() + TeamManager.getScore(winner) + "§7:" + looser.color() + TeamManager.getScore(looser) + "§7 gewonnen!", 10, 30, 15);
        }

        for (UUID uuid : TeamManager.getPlayersOfTeam(looser)) {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) continue;

            p.playSound(p, Sound.ENTITY_ENDER_DRAGON_DEATH, 0.75f, 1);
            p.sendTitle("§cVerloren!", "§7Das gegner team hat " + winner.color() + TeamManager.getScore(winner) + "§7:" + looser.color() + TeamManager.getScore(looser) + "§7 gewonnen!", 10, 30, 15);
        }

        resetGame();
        removeAllBlocks();
        tryStartGame();
    }

    public static void killPlayer(Player p) {
        TeamManager.Team playerTeam = TeamManager.getPlayerTeam(p);
        if (playerTeam == null) {
            p.teleport(MlgRush.spectator_spawn);
            return;
        }

        p.teleport(playerTeam.spawnLocation());
        if (!MlgRushGame.isPlayerInGame(p)) return;

        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(MlgRush.PREFIX + playerTeam.color() + p.getName() + "§7 ist gestorben!");
        }

        giveItemsToPlayer(p);
    }

    public static void resetGame() {
        gameState = GameState.NO_GAME;

        clearAllInventory();
        TeamManager.reset();
        removeAllBlocks();
        resetMap();
    }

    private static void resetMap() {
        try {
            Location spawn = new Location(Bukkit.getWorld("world"), 0, 100, 0);

            Clipboard clipboard;
            World world = spawn.getWorld();
            com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);
            File schematic = new File(MlgRush.dataFolder + File.separator + "mlg.schem");

            if (!schematic.exists()) return; //TODO

            ClipboardFormat format = ClipboardFormats.findByFile(schematic);
            try {
                ClipboardReader reader = format.getReader(new FileInputStream(schematic));
                clipboard = reader.read();
            } catch (IOException e) { throw new RuntimeException(e); }

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()))
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
            } catch (Exception e) { e.printStackTrace(); }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public enum GameState {
        INGAME, NO_GAME
    }
}
