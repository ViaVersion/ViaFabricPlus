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

package com.viaversion.viafabricplus.injection.mixin.features.block.shape;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BaseRailBlock.class)
public abstract class MixinAbstractRailBlock extends Block {

    @Shadow
    @Final
    private static VoxelShape SHAPE_SLOPE;

    @Unique
    private static final VoxelShape viaFabricPlus$ascending_shape_r1_10_x = Shapes.block();

    @Unique
    private static final VoxelShape viaFabricPlus$ascending_shape_r1_9_x = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.5D, 16.0D);

    @Unique
    private static final VoxelShape viaFabricPlus$ascending_shape_r1_8_x = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D);

    public MixinAbstractRailBlock(Properties settings) {
        super(settings);
    }

    @Redirect(method = "getShape", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/block/BaseRailBlock;SHAPE_SLOPE:Lnet/minecraft/world/phys/shapes/VoxelShape;"))
    private VoxelShape changeOutlineShape() {
        if (ProtocolTranslator.getTargetVersion().equalTo(ProtocolVersion.v1_10)) {
            return viaFabricPlus$ascending_shape_r1_10_x;
        } else if (ProtocolTranslator.getTargetVersion().betweenInclusive(ProtocolVersion.v1_9, ProtocolVersion.v1_9_3)) {
            return viaFabricPlus$ascending_shape_r1_9_x;
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return viaFabricPlus$ascending_shape_r1_8_x;
        } else {
            return SHAPE_SLOPE;
        }
    }

}
