/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.screen.impl.settings;

import com.viaversion.viafabricplus.api.settings.type.BooleanSetting;
import com.viaversion.viafabricplus.screen.VFPListEntry;
import java.awt.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public final class BooleanListEntry extends VFPListEntry {
    private final BooleanSetting value;

    public BooleanListEntry(BooleanSetting value) {
        this.value = value;
    }

    @Override
    public Component getNarration() {
        return this.value.getName();
    }

    @Override
    public void mappedMouseClicked(double mouseX, double mouseY, int button) {
        this.value.setValue(!this.value.getCurrentValue());
    }

    @Override
    public void mappedRender(GuiGraphics context, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        final Font textRenderer = Minecraft.getInstance().font;

        final Component text = this.value.getCurrentValue() ? Component.translatable("base.viafabricplus.on") : Component.translatable("base.viafabricplus.off");

        final int offset = textRenderer.width(text) + 2;
        renderScrollableText(this.value.getName().withStyle(ChatFormatting.GRAY), offset);
        context.drawString(textRenderer, text, entryWidth - offset, entryHeight / 2 - textRenderer.lineHeight / 2, this.value.getCurrentValue() ? Color.GREEN.getRGB() : Color.RED.getRGB());

        renderTooltip(value.getTooltip(), mouseX, mouseY);
    }

}
