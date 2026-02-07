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

package com.viaversion.viafabricplus.screen.impl;

import com.viaversion.viafabricplus.protocoltranslator.ProtocolTranslator;
import com.viaversion.viafabricplus.screen.VFPList;
import com.viaversion.viafabricplus.screen.VFPListEntry;
import com.viaversion.viafabricplus.screen.VFPScreen;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.awt.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class ProtocolSelectionScreen extends VFPScreen {

    public static final ProtocolSelectionScreen INSTANCE = new ProtocolSelectionScreen();

    private ProtocolSelectionScreen() {
        super("ViaFabricPlus", true);
    }

    @Override
    protected void init() {
        // List and Settings
        this.setupDefaultSubtitle();
        this.addRenderableWidget(new SlotList(this.minecraft, width, height, 3 + 3 /* start offset */ + (font.lineHeight + 2) * 3 /* title is 2 */, 30, font.lineHeight + 4));
        this.addRenderableWidget(Button.builder(Component.translatable("base.viafabricplus.settings"), button -> SettingsScreen.INSTANCE.open(this)).pos(width - 98 - 5, 5).size(98, 20).build());

        final Button serverList = this.addRenderableWidget(Button.builder(ServerListScreen.INSTANCE.getTitle(), button -> ServerListScreen.INSTANCE.open(this))
            .pos(5, height - 25).size(98, 20).build());
        serverList.active = Minecraft.getInstance().getConnection() == null;

        this.addRenderableWidget(Button.builder(Component.translatable("report.viafabricplus.button"), button -> ReportIssuesScreen.INSTANCE.open(this))
            .pos(width - 98 - 5, height - 25).size(98, 20).build());

        super.init();
    }

    public static class SlotList extends VFPList {
        private static double scrollAmount;

        public SlotList(Minecraft minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            ProtocolVersion.getReversedProtocols().stream().map(ProtocolSlot::new).forEach(this::addEntry);
            initScrollY(scrollAmount);
        }

        @Override
        protected void updateSlotAmount(double amount) {
            scrollAmount = amount;
        }
    }

    public static class ProtocolSlot extends VFPListEntry {

        private final ProtocolVersion protocolVersion;

        public ProtocolSlot(final ProtocolVersion protocolVersion) {
            this.protocolVersion = protocolVersion;
        }

        @Override
        public Component getNarration() {
            return Component.nullToEmpty(this.protocolVersion.getName());
        }

        @Override
        public void mappedMouseClicked(double mouseX, double mouseY, int button) {
            if (Minecraft.getInstance().getConnection() != null) {
                // Setting the target version while connected to a server is not allowed as this will
                // literally break our code away.
                return;
            }

            ProtocolTranslator.setTargetVersion(this.protocolVersion);
        }

        @Override
        public void renderContent(final GuiGraphics context, final int mouseX, final int mouseY, final boolean hovered, final float deltaTicks) {
            final boolean isSelected = ProtocolTranslator.getTargetVersion().equals(protocolVersion);

            Color color = isSelected ? Color.GREEN : Color.RED;
            if (Minecraft.getInstance().getConnection() != null) {
                color = color.darker();
            }

            final Font textRenderer = Minecraft.getInstance().font;
            context.drawCenteredString(textRenderer, this.protocolVersion.getName(), getContentXMiddle(), getContentYMiddle() - textRenderer.lineHeight / 2, color.getRGB());
        }
    }

}
