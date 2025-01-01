/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

import com.viaversion.vialoader.util.ProtocolVersionList;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viafabricplus.screen.VFPList;
import com.viaversion.viafabricplus.screen.VFPListEntry;
import com.viaversion.viafabricplus.screen.VFPScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public final class PerServerVersionScreen extends VFPScreen {

    private final Consumer<ProtocolVersion> selectionConsumer;

    public PerServerVersionScreen(final Screen prevScreen, final Consumer<ProtocolVersion> selectionConsumer) {
        super(Text.translatable("screen.viafabricplus.force_version"), false);

        this.prevScreen = prevScreen;
        this.selectionConsumer = selectionConsumer;

        this.setupSubtitle(Text.translatable("force_version.viafabricplus.title"));
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, -5, textRenderer.fontHeight + 4));
    }

    public final class SlotList extends VFPList {

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            this.addEntry(new ResetSlot());
            ProtocolVersionList.getProtocolsNewToOld().stream().map(ProtocolSlot::new).forEach(this::addEntry);
        }
    }

    public abstract class SharedSlot extends VFPListEntry {

        @Override
        public void mappedMouseClicked(double mouseX, double mouseY, int button) {
            close();
        }
    }

    public final class ResetSlot extends SharedSlot {

        @Override
        public Text getNarration() {
            return Text.translatable("base.viafabricplus.cancel_and_reset");
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            selectionConsumer.accept(null);

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            context.drawCenteredTextWithShadow(textRenderer, ((MutableText) getNarration()).formatted(Formatting.GOLD), x + entryWidth / 2, y + entryHeight / 2 - textRenderer.fontHeight / 2, -1);
        }
    }

    public final class ProtocolSlot extends SharedSlot {

        private final ProtocolVersion protocolVersion;

        public ProtocolSlot(final ProtocolVersion protocolVersion) {
            this.protocolVersion = protocolVersion;
        }

        @Override
        public Text getNarration() {
            return Text.of(this.protocolVersion.getName());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            selectionConsumer.accept(protocolVersion);

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            context.drawCenteredTextWithShadow(textRenderer, this.protocolVersion.getName(), x + entryWidth / 2, y - 1 + entryHeight / 2 - textRenderer.fontHeight / 2, -1);
        }
    }

}
