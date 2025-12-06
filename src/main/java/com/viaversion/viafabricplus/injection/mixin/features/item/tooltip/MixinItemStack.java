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

package com.viaversion.viafabricplus.injection.mixin.features.item.tooltip;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.viaversion.viafabricplus.features.item.r1_14_4_enchantment_tooltip.Enchantments1_14_4;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.util.ItemUtil;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.Protocol1_21_4To1_21_5;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow
    public abstract Item getItem();

    @WrapWithCondition(method = "addDetailsToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V"))
    private boolean hideAdditionalTooltip(Item instance, ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            final CompoundTag tag = ItemUtil.getTagOrNull((ItemStack) (Object) this);
            final CompoundTag backup = tag == null ? null : tag.getCompoundOrEmpty(ItemUtil.vvNbtName(Protocol1_21_4To1_21_5.class, "backup"));
            return backup == null || !backup.contains("hide_additional_tooltip");
        } else {
            return true;
        }
    }

    @Inject(method = "addToTooltip", at = @At("HEAD"), cancellable = true)
    private <T extends TooltipProvider> void replaceEnchantmentTooltip(DataComponentType<T> componentType, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_14_4)) {
            return;
        }

        final CompoundTag tag = ItemUtil.getTagOrNull((ItemStack) (Object) this);
        if (tag == null) {
            return;
        }
        if (componentType == DataComponents.ENCHANTMENTS) {
            this.viaFabricPlus$appendEnchantments1_14_4("Enchantments", tag, context, textConsumer);
            ci.cancel();
        } else if (componentType == DataComponents.STORED_ENCHANTMENTS) {
            this.viaFabricPlus$appendEnchantments1_14_4("StoredEnchantments", tag, context, textConsumer);
            ci.cancel();
        }
    }

    @Unique
    private void viaFabricPlus$appendEnchantments1_14_4(final String name, final CompoundTag nbt, Item.TooltipContext context, final Consumer<Component> tooltip) {
        final HolderLookup.Provider registryLookup = context.registries();
        final ListTag enchantments = nbt.getList(name).orElse(null);
        if (enchantments == null) {
            return;
        }

        for (Tag element : enchantments) {
            final CompoundTag enchantment = (CompoundTag) element;

            final String id = enchantment.getStringOr("id", "");
            final Optional<ResourceKey<Enchantment>> value = Enchantments1_14_4.getOrEmpty(id);
            value.ifPresent(e -> {
                final int lvl = enchantment.getIntOr("lvl", 0);
                if (registryLookup != null) {
                    final Optional<Holder.Reference<Enchantment>> v = registryLookup.lookupOrThrow(Registries.ENCHANTMENT).get(e);
                    v.ifPresent(enchantmentReference -> tooltip.accept(Enchantment.getFullname(enchantmentReference, Mth.clamp(lvl, Short.MIN_VALUE, Short.MAX_VALUE))));
                }
            });
        }
    }

}
