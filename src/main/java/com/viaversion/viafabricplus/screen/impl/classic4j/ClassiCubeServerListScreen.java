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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

import java.util.ArrayList;
import java.util.List;

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
            setupSubtitle(Text.translatable("betacraft.viafabricplus.loading"));
            return;
        }

        this.addDrawableChild(new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, -5, (textRenderer.fontHeight + 4) * 3));

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("base.viafabricplus.logout"), button -> {
            SaveManager.INSTANCE.getAccountsSave().setClassicubeAccount(null);
            SERVER_LIST = null;
            close();
        }).position(width - 60 - 5, 5).size(60, 20).build());

        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (SERVER_LIST == null) {
            return;
        }

        final CCAccount account = SaveManager.INSTANCE.getAccountsSave().getClassicubeAccount();
        context.drawTextWithShadow(textRenderer, Text.translatable("classicube.viafabricplus.profile"), 32, 6, -1);
        context.drawTextWithShadow(textRenderer, Text.of(account.username()), 32, 16, -1);
    }

    @Override
    protected boolean subtitleCentered() {
        return SERVER_LIST == null;
    }

    public static class SlotList extends VFPList {
        private static double scrollAmount;

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
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
        public Text getNarration() {
            return Text.of(classiCubeServerInfo.name());
        }

        @Override
        public void mappedMouseClicked(double mouseX, double mouseY, int button) {
            final boolean selectCPE = AuthenticationSettings.INSTANCE.automaticallySelectCPEInClassiCubeServerList.getValue();
            ViaFabricPlusClassicMPPassProvider.classicubeMPPass = classiCubeServerInfo.mpPass();

            ConnectionUtil.connect(classiCubeServerInfo.name(), classiCubeServerInfo.ip() + ":" + classiCubeServerInfo.port(), selectCPE ? LegacyProtocolVersion.c0_30cpe : null);
        }

        @Override
        public void mappedRender(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            context.drawCenteredTextWithShadow(textRenderer, classiCubeServerInfo.name(), entryWidth / 2, entryHeight / 2 - textRenderer.fontHeight / 2, -1);

            context.drawTextWithShadow(textRenderer, classiCubeServerInfo.software().replace('&', Formatting.FORMATTING_CODE_PREFIX), 1, 1, -1);
            final String playerText = classiCubeServerInfo.players() + "/" + classiCubeServerInfo.maxPlayers();
            context.drawTextWithShadow(textRenderer, playerText, entryWidth - textRenderer.getWidth(playerText) - 4 /* magic value from line 132 */ - 1, 1, -1);
        }
    }

}
