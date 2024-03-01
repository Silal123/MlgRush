package mlgrush;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.imageio.stream.IIOByteBuffer;
import java.util.*;

public class TeamManager {

    public static Map<UUID, Team> teams = new HashMap<>();
    public static Map<Team, Integer> scores = new HashMap<>();
    private static String TEAM_SEPARATOR = "§8 » §f";
    public static void init() {
        reset();
        reload();
        for (Player p : Bukkit.getOnlinePlayers()) {

        }
    }

    public static Team getPlayerTeam(Player p) {
        if (teams.containsKey(p.getUniqueId())) return teams.get(p.getUniqueId());
        return Team.SPECTATOR;
    }

    public static void setPlayerTeam(Player p, Team t) {
        if (t == null) return;
        if (teams.containsKey(p.getUniqueId())) teams.remove(p.getUniqueId());
        teams.put(p.getUniqueId(), t);
    }

    public static List<UUID> getPlayersOfTeam(Team team) {
        List<UUID> re = new ArrayList<>();
        for (Map.Entry<UUID, Team> entry : teams.entrySet()) {
            if (!entry.getValue().equals(team)) continue;
            re.add(entry.getKey());
        }
        return re;
    }

    public static Team getOpponent(Team team) {
        if (team == null) return null;
        if (team.equals(Team.RED)) return Team.BLUE;
        if (team.equals(Team.BLUE)) return Team.RED;
        return null;
    }

    public static int getScore(Team team) {
        if (team == null) return 0;
        if (!scores.containsKey(team)) return 0;
        return scores.get(team);
    }

    public static void setScore(Team team, int score) {
        if (team == null) return;
        scores.remove(team);
        scores.put(team, score);
    }

    public static void reload() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

            scoreboard.getTeams().forEach(team -> team.unregister());

            for (Team t : Team.values()) {
                org.bukkit.scoreboard.Team created_t = scoreboard.registerNewTeam(t.teamName());
                created_t.prefix(Component.text(t.color() + t.teamName() + TEAM_SEPARATOR));
                created_t.setColor(ChatColor.getByChar('f'));
            }

            for (Player this_p : Bukkit.getOnlinePlayers()) {
                scoreboard.getTeam(getPlayerTeam(this_p).teamName()).addPlayer(this_p);
            }

            Objective o = scoreboard.registerNewObjective("sidebar", "dummy", "§6" + p.getName().toUpperCase());
            o.setDisplaySlot(DisplaySlot.SIDEBAR);

            Team blue = Team.BLUE;
            Team red = Team.RED;

            int score = 9;
            if (MlgRushGame.isPlayerInGame(p)) {
                o.getScore("§7Status: §2Im Spiel").setScore(score);
                score--;
                o.getScore("§7Dein Team: " + TeamManager.getPlayerTeam(p).color() + TeamManager.getPlayerTeam(p).teamName()).setScore(score);
                score--;
            } else {
                if (MlgRushGame.queue.contains(p.getUniqueId())) {
                    o.getScore("§7Status: §eWarten").setScore(score);
                    score--;
                } else {
                    o.getScore("§7Status: §fSpectating").setScore(score);
                    score--;
                }
            }

            o.getScore(" ").setScore(score);
            score--;
            o.getScore(blue.color() + blue.teamName() + "§7: " + TeamManager.getScore(blue)).setScore(score);
            score--;
            o.getScore(red.color() + red.teamName() + "§7: " + TeamManager.getScore(red)).setScore(score);

            p.setScoreboard(scoreboard);
        }
    }
    public static void reset() {
        scores.clear();
        teams.clear();
        reload();
    }

    public static Team getTeamByMaterial(Material material) {
        if (material == Material.RED_BED) return Team.RED;
        if (material == Material.BLUE_BED) return Team.BLUE;
        return null;
    }

    public enum Team {
        BLUE("Blue", "1", new Location(Bukkit.getWorld("world"), 0, 104, -18)),
        RED("Red", "4", new Location(Bukkit.getWorld("world"), 0, 104, 18)),
        SPECTATOR("Zuschauer", "7", new Location(Bukkit.getWorld("world"), 0, 100, 0));

        Team(String name, String color, Location spawn) {
            this.name = name;
            this.spawn = spawn;
            this.color = color;
        }
        private String name;
        private String color;
        public Location spawn;

        public String teamName() { return name; }
        public Location spawnLocation() { return spawn.setDirection(MlgRush.spectator_spawn.getDirection()); }
        public String color() { return "§" + color; }
    }
}
