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

package com.viaversion.viafabricplus.injection.mixin.features.movement.liquid;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFluidInteraction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityFluidInteraction.class)
public abstract class MixinEntityFluidInteraction {

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getEyeY()D"))
    private double subtractMagicOffset(Entity instance) {
        if (ProtocolTranslator.getTargetVersion().betweenInclusive(ProtocolVersion.v1_16, ProtocolVersion.v1_20_3)) {
            return instance.getEyeY() - 0.11111111F;
        } else {
            return instance.getEyeY();
        }
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;getHeight(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"))
    private float addMagicOffset(FluidState instance, BlockGetter blockGetter, BlockPos blockPos) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            return instance.getHeight(blockGetter, blockPos) + 0.11111111F;
        } else {
            return instance.getHeight(blockGetter, blockPos);
        }
    }

    // TODO 26.1
//    @Inject(method = "updateFluidHeightAndDoFluidPushing", at = @At("HEAD"), cancellable = true)
//    private void modifyFluidMovementBoundingBox(TagKey<Fluid> fluidTag, double d, CallbackInfoReturnable<Boolean> cir) {
//        if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12_2) && !ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
//            return;
//        }
//
//        AABB box = this.getBoundingBox().inflate(0, -0.4, 0).deflate(0.001);
//        int minX = Mth.floor(box.minX);
//        int maxX = Mth.ceil(box.maxX);
//        int minY = Mth.floor(box.minY);
//        int maxY = Mth.ceil(box.maxY);
//        int minZ = Mth.floor(box.minZ);
//        int maxZ = Mth.ceil(box.maxZ);
//
//        if (!this.level.hasChunksAt(minX, minY, minZ, maxX, maxY, maxZ)) {
//            cir.setReturnValue(false);
//            return;
//        }
//
//        double waterHeight = 0;
//        boolean foundFluid = false;
//        Vec3 pushVec = Vec3.ZERO;
//
//        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
//
//        for (int x = minX; x < maxX; x++) {
//            for (int y = minY - 1; y < maxY; y++) {
//                for (int z = minZ; z < maxZ; z++) {
//                    mutable.set(x, y, z);
//                    FluidState state = this.level.getFluidState(mutable);
//                    if (state.is(fluidTag)) {
//                        double height = y + state.getHeight(this.level, mutable);
//                        if (height >= box.minY - 0.4)
//                            waterHeight = Math.max(height - box.minY + 0.4, waterHeight);
//                        if (y >= minY && maxY >= height) {
//                            foundFluid = true;
//                            pushVec = pushVec.add(state.getFlow(this.level, mutable));
//                        }
//                    }
//                }
//            }
//        }
//
//        if (pushVec.length() > 0) {
//            pushVec = pushVec.normalize().scale(0.014);
//            this.setDeltaMovement(this.getDeltaMovement().add(pushVec));
//        }
//
//        this.fluidHeight.put(fluidTag, waterHeight);
//        cir.setReturnValue(foundFluid);
//    }

}
