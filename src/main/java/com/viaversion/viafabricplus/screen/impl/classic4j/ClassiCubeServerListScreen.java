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

package com.viaversion.viafabricplus.screen.impl.classic4j;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.protocoltranslator.impl.provider.vialegacy.ViaFabricPlusClassicMPPassProvider;
import com.viaversion.viafabricplus.save.SaveManager;
import com.viaversion.viafabricplus.screen.VFPList;
import com.viaversion.viafabricplus.screen.VFPListEntry;
import com.viaversion.viafabricplus.screen.VFPScreen;
import com.viaversion.viafabricplus.settings.impl.AuthenticationSettings;
import com.viaversion.viafabricplus.util.ConnectionUtil;
import de.florianmichael.classic4j.ClassiCubeHandler;
import de.florianmichael.classic4j.model.classicube.account.CCAccount;
import de.florianmichael.classic4j.model.classicube.server.CCServerInfo;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

import static com.viaversion.viafabricplus.screen.VFPListEntry.SLOT_MARGIN;

public final class ClassiCubeServerListScreen extends VFPScreen {

    public static final ClassiCubeServerListScreen INSTANCE = new ClassiCubeServerListScreen();

    private static List<CCServerInfo> SERVER_LIST;
    private static final String CLASSICUBE_SERVER_LIST_URL = "https://www.classicube.net/server/list/";

    public ClassiCubeServerListScreen() {
        super("ClassiCube", true);
    }

    @Override
    protected void init() {
        final CCAccount account = SaveManager.INSTANCE.getAccountsSave().getClassicubeAccount();
        if (SERVER_LIST == null) {
            ClassiCubeHandler.requestServerList(account, serverList -> {
                SERVER_LIST = new ArrayList<>(serverList.servers());
                open(prevScreen);
                setupUrlSubtitle(CLASSICUBE_SERVER_LIST_URL);
            }, throwable -> {
                ViaFabricPlusImpl.INSTANCE.getLogger().error("Error while loading ClassiCube servers!", throwable);
                showErrorScreen(INSTANCE.getTitle(), throwable, prevScreen);
            });
            setupSubtitle(Component.translatable("betacraft.viafabricplus.loading"));
            return;
        }

        final int entryHeight = (font.lineHeight + 2) * 3; // title is 2
        this.addRenderableWidget(new SlotList(this.minecraft, width, height, 2 * SLOT_MARGIN + entryHeight, -5, entryHeight));

        this.addRenderableWidget(Button.builder(Component.translatable("base.viafabricplus.logout"), button -> {
            SaveManager.INSTANCE.getAccountsSave().setClassicubeAccount(null);
            SERVER_LIST = null;
            onClose();
        }).pos(width - 60 - 5, 5).size(60, 20).build());

        super.init();
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (SERVER_LIST == null) {
            return;
        }

        final CCAccount account = SaveManager.INSTANCE.getAccountsSave().getClassicubeAccount();
        context.drawString(font, Component.translatable("classicube.viafabricplus.profile"), 32, 6, -1);
        context.drawString(font, Component.nullToEmpty(account.username()), 32, 16, -1);
    }

    @Override
    protected boolean subtitleCentered() {
        return SERVER_LIST == null;
    }

    public static class SlotList extends VFPList {
        private static double scrollAmount;

        public SlotList(Minecraft minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            SERVER_LIST.forEach(serverInfo -> this.addEntry(new ServerSlot(serverInfo)));
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
        private final CCServerInfo classiCubeServerInfo;

        public ServerSlot(CCServerInfo classiCubeServerInfo) {
            this.classiCubeServerInfo = classiCubeServerInfo;
        }

        @Override
        public Component getNarration() {
            return Component.nullToEmpty(classiCubeServerInfo.name());
        }

        @Override
        public void mappedMouseClicked(double mouseX, double mouseY, int button) {
            final boolean selectCPE = AuthenticationSettings.INSTANCE.automaticallySelectCPEInClassiCubeServerList.getValue();
            ViaFabricPlusClassicMPPassProvider.classicubeMPPass = classiCubeServerInfo.mpPass();

            ConnectionUtil.connect(classiCubeServerInfo.name(), classiCubeServerInfo.ip() + ":" + classiCubeServerInfo.port(), selectCPE ? LegacyProtocolVersion.c0_30cpe : null);
        }

        @Override
        public void mappedRender(GuiGraphics context, int x, int y, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final Font textRenderer = Minecraft.getInstance().font;
            context.drawCenteredString(textRenderer, classiCubeServerInfo.name(), entryWidth / 2, entryHeight / 2 - textRenderer.lineHeight / 2, -1);

            context.drawString(textRenderer, classiCubeServerInfo.software().replace('&', ChatFormatting.PREFIX_CODE), 1, 1, -1);
            final String playerText = classiCubeServerInfo.players() + "/" + classiCubeServerInfo.maxPlayers();
            context.drawString(textRenderer, playerText, entryWidth - textRenderer.width(playerText) - 1, 1, -1);
        }
    }

}
