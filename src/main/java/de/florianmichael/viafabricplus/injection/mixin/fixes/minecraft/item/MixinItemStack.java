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
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TooltipAppender;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract ComponentMap getComponents();

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    private <T extends TooltipAppender> void replaceEnchantmentTooltip(DataComponentType<T> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_14_4)) {
            return;
        }
        // Via 1.20.5->.3 will always put the original item data into CUSTOM_DATA, so we can assume its present.
        final NbtCompound customData = this.getComponents().get(DataComponentTypes.CUSTOM_DATA).getNbt();

        if (componentType == DataComponentTypes.ENCHANTMENTS) {
            this.viaFabricPlus$appendEnchantments1_14_4("Enchantments", customData, textConsumer);
            ci.cancel();
        } else if (componentType == DataComponentTypes.STORED_ENCHANTMENTS) {
            this.viaFabricPlus$appendEnchantments1_14_4("StoredEnchantments", customData, textConsumer);
            ci.cancel();
        }
    }

    @Unique
    private void viaFabricPlus$appendEnchantments1_14_4(final String name, final NbtCompound nbt, final Consumer<Text> tooltip) {
        final NbtList enchantments = nbt.getList(name, NbtElement.COMPOUND_TYPE);
        for (NbtElement element : enchantments) {
            final NbtCompound enchantment = (NbtCompound) element;
            final String id = enchantment.getString("id");

            final Optional<Enchantment> value = Registries.ENCHANTMENT.getOrEmpty(Identifier.tryParse(id));
            value.ifPresent(e -> {
                final int lvl = enchantment.getInt("lvl");
                tooltip.accept(e.getName(MathHelper.clamp(lvl, Short.MIN_VALUE, Short.MAX_VALUE)));
            });
        }
    }

}
