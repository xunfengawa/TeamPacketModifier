package awa.xunfeng.TPM.team;

import awa.xunfeng.TPM.config.TPMConfig;
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
            List<UUID> uuidLs = new ArrayList<>();
            for (String entry : team.getEntries()) {
                uuidLs.add(Bukkit.getOfflinePlayer(entry).getUniqueId());
            }
            teamMap.put(team,uuidLs);
        }
        if (oldTeamMap.size()==0) oldTeamMap.putAll(teamMap);
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
        for (Player p : Bukkit.getOnlinePlayers()) {
            Team pTeam = findTeamByPlayerUUID(p.getUniqueId());
            if (TPMConfig.getGlowTeamList().contains(pTeam)) {
                ls.add(p.getUniqueId());
            }
        }
        return ls;
    }
}
