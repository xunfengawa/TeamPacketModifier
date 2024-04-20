package awa.xunfeng.TPM.command;

import awa.xunfeng.TPM.TeamPacketModifier;
import awa.xunfeng.TPM.config.TPMConfig;
import awa.xunfeng.TPM.packets.EntityPosePacketHandle;
import awa.xunfeng.TPM.packets.PacketHandler;
import awa.xunfeng.TPM.team.TeamManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static awa.xunfeng.TPM.TeamPacketModifier.enabled;
import static awa.xunfeng.TPM.packets.PacketHandler.entityPosePacketHandleMap;

public class TPMCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            if (args[0].equals("reload")) {
                TPMConfig.load();
                PacketHandler.refresh();
                sender.sendMessage("[§bTPM§r] 已重载");
                return true;
            }
            else if (args[0].equals("enable")) {
                if (enabled) {
                    sender.sendMessage("[§bTPM§r] 无变化,TPM功能已启用");
                    return true;
                }
                TeamPacketModifier.enable();
                sender.sendMessage("[§bTPM§r] 已启用TPM功能");
                return true;
            }
            else if (args[0].equals("disable")) {
                if (!enabled) {
                    sender.sendMessage("[§bTPM§r] 无变化,TPM功能已禁用");
                    return true;
                }
                TeamPacketModifier.disable();
                sender.sendMessage("[§bTPM§r] 已禁用TPM功能");
                return true;
            }
            else if (args[0].equals("status")) {
                sender.sendMessage("[§bTPM§r] TPM状态:");
                if (!enabled) {
                    sender.sendMessage("TPM功能已禁用");
                    return true;
                }
                else {
                    sender.sendMessage("TPM功能已启用");
                    sender.sendMessage("[§bTPM§r] GlowHandlers: ");
                    entityPosePacketHandleMap().forEach((entityIdLs, packetHandle) -> {
                        Player player1 = Bukkit.getPlayer(entityIdLs.get(0));
                        Player player2 = Bukkit.getPlayer(entityIdLs.get(1));
                        Team team1 = TeamManager.findTeamByPlayerUUID(entityIdLs.get(0));
                        Team team2 = TeamManager.findTeamByPlayerUUID(entityIdLs.get(1));
                        if (packetHandle.getGlowing() != EntityPosePacketHandle.EntityPoseHandleType.IGNORE) {
                            if (player1 == null || player2 == null || team1 == null || team2 == null) {
                                if (player1 == null || player2 == null)
                                    sender.sendMessage(
                                            Component.text(packetHandle.getGlowing().toString() + ": ")
                                                    .append(Component.text(entityIdLs.get(0).toString() + " -> " + entityIdLs.get(1).toString())));
                                else
                                    sender.sendMessage(
                                            Component.text(packetHandle.getInvisible().toString() + ": ")
                                                    .append(Component.text(player1.getName()))
                                                    .append(Component.text(" -> "))
                                                    .append(Component.text(player2.getName())));
                            }
                            else
                                sender.sendMessage(
                                        Component.text(packetHandle.getGlowing().toString() + ": ")
                                                .append(Component.text(player1.getName()).color(team1.color()))
                                                .append(Component.text(" -> "))
                                                .append(Component.text(player2.getName()).color(team2.color())));
                        }
                    });
                    sender.sendMessage("[§bTPM§r] InvisHandlers: ");
                    entityPosePacketHandleMap().forEach((entityIdLs, packetHandle) -> {
                        Player player1 = Bukkit.getPlayer(entityIdLs.get(0));
                        Player player2 = Bukkit.getPlayer(entityIdLs.get(1));
                        Team team1 = TeamManager.findTeamByPlayerUUID(entityIdLs.get(0));
                        Team team2 = TeamManager.findTeamByPlayerUUID(entityIdLs.get(1));
                        if (packetHandle.getInvisible() != EntityPosePacketHandle.EntityPoseHandleType.IGNORE) {
                            if (player1 == null || player2 == null || team1 == null || team2 == null) {
                                if (player1 == null || player2 == null)
                                    sender.sendMessage(
                                            Component.text(packetHandle.getInvisible().toString() + ": ")
                                                    .append(Component.text(entityIdLs.get(0).toString() + " -> " + entityIdLs.get(1).toString())));
                                else
                                    sender.sendMessage(
                                            Component.text(packetHandle.getInvisible().toString() + ": ")
                                                    .append(Component.text(player1.getName()))
                                                    .append(Component.text(" -> "))
                                                    .append(Component.text(player2.getName())));
                            }
                            else
                                sender.sendMessage(
                                        Component.text(packetHandle.getInvisible().toString() + ": ")
                                                .append(Component.text(player1.getName()).color(team1.color()))
                                                .append(Component.text(" -> "))
                                                .append(Component.text(player2.getName()).color(team2.color())));
                        }
                    });
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> list =  Arrays.asList(
                    "reload",
                    "enable",
                    "disable",
                    "status"
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
