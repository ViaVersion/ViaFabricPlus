/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD
 * Copyright (C) 2023      RK_01/RaphiMC and contributors
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

import de.florianmichael.viafabricplus.screen.VFPScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.raphimc.vialoader.util.VersionEnum;

import java.util.function.Consumer;

public class PerServerVersionScreen extends VFPScreen {

    private final Consumer<VersionEnum> selectionConsumer;

    public PerServerVersionScreen(final Screen prevScreen, final Consumer<VersionEnum> selectionConsumer) {
        super("Force version", false);

        this.prevScreen = prevScreen;
        this.selectionConsumer = selectionConsumer;

        this.setupSubtitle(Text.translatable("base.viafabricplus.force_version_title"));
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, height + 5, textRenderer.fontHeight + 4));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        this.renderTitle(context);
    }

    public class SlotList extends AlwaysSelectedEntryListWidget<DummyProtocolSlot> {

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            this.addEntry(new ResetProtocolSlot());
            VersionEnum.SORTED_VERSIONS.stream().map(ViaProtocolSlot::new).forEach(this::addEntry);
        }
    }


    public abstract static class DummyProtocolSlot extends AlwaysSelectedEntryListWidget.Entry<DummyProtocolSlot> {
    }

    public class ResetProtocolSlot extends DummyProtocolSlot {

        @Override
        public Text getNarration() {
            return Text.translatable("base.viafabricplus.cancel_and_reset");
        }


        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            selectionConsumer.accept(null);
            playClickSound();
            close();

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            context.drawCenteredTextWithShadow(textRenderer, ((MutableText) getNarration()).formatted(Formatting.GOLD), x + entryWidth / 2, y + entryHeight / 2 - textRenderer.fontHeight / 2, -1);
        }
    }

    public class ViaProtocolSlot extends DummyProtocolSlot {
        private final VersionEnum versionEnum;

        public ViaProtocolSlot(final VersionEnum versionEnum) {
            this.versionEnum = versionEnum;
        }

        @Override
        public Text getNarration() {
            return Text.literal(this.versionEnum.getName());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            selectionConsumer.accept(versionEnum);
            playClickSound();
            close();

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            context.drawCenteredTextWithShadow(textRenderer, this.versionEnum.getName(), x + entryWidth / 2, y - 1 + entryHeight / 2 - textRenderer.fontHeight / 2, -1);
        }
    }

}
