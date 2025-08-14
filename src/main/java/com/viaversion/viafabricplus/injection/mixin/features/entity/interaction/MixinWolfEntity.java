/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.entity.interaction;

import com.viaversion.viafabricplus.features.entity.metadata_handling.WolfHealthTracker1_14_4;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntity.class)
public abstract class MixinWolfEntity extends TameableEntity implements Angerable {

    protected MixinWolfEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract DyeColor getCollarColor();

    @Shadow
    protected abstract void setCollarColor(DyeColor color);

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void fixWolfInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        final ItemStack itemStack = player.getStackInHand(hand);
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            final Item item = itemStack.getItem();
            if (this.isTamed()) {
                final FoodComponent foodComponent = itemStack.get(DataComponentTypes.FOOD);
                if (foodComponent != null) {
                    if (this.isBreedingItem(itemStack) && WolfHealthTracker1_14_4.getWolfHealth(this) < 20.0F) {
                        if (!player.getAbilities().creativeMode) itemStack.decrement(1);
                        this.heal(foodComponent.nutrition());
                        cir.setReturnValue(ActionResult.SUCCESS);
                        return;
                    }
                } else if (item instanceof DyeItem dyeItem) {
                    final DyeColor dyeColor = dyeItem.getColor();
                    if (dyeColor != this.getCollarColor()) {
                        this.setCollarColor(dyeColor);
                        if (!player.getAbilities().creativeMode) itemStack.decrement(1);
                        cir.setReturnValue(ActionResult.SUCCESS);
                        return;
                    }
                }
            } else if (item == Items.BONE && !this.hasAngerTime()) {
                if (!player.getAbilities().creativeMode) itemStack.decrement(1);
                cir.setReturnValue(ActionResult.SUCCESS);
                return;
            }

            cir.setReturnValue(super.interactMob(player, hand));
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_5) && this.isTamed()) {
            // Call armor shearing manually as removed in MixinEntity
            if (itemStack.isOf(Items.SHEARS)
                && this.isOwner(player)
                && this.isWearingBodyArmor()
                && (!EnchantmentHelper.hasAnyEnchantmentsWith(this.getBodyArmor(), EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE) || player.isCreative())) {
                this.playSoundIfNotSilent(SoundEvents.ITEM_ARMOR_UNEQUIP_WOLF);
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

}
