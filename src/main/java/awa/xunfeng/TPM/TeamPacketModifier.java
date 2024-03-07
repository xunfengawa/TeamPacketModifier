package awa.xunfeng.TPM;

import awa.xunfeng.TPM.command.TPMCommand;
import awa.xunfeng.TPM.config.TPMConfig;
import awa.xunfeng.TPM.packets.PacketHandler;
import awa.xunfeng.TPM.packets.PacketListener;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class TeamPacketModifier extends JavaPlugin implements Listener {
    private static TeamPacketModifier INSTANCE;
    public static Scoreboard scoreboard;
    public static ProtocolManager protocolManager;
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
        TPMConfig.load();
        if (isEnabled()) {
            scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
            protocolManager = ProtocolLibrary.getProtocolManager();
            PacketHandler.init(this);
            PacketHandler.refresh();

            Bukkit.getPluginManager().registerEvents(new PacketListener(), this);
            TPMCommand TPMCommand = new TPMCommand();
            Objects.requireNonNull(Bukkit.getPluginCommand("TPM")).setExecutor(TPMCommand);
        }
    }
}