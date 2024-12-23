/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.old.viaversion;

import com.viaversion.nbt.tag.CompoundTag;
import com.viaversion.nbt.tag.ListTag;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.item.data.AttributeModifiers1_20_5;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.v1_8to1_9.Protocol1_8To1_9;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.v1_8to1_9.packet.ServerboundPackets1_9;
import com.viaversion.viaversion.protocols.v1_8to1_9.rewriter.ItemPacketRewriter1_9;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import com.viaversion.viaversion.util.Pair;
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(value = ItemPacketRewriter1_9.class, remap = false)
public abstract class MixinItemPacketRewriter1_9 extends ItemRewriter<ClientboundPackets1_8, ServerboundPackets1_9, Protocol1_8To1_9> {

    @Unique
    private final Int2ObjectMap<String> viaFabricPlus$itemIdentifiers = new Int2ObjectOpenHashMap<>();

    @Unique
    private final Map<String, Map<String, Pair<String, AttributeModifiers1_20_5.ModifierData>>> viaFabricPlus$itemAttributes = new HashMap<>();

    public MixinItemPacketRewriter1_9(Protocol1_8To1_9 protocol, Type<Item> itemType, Type<Item[]> itemArrayType, Type<Item> mappedItemType, Type<Item[]> mappedItemArrayType) {
        super(protocol, itemType, itemArrayType, mappedItemType, mappedItemArrayType);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void loadAdditionalData(CallbackInfo ci) {
        final JsonObject itemIdentifiers = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("item-identifiers-1.8.json");
        for (Map.Entry<String, JsonElement> entry : itemIdentifiers.entrySet()) {
            viaFabricPlus$itemIdentifiers.put(entry.getValue().getAsInt(), entry.getKey());
        }

        final JsonObject itemAttributes = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("item-attributes-1.8.json");
        for (Map.Entry<String, JsonElement> itemEntry : itemAttributes.entrySet()) {
            final String itemIdentifier = itemEntry.getKey();
            final Map<String, Pair<String, AttributeModifiers1_20_5.ModifierData>> attributes = new HashMap<>();
            for (Map.Entry<String, JsonElement> attributeEntry : itemEntry.getValue().getAsJsonObject().entrySet()) {
                final String attribute = attributeEntry.getKey();
                final JsonObject attributeData = attributeEntry.getValue().getAsJsonObject();
                final AttributeModifiers1_20_5.ModifierData modifierData = new AttributeModifiers1_20_5.ModifierData(UUID.fromString(attributeData.get("id").getAsString()), attributeData.get("name").getAsString(), attributeData.get("amount").getAsDouble(), attributeData.get("operation").getAsInt());
                final String slot = attributeData.get("slot").getAsString();
                attributes.put(attribute, new Pair<>(slot, modifierData));
            }
            viaFabricPlus$itemAttributes.put(itemIdentifier, attributes);
        }
    }

    @Inject(method = "handleItemToClient", at = @At("RETURN"))
    private void addAttributeFixData(CallbackInfoReturnable<Item> cir) {
        final Item item = cir.getReturnValue();
        if (item == null) {
            return;
        }

        final String identifier = viaFabricPlus$itemIdentifiers.get(item.identifier());
        if (identifier != null && viaFabricPlus$itemAttributes.containsKey(identifier)) {
            final Map<String, Pair<String, AttributeModifiers1_20_5.ModifierData>> attributes = viaFabricPlus$itemAttributes.get(identifier);
            final CompoundTag attributeFixTag = new CompoundTag();
            CompoundTag tag = item.tag();
            if (tag == null) {
                tag = new CompoundTag();
                item.setTag(tag);
                attributeFixTag.putBoolean("RemoveTag", true);
            }
            tag.put(nbtTagName("attributeFix"), attributeFixTag);

            ListTag<CompoundTag> attributeModifiers = tag.getListTag("AttributeModifiers", CompoundTag.class);
            if (attributeModifiers == null) {
                attributeModifiers = new ListTag<>(CompoundTag.class);
                for (Map.Entry<String, Pair<String, AttributeModifiers1_20_5.ModifierData>> entry : attributes.entrySet()) {
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

    @Inject(method = "handleItemToServer", at = @At("RETURN"))
    private void removeAttributeFixData(CallbackInfoReturnable<Item> cir) {
        final Item item = cir.getReturnValue();
        if (item == null) {
            return;
        }
        final CompoundTag tag = item.tag();
        if (tag == null) {
            return;
        }
        final CompoundTag attributeFixTag = tag.removeUnchecked(nbtTagName("attributeFix"));
        if (attributeFixTag == null) {
            return;
        }

        if (attributeFixTag.contains("RemoveAttributeModifiers")) {
            tag.remove("AttributeModifiers");
        }
        if (attributeFixTag.contains("RemoveTag")) {
            item.setTag(null);
        }
    }

}
