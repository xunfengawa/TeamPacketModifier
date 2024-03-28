package awa.xunfeng.TPM.packets;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * 存储一个玩家对另一个玩家的metadata设置
 */
public class EntityPosePacketHandle {
    private final UUID entityModifiedUUID;
    private final UUID playerSeeUUID;
    private Integer timeInTick = -1;
    private EntityPoseHandleType onFire = EntityPoseHandleType.IGNORE;
    private EntityPoseHandleType crouching = EntityPoseHandleType.IGNORE;
    private EntityPoseHandleType previouslyRiding = EntityPoseHandleType.FALSE;   //Unused
    private EntityPoseHandleType sprinting = EntityPoseHandleType.IGNORE;
    private EntityPoseHandleType swimming = EntityPoseHandleType.IGNORE;
    private EntityPoseHandleType invisible = EntityPoseHandleType.IGNORE;
    private EntityPoseHandleType glowing = EntityPoseHandleType.IGNORE;
    private EntityPoseHandleType gliding = EntityPoseHandleType.IGNORE;
    EntityPosePacketHandle(EntityPosePacketHandleBuilder builder) {
        this.entityModifiedUUID = builder.entityModifiedUUID;
        this.playerSeeUUID = builder.playerSeeUUID;
        this.timeInTick = builder.timeInTick;
        this.onFire = builder.onFire;
        this.crouching = builder.crouching;
        this.previouslyRiding = builder.previouslyRiding;
        this.sprinting = builder.sprinting;
        this.swimming = builder.swimming;
        this.invisible = builder.invisible;
        this.glowing = builder.glowing;
        this.gliding = builder.gliding;
    }

    public EntityPosePacketHandle clone() {
        return new EntityPosePacketHandleBuilder(this.entityModifiedUUID, this.playerSeeUUID)
                        .onFire(this.onFire)
                        .crouching(this.crouching)
                        .previouslyRiding(this.previouslyRiding)
                        .sprinting(this.sprinting)
                        .swimming(this.swimming)
                        .invisible(this.invisible)
                        .glowing(this.glowing)
                        .gliding(this.gliding)
                        .timeInTick(this.timeInTick)
                        .build();
    }

    public UUID getEntityModifiedUUID() {
        return entityModifiedUUID;
    }

    public LivingEntity getEntityModified() {
        return (LivingEntity) Bukkit.getEntity(entityModifiedUUID);
    }

    public UUID getPlayerSeeUUID() {
        return playerSeeUUID;
    }

    public Player getPlayerSee() {
        return Bukkit.getPlayer(playerSeeUUID);
    }

    public Integer getTimeInTick() {
        return timeInTick;
    }

    public EntityPoseHandleType getOnFire() {
        return onFire;
    }

    public EntityPoseHandleType getCrouching() {
        return crouching;
    }

    public EntityPoseHandleType getPreviouslyRiding() {
        return previouslyRiding;
    }

    public EntityPoseHandleType getSprinting() {
        return sprinting;
    }

    public EntityPoseHandleType getSwimming() {
        return swimming;
    }

    public EntityPoseHandleType getInvisible() {
        return invisible;
    }

    public EntityPoseHandleType getGlowing() {
        return glowing;
    }

    public EntityPoseHandleType getGliding() {
        return gliding;
    }

    public void setTimeInTick(Integer timeInTick) {
        this.timeInTick = timeInTick;
    }

    public void setOnFire(EntityPoseHandleType onFire) {
        this.onFire = onFire;
    }

    public void setCrouching(EntityPoseHandleType crouching) {
        this.crouching = crouching;
    }

    public void setPreviouslyRiding(EntityPoseHandleType previouslyRiding) {
        this.previouslyRiding = previouslyRiding;
    }

    public void setSprinting(EntityPoseHandleType sprinting) {
        this.sprinting = sprinting;
    }

    public void setSwimming(EntityPoseHandleType swimming) {
        this.swimming = swimming;
    }

    public void setInvisible(EntityPoseHandleType invisible) {
        this.invisible = invisible;
    }

    public void setGlowing(EntityPoseHandleType glowing) {
        this.glowing = glowing;
    }

    public void setGliding(EntityPoseHandleType gliding) {
        this.gliding = gliding;
    }

    public EntityPosePacketHandle setData(EntityData entityData, EntityPosePacketHandle.EntityPoseHandleType handleType) {
        switch (entityData) {
            case ON_FIRE:
                setOnFire(handleType);
                break;
            case CROUCHING:
                setCrouching(handleType);
                break;
            case SPRINTING:
                setSprinting(handleType);
                break;
            case SWIMMING:
                setSwimming(handleType);
                break;
            case INVISIBLE:
                setInvisible(handleType);
                break;
            case GLOWING:
                setGlowing(handleType);
                break;
            case GLIDING:
                setGliding(handleType);
                break;
            default:
                break;
        }
        return this;
    }

    public static class EntityPosePacketHandleBuilder {
        private final UUID entityModifiedUUID;
        private final UUID playerSeeUUID;
        private Integer timeInTick = -1;
        private EntityPoseHandleType onFire = EntityPoseHandleType.IGNORE;
        private EntityPoseHandleType crouching = EntityPoseHandleType.IGNORE;
        private EntityPoseHandleType previouslyRiding = EntityPoseHandleType.FALSE;   //Unused
        private EntityPoseHandleType sprinting = EntityPoseHandleType.IGNORE;
        private EntityPoseHandleType swimming = EntityPoseHandleType.IGNORE;
        private EntityPoseHandleType invisible = EntityPoseHandleType.IGNORE;
        private EntityPoseHandleType glowing = EntityPoseHandleType.IGNORE;
        private EntityPoseHandleType gliding = EntityPoseHandleType.IGNORE;

        public EntityPosePacketHandleBuilder(@Nonnull UUID entityModifiedUUID, @Nonnull UUID playerSeeUUID) {
            this.entityModifiedUUID = entityModifiedUUID;
            this.playerSeeUUID = playerSeeUUID;
        }

        public EntityPosePacketHandleBuilder timeInTick(Integer timeInTick) {
            this.timeInTick = timeInTick;
            return this;
        }

        public EntityPosePacketHandleBuilder onFire(EntityPoseHandleType onFire) {
            this.onFire = onFire;
            return this;
        }

        public EntityPosePacketHandleBuilder crouching(EntityPoseHandleType crouching) {
            this.crouching = crouching;
            return this;
        }

        public EntityPosePacketHandleBuilder previouslyRiding(EntityPoseHandleType previouslyRiding) {
            this.previouslyRiding = previouslyRiding;
            return this;
        }

        public EntityPosePacketHandleBuilder sprinting(EntityPoseHandleType sprinting) {
            this.sprinting = sprinting;
            return this;
        }

        public EntityPosePacketHandleBuilder swimming(EntityPoseHandleType swimming) {
            this.swimming = swimming;
            return this;
        }

        public EntityPosePacketHandleBuilder invisible(EntityPoseHandleType invisible) {
            this.invisible = invisible;
            return this;
        }

        public EntityPosePacketHandleBuilder glowing(EntityPoseHandleType glowing) {
            this.glowing = glowing;
            return this;
        }

        public EntityPosePacketHandleBuilder gliding(EntityPoseHandleType gliding) {
            this.gliding = gliding;
            return this;
        }

        public EntityPosePacketHandle build() {
            return new EntityPosePacketHandle(this);
        }
    }

    public enum EntityPoseHandleType {
        TRUE,
        FALSE,
        IGNORE
    }
}
