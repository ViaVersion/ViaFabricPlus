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

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.HolderSet;
import com.viaversion.viaversion.api.minecraft.data.StructuredDataContainer;
import com.viaversion.viaversion.api.minecraft.data.StructuredDataKey;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.item.data.ToolProperties;
import com.viaversion.viaversion.api.minecraft.item.data.ToolRule;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.v1_20_2to1_20_3.packet.ClientboundPacket1_20_3;
import com.viaversion.viaversion.protocols.v1_20_3to1_20_5.Protocol1_20_3To1_20_5;
import com.viaversion.viaversion.protocols.v1_20_3to1_20_5.packet.ServerboundPacket1_20_5;
import com.viaversion.viaversion.protocols.v1_20_3to1_20_5.rewriter.BlockItemPacketRewriter1_20_5;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import de.florianmichael.viafabricplus.event.PostViaVersionLoadCallback;
import de.florianmichael.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(value = BlockItemPacketRewriter1_20_5.class, remap = false)
public abstract class MixinBlockItemPacketRewriter1_20_5 extends ItemRewriter<ClientboundPacket1_20_3, ServerboundPacket1_20_5, Protocol1_20_3To1_20_5> {

    @Unique
    private final Set<String> viaFabricPlus$foodItems_b1_7_3 = new HashSet<>();

    @Unique
    private final Map<String, Integer> viaFabricPlus$armorMaxDamage_b1_8_1 = new HashMap<>();

    @Unique
    private final Map<ProtocolVersion, Map<String, ToolProperties>> viaFabricPlus$toolDataChanges = new LinkedHashMap<>();

    public MixinBlockItemPacketRewriter1_20_5(Protocol1_20_3To1_20_5 protocol, Type<Item> itemType, Type<Item[]> itemArrayType, Type<Item> mappedItemType, Type<Item[]> mappedItemArrayType) {
        super(protocol, itemType, itemArrayType, mappedItemType, mappedItemArrayType);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void loadItemMappings(Protocol1_20_3To1_20_5 protocol, CallbackInfo ci) {
        // Technically it would be cleaner to split mapping loading into there respective protocols, but that will be impossible
        // in a clean way, so let's just wait for Via* to load all protocols and then load everything in here.
        this.viaFabricPlus$foodItems_b1_7_3.add("minecraft:apple");
        this.viaFabricPlus$foodItems_b1_7_3.add("minecraft:mushroom_stew");
        this.viaFabricPlus$foodItems_b1_7_3.add("minecraft:bread");
        this.viaFabricPlus$foodItems_b1_7_3.add("minecraft:porkchop");
        this.viaFabricPlus$foodItems_b1_7_3.add("minecraft:cooked_porkchop");
        this.viaFabricPlus$foodItems_b1_7_3.add("minecraft:golden_apple");
        this.viaFabricPlus$foodItems_b1_7_3.add("minecraft:cod");
        this.viaFabricPlus$foodItems_b1_7_3.add("minecraft:cooked_cod");
        this.viaFabricPlus$foodItems_b1_7_3.add("minecraft:cookie");

        final JsonObject armorMaxDamages = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("armor-damages-b1.8.1.json");
        for (Map.Entry<String, JsonElement> entry : armorMaxDamages.entrySet()) {
            this.viaFabricPlus$armorMaxDamage_b1_8_1.put(entry.getKey(), entry.getValue().getAsInt());
        }

        PostViaVersionLoadCallback.EVENT.register(() -> {
            final JsonObject itemToolComponents = ViaFabricPlusMappingDataLoader.INSTANCE.loadData("item-tool-components.json");
            for (Map.Entry<String, JsonElement> entry : itemToolComponents.entrySet()) {
                final ProtocolVersion version = ProtocolVersion.getClosest(entry.getKey());
                if (version == null) {
                    throw new IllegalStateException("Unknown protocol version: " + entry.getKey());
                }
                final Map<String, ToolProperties> toolProperties = new HashMap<>();
                final JsonArray toolComponents = entry.getValue().getAsJsonArray();
                for (JsonElement toolComponent : toolComponents) {
                    final JsonObject toolComponentObject = toolComponent.getAsJsonObject();
                    final String item = toolComponentObject.get("item").getAsString();
                    final float defaultMiningSpeed = toolComponentObject.get("default_mining_speed").getAsFloat();
                    final int damagePerBlock = toolComponentObject.get("damage_per_block").getAsInt();
                    final int[] suitableFor = this.viaFabricPlus$blockJsonArrayToIds(version, toolComponentObject.getAsJsonArray("suitable_for"));
                    final List<ToolRule> toolRules = new ArrayList<>();
                    final JsonArray miningSpeeds = toolComponentObject.getAsJsonArray("mining_speeds");
                    for (JsonElement miningSpeed : miningSpeeds) {
                        final JsonObject miningSpeedObject = miningSpeed.getAsJsonObject();
                        final int[] blocks = this.viaFabricPlus$blockJsonArrayToIds(version, miningSpeedObject.getAsJsonArray("blocks"));
                        final float speed = miningSpeedObject.get("speed").getAsFloat();
                        toolRules.add(new ToolRule(HolderSet.of(blocks), speed, null));
                    }
                    if (suitableFor.length > 0) {
                        toolRules.add(new ToolRule(HolderSet.of(suitableFor), null, true));
                    }
                    toolProperties.put(item, new ToolProperties(toolRules.toArray(new ToolRule[0]), defaultMiningSpeed, damagePerBlock));
                }
                this.viaFabricPlus$toolDataChanges.put(version, toolProperties);
            }
        });
    }

    @Redirect(method = "appendItemDataFixComponents", at = @At(value = "INVOKE", target = "Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;olderThanOrEqualTo(Lcom/viaversion/viaversion/api/protocol/version/ProtocolVersion;)Z"))
    private boolean changeSwordFixVersionRange(ProtocolVersion instance, ProtocolVersion other) {
        if (other == ProtocolVersion.v1_8) {
            return instance.betweenInclusive(LegacyProtocolVersion.b1_8tob1_8_1, ProtocolVersion.v1_8);
        } else {
            return instance.olderThanOrEqualTo(other);
        }
    }

    // Older servers don't have these components, so we can use them to emulate old item behaviour without the need
    // of modifying tons of code in the game.
    @Inject(method = "appendItemDataFixComponents", at = @At("RETURN"))
    private void appendItemDataFixComponents(UserConnection user, Item item, CallbackInfo ci) {
        final StructuredDataContainer data = item.dataContainer();
        final String identifier = this.protocol.getMappingData().getFullItemMappings().identifier(item.identifier());

        // Fix durability tooltip displaying wrong
        if (user.getProtocolInfo().serverProtocolVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_8tob1_8_1)) {
            if (this.viaFabricPlus$armorMaxDamage_b1_8_1.containsKey(identifier)) {
                data.set(StructuredDataKey.MAX_DAMAGE, this.viaFabricPlus$armorMaxDamage_b1_8_1.get(identifier));
            }
        }

        // Fix item desyncs
        if (user.getProtocolInfo().serverProtocolVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_7tob1_7_3)) {
            if (this.viaFabricPlus$foodItems_b1_7_3.contains(identifier)) {
                data.set(StructuredDataKey.MAX_STACK_SIZE, 1);
                data.setEmpty(StructuredDataKey.FOOD1_20_5);
            }
        }

