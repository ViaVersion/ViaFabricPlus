package de.florianmichael.viafabricplus.injection.mixin.fixes.block;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Supplier;

@Mixin(ChestBlock.class)
public abstract class MixinChestBlock extends AbstractChestBlock<ChestBlockEntity> {

    protected MixinChestBlock(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier) {
        super(settings, blockEntityTypeSupplier);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(LegacyProtocolVersion.r1_4_2)) {
            return VoxelShapes.fullCube();
        }
        return super.getCollisionShape(state, world, pos, context);
    }
}
