package awa.xunfeng.TPM;

import awa.xunfeng.TPM.command.TPMCommand;
import awa.xunfeng.TPM.config.TPMConfig;
import awa.xunfeng.TPM.listeners.PlayerChangeTeamListener;
import awa.xunfeng.TPM.packets.PacketHandler;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class TeamPacketModifier extends JavaPlugin implements Listener {
    private static TeamPacketModifier INSTANCE;
    public static Scoreboard scoreboard;
    public static ProtocolManager protocolManager;
    public static boolean enabled = false;
    public static PlayerChangeTeamListener playerChangeTeamListener;
    public static TeamPacketModifier getInstance() {
        return INSTANCE;
    }

    public static boolean getIngameConfig(String option) {
        if (scoreboard.getObjective("TPM") == null) return false;
        return (Objects.requireNonNull(scoreboard.getObjective("TPM")).getScore(option).getScore() == 1);
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        playerChangeTeamListener = new PlayerChangeTeamListener();
        TPMConfig.load();
        scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
        protocolManager = ProtocolLibrary.getProtocolManager();
        enable();

        TPMCommand TPMCommand = new TPMCommand();
        Objects.requireNonNull(Bukkit.getPluginCommand("TPM")).setExecutor(TPMCommand);
    }

    public static void enable() {
        if (enabled) return;
        enabled = true;
        PacketHandler.init();
        Bukkit.getPluginManager().registerEvents(playerChangeTeamListener, TeamPacketModifier.getInstance());
    }

    public static void disable() {
        if (!enabled) return;
        enabled = false;
        PacketHandler.disable();
        HandlerList.unregisterAll(playerChangeTeamListener);
    }
}