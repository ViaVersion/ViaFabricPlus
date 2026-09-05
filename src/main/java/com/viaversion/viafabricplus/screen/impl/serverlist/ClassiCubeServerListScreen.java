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

package com.viaversion.viafabricplus.screen.impl.serverlist;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.features.classic.ClassiCubeAccount;
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy.ViaFabricPlusClassicMPPassProvider;
import com.viaversion.viafabricplus.screen.base.VFPScreen;
import com.viaversion.viafabricplus.screen.base.list.VFPList;
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry;
import com.viaversion.viafabricplus.screen.base.list.VFPTextEntry;
import com.viaversion.viafabricplus.util.network.ConnectionUtil;
import de.florianreuth.classic4j.ClassiCubeHandler;
import de.florianreuth.classic4j.model.classicube.server.CCServerInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public final class ClassiCubeServerListScreen extends VFPScreen {

    public static final Component TITLE = Component.nullToEmpty("ClassiCube");
    private static final int LIST_TOP = SEARCH_TOP + SEARCH_HEIGHT + SEARCH_MARGIN;
    private static final int ROW_WIDTH = 360;

    private static @Nullable List<CCServerInfo> serverList;
    private static boolean loading;

    private SlotList list;

    public ClassiCubeServerListScreen() {
        super(TITLE, true);
    }

    public static boolean loggedIn() {
        return ClassiCubeAccount.get() != null;
    }

    @Override
    protected void init() {
        if (loggedIn() && serverList == null) {
            this.load();
        }

        this.addSearchBar(this::search);

        this.list = this.addRenderableWidget(new SlotList(this.minecraft, this.width, this.height, LIST_TOP, FOOTER_HEIGHT,
            (this.font.lineHeight + 2) * 3 /* title is 2 */));

        this.addFooter(Button.builder(Component.translatable("base.viafabricplus.logout"), _ -> {
            ClassiCubeAccount.set(null);
            serverList = null;
            this.onClose();
        }).build());

        super.init();
    }

    private void search(final String query) {
        this.list.showServers(query);
    }

    // Guarded against the screen being rebuilt while the request is still running, which would send it again
    private void load() {
        if (loading) {
            return;
        }

        loading = true;
        ClassiCubeHandler.requestServerList(ClassiCubeAccount.get(), response -> {
            serverList = new ArrayList<>(response.servers());
            loading = false;
            Minecraft.getInstance().execute(this::rebuildWidgets);
        }, throwable -> {
            loading = false;
            ViaFabricPlusImpl.impl().logger().error("Error while loading ClassiCube servers!", throwable);
            showToast(Component.translatable("base.viafabricplus.something_went_wrong"));
            Minecraft.getInstance().execute(this::rebuildWidgets); // Replaces the loading text of the list
        });
    }

    public static class SlotList extends VFPList {
        private static double scrollAmount;

        private boolean searching;

        public SlotList(Minecraft minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            this.showServers("");
        }

        public void showServers(final String query) {
            this.searching = !query.isEmpty();
            this.clearEntries();

            if (serverList == null) { // The servers are either still loading or the request failed
                this.addEntry(new VFPTextEntry(loading ? Component.translatable("classicube.viafabricplus.loading") : Component.translatable("base.viafabricplus.something_went_wrong")));
                return;
            }

            serverList.stream().filter(server -> matches(server, query)).forEach(server -> this.addEntry(new ServerSlot(server)));

            // Needs calling last to have the entries added before setting the scroll amount
            this.setScrollAmount(this.searching ? 0D : scrollAmount);
        }

        private static boolean matches(final CCServerInfo server, final String query) {
            return server.name().toLowerCase(Locale.ROOT).contains(query) || server.software().toLowerCase(Locale.ROOT).contains(query);
        }

        @Override
        public int getRowWidth() {
            return Math.min(ROW_WIDTH, this.width - 20);
        }

        @Override
        protected void updateSlotAmount(double amount) {
            if (!this.searching) { // Searching always starts at the top
                scrollAmount = amount;
            }
        }
    }

    public static class ServerSlot extends VFPListEntry {

        private static final int ICON_SIZE = 24;
        private static final int TEXT_OFFSET = SLOT_MARGIN + ICON_SIZE + SLOT_MARGIN;

        private final CCServerInfo classiCubeServerInfo;

        public ServerSlot(CCServerInfo classiCubeServerInfo) {
            this.classiCubeServerInfo = classiCubeServerInfo;
        }

        @Override
        public @NonNull Component getNarration() {
            return Component.nullToEmpty(classiCubeServerInfo.name());
        }

        @Override
        public void mappedMouseClicked(double mouseX, double mouseY, int button) {
            ViaFabricPlusClassicMPPassProvider.classicubeMPPass = classiCubeServerInfo.mpPass();
            ConnectionUtil.connect(classiCubeServerInfo.name(), classiCubeServerInfo.ip() + ":" + classiCubeServerInfo.port(), LegacyProtocolVersion.c0_30cpe);
        }

        @Override
        public void mappedRender(GuiGraphicsExtractor context, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.blit(RenderPipelines.GUI_TEXTURED, FaviconTexture.MISSING_LOCATION, SLOT_MARGIN, (entryHeight - ICON_SIZE) / 2, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

            final Font font = Minecraft.getInstance().font;
            final int nameY = entryHeight / 2 - font.lineHeight - 1;
            final int softwareY = entryHeight / 2 + 1;

            final String players = classiCubeServerInfo.players() + "/" + classiCubeServerInfo.maxPlayers();
            final int playersX = entryWidth - font.width(players) - SLOT_MARGIN;
            context.text(font, players, playersX, nameY, -1);

            // Long names and software names are cut off instead of overlapping the player count
            context.enableScissor(TEXT_OFFSET, 0, playersX - SLOT_MARGIN, entryHeight);
            context.text(font, classiCubeServerInfo.name(), TEXT_OFFSET, nameY, -1);
            // The software name carries its own colors
            context.text(font, classiCubeServerInfo.software().replace('&', ChatFormatting.PREFIX_CODE), TEXT_OFFSET, softwareY, -1);
            context.disableScissor();
        }
    }

}
