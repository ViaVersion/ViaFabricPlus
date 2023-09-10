/*
 * This file is part of ViaProxy - https://github.com/RaphiMC/ViaProxy
 * Copyright (C) 2023 RK_01/RaphiMC and contributors
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
package de.florianmichael.viafabricplus.definition.classic;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.types.Chunk1_17Type;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.raphimc.vialoader.util.VersionEnum;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.model.ClassicLevel;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.providers.ClassicWorldHeightProvider;
import net.raphimc.vialegacy.protocols.classic.protocola1_0_15toc0_28_30.storage.ClassicLevelStorage;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class ClassicWorldHeightInjection {

    public static PacketHandler handleJoinGame(final PacketHandler parentHandler) {
        return wrapper -> {
            parentHandler.handle(wrapper);
            if (wrapper.isCancelled()) return;

            if (ProtocolHack.getTargetVersion(wrapper.user().getChannel()).isOlderThanOrEqualTo(VersionEnum.c0_28toc0_30)) {
                for (Tag dimension : wrapper.get(Type.NBT, 0).<CompoundTag>get("minecraft:dimension_type").<ListTag>get("value")) {
                    changeDimensionTagHeight(wrapper.user(), ((CompoundTag) dimension).get("element"));
                }
                changeDimensionTagHeight(wrapper.user(), wrapper.get(Type.NBT, 1));
            }
        };
    }

    public static PacketHandler handleRespawn(final PacketHandler parentHandler) {
        return wrapper -> {
            parentHandler.handle(wrapper);
            if (wrapper.isCancelled()) return;

            if (ProtocolHack.getTargetVersion(wrapper.user().getChannel()).isOlderThanOrEqualTo(VersionEnum.c0_28toc0_30)) {
                changeDimensionTagHeight(wrapper.user(), wrapper.get(Type.NBT, 0));
            }
        };
    }

    public static PacketHandler handleChunkData(final PacketHandler parentHandler) {
        return wrapper -> {
            parentHandler.handle(wrapper);
            if (wrapper.isCancelled()) return;

            if (ProtocolHack.getTargetVersion(wrapper.user().getChannel()).isOlderThanOrEqualTo(VersionEnum.c0_28toc0_30)) {
                wrapper.resetReader();
                final Chunk chunk = wrapper.read(new Chunk1_17Type(16));
                wrapper.write(new Chunk1_17Type(chunk.getSections().length), chunk);

                final ClassicWorldHeightProvider heightProvider = Via.getManager().getProviders().get(ClassicWorldHeightProvider.class);
                if (chunk.getSections().length < heightProvider.getMaxChunkSectionCount(wrapper.user())) { // Increase available sections to match new world height
                    final ChunkSection[] newArray = new ChunkSection[heightProvider.getMaxChunkSectionCount(wrapper.user())];
                    System.arraycopy(chunk.getSections(), 0, newArray, 0, chunk.getSections().length);
                    chunk.setSections(newArray);
                }

                final BitSet chunkMask = new BitSet();
                for (int i = 0; i < chunk.getSections().length; i++) {
                    if (chunk.getSections()[i] != null) chunkMask.set(i);
                }
                chunk.setChunkMask(chunkMask);

                final int[] newBiomeData = new int[chunk.getSections().length * 4 * 4 * 4];
                System.arraycopy(chunk.getBiomeData(), 0, newBiomeData, 0, chunk.getBiomeData().length);
                for (int i = 64; i < chunk.getSections().length * 4; i++) { // copy top layer of old biome data all the way to max world height
                    System.arraycopy(chunk.getBiomeData(), chunk.getBiomeData().length - 16, newBiomeData, i * 16, 16);
                }
                chunk.setBiomeData(newBiomeData);

                chunk.setHeightMap(new CompoundTag()); // rip heightmap :(
            }
        };
    }

    public static PacketHandler handleUpdateLight(final PacketHandler parentHandler) {
        final PacketHandler classicLightHandler = new PacketHandlers() {
            @Override
            public void register() {
                map(Type.VAR_INT); // x
                map(Type.VAR_INT); // y
                map(Type.BOOLEAN); // trust edges
                handler(wrapper -> {
                    wrapper.read(Type.VAR_INT); // sky light mask
                    wrapper.read(Type.VAR_INT); // block light mask
                    final int emptySkyLightMask = wrapper.read(Type.VAR_INT); // empty sky light mask
                    final int emptyBlockLightMask = wrapper.read(Type.VAR_INT); // empty block light mask

                    final ClassicLevel level = wrapper.user().get(ClassicLevelStorage.class).getClassicLevel();
                    final ClassicWorldHeightProvider heightProvider = Via.getManager().getProviders().get(ClassicWorldHeightProvider.class);

                    int sectionYCount = level.getSizeY() >> 4;
                    if (level.getSizeY() % 16 != 0) sectionYCount++;
                    if (sectionYCount > heightProvider.getMaxChunkSectionCount(wrapper.user())) {
                        sectionYCount = heightProvider.getMaxChunkSectionCount(wrapper.user());
                    }

                    final List<byte[]> lightArrays = new ArrayList<>();
                    while (wrapper.isReadable(Type.BYTE_ARRAY_PRIMITIVE, 0)) {
                        lightArrays.add(wrapper.read(Type.BYTE_ARRAY_PRIMITIVE));
                    }

                    int skyLightCount = 16;
                    int blockLightCount = sectionYCount;
                    if (lightArrays.size() == 16 + 0 + 2) {
                        blockLightCount = 0;
                    } else if (lightArrays.size() == 16 + sectionYCount + 2) {
                    } else if (lightArrays.size() == sectionYCount + sectionYCount + 2) {
                        skyLightCount = sectionYCount;
                    }
                    skyLightCount += 2; // Chunk below 0 and above 255

                    final BitSet skyLightMask = new BitSet();
                    final BitSet blockLightMask = new BitSet();
                    skyLightMask.set(0, skyLightCount);
                    blockLightMask.set(0, blockLightCount);

                    wrapper.write(Type.LONG_ARRAY_PRIMITIVE, skyLightMask.toLongArray());
                    wrapper.write(Type.LONG_ARRAY_PRIMITIVE, blockLightMask.toLongArray());
                    wrapper.write(Type.LONG_ARRAY_PRIMITIVE, new long[emptySkyLightMask]);
                    wrapper.write(Type.LONG_ARRAY_PRIMITIVE, new long[emptyBlockLightMask]);

                    wrapper.write(Type.VAR_INT, skyLightCount);
                    for (int i = 0; i < skyLightCount; i++) {
                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, lightArrays.remove(0));
                    }
                    wrapper.write(Type.VAR_INT, blockLightCount);
                    for (int i = 0; i < blockLightCount; i++) {
                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, lightArrays.remove(0));
                    }
                });
            }
        };

        return wrapper -> {
            if (ProtocolHack.getTargetVersion(wrapper.user().getChannel()).isOlderThanOrEqualTo(VersionEnum.c0_28toc0_30)) {
                classicLightHandler.handle(wrapper);
            } else {
                parentHandler.handle(wrapper);
            }
        };
    }

    private static void changeDimensionTagHeight(final UserConnection user, final CompoundTag tag) {
        tag.put("height", new IntTag(Via.getManager().getProviders().get(ClassicWorldHeightProvider.class).getMaxChunkSectionCount(user) << 4));
    }
}
