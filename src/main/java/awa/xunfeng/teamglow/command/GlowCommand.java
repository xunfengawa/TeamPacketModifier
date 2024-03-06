package awa.xunfeng.teamglow.command;

import awa.xunfeng.teamglow.config.TeamGlowConfig;
import awa.xunfeng.teamglow.glow.GlowingHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static awa.xunfeng.teamglow.config.TeamGlowConfig.setEnabled;

public class GlowCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            if (args[0].equals("on")) {
                setEnabled(true);
                TeamGlowConfig.load();
                GlowingHandler.refresh();
                sender.sendMessage("[§bTeamGlow§r] 已启用");
                return true;
            }
            else if (args[0].equals("off")) {
                setEnabled(false);
                TeamGlowConfig.load();
                GlowingHandler.stopAllGlows();
                sender.sendMessage("[§bTeamGlow§r] 已禁用");
                return true;
            }
            else if (args[0].equals("reload")) {
                TeamGlowConfig.load();
                GlowingHandler.refresh();
                sender.sendMessage("[§bTeamGlow§r] 已重载");
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList(
                    "on",
                    "off",
                    "reload"
            );
        }
        else {
            return new ArrayList<>();
        }
    }
}
