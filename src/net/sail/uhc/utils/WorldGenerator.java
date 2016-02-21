package net.sail.uhc.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Created by brand on 12/30/2015.
 */
public class WorldGenerator {

    private static ArrayList<Long> seeds = new ArrayList<Long>();

    public World generateWorld(String name) {
        Random randomInstance = new Random();

        if (Bukkit.getWorld(name) != null) {
            return Bukkit.getWorld(name);
        }

        WorldCreator wc = new WorldCreator(name);

        wc.environment(World.Environment.NORMAL);
        wc.generateStructures(true);
        wc.type(WorldType.NORMAL);
        if (seeds.size() > 0) {
            wc.seed(seeds.get(randomInstance.nextInt(seeds.size())));
        }


        return Bukkit.createWorld(wc);
    }

    public static void addSeed(Long seed) {
        seeds.add(seed);
    }

}
