/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.screen.thirdparty.classicube;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.classic4j.ClassiCubeHandler;
import de.florianmichael.classic4j.api.LoginProcessHandler;
import de.florianmichael.classic4j.model.classicube.account.CCAccount;
import de.florianmichael.classic4j.model.classicube.server.CCServerInfo;
import de.florianmichael.viafabricplus.definition.account.ClassiCubeAccountHandler;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import de.florianmichael.viafabricplus.protocolhack.provider.vialegacy.ViaFabricPlusClassicMPPassProvider;
import de.florianmichael.viafabricplus.base.screen.VFPScreen;
import de.florianmichael.viafabricplus.screen.base.ProtocolSelectionScreen;
import de.florianmichael.viafabricplus.base.screen.MappedSlotEntry;
import de.florianmichael.viafabricplus.base.settings.groups.AuthenticationSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.ArrayList;
import java.util.List;

public class ClassiCubeServerListScreen extends VFPScreen {
    public final static List<CCServerInfo> SERVER_LIST = new ArrayList<>();
    public final static ClassiCubeServerListScreen INSTANCE = new ClassiCubeServerListScreen();

    public static void open(final Screen prevScreen, final LoginProcessHandler loginProcessHandler) {
        ClassiCubeHandler.requestServerList(ClassiCubeAccountHandler.INSTANCE.getAccount(), ccServerList -> {
            ClassiCubeServerListScreen.SERVER_LIST.addAll(ccServerList.servers());
            RenderSystem.recordRenderCall(() -> ClassiCubeServerListScreen.INSTANCE.open(prevScreen));
        }, loginProcessHandler::handleException);
    }

    public ClassiCubeServerListScreen() {
        super("ClassiCube ServerList", true);
    }

    @Override
    protected void init() {
        this.addDrawableChild(new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, height + 5, (textRenderer.fontHeight + 4) * 3));

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("misc.viafabricplus.logout"), button -> {
            close();
            ClassiCubeAccountHandler.INSTANCE.setAccount(null);
            SERVER_LIST.clear();
        }).position(width - 98 - 5, 5).size(98, 20).build());

        super.init();
    }

    @Override
    public void close() {
        ProtocolSelectionScreen.INSTANCE.open(prevScreen);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        final CCAccount account = ClassiCubeAccountHandler.INSTANCE.getAccount();
        if (account == null) return;

        this.renderTitle(context, Text.of("ClassiCube Profile: " + account.username()));
    }

    public static class SlotList extends AlwaysSelectedEntryListWidget<MappedSlotEntry> {

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            SERVER_LIST.forEach(serverInfo -> this.addEntry(new ServerSlot(serverInfo)));
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 140;
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.width - 5;
        }
    }


    public static class ServerSlot extends MappedSlotEntry {
        private final CCServerInfo classiCubeServerInfo;

        public ServerSlot(CCServerInfo classiCubeServerInfo) {
            this.classiCubeServerInfo = classiCubeServerInfo;
        }

        @Override
        public Text getNarration() {
            return Text.literal(classiCubeServerInfo.name());
        }

        @Override
        public void mappedMouseClicked(double mouseX, double mouseY, int button) {
            final ServerAddress serverAddress = ServerAddress.parse(classiCubeServerInfo.ip() + ":" + classiCubeServerInfo.port());
            final ServerInfo entry = new ServerInfo(classiCubeServerInfo.name(), serverAddress.getAddress(), false);
            ViaFabricPlusClassicMPPassProvider.classiCubeMPPass = classiCubeServerInfo.mpPass();

            if (AuthenticationSettings.INSTANCE.forceCPEIfUsingClassiCube.getValue()) {
                ((IServerInfo) entry).viafabricplus_forceVersion(VersionEnum.c0_30cpe);
            }

            ConnectScreen.connect(MinecraftClient.getInstance().currentScreen, MinecraftClient.getInstance(), serverAddress, entry, false);
            super.mappedMouseClicked(mouseX, mouseY, button);
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
