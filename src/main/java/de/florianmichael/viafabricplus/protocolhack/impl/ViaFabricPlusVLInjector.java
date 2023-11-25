/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.protocolhack.impl;

import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.raphimc.vialoader.impl.viaversion.VLInjector;
import net.raphimc.vialoader.netty.VLLegacyPipeline;

public class ViaFabricPlusVLInjector extends VLInjector {

    @Override
    public String getEncoderName() {
        return VLLegacyPipeline.VIA_ENCODER_NAME;
    }

    @Override
    public String getDecoderName() {
        return VLLegacyPipeline.VIA_DECODER_NAME;
    }

    @Override
    public JsonObject getDump() {
        JsonObject platformSpecific = new JsonObject();
        JsonArray mods = new JsonArray();
        FabricLoader.getInstance().getAllMods().stream().map((mod) -> {
            JsonObject jsonMod = new JsonObject();
            jsonMod.addProperty("id", mod.getMetadata().getId());
            jsonMod.addProperty("name", mod.getMetadata().getName());
            jsonMod.addProperty("version", mod.getMetadata().getVersion().getFriendlyString());
            JsonArray authors = new JsonArray();
            mod.getMetadata().getAuthors().stream().map(it -> {
                JsonObject info = new JsonObject();
                JsonObject contact = new JsonObject();
                it.getContact().asMap().forEach(contact::addProperty);

                if (contact.size() != 0) info.add("contact", contact);
                info.addProperty("name", it.getName());

                return info;
            }).forEach(authors::add);
            jsonMod.add("authors", authors);

            return jsonMod;
        }).forEach(mods::add);

        platformSpecific.add("mods", mods);
        platformSpecific.addProperty("native version", SharedConstants.getProtocolVersion());

        return platformSpecific;
    }
}
