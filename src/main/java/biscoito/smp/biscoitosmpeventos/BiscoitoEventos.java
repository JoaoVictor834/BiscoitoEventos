package biscoito.smp.biscoitosmpeventos;

import biscoito.smp.biscoitosmpeventos.commands.Event;
import biscoito.smp.biscoitosmpeventos.commands.EventTab;
import biscoito.smp.biscoitosmpeventos.database.EventsDatabase;
import biscoito.smp.biscoitosmpeventos.listeners.eventChecks;
import biscoito.smp.biscoitosmpeventos.placeholder.EventsExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public final class BiscoitoEventos extends JavaPlugin {

    private static BiscoitoEventos instance;
    private List<String> playersInEvent = new ArrayList<>();

    private static List<String> eventType = new ArrayList<>();


    public static void setEventType(String action, String type) {
        eventType.clear();
        eventType.add(0, action);
        eventType.add(1, type);
    }

    public static void clearEventType() {
        eventType.clear();
    }
    public static List<String> getEventType() {
        return eventType;
    }

    public static BiscoitoEventos getInstance() {
        return instance;
    }
    private EventsDatabase eventsDatabase;

    @Override
    public void onEnable() {
        instance = this;

        try {
            if(!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }
            eventsDatabase = new EventsDatabase(getDataFolder().getAbsolutePath() + "/events.db");
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Não foi possivel se conectar a database.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        getCommand("evento").setExecutor(new Event(playersInEvent));
        getCommand("evento").setTabCompleter(new EventTab());
        getServer().getPluginManager().registerEvents(new eventChecks(playersInEvent), this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new EventsExpansion(this).register();
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "=================================================");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "           BiscoitoSmp eventos ligado");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "=================================================");


    }

    @Override
    public void onDisable() {
        playersInEvent.clear();
        clearEventType();
        try {
            eventsDatabase.closeConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public EventsDatabase getEventsDatabase() {
        return eventsDatabase;
    }

}
