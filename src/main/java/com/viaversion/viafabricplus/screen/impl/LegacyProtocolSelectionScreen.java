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

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.api.protocoltranslator.ProtocolTranslation;
import com.viaversion.viafabricplus.screen.base.list.VFPList;
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry;
import com.viaversion.viafabricplus.screen.impl.serverlist.BetaCraftServerListScreen;
import com.viaversion.viafabricplus.screen.impl.serverlist.ClassiCubeServerListScreen;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.awt.Color;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public final class LegacyProtocolSelectionScreen extends LegacyStyleScreen {

    private static double globalScrollAmount;

    private final Consumer<ProtocolVersion> selectionConsumer;
    private final Predicate<ProtocolVersion> selectionPredicate;
    private final BooleanSupplier selectable;
    private final boolean perServer;

    public LegacyProtocolSelectionScreen() {
        super(Component.nullToEmpty("ViaFabricPlus"), projectSubtitle(), true);
        this.selectionConsumer = ViaFabricPlus.api()::setTargetVersion;
        this.selectionPredicate = version -> ViaFabricPlus.api().targetVersion().equals(version);
        this.selectable = () -> Minecraft.getInstance().getConnection() == null;
        this.perServer = false;
    }

    public LegacyProtocolSelectionScreen(final Screen parent, final Consumer<ProtocolVersion> selectionConsumer,
                                         final Supplier<ProtocolVersion> selectionSupplier) {
        super(Component.translatable("screen.viafabricplus.force_version"),
            Component.translatable("force_version.viafabricplus.title"), false);
        this.prevScreen = parent;
        this.selectionConsumer = selectionConsumer;
        this.selectionPredicate = version -> version.equals(selectionSupplier.get());
        this.selectable = () -> true;
        this.perServer = true;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new SlotList(this.minecraft, this.width, this.height,
            6 + (this.font.lineHeight + 2) * 3, this.perServer ? -5 : 30, this.font.lineHeight + 4));

        if (!this.perServer) {
            final var screens = ViaFabricPlusImpl.impl().screens();
            this.addRenderableWidget(Button.builder(Component.translatable("base.viafabricplus.settings"),
                _ -> screens.openSettingsScreen(this)).pos(this.width - 103, 5).size(98, 20).build());

            final Button classiCube = this.addRenderableWidget(Button.builder(ClassiCubeServerListScreen.TITLE,
                _ -> screens.openClassiCubeServerListScreen(this)).pos(5, this.height - 25).size(98, 20).build());
            classiCube.active = this.selectable.getAsBoolean();

            final Button betaCraft = this.addRenderableWidget(Button.builder(BetaCraftServerListScreen.TITLE,
                _ -> screens.openBetaCraftServerListScreen(this)).pos(108, this.height - 25).size(98, 20).build());
            betaCraft.active = this.selectable.getAsBoolean();

            this.addRenderableWidget(Button.builder(Component.translatable("report.viafabricplus.button"),
                _ -> screens.reportIssuesScreen().open(this)).pos(this.width - 103, this.height - 25)
                .size(98, 20).build());
        }
    }

    private final class SlotList extends VFPList {

        private SlotList(final Minecraft minecraft, final int width, final int height,
                         final int top, final int bottom, final int entryHeight) {
            super(minecraft, width, height, top, bottom, entryHeight);
            if (perServer) {
                this.addEntry(new ResetSlot());
            }
            if (!ProtocolVersion.getReversedProtocols().contains(ProtocolTranslation.AUTO_DETECT_VERSION)) {
                this.addEntry(new ProtocolSlot(ProtocolTranslation.AUTO_DETECT_VERSION));
            }
            ProtocolVersion.getReversedProtocols().stream().map(ProtocolSlot::new).forEach(this::addEntry);
            if (!perServer) {
                this.setScrollAmount(globalScrollAmount);
            }
        }

        @Override
        protected void updateSlotAmount(final double amount) {
            if (!perServer) {
                globalScrollAmount = amount;
            }
        }

    }

    private final class ResetSlot extends VFPListEntry {

        @Override
        public Component getNarration() {
            return Component.translatable("base.viafabricplus.reset");
        }

        @Override
        public void mappedMouseClicked() {
            selectionConsumer.accept(null);
        }

        @Override
        public void mappedRender(final GuiGraphicsExtractor graphics, final int width, final int height) {
            final Font font = Minecraft.getInstance().font;
            graphics.centeredText(font, this.getNarration(), width / 2,
                (height - font.lineHeight) / 2, Color.ORANGE.getRGB());
        }

    }

    private final class ProtocolSlot extends VFPListEntry {

        private final ProtocolVersion version;

        private ProtocolSlot(final ProtocolVersion version) {
            this.version = version;
        }

        @Override
        public @NonNull Component getNarration() {
            return Component.nullToEmpty(this.version.getName());
        }

        @Override
        public void mappedMouseClicked() {
            if (selectable.getAsBoolean()) {
                selectionConsumer.accept(this.version);
            }
        }

        @Override
        public void mappedRender(final GuiGraphicsExtractor graphics, final int width, final int height) {
            Color color = selectionPredicate.test(this.version) ? Color.GREEN : (perServer ? Color.WHITE : Color.RED);
            if (!selectable.getAsBoolean()) {
                color = color.darker();
            }
            final Font font = Minecraft.getInstance().font;
            graphics.centeredText(font, this.version.getName(), width / 2,
                (height - font.lineHeight) / 2, color.getRGB());
        }

    }

}
