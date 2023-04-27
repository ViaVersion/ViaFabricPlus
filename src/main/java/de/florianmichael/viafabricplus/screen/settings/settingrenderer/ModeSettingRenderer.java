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
package de.florianmichael.viafabricplus.screen.settings.settingrenderer;

import de.florianmichael.viafabricplus.screen.base.MappedSlotEntry;
import de.florianmichael.viafabricplus.settings.type_impl.ModeSetting;
import de.florianmichael.viafabricplus.util.ScreenUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;

public class ModeSettingRenderer extends MappedSlotEntry {
    private final ModeSetting value;

    public ModeSettingRenderer(ModeSetting value) {
        this.value = value;
    }

    @Override
    public Text getNarration() {
        return this.value.getName();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        final int currentIndex = Arrays.stream(this.value.getOptions()).toList().indexOf(this.value.value) + 1;
        this.value.setValue(currentIndex > this.value.getOptions().length - 1 ? 0 : currentIndex);
        ScreenUtil.playClickSound();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mappedRenderer(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        context.drawTextWithShadow(textRenderer, this.value.getName().formatted(Formatting.GRAY), 3, entryHeight / 2 - textRenderer.fontHeight / 2, -1);
        context.drawTextWithShadow(textRenderer, this.value.getValue(), entryWidth - textRenderer.getWidth(this.value.getValue()) - 3 - 3, entryHeight / 2 - textRenderer.fontHeight / 2, -1);
    }
}
