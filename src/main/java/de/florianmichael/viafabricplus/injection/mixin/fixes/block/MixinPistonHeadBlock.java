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
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonHeadBlock.class)
public class MixinPistonHeadBlock extends FacingBlock {

    public MixinPistonHeadBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void injectGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2))
            cir.setReturnValue(getCoreShape_1_8(state));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_8))
            return VoxelShapes.union(getHeadShape_1_8(state), getCoreShape_1_8(state));

        return super.getCollisionShape(state, world, pos, context);
    }

    @Unique
    private final VoxelShape protocolhack_CORE_DOWN_SHAPE_1_8 = Block.createCuboidShape(6, 4, 6, 10, 16, 10);

    @Unique
    private final VoxelShape protocolhack_CORE_UP_SHAPE_1_8 = Block.createCuboidShape(6, 0, 6, 10, 12, 10);

    @Unique
    private final VoxelShape protocolhack_CORE_NORTH_SHAPE_1_8 = Block.createCuboidShape(4, 6, 4, 12, 10, 16);

    @Unique
    private final VoxelShape protocolhack_CORE_SOUTH_SHAPE_1_8 = Block.createCuboidShape(4, 6, 0, 12, 10, 12);

    @Unique
    private final VoxelShape protocolhack_CORE_WEST_SHAPE_1_8 = Block.createCuboidShape(6, 4, 4, 10, 12, 16);

    @Unique
    private final VoxelShape protocolhack_CORE_EAST_SHAPE_1_8 = Block.createCuboidShape(0, 6, 4, 12, 10, 12);

    @Unique
    private VoxelShape getCoreShape_1_8(BlockState state) {
        final Direction direction = state.get(FACING);

        return switch (direction) {
            case DOWN -> protocolhack_CORE_DOWN_SHAPE_1_8;
            case UP -> protocolhack_CORE_UP_SHAPE_1_8;
            case NORTH -> protocolhack_CORE_NORTH_SHAPE_1_8;
            case SOUTH -> protocolhack_CORE_SOUTH_SHAPE_1_8;
            case WEST -> protocolhack_CORE_WEST_SHAPE_1_8;
            case EAST -> protocolhack_CORE_EAST_SHAPE_1_8;
        };
    }

    @Unique
    private final VoxelShape protocolhack_HEAD_DOWN_SHAPE_1_8 = Block.createCuboidShape(0, 0, 0, 16, 4, 16);

    @Unique
    private final VoxelShape protocolhack_HEAD_UP_SHAPE_1_8 = Block.createCuboidShape(0, 12, 0, 16, 16, 16);

    @Unique
    private final VoxelShape protocolhack_HEAD_NORTH_SHAPE_1_8 = Block.createCuboidShape(0, 0, 0, 16, 16, 4);

    @Unique
    private final VoxelShape protocolhack_HEAD_SOUTH_SHAPE_1_8 = Block.createCuboidShape(0, 0, 12, 16, 16, 16);

    @Unique
    private final VoxelShape protocolhack_HEAD_WEST_SHAPE_1_8 = Block.createCuboidShape(0, 0, 0, 4, 16, 16);

    @Unique
    private final VoxelShape protocolhack_HEAD_EAST_SHAPE_1_8 = Block.createCuboidShape(12, 0, 0, 16, 16, 16);

    @Unique
    private VoxelShape getHeadShape_1_8(BlockState state) {
        final Direction direction = state.get(FACING);

        return switch (direction) {
            case DOWN -> protocolhack_HEAD_DOWN_SHAPE_1_8;
            case UP -> protocolhack_HEAD_UP_SHAPE_1_8;
            case NORTH -> protocolhack_HEAD_NORTH_SHAPE_1_8;
            case SOUTH -> protocolhack_HEAD_SOUTH_SHAPE_1_8;
            case WEST -> protocolhack_HEAD_WEST_SHAPE_1_8;
            case EAST -> protocolhack_HEAD_EAST_SHAPE_1_8;
        };
    }
}
