package awa.xunfeng.TPM.packets;

import awa.xunfeng.TPM.TeamPacketModifier;
import awa.xunfeng.TPM.config.TPMConfig;
import awa.xunfeng.TPM.team.TeamManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nullable;
import java.util.*;

import static awa.xunfeng.TPM.TeamPacketModifier.getIngameConfig;
import static awa.xunfeng.TPM.packets.PacketHandler.*;
import static awa.xunfeng.TPM.team.TeamManager.*;
import static awa.xunfeng.TPM.team.TeamManager.findTeamByPlayerUUID;

public class PacketListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        refreshTeamMap();
        updateTeamGlow(null, findTeamByPlayerUUID(event.getPlayer().getUniqueId()), event.getPlayer().getUniqueId());
    }

//    // 指令触发
    @EventHandler
    public void onPlayerTeamCommand(PlayerCommandPreprocessEvent event) {
//        System.out.println("PlayerCommandPreprocessEvent: " + event.getMessage());
        String command = event.getMessage();
        applyChangesOnCommand(command);
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
//        System.out.println("ServerCommandEvent: " + event.getCommand());
        String command = event.getCommand();
        applyChangesOnCommand(command);
    }

    private void applyChangesOnCommand(String command) {
        if (command.startsWith("/team ") || command.startsWith("/minecraft:team ")
                || command.startsWith("team ") || command.startsWith("minecraft:team ")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    teamUpdate();
                }
            }.runTaskLater(TeamPacketModifier.getInstance(),1);
        }
        else if ((command.startsWith("/scoreboard players ") || command.startsWith("/minecraft:scoreboard players "))
                || (command.startsWith("scoreboard players ") || command.startsWith("minecraft:scoreboard players "))
                && command.contains("TPM")) {
            if (command.contains("Glow")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        configGlowUpdate();
                    }
                }.runTaskLater(TeamPacketModifier.getInstance(),1);
            }
            else if (command.contains("CancelSelfInvis")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        configInvisUpdate();
                    }
                }.runTaskLater(TeamPacketModifier.getInstance(),1);
            }
        }
    }

    private void teamUpdate() {
        refreshTeamMap();
        if (oldTeamMap.equals(teamMap)) return;
        List<UUID> updatePlayerLs = new ArrayList<>();
        for (Team team : teamMap.keySet()) {
            List<UUID> curPlayerLs = new ArrayList<>(teamMap.get(team));
            List<UUID> oldPlayerLs = new ArrayList<>();
            if (oldTeamMap.containsKey(team))
                oldPlayerLs = new ArrayList<>(oldTeamMap.get(team));
            List<UUID> sameLs = new ArrayList<>(curPlayerLs);
            sameLs.retainAll(oldPlayerLs);
            curPlayerLs.removeAll(sameLs);
            oldPlayerLs.removeAll(sameLs);
            updatePlayerLs.addAll(curPlayerLs);
            updatePlayerLs.addAll(oldPlayerLs);
        }
        for (UUID updateUUID : updatePlayerLs) {
            Team oldTeam = findOldTeamByPlayerUUID(updateUUID);
            Team curTeam = findTeamByPlayerUUID(updateUUID);
            updateTeamGlow(oldTeam, curTeam, updateUUID);
        }
    }

    /**
     * 单参赛队伍队内发光
     */
    public static void updateTeamGlow(@Nullable Team oldTeam, @Nullable Team curTeam, UUID pUpdateUUID) {
        //原队伍处理
        if (oldTeam != null && TeamManager.teamMap.containsKey(oldTeam)) {
            if (TPMConfig.getGlowTeamList().contains(oldTeam.color())) {  //原为玩家队
                for (UUID uuid : TeamManager.teamMap.get(oldTeam)) {
                    //以前的队伍，移除该玩家对各玩家的发光
                    setPacketHandle(
                            pUpdateUUID,
                            uuid,
                            EntityData.GLOWING,
                            EntityPosePacketHandle.EntityPoseHandleType.IGNORE
                    );
                    //以前的队伍，移除各玩家对该玩家的发光
                    setPacketHandle(
                            uuid,
                            pUpdateUUID,
                            EntityData.GLOWING,
                            EntityPosePacketHandle.EntityPoseHandleType.IGNORE
                    );
                }
            } else if (TPMConfig.getSeeAllGlowTeamList().contains(oldTeam.color())) {  //原为旁观队
                for (UUID uuidGlow : getAllGlowPlayerUUID()) {
                    //取消所有玩家对该玩家发光
                    setPacketHandle(
                            uuidGlow,
                            pUpdateUUID,
                            EntityData.GLOWING,
                            EntityPosePacketHandle.EntityPoseHandleType.IGNORE
                    );
                }
                for (UUID uuid : getAllSpecPlayerUUID()) {
                    //添加该玩家对所有旁观玩家发光
                    setPacketHandle(
                            pUpdateUUID,
                            uuid,
                            EntityData.GLOWING,
                            EntityPosePacketHandle.EntityPoseHandleType.TRUE
                    );
                }
            }
        }
        //新队伍处理
        if (curTeam != null && TeamManager.oldTeamMap.containsKey(curTeam)) {
            if (TPMConfig.getGlowTeamList().contains(curTeam.color())) {  //换入玩家队
                for (UUID uuid : TeamManager.oldTeamMap.get(curTeam)) {
                    //现在的队伍，添加该玩家对各玩家的发光
                    setPacketHandle(
                            pUpdateUUID,
                            uuid,
                            EntityData.GLOWING,
                            EntityPosePacketHandle.EntityPoseHandleType.TRUE
                    );
                    //现在的队伍，添加各玩家对该玩家的发光
                    setPacketHandle(
                            uuid,
                            pUpdateUUID,
                            EntityData.GLOWING,
                            EntityPosePacketHandle.EntityPoseHandleType.TRUE
                    );
                }
            }
            else if (TPMConfig.getSeeAllGlowTeamList().contains(curTeam.color())) {  //换入旁观队
                for (UUID uuid : getAllGlowPlayerUUID()) {
                    //添加所有玩家对该玩家发光
                    setPacketHandle(
                            uuid,
                            pUpdateUUID,
                            EntityData.GLOWING,
                            EntityPosePacketHandle.EntityPoseHandleType.TRUE
                    );
                }
                for (UUID uuid : getAllSpecPlayerUUID()) {
                    //取消该玩家对所有旁观玩家发光
                    setPacketHandle(
                            pUpdateUUID,
                            uuid,
                            EntityData.GLOWING,
                            EntityPosePacketHandle.EntityPoseHandleType.IGNORE
                    );
                }
            }
        }
    }

    private void configGlowUpdate() {
        stopTeamGlowAll();
        stopSpecTeamGlowAll();
        if (getIngameConfig("Glow")) {
            startTeamGlowAll();
            startSpecTeamGlowAll();
        }
    }

    private void configInvisUpdate() {
        stopCancelSelfInvisAll();
        if (getIngameConfig("CancelSelfInvis"))
            startCancelSelfInvisAll();
    }

}
