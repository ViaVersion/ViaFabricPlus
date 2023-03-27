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
package de.florianmichael.viafabricplus.screen.settings.settingrenderer.meta;

import de.florianmichael.viafabricplus.screen.base.MappedSlotEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TitleRenderer extends MappedSlotEntry {
    private final String name;

    public TitleRenderer(String name) {
        this.name = name;
    }

    @Override
    public Text getNarration() {
        return Text.literal(this.name);
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        matrices.push();
        matrices.translate(x, y, 0);
        mappedRenderer(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, hovered, tickDelta);
        matrices.pop();
    }

    @Override
    public void mappedRenderer(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        textRenderer.drawWithShadow(matrices, Formatting.BOLD + this.name, 3, entryHeight / 2F - textRenderer.fontHeight / 2F, -1);
    }
}
