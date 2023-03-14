/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/MrLookAtMe (EnZaXD) and contributors
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
package de.florianmichael.viafabricplus.event;

import de.florianmichael.dietrichevents.AbstractEvent;
import de.florianmichael.dietrichevents.handle.EventExecutor;
import de.florianmichael.dietrichevents.handle.Listener;
import de.florianmichael.vialoadingbase.platform.ComparableProtocolVersion;

public interface ChangeProtocolVersionListener extends Listener {

    void onChangeProtocolVersion(final ComparableProtocolVersion protocolVersion);

    class ChangeProtocolVersionEvent extends AbstractEvent<ChangeProtocolVersionListener> {
        private final EventExecutor<ChangeProtocolVersionListener> eventExecutor;

        public ChangeProtocolVersionEvent(final ComparableProtocolVersion protocolVersion) {
            this.eventExecutor = listener -> listener.onChangeProtocolVersion(protocolVersion);
        }

        @Override
        public EventExecutor<ChangeProtocolVersionListener> getEventExecutor() {
            return this.eventExecutor;
        }

        @Override
        public Class<ChangeProtocolVersionListener> getListenerType() {
            return ChangeProtocolVersionListener.class;
        }
    }
}
