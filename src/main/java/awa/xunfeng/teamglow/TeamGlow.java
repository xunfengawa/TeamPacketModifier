package awa.xunfeng.teamglow;

import awa.xunfeng.command.GlowCommand;
import awa.xunfeng.config.TeamGlowConfig;
import awa.xunfeng.glow.GlowingHandler;
import awa.xunfeng.glow.GlowingListener;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
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
        scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
        protocolManager = ProtocolLibrary.getProtocolManager();
        TeamGlowConfig.load();
        GlowingHandler.initOneWayGlowing();
        GlowingHandler.refresh();

        Bukkit.getPluginManager().registerEvents(new GlowingListener(), this);
        GlowCommand glowCommand = new GlowCommand();
        Objects.requireNonNull(Bukkit.getPluginCommand("teamglow")).setExecutor(glowCommand);
    }
    public static Map<TextColor, List<UUID>> getTeamMap() {
        Map<TextColor, List<UUID>> map = new LinkedHashMap<>();
        for (Team team : scoreboard.getTeams()) {
            TextColor color = team.color();
            System.out.println(color);
        }
        return map;
    }
}