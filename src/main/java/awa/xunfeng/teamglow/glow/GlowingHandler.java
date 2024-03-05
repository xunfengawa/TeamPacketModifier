package awa.xunfeng.teamglow.glow;

import awa.xunfeng.teamglow.TeamGlow;
import awa.xunfeng.teamglow.config.TeamGlowConfig;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.List;

import static awa.xunfeng.teamglow.TeamGlow.protocolManager;
import static awa.xunfeng.teamglow.TeamGlow.scoreboard;

public class GlowingHandler extends PacketAdapter{
    private static final Set<List<UUID>> oneWayGlowSet = new HashSet<>();
    private static boolean isTeamGlowing = false;
    private static final PacketAdapter packetAdapter = new PacketAdapter(TeamGlow.getInstance(), PacketType.Play.Server.ENTITY_METADATA)
    {
        public void onPacketSending(PacketEvent event) {
            if (!event.getPacket().getType().equals(PacketType.Play.Server.ENTITY_METADATA)) return;
            PacketContainer packet = event.getPacket().deepClone();
            Player receiver = event.getPlayer();
            Entity glowingEntity = packet.getEntityModifier(receiver.getWorld()).readSafely(0);
            if (!(glowingEntity instanceof Player)) return;
            List<UUID> playerLs = getUUIDLs(glowingEntity.getUniqueId(),receiver.getUniqueId());
            if (oneWayGlowSet.contains(playerLs)) {
                List<WrappedDataValue> metadata = packet.getDataValueCollectionModifier().read(0);
                WrappedDataValue bitMaskContainer = metadata.stream().filter(obj -> (obj.getIndex() == 0)).findAny().orElse(null);
                if (bitMaskContainer != null) {
                    Byte flags = (Byte) bitMaskContainer.getValue();
                    bitMaskContainer.setValue((byte)(flags | (byte)64));
                    event.setPacket(packet);
                }
            }
        }
    };
    public GlowingHandler(Plugin arg0, ListenerPriority arg1, PacketType... arg2) {
        super(arg0, arg1, arg2);
    }

    public static Set<List<UUID>> getOneWayGlowSet() {
        return oneWayGlowSet;
    }
    public static void initOneWayGlowing() {
        if (!protocolManager.getPacketListeners().contains(packetAdapter)) {
            protocolManager.addPacketListener(packetAdapter);
        }
    }
    public static List<UUID> getUUIDLs(UUID uuid1, UUID uuid2) {
        List<UUID> ls = new ArrayList<>();
        ls.add(uuid1);
        ls.add(uuid2);
        return ls;
    }
    /**
     * 让一个玩家对另一个玩家单独发光
     * @param playerGlowUUID 被发光的玩家的UUID
     * @param playerSeeUUID 看见发光的玩家的UUID
     */
    public static void addOneWayGlow(@Nonnull UUID playerGlowUUID, @Nonnull UUID playerSeeUUID) {
        oneWayGlowSet.add(getUUIDLs(playerGlowUUID,playerSeeUUID));
        Player playerGlow = Bukkit.getPlayer(playerGlowUUID);
        Player playerSee = Bukkit.getPlayer(playerSeeUUID);
        if (playerGlow!=null && playerSee!=null) sendGlowPacket(protocolManager,playerGlow,playerSee,true);
    }
    /**
     * 取消一个玩家对另一个玩家的发光(无法取消已启用的队内发光)
     * @param playerGlowUUID 被发光的玩家的UUID
     * @param playerSeeUUID 看见发光的玩家的UUID
     */
    public static void removeOneWayGlow(@Nonnull UUID playerGlowUUID, @Nonnull UUID playerSeeUUID) {
        if (isTeamGlowing && scoreboard.getPlayerTeam(Bukkit.getOfflinePlayer(playerGlowUUID)) == scoreboard.getPlayerTeam(Bukkit.getOfflinePlayer(playerSeeUUID))) return;
        oneWayGlowSet.remove(getUUIDLs(playerGlowUUID,playerSeeUUID));
        Player playerGlow = Bukkit.getPlayer(playerGlowUUID);
        Player playerSee = Bukkit.getPlayer(playerSeeUUID);
        if (playerGlow!=null && playerSee!=null) sendGlowPacket(protocolManager,playerGlow,playerSee,false);
    }
    /**
     * 全部参赛队伍队内发光
     */
    public static void startAllGlows() {
        isTeamGlowing = true;
        TeamGlowConfig.getGlowTeamList().forEach(textColor -> {
            if(!TeamGlowConfig.getSeeAllGlowTeamList().contains(textColor)) {
                for (UUID uuidGlow : TeamGlow.getTeamMap().get(textColor)) {
                    for (UUID uuidSee : TeamGlow.getTeamMap().get(textColor)) {
                        if(uuidGlow != uuidSee) {
                            addOneWayGlow(
                                    Objects.requireNonNull(uuidGlow),
                                    Objects.requireNonNull(uuidSee)
                            );
                        }
                    }
                }
            }
        });
        TeamGlowConfig.getSeeAllGlowTeamList().forEach(textColor -> {
            TeamGlowConfig.getGlowTeamList().forEach(textColorGlow -> {
                for (UUID uuidGlow : TeamGlow.getTeamMap().get(textColorGlow)) {
                    for (UUID uuidSee : TeamGlow.getTeamMap().get(textColor)) {
                        if (uuidGlow != uuidSee) {
                            addOneWayGlow(
                                    Objects.requireNonNull(uuidGlow),
                                    Objects.requireNonNull(uuidSee)
                            );
                        }
                    }
                }
            });
        });
    }
    /**
     * 全部参赛队伍停止队内发光
     */
    public static void stopAllGlows() {
        oneWayGlowSet.clear();
    }
    static void sendGlowPacket(ProtocolManager protocolManager, @Nonnull Player playerGlow, @Nonnull Player playerSee, boolean shouldGlow) {
        PacketContainer glowPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        glowPacket.getIntegers().write(0, playerGlow.getEntityId());
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
        watcher.setEntity(playerGlow);
        //逻辑部分
        byte entityByte = getPlayerByte(playerGlow, shouldGlow);
        watcher.setObject(0, byteSerializer, entityByte);
        //发送部分
        final List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
        watcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
            final WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
            wrappedDataValueList.add(new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
        });
        glowPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        protocolManager.sendServerPacket(playerSee, glowPacket);
    }
    public static byte getPlayerByte(Player playerGlow, boolean shouldGlow) {
        boolean[] data = {
                playerGlow.isVisualFire(), playerGlow.isSneaking(), false, playerGlow.isSprinting(),
                playerGlow.isSwimming(), playerGlow.isInvisible(), shouldGlow, playerGlow.isGliding()
        };
        byte entityByte = 0;
        for (int i = 0 ; i < 8; i++) {
            if (data[i]) {
                entityByte |= (1 << i);
            }
        }
        return entityByte;
    }

    public static void refresh() {
        oneWayGlowSet.clear();
        startAllGlows();
    }
}
