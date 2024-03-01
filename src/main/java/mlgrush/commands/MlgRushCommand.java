package mlgrush.commands;

import mlgrush.MlgRush;
import mlgrush.MlgRushGame;
import mlgrush.TeamManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MlgRushCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player p)) return false;

        if (strings.length < 1) {
            sendHelp(p);
            return true;
        }

        String option = strings[0];

        if (option.equals("join")) {
            MlgRushGame.queuePlayer(p);
            p.sendMessage(MlgRush.PREFIX + "Du befindest dich nun in der §aQueue§7!");
        }

        if (option.equals("leave")) {
            MlgRushGame.unQueuePlayer(p);
            p.sendMessage(MlgRush.PREFIX + "Du befindest dich nun nicht mehr in der §cQueue§7!");
        }

        if (option.equals("reload")) {
            MlgRushGame.resetGame();
            TeamManager.reset();
            p.sendMessage(MlgRush.PREFIX + "Mlr Rush wurde §areloaded§7!");
        }

        TeamManager.reload();
        return true;
    }

    public void sendHelp(Player p) {
        p.sendMessage("");
        p.sendMessage(MlgRush.PREFIX + "§a§lHelp:");
        p.sendMessage(MlgRush.PREFIX + "/mlg join §8-§7 join the queue!");
        p.sendMessage(MlgRush.PREFIX + "/mlg leave §8-§7 leave the queue!");
        p.sendMessage("");
    }
}
