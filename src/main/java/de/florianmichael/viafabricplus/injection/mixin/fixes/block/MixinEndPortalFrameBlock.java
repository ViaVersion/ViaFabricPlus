/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.block;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Block;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EndPortalFrameBlock.class)
public class MixinEndPortalFrameBlock {

    @Shadow
    @Final
    protected static VoxelShape FRAME_SHAPE;
    @Shadow
    @Final
    protected static VoxelShape FRAME_WITH_EYE_SHAPE;
    @Unique
    private final VoxelShape viafabricplus_eye_shape_v1_12_2 = Block.createCuboidShape(5.0, 13.0, 5.0, 11.0, 16.0, 11.0);

    @Redirect(method = "getOutlineShape", at = @At(value = "FIELD", target = "Lnet/minecraft/block/EndPortalFrameBlock;FRAME_WITH_EYE_SHAPE:Lnet/minecraft/util/shape/VoxelShape;"))
    public VoxelShape redirectGetOutlineShape() {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return VoxelShapes.union(FRAME_SHAPE, viafabricplus_eye_shape_v1_12_2);
        }
        return FRAME_WITH_EYE_SHAPE;
    }
}
