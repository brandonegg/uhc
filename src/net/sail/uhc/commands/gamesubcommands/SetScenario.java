package net.sail.uhc.commands.gamesubcommands;

import net.sail.uhc.commands.SubCommand;
import net.sail.uhc.manager.ScenarioManager;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.Messaging;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by brand on 1/30/2016.
 */
public class SetScenario implements SubCommand {

    private final ScenarioManager scenarioManager;

    public SetScenario(ScenarioManager scenarioManager) {
        this.scenarioManager = scenarioManager;
    }

    @Override
    public boolean onCommand(Player p, String[] args) {
        if (args.length == 0) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "Invalid usage, type /game setscenario (scenario)");
            return false;
        }

        if (ScenarioManager.getScenarioFromString(args[0]) == null) {
            p.sendMessage(Messaging.Tag.ERROR.getTag() + "The scenario you listed was invalid.");
            return false;
        }

        ScenarioManager.Scenario scenario = ScenarioManager.getScenarioFromString(args[0]);

        scenarioManager.setCurrentScenario(scenario);

        p.sendMessage(Messaging.Tag.SUCCESS.getTag() + "The scenario has been set to " + scenario.getName());

        return false;
    }

    public String help(Player p) {
        return (ChatColor.RED + "" + ChatColor.BOLD + " - " + ChatColor.DARK_GRAY + "/game setscenario (scenario), sets servers scenario.");
    }

}
