package com.example.nutriknoledge;

public class GlossaryItem {
    private String term;
    private String definition;
    private String category;
    private boolean isFavorite;

    public GlossaryItem(String term, String definition, String category) {
        this.term = term;
        this.definition = definition;
        this.category = category;
        this.isFavorite = false;
    }

    public String getTerm() { return term; }
    public String getDefinition() { return definition; }
    public String getCategory() { return category; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}
