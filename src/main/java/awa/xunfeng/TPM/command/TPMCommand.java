package awa.xunfeng.TPM.command;

import awa.xunfeng.TPM.TeamPacketModifier;
import awa.xunfeng.TPM.config.TPMConfig;
import awa.xunfeng.TPM.packets.PacketHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static awa.xunfeng.TPM.TeamPacketModifier.enabled;
import static awa.xunfeng.TPM.packets.PacketHandler.cancelPacketHandle;
import static awa.xunfeng.TPM.team.TeamManager.refreshTeamMap;

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
            else if (args[0].equals("enable")) {
                if (enabled) {
                    sender.sendMessage("[§bTeamGlow§r] 无变化,TPM功能已启用");
                    return true;
                }
                TeamPacketModifier.enable();
                sender.sendMessage("[§bTeamGlow§r] 已启用TPM功能");
                return true;
            }
            else if (args[0].equals("disable")) {
                if (!enabled) {
                    sender.sendMessage("[§bTeamGlow§r] 无变化,TPM功能已禁用");
                    return true;
                }
                TeamPacketModifier.disable();
                sender.sendMessage("[§bTeamGlow§r] 已禁用TPM功能");
                return true;
            }
            else if (args[0].equals("RefreshTeamMap")) {
                refreshTeamMap();
                sender.sendMessage("[§bTeamGlow§r] 已重载队伍");
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> list =  Arrays.asList(
                    "reload",
                    "StopAllGlow",
                    "enable",
                    "disable",
                    "RefreshTeamMap"
            );
            return list.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                    .toList();
        }
        else {
            return new ArrayList<>();
        }
    }
}
