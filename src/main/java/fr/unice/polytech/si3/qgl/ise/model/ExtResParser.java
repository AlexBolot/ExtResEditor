package fr.unice.polytech.si3.qgl.ise.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@SuppressWarnings("WeakerAccess")
public class ExtResParser {

    private ExtResBundle bundle = new ExtResBundle();

    //region --------------- Parsing Methods ---------------

    public ExtResBundle parse(String jsonContent) {
        try {
            JSONObject data = new JSONObject(jsonContent);

            JSONArray jsonRawRes = data.getJSONArray("raw-resources");
            JSONObject jsonCraftedRes = data.getJSONObject("crafted-resources");
            JSONObject jsonBiomes = data.getJSONObject("biomes");

            parseRawResources(jsonRawRes);
            parseCraftedResources(jsonCraftedRes);
            parseBiomes(jsonBiomes);

        } catch (Exception ignored) {
        }

        return bundle;
    }

    private void parseRawResources(JSONArray jsonRawRes) {
        for (Object rawResName : jsonRawRes) {
            assertFalse(String.valueOf(rawResName).trim().isEmpty(), "Raw Resource's Name is empty");
            assertFalse(bundle.hasRawRes((String) rawResName), "Raw Resource already exists");

            bundle.addRawRes((String) rawResName);
        }
    }

    private void parseCraftedResources(JSONObject jsonCraftedRes) {

        for (String craftedResName : jsonCraftedRes.keySet()) {

            assertFalse(craftedResName.trim().isEmpty(), "Crafted Resource's Name is empty");
            assertFalse(bundle.hasCraftedRes(craftedResName), "CraftedResource already exists");

            HashMap<String, String> recipe = new HashMap<>();

            JSONObject jsonRecipe = jsonCraftedRes.getJSONObject(craftedResName);

            for (String rawResName : jsonRecipe.keySet()) {
                assertFalse(String.valueOf(rawResName).trim().isEmpty(), "Raw Resource is empty");
                assertTrue(bundle.hasRawRes(rawResName), "Raw Resource doesn't exist");

                recipe.put(rawResName, String.valueOf(jsonRecipe.getDouble(rawResName)));
            }

            bundle.addCraftedRes(new CraftedResource(craftedResName, recipe));
        }
    }

    private void parseBiomes(JSONObject jsonBiomes) {

        for (String biomeName : jsonBiomes.keySet()) {

            assertFalse(biomeName.trim().isEmpty(), "Biome' Name is empty");
            assertFalse(bundle.hasBiome(biomeName), "Biome already exists");

            ArrayList<String> possibleRes = new ArrayList<>();

            JSONArray jsonPossibleRes = jsonBiomes.getJSONArray(biomeName);

            for (Object rawResName : jsonPossibleRes) {
                assertFalse(String.valueOf(rawResName).trim().isEmpty(), "Raw Resource is empty");
                assertTrue(bundle.hasRawRes((String) rawResName), "Raw Resource doesn't exist");

                possibleRes.add((String) rawResName);
            }

            bundle.addBiome(new Biome(biomeName, possibleRes));
        }
    }

    //endregion

    //region --------------- Validator Methods ---------------

    @SuppressWarnings("SameParameterValue")
    private void assertTrue(boolean bool, String message) {
        if (!bool) throw new JSONException(message);
    }

    private void assertFalse(boolean bool, String message) {
        if (bool) throw new JSONException(message);
    }

    //endregion

    public static class ExtResBundle {

        private ObservableList<String> rawResources = FXCollections.observableArrayList();
        private ObservableList<CraftedResource> craftedResources = FXCollections.observableArrayList();
        private ObservableList<Biome> biomes = FXCollections.observableArrayList();

        public ObservableList<String> getRawResources() {
            return rawResources;
        }

        public ObservableList<CraftedResource> getCraftedResources() {
            return craftedResources;
        }

        public ObservableList<Biome> getBiomes() {
            return biomes;
        }

        public CraftedResource getCraftedRes(String name) {
            return getCraftedResources().stream()
                    .filter(craftedRes -> craftedRes.sameName(name))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("This crafted resource does not exist : "+ name));
        }

        public Biome getBiome(String name) {
            return getBiomes().stream()
                    .filter(biome -> biome.sameName(name))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("This biome does not exist : "+ name));
        }

        public boolean hasBiome(String biomeName) {
            return biomes.stream().anyMatch(biome -> biome.sameName(biomeName));
        }

        public boolean hasRawRes(String rawResName) {
            return rawResources.stream().anyMatch(rawRes -> rawRes.equalsIgnoreCase(rawResName));
        }

        public boolean hasCraftedRes(String craftedResName) {
            return craftedResources.stream().anyMatch(craftedRes -> craftedRes.sameName(craftedResName));
        }

        public void addRawRes(String... rawRes) {
            rawResources.addAll(Arrays.asList(rawRes));
        }

        public void addCraftedRes(CraftedResource... craftedRes) {
            craftedResources.addAll(Arrays.asList(craftedRes));
        }

        public void addBiome(Biome... biome) {
            biomes.addAll(Arrays.asList(biome));
        }
    }
}
