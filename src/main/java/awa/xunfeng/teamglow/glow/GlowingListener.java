package awa.xunfeng.teamglow.glow;

import awa.xunfeng.teamglow.TeamGlow;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

import static awa.xunfeng.teamglow.TeamGlow.protocolManager;
import static awa.xunfeng.teamglow.config.TeamGlowConfig.isEnabled;
import static awa.xunfeng.teamglow.glow.GlowingHandler.sendGlowPacket;

public class GlowingListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!isEnabled()) return;
        Player p = event.getPlayer();
        for (List<OfflinePlayer> playerLs : GlowingHandler.getOneWayGlowSet()) {
            OfflinePlayer playerGlow = playerLs.get(0);
            OfflinePlayer playerSee = playerLs.get(1);
            if (playerGlow.isOnline() && playerSee.isOnline() && !playerGlow.equals(playerSee)
                && (p.getUniqueId().equals(playerGlow.getUniqueId()) || p.getUniqueId().equals(playerSee.getUniqueId()))) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        sendGlowPacket(
                                protocolManager,
                                Objects.requireNonNull(Bukkit.getPlayer(playerGlow.getUniqueId())),
                                Objects.requireNonNull(Bukkit.getPlayer(playerSee.getUniqueId())),
                                true);
                        System.out.println(playerGlow.getName()+"->"+playerSee.getName());
                    }
                }.runTaskLater(TeamGlow.getInstance(),10);
            }
        }
    }

    @EventHandler
    public void onPlayerTeamCommand(PlayerCommandPreprocessEvent event) {
        if (!isEnabled()) return;
        String command = event.getMessage();
        if (command.startsWith("/team ")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    GlowingHandler.refresh();
                }
            }.runTaskLater(TeamGlow.getInstance(),1);
        }
    }

    @EventHandler
    public void onServerTeamCommand(ServerCommandEvent event) {
        if (!isEnabled()) return;
        String command = event.getCommand();
        if (command.startsWith("/team ")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    GlowingHandler.refresh();
                }
            }.runTaskLater(TeamGlow.getInstance(),1);
        }
    }
}
