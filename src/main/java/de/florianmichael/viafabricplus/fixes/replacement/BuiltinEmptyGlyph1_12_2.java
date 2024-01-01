/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2021-2024 RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.fixes.replacement;

import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;

import java.util.function.Function;

/**
 * Implementation of a blank glyph for 1.12.2 and lower since those versions don't draw a white rectangle for empty
 * glyphs but instead just skip them. See {@link de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.MixinFontStorage} for more information.
 */
public enum BuiltinEmptyGlyph1_12_2 implements Glyph {
    INSTANCE;

    private static final int WIDTH = 0;
    private static final int HEIGHT = 8;

    @Override
    public float getAdvance() {
        return WIDTH;
    }

    @Override
    public GlyphRenderer bake(Function<RenderableGlyph, GlyphRenderer> glyphRendererGetter) {
        return glyphRendererGetter.apply(new RenderableGlyph() {

            @Override
            public int getWidth() {
                return WIDTH;
            }

            @Override
            public int getHeight() {
                return HEIGHT;
            }

            @Override
            public float getOversample() {
                return 1F;
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
