package biscoito.smp.biscoitosmpeventos.placeholder;

import biscoito.smp.biscoitosmpeventos.BiscoitoEventos;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class EventsExpansion extends PlaceholderExpansion {

    private BiscoitoEventos plugin;

    public EventsExpansion(BiscoitoEventos plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "eventos";
    }

    @Override
    public @NotNull String getAuthor() {
        return "patinho";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("wins")) {
            try {
                return String.valueOf(plugin.getEventsDatabase().getVictories((Player) player));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        }

        if(params.equalsIgnoreCase("participations")) {
            try {
                return String.valueOf(plugin.getEventsDatabase().getParticipations((Player) player));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }



        return null;
    }

}
