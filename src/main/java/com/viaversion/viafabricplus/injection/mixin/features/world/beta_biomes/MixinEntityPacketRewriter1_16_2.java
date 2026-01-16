/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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

package com.viaversion.viafabricplus.injection.mixin.features.world.beta_biomes;

import com.viaversion.nbt.tag.CompoundTag;
import com.viaversion.nbt.tag.ListTag;
import com.viaversion.viafabricplus.features.world.beta_biomes.BetaBiomeMapping;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_15_2to1_16.packet.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.Protocol1_16_1To1_16_2;
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.rewriter.EntityPacketRewriter1_16_2;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import com.viaversion.viaversion.util.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPacketRewriter1_16_2.class)
public abstract class MixinEntityPacketRewriter1_16_2 extends EntityRewriter<ClientboundPackets1_16, Protocol1_16_1To1_16_2> {

    @Unique
    private static final Map<String, BetaBiomeMapping> viaFabricPlus$betaMappings = new HashMap<>();

    static {
        viaFabricPlus$betaMappings.put("jungle", new BetaBiomeMapping(588342, 2094168)); // Rainforest
        viaFabricPlus$betaMappings.put("swamp", new BetaBiomeMapping(522674, 9154376)); // Swampland
        viaFabricPlus$betaMappings.put("forest", new BetaBiomeMapping(353825, 5159473)); // Forest
        viaFabricPlus$betaMappings.put("savanna", new BetaBiomeMapping(14278691)); // Savanna
        viaFabricPlus$betaMappings.put("modified_jungle_edge", new BetaBiomeMapping(10595616)); // Shrubland
        viaFabricPlus$betaMappings.put("taiga", new BetaBiomeMapping(3060051, 8107825)); // Taiga
        viaFabricPlus$betaMappings.put("desert", new BetaBiomeMapping(16421912)); // Desert
        viaFabricPlus$betaMappings.put("plains", new BetaBiomeMapping(16767248)); // Plains
        viaFabricPlus$betaMappings.put("ice_spikes", new BetaBiomeMapping(5762041, 12899129)); // Tundra
        viaFabricPlus$betaMappings.put("nether_wastes", new BetaBiomeMapping(16711680)); // Hell
        viaFabricPlus$betaMappings.put("the_end", new BetaBiomeMapping(8421631)); // The End
    }

    protected MixinEntityPacketRewriter1_16_2(final Protocol1_16_1To1_16_2 protocol) {
        super(protocol);
    }

    @Inject(method = "registerPackets", at = @At("TAIL"))
    private void rewriteBetaBiomes(final CallbackInfo ci) {
        protocol.appendClientbound(ClientboundPackets1_16.LOGIN, wrapper -> {
            if (ProtocolTranslator.getTargetVersion().olderThanOrEqualTo(LegacyProtocolVersion.b1_7tob1_7_3)) {
                viaFabricPlus$rewriteBiomes(wrapper.get(Types.NAMED_COMPOUND_TAG, 0));
            }
        });
    }

    @Unique
    private void viaFabricPlus$rewriteBiomes(final CompoundTag compoundTag) {
        final CompoundTag biomesTag = Objects.requireNonNull(compoundTag.getCompoundTag("minecraft:worldgen/biome"));
        final ListTag<CompoundTag> biomes = Objects.requireNonNull(biomesTag.getListTag("value", CompoundTag.class));
        for (final CompoundTag biomeTag : biomes) {
            final BetaBiomeMapping mapping = viaFabricPlus$betaMappings.get(Key.stripMinecraftNamespace(Objects.requireNonNull(biomeTag.getString("name"))));
            if (mapping != null) {
                viaFabricPlus$modifyBiome(mapping, Objects.requireNonNull(biomeTag.getCompoundTag("element")));
            }
        }
    }

    @Unique
    private void viaFabricPlus$modifyBiome(final BetaBiomeMapping mapping, final CompoundTag compoundTag) {
        // TODO: sky color/fog color/water color
        compoundTag.putFloat("temperature", mapping.temperature());
        compoundTag.putFloat("downfall", mapping.downfall());

        final CompoundTag effectsTag = Objects.requireNonNull(compoundTag.getCompoundTag("effects"));
        effectsTag.putInt("grass_color", mapping.baseColor());
        effectsTag.putInt("foliage_color", mapping.mutatedColor());
    }
}
