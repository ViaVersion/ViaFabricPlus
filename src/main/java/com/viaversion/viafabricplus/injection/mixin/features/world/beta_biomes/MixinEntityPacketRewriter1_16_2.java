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
import com.viaversion.viafabricplus.features.world.beta_biomes.BetaBiomeColorMapping;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_15_2to1_16.packet.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.Protocol1_16_1To1_16_2;
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.rewriter.EntityPacketRewriter1_16_2;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import java.util.Objects;
import net.minecraft.resources.Identifier;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPacketRewriter1_16_2.class)
public abstract class MixinEntityPacketRewriter1_16_2 extends EntityRewriter<ClientboundPackets1_16, Protocol1_16_1To1_16_2> {

    @Unique
    private static final BetaBiomeColorMapping[] viaFabricPlus$betaMappings = new BetaBiomeColorMapping[]{
        new BetaBiomeColorMapping(Identifier.withDefaultNamespace("jungle"), 588342, 2094168), // Rainforest
        new BetaBiomeColorMapping(Identifier.withDefaultNamespace("swamp"), 522674, 9154376), // Swampland
        new BetaBiomeColorMapping(Identifier.withDefaultNamespace("forest"), 353825, 5159473), // Forest
        new BetaBiomeColorMapping(Identifier.withDefaultNamespace("savanna"), 14278691), // Savanna
        new BetaBiomeColorMapping(Identifier.withDefaultNamespace("modified_jungle_edge"), 10595616), // Shrubland
        new BetaBiomeColorMapping(Identifier.withDefaultNamespace("taiga"), 3060051, 8107825), // Taiga
        new BetaBiomeColorMapping(Identifier.withDefaultNamespace("desert"), 16421912), // Desert
        new BetaBiomeColorMapping(Identifier.withDefaultNamespace("plains"), 16767248), // Plains
        new BetaBiomeColorMapping(Identifier.withDefaultNamespace("ice_spikes"), 5762041, 12899129), // Tundra
        new BetaBiomeColorMapping(Identifier.withDefaultNamespace("nether_wastes"), 16711680), // Hell
        new BetaBiomeColorMapping(Identifier.withDefaultNamespace("the_end"), 8421631), // The End
    };

    protected MixinEntityPacketRewriter1_16_2(final Protocol1_16_1To1_16_2 protocol) {
        super(protocol);
    }

    @Inject(method = "registerPackets", at = @At("TAIL"))
    private void rewriteBetaBiomes(final CallbackInfo ci) {
        protocol.appendClientbound(ClientboundPackets1_16.LOGIN, wrapper -> {
            wrapper.set(Types.NAMED_COMPOUND_TAG, 0, viaFabricPlus$rewriteBiomes(wrapper.get(Types.NAMED_COMPOUND_TAG, 0)));
        });
    }

    @Unique
    private CompoundTag viaFabricPlus$rewriteBiomes(final CompoundTag compoundTag) {
        if (ProtocolTranslator.getTargetVersion().newerThan(LegacyProtocolVersion.b1_7tob1_7_3)) {
            return compoundTag; // No need to rewrite
        }

        final CompoundTag biomesTag = Objects.requireNonNull(compoundTag.getCompoundTag("minecraft:worldgen/biome"));
        for (final CompoundTag biomeTag : Objects.requireNonNull(biomesTag.getListTag("value", CompoundTag.class))) {
            final String name = biomeTag.getString("name");
            for (final BetaBiomeColorMapping mapping : viaFabricPlus$betaMappings) {
                if (mapping.id().equals(name)) {

                }
            }
        }

        return compoundTag;
    }

}
