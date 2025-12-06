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
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public abstract class MixinWolfEntity extends TamableAnimal implements NeutralMob {

    protected MixinWolfEntity(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }

    @Shadow
    public abstract DyeColor getCollarColor();

    @Shadow
    protected abstract void setCollarColor(DyeColor color);

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void fixWolfInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        final ItemStack itemStack = player.getItemInHand(hand);
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_14_4)) {
            final Item item = itemStack.getItem();
            if (this.isTame()) {
                final FoodProperties foodComponent = itemStack.get(DataComponents.FOOD);
                if (foodComponent != null) {
                    if (this.isFood(itemStack) && WolfHealthTracker1_14_4.getWolfHealth(this) < 20.0F) {
                        if (!player.getAbilities().instabuild) itemStack.shrink(1);
                        this.heal(foodComponent.nutrition());
                        cir.setReturnValue(InteractionResult.SUCCESS);
                        return;
                    }
                } else if (item instanceof DyeItem dyeItem) {
                    final DyeColor dyeColor = dyeItem.getDyeColor();
                    if (dyeColor != this.getCollarColor()) {
                        this.setCollarColor(dyeColor);
                        if (!player.getAbilities().instabuild) itemStack.shrink(1);
                        cir.setReturnValue(InteractionResult.SUCCESS);
                        return;
                    }
                }
            } else if (item == Items.BONE && !this.isAngry()) {
                if (!player.getAbilities().instabuild) itemStack.shrink(1);
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }

            cir.setReturnValue(super.mobInteract(player, hand));
        } else if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_5) && this.isTame()) {
            // Call armor shearing manually as removed in MixinEntity
            if (itemStack.is(Items.SHEARS)
                && this.isOwnedBy(player)
                && this.isWearingBodyArmor()
                && (!EnchantmentHelper.has(this.getBodyArmorItem(), EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) || player.isCreative())) {
                this.playSound(SoundEvents.ARMOR_UNEQUIP_WOLF);
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

}
