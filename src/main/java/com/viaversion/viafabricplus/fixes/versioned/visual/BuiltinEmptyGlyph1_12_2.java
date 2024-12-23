/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package com.viaversion.viafabricplus.fixes.versioned.visual;

import com.viaversion.viafabricplus.injection.mixin.fixes.minecraft.MixinFontStorage;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.RenderableGlyph;

import java.util.function.Function;

/**
 * Implementation of a blank glyph for 1.12.2 and lower since those versions don't draw a white rectangle for empty
 * glyphs but instead just skip them. See {@link MixinFontStorage} for more information.
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
    public BakedGlyph bake(Function<RenderableGlyph, BakedGlyph> glyphRendererGetter) {
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
