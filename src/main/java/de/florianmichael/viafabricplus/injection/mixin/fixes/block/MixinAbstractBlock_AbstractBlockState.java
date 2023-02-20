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
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.InfestedBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlock_AbstractBlockState {

    @Shadow
    protected abstract BlockState asBlockState();

    @Inject(method = "getHardness", at = @At("RETURN"), cancellable = true)
    public void changeHardness(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        final BlockState state = this.asBlockState();

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            if (state.getBlock() instanceof InfestedBlock) {
                cir.setReturnValue(0.75F);
            }
        }

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            if (state.getBlock() == Blocks.END_STONE_BRICKS || state.getBlock() == Blocks.END_STONE_BRICK_SLAB || state.getBlock() == Blocks.END_STONE_BRICK_STAIRS || state.getBlock() == Blocks.END_STONE_BRICK_WALL) {
                cir.setReturnValue(0.8F);
            }
        }

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_15_2)) {
            if (state.getBlock() == Blocks.PISTON || state.getBlock() == Blocks.STICKY_PISTON || state.getBlock() == Blocks.PISTON_HEAD) {
                cir.setReturnValue(0.5F);
            }
        }

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_16_4)) {
            if (state.getBlock() instanceof InfestedBlock) {
                cir.setReturnValue(0F);
            }
        }
    }
}
