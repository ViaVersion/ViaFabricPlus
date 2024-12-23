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

package com.viaversion.viafabricplus.features.text_rendering.non_existing_characters;

import com.viaversion.viafabricplus.base.Events;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.protocoltranslator.util.LanguageUtil;
import com.viaversion.viafabricplus.base.settings.impl.VisualSettings;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.util.Language;
import org.jetbrains.annotations.ApiStatus;

/**
 * Older versions only had unicode font support for some languages and therefore servers are expecting the client
 * to use a unicode font, not using it on older versions can cause issues with wrong dimensions in chat components.
 */
@ApiStatus.Internal
public class UnicodeFontFix1_12_2 {

    private static boolean enabled = false;
    private static Runnable task = null;

    static {
        Events.CHANGE_PROTOCOL_VERSION.register((oldVersion, newVersion) -> updateUnicodeFontOverride(newVersion));

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            // Prevent usages of RenderSystem.recordRenderCall()
            if (task != null) {
                task.run();
                task = null;
            }
        });
    }

    public static void init() {
        // Calls the static block
    }

    public static void updateUnicodeFontOverride(final ProtocolVersion version) {
        final SimpleOption<Boolean> option = MinecraftClient.getInstance().options.getForceUnicodeFont();

        if (VisualSettings.global().forceUnicodeFontForNonAsciiLanguages.isEnabled(version)) {
            if (Language.getInstance() instanceof TranslationStorage storage) {
                enabled = LanguageUtil.isUnicodeFont1_12_2(storage.translations);
                task = () -> option.setValue(enabled);
            }
        } else if (enabled) {
            enabled = false;
            task = () -> option.setValue(false);
        }
    }

}
