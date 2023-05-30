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
package de.florianmichael.viafabricplus.information.impl;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viafabricplus.information.AbstractInformationGroup;
import net.raphimc.vialegacy.protocols.release.protocol1_8to1_7_6_10.storage.EntityTracker;
import net.raphimc.vialoader.util.VersionEnum;
import net.raphimc.vialoader.util.VersionRange;

import java.util.List;

public class V1_7_10Information extends AbstractInformationGroup {

    public V1_7_10Information() {
        super(VersionRange.andOlder(VersionEnum.r1_7_6tor1_7_10));
    }

    @Override
    public void applyInformation(UserConnection userConnection, List<String> output) {
        if (userConnection.has(EntityTracker.class)) {
            final int entities = userConnection.get(EntityTracker.class).getTrackedEntities().size();
            if (entities != 0) output.add("Entity Tracker: " + entities);
        }
    }
}
