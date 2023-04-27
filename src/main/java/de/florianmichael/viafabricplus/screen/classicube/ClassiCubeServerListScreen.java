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
package de.florianmichael.viafabricplus.screen.classicube;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.classic4j.ClassiCubeHandler;
import de.florianmichael.classic4j.api.LoginProcessHandler;
import de.florianmichael.classic4j.model.classicube.CCServerInfo;
import de.florianmichael.viafabricplus.definition.c0_30.ClassiCubeAccountHandler;
import de.florianmichael.viafabricplus.injection.access.IServerInfo;
import de.florianmichael.viafabricplus.integration.Classic4JImpl;
import de.florianmichael.viafabricplus.protocolhack.provider.vialegacy.ViaFabricPlusClassicMPPassProvider;
import de.florianmichael.viafabricplus.screen.ProtocolSelectionScreen;
import de.florianmichael.viafabricplus.screen.base.MappedSlotEntry;
import de.florianmichael.viafabricplus.settings.groups.AuthenticationSettings;
import de.florianmichael.viafabricplus.util.ScreenUtil;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClassiCubeServerListScreen extends Screen {
    public final static List<CCServerInfo> SERVERS = new ArrayList<>();
    private final static ClassiCubeServerListScreen INSTANCE = new ClassiCubeServerListScreen();
    public Screen prevScreen;

    public static ClassiCubeServerListScreen get(final Screen prevScreen) {
        ClassiCubeServerListScreen.INSTANCE.prevScreen = prevScreen;
        return ClassiCubeServerListScreen.INSTANCE;
    }

    public static void open(final Screen prevScreen, final LoginProcessHandler loginProcessHandler) {
        ClassiCubeHandler.requestServerList(ClassiCubeAccountHandler.INSTANCE.getAccount(), ccServerList -> {
            ClassiCubeServerListScreen.SERVERS.addAll(ccServerList.servers());
            RenderSystem.recordRenderCall(() -> MinecraftClient.getInstance().setScreen(ClassiCubeServerListScreen.get(prevScreen)));
        }, loginProcessHandler::handleException);
    }

    public ClassiCubeServerListScreen() {
        super(Text.literal("ClassiCube ServerList"));
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, height + 5, (textRenderer.fontHeight + 4) * 3));
        this.addDrawableChild(ButtonWidget.builder(Text.literal("<-"), button -> this.close()).position(5, 5).size(20, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("words.viafabricplus.logout"), button -> {
            client.setScreen(prevScreen);
            ClassiCubeAccountHandler.INSTANCE.setAccount(null);
            SERVERS.clear();
        }).position(width - 98 - 5, 5).size(98, 20).build());
    }

    @Override
    public void close() {
        ProtocolSelectionScreen.open(new MultiplayerScreen(new TitleScreen()));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);

        final MatrixStack matrices = context.getMatrices();

        matrices.push();
        matrices.scale(2F, 2F, 2F);
        context.drawCenteredTextWithShadow(textRenderer, "ViaFabricPlus", width / 4, 3, Color.ORANGE.getRGB());
        matrices.pop();
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("ClassiCube Profile: " + ClassiCubeAccountHandler.INSTANCE.getAccount().username()), width / 2, (textRenderer.fontHeight + 2) * 2 + 3, -1);
    }

    public static class SlotList extends AlwaysSelectedEntryListWidget<MappedSlotEntry> {

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            SERVERS.forEach(serverInfo -> this.addEntry(new ServerSlot(serverInfo)));
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
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            final ServerAddress serverAddress = ServerAddress.parse(classiCubeServerInfo.ip() + ":" + classiCubeServerInfo.port());
            final ServerInfo entry = new ServerInfo(classiCubeServerInfo.name(), serverAddress.getAddress(), false);
            ViaFabricPlusClassicMPPassProvider.classiCubeMPPass = classiCubeServerInfo.mpPass();

            if (AuthenticationSettings.INSTANCE.forceCPEIfUsingClassiCube.getValue()) {
                ((IServerInfo) entry).viafabricplus_forceVersion(ViaLoadingBase.fromProtocolVersion(LegacyProtocolVersion.c0_30cpe));
            }

            ConnectScreen.connect(MinecraftClient.getInstance().currentScreen, MinecraftClient.getInstance(), serverAddress, entry, false);
            ScreenUtil.playClickSound();
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void mappedRenderer(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            context.drawCenteredTextWithShadow(textRenderer, classiCubeServerInfo.name(), entryWidth / 2, entryHeight / 2 - textRenderer.fontHeight / 2, -1);

            context.drawTextWithShadow(textRenderer, classiCubeServerInfo.software().replace('&', Formatting.FORMATTING_CODE_PREFIX), 1, 1, -1);
            final String playerText = classiCubeServerInfo.players() + "/" + classiCubeServerInfo.maxPlayers();
            context.drawTextWithShadow(textRenderer, playerText, entryWidth - textRenderer.getWidth(playerText) - 4 /* magic value from line 132 */ - 1, 1, -1);
        }
    }
}
