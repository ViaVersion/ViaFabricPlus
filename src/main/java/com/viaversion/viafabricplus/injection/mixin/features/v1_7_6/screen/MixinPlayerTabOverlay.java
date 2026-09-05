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

package com.viaversion.viafabricplus.injection.mixin.features.v1_7_6.screen;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.injection.access.v1_7_6.IPlayerInfo;
import com.viaversion.viafabricplus.injection.access.v1_7_6.IPlayerTabOverlay;
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
public abstract class MixinPlayerTabOverlay implements IPlayerTabOverlay {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private static final Comparator<PlayerInfo> viaFabricPlus$FIFO_COMPARATOR = Comparator.comparingInt(e -> ((IPlayerInfo) e).viaFabricPlus$getIndex());

    @Unique
    private int viaFabricPlus$maxSlots;

    @Unique
    private boolean viaFabricPlus$hideSkins = true;

    @Inject(method = "getPlayerInfos", at = @At("HEAD"), cancellable = true)
    private void collectPlayerEntries(CallbackInfoReturnable<List<PlayerInfo>> result) {
        if (ViaFabricPlusImpl.impl().visuals().enableLegacyTablist().isActive()) {
            result.setReturnValue(this.minecraft.player.connection.getListedOnlinePlayers().stream()
                .sorted(viaFabricPlus$FIFO_COMPARATOR)
                .limit(viaFabricPlus$maxSlots)
                .collect(Collectors.collectingAndThen(Collectors.toList(), this::viaFabricPlus$transpose)));
        } else {
            viaFabricPlus$hideSkins = false;
        }
    }

    @ModifyExpressionValue(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;onlineMode()Z"))
    private boolean hideSkins(boolean original) {
        return original && !viaFabricPlus$hideSkins;
    }

    @Unique
    private List<PlayerInfo> viaFabricPlus$transpose(final List<PlayerInfo> list) {
        // Only bother transposing if we know the list is full
        if (list.size() != viaFabricPlus$maxSlots) {
            viaFabricPlus$hideSkins = list.stream().noneMatch(e -> e.getProfile().properties().containsKey("textures"));
            return list;
        }

        final List<PlayerInfo> result = new ArrayList<>(list.size());

        final int columns = viaFabricPlus$maxSlots / PlayerTabOverlay.MAX_ROWS_PER_COL;
        boolean anyHasSkinData = false;
        for (int i = 0; i < viaFabricPlus$maxSlots; i++) {
            final int row = i % PlayerTabOverlay.MAX_ROWS_PER_COL;
            final int col = i / PlayerTabOverlay.MAX_ROWS_PER_COL;
            final PlayerInfo current = list.get(row * columns + col);
            result.add(current);
            anyHasSkinData = anyHasSkinData || current.getProfile().properties().containsKey("textures");
        }
        viaFabricPlus$hideSkins = !anyHasSkinData;
        return result;
    }

    @Override
    public void viaFabricPlus$setMaxPlayers(int maxPlayers) {
        this.viaFabricPlus$maxSlots = Math.clamp(((maxPlayers + PlayerTabOverlay.MAX_ROWS_PER_COL - 1) / PlayerTabOverlay.MAX_ROWS_PER_COL) * PlayerTabOverlay.MAX_ROWS_PER_COL, 20, 200);
    }

}
