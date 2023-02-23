package de.florianmichael.viafabricplus.injection.access;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.util.Pair;

import java.util.Deque;

public interface IPacketWrapperImpl {

    Deque<Pair<Type<?>, Object>> viafabricplus_readableObjects();
}
