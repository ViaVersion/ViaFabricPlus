/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2024 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2024 ViaVersion and contributors
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

package com.viaversion.viafabricplus.screen.settings;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.screen.VFPList;
import com.viaversion.viafabricplus.screen.VFPScreen;
import com.viaversion.viafabricplus.settings.base.AbstractSetting;
import com.viaversion.viafabricplus.settings.base.SettingGroup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class SettingsScreen extends VFPScreen {

    public static final SettingsScreen INSTANCE = new SettingsScreen();

    public SettingsScreen() {
        super(Text.translatable("screen.viafabricplus.settings"), true);
    }

    @Override
    protected void init() {
        this.setupDefaultSubtitle();
        this.addDrawableChild(new SlotList(this.client, width, height, 3 + 3 /* start offset */ + (textRenderer.fontHeight + 2) * 3 /* title is 2 */, -5, (textRenderer.fontHeight + 2) * 2));

        super.init();
    }

    public static class SlotList extends VFPList {
        private static double scrollAmount;

        public SlotList(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int entryHeight) {
            super(minecraftClient, width, height, top, bottom, entryHeight);

            for (SettingGroup group : ViaFabricPlus.global().getSettingsManager().getGroups()) {
                this.addEntry(new TitleRenderer(group.getName()));

                for (AbstractSetting<?> setting : group.getSettings()) {
                    this.addEntry(setting.makeSettingRenderer());
                }
            }
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

}