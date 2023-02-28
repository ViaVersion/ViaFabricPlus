package de.florianmichael.viafabricplus.injection.mixin.fixes.block;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SoulSandBlock.class)
public class MixinSoulSandBlock extends Block {

    @Unique
    private boolean viafabricplus_forceValue;

    public MixinSoulSandBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            final Vec3d velocity = entity.getVelocity();
            this.viafabricplus_forceValue = true;
            entity.setVelocity(velocity.getX() * this.getVelocityMultiplier(), velocity.getY(), velocity.getZ() * this.getVelocityMultiplier());
            this.viafabricplus_forceValue = false;
        }
    }

    @Override
    public float getVelocityMultiplier() {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_14_4) && !this.viafabricplus_forceValue) {
            return 1.0F;
        }
        return super.getVelocityMultiplier();
    }
}
