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

package de.florianmichael.viafabricplus.screen.settings;

import de.florianmichael.viafabricplus.ViaFabricPlus;
import de.florianmichael.viafabricplus.screen.VFPListEntry;
import de.florianmichael.viafabricplus.screen.VFPScreen;
import de.florianmichael.viafabricplus.settings.base.AbstractSetting;
import de.florianmichael.viafabricplus.settings.base.SettingGroup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

public class SettingsScreen extends VFPScreen {

    public static final SettingsScreen INSTANCE = new SettingsScreen();

    public SettingsScreen() {
        super("Setting", true);
        this.setupDefaultSubtitle();
    }

    @Override
    protected void init() {
        this.addDrawableChild(new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, -5, (textRenderer.fontHeight + 2) * 2));

        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        this.renderTitle(context);
    }

    public static class SlotList extends AlwaysSelectedEntryListWidget<VFPListEntry> {

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height - top - bottom, top, entryHeight);

            for (SettingGroup group : ViaFabricPlus.global().getSettingsManager().getGroups()) {
                this.addEntry(new TitleRenderer(group.getName()));

                for (AbstractSetting<?> setting : group.getSettings()) {
                    this.addEntry(setting.makeSettingRenderer());
                }
            }
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

}
