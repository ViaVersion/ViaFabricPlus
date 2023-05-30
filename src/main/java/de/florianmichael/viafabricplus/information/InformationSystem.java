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
package de.florianmichael.viafabricplus.information;

import de.florianmichael.viafabricplus.information.impl.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InformationSystem {
    private final List<AbstractInformationGroup> groups = new ArrayList<>();

    public void init() {
        addGroup(
                new GeneralInformation(),

                new BedrockInformation(),

                new V1_7_10Information(),
                new V1_5_2Information(),
                new V1_2_4_5Information(),
                new V1_1Information(),

                new C0_30CPEInformation()
        );
    }

    public void addGroup(final AbstractInformationGroup... groups) {
        Collections.addAll(this.groups, groups);
    }

    public List<AbstractInformationGroup> getGroups() {
        return groups;
    }
}
