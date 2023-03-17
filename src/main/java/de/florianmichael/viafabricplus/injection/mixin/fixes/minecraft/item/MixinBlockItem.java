/*
 * MIT License
 *
 * Copyright (c) 2019 Joseph Burton (Earthcomputer)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.item;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class MixinBlockItem {

    @Inject(method = "canPlace", at = @At("HEAD"), cancellable = true)
    private void injectCanPlace(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> ci) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            Block block = state.getBlock();
            if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
                World world = context.getWorld();
                BlockPos pos = context.getBlockPos();
                boolean foundAdjChest = false;
                for (Direction dir : Direction.Type.HORIZONTAL) {
                    BlockState otherState = world.getBlockState(pos.offset(dir));
                    if (otherState.getBlock() == block) {
                        if (foundAdjChest) {
                            ci.setReturnValue(false);
                            return;
                        }
                        foundAdjChest = true;
                        if (otherState.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
                            ci.setReturnValue(false);
                            return;
                        }
                    }
                }
            }
        }
    }
}
