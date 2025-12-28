/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2026 the original authors
 *                         - FlorianMichael/EnZaXD <git@florianmichael.de>
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

package com.viaversion.viafabricplus.features.recipe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class RecipeManager1_11_2 {

    private final Multimap<RecipeType<?>, RecipeHolder<?>> recipesByType;
    private final Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> recipesById;

    public RecipeManager1_11_2(final Iterable<RecipeHolder<?>> recipes) {
        final ImmutableMultimap.Builder<RecipeType<?>, RecipeHolder<?>> recipesByTypeBuilder = ImmutableMultimap.builder();
        final ImmutableMap.Builder<ResourceKey<Recipe<?>>, RecipeHolder<?>> recipesByIdBuilder = ImmutableMap.builder();

        for (RecipeHolder<?> recipeEntry : recipes) {
            final RecipeType<?> recipeType = recipeEntry.value().getType();
            recipesByTypeBuilder.put(recipeType, recipeEntry);
            recipesByIdBuilder.put(recipeEntry.id(), recipeEntry);
        }

        this.recipesByType = recipesByTypeBuilder.build();
        this.recipesById = recipesByIdBuilder.build();
    }

    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getFirstMatch(final RecipeType<T> type, final I input, final Level world) {
        if (input.isEmpty()) {
            return Optional.empty();
        } else {
            return this.recipesByType.get(type).stream().map(e -> (RecipeHolder<T>) e).filter(recipe -> recipe.value().matches(input, world)).findFirst();
        }
    }

    public Optional<RecipeHolder<?>> get(final ResourceKey<Recipe<?>> id) {
        return Optional.ofNullable(this.recipesById.get(id));
    }

    public Collection<RecipeHolder<?>> values() {
        return this.recipesById.values();
    }

    public Stream<ResourceKey<Recipe<?>>> keys() {
        return this.recipesById.keySet().stream();
    }

}
