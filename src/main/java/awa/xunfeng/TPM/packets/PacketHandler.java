package awa.xunfeng.TPM.packets;

import awa.xunfeng.TPM.TeamPacketModifier;
import awa.xunfeng.TPM.config.TPMConfig;
import awa.xunfeng.TPM.team.TeamManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

import static awa.xunfeng.TPM.TeamPacketModifier.getIngameConfig;
import static awa.xunfeng.TPM.TeamPacketModifier.protocolManager;
import static awa.xunfeng.TPM.packets.ManualPacket.getEntityPoseByte;
import static awa.xunfeng.TPM.packets.ManualPacket.sendManualPacket;
import static awa.xunfeng.TPM.team.TeamManager.refreshTeamMap;

public class PacketHandler extends PacketAdapter{
    private static final Map<List<UUID>,EntityPosePacketHandle> entityPosePacketHandleMap = new HashMap<>();
    private static final PacketAdapter packetAdapter = new PacketAdapter(TeamPacketModifier.getInstance(), PacketType.Play.Server.ENTITY_METADATA)
    {
        public void onPacketSending(PacketEvent event) {
//            System.out.println("onPacketSending: " + event.getPacket());
            if (!event.getPacket().getType().equals(PacketType.Play.Server.ENTITY_METADATA)) return;
            PacketContainer packet = event.getPacket().deepClone();
            Player receiver = event.getPlayer();
            LivingEntity entityModified;
            try {
                entityModified = (LivingEntity) packet.getEntityModifier(receiver.getWorld()).readSafely(0);
            } catch (Exception e) {
                return;
            }

            if (entityModified instanceof Player player && player.getGameMode() == GameMode.SPECTATOR) return;

            List<UUID> playerLs = Arrays.asList(entityModified.getUniqueId(),receiver.getUniqueId());
            List<WrappedDataValue> metadata = packet.getDataValueCollectionModifier().read(0);
            WrappedDataValue bitMaskContainer = metadata.stream().filter(obj -> (obj.getIndex() == 0)).findAny().orElse(null);
            if (entityPosePacketHandleMap.containsKey(playerLs)) {
                EntityPosePacketHandle handle = entityPosePacketHandleMap.get(playerLs);
                if (bitMaskContainer == null) {
                    bitMaskContainer = new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), getEntityPoseByte(entityModified));
                    setContainerBits(handle, bitMaskContainer);
                    metadata.add(bitMaskContainer);
                    packet.getDataValueCollectionModifier().write(0, metadata);
                }
                else
                    setContainerBits(handle, bitMaskContainer);
            }

