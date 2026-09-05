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

import com.viaversion.viafabricplus.api.settings.base.EnumSetting;
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry;
import java.util.Arrays;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public final class EnumListEntry<T extends EnumSetting.EnumValue> extends VFPListEntry {

    private static final int VALUE_MARGIN = 2;

    private final EnumSetting<T> value;

    public EnumListEntry(EnumSetting<T> value) {
        this.value = value;
    }

    @Override
    public @NonNull Component getNarration() {
        return this.value.name();
    }

    @Override
    public void mappedMouseClicked() {
        final T[] values = this.value.values();
        final int currentIndex = Arrays.asList(values).indexOf(this.value.value());
        this.value.setValue(currentIndex + 1 == values.length ? values[0] : values[currentIndex + 1]);
    }

    @Override
    public void mappedRender(GuiGraphicsExtractor context, int entryWidth, int entryHeight) {
        final Font textRenderer = Minecraft.getInstance().font;

        final Component name = this.value.value().component();
        final int offset = textRenderer.width(name) + VALUE_MARGIN;
        renderScrollableText(context, this.value.name().copy().withStyle(ChatFormatting.GRAY), offset);
        context.text(textRenderer, name, entryWidth - offset, entryHeight / 2 - textRenderer.lineHeight / 2, -1);
    }

}
