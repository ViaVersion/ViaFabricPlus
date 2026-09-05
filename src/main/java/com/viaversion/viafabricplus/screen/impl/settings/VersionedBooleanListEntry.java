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

package com.viaversion.viafabricplus.screen.impl.settings;

import com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting;
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public final class VersionedBooleanListEntry extends VFPListEntry {

    private static final int ACTIVE_COLOR = 0xFF00FF00;
    private static final int INACTIVE_COLOR = 0xFFFF0000;
    private static final int STATE_MARGIN = 2;

    private final VersionedBooleanSetting value;

    public VersionedBooleanListEntry(VersionedBooleanSetting value) {
        this.value = value;
    }

    @Override
    public @NonNull Component getNarration() {
        return this.value.name();
    }

    @Override
    public void mappedMouseClicked() {
        this.value.setActive(!this.value.value());
    }

    @Override
    public void mappedRender(GuiGraphicsExtractor context, int entryWidth, int entryHeight) {
        final Font textRenderer = Minecraft.getInstance().font;

        final Component text = this.value.value() ? Component.translatable("base.viafabricplus.on") : Component.translatable("base.viafabricplus.off");

        final int offset = textRenderer.width(text) + STATE_MARGIN;
        renderScrollableText(context, Component.nullToEmpty(ChatFormatting.GRAY + this.value.name().getString() + " " + ChatFormatting.RESET + this.value.versionRange().toString()), offset);
        context.text(textRenderer, text, entryWidth - offset, entryHeight / 2 - textRenderer.lineHeight / 2, this.value.value() ? ACTIVE_COLOR : INACTIVE_COLOR);
    }

}
