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

package com.viaversion.viafabricplus.protocoltranslator.impl.platform;

import com.viaversion.viaversion.configuration.AbstractViaConfig;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.injection.ViaFabricPlusMixinPlugin;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.impl.viaversion.ViaFabricPlusVLViaConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.raphimc.vialoader.impl.platform.ViaVersionPlatformImpl;

import java.io.File;

public final class ViaFabricPlusViaVersionPlatformImpl extends ViaVersionPlatformImpl {

    public ViaFabricPlusViaVersionPlatformImpl(File rootFolder) { // Only required to not throw an exception, not used
        super(rootFolder);
    }

    @Override
    protected AbstractViaConfig createConfig() {
        // Use config overload and change directory to root folder
        return new ViaFabricPlusVLViaConfig(new File(getDataFolder(), "viaversion.yml"), this.getLogger());
    }

    @Override
    public JsonObject getDump() {
        final JsonObject platformDump = new JsonObject();
        platformDump.addProperty("version", ViaFabricPlusMixinPlugin.VFP_VERSION);
        platformDump.addProperty("impl_version", ViaFabricPlusMixinPlugin.VFP_IMPL_VERSION);
        platformDump.addProperty("native_version", ProtocolTranslator.NATIVE_VERSION.toString());

        final JsonArray mods = new JsonArray();
        FabricLoader.getInstance().getAllMods().stream().map(mod -> {
            final JsonObject jsonMod = new JsonObject();
            jsonMod.addProperty("id", mod.getMetadata().getId());
            jsonMod.addProperty("name", mod.getMetadata().getName());
            jsonMod.addProperty("version", mod.getMetadata().getVersion().getFriendlyString());
            final JsonArray authors = new JsonArray();
            mod.getMetadata().getAuthors().stream().map(it -> {
                final JsonObject info = new JsonObject();
                final JsonObject contact = new JsonObject();
                it.getContact().asMap().forEach(contact::addProperty);

                if (!contact.isEmpty()) info.add("contact", contact);
                info.addProperty("name", it.getName());
                return info;
            }).forEach(authors::add);
            jsonMod.add("authors", authors);

            return jsonMod;
        }).forEach(mods::add);

        platformDump.add("mods", mods);

        return platformDump;
    }

    @Override
    public File getDataFolder() {
        // Move ViaLoader files directly into root folder
        return ViaFabricPlusImpl.INSTANCE.rootPath().toFile();
    }

}
