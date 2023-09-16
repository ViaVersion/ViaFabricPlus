package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.entity;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.vehicle.BoatEntity;
import net.raphimc.vialoader.util.VersionEnum;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BoatEntity.class)
public class MixinBoatEntity {

    @Inject(method = "getPassengerAttachmentPos", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void modifyDimensionHeight(Entity passenger, EntityDimensions dimensions, float scaleFactor, CallbackInfoReturnable<Vector3f> cir, float f) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_4)) {
            cir.setReturnValue(new Vector3f(0F, 0.3F, f));
        }
    }
}
