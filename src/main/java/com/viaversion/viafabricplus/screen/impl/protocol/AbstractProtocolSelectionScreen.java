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

package com.viaversion.viafabricplus.screen.impl.protocol;

import com.viaversion.viafabricplus.protocoltranslator.util.ProtocolVersionDetector;
import com.viaversion.viafabricplus.screen.base.VFPTabbedScreen;
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.network.chat.Component;

public abstract class AbstractProtocolSelectionScreen extends VFPTabbedScreen<ProtocolCategory> {

    private static final int SLOT_HEIGHT = 32;
    private static final int ROW_WIDTH = 260;

    protected AbstractProtocolSelectionScreen(final Component title, final boolean backButton) {
        super(title, backButton);
    }

    protected abstract void select(final ProtocolVersion version);

    protected abstract boolean selected(final ProtocolVersion version);

    protected abstract boolean selectable();

    @Override
    protected List<ProtocolCategory> tabs() {
        return List.of(ProtocolCategory.values());
    }

    @Override
    protected Component tabTitle(final ProtocolCategory tab) {
        return tab.title();
    }

    @Override
    protected int entryHeight() {
        return SLOT_HEIGHT;
    }

    @Override
    protected int rowWidth(final int screenWidth) {
        return Math.min(ROW_WIDTH, screenWidth - 20);
    }

    @Override
    protected List<VFPListEntry> entries(final ProtocolCategory tab) {
        final List<VFPListEntry> entries = new ArrayList<>();
        if (tab == ProtocolCategory.MODERN) {
            // Auto Detect is no Minecraft version, it is pinned to the first tab as it is not limited to a single category
            entries.add(new ProtocolSlot(ProtocolVersionDetector.AUTO_DETECT_VERSION, this));
        }

        for (final ProtocolVersion version : ProtocolVersion.getReversedProtocols()) {
            if (version != ProtocolVersionDetector.AUTO_DETECT_VERSION && ProtocolCategory.of(version) == tab) {
                entries.add(new ProtocolSlot(version, this));
            }
        }
        return entries;
    }

    @Override
    protected List<VFPListEntry> results(final String query) {
        return ProtocolVersion.getReversedProtocols().stream()
            .filter(version -> matches(version, query))
            .<VFPListEntry>map(version -> new ProtocolSlot(version, this))
            .toList();
    }

    @Override
    protected ProtocolCategory initialTab() {
        // Open the tab holding the selected version so it doesn't have to be searched for
        for (final ProtocolVersion version : ProtocolVersion.getReversedProtocols()) {
            if (this.selected(version)) {
                // Auto Detect is no Minecraft version and therefore has no category of its own
                return version == ProtocolVersionDetector.AUTO_DETECT_VERSION ? ProtocolCategory.MODERN : ProtocolCategory.of(version);
            }
        }

        return ProtocolCategory.MODERN;
    }

    private static boolean matches(final ProtocolVersion version, final String query) {
        if (version.getName().toLowerCase(Locale.ROOT).contains(query)) {
            return true;
        }
        // Ranges like 1.7.6-1.7.10 are matched by every version they include as well
        for (final String includedVersion : version.getIncludedVersions()) {
            if (includedVersion.toLowerCase(Locale.ROOT).contains(query)) {
                return true;
            }
        }

        final ProtocolVersionMetadata metadata = ProtocolVersionMetadata.of(version);
        return metadata != null && metadata.title() != null && metadata.title().toLowerCase(Locale.ROOT).contains(query);
    }

}
