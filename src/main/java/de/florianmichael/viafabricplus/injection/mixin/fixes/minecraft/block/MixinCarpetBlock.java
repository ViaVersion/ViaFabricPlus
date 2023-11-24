package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.block;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CarpetBlock.class)
public abstract class MixinCarpetBlock extends Block {

    @Unique
    private static final VoxelShape viaFabricPlus$shape_r1_7_10 = Block.createCuboidShape(0.0D, -0.00001D/*0.0D*/, 0.0D, 16.0D, 0.0D, 16.0D);

    public MixinCarpetBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_7_6tor1_7_10)) {
            return viaFabricPlus$shape_r1_7_10;
        }

        return super.getCollisionShape(state, world, pos, context);
    }

}
