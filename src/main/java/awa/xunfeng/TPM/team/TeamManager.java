package awa.xunfeng.TPM.team;

import awa.xunfeng.TPM.config.TPMConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

import static awa.xunfeng.TPM.TeamPacketModifier.scoreboard;

public class TeamManager {
    public static Map<Team, List<UUID>> teamMap = new LinkedHashMap<>();
    public static void refreshTeamMap() {
        teamMap.clear();
        for (Team team : scoreboard.getTeams()) {
            if (!team.hasColor()) team.color(NamedTextColor.WHITE);
            List<UUID> uuidLs = new ArrayList<>();
            for (String entry : team.getEntries()) {
                uuidLs.add(Bukkit.getOfflinePlayer(entry).getUniqueId());
            }
            teamMap.put(team,uuidLs);
        }
    }
    public static Team findTeamByPlayerUUID(UUID uuid) {
        for (Team team : teamMap.keySet()) {
            if (teamMap.get(team).contains(uuid)) return team;
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
