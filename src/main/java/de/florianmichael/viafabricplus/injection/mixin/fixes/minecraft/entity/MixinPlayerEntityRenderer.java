/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer {

    @Inject(method = "getPositionOffset(Lnet/minecraft/client/network/AbstractClientPlayerEntity;F)Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"), cancellable = true)
    private void modifySleepingOffset(AbstractClientPlayerEntity player, float delta, CallbackInfoReturnable<Vec3d> cir) {
        if (player.getPose() == EntityPose.SLEEPING) {
            final Direction sleepingDir = player.getSleepingDirection();
            if (sleepingDir != null) {
                if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2)) {
                    cir.setReturnValue(cir.getReturnValue().subtract(sleepingDir.getOffsetX() * 0.4, 0, sleepingDir.getOffsetZ() * 0.4));
                }
                if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_5tob1_5_2)) {
                    cir.setReturnValue(cir.getReturnValue().subtract(sleepingDir.getOffsetX() * 0.1, 0, sleepingDir.getOffsetZ() * 0.1));
                }
                if (ProtocolTranslator.getTargetVersion().betweenInclusive(LegacyProtocolVersion.b1_6tob1_6_6, ProtocolVersion.v1_7_6)) {
                    cir.setReturnValue(cir.getReturnValue().subtract(0, 0.3F, 0));
                }
            }
        }
    }

    @Redirect(method = "getPositionOffset(Lnet/minecraft/client/network/AbstractClientPlayerEntity;F)Lnet/minecraft/util/math/Vec3d;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isInSneakingPose()Z"))
    private boolean disableSneakPositionOffset(AbstractClientPlayerEntity player) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_11_1) && player.isInSneakingPose();
    }

}
