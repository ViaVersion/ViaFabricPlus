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
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallBlock;
import net.minecraft.block.enums.WallShape;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallBlock.class)
public class MixinWallBlock extends Block {

    @Unique
    private static final VoxelShape[] protocolhack_SHAPE_BY_INDEX_1_12_2 = new VoxelShape[]{
            Block.createCuboidShape(4, 0, 4, 12, 16, 12),
            Block.createCuboidShape(4, 0, 4, 12, 16, 16),
            Block.createCuboidShape(0, 0, 4, 12, 16, 12),
            Block.createCuboidShape(0, 0, 4, 12, 16, 16),
            Block.createCuboidShape(4, 0, 0, 12, 16, 12),

            Block.createCuboidShape(5, 0, 0, 11, 14, 16),

            Block.createCuboidShape(0, 0, 0, 12, 16, 12),
            Block.createCuboidShape(0, 0, 0, 12, 16, 16),
            Block.createCuboidShape(4, 0, 4, 16, 16, 12),
            Block.createCuboidShape(4, 0, 4, 16, 16, 16),

            Block.createCuboidShape(0, 0, 5, 16, 14, 11),

            Block.createCuboidShape(0, 0, 4, 16, 16, 16),
            Block.createCuboidShape(4, 0, 0, 16, 16, 12),
            Block.createCuboidShape(4, 0, 0, 16, 16, 16),
            Block.createCuboidShape(0, 0, 0, 16, 16, 12),
            Block.createCuboidShape(0, 0, 0, 16, 16, 16)
    };
    @Unique
    private static final VoxelShape[] protocolhack_CLIP_SHAPE_BY_INDEX_1_12_2 = new VoxelShape[]{
            Block.createCuboidShape(4, 0, 4, 12, 24, 12),
            Block.createCuboidShape(4, 0, 4, 12, 24, 16),
            Block.createCuboidShape(0, 0, 4, 12, 24, 12),
            Block.createCuboidShape(0, 0, 4, 12, 24, 16),
            Block.createCuboidShape(4, 0, 0, 12, 24, 12),

            Block.createCuboidShape(5, 0, 0, 11, 24, 16),

            Block.createCuboidShape(0, 0, 0, 12, 24, 12),
            Block.createCuboidShape(0, 0, 0, 12, 24, 16),
            Block.createCuboidShape(4, 0, 4, 16, 24, 12),
            Block.createCuboidShape(4, 0, 4, 16, 24, 16),

            Block.createCuboidShape(0, 0, 5, 16, 24, 11),

            Block.createCuboidShape(0, 0, 4, 16, 24, 16),
            Block.createCuboidShape(4, 0, 0, 16, 24, 12),
            Block.createCuboidShape(4, 0, 0, 16, 24, 16),
            Block.createCuboidShape(0, 0, 0, 16, 24, 12),
            Block.createCuboidShape(0, 0, 0, 16, 24, 16)
    };
    @Shadow
    @Final
    public static EnumProperty<WallShape> EAST_SHAPE;
    @Shadow
    @Final
    public static EnumProperty<WallShape> NORTH_SHAPE;
    @Shadow
    @Final
    public static EnumProperty<WallShape> WEST_SHAPE;
    @Shadow
    @Final
    public static EnumProperty<WallShape> SOUTH_SHAPE;
    public MixinWallBlock(Settings settings) {
        super(settings);
    }

    @Unique
    private static BlockState protocolhack_oldWallPlacementLogic(BlockState state) {
        boolean addUp = false;
        if (state.get(WallBlock.NORTH_SHAPE) == WallShape.TALL) {
            state = state.with(WallBlock.NORTH_SHAPE, WallShape.LOW);
            addUp = true;
        }
        if (state.get(WallBlock.EAST_SHAPE) == WallShape.TALL) {
            state = state.with(WallBlock.EAST_SHAPE, WallShape.LOW);
            addUp = true;
        }
        if (state.get(WallBlock.SOUTH_SHAPE) == WallShape.TALL) {
            state = state.with(WallBlock.SOUTH_SHAPE, WallShape.LOW);
            addUp = true;
        }
        if (state.get(WallBlock.WEST_SHAPE) == WallShape.TALL) {
            state = state.with(WallBlock.WEST_SHAPE, WallShape.LOW);
            addUp = true;
        }
        if (addUp) {
            state = state.with(WallBlock.UP, true);
        }
        return state;
    }

    @Unique
    private static int protocolhack_getShapeIndex_1122(BlockState state) {
        int i = 0;

        if (state.get(NORTH_SHAPE) != WallShape.NONE) i |= 1 << Direction.NORTH.getHorizontal();
        if (state.get(EAST_SHAPE) != WallShape.NONE) i |= 1 << Direction.EAST.getHorizontal();
        if (state.get(SOUTH_SHAPE) != WallShape.NONE) i |= 1 << Direction.SOUTH.getHorizontal();
        if (state.get(WEST_SHAPE) != WallShape.NONE) i |= 1 << Direction.WEST.getHorizontal();

        return i;
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    public void injectGetPlacementState(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            cir.setReturnValue(protocolhack_oldWallPlacementLogic(cir.getReturnValue()));
        }
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("RETURN"), cancellable = true)
    public void injectGetStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            cir.setReturnValue(protocolhack_oldWallPlacementLogic(cir.getReturnValue()));
        }
    }

    @Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
    public void injectGetCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(protocolhack_CLIP_SHAPE_BY_INDEX_1_12_2[protocolhack_getShapeIndex_1122(state)]);
        }
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void injectGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            cir.setReturnValue(protocolhack_SHAPE_BY_INDEX_1_12_2[protocolhack_getShapeIndex_1122(state)]);
        }
    }
}
