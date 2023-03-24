package de.florianmichael.viafabricplus.definition.c0_30.classicube.response.server;

import de.florianmichael.viafabricplus.definition.c0_30.classicube.data.ClassiCubeServerInfo;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.response.ClassiCubeResponse;

import java.util.Set;

public class ClassiCubeServerInfoResponse extends ClassiCubeResponse {
    private final Set<ClassiCubeServerInfo> servers;

    public ClassiCubeServerInfoResponse(Set<ClassiCubeServerInfo> servers) {
        this.servers = servers;
    }

    public Set<ClassiCubeServerInfo> getServers() {
        return servers;
    }

    public static ClassiCubeServerInfoResponse fromJson(final String json) {
        return GSON.fromJson(json, ClassiCubeServerInfoResponse.class);
    }
}
