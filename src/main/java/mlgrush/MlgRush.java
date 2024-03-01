package mlgrush;

import mlgrush.commands.MlgCommandComplete;
import mlgrush.commands.MlgRushCommand;
import mlgrush.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MlgRush extends JavaPlugin {

    public static MlgRush instance;
    public static String dataFolder;
    public static String PREFIX = "§aM§2L§aG§8 » §7";
    public static Location spectator_spawn;
    @Override
    public void onEnable() {
        instance = this;
        dataFolder = getDataFolder().getPath();
        spectator_spawn = new Location(Bukkit.getWorld("world"), 0, 100, 0);

        if (!new File(dataFolder).exists()) new File(dataFolder).mkdir();

        TeamManager.init();
        MlgRushGame.resetGame();

        getCommand("mlg").setExecutor(new MlgRushCommand());
        getCommand("mlg").setTabCompleter(new MlgCommandComplete());

        PluginManager man = getServer().getPluginManager();
        man.registerEvents(new PlayerBreakBlockListener(), this);
        man.registerEvents(new PlayerPlaceBlockListener(), this);
        man.registerEvents(new PlayerJoinListener(), this);
        man.registerEvents(new PlayerLeaveListener(), this);
        man.registerEvents(new PlayerDeathListener(), this);
        man.registerEvents(new PlayerMoveListener(), this);
        man.registerEvents(new PlayerTakeDamageListener(), this);
        man.registerEvents(new PlayerFoodLevelChangeListener(), this);

        Scheduler.start();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
