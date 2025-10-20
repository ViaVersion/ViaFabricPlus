/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.screen.impl.settings;

import com.viaversion.viafabricplus.screen.VFPListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Matrix3x2fStack;

public final class TitleEntry extends VFPListEntry {

    private final Text name;

    public TitleEntry(Text name) {
        this.name = name;
    }

    @Override
    public Text getNarration() {
        return this.name;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final boolean hovered, final float deltaTicks) {
        final Matrix3x2fStack matrices = context.getMatrices();

        matrices.pushMatrix();
        matrices.translate(getX(), getY());
        mappedRender(context, getX(), getY(), getWidth(), getHeight(), mouseX, mouseY, hovered, deltaTicks);
        matrices.popMatrix();
    }

    @Override
    public void mappedRender(DrawContext context, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        context.drawTextWithShadow(textRenderer, this.name.copy().formatted(Formatting.BOLD), 3, entryHeight / 2 - textRenderer.fontHeight / 2, -1);
    }

}
