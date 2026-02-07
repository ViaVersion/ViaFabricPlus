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

package com.viaversion.viafabricplus.screen.impl;

import com.viaversion.viafabricplus.ViaFabricPlusImpl;
import com.viaversion.viafabricplus.screen.VFPScreen;
import com.viaversion.viaversion.util.DumpUtil;
import java.io.File;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public final class ReportIssuesScreen extends VFPScreen {

    public static final ReportIssuesScreen INSTANCE = new ReportIssuesScreen();

    private final Map<String, Runnable> actions = new LinkedHashMap<>();

    private long delay = -1;

    public ReportIssuesScreen() {
        super(Component.translatable("screen.viafabricplus.report_issues"), true);

        if (!actions.isEmpty()) {
            return;
        }
        actions.put("report.viafabricplus.bug_report", () -> {
            Util.getPlatform().openUri(URI.create("https://github.com/ViaVersion/ViaFabricPlus/issues/new?assignees=&labels=bug&projects=&template=bug_report.yml"));
            this.setupSubtitle(Component.translatable("report.viafabricplus.bug_report.response"));
        });
        actions.put("report.viafabricplus.feature_request", () -> {
            Util.getPlatform().openUri(URI.create("https://github.com/ViaVersion/ViaFabricPlus/issues/new?assignees=&labels=enhancement&projects=&template=feature_request.yml"));
            this.setupSubtitle(Component.translatable("report.viafabricplus.feature_request.response"));
        });
        actions.put("report.viafabricplus.create_via_dump", () -> DumpUtil.postDump(minecraft.getUser().getProfileId()).whenComplete((s, throwable) -> {
            if (throwable != null) {
                this.setupSubtitle(Component.translatable("report.viafabricplus.create_via_dump.failed"));
                ViaFabricPlusImpl.INSTANCE.getLogger().error("Failed to create a dump", throwable);
                return;
            }
            this.setupSubtitle(Component.translatable("report.viafabricplus.create_via_dump.success"));
            minecraft.keyboardHandler.setClipboard(s);
        }));
        actions.put("report.viafabricplus.open_logs", () -> {
            Util.getPlatform().openFile(new File(minecraft.gameDirectory, "logs") /* there is no constant for this in the game */);
            this.setupSubtitle(Component.translatable("report.viafabricplus.open_logs.response"));
        });
    }

    @Override
    protected void init() {
        super.init();
        this.setupDefaultSubtitle();

        int i = 0;
        for (Map.Entry<String, Runnable> entry : actions.entrySet()) {
            this.addRenderableWidget(Button.builder(Component.translatable(entry.getKey()), button -> entry.getValue().run()).
                    pos(this.width / 2 - 100, this.height / 2 - 25 + i * (20 + 3)).size(200, 20).build());
            i++;
        }
    }

    @Override
    public void setupSubtitle(@Nullable Component subtitle) {
        super.setupSubtitle(subtitle);

        this.delay = System.currentTimeMillis();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.delay != -1 && System.currentTimeMillis() - this.delay > 5000 /* 5 seconds */) {
            this.setupDefaultSubtitle();
            this.delay = -1; // Don't recall this method all the time
        }
    }

}
