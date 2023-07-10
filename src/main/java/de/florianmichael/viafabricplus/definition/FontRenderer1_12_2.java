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
package de.florianmichael.viafabricplus.definition;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.viafabricplus.base.settings.groups.VisualSettings;
import de.florianmichael.viafabricplus.base.event.ChangeProtocolVersionCallback;
import de.florianmichael.viafabricplus.injection.access.IFontStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;

import java.util.function.Function;

public class FontRenderer1_12_2 {
    public final static boolean DASH_LOADER = FabricLoader.getInstance().isModLoaded("dashloader");

    public static void init() {
        if (DASH_LOADER) return;

        ChangeProtocolVersionCallback.EVENT.register(protocolVersion ->
                MinecraftClient.getInstance().fontManager.fontStorages.values().forEach(fontStorage ->
                        RenderSystem.recordRenderCall(() -> ((IFontStorage) fontStorage).viafabricplus_clearCaches())));
    }

    public static boolean shouldReplaceFontRenderer() {
        if (DASH_LOADER) return false;
        return VisualSettings.INSTANCE.changeFontRendererBehaviour.isEnabled();
    }

    public enum BuiltinEmptyGlyph1_12_2 implements Glyph {

        VERY_MISSING;

        @Override
        public float getAdvance() {
            return 1;
        }

        @Override
        public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> function) {
            return function.apply(new RenderableGlyph() {

                @Override
                public int getWidth() {
                    return 0;
                }

                @Override
                public int getHeight() {
                    return 0;
                }

                @Override
                public float getOversample() {
                    return 1.0f;
                }

                @Override
                public void upload(int x, int y) {
                }

                @Override
                public boolean hasColor() {
                    return true;
                }
            });
        }
    }
}
