/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.viafabricplus.injection.mixin.fixes.block;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LadderBlock.class)
public class MixinLadderBlock {

    @Unique
    private static final VoxelShape protocolhack_EAST_SHAPE_1_8 = Block.createCuboidShape(0, 0, 0, 2, 16, 16);

    @Unique
    private static final VoxelShape protocolhack_WEST_SHAPE_1_8 = Block.createCuboidShape(14, 0, 0, 16, 16, 16);

    @Unique
    private static final VoxelShape protocolhack_SOUTH_SHAPE_1_8 = Block.createCuboidShape(0, 0, 0, 16, 16, 2);

    @Unique
    private static final VoxelShape protocolhack_NORTH_SHAPE_1_8 = Block.createCuboidShape(0, 0, 14, 16, 16, 16);

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8)) {
            switch (state.get(LadderBlock.FACING)) {
                case NORTH -> ci.setReturnValue(protocolhack_NORTH_SHAPE_1_8);
                case SOUTH -> ci.setReturnValue(protocolhack_SOUTH_SHAPE_1_8);
                case WEST -> ci.setReturnValue(protocolhack_WEST_SHAPE_1_8);
                default -> ci.setReturnValue(protocolhack_EAST_SHAPE_1_8);
            }
        }
    }
}
