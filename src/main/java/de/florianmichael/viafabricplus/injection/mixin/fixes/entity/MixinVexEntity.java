package de.florianmichael.viafabricplus.injection.mixin.fixes.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VexEntity.class)
public class MixinVexEntity extends HostileEntity {

    public MixinVexEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getHeightOffset", at = @At("HEAD"), cancellable = true)
    public void changeHeightOffset(CallbackInfoReturnable<Double> cir) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
            cir.setReturnValue(0.0);
        }
    }
}
