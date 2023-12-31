/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD
 * Copyright (C) 2024      RK_01/RaphiMC and contributors
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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.screen;

import de.florianmichael.viafabricplus.injection.access.IDownloadingTerrainScreen;
import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DownloadingTerrainScreen.class)
public abstract class MixinDownloadingTerrainScreen extends Screen implements IDownloadingTerrainScreen {

    @Shadow
    @Final
    private long loadStartTime;

    @Unique
    private int viaFabricPlus$tickCounter;

    @Unique
    private boolean viaFabricPlus$ready;

    @Unique
    private boolean viaFabricPlus$closeOnNextTick = false;

    public MixinDownloadingTerrainScreen(Text title) {
        super(title);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void modifyCloseCondition(CallbackInfo ci) {
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_20_2)) {
            ci.cancel();

            if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_18tor1_18_1)) {
                if (this.viaFabricPlus$ready) {
                    this.close();
                }

                if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_12_1)) {
                    this.viaFabricPlus$tickCounter++;
                    if (this.viaFabricPlus$tickCounter % 20 == 0) {
                        this.client.getNetworkHandler().sendPacket(new KeepAliveC2SPacket(0));
                    }
                }
            } else {
                if (System.currentTimeMillis() > this.loadStartTime + 30000L) {
                    this.close();
                } else {
                    if (this.viaFabricPlus$closeOnNextTick) {
                        if (this.client.player == null) return;

                        final BlockPos blockPos = this.client.player.getBlockPos();
                        final boolean isOutOfHeightLimit = this.client.world != null && this.client.world.isOutOfHeightLimit(blockPos.getY());
                        if (isOutOfHeightLimit || this.client.worldRenderer.isRenderingReady(blockPos) || this.client.player.isSpectator() || !this.client.player.isAlive()) {
                            this.close();
                        }
                    } else {
                        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_19_1tor1_19_2)) {
                            this.viaFabricPlus$closeOnNextTick = this.viaFabricPlus$ready || System.currentTimeMillis() > this.loadStartTime + 2000;
                        } else {
                            this.viaFabricPlus$closeOnNextTick = this.viaFabricPlus$ready;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void viaFabricPlus$setReady() {
        this.viaFabricPlus$ready = true;
    }

}
