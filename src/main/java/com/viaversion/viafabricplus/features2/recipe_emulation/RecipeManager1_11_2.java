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

package com.viaversion.viafabricplus.features2.recipe_emulation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class RecipeManager1_11_2 {

    private final Multimap<RecipeType<?>, RecipeEntry<?>> recipesByType;
    private final Map<RegistryKey<Recipe<?>>, RecipeEntry<?>> recipesById;

    public RecipeManager1_11_2(final Iterable<RecipeEntry<?>> recipes) {
        final ImmutableMultimap.Builder<RecipeType<?>, RecipeEntry<?>> recipesByTypeBuilder = ImmutableMultimap.builder();
        final ImmutableMap.Builder<RegistryKey<Recipe<?>>, RecipeEntry<?>> recipesByIdBuilder = ImmutableMap.builder();

        for (RecipeEntry<?> recipeEntry : recipes) {
            final RecipeType<?> recipeType = recipeEntry.value().getType();
            recipesByTypeBuilder.put(recipeType, recipeEntry);
            recipesByIdBuilder.put(recipeEntry.id(), recipeEntry);
        }

        this.recipesByType = recipesByTypeBuilder.build();
        this.recipesById = recipesByIdBuilder.build();
    }

    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeEntry<T>> getFirstMatch(final RecipeType<T> type, final I input, final World world) {
        if (input.isEmpty()) {
            return Optional.empty();
        } else {
            return this.recipesByType.get(type).stream().map(e -> (RecipeEntry<T>) e).filter(recipe -> recipe.value().matches(input, world)).findFirst();
        }
    }

    public Optional<RecipeEntry<?>> get(final RegistryKey<Recipe<?>> id) {
        return Optional.ofNullable(this.recipesById.get(id));
    }

    public Collection<RecipeEntry<?>> values() {
        return this.recipesById.values();
    }

    public Stream<RegistryKey<Recipe<?>>> keys() {
        return this.recipesById.keySet().stream();
    }

}
