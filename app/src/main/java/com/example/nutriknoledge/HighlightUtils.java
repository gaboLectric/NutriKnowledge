package com.example.nutriknoledge;

import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HighlightUtils {
    private static final String HIGHLIGHTS_KEY = "highlights";

    public static class HighlightItem {
        public String text;
        public String color;
        public String topicTitle;
        public String topicContent;
        public String topicUrl;
        public String type; // "highlight" or "underline"
        public HighlightItem(String text, String color, String topicTitle, String topicContent, String topicUrl, String type) {
            this.text = text;
            this.color = color;
            this.topicTitle = topicTitle;
            this.topicContent = topicContent;
            this.topicUrl = topicUrl;
            this.type = type;
        }
        public HighlightItem(String text, String color, String topicTitle, String topicContent, String topicUrl) {
            this(text, color, topicTitle, topicContent, topicUrl, "highlight"); // default to highlight for old code
        }
        public String toStorageString() {
            return text + ":::" + color + ":::" + topicTitle + ":::" + topicContent + ":::" + topicUrl + ":::" + type;
        }
        public static HighlightItem fromStorageString(String s) {
            try {
                String[] parts = s.split(":::");
                String text = parts.length > 0 ? parts[0] : "";
                String color = parts.length > 1 ? parts[1] : "#FFF9C4";
                String topicTitle = parts.length > 2 ? parts[2] : "";
                String topicContent = parts.length > 3 ? parts[3] : "";
                String topicUrl = parts.length > 4 ? parts[4] : "";
                String type = parts.length > 5 ? parts[5] : "highlight";
                return new HighlightItem(text, color, topicTitle, topicContent, topicUrl, type);
            } catch (Exception e) {
                // fallback for any corrupted or unexpected data
                return new HighlightItem("", "#FFF9C4", "", "", "", "highlight");
            }
        }
    }

    public static void addHighlight(SharedPreferences prefs, String text, String color, String topicTitle, String topicContent, String topicUrl, String type) {
        List<String> highlights = getRawHighlights(prefs);
        String storage = text + ":::" + color + ":::" + topicTitle + ":::" + topicContent + ":::" + topicUrl + ":::" + type;
        if (!highlights.contains(storage)) {
            highlights.add(storage);
            prefs.edit().putString(HIGHLIGHTS_KEY, String.join("|||", highlights)).apply();
        }
    }
    public static List<HighlightItem> getHighlights(SharedPreferences prefs) {
        List<String> raw = getRawHighlights(prefs);
        List<HighlightItem> items = new ArrayList<>();
        for (String s : raw) items.add(HighlightItem.fromStorageString(s));
        return items;
    }
    private static List<String> getRawHighlights(SharedPreferences prefs) {
        String joined = prefs.getString(HIGHLIGHTS_KEY, "");
        if (joined.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(joined.split("\\|\\|\\|")));
    }
    public static void saveHighlights(SharedPreferences prefs, List<HighlightItem> highlights) {
        List<String> storage = new ArrayList<>();
        for (HighlightItem h : highlights) storage.add(h.toStorageString());
        prefs.edit().putString(HIGHLIGHTS_KEY, String.join("|||", storage)).apply();
    }
    public static List<HighlightItem> getHighlightsForContent(SharedPreferences prefs, String content) {
        List<HighlightItem> all = getHighlights(prefs);
        List<HighlightItem> found = new ArrayList<>();
        for (HighlightItem h : all) {
            if (content.contains(h.text)) found.add(h);
        }
        return found;
    }
}
