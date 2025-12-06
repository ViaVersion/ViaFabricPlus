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

package com.viaversion.viafabricplus.injection.mixin.features.block.interaction;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronBarsBlock.class)
public abstract class MixinPaneBlock extends CrossCollisionBlock {

    protected MixinPaneBlock(final float radius1, final float radius2, final float boundingHeight1, final float boundingHeight2, final float collisionHeight, final Properties settings) {
        super(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings);
    }

    @WrapOperation(method = "getStateForPlacement", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/IronBarsBlock;attachsTo(Lnet/minecraft/world/level/block/state/BlockState;Z)Z"))
    private boolean countConnections(IronBarsBlock instance, BlockState state, boolean sideSolidFullSquare, Operation<Boolean> original, @Share("count") LocalIntRef countRef) {
        final boolean connectsTo = original.call(instance, state, sideSolidFullSquare);
        if (connectsTo) {
            countRef.set(countRef.get() + 1);
        }
        return connectsTo;
    }

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void changePlacementState(BlockPlaceContext ctx, CallbackInfoReturnable<BlockState> cir, @Share("count") LocalIntRef countRef) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8) && countRef.get() == 0) {
            cir.setReturnValue(cir.getReturnValue().setValue(NORTH, true).setValue(SOUTH, true).setValue(WEST, true).setValue(EAST, true));
        }
    }

}
