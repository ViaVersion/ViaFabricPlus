/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
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
package de.florianmichael.viafabricplus.injection.mixin.fixes.minecraft.font;

import de.florianmichael.viafabricplus.definition.FontCacheFix;
import de.florianmichael.viafabricplus.injection.access.IFontStorage;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.RenderableGlyph;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FontStorage.class)
public abstract class MixinFontStorage implements IFontStorage {

    @Shadow protected abstract GlyphRenderer getGlyphRenderer(RenderableGlyph c);

    @Shadow @Final private List<Font> fonts;

    @Shadow public abstract void setFonts(List<Font> fonts);

    @Unique
    private GlyphRenderer unknownGlyphRenderer;

    @Unique
    private List<Font> viafabricplus_fontCache;

    @Inject(method = "setFonts", at = @At("HEAD"))
    public void cacheFonts(List<Font> fonts, CallbackInfo ci) {
        if (viafabricplus_fontCache == fonts) return;

        viafabricplus_fontCache = fonts;
    }

    @Inject(method = "setFonts", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/BuiltinEmptyGlyph;bake(Ljava/util/function/Function;)Lnet/minecraft/client/font/GlyphRenderer;", ordinal = 0, shift = At.Shift.AFTER))
    public void injectSetFonts(List<Font> fonts, CallbackInfo ci) {
        this.unknownGlyphRenderer = FontCacheFix.BuiltinEmptyGlyph1_12_2.VERY_MISSING.bake(this::getGlyphRenderer);
    }

    @Inject(method = "getRectangleRenderer", at = @At("HEAD"), cancellable = true)
    public void setCustomRenderer(CallbackInfoReturnable<GlyphRenderer> cir) {
        if (FontCacheFix.shouldReplaceFontRenderer()) {
            cir.setReturnValue(this.unknownGlyphRenderer);
        }
    }

    @Override
    public void viafabricplus_clearCaches() {
        if (fonts != null) {
            fonts.clear();
            if (viafabricplus_fontCache != null) {
                setFonts(viafabricplus_fontCache);
            }
        }
    }
}
