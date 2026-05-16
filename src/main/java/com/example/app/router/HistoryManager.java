package com.example.app.router;

import javafx.scene.control.Tab;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HistoryManager {

    private final List<RouteHistory> allHistory = new ArrayList<>();
    private int currentIndex = -1;

    public void push(RouteHistory entry) {
        if (currentIndex < allHistory.size() - 1) {
            allHistory.subList(currentIndex + 1, allHistory.size()).clear();
        }
        allHistory.add(entry);
        currentIndex = allHistory.size() - 1;
        log.debug("History push: {} (index: {})", entry.getPath(), currentIndex);
    }

    public RouteHistory back() {
        if (currentIndex <= 0) {
            log.debug("Cannot go back: already at beginning");
            return null;
        }
        currentIndex--;
        RouteHistory entry = allHistory.get(currentIndex);
        log.debug("History back: {} (index: {})", entry.getPath(), currentIndex);
        return entry;
    }

    public RouteHistory forward() {
        if (currentIndex >= allHistory.size() - 1) {
            log.debug("Cannot go forward: already at end");
            return null;
        }
        currentIndex++;
        RouteHistory entry = allHistory.get(currentIndex);
        log.debug("History forward: {} (index: {})", entry.getPath(), currentIndex);
        return entry;
    }

    public boolean canGoBack() {
        return currentIndex > 0;
    }

    public boolean canGoForward() {
        return currentIndex < allHistory.size() - 1;
    }

    public void onTabClosed(Tab tab) {
        allHistory.removeIf(h -> h.getTab() == tab);
        if (currentIndex >= allHistory.size()) {
            currentIndex = allHistory.size() - 1;
        }
        log.debug("Tab closed, history size: {}, current index: {}", allHistory.size(), currentIndex);
    }

    public RouteHistory getCurrent() {
        if (currentIndex >= 0 && currentIndex < allHistory.size()) {
            return allHistory.get(currentIndex);
        }
        return null;
    }

    public List<RouteHistory> getAllHistory() {
        return new ArrayList<>(allHistory);
    }

    public void clear() {
        allHistory.clear();
        currentIndex = -1;
        log.debug("History cleared");
    }

    public int size() {
        return allHistory.size();
    }
}
