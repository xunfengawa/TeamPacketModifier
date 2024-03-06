package awa.xunfeng.teamglow;

import awa.xunfeng.teamglow.command.GlowCommand;
import awa.xunfeng.teamglow.config.TeamGlowConfig;
import awa.xunfeng.teamglow.glow.GlowingHandler;
import awa.xunfeng.teamglow.glow.GlowingListener;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TeamGlow extends JavaPlugin implements Listener {
    private static TeamGlow INSTANCE;
    public static Scoreboard scoreboard;
    public static ProtocolManager protocolManager;
    public static TeamGlow getInstance() {
        return INSTANCE;
    }
    @Override
    public void onEnable() {
        INSTANCE = this;
        TeamGlowConfig.load();
        if (isEnabled()) {
            scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
            protocolManager = ProtocolLibrary.getProtocolManager();
            GlowingHandler.initOneWayGlowing();
            GlowingHandler.refresh();

            Bukkit.getPluginManager().registerEvents(new GlowingListener(), this);
            GlowCommand glowCommand = new GlowCommand();
            Objects.requireNonNull(Bukkit.getPluginCommand("teamglow")).setExecutor(glowCommand);
        }
    }
    public static Map<TextColor, Set<OfflinePlayer>> getTeamMap() {
        Map<TextColor, Set<OfflinePlayer>> map = new LinkedHashMap<>();
        for (Team team : scoreboard.getTeams()) {
            TextColor color;
            if (!team.hasColor()) team.setColor(ChatColor.WHITE);
            color = team.color();
            Set<OfflinePlayer> entrySet = team.getPlayers();
            map.put(color,entrySet);
        }
        return map;
    }
}