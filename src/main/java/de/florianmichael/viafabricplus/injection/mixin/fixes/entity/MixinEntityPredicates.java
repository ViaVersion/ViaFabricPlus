package de.florianmichael.viafabricplus.injection.mixin.fixes.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPredicates.class)
public class MixinEntityPredicates {

    @SuppressWarnings("target")
    @Redirect(method = "method_5915(Lnet/minecraft/entity/Entity;Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/scoreboard/AbstractTeam$CollisionRule;Lnet/minecraft/entity/Entity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isMainPlayer()Z"))
    private static boolean makeMainPlayerUnpushable(PlayerEntity player) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return false;
        }
        return player.isMainPlayer();
    }
}
