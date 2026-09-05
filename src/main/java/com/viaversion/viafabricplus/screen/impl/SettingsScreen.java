/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - Florian Reuth <git@florianreuth.de>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2026 ViaVersion and contributors
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
import com.viaversion.viafabricplus.api.settings.base.BooleanSetting;
import com.viaversion.viafabricplus.api.settings.base.EnumSetting;
import com.viaversion.viafabricplus.api.settings.base.Setting;
import com.viaversion.viafabricplus.api.settings.base.SettingGroup;
import com.viaversion.viafabricplus.api.settings.base.VersionedBooleanSetting;
import com.viaversion.viafabricplus.screen.base.VFPTabbedScreen;
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry;
import com.viaversion.viafabricplus.screen.impl.settings.BooleanListEntry;
import com.viaversion.viafabricplus.screen.impl.settings.EnumListEntry;
import com.viaversion.viafabricplus.screen.impl.settings.VersionedBooleanListEntry;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public final class SettingsScreen extends VFPTabbedScreen<SettingGroup> {

    private static final int ROW_WIDTH = 360;
    private static final int LIST_BOTTOM_MARGIN = 5; // The screen has no buttons below the list

    public SettingsScreen() {
        super(Component.translatable("screen.viafabricplus.settings"), true);
    }

    @Override
    protected List<SettingGroup> tabs() {
        return ViaFabricPlusImpl.impl().settings().groups();
    }

    @Override
    protected Component tabTitle(final SettingGroup tab) {
        return tab.name();
    }

    @Override
    protected int entryHeight() {
        return (this.font.lineHeight + 2) * 2;
    }

    @Override
    protected int rowWidth(final int screenWidth) {
        return Math.min(ROW_WIDTH, screenWidth - 20);
    }

    @Override
    protected int listBottomMargin() {
        return LIST_BOTTOM_MARGIN;
    }

    @Override
    protected List<VFPListEntry> entries(final SettingGroup tab) {
        return tab.settings().stream()
            .map(SettingsScreen::entry)
            .filter(Objects::nonNull)
            .toList();
    }

    @Override
    protected List<VFPListEntry> results(final String query) {
        return this.tabs().stream()
            .flatMap(group -> group.settings().stream())
            .filter(setting -> setting.name().getString().toLowerCase(Locale.ROOT).contains(query))
            .map(SettingsScreen::entry)
            .filter(Objects::nonNull)
            .toList();
    }

    private static @Nullable VFPListEntry entry(final Setting setting) {
        return switch (setting) {
            case final VersionedBooleanSetting versionedBooleanSetting ->
                new VersionedBooleanListEntry(versionedBooleanSetting);
            case final BooleanSetting booleanSetting -> new BooleanListEntry(booleanSetting);
            case final EnumSetting<?> enumSetting -> new EnumListEntry<>(enumSetting);
            default -> {
                ViaFabricPlusImpl.impl().logger().warn("Unknown setting type: {}", setting.getClass().getName());
                yield null;
            }
        };
    }

}
