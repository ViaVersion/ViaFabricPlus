/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and RK_01/RaphiMC
 * Copyright (C) 2023-2024 contributors
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

package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft;

import com.viaversion.viaaprilfools.api.AprilFoolsProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import net.minecraft.client.sound.StaticSound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;

@Mixin(StaticSound.class)
public abstract class MixinStaticSound {

    @Shadow
    @Final
    private AudioFormat format;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void modifyBuffer(ByteBuffer sample, AudioFormat format, CallbackInfo ci) {
        if (ProtocolTranslator.getTargetVersion().equals(AprilFoolsProtocolVersion.s3d_shareware)) {
            this.viaFabricPlus$apply8BitSound(sample);
        }
    }

    @Unique
    private void viaFabricPlus$apply8BitSound(final ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return;
        }
        if (this.format.getChannels() == 1) {
            this.viaFabricPlus$apply8BitMono(byteBuffer);
        } else {
            this.viaFabricPlus$apply8BitStereo(byteBuffer);
        }
    }

    @Unique
    private void viaFabricPlus$apply8BitMono(final ByteBuffer byteBuffer) {
        short short2 = 0;
        int integer3 = 0;
        while (byteBuffer.hasRemaining()) {
            if (integer3 == 0) {
                byteBuffer.mark();
                short2 = (short) (byteBuffer.getShort() & 0xFFFFFFFC);
                byteBuffer.reset();
                integer3 = 15;
            } else {
                --integer3;
            }
            byteBuffer.putShort(short2);
        }
        byteBuffer.flip();
    }

    @Unique
    private void viaFabricPlus$apply8BitStereo(final ByteBuffer byteBuffer) {
        short short2 = 0;
        short short3 = 0;
        int integer4 = 0;
        while (byteBuffer.hasRemaining()) {
            if (integer4 == 0) {
                byteBuffer.mark();
                short2 = (short) (byteBuffer.getShort() & 0xFFFFFFFC);
                short3 = (short) (byteBuffer.getShort() & 0xFFFFFFFC);
                byteBuffer.reset();
                integer4 = 15;
            } else {
                --integer4;
            }
            byteBuffer.putShort(short2);
            byteBuffer.putShort(short3);
        }
        byteBuffer.flip();
    }

}
