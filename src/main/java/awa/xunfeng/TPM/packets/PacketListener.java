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

import java.util.List;
import java.util.UUID;

import static awa.xunfeng.TPM.TeamPacketModifier.protocolManager;

public class PacketListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        for (List<UUID> uuidLs : PacketHandler.getOneWayPacketHandleMap().keySet()) {
            UUID uuidGlow = uuidLs.get(0);
            UUID uuidSee = uuidLs.get(1);
            if(!p.getUniqueId().equals(uuidGlow) && !p.getUniqueId().equals(uuidSee)) continue;
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
        if (command.startsWith("/team ") || command.startsWith("/scoreboard ")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    PacketHandler.refresh();
                }
            }.runTaskLater(TeamPacketModifier.getInstance(),1);
        }
    }

    @EventHandler
    public void onServerTeamCommand(ServerCommandEvent event) {
        String command = event.getCommand();
        if (command.startsWith("/team ") || command.startsWith("/scoreboard ")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    PacketHandler.refresh();
                }
            }.runTaskLater(TeamPacketModifier.getInstance(),1);
        }
    }
}
