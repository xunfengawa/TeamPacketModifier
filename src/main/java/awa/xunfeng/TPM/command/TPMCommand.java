package awa.xunfeng.TPM.command;

import awa.xunfeng.TPM.config.TPMConfig;
import awa.xunfeng.TPM.packets.PacketHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TPMCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            if (args[0].equals("reload")) {
                TPMConfig.load();
                PacketHandler.refresh();
                sender.sendMessage("[§bTeamGlow§r] 已重载");
                return true;
            }
            else if (args[0].equals("stopAllGlow")) {
                PacketHandler.stopTeamGlowAll();
                PacketHandler.stopSpecTeamGlowAll();
                sender.sendMessage("[§bTeamGlow§r] 已停止发光");
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList(
                    "reload",
                    "stopAllGlow"
            );
        }
        else {
            return new ArrayList<>();
        }
    }
}
