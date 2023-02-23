package de.florianmichael.viafabricplus.injection.mixin.fixes.block;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(CauldronBlock.class)
public abstract class MixinCauldronBlock extends AbstractCauldronBlock {

    @Unique
    private final static VoxelShape viafabricplus_cauldron_shape_v1_12_2 = VoxelShapes.combineAndSimplify(
            VoxelShapes.fullCube(),
            Block.createCuboidShape(2.0D, 5.0D, 2.0D, 14.0D, 16.0D, 14.0D),
            BooleanBiFunction.ONLY_FIRST
    );

    public MixinCauldronBlock(Settings settings, Map<Item, CauldronBehavior> behaviorMap) {
        super(settings, behaviorMap);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return viafabricplus_cauldron_shape_v1_12_2;
        }
        return super.getOutlineShape(state, world, pos, context);
    }
}
