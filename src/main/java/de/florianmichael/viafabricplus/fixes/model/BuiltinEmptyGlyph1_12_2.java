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

package de.florianmichael.viafabricplus.fixes.model;

import net.minecraft.client.font.Glyph;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;

import java.util.function.Function;

/**
 * Implementation of a "Very Missing" Glyph which doesn't have any rendering at all ("Missing" in <= 1.12.2)
 */
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
