/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.screen.base;

import de.florianmichael.classic4j.BetaCraftHandler;
import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.florianmichael.viafabricplus.screen.VFPScreen;
import de.florianmichael.viafabricplus.screen.classic4j.BetaCraftScreen;
import de.florianmichael.viafabricplus.screen.classic4j.ClassiCubeLoginScreen;
import de.florianmichael.viafabricplus.screen.classic4j.ClassiCubeServerListScreen;
import de.florianmichael.viafabricplus.screen.settings.SettingsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.raphimc.vialoader.util.VersionEnum;

import java.awt.*;

public class ProtocolSelectionScreen extends VFPScreen {

    public static final ProtocolSelectionScreen INSTANCE = new ProtocolSelectionScreen();

    private ButtonWidget betaCraftButton;

    protected ProtocolSelectionScreen() {
        super("ViaFabricPlus", true);
        this.setupDefaultSubtitle();
    }

    @Override
    protected void init() {
        // List and Settings
        this.addDrawableChild(new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, 30, textRenderer.fontHeight + 4));
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("base.viafabricplus.settings"), button -> SettingsScreen.INSTANCE.open(this)).position(width - 98 - 5, 5).size(98, 20).build());

        // ClassiCube
        final boolean loggedIn = ViaFabricPlus.global().getSaveManager().getAccountsSave().getClassicubeAccount() != null;

        ButtonWidget.Builder classiCubeBuilder = ButtonWidget.builder(Text.literal("ClassiCube"), button -> {
            if (!loggedIn) {
                ClassiCubeLoginScreen.INSTANCE.open(prevScreen);
                return;
            }
            ClassiCubeServerListScreen.INSTANCE.open(prevScreen);
        }).position(width - 98 - 5, height - 25).size(98, 20);
        if (!loggedIn) {
            classiCubeBuilder = classiCubeBuilder.tooltip(Tooltip.of(Text.translatable("classicube.viafabricplus.warning")));
        }
        this.addDrawableChild(classiCubeBuilder.build());

        // BetaCraft
        ButtonWidget.Builder betaCraftBuilder = ButtonWidget.builder(Text.literal("BetaCraft"), button -> {
            if (BetaCraftScreen.SERVER_LIST == null) {
                betaCraftButton = button;

                BetaCraftHandler.requestV1ServerList(serverList -> {
                    BetaCraftScreen.SERVER_LIST = serverList;

                    BetaCraftScreen.INSTANCE.open(this);
                }, throwable -> showErrorScreen("BetaCraft", throwable, this));

            } else {
                BetaCraftScreen.INSTANCE.open(this);
            }
        }).position(5, height - 25).size(98, 20);
        if (BetaCraftScreen.SERVER_LIST == null) {
            betaCraftBuilder = betaCraftBuilder.tooltip(Tooltip.of(Text.translatable("betacraft.viafabricplus.warning")));
        }
        this.addDrawableChild(betaCraftBuilder.build());

        super.init();
    }

    @Override
    public void tick() {
        if (betaCraftButton != null) betaCraftButton.setMessage(Text.of("Loading..."));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        this.renderTitle(context);
    }

    public static class SlotList extends AlwaysSelectedEntryListWidget<ProtocolSlot> {

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height - top - bottom, top, entryHeight);

            VersionEnum.SORTED_VERSIONS.stream().map(ProtocolSlot::new).forEach(this::addEntry);
        }
    }

    public static class ProtocolSlot extends AlwaysSelectedEntryListWidget.Entry<ProtocolSlot> {
        private final VersionEnum versionEnum;

        public ProtocolSlot(final VersionEnum versionEnum) {
            this.versionEnum = versionEnum;
        }

        @Override
        public Text getNarration() {
            return Text.literal(this.versionEnum.getName());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            ProtocolHack.setTargetVersion(this.versionEnum);
            playClickSound();
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final boolean isSelected = ProtocolHack.getTargetVersion().getVersion() == versionEnum.getVersion();

            final MatrixStack matrices = context.getMatrices();

            matrices.push();
            matrices.translate(x, y - 1, 0);

            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            context.drawCenteredTextWithShadow(textRenderer, this.versionEnum.getName(), entryWidth / 2, entryHeight / 2 - textRenderer.fontHeight / 2, isSelected ? Color.GREEN.getRGB() : Color.RED.getRGB());
            matrices.pop();
        }
    }

}