            // 计分板接口
            if (TeamPacketModifier.getIngameConfig("CancelSelfInvis")
                    && entityModified.getUniqueId().equals(receiver.getUniqueId())) {
                if (bitMaskContainer == null) {
                    bitMaskContainer = new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), getEntityPoseByte(entityModified));
                    Byte flags = (Byte) bitMaskContainer.getValue();
                    bitMaskContainer.setValue(EntityData.INVISIBLE.unsetBit(flags));
                    metadata.add(bitMaskContainer);
                    packet.getDataValueCollectionModifier().write(0, metadata);
                }
                else {
                    Byte flags = (Byte) bitMaskContainer.getValue();
                    bitMaskContainer.setValue(EntityData.INVISIBLE.unsetBit(flags));
                }
            }

            event.setPacket(packet);
        }
    };

    static void setContainerBits(EntityPosePacketHandle handle, WrappedDataValue bitMaskContainer) {
        Byte flags = (Byte) bitMaskContainer.getValue();
        if (handle.getOnFire() == EntityPosePacketHandle.EntityPoseHandleType.TRUE) bitMaskContainer.setValue(EntityData.ON_FIRE.setBit(flags));
        if (handle.getOnFire() == EntityPosePacketHandle.EntityPoseHandleType.IGNORE)
            bitMaskContainer.setValue(handle.getEntityModified().isVisualFire() ? EntityData.ON_FIRE.setBit(flags) : EntityData.ON_FIRE.unsetBit(flags));
        if (handle.getOnFire() == EntityPosePacketHandle.EntityPoseHandleType.FALSE) bitMaskContainer.setValue(EntityData.ON_FIRE.unsetBit(flags));

        flags = (Byte) bitMaskContainer.getValue();
        if (handle.getCrouching() == EntityPosePacketHandle.EntityPoseHandleType.TRUE) bitMaskContainer.setValue(EntityData.CROUCHING.setBit(flags));
        if (handle.getCrouching() == EntityPosePacketHandle.EntityPoseHandleType.IGNORE)
            bitMaskContainer.setValue(handle.getEntityModified().isSneaking() ? EntityData.CROUCHING.setBit(flags) : EntityData.CROUCHING.unsetBit(flags));
        if (handle.getCrouching() == EntityPosePacketHandle.EntityPoseHandleType.FALSE) bitMaskContainer.setValue(EntityData.CROUCHING.unsetBit(flags));

        flags = (Byte) bitMaskContainer.getValue();
        if (handle.getPreviouslyRiding() == EntityPosePacketHandle.EntityPoseHandleType.TRUE) bitMaskContainer.setValue(EntityData.PREVIOUSLY_RIDING.setBit(flags));
        if (handle.getPreviouslyRiding() == EntityPosePacketHandle.EntityPoseHandleType.IGNORE) bitMaskContainer.setValue(EntityData.PREVIOUSLY_RIDING.unsetBit(flags));
        if (handle.getPreviouslyRiding() == EntityPosePacketHandle.EntityPoseHandleType.FALSE) bitMaskContainer.setValue(EntityData.PREVIOUSLY_RIDING.unsetBit(flags));

        flags = (Byte) bitMaskContainer.getValue();
        if (handle.getSprinting() == EntityPosePacketHandle.EntityPoseHandleType.TRUE) bitMaskContainer.setValue(EntityData.SPRINTING.setBit(flags));
        if (handle.getSprinting() == EntityPosePacketHandle.EntityPoseHandleType.IGNORE)
            bitMaskContainer.setValue(((Player) handle.getEntityModified()).isSprinting() ? EntityData.SPRINTING.setBit(flags) : EntityData.SPRINTING.unsetBit(flags));
        if (handle.getSprinting() == EntityPosePacketHandle.EntityPoseHandleType.FALSE) bitMaskContainer.setValue(EntityData.SPRINTING.unsetBit(flags));

        flags = (Byte) bitMaskContainer.getValue();
        if (handle.getSwimming() == EntityPosePacketHandle.EntityPoseHandleType.TRUE) bitMaskContainer.setValue(EntityData.SWIMMING.setBit(flags));
        if (handle.getSwimming() == EntityPosePacketHandle.EntityPoseHandleType.IGNORE)
            bitMaskContainer.setValue(handle.getEntityModified().isSwimming() ? EntityData.SWIMMING.setBit(flags) : EntityData.SWIMMING.unsetBit(flags));
        if (handle.getSwimming() == EntityPosePacketHandle.EntityPoseHandleType.FALSE) bitMaskContainer.setValue(EntityData.SWIMMING.unsetBit(flags));

        flags = (Byte) bitMaskContainer.getValue();
        if (handle.getInvisible() == EntityPosePacketHandle.EntityPoseHandleType.TRUE) bitMaskContainer.setValue(EntityData.INVISIBLE.setBit(flags));
        if (handle.getInvisible() == EntityPosePacketHandle.EntityPoseHandleType.IGNORE)
            bitMaskContainer.setValue(handle.getEntityModified().isInvisible() ? EntityData.INVISIBLE.setBit(flags) : EntityData.INVISIBLE.unsetBit(flags));
        if (handle.getInvisible() == EntityPosePacketHandle.EntityPoseHandleType.FALSE) bitMaskContainer.setValue(EntityData.INVISIBLE.unsetBit(flags));

        flags = (Byte) bitMaskContainer.getValue();
        if (handle.getGlowing() == EntityPosePacketHandle.EntityPoseHandleType.TRUE) bitMaskContainer.setValue(EntityData.GLOWING.setBit(flags));
        if (handle.getGlowing() == EntityPosePacketHandle.EntityPoseHandleType.IGNORE)
            bitMaskContainer.setValue(handle.getEntityModified().isGlowing() ? EntityData.GLOWING.setBit(flags) : EntityData.GLOWING.unsetBit(flags));
        if (handle.getGlowing() == EntityPosePacketHandle.EntityPoseHandleType.FALSE) bitMaskContainer.setValue(EntityData.GLOWING.unsetBit(flags));

        flags = (Byte) bitMaskContainer.getValue();
        if (handle.getGliding() == EntityPosePacketHandle.EntityPoseHandleType.TRUE) bitMaskContainer.setValue(EntityData.GLIDING.setBit(flags));
        if (handle.getGliding() == EntityPosePacketHandle.EntityPoseHandleType.IGNORE)
            bitMaskContainer.setValue(handle.getEntityModified().isGliding() ? EntityData.GLIDING.setBit(flags) : EntityData.GLIDING.unsetBit(flags));
        if (handle.getGliding() == EntityPosePacketHandle.EntityPoseHandleType.FALSE) bitMaskContainer.setValue(EntityData.GLIDING.unsetBit(flags));
    }

    public PacketHandler(Plugin arg0, ListenerPriority arg1, PacketType... arg2) {
        super(arg0, arg1, arg2);
    }
    public static void init(Plugin plugin) {
        PacketHandler.initPacketHandle();
        Bukkit.getPluginManager().registerEvents(new PacketListener(),plugin);
    }
    public static void initPacketHandle() {
        if (!protocolManager.getPacketListeners().contains(packetAdapter)) {
            protocolManager.addPacketListener(packetAdapter);
        }
    }
    public static void cancelPacketHandle() {
        if (protocolManager.getPacketListeners().contains(packetAdapter)) {
            protocolManager.removePacketListener(packetAdapter);
        }
    }
    public static Map<List<UUID>,EntityPosePacketHandle> entityPosePacketHandleMap() {
        return entityPosePacketHandleMap;
    }

    /**
     * 设置一个实体对一个玩家的发包处理
     * @param handle 实体对玩家的发包处理类
     * 使用EntityPosePacketHandleBuilder(entityModifiedUUID,playerSeeUUID).build()来构建
     */
    /**
     * 修改一个实体对一个玩家的发包处理
     * 不会覆盖原先已有的处理
     * @param entityModifiedUUID 被修改实体的UUID
     * @param playerSeeUUID 收包玩家的UUID
     * @param dataType 要修改的实体姿态数据类型
     * @param handleType 目标操作类型
     */
    public static void setPacketHandle(UUID entityModifiedUUID, UUID playerSeeUUID, EntityData dataType, EntityPosePacketHandle.EntityPoseHandleType handleType) {
        List<UUID> uuidLs = Arrays.asList(entityModifiedUUID,playerSeeUUID);
        EntityPosePacketHandle handle = entityPosePacketHandleMap.getOrDefault(
                uuidLs,
                new EntityPosePacketHandle.EntityPosePacketHandleBuilder(entityModifiedUUID,playerSeeUUID)
                        .build()
        ).setData(dataType, handleType);
        entityPosePacketHandleMap.put(uuidLs,handle);
        sendManualPacket(protocolManager,handle);
    }

    /**
     * 全部参赛队伍队内发光
     */
    public static void startTeamGlowAll() {
        TeamManager.teamMap.forEach((team, uuids) -> {
            if(TPMConfig.getGlowTeamList().contains(team.color())) {
                for (UUID entityModifiedUUID : uuids) {
                    for (UUID playerSeeUUID : uuids) {
                        if(entityModifiedUUID != playerSeeUUID) {
                            setPacketHandle(
                                    entityModifiedUUID,
                                    playerSeeUUID,
                                    EntityData.GLOWING,
                                    EntityPosePacketHandle.EntityPoseHandleType.TRUE
                            );
                        }
                    }
                }
            }
        });
    }
    /**
     * 全部参赛队伍停止队内发光
     */
    public static void stopTeamGlowAll() {
        TeamManager.teamMap.forEach((team, uuids) -> {
            if(TPMConfig.getGlowTeamList().contains(team.color())) {
                for (UUID entityModifiedUUID : uuids) {
                    for (UUID playerSeeUUID : uuids) {
                        if(entityModifiedUUID != playerSeeUUID) {
                            setPacketHandle(
                                    entityModifiedUUID,
                                    playerSeeUUID,
                                    EntityData.GLOWING,
                                    EntityPosePacketHandle.EntityPoseHandleType.IGNORE
                            );
                        }
                    }
                }
            }
        });
    }
    /**
     * 全部旁观队伍看参赛队伍全部发光
     */
    public static void startSpecTeamGlowAll() {
        TeamManager.teamMap.forEach((team, uuids) -> {
            if(TPMConfig.getSeeAllGlowTeamList().contains(team.color())) {
                List<UUID> playerParticipantLs = TeamManager.getAllGlowPlayerUUID();
                for (UUID uuidGlow : playerParticipantLs) {
                    for (UUID uuidSee : uuids) {
                        if(uuidGlow != uuidSee) {
                            setPacketHandle(
                                    uuidGlow,
                                    uuidSee,
                                    EntityData.GLOWING,
                                    EntityPosePacketHandle.EntityPoseHandleType.TRUE
                            );
                        }
                    }
                }
            }
        });
    }
    /**
     * 全部旁观队伍移除参赛队伍全部发光
     */
    public static void stopSpecTeamGlowAll() {
        TeamManager.teamMap.forEach((team, uuids) -> {
            if(TPMConfig.getSeeAllGlowTeamList().contains(team.color())) {
                List<UUID> playerParticipantLs = TeamManager.getAllGlowPlayerUUID();
                for (UUID uuidGlow : playerParticipantLs) {
                    for (UUID uuidSee : uuids) {
                        if(uuidGlow != uuidSee) {
                            setPacketHandle(
                                    uuidGlow,
                                    uuidSee,
                                    EntityData.GLOWING,
                                    EntityPosePacketHandle.EntityPoseHandleType.IGNORE
                            );
                        }
                    }
                }
            }
        });
    }
    /**
     * 全部玩家看不见自身隐身
     */
    public static void startCancelSelfInvisAll() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            setPacketHandle(
                    player.getUniqueId(),
                    player.getUniqueId(),
                    EntityData.INVISIBLE,
                    EntityPosePacketHandle.EntityPoseHandleType.FALSE
            );
        });
    }
    /**
     * 全部玩家取消看不见自身隐身
     */
    public static void stopCancelSelfInvisAll() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            setPacketHandle(
                    player.getUniqueId(),
                    player.getUniqueId(),
                    EntityData.INVISIBLE,
                    EntityPosePacketHandle.EntityPoseHandleType.IGNORE
            );
        });
    }
    /**
     * 移除所有单向发包
     */
    public static void removeAllPosePacketHandle() {
        entityPosePacketHandleMap.forEach((uuidLs,handle) -> {
            LivingEntity entityModified = handle.getEntityModified();
            Player playerSee = handle.getPlayerSee();
            if (entityModified!=null && playerSee!=null)
                sendManualPacket(protocolManager,handle);
        });
        entityPosePacketHandleMap.clear();
    }

    /**
     * 重新加载配置文件后重新计算哪些队伍需要发包
     */
    public static void refresh() {
        refreshTeamMap();
        removeAllPosePacketHandle();
        if (getIngameConfig("Glow")) {
            startTeamGlowAll();
            startSpecTeamGlowAll();
        }
        if (getIngameConfig("CancelSelfInvis")) {
            startCancelSelfInvisAll();
        }
    }
}
