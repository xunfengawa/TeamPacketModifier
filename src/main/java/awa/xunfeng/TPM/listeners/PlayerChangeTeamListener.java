package awa.xunfeng.TPM.listeners;

import awa.xunfeng.TPM.team.TeamManager;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static awa.xunfeng.TPM.TeamPacketModifier.enabled;
import static awa.xunfeng.TPM.packets.PacketHandler.*;
import static awa.xunfeng.TPM.team.TeamManager.refreshTeamMap;

public class PlayerChangeTeamListener implements Listener {
    private static final Map<UUID, Team> oldTeamMap = new HashMap<>();
    public static boolean hasChangeInTick = false;
    @EventHandler
    public void onTickStart(ServerTickStartEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Team oldTeam = TeamManager.findTeamByPlayerUUID(player.getUniqueId());
            oldTeamMap.put(player.getUniqueId(), oldTeam);
        }
        if (hasChangeInTick) {
            refresh();
        }
        hasChangeInTick = false;
    }
    @EventHandler
    public void onTickEnd(ServerTickEndEvent event) {
        if (!enabled) return;
        refreshTeamMap();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Team oldTeam = oldTeamMap.get(player.getUniqueId());
            Team newTeam = TeamManager.findTeamByPlayerUUID(player.getUniqueId());
            if (oldTeam == null && newTeam == null) continue;
            else if (oldTeam == null || newTeam == null) hasChangeInTick = true;
            else if (!oldTeam.getName().equals(newTeam.getName())) hasChangeInTick = true;
        }
    }
}
