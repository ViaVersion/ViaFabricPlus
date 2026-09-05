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

package com.viaversion.viafabricplus.injection.mixin.features.v1_21_5;

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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
    public abstract boolean isAlive();

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void removeLeashActions(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        // Removes shearing of equipment & snipping all leashes + condition changes
        if (ViaFabricPlus.api().targetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_5)) {
            final ItemStack itemStack = player.getItemInHand(hand);
            if (this.isAlive() && this instanceof final Leashable leashable) {
                if (leashable.getLeashHolder() != player) {
                    if (itemStack.is(Items.LEAD) && leashable.canHaveALeashAttachedTo(player)) {
                        itemStack.shrink(1);
                        cir.setReturnValue(InteractionResult.SUCCESS);
                        return;
                    }
                } else {
                    cir.setReturnValue(InteractionResult.SUCCESS.withoutItem());
                    return;
                }
            }

            cir.setReturnValue(InteractionResult.PASS);
        }
    }

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

    @WrapWithCondition(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addMovementThisTick(Lnet/minecraft/world/entity/Entity$Movement;)V"))
    private boolean removeExtraCollisionChecks(Entity instance, Entity.Movement movement) {
        return ViaFabricPlus.api().targetVersion().newerThanOrEqualTo(ProtocolVersion.v1_21_5);
    }
}
