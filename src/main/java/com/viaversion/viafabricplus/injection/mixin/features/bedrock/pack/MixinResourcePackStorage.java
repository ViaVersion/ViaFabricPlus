/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
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

package com.viaversion.viafabricplus.injection.mixin.features.bedrock.pack;

import com.viaversion.viafabricplus.features.block.bedrock.dynamic.DynamicBlockCache;
import com.viaversion.viafabricplus.injection.access.bedrock.pack.IResourcePackStorage;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import net.raphimc.viabedrock.api.resourcepack.ResourcePack;
import net.raphimc.viabedrock.protocol.storage.ResourcePackStorage;
import org.cube.converter.util.element.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(value = ResourcePackStorage.class, remap = false)
public class MixinResourcePackStorage implements IResourcePackStorage {
    @Unique
    private final Map<String, EnumMap<Direction, String>> ID_TO_TEXTURE_MAP = new HashMap<>();

    @Unique
    private final Map<String, String> ID_TO_TEXTURE_PATH = new HashMap<>();

    @Unique
    private final Set<String> TEXTURES_TO_LOAD = new HashSet<>();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void cacheTextures(List<ResourcePack> resourcePacksTopToBottom, CallbackInfo ci) {
        DynamicBlockCache.STORAGE_INSTANCE = (ResourcePackStorage)((Object)this);

        for (ResourcePack pack : resourcePacksTopToBottom) {
            if (!pack.content().contains("blocks.json")) {
                continue;
            }

            final JsonObject object = pack.content().getJson("blocks.json");

            for (String key : object.keySet()) {
                final JsonElement element = object.get(key);
                if (!element.isJsonObject()) {
                    continue;
                }
                final JsonObject jsonObject = element.getAsJsonObject();
                final JsonElement textures = jsonObject.get("textures");
                if (textures instanceof JsonPrimitive primitive) {
                    final EnumMap<Direction, String> map = new EnumMap<>(Direction.class);

                    for (Direction direction : Direction.values()) {
                        map.put(direction, primitive.getAsString());
                    }
                    ID_TO_TEXTURE_MAP.put(key, map);
                } else if (textures instanceof JsonObject texturesObject) {
                    final EnumMap<Direction, String> map = new EnumMap<>(Direction.class);

                    for (Direction direction : Direction.values()) {
                        String name = direction.name().toLowerCase();
                        if (texturesObject.has(name)) {
                            map.put(direction, texturesObject.getAsJsonPrimitive(name).getAsString());
                        } else {
                            map.put(direction, "empty");
                        }
                    }
                    ID_TO_TEXTURE_MAP.put(key, map);
                }
            }
        }

        for (ResourcePack pack : resourcePacksTopToBottom) {
            if (!pack.content().contains("textures/terrain_texture.json")) {
                continue;
            }

            final JsonObject object = pack.content().getJson("textures/terrain_texture.json");
            if (!object.has("texture_data")) {
                continue;
            }

            final JsonObject textureData = object.getAsJsonObject("texture_data");
            for (String key : textureData.keySet()) {
                final JsonElement element = textureData.get(key);
                if (!element.isJsonObject()) {
                    continue;
                }
                final JsonElement textures = element.getAsJsonObject().get("textures");
                if (!textures.isJsonPrimitive() && !textures.isJsonArray()) {
                    continue;
                }

                ID_TO_TEXTURE_PATH.put(key, textures.getAsString());
                TEXTURES_TO_LOAD.add(textures.getAsString());
            }
        }
    }

    @Override
    public EnumMap<Direction, String> viaFabricPlus$textures(final String string) {
        if (!ID_TO_TEXTURE_MAP.containsKey(string)) {
            return null;
        }

        final EnumMap<Direction, String> map = new EnumMap<>(Direction.class);
        for (Map.Entry<Direction, String> entry : ID_TO_TEXTURE_MAP.get(string).entrySet()) {
            String path = ID_TO_TEXTURE_PATH.get(entry.getValue());
            map.put(entry.getKey(), "viabedrock:block/" + (path == null ? entry.getValue() : path));
        }
        return map;
    }

    @Override
    public String viaFabricPlus$texturesPathFromId(final String string) {
        return "viabedrock:block/" +  ID_TO_TEXTURE_PATH.get(string);
    }

    @Override
    public Set<String> viaFabricPlus$texturesToLoad() {
        return TEXTURES_TO_LOAD;
    }
}
