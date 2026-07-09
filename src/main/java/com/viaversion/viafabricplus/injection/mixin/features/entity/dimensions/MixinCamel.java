/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.entity.dimensions;

import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camel.class)
public abstract class MixinCamel extends AbstractHorse {

    public MixinCamel(EntityType<? extends AbstractHorse> entityType, Level world) {
        super(entityType, world);
    }

    @ModifyConstant(method = "getBodyAnchorAnimationYOffset", constant = @Constant(doubleValue = 0.375))
    private double scaleSitOffset(double constant, @Local(ordinal = 1, argsOnly = true) float scale) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v26_1)) {
            return constant * scale;
        } else {
            return constant;
        }
    }

    @Redirect(method = "getBodyAnchorAnimationYOffset", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/camel/Camel;isBaby()Z"))
    private boolean removeBabySitOffset(Camel instance) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v26_1) && instance.isBaby();
    }

    @Redirect(method = "getDefaultDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/camel/Camel;isBaby()Z"))
    private boolean dontChangeScale(Camel instance) {
        return ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v26_1) && instance.isBaby();
    }

    @Inject(method = "getAgeScale", at = @At("HEAD"), cancellable = true)
    private void changeBabyScale(CallbackInfoReturnable<Float> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_11) && this.isBaby()) {
            cir.setReturnValue(0.45F);
        }
    }

    @Override
    public void onPassengerTurned(@NonNull Entity passenger) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20) && this.getControllingPassenger() != passenger) {
            this.viaFabricPlus$clampPassengerYaw1_20_1(passenger);
        }
    }

    @Override
    protected void positionRider(@NonNull Entity passenger, @NonNull MoveFunction positionUpdater) {
        super.positionRider(passenger, positionUpdater);

        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20)) {
            this.viaFabricPlus$clampPassengerYaw1_20_1(passenger);
        }
    }

    @Unique
    private void viaFabricPlus$clampPassengerYaw1_20_1(final Entity passenger) {
        passenger.setYBodyRot(this.getYRot());
        final float passengerYaw = passenger.getYRot();

        final float deltaDegrees = Mth.wrapDegrees(passengerYaw - this.getYRot());
        final float clampedDelta = Mth.clamp(deltaDegrees, -160.0F, 160.0F);
        passenger.yRotO += clampedDelta - deltaDegrees;

        final float newYaw = passengerYaw + clampedDelta - deltaDegrees;
        passenger.setYRot(newYaw);
        passenger.setYHeadRot(newYaw);
    }

}
