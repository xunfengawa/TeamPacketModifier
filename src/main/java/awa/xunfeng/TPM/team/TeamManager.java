package awa.xunfeng.TPM.team;

import awa.xunfeng.TPM.config.TPMConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

import static awa.xunfeng.TPM.TeamPacketModifier.scoreboard;

public class TeamManager {
    public static Map<Team, List<UUID>> oldTeamMap = new LinkedHashMap<>();
    public static Map<Team, List<UUID>> teamMap = new LinkedHashMap<>();
    public static void refreshTeamMap() {
        oldTeamMap.putAll(teamMap);
        teamMap.clear();
        for (Team team : scoreboard.getTeams()) {
            if (!team.hasColor() || TPMConfig.getIgnoreTeamList().contains(team.color())) continue;
            List<UUID> uuidLs = new ArrayList<>();
            for (String entry : team.getEntries()) {
                uuidLs.add(Bukkit.getOfflinePlayer(entry).getUniqueId());
            }
            teamMap.put(team,uuidLs);
        }
        if (oldTeamMap.isEmpty()) oldTeamMap.putAll(teamMap);
    }
    public static Team findTeamByPlayerUUID(UUID uuid) {
        for (Team team : teamMap.keySet()) {
            if (teamMap.get(team).contains(uuid)) return team;
        }
        return null;
    }

    public static Team findOldTeamByPlayerUUID(UUID uuid) {
        for (Team team : oldTeamMap.keySet()) {
            if (oldTeamMap.get(team).contains(uuid)) return team;
        }
        return null;
    }

    public static List<UUID> getAllGlowPlayerUUID() {
        List<UUID> ls = new ArrayList<>();
        teamMap.forEach((team, uuidLs) -> {
            if (TPMConfig.getGlowTeamList().contains(team.color())) {
                ls.addAll(uuidLs);
            }
        });
        return ls;
    }

    public static List<UUID> getAllSpecPlayerUUID() {
        List<UUID> ls = new ArrayList<>();
        teamMap.forEach((team, uuidLs) -> {
            if (TPMConfig.getSeeAllGlowTeamList().contains(team.color())) {
                ls.addAll(uuidLs);
            }
        });
        return ls;
    }
}
