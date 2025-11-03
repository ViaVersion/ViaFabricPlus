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

package com.viaversion.viafabricplus.screen.impl;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.api.settings.AbstractSetting;
import com.viaversion.viafabricplus.api.settings.SettingGroup;
import com.viaversion.viafabricplus.api.settings.type.BooleanSetting;
import com.viaversion.viafabricplus.api.settings.type.ButtonSetting;
import com.viaversion.viafabricplus.api.settings.type.ModeSetting;
import com.viaversion.viafabricplus.api.settings.type.VersionedBooleanSetting;
import com.viaversion.viafabricplus.screen.VFPList;
import com.viaversion.viafabricplus.screen.VFPScreen;
import com.viaversion.viafabricplus.screen.impl.settings.BooleanListEntry;
import com.viaversion.viafabricplus.screen.impl.settings.ButtonListEntry;
import com.viaversion.viafabricplus.screen.impl.settings.ModeListEntry;
import com.viaversion.viafabricplus.screen.impl.settings.TitleEntry;
import com.viaversion.viafabricplus.screen.impl.settings.VersionedBooleanListEntry;
import com.viaversion.viafabricplus.settings.SettingsManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class SettingsScreen extends VFPScreen {

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

            for (SettingGroup group : SettingsManager.INSTANCE.getGroups()) {
                this.addEntry(new TitleEntry(group.getName()));

                for (AbstractSetting<?> setting : group.getSettings()) {
                    switch (setting) {
                        case final BooleanSetting booleanSetting -> this.addEntry(new BooleanListEntry(booleanSetting));
                        case final ButtonSetting buttonSetting -> this.addEntry(new ButtonListEntry(buttonSetting));
                        case final ModeSetting modeSetting -> this.addEntry(new ModeListEntry(modeSetting));
                        case final VersionedBooleanSetting versionedBooleanSetting ->
                            this.addEntry(new VersionedBooleanListEntry(versionedBooleanSetting));
                        default ->
                            ViaFabricPlusImpl.INSTANCE.getLogger().warn("Unknown setting type: {}", setting.getClass().getName());
                    }
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
