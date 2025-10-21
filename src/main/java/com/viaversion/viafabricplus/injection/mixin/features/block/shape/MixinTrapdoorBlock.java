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
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.raphimc.viabedrock.api.BedrockProtocolVersion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Map;

@Mixin(TrapdoorBlock.class)
public abstract class MixinTrapdoorBlock {

    @Unique
    private static final Map<Direction, VoxelShape> viaFabricPlus$shape_bedrock = Map.of(
        Direction.NORTH, VoxelShapes.cuboid(0, 0, 0.8175, 1, 1, 1),
        Direction.SOUTH, VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.1825),
        Direction.WEST, VoxelShapes.cuboid(0.8175, 0, 0, 1, 1, 1),
        Direction.EAST, VoxelShapes.cuboid(0, 0, 0, 0.1825, 1, 1),
        Direction.DOWN, VoxelShapes.cuboid(0, 0.8175, 0, 1, 1, 1),
        Direction.UP, VoxelShapes.cuboid(0, 0, 0, 1, 0.1825, 1)
    );

    @Shadow
    @Final
    private static Map<Direction, VoxelShape> shapeByDirection;

    @Redirect(method = "getOutlineShape", at = @At(value = "FIELD", target = "Lnet/minecraft/block/TrapdoorBlock;shapeByDirection:Ljava/util/Map;"))
    private Map<Direction, VoxelShape> modifyShapeByDirection() {
        if (ProtocolTranslator.getTargetVersion().equals(BedrockProtocolVersion.bedrockLatest)) {
            return viaFabricPlus$shape_bedrock;
        }
        return shapeByDirection;
    }

}
