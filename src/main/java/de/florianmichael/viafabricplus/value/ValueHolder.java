/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.viafabricplus.value;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.platform.ProtocolRange;
import de.florianmichael.viafabricplus.value.impl.BooleanValue;
import de.florianmichael.viafabricplus.value.impl.ProtocolSyncBooleanValue;
import net.raphimc.vialegacy.api.LegacyProtocolVersion;

import java.util.ArrayList;
import java.util.List;

public class ValueHolder {
    public final static List<AbstractValue<?>> values = new ArrayList<>();

    // General settings
    public static final BooleanValue removeNotAvailableItemsFromCreativeTab = new BooleanValue("Remove not available items from creative tab", true);

    // 1.19 -> 1.18.2
    public static final ProtocolSyncBooleanValue disableSequencing = new ProtocolSyncBooleanValue("Disable sequencing", ProtocolRange.andOlder(ProtocolVersion.v1_18_2));

    // 1.14 -> 1.13.2
    public static final ProtocolSyncBooleanValue smoothOutMerchantScreens = new ProtocolSyncBooleanValue("Smooth out merchant screens", ProtocolRange.andOlder(ProtocolVersion.v1_13_2));

    // 1.13 -> 1.12.2
    public static final ProtocolSyncBooleanValue executeInputsInSync = new ProtocolSyncBooleanValue("Execute inputs in sync", ProtocolRange.andOlder(ProtocolVersion.v1_12_2));
    public static final ProtocolSyncBooleanValue sneakInstant = new ProtocolSyncBooleanValue("Sneak instant", new ProtocolRange(ProtocolVersion.v1_12_2, ProtocolVersion.v1_8));

    // 1.9 -> 1.8.x
    public static final ProtocolSyncBooleanValue removeCooldowns = new ProtocolSyncBooleanValue("Remove cooldowns", ProtocolRange.andOlder(ProtocolVersion.v1_8));
    public static final ProtocolSyncBooleanValue sendIdlePacket = new ProtocolSyncBooleanValue("Send idle packet", new ProtocolRange(ProtocolVersion.v1_8, LegacyProtocolVersion.r1_3_1tor1_3_2));

    // 1.8.x -> 1.7.6
    public static final ProtocolSyncBooleanValue replaceSneaking = new ProtocolSyncBooleanValue("Replace sneaking", ProtocolRange.andOlder(ProtocolVersion.v1_7_6));
    public static final ProtocolSyncBooleanValue longSneaking = new ProtocolSyncBooleanValue("Long sneaking", ProtocolRange.andOlder(ProtocolVersion.v1_7_6));

    public static void setup() {
    }
}
