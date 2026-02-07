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

package com.viaversion.viafabricplus.protocoltranslator.impl.platform;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.protocoltranslator.impl.viaversion.ViaFabricPlusConfig;
import com.viaversion.viafabricplus.protocoltranslator.protocol.ViaFabricPlusProtocol;
import com.viaversion.viafabricplus.protocoltranslator.util.JLoggerToSLF4J;
import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.configuration.AbstractViaConfig;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.platform.UserConnectionViaVersionPlatform;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;
import com.viaversion.viaversion.util.GsonUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.Minecraft;
import org.slf4j.LoggerFactory;

public final class ViaFabricPlusViaVersionPlatform extends UserConnectionViaVersionPlatform {

    public ViaFabricPlusViaVersionPlatform(File dataFolder) {
        super(dataFolder);
    }

    @Override
    public Logger createLogger(final String name) {
        return new JLoggerToSLF4J(LoggerFactory.getLogger(name));
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
        return new ViaFabricPlusConfig(new File(getDataFolder(), "viaversion.yml"), this.getLogger());
    }

    @Override
    public void sendCustomPayload(UserConnection connection, String channel, byte[] message) {
        final PacketWrapper customPayload = PacketWrapper.create(ViaFabricPlusProtocol.INSTANCE.getCustomPayloadPacketType(), connection);
        customPayload.write(Types.STRING, channel);
        customPayload.write(Types.REMAINING_BYTES, message);
        customPayload.scheduleSendToServer(ViaFabricPlusProtocol.class);
    }

    @Override
    public void sendCustomPayloadToClient(final UserConnection connection, final String channel, final byte[] message) {
        final PacketWrapper customPayload = PacketWrapper.create(ViaFabricPlusProtocol.INSTANCE.getClientboundCustomPayloadPacketType(), connection);
        customPayload.write(Types.STRING, channel);
        customPayload.write(Types.REMAINING_BYTES, message);
        customPayload.scheduleSend(ViaFabricPlusProtocol.class);
    }

    @Override
    public JsonObject getDump() {
        final JsonObject platformDump = new JsonObject();
        platformDump.addProperty("impl_version", ViaFabricPlusImpl.INSTANCE.getImplVersion());
        platformDump.addProperty("native_version", ProtocolTranslator.NATIVE_VERSION.toString());
        platformDump.addProperty("target_version", ProtocolTranslator.getTargetVersion().toString());
        platformDump.addProperty("in_world", Minecraft.getInstance().level != null);

        final Collection<ModContainer> allMods = FabricLoader.getInstance().getAllMods();
        final JsonArray mods = new JsonArray(allMods.size());
        for (final ModContainer modContainer : allMods) {
            final ModMetadata metadata = modContainer.getMetadata();
            final JsonObject mod = new JsonObject();
            mod.addProperty("id", metadata.getId());
            mod.addProperty("name", metadata.getName());
            mod.addProperty("version", metadata.getVersion().getFriendlyString());
            final JsonArray authors = new JsonArray(metadata.getAuthors().size());
            for (final Person person : metadata.getAuthors()) {
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
            mod.add("authors", authors);

            mods.add(mod);
        }

        platformDump.add("mods", mods);

        final com.google.gson.JsonObject settings = new com.google.gson.JsonObject();
        SaveManager.INSTANCE.getSettingsSave().writeSettings(settings);
        platformDump.add("settings", GsonUtil.getGson().fromJson(settings.toString(), JsonObject.class));

        return platformDump;
    }

}
