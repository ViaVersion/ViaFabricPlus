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

package com.viaversion.viafabricplus.injection.mixin.features.v26_1.movement;

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

    @Redirect(method = "move", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;horizontalCollision:Z", ordinal = 2, opcode = Opcodes.GETFIELD))
    private boolean removeVerticalCheck(Entity instance) {
        return instance.horizontalCollision || (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v26_1) && this.verticalCollision);
    }

    @Redirect(method = "restituteMovementAfterCollisions", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;verticalCollisionBelow:Z", opcode = Opcodes.GETFIELD))
    private boolean fixBelowCollisionCheck(Entity instance) {
        return instance.verticalCollisionBelow || ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v26_1);
    }

    @Definition(id = "y", field = "Lnet/minecraft/world/phys/Vec3;y:D")
    @Definition(id = "currentMovement", local = @Local(type = Vec3.class, name = "currentMovement"))
    @Expression("-currentMovement.y < ?")
    @ModifyExpressionValue(method = "restituteMovementAfterCollisions", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean fixGravityCheck(boolean original, @Local(name = "currentMovement") Vec3 currentMovement) {
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v26_1)) {
            return !(currentMovement.y < 0.0);
        } else {
            return original;
        }
    }

    @Redirect(method = "restituteMovementAfterCollisions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;with(Lnet/minecraft/core/Direction$Axis;D)Lnet/minecraft/world/phys/Vec3;", ordinal = 2))
    private Vec3 fixRestitution(Vec3 instance, Direction.Axis axis, double value, @Local(name = "restitution") double restitution) {
        return instance.with(axis, ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v26_1) ? -this.getDeltaMovement().y * restitution : value);
    }
}
