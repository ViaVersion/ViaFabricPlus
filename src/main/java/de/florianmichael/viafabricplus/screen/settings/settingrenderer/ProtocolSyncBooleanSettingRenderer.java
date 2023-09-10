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

import de.florianmichael.viafabricplus.base.screen.MappedSlotEntry;
import de.florianmichael.viafabricplus.base.settings.type_impl.ProtocolSyncBooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;

public class ProtocolSyncBooleanSettingRenderer extends MappedSlotEntry {
    private final ProtocolSyncBooleanSetting value;

    public ProtocolSyncBooleanSettingRenderer(ProtocolSyncBooleanSetting value) {
        this.value = value;
    }

    @Override
    public Text getNarration() {
        return this.value.getName();
    }

    @Override
    public void mappedMouseClicked(double mouseX, double mouseY, int button) {
        this.value.setValue(this.value.getValue() + 1);
        if (this.value.getValue() % 3 == 0) this.value.setValue(0);
    }

    @Override
    public void mappedRender(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        final Text text = Text.translatable("misc.viafabricplus." + (this.value.isAuto() ? "auto" : this.value.isEnabled() ? "on" : "off"));
        Color color = this.value.isAuto() ? Color.ORANGE : this.value.isEnabled() ? Color.GREEN : Color.RED;

        final var offset = textRenderer.getWidth(text) + 6;
        renderScrollableText(context, Text.of(Formatting.GRAY + this.value.getName().getString() + " " + Formatting.RESET + this.value.getProtocolRange().toString()), x, y, entryWidth, entryHeight, offset);
        context.drawTextWithShadow(textRenderer, text, entryWidth - offset, entryHeight / 2 - textRenderer.fontHeight / 2, color.getRGB());
    }
}
