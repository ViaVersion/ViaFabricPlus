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

import com.viaversion.viafabricplus.screen.impl.protocol.AbstractProtocolSelectionScreen;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class PerServerVersionScreen extends AbstractProtocolSelectionScreen {

    private final Consumer<ProtocolVersion> selectionConsumer;
    private final Supplier<ProtocolVersion> selectionSupplier;

    public PerServerVersionScreen(final Screen prevScreen, final Consumer<ProtocolVersion> selectionConsumer, final Supplier<ProtocolVersion> selectionSupplier) {
        super(Component.translatable("screen.viafabricplus.force_version"), true);

        this.prevScreen = prevScreen;
        this.selectionConsumer = selectionConsumer;
        this.selectionSupplier = selectionSupplier;
    }

    @Override
    protected void init() {
        super.init();

        this.addFooter(Button.builder(Component.translatable("base.viafabricplus.reset"), _ -> this.selectionConsumer.accept(null)).build());
    }

    @Override
    protected void select(final ProtocolVersion version) {
        this.selectionConsumer.accept(version);
    }

    @Override
    protected boolean selected(final ProtocolVersion version) {
        return version.equals(this.selectionSupplier.get());
    }

    @Override
    protected boolean selectable() {
        return true;
    }

}
