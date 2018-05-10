package fr.unice.polytech.si3.qgl.ise.service;

import fr.unice.polytech.si3.qgl.ise.model.Biome;
import fr.unice.polytech.si3.qgl.ise.model.CraftedResource;
import fr.unice.polytech.si3.qgl.ise.model.ExtResParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import static fr.unice.polytech.si3.qgl.ise.model.ExtResParser.ExtResBundle;

public class JsonAccessor {

    public static ExtResBundle loadBundle(File file) {

        ExtResBundle bundle = new ExtResBundle();

        try (Scanner sc = new Scanner(file).useDelimiter("you'd never type that, would you \\?")) {

            ExtResParser extResParser = new ExtResParser();

            if (sc.hasNext()) bundle = extResParser.parse(sc.next());

        } catch (Exception ignored) {
            bundle = new ExtResBundle();
        }

        return bundle;
    }

    public static void writeBunlde(File file, ExtResBundle bundle) {

        JSONObject root = new JSONObject();

        JSONArray rawResources = new JSONArray();
        JSONObject craftedResources = new JSONObject();
        JSONObject biomes = new JSONObject();

        for (String string : bundle.getRawResources()) {
            rawResources.put(string);
        }

        for (CraftedResource crafted : bundle.getCraftedResources()) {
            JSONObject recipe = new JSONObject();

            for (Map.Entry<String, String> entry : crafted.getRecipe().entrySet()) {
                recipe.put(entry.getKey(), Double.valueOf(entry.getValue()));
            }

            craftedResources.put(crafted.getName(), recipe);
        }

        for (Biome biome : bundle.getBiomes()) {
            JSONArray resources = new JSONArray();

            for (String rawRes : biome.getResources()) {
                resources.put(rawRes);
            }

            biomes.put(biome.getName(), resources);
        }

        root.put("raw-resources", rawResources);
        root.put("crafted-resources", craftedResources);
        root.put("biomes", biomes);

        try (BufferedWriter output = new BufferedWriter(new FileWriter(file))) {
            output.write(root.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
