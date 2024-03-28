package awa.xunfeng.TPM.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.collect.Lists;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

import static awa.xunfeng.TPM.TeamPacketModifier.enabled;
import static awa.xunfeng.TPM.packets.PacketHandler.setContainerBits;

public class ManualPacket {
    /**
     * 手动发送实体姿态包
     * @param protocolManager 全局protocolManager
     * @param handle 姿态包处理类实例
     */
    public static void sendManualPacket(ProtocolManager protocolManager, @Nonnull EntityPosePacketHandle handle) {
        LivingEntity entityModified = handle.getEntityModified();
        Player playerSee = handle.getPlayerSee();
        if (entityModified == null || playerSee == null) return;

        if (entityModified instanceof Player player && player.getGameMode() == GameMode.SPECTATOR) return;

        PacketContainer modifiedPacket = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        modifiedPacket.getIntegers().write(0, entityModified.getEntityId());
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
        watcher.setEntity(entityModified);
        //逻辑部分
        WrappedDataValue bitMaskContainer = new WrappedDataValue(0, WrappedDataWatcher.Registry.get(Byte.class), getEntityPoseByte(entityModified));
        if (enabled) setContainerBits(handle, bitMaskContainer);
        watcher.setObject(0, byteSerializer, bitMaskContainer.getValue());
        //发送部分
        final List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
        watcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
            final WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
            wrappedDataValueList.add(new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
        });
        modifiedPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        protocolManager.sendServerPacket(playerSee, modifiedPacket);
//        System.out.println("手动发送实体姿态包: " + entityModified.getName() + " -> " + playerSee.getName() + " (" + bitMaskContainer.getValue() + ")");
    }

    public static byte getEntityPoseByte(Entity entity) {
        boolean[] data;
        if (entity instanceof Player player) {
            data = new boolean[]{
                    player.isVisualFire(), player.isSneaking(), false, player.isSprinting(),
                    player.isSwimming(), player.isInvisible(), player.isGlowing(), player.isGliding()
            };
        }
        else {
            data = new boolean[]{
                    entity.isVisualFire(), entity.isSneaking(), false, false,
                    false, entity.isInvisible(), entity.isGlowing(), false
            };
        }
        byte entityByte = 0;
        for (int i = 0 ; i < 8; i++) {
            if (data[i]) {
                entityByte |= (byte) (1 << i);
            }
        }
        return entityByte;
    }
}
