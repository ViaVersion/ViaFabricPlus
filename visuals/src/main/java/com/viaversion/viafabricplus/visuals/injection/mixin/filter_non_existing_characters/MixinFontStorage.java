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

package com.viaversion.viafabricplus.visuals.injection.mixin.filter_non_existing_characters;

import com.viaversion.viafabricplus.ViaFabricPlus;
import com.viaversion.viafabricplus.visuals.features.filter_non_existing_characters.BuiltinEmptyGlyph1_12_2;
import com.viaversion.viafabricplus.visuals.features.filter_non_existing_characters.RenderableGlyphDiff;
import com.viaversion.viafabricplus.visuals.settings.VisualSettings;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.BakedGlyph;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.RenderableGlyph;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontStorage.class)
public abstract class MixinFontStorage {

    @Shadow
    @Final
    private Identifier id;

    @Shadow
    private BakedGlyph blankBakedGlyph;

    @Shadow
    protected abstract BakedGlyph bake(RenderableGlyph c);

    @Unique
    private BakedGlyph viaFabricPlusVisuals$blankBakedGlyph1_12_2;

    @Inject(method = "clear", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/BuiltinEmptyGlyph;bake(Ljava/util/function/Function;)Lnet/minecraft/client/font/BakedGlyph;", ordinal = 0))
    private void bakeBlankGlyph1_12_2(CallbackInfo ci) {
        this.viaFabricPlusVisuals$blankBakedGlyph1_12_2 = BuiltinEmptyGlyph1_12_2.INSTANCE.bake(this::bake);
    }

    @Inject(method = "findGlyph", at = @At("RETURN"), cancellable = true)
    private void filterGlyphs(int codePoint, CallbackInfoReturnable<FontStorage.GlyphPair> cir) {
        if (this.viaFabricPlusVisuals$shouldBeInvisible(codePoint)) {
            cir.setReturnValue(this.viaFabricPlusVisuals$getBlankGlyphPair());
        }
    }

    @Inject(method = "bake(I)Lnet/minecraft/client/font/BakedGlyph;", at = @At("RETURN"), cancellable = true)
    private void filterBakedGlyph(int codePoint, CallbackInfoReturnable<BakedGlyph> cir) {
        if (this.viaFabricPlusVisuals$shouldBeInvisible(codePoint)) {
            cir.setReturnValue(this.viaFabricPlusVisuals$getBlankBakedGlyph());
        }
    }

    @Unique
    private boolean viaFabricPlusVisuals$shouldBeInvisible(final int codePoint) {
        if (VisualSettings.INSTANCE.filterNonExistingGlyphs.getValue()) {
            return (this.id.equals(MinecraftClient.DEFAULT_FONT_ID) || this.id.equals(MinecraftClient.UNICODE_FONT_ID)) && !RenderableGlyphDiff.isGlyphRenderable(codePoint);
        } else {
            return false;
        }
    }

    @Unique
    private FontStorage.GlyphPair viaFabricPlusVisuals$getBlankGlyphPair() {
        if (ViaFabricPlus.getImpl().getTargetVersion().olderThanOrEqualTo(ProtocolVersion.v1_21_2)) {
            return new FontStorage.GlyphPair(BuiltinEmptyGlyph1_12_2.INSTANCE, BuiltinEmptyGlyph1_12_2.INSTANCE);
        } else {
            return FontStorage.GlyphPair.MISSING;
        }
    }

    @Unique
    private BakedGlyph viaFabricPlusVisuals$getBlankBakedGlyph() {
        if (VisualSettings.INSTANCE.filterNonExistingGlyphs.getValue()) {
            return this.viaFabricPlusVisuals$blankBakedGlyph1_12_2;
        } else {
            return this.blankBakedGlyph;
        }
    }

}
