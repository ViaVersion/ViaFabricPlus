package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.block;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignBlock.class)
public abstract class MixinAbstractSignBlock {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void alwaysReturnSuccess(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!world.isClient) return;

        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_14_4)) {
            cir.setReturnValue(ActionResult.SUCCESS);
        } else if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_4)) {
            final ItemStack itemStack = player.getStackInHand(hand);
            final Item item = itemStack.getItem();
            final boolean isSuccess = (item instanceof DyeItem || itemStack.isOf(Items.GLOW_INK_SAC) || itemStack.isOf(Items.INK_SAC)) && player.canModifyBlocks();
            cir.setReturnValue(isSuccess ? ActionResult.SUCCESS : ActionResult.CONSUME);
        }
    }

}
