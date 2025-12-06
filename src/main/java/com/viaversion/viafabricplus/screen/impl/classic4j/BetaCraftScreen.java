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

package com.viaversion.viafabricplus.screen.impl.classic4j;

import com.viaversion.viafabricplus.screen.VFPList;
import com.viaversion.viafabricplus.screen.VFPListEntry;
import com.viaversion.viafabricplus.screen.VFPScreen;
import com.viaversion.viafabricplus.screen.impl.settings.TitleEntry;
import com.viaversion.viafabricplus.util.ConnectionUtil;
import de.florianmichael.classic4j.BetaCraftHandler;
import de.florianmichael.classic4j.model.betacraft.BCServerInfo;
import de.florianmichael.classic4j.model.betacraft.BCServerList;
import de.florianmichael.classic4j.model.betacraft.BCVersionCategory;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import static com.viaversion.viafabricplus.screen.VFPListEntry.SLOT_MARGIN;

public final class BetaCraftScreen extends VFPScreen {

    public static final BetaCraftScreen INSTANCE = new BetaCraftScreen();

    public static BCServerList SERVER_LIST;
    private static final String BETA_CRAFT_SERVER_LIST_URL = "https://betacraft.uk/serverlist/";

    private BetaCraftScreen() {
        super("BetaCraft", true);
    }

    @Override
    protected void init() {
        super.init();
        if (SERVER_LIST != null) {
            createView();
            return;
        }
        setupSubtitle(Component.translatable("betacraft.viafabricplus.loading"));
        BetaCraftHandler.requestServerList(serverList -> {
            BetaCraftScreen.SERVER_LIST = serverList;
            createView();
        }, throwable -> showErrorScreen(BetaCraftScreen.INSTANCE.getTitle(), throwable, this));
    }

    private void createView() {
        this.setupSubtitle(Component.nullToEmpty(BETA_CRAFT_SERVER_LIST_URL), ConfirmLinkScreen.confirmLink(this, BETA_CRAFT_SERVER_LIST_URL));

        final int entryHeight = (font.lineHeight + 2) * 3; // title is 2
        this.addRenderableWidget(new SlotList(this.minecraft, width, height, 2 * SLOT_MARGIN + entryHeight, -5, entryHeight));

        this.addRefreshButton(() -> SERVER_LIST = null);
    }

    @Override
    protected boolean subtitleCentered() {
        return SERVER_LIST == null;
    }

    public static class SlotList extends VFPList {
        private static double scrollAmount;

        public SlotList(Minecraft minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);
            if (SERVER_LIST == null) {
                return;
            }

            for (BCVersionCategory value : BCVersionCategory.values()) {
                final List<BCServerInfo> servers = SERVER_LIST.serversOfVersionCategory(value);
                if (servers.isEmpty()) {
                    continue;
                }
                addEntry(new TitleEntry(Component.nullToEmpty(value.name())));
                for (BCServerInfo server : servers) {
                    addEntry(new ServerSlot(server));
                }
            }

            initScrollY(scrollAmount);
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 140;
        }

        @Override
        protected void updateSlotAmount(double amount) {
            scrollAmount = amount;
        }
    }

    public static class ServerSlot extends VFPListEntry {
        private final BCServerInfo server;

        public ServerSlot(BCServerInfo server) {
            this.server = server;
        }

        @Override
        public Component getNarration() {
            return Component.nullToEmpty(server.name());
        }

        @Override
        public void mappedMouseClicked(double mouseX, double mouseY, int button) {
            ConnectionUtil.connect(server.name(), server.socket());
            super.mappedMouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void mappedRender(GuiGraphics context, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final Font textRenderer = Minecraft.getInstance().font;
            context.drawCenteredString(textRenderer, server.name() + ChatFormatting.DARK_GRAY + " [" + server.gameVersion() + "]", entryWidth / 2, entryHeight / 2 - textRenderer.lineHeight / 2, -1);

            if (server.onlineMode()) {
                context.drawString(textRenderer, Component.translatable("base.viafabricplus.online_mode").withStyle(ChatFormatting.GREEN), 1, 1, -1);
            }
            final String playerText = server.playerCount() + "/" + server.playerLimit();
            context.drawString(textRenderer, playerText, entryWidth - textRenderer.width(playerText) - 1, 1, -1);
        }
    }

}
