package awa.xunfeng.teamglow.glow;

import awa.xunfeng.teamglow.TeamGlow;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

import static awa.xunfeng.teamglow.TeamGlow.protocolManager;
import static awa.xunfeng.teamglow.glow.GlowingHandler.sendGlowPacket;

public class GlowingListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        for (List<UUID> uuidLs : GlowingHandler.getOneWayGlowSet()) {
            UUID uuidGlow = uuidLs.get(0);
            UUID uuidSee = uuidLs.get(1);
            if(!p.getUniqueId().equals(uuidGlow) && !p.getUniqueId().equals(uuidSee)) continue;
            Player playerGlow = Bukkit.getPlayer(uuidGlow);
            Player playerSee = Bukkit.getPlayer(uuidSee);
            if (playerGlow != null && playerSee != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        sendGlowPacket(protocolManager,playerGlow,playerSee,true);
                    }
                }.runTaskLater(TeamGlow.getInstance(),1);
            }
        }
    }

    @EventHandler
    public void onPlayerTeamCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage();
        if (command.startsWith("/team ")) {
            GlowingHandler.refresh();
            System.out.println("refreshed");
        }
    }
}
