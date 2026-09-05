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

package com.viaversion.viafabricplus.screen.base;

import com.google.common.collect.ImmutableList;
import com.viaversion.viafabricplus.screen.base.list.VFPList;
import com.viaversion.viafabricplus.screen.base.list.VFPListEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.TabButton;
import net.minecraft.client.gui.components.tabs.MenuTabBar;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public abstract class VFPTabbedScreen<T> extends VFPScreen {

    private static final int TAB_BAR_HEIGHT = 24;

    private final Map<T, Double> scrollAmounts = new HashMap<>();
    private final TabManager tabManager = new TabManager(this::addRenderableWidget, this::removeWidget, this::onTabSelected, _ -> {
    });

    private VFPTabBar tabBar;
    private EditBox searchBox;
    private ListTab searchTab;
    private List<T> tabKeys;
    private ImmutableList<Tab> tabWidgets;
    private Tab currentTab;
    private @Nullable T lastTab;

    protected VFPTabbedScreen(final Component title, final boolean backButton) {
        super(title, backButton);
    }

    protected abstract List<T> tabs();

    protected abstract Component tabTitle(final T tab);

    protected abstract int entryHeight();

    protected abstract int rowWidth(final int screenWidth);

    protected abstract List<VFPListEntry> entries(final T tab);

    protected abstract List<VFPListEntry> results(final String query);

    protected T initialTab() {
        final List<T> tabs = this.tabs();
        return this.lastTab != null && tabs.contains(this.lastTab) ? this.lastTab : tabs.getFirst();
    }

    protected int listBottomMargin() {
        return FOOTER_HEIGHT;
    }

    @Override
    protected void init() {
        this.tabKeys = this.tabs();
        final ImmutableList.Builder<Tab> listTabs = ImmutableList.builder();
        final ImmutableList.Builder<TabButton> tabButtons = ImmutableList.builder();
        for (final T tab : this.tabKeys) {
            final ListTab listTab = new ListTab(tab);
            listTabs.add(listTab);
            tabButtons.add(new MenuTabBar.MenuTabButton(this.tabManager, listTab, 0, TAB_BAR_HEIGHT));
        }
        this.tabWidgets = listTabs.build();
        this.tabBar = this.addRenderableWidget(new VFPTabBar(CONTENT_TOP, this.width, TAB_BAR_HEIGHT, this.tabManager, tabButtons.build(), this.tabWidgets));

        // The search results are held by a tab which isn't part of the tab bar, so selecting them
        // deselects all other tabs without any further handling
        this.searchTab = new ListTab(null);

        this.searchBox = this.addSearchBar(this::search);

        final int initialTab = Math.max(0, this.tabKeys.indexOf(this.initialTab()));
        this.currentTab = this.tabWidgets.get(initialTab);
        this.tabBar.selectTab(initialTab, false);

        this.tabBar.arrangeElements(this.width);
        final int listTop = this.tabBar.getRectangle().bottom();
        this.tabManager.setTabArea(new ScreenRectangle(0, listTop, this.width, this.height - this.listBottomMargin() - listTop));

        super.init();
    }

    @Override
    public boolean keyPressed(final @NonNull KeyEvent event) {
        return this.tabBar.keyPressed(event) || super.keyPressed(event);
    }

    private void search(final String query) {
        if (query.isEmpty()) {
            this.tabManager.setCurrentTab(this.currentTab, false);
            return;
        }

        this.searchTab.list.showResults(query);
        this.tabManager.setCurrentTab(this.searchTab, false);
    }

    private void onTabSelected(final Tab tab) {
        if (tab == this.searchTab) {
            return;
        }

        this.currentTab = tab;

        final int index = this.tabWidgets.indexOf(tab);
        if (index != -1) {
            this.lastTab = this.tabKeys.get(index); // Reopened when the widgets are rebuilt
        }

        this.searchBox.setValue(""); // Selecting a tab leaves the search results
    }

    private final class ListTab implements Tab {

        private final @Nullable T tab;
        private final FrameLayout layout = new FrameLayout();
        private final TabList list;

        private ListTab(final @Nullable T tab) {
            this.tab = tab;
            this.list = new TabList(minecraft, width, height, listBottomMargin(), entryHeight(), tab);
        }

        @Override
        public @NonNull Component getTabTitle() {
            return this.tab == null ? SEARCH_TITLE : tabTitle(this.tab);
        }

        @Override
        public @NonNull Component getTabExtraNarration() {
            return Component.empty();
        }

        @Override
        public void visitChildren(final Consumer<AbstractWidget> childrenConsumer) {
            childrenConsumer.accept(this.list);
        }

        @Override
        public void doLayout(final ScreenRectangle screenRectangle) {
            this.list.updateSizeAndPosition(screenRectangle.width(), screenRectangle.height(), screenRectangle.left(), screenRectangle.top());
        }

        @Override
        public @NonNull Layout getLayout() {
            return this.layout;
        }

    }

    private final class TabList extends VFPList {

        private final @Nullable T tab;

        private TabList(final Minecraft minecraftClient, final int width, final int height, final int bottom, final int entryHeight, final @Nullable T tab) {
            super(minecraftClient, width, height, CONTENT_TOP + TAB_BAR_HEIGHT, bottom, entryHeight);
            this.tab = tab;

            if (tab == null) { // Search results are filled in by #showResults
                return;
            }

            entries(tab).forEach(this::addEntry);
            // Needs calling last to have the entries added before setting the scroll amount
            setScrollAmount(scrollAmounts.getOrDefault(tab, 0D));
        }

        private void showResults(final String query) {
            this.replaceEntries(results(query));
            this.setScrollAmount(0D); // Also repositions the new entries
        }

        @Override
        public int getRowWidth() {
            return rowWidth(this.width);
        }

        @Override
        protected void updateSlotAmount(final double amount) {
            if (this.tab != null) { // Search results always start at the top
                scrollAmounts.put(this.tab, amount);
            }
        }

    }

}
