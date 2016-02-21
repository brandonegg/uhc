package net.sail.uhc;

import me.Hippo.Plugin.API;
import net.sail.uhc.commands.Game;
import net.sail.uhc.commands.Host;
import net.sail.uhc.commands.TeamCmd;
import net.sail.uhc.listener.*;
import net.sail.uhc.manager.ScenarioManager;
import net.sail.uhc.manager.ServerScoreboardManager;
import net.sail.uhc.manager.TeamManager;
import net.sail.uhc.settings.ConfigManager;
import net.sail.uhc.settings.GameSettings;
import net.sail.uhc.utils.GameStatus;
import net.sail.uhc.utils.GameUtils;
import net.sail.uhc.utils.Messaging;
import net.sail.uhc.utils.WorldGenerator;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.HashSet;

/**
 * Created by brand on 12/30/2015.
 */
public class UHCCore extends JavaPlugin {

    private GameSettings gameSettings;
    private TeamCmd teamcmd;
    private TeamManager teamManager;
    private Messaging messaging;
    private GameStatus gameStatus;
    private ServerScoreboardManager serverScoreboardManager;
    private ConfigManager settingsConfig;
    private ScenarioManager scenarioManager;
    private GameUtils gameUtils;
    private WorldGenerator worldGenerator;

    @Override
    public void onEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.worldGenerator = new WorldGenerator();
        settingsConfig = new ConfigManager("settings");
        gameSettings = new GameSettings(settingsConfig);
        scenarioManager = new ScenarioManager(settingsConfig);
        gameUtils = new GameUtils(gameSettings);

        if (gameSettings.getTeamSize() == 1) {
            teamcmd = null;
        } else {
            teamcmd = new TeamCmd(teamManager, gameSettings, gameStatus);
        }

        messaging = new Messaging(gameSettings);
        teamManager = new TeamManager(this, gameSettings);
        gameStatus = new GameStatus(this, teamManager, gameSettings, messaging, gameUtils);
        serverScoreboardManager = new ServerScoreboardManager(gameStatus, gameSettings, teamManager, this);

        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new FoodLevelEvent(gameStatus), this);
        pm.registerEvents(new JoinEvent(gameSettings, gameStatus, serverScoreboardManager, gameUtils), this);
        pm.registerEvents(new LeaveEvent(gameSettings, gameStatus, teamManager), this);
        pm.registerEvents(new PreJoinEvent(gameStatus, gameSettings), this);
        pm.registerEvents(new ListPingEvent(gameStatus), this);
        pm.registerEvents(new GameEvents(gameSettings, gameStatus, teamManager, this, scenarioManager), this);
        pm.registerEvents(new PlayerBreakBlock(scenarioManager, gameSettings, gameStatus), this);
        pm.registerEvents(new PlayerDamagedEvent(teamManager, gameStatus, gameSettings), this);
        pm.registerEvents(new ChatManager(teamManager, gameSettings), this);

        getCommand("game").setExecutor(new Game(gameSettings, teamManager, gameStatus, scenarioManager));
        getCommand("team").setExecutor(new TeamCmd(teamManager, gameSettings, gameStatus));
        getCommand("host").setExecutor(new Host(gameSettings, gameStatus, teamManager));

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                serverScoreboardManager.loadScoreboards();
            }
        }, 40L);

        API.toggleformat(false);
    }

    @Override
    public void onDisable() {
        scenarioManager.saveScenario();
        gameSettings.saveSettings();
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(ChatColor.YELLOW + "You have been sent back to the lobby, the previous UHC server you were on closed.");
            sendToServer(p, "HUB-1");
        }
    }

    public void deleteUHCMap() {
        World playerworld = Bukkit.getWorld("uhc_map");
        Bukkit.getLogger().info(playerworld.toString());
        File folder = playerworld.getWorldFolder();
        if(new File(playerworld.getWorldFolder().getPath() + "/level.dat").exists()){
            getServer().unloadWorld(playerworld, true);
            deleteDirectory(folder);
        }
    }

    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                } //end else
            }
        }
        return( path.delete() );
    }

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("SailUHC");
    }

    public static void sendToServer(Player player, String targetServer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(out);
        try {
            dataOut.writeUTF("Connect"); //Command/Function you want to perform.
            dataOut.writeUTF(targetServer); //Server name so for hub "Hub-1"
        } catch(Exception e) {
            e.printStackTrace();
            Bukkit.broadcastMessage("§c§lSocket Error: §2" + e.getMessage()); //This is just an error message.
        }
        player.sendPluginMessage(getPlugin(), "BungeeCord", out.toByteArray()); //Sends the player to the server via bungee channel.
    }

    public void restart() {
        deleteUHCMap();
        this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "reload");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("testend")) {
            gameStatus.getGameManager().endGame();
        }
        return false;
    }
}
