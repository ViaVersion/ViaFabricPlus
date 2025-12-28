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

package com.viaversion.viafabricplus.features.font;

import com.viaversion.nbt.tag.CompoundTag;
import com.viaversion.nbt.tag.IntTag;
import com.viaversion.nbt.tag.MixedListTag;
import com.viaversion.nbt.tag.Tag;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.impl.ViaFabricPlusMappingDataLoader;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Map;

import static com.viaversion.viaversion.api.protocol.version.ProtocolVersion.v1_20;

/**
 * Data dump which contains the {@link ProtocolVersion} for each renderable glyph. This is used to hide characters
 * which are not supported by the current protocol version. This is because some servers in older versions are using
 * characters which the client doesn't know about and therefore can't render as placeholder for e.g. scoreboards, we have
 * to hide them because our client would render them as a different character.
 */
public final class RenderableGlyphDiff {

    private static final Int2ObjectMap<ProtocolVersion> RENDERABLE_GLYPH_DIFF_LEGACY = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<ProtocolVersion> RENDERABLE_GLYPH_DIFF = new Int2ObjectOpenHashMap<>();

    public static void init() {
        final CompoundTag glyphs = ViaFabricPlusMappingDataLoader.INSTANCE.loadNBT("renderable-glyphs.nbt");
        fill(glyphs.getCompoundTag("legacy"), RENDERABLE_GLYPH_DIFF_LEGACY);
        fill(glyphs.getCompoundTag("unihex"), RENDERABLE_GLYPH_DIFF);
    }

    private static void fill(final CompoundTag glyphs, final Int2ObjectMap<ProtocolVersion> map) {
        for (final Map.Entry<String, Tag> entry : glyphs) {
            final ProtocolVersion version = ProtocolVersion.getClosest(entry.getKey());
            if (version == null) {
                throw new IllegalStateException("Unknown protocol version: " + entry.getKey());
            }

            final MixedListTag list = (MixedListTag) entry.getValue();
            for (final Tag i : list) {
                if (i instanceof final IntTag intTag) {
                    map.put(intTag.asInt(), version);
                } else if (i instanceof CompoundTag compoundTag) {
                    final int from = compoundTag.getInt("from");
                    final int to = compoundTag.getInt("to");
                    for (int j = from; j <= to; j++) {
                        map.put(j, version);
                    }
                }
            }
        }
    }

    /**
     * @param codePoint the code point to check
     * @return true if the given code point is renderable in the current version of the game
     */
    public static boolean isGlyphRenderable(final int codePoint) {
        final ProtocolVersion targetVersion = ProtocolTranslator.getTargetVersion();

        if (targetVersion.newerThanOrEqualTo(v1_20)) { // 1.20 switched to using Unihex as a main font
            return !RENDERABLE_GLYPH_DIFF.containsKey(codePoint) || targetVersion.newerThanOrEqualTo(RENDERABLE_GLYPH_DIFF.get(codePoint));
        } else {
            return RENDERABLE_GLYPH_DIFF_LEGACY.containsKey(codePoint) && targetVersion.newerThanOrEqualTo(RENDERABLE_GLYPH_DIFF_LEGACY.get(codePoint));
        }
    }

}
