package de.florianmichael.viafabricplus.injection.mixin.fixes.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractDonkeyEntity.class)
public abstract class MixinAbstractDonkeyEntity extends AbstractHorseEntity {

    @Shadow public abstract boolean hasChest();

    @Shadow public abstract void setHasChest(boolean hasChest);

    @Shadow protected abstract void playAddChestSound();

    public MixinAbstractDonkeyEntity(EntityType<? extends AbstractHorseEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    public void fixInteraction(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
            ItemStack lv = player.getStackInHand(hand);
            if (!this.isBaby()) {
                if (this.isTame() && player.shouldCancelInteraction()) {
                    this.openInventory(player);
                    cir.setReturnValue(ActionResult.success(this.world.isClient));
                }
                if (this.hasPassengers()) {
                    cir.setReturnValue(super.interactMob(player, hand));
                }
            }
            if (!lv.isEmpty()) {
                if (this.isBreedingItem(lv)) {
                    cir.setReturnValue(this.interactHorse(player, lv));
                }
                if (!this.isTame()) {
                    this.playAngrySound();
                    cir.setReturnValue(ActionResult.success(this.world.isClient));
                }
                if (!this.hasChest() && lv.isOf(Blocks.CHEST.asItem())) {
                    this.setHasChest(true);
                    this.playAddChestSound();
                    if (!player.getAbilities().creativeMode) {
                        lv.decrement(1);
                    }
                    this.onChestedStatusChanged();
                    cir.setReturnValue(ActionResult.success(this.world.isClient));
                }
                if (!this.isBaby() && !this.isSaddled() && lv.isOf(Items.SADDLE)) {
                    this.openInventory(player);
                    cir.setReturnValue(ActionResult.success(this.world.isClient));
                }
            }
            if (this.isBaby()) {
                cir.setReturnValue(super.interactMob(player, hand));
            }
            this.putPlayerOnBack(player);
            cir.setReturnValue(ActionResult.success(this.world.isClient));
        }
    }
}
