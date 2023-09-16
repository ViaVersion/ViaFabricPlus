package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.entity;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.entity.vehicle.BoatEntity;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(BoatEntity.class)
public class MixinBoatEntity {

//    @ModifyConstant(method = "getMountedHeightOffset", constant = @Constant(doubleValue = 0.25))
//    public double modifyConstant(double constant) {
//        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_4)) {
//            return 0.3;
//        }
//        return constant;
//    }
}
