package awa.xunfeng.TPM.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static awa.xunfeng.TPM.TeamPacketModifier.getIngameConfig;

public class ManualPacket {
    /**
     * 手动发送玩家metadata包
     * @param protocolManager 全局protocolManager
     * @param playerModified 发光玩家
     * @param playerSee 看见发光的玩家
     * @param shouldGlow 应该发光
     */
    public static void sendManualPacket(ProtocolManager protocolManager, @Nonnull Player playerModified, @Nonnull Player playerSee, boolean shouldGlow) {
        sendManualPacket(protocolManager,playerModified,playerSee,shouldGlow,playerModified.isInvisible());
    }
    /**
     * 手动发送玩家metadata包
     * @param protocolManager 全局protocolManager
     * @param playerModified 发光玩家
     * @param playerSee 看见发光的玩家
     * @param shouldGlow 应该发光
     * @param shouldInvis 应该隐身
     */
    public static void sendManualPacket(ProtocolManager protocolManager, @Nonnull Player playerModified, @Nonnull Player playerSee, boolean shouldGlow, boolean shouldInvis) {
        PacketContainer glowPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        if (playerModified.getGameMode().equals(GameMode.SPECTATOR)) return;
        if (!getIngameConfig("Glow")) shouldGlow = playerModified.isGlowing();
        glowPacket.getIntegers().write(0, playerModified.getEntityId());
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
        watcher.setEntity(playerModified);
        //逻辑部分
        byte entityByte = getPlayerByte(playerModified, shouldGlow, shouldInvis);
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
    public static byte getPlayerByte(Player playerModified, @Nullable Boolean shouldGlow, @Nullable Boolean shouldInvis) {
        if (shouldGlow == null) shouldGlow = playerModified.isGlowing();
        if (shouldInvis == null) shouldInvis = playerModified.isInvisible();
        if (!getIngameConfig("Glow")) shouldGlow = playerModified.isGlowing();
        boolean[] data = {
                playerModified.isVisualFire(), playerModified.isSneaking(), false, playerModified.isSprinting(),
                playerModified.isSwimming(), shouldInvis, shouldGlow, playerModified.isGliding()
        };
        byte entityByte = 0;
        for (int i = 0 ; i < 8; i++) {
            if (data[i]) {
                entityByte |= (1 << i);
            }
        }
        return entityByte;
    }
}
