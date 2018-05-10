package fr.unice.polytech.si3.qgl.ise.model;

import java.util.Map;
import java.util.Objects;

public class CraftedResource {

    private String name;
    private Map<String, String> recipe;

    public CraftedResource(String name, Map<String, String> recipe) {
        this.name = name;
        this.recipe = recipe;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getRecipe() {
        return recipe;
    }

    public boolean sameName(String name) {
        return this.getName().trim().equalsIgnoreCase(name.trim());
    }

    @Override
    public String toString() {
        return getName() + " : " + getRecipe().entrySet();
    }

    @Override
    public boolean equals(Object toCompare) {
        return super.equals(toCompare) && toCompare instanceof CraftedResource;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getRecipe());
    }

}
