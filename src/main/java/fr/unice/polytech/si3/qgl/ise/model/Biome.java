package fr.unice.polytech.si3.qgl.ise.model;

import java.util.List;
import java.util.Objects;

public class Biome {
    private String name;
    private List<String> possibleResources;

    public Biome(String name, List<String> possibleResources) {
        this.name = name;
        this.possibleResources = possibleResources;
    }

    public String getName() {
        return name;
    }

    public List<String> getResources() {
        return possibleResources;
    }

    public boolean sameName(String name) {
        return this.getName().trim().equalsIgnoreCase(name.trim());
    }

    @Override
    public String toString() {
        return getName() + " " + getResources().toString();
    }

    @Override
    public boolean equals(Object toCompare) {
        return toCompare instanceof Biome && this.sameName(((Biome) toCompare).getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, possibleResources);
    }
}
