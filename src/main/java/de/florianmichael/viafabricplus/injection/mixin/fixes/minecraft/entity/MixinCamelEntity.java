package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.entity;

import de.florianmichael.viafabricplus.definition.EntityHeightOffsetsPre1_20_2;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.world.World;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CamelEntity.class)
public abstract class MixinCamelEntity extends AbstractHorseEntity {

    public MixinCamelEntity(EntityType<? extends AbstractHorseEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/CamelEntity;isBaby()Z", ordinal = 0))
    public boolean removeIfCase(CamelEntity instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_4)) {
            return false;
        }
        return instance.isBaby();
    }

    @Override
    public void onPassengerLookAround(Entity passenger) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20tor1_20_1) && this.getControllingPassenger() != passenger) {
            EntityHeightOffsetsPre1_20_2.clamPassengerYaw(this, passenger);
        }
    }

    @Override
    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        super.updatePassengerPosition(passenger, positionUpdater);

        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            EntityHeightOffsetsPre1_20_2.clamPassengerYaw(this, passenger);
        }
    }
}
