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

package de.florianmichael.viafabricplus.protocoltranslator.impl.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.signature.SignableCommandArgumentsProvider;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.protocols.v1_12_2to1_13.provider.PlayerLookTargetProvider;
import com.viaversion.viaversion.protocols.v1_15_2to1_16.provider.PlayerAbilitiesProvider;
import com.viaversion.viaversion.protocols.v1_18_2to1_19.provider.AckSequenceProvider;
import com.viaversion.viaversion.protocols.v1_8to1_9.provider.HandItemProvider;
import de.florianmichael.viafabricplus.event.PostViaVersionLoadCallback;
import de.florianmichael.viafabricplus.protocoltranslator.impl.provider.viabedrock.ViaFabricPlusNettyPipelineProvider;
import de.florianmichael.viafabricplus.protocoltranslator.impl.provider.vialegacy.*;
import de.florianmichael.viafabricplus.protocoltranslator.impl.provider.viaversion.*;
import de.florianmichael.viafabricplus.settings.impl.GeneralSettings;
import net.raphimc.viabedrock.protocol.provider.NettyPipelineProvider;
import net.raphimc.vialegacy.protocol.alpha.a1_2_3_5_1_2_6tob1_0_1_1_1.provider.AlphaInventoryProvider;
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.provider.ClassicMPPassProvider;
import net.raphimc.vialegacy.protocol.classic.c0_28_30toa1_0_15.provider.ClassicWorldHeightProvider;
import net.raphimc.vialegacy.protocol.release.r1_2_4_5tor1_3_1_2.provider.OldAuthProvider;
import net.raphimc.vialegacy.protocol.release.r1_6_4tor1_7_2_5.provider.EncryptionProvider;
import net.raphimc.vialegacy.protocol.release.r1_7_6_10tor1_8.provider.GameProfileFetcher;
import com.viaversion.vialoader.impl.viaversion.VLLoader;

public class ViaFabricPlusVLLoader extends VLLoader {

    @Override
    public void load() {
        super.load();

        final ViaProviders providers = Via.getManager().getProviders();

        providers.use(VersionProvider.class, new ViaFabricPlusBaseVersionProvider());

        providers.use(HandItemProvider.class, new ViaFabricPlusHandItemProvider());
        providers.use(PlayerLookTargetProvider.class, new ViaFabricPlusPlayerLookTargetProvider());
        providers.use(PlayerAbilitiesProvider.class, new ViaFabricPlusPlayerAbilitiesProvider());
        providers.use(SignableCommandArgumentsProvider.class, new ViaFabricPlusCommandArgumentsProvider());
        providers.use(AckSequenceProvider.class, new ViaFabricPlusAckSequenceProvider());

        providers.use(OldAuthProvider.class, new ViaFabricPlusOldAuthProvider());
        providers.use(ClassicWorldHeightProvider.class, new ViaFabricPlusClassicWorldHeightProvider());
        providers.use(EncryptionProvider.class, new ViaFabricPlusEncryptionProvider());
        providers.use(GameProfileFetcher.class, new ViaFabricPlusGameProfileFetcher());
        providers.use(ClassicMPPassProvider.class, new ViaFabricPlusClassicMPPassProvider());
        if (GeneralSettings.global().emulateInventoryActionsInAlphaVersions.getValue()) {
            providers.use(AlphaInventoryProvider.class, new ViaFabricPlusAlphaInventoryProvider());
        }

        providers.use(NettyPipelineProvider.class, new ViaFabricPlusNettyPipelineProvider());

        PostViaVersionLoadCallback.EVENT.invoker().onPostViaVersionLoad();
    }

}
