/*
 * This file is part of ViaFabricPlus - https://github.com/ViaVersion/ViaFabricPlus
 * Copyright (C) 2021-2025 the original authors
 *                         - FlorianMichael/EnZaXD <florian.michael07@gmail.com>
 *                         - RK_01/RaphiMC
 * Copyright (C) 2023-2025 ViaVersion and contributors
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

package com.viaversion.viafabricplus.visuals.injection.mixin.r1_7_tab_list_style;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.viaversion.viafabricplus.visuals.injection.access.r1_7_tab_list_tyle.IPlayerListEntry;
import com.viaversion.viafabricplus.visuals.injection.access.r1_7_tab_list_tyle.IPlayerListHud;
import com.viaversion.viafabricplus.visuals.settings.VisualSettings;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerTabOverlay.class)
public abstract class MixinPlayerListHud implements IPlayerListHud {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private static final Comparator<PlayerInfo> viaFabricPlusVisuals$FIFO_COMPARATOR = Comparator.comparingInt(e -> ((IPlayerListEntry) e).viaFabricPlusVisuals$getIndex());

    @Unique
    private int viaFabricPlusVisuals$maxSlots;

    @Unique
    private boolean viaFabricPlusVisuals$hideSkins = true;

    @Inject(method = "getPlayerInfos", at = @At("HEAD"), cancellable = true)
    private void collectPlayerEntries(CallbackInfoReturnable<List<PlayerInfo>> result) {
        if (VisualSettings.INSTANCE.enableLegacyTablist.isEnabled()) {
            result.setReturnValue(this.minecraft.player.connection.getListedOnlinePlayers().stream()
                    .sorted(viaFabricPlusVisuals$FIFO_COMPARATOR)
                    .limit(viaFabricPlusVisuals$maxSlots)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), this::viaFabricPlusVisuals$transpose)));
        } else {
            viaFabricPlusVisuals$hideSkins = false;
        }
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;isEncrypted()Z"))
    private boolean hideSkins(boolean original) {
        return original && !viaFabricPlusVisuals$hideSkins;
    }

    @Unique
    private List<PlayerInfo> viaFabricPlusVisuals$transpose(final List<PlayerInfo> list) {
        // Only bother transposing if we know the list is full
        if (list.size() != viaFabricPlusVisuals$maxSlots) {
            viaFabricPlusVisuals$hideSkins = list.stream().noneMatch(e -> e.getProfile().properties().containsKey("textures"));
            return list;
        }

        final List<PlayerInfo> result = new ArrayList<>(list.size());

        final int columns = viaFabricPlusVisuals$maxSlots / PlayerTabOverlay.MAX_ROWS_PER_COL;
        boolean anyHasSkinData = false;
        for (int i = 0; i < viaFabricPlusVisuals$maxSlots; i++) {
            final int row = i % PlayerTabOverlay.MAX_ROWS_PER_COL;
            final int col = i / PlayerTabOverlay.MAX_ROWS_PER_COL;
            final PlayerInfo current = list.get(row * columns + col);
            result.add(current);
            anyHasSkinData = anyHasSkinData || current.getProfile().properties().containsKey("textures");
        }
        viaFabricPlusVisuals$hideSkins = !anyHasSkinData;
        return result;
    }

    @Override
    public void viaFabricPlusVisuals$setMaxPlayers(int maxPlayers) {
        this.viaFabricPlusVisuals$maxSlots = Math.min(200, Math.max(20, ((maxPlayers + PlayerTabOverlay.MAX_ROWS_PER_COL - 1) / PlayerTabOverlay.MAX_ROWS_PER_COL) * PlayerTabOverlay.MAX_ROWS_PER_COL));
    }

}
