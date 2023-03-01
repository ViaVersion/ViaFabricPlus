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
