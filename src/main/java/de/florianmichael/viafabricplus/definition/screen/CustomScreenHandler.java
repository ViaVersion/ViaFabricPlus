/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.definition.screen;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.definition.screen.netminecraft.LegacySmithingScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CustomScreenHandler {
    public final static Consumer<PacketByteBuf> TRIPLE_CHEST_HANDLER = data -> {
        final var byteBuf = data.asByteBuf();

        try {
            CustomScreenHandler.handleTripleChestHandler(Type.SHORT.readPrimitive(byteBuf), Type.COMPONENT.read(byteBuf), Type.SHORT.readPrimitive(byteBuf));
        } catch (Exception e) {
            ViaFabricPlus.LOGGER.error("Failed to open custom ScreenHandler with dimension 9xN", e);
        }
    };

    public final static Consumer<PacketByteBuf> LEGACY_SMITHING_HANDLER = data -> {
        final var byteBuf = data.asByteBuf();

        try {
            CustomScreenHandler.openLegacySmithingScreen(Type.VAR_INT.readPrimitive(byteBuf), Type.COMPONENT.read(byteBuf));
        } catch (Exception e) {
            ViaFabricPlus.LOGGER.error("Failed to open legacy smithing table", e);
        }
    };

    public final static ScreenHandlerType<LegacySmithingScreenHandler> LEGACY_SMITHING = new ScreenHandlerType<>(LegacySmithingScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
    private final static Map<Integer, ScreenHandlerType<ScreenHandler>> TRIPLE_CHEST_HANDLERS = new LinkedHashMap<>();

    static {
        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            TRIPLE_CHEST_HANDLERS.put(i, new ScreenHandlerType<>((syncId, playerInventory) -> new GenericContainerScreenHandler(TRIPLE_CHEST_HANDLERS.get(finalI), syncId, playerInventory, finalI), FeatureFlags.VANILLA_FEATURES));
        }
    }

    public static void openLegacySmithingScreen(final int windowID, final JsonElement title) {
        HandledScreens.open(CustomScreenHandler.LEGACY_SMITHING, MinecraftClient.getInstance(), windowID, Text.Serializer.fromJson(title.toString()));
    }

    public static void handleTripleChestHandler(final short windowID, final JsonElement title, final short slots) {
        int n = slots / 9;
        final int modulo = slots % 9;
        if (modulo > 0) n++;

        HandledScreens.open(CustomScreenHandler.TRIPLE_CHEST_HANDLERS.get(n), MinecraftClient.getInstance(), windowID, Text.Serializer.fromJson(title.toString()));
    }

    public static boolean isTripleChestHandler(final ScreenHandlerType<?> screenHandlerType) {
        return TRIPLE_CHEST_HANDLERS.containsValue(screenHandlerType);
    }
}
