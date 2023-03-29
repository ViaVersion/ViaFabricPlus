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
package de.florianmichael.viafabricplus.screen;

import com.github.allinkdev.betacraftserverlistparser.BetacraftServerList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.ClassiCubeAccountHandler;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeAccount;
import de.florianmichael.viafabricplus.screen.classicube.ClassiCubeLoginScreen;
import de.florianmichael.viafabricplus.screen.classicube.ClassiCubeServerListScreen;
import de.florianmichael.viafabricplus.screen.settings.SettingsScreen;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.util.ScreenUtil;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.platform.InternalProtocolList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings({"DataFlowIssue", "DuplicatedCode"})
public class ProtocolSelectionScreen extends Screen {
    private final static ProtocolSelectionScreen INSTANCE = new ProtocolSelectionScreen();
    public Screen prevScreen;

    protected ProtocolSelectionScreen() {
        super(Text.literal("Protocol selection"));
    }

    public static void open(final Screen current) {
        INSTANCE.prevScreen = current;

        RenderSystem.recordRenderCall(() -> MinecraftClient.getInstance().setScreen(INSTANCE));
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, height - 30, textRenderer.fontHeight + 4));
        this.addDrawableChild(ButtonWidget.builder(Text.literal("<-"), button -> this.close()).position(5, 5).size(20, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("words.viafabricplus.settings"), button -> client.setScreen(SettingsScreen.get(this))).position(width - 98 - 5, 5).size(98, 20).build());
        final ClassiCubeAccount classiCubeAccount = ClassiCubeAccountHandler.INSTANCE.getAccount();
        ButtonWidget.Builder classiCubeBuilder = ButtonWidget.builder(Text.literal("ClassiCube"), button -> {
            if (classiCubeAccount == null || classiCubeAccount.token == null) {
                client.setScreen(ClassiCubeLoginScreen.get(this));
                return;
            }
            client.setScreen(ClassiCubeServerListScreen.get(this));
        }).position(width - 98 - 5, height - 25).size(98, 20);
        if (classiCubeAccount == null || classiCubeAccount.token == null) {
            classiCubeBuilder = classiCubeBuilder.tooltip(Tooltip.of(Text.translatable("classicube.viafabricplus.warning")));
        }
        this.addDrawableChild(classiCubeBuilder.build());
        ButtonWidget.Builder betaCraftBuilder = ButtonWidget.builder(Text.literal("BetaCraft"), button -> {
            if (BetaCraftScreen.SERVER_LIST == null) {
                CompletableFuture.runAsync(() -> BetacraftServerList.getFuture().whenComplete((betacraftServerList, throwable) -> {
                    if (throwable != null) {
                        MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new NoticeScreen(() -> RenderSystem.recordRenderCall(() -> client.setScreen(BetaCraftScreen.get(this))), Text.literal("Microsoft Bedrock login"), Text.translatable("betacraft.viafabricplus.error"), Text.translatable("words.viafabricplus.cancel"), false)));
                        return;
                    }
                    BetaCraftScreen.SERVER_LIST = betacraftServerList;
                    RenderSystem.recordRenderCall(() -> client.setScreen(BetaCraftScreen.get(this)));
                }));
            } else client.setScreen(BetaCraftScreen.get(this));

            button.setMessage(Text.literal("BetaCraft"));
        }).position(5, height - 25).size(98, 20);
        if (BetaCraftScreen.SERVER_LIST == null) {
            betaCraftBuilder = betaCraftBuilder.tooltip(Tooltip.of(Text.translatable("betacraft.viafabricplus.warning")));
        }
        this.addDrawableChild(betaCraftBuilder.build());
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        matrices.push();
        matrices.scale(2F, 2F, 2F);
        drawCenteredTextWithShadow(matrices, textRenderer, "ViaFabricPlus", width / 4, 3, Color.ORANGE.getRGB());
        matrices.pop();
        drawCenteredTextWithShadow(matrices, textRenderer, "https://github.com/FlorianMichael/ViaFabricPlus", width / 2, (textRenderer.fontHeight + 2) * 2 + 3, -1);
    }

    @Override
    public void close() {
        client.setScreen(prevScreen);
    }

    public static class SlotList extends AlwaysSelectedEntryListWidget<ProtocolSlot> {

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            InternalProtocolList.getProtocols().stream().map(ProtocolSlot::new).forEach(this::addEntry);
        }
    }

    public static class ProtocolSlot extends AlwaysSelectedEntryListWidget.Entry<ProtocolSlot> {
        private final ProtocolVersion protocolVersion;

        public ProtocolSlot(final ProtocolVersion protocolVersion) {
            this.protocolVersion = protocolVersion;
        }

        @Override
        public Text getNarration() {
            return Text.literal(this.protocolVersion.getName());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            ViaLoadingBase.getClassWrapper().reload(this.protocolVersion);
            ScreenUtil.playClickSound();
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final boolean isSelected = ProtocolHack.getTargetVersion().getVersion() == protocolVersion.getVersion();

            matrices.push();
            matrices.translate(x, y - 1, 0);

            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            drawCenteredTextWithShadow(matrices, textRenderer, this.protocolVersion.getName(), entryWidth / 2, entryHeight / 2 - textRenderer.fontHeight / 2, isSelected ? Color.GREEN.getRGB() : Color.RED.getRGB());
            matrices.pop();
        }
    }
}
