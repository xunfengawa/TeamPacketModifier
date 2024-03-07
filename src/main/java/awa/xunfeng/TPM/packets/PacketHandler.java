package awa.xunfeng.TPM.packets;

import awa.xunfeng.TPM.TeamPacketModifier;
import awa.xunfeng.TPM.config.TPMConfig;
import awa.xunfeng.TPM.team.TeamManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.List;

import static awa.xunfeng.TPM.TeamPacketModifier.*;
import static awa.xunfeng.TPM.packets.ManualPacket.getPlayerByte;
import static awa.xunfeng.TPM.team.TeamManager.refreshTeamMap;

public class PacketHandler extends PacketAdapter{
    private static final Map<List<UUID>, List<Boolean>> oneWayPacketHandleMap = new HashMap<>();
    private static boolean isTeamGlowing = false;
    private static final PacketAdapter packetAdapter = new PacketAdapter(TeamPacketModifier.getInstance(), PacketType.Play.Server.ENTITY_METADATA)
    {
        public void onPacketSending(PacketEvent event) {
            if (!event.getPacket().getType().equals(PacketType.Play.Server.ENTITY_METADATA)) return;
            PacketContainer packet = event.getPacket().deepClone();
            Player receiver = event.getPlayer();
            Entity glowingEntity = packet.getEntityModifier(receiver.getWorld()).readSafely(0);
            if (!(glowingEntity instanceof Player)) return;
            if (((Player) glowingEntity).getGameMode().equals(GameMode.SPECTATOR)) return;
            List<UUID> playerLs = Arrays.asList(glowingEntity.getUniqueId(),receiver.getUniqueId());
            if (oneWayPacketHandleMap.containsKey(playerLs)) {
                List<Boolean> boolLs = oneWayPacketHandleMap.get(playerLs);
                List<WrappedDataValue> metadata = packet.getDataValueCollectionModifier().read(0);
                WrappedDataValue bitMaskContainer = metadata.stream().filter(obj -> (obj.getIndex() == 0)).findAny().orElse(null);
                if (bitMaskContainer != null) {
                    Byte flags = (Byte) bitMaskContainer.getValue();
                    if (boolLs.get(0) && getIngameConfig("Glow"))
                        bitMaskContainer.setValue(EntityData.GLOWING.setBit(flags));
                    if (boolLs.get(1))
                        bitMaskContainer.setValue(EntityData.INVISIBLE.setBit(flags));
                    event.setPacket(packet);
                }
                else {
                    Byte entityByte = getPlayerByte((Player) glowingEntity, boolLs.get(0), boolLs.get(1));
                    metadata.add(new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class),entityByte));
                    packet.getDataValueCollectionModifier().write(0,metadata);
                    event.setPacket(packet);
                }
            }
            else if (TeamPacketModifier.getIngameConfig("CancelSelfInvis")
                    && glowingEntity.getUniqueId().equals(receiver.getUniqueId())) {
                //如果自己隐身，则若启用可见，能看到自己
                List<WrappedDataValue> metadata = packet.getDataValueCollectionModifier().read(0);
                Byte entityByte = getPlayerByte((Player) glowingEntity, receiver.isGlowing(), false);
                metadata.add(new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class),entityByte));
                packet.getDataValueCollectionModifier().write(0,metadata);
                event.setPacket(packet);
            }
        }
    };
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
    /**
     * 让一个玩家对另一个玩家 是否 发光/隐身
     * @param playerModified 被发光的玩家
     * @param playerSee 看见发光的玩家
     * @param timeInTick 发光持续时间(null为无限长)
     * @param shouldGlow 是否单向发光(null为保持原样)
     * @param shouldInvis 是否单向隐身(null为保持原样)
     */
    public static void setPacketHandle(@Nonnull Player playerModified, @Nonnull Player playerSee, @Nullable Integer timeInTick, @Nullable Boolean shouldGlow, @Nullable Boolean shouldInvis) {
        setPacketHandle(playerModified.getUniqueId(), playerSee.getUniqueId(), timeInTick, shouldGlow, shouldInvis);
    }
    /**
     * 让一个玩家对另一个玩家 是否 发光/隐身
     * @param playerModifiedUUID 被发光的玩家的UUID
     * @param playerSeeUUID 看见发光的玩家的UUID
     * @param timeInTick 发光持续时间(null为无限长)
     * @param shouldGlow 是否单向发光(null为保持原样)
     * @param shouldInvis 是否单向隐身(null为保持原样)
     */
    public static void setPacketHandle(@Nonnull UUID playerModifiedUUID, @Nonnull UUID playerSeeUUID, @Nullable Integer timeInTick, @Nullable Boolean shouldGlow, @Nullable Boolean shouldInvis) {
        if (playerModifiedUUID == playerSeeUUID) return;
        if (Boolean.FALSE.equals(shouldGlow)
                && isTeamGlowing
                && TeamManager.findTeamByPlayerUUID(playerModifiedUUID) == TeamManager.findTeamByPlayerUUID(playerSeeUUID)) {
            //队内发光中，发光为true
            shouldGlow = true;
        }

        List<UUID> uuidLs = Arrays.asList(playerModifiedUUID,playerSeeUUID);
        Player playerModified = Bukkit.getPlayer(playerModifiedUUID);
        Player playerSee = Bukkit.getPlayer(playerSeeUUID);

        if (!oneWayPacketHandleMap.containsKey(uuidLs)) {
            //原先不存在
            if (shouldGlow == null) shouldGlow = false;
            if (shouldInvis == null) shouldInvis = false;
            oneWayPacketHandleMap.put(uuidLs, Arrays.asList(shouldGlow,shouldInvis));
        }

        if (playerModified != null) {
            //存在药水效果
            if (playerModified.isGlowing()) shouldGlow = true;
            if (playerModified.isInvisible()) shouldInvis = true;
        }

        List<Boolean> oldBoolLs =
                Arrays.asList(
                        oneWayPacketHandleMap.get(Arrays.asList(playerModifiedUUID,playerSeeUUID)).get(0),
                        oneWayPacketHandleMap.get(Arrays.asList(playerModifiedUUID,playerSeeUUID)).get(1)
                );
        if (shouldGlow == null) shouldGlow = oldBoolLs.get(0);
        if (shouldInvis == null) shouldInvis = oldBoolLs.get(1);
        List<Boolean> boolLs = Arrays.asList(shouldGlow,shouldInvis);

        if (!shouldGlow && !shouldInvis) {
            //不需要更改发包，从列表剔除
            oneWayPacketHandleMap.remove(uuidLs);
            if (playerModified!=null && playerSee!=null)
                ManualPacket.sendManualPacket(protocolManager,playerModified,playerSee,false,false);
            return;
        }

        if (timeInTick == null) timeInTick = -1;
        if(timeInTick != -1) {  //非无限时长
            oneWayPacketHandleMap.put(uuidLs,boolLs);
            new BukkitRunnable() {
                @Override
                public void run() {
                    oneWayPacketHandleMap.put(uuidLs,oldBoolLs);
                    if (playerModified!=null && playerSee!=null) {
                        ManualPacket.sendManualPacket(
                                protocolManager,
                                playerModified,
                                playerSee,
                                playerModified.isGlowing(),
                                playerModified.isInvisible());
                    }
                }
            }.runTaskLater(TeamPacketModifier.getInstance(), timeInTick);
        }

        else {  //无限时长
            oneWayPacketHandleMap.put(
                    Arrays.asList(playerModifiedUUID,playerSeeUUID),
                    Arrays.asList(shouldGlow,shouldInvis)
            );
        }
        if (playerModified!=null && playerSee!=null) ManualPacket.sendManualPacket(protocolManager,playerModified,playerSee,shouldGlow,shouldInvis);
    }
    /**
     * 全部参赛队伍队内发光
     */
    public static void startTeamGlow() {
        isTeamGlowing = true;
        TeamManager.teamMap.forEach((team, uuids) -> {
            if(TPMConfig.getGlowTeamList().contains(team.color())) {
                for (UUID uuidGlow : uuids) {
                    for (UUID uuidSee : uuids) {
                        if(uuidGlow != uuidSee) {
                            setPacketHandle(
                                    Objects.requireNonNull(uuidGlow),
                                    Objects.requireNonNull(uuidSee),
                                    -1,
                                    true,
                                    null
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
                                    Objects.requireNonNull(uuidGlow),
                                    Objects.requireNonNull(uuidSee),
                                    -1,
                                    true,
                                    null
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
        isTeamGlowing = false;
        TeamManager.teamMap.forEach((team, uuids) -> {
            if(TPMConfig.getGlowTeamList().contains(team.color())) {
                for (UUID uuidGlow : uuids) {
                    for (UUID uuidSee : uuids) {
                        if(uuidGlow != uuidSee) {
                            setPacketHandle(
                                    Objects.requireNonNull(uuidGlow),
                                    Objects.requireNonNull(uuidSee),
                                    null,
                                    false,
                                    null
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
    public static void stopSpecTeamGlowAll() {
        TeamManager.teamMap.forEach((team, uuids) -> {
            if(TPMConfig.getSeeAllGlowTeamList().contains(team.color())) {
                List<UUID> playerParticipantLs = TeamManager.getAllGlowPlayerUUID();
                for (UUID uuidGlow : playerParticipantLs) {
                    for (UUID uuidSee : uuids) {
                        if(uuidGlow != uuidSee) {
                            setPacketHandle(
                                    Objects.requireNonNull(uuidGlow),
                                    Objects.requireNonNull(uuidSee),
                                    -1,
                                    false,
                                    null
                            );
                        }
                    }
                }
            }
        });
    }
    public static Map<List<UUID>,List<Boolean>> getOneWayPacketHandleMap() {
        return oneWayPacketHandleMap;
    }
    /**
     * 移除所有单向发包修改
     */
    public static void stop() {
        stopTeamGlowAll();
        stopSpecTeamGlowAll();
        oneWayPacketHandleMap.keySet().forEach(uuidLs -> {
            Player playerModified = Bukkit.getPlayer(uuidLs.get(0));
            Player playerSee = Bukkit.getPlayer(uuidLs.get(1));
            if (playerModified!=null && playerSee!=null) {
                ManualPacket.sendManualPacket(protocolManager, playerModified, playerSee, playerModified.isGlowing(), playerModified.isInvisible());
            }
        });
        oneWayPacketHandleMap.clear();
    }

    public static void refresh() {
        stopTeamGlowAll();
        stopSpecTeamGlowAll();
        refreshTeamMap();
        startTeamGlow();
        startSpecTeamGlowAll();
        for (Player p : Bukkit.getOnlinePlayers()) {
            ManualPacket.sendManualPacket(protocolManager, p, p, p.isGlowing(), (p.isInvisible() && !getIngameConfig("CancelSelfInvis")));
        }
    }
}
