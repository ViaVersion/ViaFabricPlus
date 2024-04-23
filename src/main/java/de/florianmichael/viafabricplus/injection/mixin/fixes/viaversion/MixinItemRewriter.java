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

package de.florianmichael.viafabricplus.injection.mixin.fixes.viaversion;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.item.data.ModifierData;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import com.viaversion.viaversion.util.Pair;
import de.florianmichael.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(value = ItemRewriter.class, remap = false)
public abstract class MixinItemRewriter {

    @Unique
    private static final Int2ObjectMap<String> ITEM_IDENTIFIERS = new Int2ObjectOpenHashMap<>();

    @Unique
    private static final Map<String, Map<String, Pair<String, ModifierData>>> ITEM_ATTRIBUTES = new HashMap<>();

    @Unique
    private static final String TAG_NAME = "VV|AttributeFix";

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void loadAdditionalData(CallbackInfo ci) {
        final JsonObject itemIdentifiers = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("item-identifiers-1.8.json");
        for (Map.Entry<String, JsonElement> entry : itemIdentifiers.entrySet()) {
            ITEM_IDENTIFIERS.put(entry.getValue().getAsInt(), entry.getKey());
        }

        final JsonObject itemAttributes = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("item-attributes-1.8.json");
        for (Map.Entry<String, JsonElement> itemEntry : itemAttributes.entrySet()) {
            final String itemIdentifier = itemEntry.getKey();
            final Map<String, Pair<String, ModifierData>> attributes = new HashMap<>();
            for (Map.Entry<String, JsonElement> attributeEntry : itemEntry.getValue().getAsJsonObject().entrySet()) {
                final String attribute = attributeEntry.getKey();
                final JsonObject attributeData = attributeEntry.getValue().getAsJsonObject();
                final ModifierData modifierData = new ModifierData(UUID.fromString(attributeData.get("id").getAsString()), attributeData.get("name").getAsString(), attributeData.get("amount").getAsDouble(), attributeData.get("operation").getAsInt());
                final String slot = attributeData.get("slot").getAsString();
                attributes.put(attribute, new Pair<>(slot, modifierData));
            }
            ITEM_ATTRIBUTES.put(itemIdentifier, attributes);
        }
    }

    @Inject(method = "toClient", at = @At("RETURN"))
    private static void addAttributeFixData(Item item, CallbackInfo ci) {
        if (item == null) return;

        final String identifier = ITEM_IDENTIFIERS.get(item.identifier());
        if (identifier != null && ITEM_ATTRIBUTES.containsKey(identifier)) {
            final Map<String, Pair<String, ModifierData>> attributes = ITEM_ATTRIBUTES.get(identifier);
            final CompoundTag attributeFixTag = new CompoundTag();
            CompoundTag tag = item.tag();
            if (tag == null) {
                tag = new CompoundTag();
                item.setTag(tag);
                attributeFixTag.putBoolean("RemoveTag", true);
            }
            tag.put(TAG_NAME, attributeFixTag);

            ListTag<CompoundTag> attributeModifiers = tag.getListTag("AttributeModifiers", CompoundTag.class);
            if (attributeModifiers == null) {
                attributeModifiers = new ListTag<>(CompoundTag.class);
                for (Map.Entry<String, Pair<String, ModifierData>> entry : attributes.entrySet()) {
                    final CompoundTag attributeModifier = new CompoundTag();
                    attributeModifier.putString("AttributeName", entry.getKey());
                    attributeModifier.putString("Name", entry.getValue().value().name());
                    attributeModifier.putDouble("Amount", entry.getValue().value().amount());
                    attributeModifier.putInt("Operation", entry.getValue().value().operation());
                    attributeModifier.putLong("UUIDMost", entry.getValue().value().uuid().getMostSignificantBits());
                    attributeModifier.putLong("UUIDLeast", entry.getValue().value().uuid().getLeastSignificantBits());
                    attributeModifier.putString("Slot", entry.getValue().key());
                    attributeModifiers.add(attributeModifier);
                }
                tag.put("AttributeModifiers", attributeModifiers);
                attributeFixTag.putBoolean("RemoveAttributeModifiers", true);
            }
        }
    }

    @Inject(method = "toServer", at = @At("RETURN"))
    private static void removeAttributeFixData(Item item, CallbackInfo ci) {
        if (item == null) return;
        final CompoundTag tag = item.tag();
        if (tag == null) return;
        final CompoundTag attributeFixTag = tag.removeUnchecked(TAG_NAME);
        if (attributeFixTag == null) return;

        if (attributeFixTag.contains("RemoveAttributeModifiers")) {
            tag.remove("AttributeModifiers");
        }
        if (attributeFixTag.contains("RemoveTag")) {
            item.setTag(null);
        }
    }

}
