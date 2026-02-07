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

package com.viaversion.viafabricplus.visuals.features.force_unicode_font;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.visuals.settings.VisualSettings;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;

/**
 * Older versions only had unicode font support for some languages and therefore servers are expecting the client
 * to use a unicode font, not using it on older versions can cause issues with wrong dimensions in chat components.
 */
public final class UnicodeFontFix1_12_2 {

    private static boolean enabled = false;
    private static Runnable task = null;

    public static void init() {
        ViaFabricPlus.getImpl().registerOnChangeProtocolVersionCallback((oldVersion, newVersion) -> {
            updateUnicodeFontOverride(newVersion);
        });

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            // Prevent usages of RenderSystem.recordRenderCall()
            if (task != null) {
                task.run();
                task = null;
            }
        });
    }

    public static void updateUnicodeFontOverride(final ProtocolVersion version) {
        final OptionInstance<Boolean> option = Minecraft.getInstance().options.forceUnicodeFont();

        if (VisualSettings.INSTANCE.forceUnicodeFontForNonAsciiLanguages.isEnabled(version)) {
            if (Language.getInstance() instanceof ClientLanguage storage) {
                enabled = LanguageUtil.isUnicodeFont1_12_2(storage.storage);
                task = () -> option.set(enabled);
            }
        } else if (enabled) {
            enabled = false;
            task = () -> option.set(false);
        }
    }

}
