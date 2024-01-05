package biscoito.smp.biscoitosmpeventos;

import biscoito.smp.biscoitosmpeventos.commands.Event;
import biscoito.smp.biscoitosmpeventos.database.EventsDatabase;
import biscoito.smp.biscoitosmpeventos.placeholder.EventsExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class BiscoitoEventos extends JavaPlugin {

    private static BiscoitoEventos instance;

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

        getCommand("evento").setExecutor(new Event());

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new EventsExpansion(this).register();
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "=================================================");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "           BiscoitoSmp eventos ligado");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "=================================================");


    }

    @Override
    public void onDisable() {
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