        // Tool data changes include mining speeds as well as suitable blocks and damage values
        for (Map.Entry<ProtocolVersion, Map<String, ToolProperties>> entry : this.viaFabricPlus$toolDataChanges.entrySet()) {
            if (user.getProtocolInfo().serverProtocolVersion().olderThanOrEqualTo(entry.getKey())) {
                final ToolProperties toolProperties = entry.getValue().get(identifier);
                if (toolProperties != null) {
                    data.set(StructuredDataKey.TOOL, toolProperties);
                    break;
                }
            }
        }
    }

    // Converts block identifiers as well as materials (prefixed with #) to block ids
    @Unique
    private int[] viaFabricPlus$blockJsonArrayToIds(final ProtocolVersion protocolVersion, final JsonArray jsonArray) {
        final IntSet ids = new IntOpenHashSet();
        for (final JsonElement element : jsonArray) {
            final String name = element.getAsString();
            if (name.startsWith("#")) { // Material name
                final String material = name.substring(1);
                for (Map.Entry<String, Map<ProtocolVersion, String>> entry : ViaFabricPlusMappingDataLoader.BLOCK_MATERIALS.entrySet()) {
                    for (Map.Entry<ProtocolVersion, String> materialEntry : entry.getValue().entrySet()) {
                        if (protocolVersion.olderThanOrEqualTo(materialEntry.getKey()) && materialEntry.getValue().equals(material)) {
                            ids.add(this.protocol.getMappingData().blockId(entry.getKey()));
                            break;
                        }
                    }
                }
            } else if (name.startsWith("-")) { // Block name
                ids.remove(this.protocol.getMappingData().blockId(name.substring(1)));
            } else { // Block name
                ids.add(this.protocol.getMappingData().blockId(name));
            }
        }
        return ids.toIntArray();
    }

}
