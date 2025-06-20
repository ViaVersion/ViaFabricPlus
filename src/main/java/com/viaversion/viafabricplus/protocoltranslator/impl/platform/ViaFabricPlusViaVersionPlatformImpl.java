/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.protocoltranslator.impl.platform;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.impl.viaversion.ViaFabricPlusVLViaConfig;
import com.viaversion.vialoader.impl.platform.ViaVersionPlatformImpl;
import com.viaversion.viaversion.configuration.AbstractViaConfig;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public final class ViaFabricPlusViaVersionPlatformImpl extends ViaVersionPlatformImpl {

    public ViaFabricPlusViaVersionPlatformImpl(File rootFolder) { // Only required to not throw an exception, not used
        super(rootFolder);
    }

    @Override
    public String getPlatformName() {
        return "ViaFabricPlus";
    }

    @Override
    public String getPlatformVersion() {
        return ViaFabricPlusImpl.INSTANCE.getVersion();
    }

    @Override
    protected AbstractViaConfig createConfig() {
        // Use config overload and change directory to root folder
        return new ViaFabricPlusVLViaConfig(new File(getDataFolder(), "viaversion.yml"), this.getLogger());
    }

    @Override
    public JsonObject getDump() {
        final JsonObject platformDump = new JsonObject();
        platformDump.addProperty("impl_version", ViaFabricPlusImpl.INSTANCE.getImplVersion());
        platformDump.addProperty("native_version", ProtocolTranslator.NATIVE_VERSION.toString());
        platformDump.addProperty("target_version", ProtocolTranslator.getTargetVersion().toString());
        platformDump.addProperty("in_world", MinecraftClient.getInstance().world != null);

        final Collection<ModContainer> mods = FabricLoader.getInstance().getAllMods();
        final JsonArray jsonMods = new JsonArray(mods.size());
        for (ModContainer modContainer : mods) {
            final ModMetadata metadata = modContainer.getMetadata();
            final JsonObject jsonMod = new JsonObject();
            jsonMod.addProperty("id", metadata.getId());
            jsonMod.addProperty("name", metadata.getName());
            jsonMod.addProperty("version", metadata.getVersion().getFriendlyString());
            final JsonArray authors = new JsonArray(metadata.getAuthors().size());
            for (Person person : metadata.getAuthors()) {
                final JsonObject info = new JsonObject();
                final Map<String, String> contactMap = person.getContact().asMap();
                if (!contactMap.isEmpty()) {
                    final JsonObject contact = new JsonObject();
                    contactMap.forEach(contact::addProperty);
                    info.add("contact", contact);
                }
                info.addProperty("name", person.getName());
                authors.add(info);
            }
            jsonMod.add("authors", authors);

            jsonMods.add(jsonMod);
        }

        platformDump.add("mods", jsonMods);

        return platformDump;
    }

    @Override
    public File getDataFolder() {
        // Move ViaLoader files directly into root folder
        return ViaFabricPlusImpl.INSTANCE.getPath().toFile();
    }

}
