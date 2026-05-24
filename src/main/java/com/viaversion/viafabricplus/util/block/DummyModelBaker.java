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

package com.viaversion.viafabricplus.util.block;

import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.resources.Identifier;

public class DummyModelBaker implements ModelBaker {
    private final MaterialBaker materials;
    private final ModelBaker.Interner interner;

    public DummyModelBaker(final MaterialBaker materials, final Interner interner) {
        this.materials = materials;
        this.interner = interner;
    }

    @Override
    public ResolvedModel getModel(final Identifier location) {
        return null;
    }

    public BlockStateModelPart missingBlockModelPart() {
        return null;
    }

    @Override
    public <T> T compute(final SharedOperationKey<T> key) {
        return null;
    }

    public MaterialBaker materials() {
        return this.materials;
    }

    public ModelBaker.Interner interner() {
        return this.interner;
    }
}
