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

package com.viaversion.viafabricplus.injection.mixin.features.v1_20_5.movement;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Shadow
    private Vec3 position;

    @Shadow
    private Level level;

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract boolean onGround();

    @Shadow
    public abstract float maxUpStep();

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    public boolean verticalCollision;

    @Inject(method = "collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
    private void use1_20_6StepCollisionCalculation(Vec3 movement, CallbackInfoReturnable<Vec3> cir) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_5)) {
            final Entity thiz = (Entity) (Object) this;
            final AABB box = this.getBoundingBox();
            final List<VoxelShape> collisions = this.level().getEntityCollisions(thiz, box.expandTowards(movement));
            Vec3 adjustedMovement = movement.lengthSqr() == 0D ? movement : Entity.collideBoundingBox(thiz, movement, box, this.level(), collisions);
            final boolean changedX = movement.x != adjustedMovement.x;
            final boolean changedY = movement.y != adjustedMovement.y;
            final boolean changedZ = movement.z != adjustedMovement.z;
            final boolean mayTouchGround = this.onGround() || changedY && movement.y < 0D;
            if (this.maxUpStep() > 0F && mayTouchGround && (changedX || changedZ)) {
                Vec3 vec3d2 = Entity.collideBoundingBox(thiz, new Vec3(movement.x, this.maxUpStep(), movement.z), box, this.level(), collisions);
                Vec3 vec3d3 = Entity.collideBoundingBox(thiz, new Vec3(0D, this.maxUpStep(), 0D), box.expandTowards(movement.x, 0D, movement.z), this.level(), collisions);
                if (vec3d3.y < this.maxUpStep()) {
                    Vec3 vec3d4 = Entity.collideBoundingBox(thiz, new Vec3(movement.x, 0D, movement.z), box.move(vec3d3), this.level(), collisions).add(vec3d3);
                    if (vec3d4.horizontalDistanceSqr() > vec3d2.horizontalDistanceSqr()) {
                        vec3d2 = vec3d4;
                    }
                }

                if (vec3d2.horizontalDistanceSqr() > adjustedMovement.horizontalDistanceSqr()) {
                    adjustedMovement = vec3d2.add(Entity.collideBoundingBox(thiz, new Vec3(0D, -vec3d2.y + (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_13_2) ? 0 : movement.y), 0D), box.move(vec3d2), this.level(), collisions));
                }
            }

            cir.setReturnValue(adjustedMovement);
        }
    }
}
