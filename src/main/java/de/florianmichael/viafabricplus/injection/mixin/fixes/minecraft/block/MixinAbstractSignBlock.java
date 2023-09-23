package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.block;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignBlock.class)
public abstract class MixinAbstractSignBlock {

    @Shadow protected abstract ActionResult getActionResult(boolean usedSignChanger);

    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/SignBlockEntity;isWaxed()Z", ordinal = 1))
    public boolean removeCondition(SignBlockEntity instance) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_4)) {
            return false;
        }
        return instance.isWaxed();
    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    public void changeSignApplicators(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (ProtocolHack.getTargetVersion().isNewerThan(VersionEnum.r1_19_4)) return;

        // Remove HoneycombItem interactions
        if (world.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity && world.isClient) {
            final ItemStack item = player.getStackInHand(hand);

            cir.setReturnValue(!((item.isOf(Items.GLOW_INK_SAC) || item.isOf(Items.INK_SAC) || item.getItem() instanceof DyeItem) && player.canModifyBlocks()) && !signBlockEntity.isWaxed() ? ActionResult.CONSUME : ActionResult.SUCCESS);
        }
    }

    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractSignBlock;getActionResult(Z)Lnet/minecraft/util/ActionResult;", ordinal = 0))
    public ActionResult returnStaticResult_Pass(AbstractSignBlock instance, boolean usedSignChanger) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            return ActionResult.PASS;
        }
        return getActionResult(usedSignChanger);
    }

    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractSignBlock;getActionResult(Z)Lnet/minecraft/util/ActionResult;", ordinal = 1))
    public ActionResult returnStaticResult_Success(AbstractSignBlock instance, boolean usedSignChanger) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20tor1_20_1)) {
            return ActionResult.SUCCESS;
        }
        return getActionResult(usedSignChanger);
    }
}
