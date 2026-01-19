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

package com.viaversion.viafabricplus.screen.impl;

import com.viaversion.viafabricplus.screen.VFPList;
import com.viaversion.viafabricplus.screen.VFPListEntry;
import com.viaversion.viafabricplus.screen.VFPScreen;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public final class PerServerVersionScreen extends VFPScreen {

    private final Consumer<ProtocolVersion> selectionConsumer;
    private final Supplier<ProtocolVersion> selectionSupplier;

    public PerServerVersionScreen(final Screen prevScreen, final Consumer<ProtocolVersion> selectionConsumer, final Supplier<ProtocolVersion> selectionSupplier) {
        super(Component.translatable("screen.viafabricplus.force_version"), false);

        this.prevScreen = prevScreen;
        this.selectionConsumer = selectionConsumer;
        this.selectionSupplier = selectionSupplier;

        this.setupSubtitle(Component.translatable("force_version.viafabricplus.title"));
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(new SlotList(this.minecraft, width, height, 3 + 3 /* start offset */ + (font.lineHeight + 2) * 3 /* title is 2 */, -5, font.lineHeight + 4));
    }

    public final class SlotList extends VFPList {

        public SlotList(Minecraft minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            this.addEntry(new ResetSlot());
            ProtocolVersion.getReversedProtocols().stream().map(ProtocolSlot::new).forEach(this::addEntry);
        }
    }

    public abstract class SharedSlot extends VFPListEntry {

        @Override
        public void mappedMouseClicked(double mouseX, double mouseY, int button) {
            onClose();
        }
    }

    public final class ResetSlot extends SharedSlot {

        @Override
        public Component getNarration() {
            return Component.translatable("base.viafabricplus.cancel_and_reset");
        }

        @Override
        public void mappedMouseClicked(final double mouseX, final double mouseY, final int button) {
            selectionConsumer.accept(null);
        }

        @Override
        public void renderContent(final GuiGraphics context, final int mouseX, final int mouseY, final boolean hovered, final float deltaTicks) {
            final Font textRenderer = Minecraft.getInstance().font;
            context.drawCenteredString(textRenderer, ((MutableComponent) getNarration()).withStyle(ChatFormatting.GOLD), getContentXMiddle(), getContentYMiddle() - textRenderer.lineHeight / 2, -1);
        }
    }

    public final class ProtocolSlot extends SharedSlot {

        private final ProtocolVersion protocolVersion;

        public ProtocolSlot(final ProtocolVersion protocolVersion) {
            this.protocolVersion = protocolVersion;
        }

        @Override
        public Component getNarration() {
            return Component.nullToEmpty(this.protocolVersion.getName());
        }

        @Override
        public void mappedMouseClicked(final double mouseX, final double mouseY, final int button) {
            selectionConsumer.accept(protocolVersion);
        }

        @Override
        public void renderContent(final GuiGraphics context, final int mouseX, final int mouseY, final boolean hovered, final float deltaTicks) {
            final boolean isSelected = protocolVersion.equals(selectionSupplier.get());

            final Font textRenderer = Minecraft.getInstance().font;
            context.drawCenteredString(textRenderer, this.protocolVersion.getName(), getContentXMiddle(), getContentYMiddle() - textRenderer.lineHeight / 2, isSelected ? Color.GREEN.getRGB() : -1);
        }
    }

}
