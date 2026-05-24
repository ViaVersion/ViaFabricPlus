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

package com.viaversion.viafabricplus.injection.mixin.features.bedrock.raphi;

import com.mojang.blaze3d.platform.NativeImage;
import com.viaversion.viafabricplus.injection.access.raphi.IResourcePackStorage;
import com.viaversion.viafabricplus.util.BlockLoaderUtil;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.raphimc.viabedrock.api.resourcepack.ResourcePack;
import net.raphimc.viabedrock.api.resourcepack.content.Content;
import net.raphimc.viabedrock.protocol.storage.ResourcePackStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Mixin(value = ResourcePackStorage.class, remap = false)
public class MixinResourcePackStorage implements IResourcePackStorage {
    @Unique
    private final Map<String, String> ID_TO_TEXTURE = new HashMap<>();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void cacheTextures(List<ResourcePack> resourcePacksTopToBottom, CallbackInfo ci) {
        BlockLoaderUtil.STORAGE = (ResourcePackStorage)((Object)this);

        for (ResourcePack pack : resourcePacksTopToBottom) {
            if (!pack.content().contains("blocks.json")) {
                continue;
            }

            final JsonObject object = pack.content().getJson("blocks.json");

            for (String key : object.keySet()) {
                final JsonElement element = object.get(key);
                if (!element.isJsonPrimitive()) {
                    continue; // TODO: Support for this.
                }

                ID_TO_TEXTURE.put(key, element.getAsString());
            }
        }
    }

    @Override
    public String viaFabricPlus$textures(final String string) {
        return ID_TO_TEXTURE.get(string);
    }
}
