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

package com.viaversion.viafabricplus.protocoltranslator.impl.viaversion;

import com.viaversion.viafabricplus.settings.impl.GeneralSettings;
import com.viaversion.viaversion.configuration.AbstractViaConfig;
import com.viaversion.viaversion.util.ConfigSection;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public final class ViaFabricPlusConfig extends AbstractViaConfig {

    public ViaFabricPlusConfig(File configFile, Logger logger) {
        super(configFile, logger);
    }

    @Override
    protected boolean updateConfig() {
        boolean modified = false;

        final ConfigSection existingConfig = originalRootSection();
        if (existingConfig == null) {
            // Defaults since 2.9.9, the directory was different back then so when upgrading the file won't be present anyway
            set("fix-infested-block-breaking", false);
            set("shield-blocking", false);
            set("no-delay-shield-blocking", true);
            set("handle-invalid-item-count", true);
            set("chunk-border-fix", true);
            modified = true;
        }

        // 4.4.3 additions
        if (modified || !existingConfig.contains("send-player-details")) {
            set("send-player-details", false);
            modified = true;
        }
        return super.updateConfig() || modified;
    }

    @Override
    public List<String> getUnsupportedOptions() {
        final List<String> unsupported = super.getUnsupportedOptions();
        unsupported.add("simulate-pt");
        unsupported.add("fix-1_21-placement-rotation");
        unsupported.add("team-colour-fix");
        unsupported.add("cancel-swing-in-inventory");
        unsupported.add("cancel-block-sounds");
        unsupported.add("use-1_8-hitbox-margin");
        return unsupported;
    }

    @Override
    public boolean isCheckForUpdates() {
        return false;
    }

    @Override
    public boolean isSimulatePlayerTick() {
        return false;
    }

    @Override
    public boolean isServersideBlockConnections() {
        if (GeneralSettings.INSTANCE.experimentalBlockConnections.getValue()) {
            return false;
        } else {
            return super.isServersideBlockConnections();
        }
    }

    @Override
    public boolean fix1_21PlacementRotation() {
        return false;
    }

    @Override
    public boolean is1_13TeamColourFix() {
        return false;
    }

    @Override
    public boolean cancelSwingInInventory() {
        return false;
    }

    @Override
    public boolean cancelBlockSounds() {
        return false;
    }

    @Override
    public boolean use1_8HitboxMargin() {
        return false;
    }

}
