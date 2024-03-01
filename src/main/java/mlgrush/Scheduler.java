package mlgrush;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Scheduler {

    private final static int ONE_TICK = 1;
    private final static int ONE_SECOND = 20 * ONE_TICK;
    private final static int ONE_MINUTE = 60 * ONE_SECOND;

    public static void start() {
        start1TickScheduler();
        start1SecondScheduler();
        start10SecondScheduler();
        start30SecondScheduler();
        start45SecondScheduler();
        start1MinuteScheduler();
    }

    public static void stop() {
        Bukkit.getScheduler().cancelTask(start1TickScheduler);
        Bukkit.getScheduler().cancelTask(start1SecondScheduler);
        Bukkit.getScheduler().cancelTask(start10SecondScheduler);
        Bukkit.getScheduler().cancelTask(start30SecondScheduler);
        Bukkit.getScheduler().cancelTask(start45SecondScheduler);
        Bukkit.getScheduler().cancelTask(start1MinuteScheduler);
    }

    private static int start1TickScheduler;
    private static int start1SecondScheduler;
    private static int start10SecondScheduler;
    private static int start30SecondScheduler;
    private static int start45SecondScheduler;
    private static int start1MinuteScheduler;

    private static void start1TickScheduler() {
        start1TickScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(MlgRush.instance, () -> {
            MlgRushGame.updateGamemodes();
            //
        }, 0, ONE_TICK);
    }

    private static void start1SecondScheduler() {
        start1SecondScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(MlgRush.instance, () -> {
            MlgRushGame.secScheduler();
            //
        }, 0, ONE_SECOND);
    }

    private static void start10SecondScheduler() {
        start10SecondScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(MlgRush.instance, () -> {
            //
        }, 0, 10 * ONE_SECOND);
    }
    public static void start30SecondScheduler() {
        start30SecondScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(MlgRush.instance, () -> {
            //
        }, 0, 30 * ONE_SECOND);
    }
    private static void start45SecondScheduler() {
        start45SecondScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(MlgRush.instance, () -> {
            //
        }, 0, 45 * ONE_SECOND);
    }

    private static void start1MinuteScheduler() {
        start1MinuteScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(MlgRush.instance, () -> {
            //
        }, 0, ONE_MINUTE);
    }
    private static void start10MinuteScheduler() {
        start1MinuteScheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(MlgRush.instance, () -> {
            //
        }, 0, ONE_MINUTE * 10);
    }
}
