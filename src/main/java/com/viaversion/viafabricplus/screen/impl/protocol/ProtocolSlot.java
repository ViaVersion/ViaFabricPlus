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

package com.viaversion.viafabricplus.screen.impl.protocol;

import com.viaversion.viafabricplus.protocoltranslator.util.ProtocolVersionDetector;
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public final class ProtocolSlot extends VFPListEntry {

    private static final Identifier AUTO_DETECT_ICON = Identifier.withDefaultNamespace("textures/item/spyglass.png");

    private static final int ICON_SIZE = 16;
    private static final int TEXT_OFFSET = SLOT_MARGIN + ICON_SIZE + SLOT_MARGIN;

    private static final int SELECTED_COLOR = 0xFF58A6FF;
    private static final int SELECTED_BACKGROUND_COLOR = 0x4058A6FF;
    private static final int SUBTITLE_COLOR = 0xFFAAAAAA;
    private static final int DISABLED_COLOR = 0xFF808080;

    private final ProtocolVersion protocolVersion;
    private final AbstractProtocolSelectionScreen screen;
    private final @Nullable ProtocolVersionMetadata metadata;
    private final Identifier icon;

    public ProtocolSlot(final ProtocolVersion protocolVersion, final AbstractProtocolSelectionScreen screen) {
        this.protocolVersion = protocolVersion;
        this.screen = screen;
        this.metadata = ProtocolVersionMetadata.of(protocolVersion);
        this.icon = resolveIcon(protocolVersion);
    }

    private static Identifier resolveIcon(final ProtocolVersion protocolVersion) {
        // Auto Detect is no Minecraft version and therefore has no metadata to take an icon from
        if (protocolVersion == ProtocolVersionDetector.AUTO_DETECT_VERSION) {
            return AUTO_DETECT_ICON;
        }

        return ProtocolVersionMetadata.icon(protocolVersion);
    }

    @Override
    public @NonNull Component getNarration() {
        return Component.nullToEmpty(this.protocolVersion.getName());
    }

    @Override
    public void mappedMouseClicked(final double mouseX, final double mouseY, final int button) {
        if (this.screen.selectable()) {
            this.screen.select(this.protocolVersion);
        }
    }

    @Override
    public void mappedRender(final GuiGraphicsExtractor context, final int x, final int y, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean hovered, final float tickDelta) {
        final boolean selectable = this.screen.selectable();
        final boolean selected = this.screen.selected(this.protocolVersion);
        if (selected && selectable) {
            context.fill(0, 0, entryWidth, entryHeight, SELECTED_BACKGROUND_COLOR);
        }

        context.blit(RenderPipelines.GUI_TEXTURED, this.icon, SLOT_MARGIN, (entryHeight - ICON_SIZE) / 2, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        final Font font = Minecraft.getInstance().font;
        final int nameColor = !selectable ? DISABLED_COLOR : selected ? SELECTED_COLOR : -1;
        final int centeredY = entryHeight / 2 - font.lineHeight / 2;
        if (this.metadata == null) {
            context.text(font, this.protocolVersion.getName(), TEXT_OFFSET, centeredY, nameColor);
            return;
        }

        final int subtitleColor = !selectable ? DISABLED_COLOR : SUBTITLE_COLOR;
        final Component releaseDate = this.metadata.formattedReleaseDate();
        final int releaseDateX = entryWidth - font.width(releaseDate) - SLOT_MARGIN;
        context.text(font, releaseDate, releaseDateX, centeredY, subtitleColor);

        if (this.metadata.title() == null) {
            // Versions without an update name only have a single line of text to center
            context.text(font, this.protocolVersion.getName(), TEXT_OFFSET, centeredY, nameColor);
            return;
        }

        context.text(font, this.protocolVersion.getName(), TEXT_OFFSET, entryHeight / 2 - font.lineHeight - 1, nameColor);

        // Long update names are cut off instead of overlapping the release date
        context.enableScissor(TEXT_OFFSET, 0, releaseDateX - SLOT_MARGIN, entryHeight);
        context.text(font, this.metadata.title(), TEXT_OFFSET, entryHeight / 2 + 1, subtitleColor);
        context.disableScissor();
    }

}
