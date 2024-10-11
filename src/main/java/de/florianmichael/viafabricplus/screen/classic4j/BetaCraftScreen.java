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

package de.florianmichael.viafabricplus.screen.classic4j;

import de.florianmichael.classic4j.BetaCraftHandler;
import de.florianmichael.classic4j.model.betacraft.BCServerInfoSpec;
import de.florianmichael.classic4j.model.betacraft.BCServerList;
import de.florianmichael.classic4j.model.betacraft.BCVersionCategory;
import de.florianmichael.viafabricplus.screen.VFPList;
import de.florianmichael.viafabricplus.screen.VFPListEntry;
import de.florianmichael.viafabricplus.screen.VFPScreen;
import de.florianmichael.viafabricplus.screen.settings.TitleRenderer;
import de.florianmichael.viafabricplus.util.ConnectionUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class BetaCraftScreen extends VFPScreen {

    public static final BetaCraftScreen INSTANCE = new BetaCraftScreen();

    public static BCServerList SERVER_LIST;
    private static final String BETA_CRAFT_SERVER_LIST_URL = "https://betacraft.uk/serverlist/";

    protected BetaCraftScreen() {
        super("BetaCraft", true);
    }

    @Override
    protected void init() {
        super.init();
        if (SERVER_LIST != null) {
            createView();
            return;
        }
        setupSubtitle(Text.translatable("betacraft.viafabricplus.loading"));
        BetaCraftHandler.requestV2ServerList(serverList -> {
            BetaCraftScreen.SERVER_LIST = serverList;
            createView();
        }, throwable -> showErrorScreen(BetaCraftScreen.INSTANCE.getTitle(), throwable, this));
    }

    private void createView() {
        this.setupSubtitle(Text.of(BETA_CRAFT_SERVER_LIST_URL), ConfirmLinkScreen.opening(this, BETA_CRAFT_SERVER_LIST_URL));
        this.addDrawableChild(new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, -5, (textRenderer.fontHeight + 2) * 3));

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("base.viafabricplus.refresh"), button -> {
            SERVER_LIST = null;
            client.setScreen(this);
        }).position(width - 60 - 5, 5).size(60, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        this.renderTitle(context);
    }

    @Override
    protected boolean subtitleCentered() {
        return SERVER_LIST == null;
    }

    public static class SlotList extends VFPList<VFPListEntry> {
        private static double scrollAmount;

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);
            if (SERVER_LIST == null) return;

            for (BCVersionCategory value : BCVersionCategory.values()) {
                final List<BCServerInfoSpec> servers = SERVER_LIST.serversOfVersionCategory(value);
                if (servers.isEmpty()) continue;
                addEntry(new TitleRenderer(Text.of(value.name())));
                for (BCServerInfoSpec server : servers) {
                    addEntry(new ServerSlot(server));
                }
            }

            initScrollAmount(scrollAmount);
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
        private final BCServerInfoSpec server;

        public ServerSlot(BCServerInfoSpec server) {
            this.server = server;
        }

        @Override
        public Text getNarration() {
            return Text.of(server.name());
        }

        @Override
        public void mappedMouseClicked(double mouseX, double mouseY, int button) {
            ConnectionUtil.connect(server.name(), server.socket());
            super.mappedMouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void mappedRender(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            context.drawCenteredTextWithShadow(textRenderer, server.name() + Formatting.DARK_GRAY + " [" + server.gameVersion() + "]", entryWidth / 2, entryHeight / 2 - textRenderer.fontHeight / 2, -1);

            if (server.onlineMode()) {
                context.drawTextWithShadow(textRenderer, Text.translatable("base.viafabricplus.online_mode").formatted(Formatting.GREEN), 1, 1, -1);
            }
            final String playerText = server.playerCount() + "/" + server.playerLimit();
            context.drawTextWithShadow(textRenderer, playerText, entryWidth - textRenderer.getWidth(playerText) - 4 /* magic value from line 152 */ - 1, 1, -1);
        }
    }

}
