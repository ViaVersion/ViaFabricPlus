/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.movement.collision;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HoneyBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneyBlock.class)
public abstract class MixinHoneyBlock extends Block {

    public MixinHoneyBlock(final Settings settings) {
        super(settings);
    }

    @Shadow
    protected abstract boolean isSliding(BlockPos pos, Entity entity);

    @Shadow
    protected abstract void addCollisionEffects(World world, Entity entity);

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void applyBedrockHoneyCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler, boolean bl, CallbackInfo ci) {
        if (!ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            return;
        }

        ci.cancel();
        if (this.isSliding(pos, entity)) {
            this.addCollisionEffects(world, entity);
        }

        final Vec3d velocity = entity.getVelocity();
        entity.setVelocity(new Vec3d(velocity.x * 0.4F, Math.max(-0.12F, velocity.y), velocity.z * 0.4F));
    }

    @Override
    public void onSteppedOn(final World world, final BlockPos pos, final BlockState state, final Entity entity) {
        if (ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            final double absoluteY = Math.abs(entity.getVelocity().y);
            if (absoluteY < 0.1 && !entity.bypassesSteppingEffects()) {
                final double frictionFactor = 0.4 + absoluteY * 0.2;
                entity.setVelocity(entity.getVelocity().multiply(frictionFactor, 1.0F, frictionFactor));
            }
        } else {
            super.onSteppedOn(world, pos, state, entity);
        }
    }

    @Override
    public float getSlipperiness() {
        return ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest) ? 0.8F : super.getSlipperiness();
    }

    @Override
    public float getVelocityMultiplier() {
        return ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest) ? 1F : super.getVelocityMultiplier();
    }

    @Override
    public float getJumpVelocityMultiplier() {
        return ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest) ? 0.6F : super.getJumpVelocityMultiplier();
    }

    @Inject(method = {"getOldVelocityY", "getNewVelocityY"}, at = @At("HEAD"), cancellable = true)
    private static void simplifyVelocityComparisons(double d, CallbackInfoReturnable<Double> cir) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21)) {
            cir.setReturnValue(d);
        }
    }

}
