package awa.xunfeng.TPM.packets;

import awa.xunfeng.TPM.TeamPacketModifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static awa.xunfeng.TPM.TeamPacketModifier.getIngameConfig;
import static awa.xunfeng.TPM.TeamPacketModifier.protocolManager;
import static awa.xunfeng.TPM.packets.PacketHandler.updateTeamGlow;
import static awa.xunfeng.TPM.team.TeamManager.*;

public class PacketListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        for (List<UUID> uuidLs : PacketHandler.getOneWayPacketHandleMap().keySet()) {
            UUID uuidGlow = uuidLs.get(0);
            UUID uuidSee = uuidLs.get(1);
            if(!p.getUniqueId().equals(uuidGlow) && !p.getUniqueId().equals(uuidSee)) {
                if (p.isInvisible() && getIngameConfig("CancelSelfInvis"))
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ManualPacket.sendManualPacket(protocolManager,p,p,p.isGlowing(),false);
                        }
                    }.runTaskLater(TeamPacketModifier.getInstance(),1);
                continue;
            }
            Player playerGlow = Bukkit.getPlayer(uuidGlow);
            Player playerSee = Bukkit.getPlayer(uuidSee);
            Boolean shouldGlow = PacketHandler.getOneWayPacketHandleMap().get(uuidLs).get(0);
            Boolean shouldInvis = PacketHandler.getOneWayPacketHandleMap().get(uuidLs).get(1);
            if (playerGlow != null && playerSee != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ManualPacket.sendManualPacket(protocolManager,playerGlow,playerSee,shouldGlow,shouldInvis);
                    }
                }.runTaskLater(TeamPacketModifier.getInstance(),1);
            }
        }
    }

    @EventHandler
    public void onPlayerTeamCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        applyChangesOnCommand(command);
    }

    @EventHandler
    public void onServerTeamCommand(ServerCommandEvent event) {
        String command = event.getCommand();
        applyChangesOnCommand(command);
    }

    private void applyChangesOnCommand(String command) {
        if (command.startsWith("/team ")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    teamUpdate();
                }
            }.runTaskLater(TeamPacketModifier.getInstance(),1);
        }
        else if (command.contains("TPM")) {
            if (command.startsWith("/scoreboard players ") && command.contains("Glow")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        configGlowUpdate();
                    }
                }.runTaskLater(TeamPacketModifier.getInstance(),1);
            }
            else if (command.startsWith("/scoreboard players ") && command.contains("CancelSelfInvis")) {
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
            System.out.println(updatePlayerLs);
        }
        for (UUID updateUUID : updatePlayerLs) {
            Team oldTeam = findOldTeamByPlayerUUID(updateUUID);
            Team curTeam = findTeamByPlayerUUID(updateUUID);
            updateTeamGlow(oldTeam, curTeam, updateUUID);
        }
    }

    private void configGlowUpdate() {
        for (List<UUID> uuids : PacketHandler.getOneWayPacketHandleMap().keySet()) {
            Player playerModified = Bukkit.getPlayer(uuids.get(0));
            Player playerSee = Bukkit.getPlayer(uuids.get(1));
            if (playerModified != null && playerSee != null) {
                if (PacketHandler.getOneWayPacketHandleMap().get(uuids).get(0)) {
                    ManualPacket.sendManualPacket(
                            protocolManager,
                            playerModified,
                            playerSee,
                            playerModified.isGlowing() || getIngameConfig("Glow"),
                            (playerModified.isInvisible() && !getIngameConfig("CancelSelfInvis")));
                }
            }
        }
    }

    private void configInvisUpdate() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ManualPacket.sendManualPacket(
                    protocolManager,
                    p,
                    p,
                    p.isGlowing(),
                    (p.isInvisible() && !getIngameConfig("CancelSelfInvis")));
        }
    }

}
