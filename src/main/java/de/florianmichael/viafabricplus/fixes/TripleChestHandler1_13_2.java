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

package de.florianmichael.viafabricplus.fixes;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class TripleChestHandler1_13_2 {
    public static final Consumer<PacketByteBuf> TRIPLE_CHEST_HANDLER = data -> {
        final var byteBuf = data.asByteBuf();

        try {
            TripleChestHandler1_13_2.handleTripleChestHandler(Type.SHORT.readPrimitive(byteBuf), Type.COMPONENT.read(byteBuf), Type.SHORT.readPrimitive(byteBuf));
        } catch (Exception e) {
            ViaFabricPlus.LOGGER.error("Failed to open custom ScreenHandler with dimension 9xN", e);
        }
    };

    public static void handleTripleChestHandler(final short windowID, final JsonElement title, final short slots) {
        int n = slots / 9;
        final int modulo = slots % 9;
        if (modulo > 0) n++;

        final var screenHandler = new AtomicReference<ScreenHandlerType<?>>();
        int finalN = n;
        screenHandler.set(new TripleChestScreenHandlerType((syncId, playerInventory) -> new GenericContainerScreenHandler(screenHandler.get(), syncId, playerInventory, finalN), FeatureFlags.VANILLA_FEATURES));

        HandledScreens.open(screenHandler.get(), MinecraftClient.getInstance(), windowID, Text.Serializer.fromJson(title.toString()));
    }

    public static class TripleChestScreenHandlerType extends ScreenHandlerType<GenericContainerScreenHandler> {

        public TripleChestScreenHandlerType(Factory<GenericContainerScreenHandler> factory, FeatureSet requiredFeatures) {
            super(factory, requiredFeatures);
        }
    }
}
