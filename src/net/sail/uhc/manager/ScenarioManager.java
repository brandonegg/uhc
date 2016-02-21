package net.sail.uhc.manager;

import net.sail.uhc.settings.ConfigManager;

/**
 * Created by brand on 1/29/2016.
 */
public class ScenarioManager {

    private final ConfigManager settingsManager;

    private Scenario currentScenario = Scenario.VANILLA;

    public ScenarioManager(ConfigManager settingsManager) {
        this.settingsManager = settingsManager;

        if (settingsManager.get("scenario") == null) {
            settingsManager.set("scenario", "vanilla");
        } else {
            currentScenario = getScenarioFromString(settingsManager.get("scenario"));
        }
    }

    public void saveScenario() {
        settingsManager.set("scenario", currentScenario.getName());
    }

    public enum Scenario {
        VANILLA("vanilla"), CUTCLEAN("cutclean"), BAREBONES("barebones"), DIAMONDLESS("diamondless");

        private String name;

        Scenario(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static Scenario getScenarioFromString(String str) {
        if (str.equalsIgnoreCase("vanilla")) {
            return Scenario.VANILLA;
        } else if (str.equalsIgnoreCase("cutclean")) {
            return Scenario.CUTCLEAN;
        } else if (str.equalsIgnoreCase("barebones")) {
            return Scenario.BAREBONES;
        } else if (str.equalsIgnoreCase("diamondless")) {
            return Scenario.DIAMONDLESS;
        }
        return null;
    }

    public Scenario getCurrentScenario() { return currentScenario; }

    public void setCurrentScenario(Scenario scenario) { currentScenario = scenario; }

    public void runCurrentScenario() {

    }
}
