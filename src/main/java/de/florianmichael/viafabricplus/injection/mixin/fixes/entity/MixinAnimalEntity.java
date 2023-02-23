package de.florianmichael.viafabricplus.injection.mixin.fixes.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnimalEntity.class)
public class MixinAnimalEntity {

    @Redirect(method = "interactMob", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"))
    public boolean redirectInteractMob(World instance) {
        return instance.isClient && ViaLoadingBase.getTargetVersion().isNewerThanOrEqualTo(ProtocolVersion.v1_15);
    }
}
