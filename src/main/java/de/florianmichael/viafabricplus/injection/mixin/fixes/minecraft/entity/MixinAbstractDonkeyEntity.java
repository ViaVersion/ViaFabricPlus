/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.entity;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
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
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_19_1)) {
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
