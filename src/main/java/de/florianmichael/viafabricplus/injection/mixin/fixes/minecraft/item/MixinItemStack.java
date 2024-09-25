/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.item;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.fixes.versioned.Enchantments1_14_4;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.florianmichael.viafabricplus.util.ItemUtil;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow
    public abstract Item getItem();

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    private <T extends TooltipAppender> void replaceEnchantmentTooltip(ComponentType<T> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_14_4)) {
            return;
        }

        final NbtCompound tag = ItemUtil.getTagOrNull((ItemStack) (Object) this);
        if (tag == null) {
            return;
        }
        if (componentType == DataComponentTypes.ENCHANTMENTS) {
            this.viaFabricPlus$appendEnchantments1_14_4("Enchantments", tag, context, textConsumer);
            ci.cancel();
        } else if (componentType == DataComponentTypes.STORED_ENCHANTMENTS) {
            this.viaFabricPlus$appendEnchantments1_14_4("StoredEnchantments", tag, context, textConsumer);
            ci.cancel();
        }
    }

    @Redirect(method = "appendAttributeModifierTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeBaseValue(Lnet/minecraft/registry/entry/RegistryEntry;)D", ordinal = 0))
    private double fixAttackDamageCalculation(PlayerEntity instance, RegistryEntry<EntityAttribute> registryEntry) {
        double value = 0.0;
        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_20_5)) {
            final ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments((ItemStack) (Object) this);
            for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
                if (enchantment.matchesKey(Enchantments.SHARPNESS)) {
                    final int level = enchantments.getLevel(enchantment);
                    if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
                        value = level * 1.25F;
                    } else {
                        value = 1.0F + (float) Math.max(0, level - 1) * 0.5F;
                    }
                    break;
                }
            }
        }

        if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_8)) {
            return value;
        } else {
            return instance.getAttributeBaseValue(registryEntry) + value;
        }
    }

    @Unique
    private void viaFabricPlus$appendEnchantments1_14_4(final String name, final NbtCompound nbt, Item.TooltipContext context, final Consumer<Text> tooltip) {
        final RegistryWrapper.WrapperLookup registryLookup = context.getRegistryLookup();
        final NbtList enchantments = nbt.getList(name, NbtElement.COMPOUND_TYPE);
        for (NbtElement element : enchantments) {
            final NbtCompound enchantment = (NbtCompound) element;

            final String id = enchantment.getString("id");
            final Optional<RegistryKey<Enchantment>> value = Enchantments1_14_4.getOrEmpty(id);
            value.ifPresent(e -> {
                final int lvl = enchantment.getInt("lvl");
                if (registryLookup != null) {
                    final Optional<RegistryEntry.Reference<Enchantment>> v = registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOptional(e);
                    v.ifPresent(enchantmentReference -> tooltip.accept(Enchantment.getName(enchantmentReference, MathHelper.clamp(lvl, Short.MIN_VALUE, Short.MAX_VALUE))));
                }
            });
        }
    }

}
