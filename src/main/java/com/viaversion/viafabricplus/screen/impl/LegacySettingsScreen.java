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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.viaversion.viafabricplus.screen.impl;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.api.settings.base.Setting;
import com.viaversion.viafabricplus.api.settings.base.SettingGroup;
import com.viaversion.viafabricplus.screen.base.list.VFPList;
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public final class LegacySettingsScreen extends LegacyStyleScreen {

    private static double scrollAmount;

    public LegacySettingsScreen() {
        super(Component.translatable("screen.viafabricplus.settings"), projectSubtitle(), true);
    }

    @Override
    public void onClose() {
        if (this.prevScreen instanceof LegacyProtocolSelectionScreen parent
            && !ViaFabricPlusImpl.impl().settings().general().legacyUserInterface().isActive()) {
            ViaFabricPlusImpl.impl().screens().openViaFabricPlusScreen(parent.prevScreen);
        } else {
            super.onClose();
        }
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new SlotList(this.minecraft, this.width, this.height,
            6 + (this.font.lineHeight + 2) * 3, -5, (this.font.lineHeight + 2) * 2));
    }

    private static final class SlotList extends VFPList {

        private SlotList(final Minecraft minecraft, final int width, final int height,
                         final int top, final int bottom, final int entryHeight) {
            super(minecraft, width, height, top, bottom, entryHeight);
            for (final SettingGroup group : ViaFabricPlusImpl.impl().settings().groups()) {
                this.addEntry(new GroupTitleEntry(group.name()));
                for (final Setting setting : group.settings()) {
                    final VFPListEntry entry = SettingsScreen.entry(setting);
                    if (entry != null) {
                        this.addEntry(entry);
                    }
                }
            }
            this.setScrollAmount(scrollAmount);
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 140;
        }

        @Override
        protected void updateSlotAmount(final double amount) {
            scrollAmount = amount;
        }

    }

    private static final class GroupTitleEntry extends VFPListEntry {

        private final Component name;

        private GroupTitleEntry(final Component name) {
            this.name = name;
        }

        @Override
        public Component getNarration() {
            return this.name;
        }

        @Override
        public void mappedRender(final GuiGraphicsExtractor graphics, final int width, final int height) {
            final var font = Minecraft.getInstance().font;
            graphics.text(font, this.name.copy().withStyle(ChatFormatting.BOLD),
                SLOT_MARGIN, (height - font.lineHeight) / 2, -1);
        }

    }

}
