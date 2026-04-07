package com.manilalinkup.app.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DistrictAutocompleteManager {
    private static DistrictAutocompleteManager instance;
    private final List<String> allOptions;

    private DistrictAutocompleteManager() {
        allOptions = Arrays.asList("Binondo", "Ermita", "Intramuros", "Malate", "Paco", "Pandacan", "Port Area", "Quiapo", "Sampaloc", "San Andres", "San miguel", "San Nicolas", "Santa Ana", "Santa Cruz", "Santa Mesa", "Tondo");

    }

    public static synchronized DistrictAutocompleteManager getInstance() {
        if (instance == null) {
            instance = new DistrictAutocompleteManager();
        }
        return instance;
    }

    public List<String> getFilteredResults(String query) {
        List<String> suggestions = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();

        for (String item : allOptions) {
            if (item.toLowerCase().contains(lowerQuery)) {
                suggestions.add(item);
            }
        }
        return suggestions;
    }
}
