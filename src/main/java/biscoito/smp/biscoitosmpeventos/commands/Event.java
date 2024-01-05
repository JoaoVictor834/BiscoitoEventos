package biscoito.smp.biscoitosmpeventos.commands;

import biscoito.smp.biscoitosmpeventos.BiscoitoEventos;
import biscoito.smp.biscoitosmpeventos.database.EventsDatabase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Event implements CommandExecutor {

    private boolean isEvent;
    private String eventName;

    private List<Player> playersInEvent = new ArrayList<>();

    private Location eventLoc;

    public String MsgColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSomente players podem executar este comando."));
            return true;
        }

        EventsDatabase db = BiscoitoEventos.getInstance().getEventsDatabase();
        Player player = ((Player) commandSender);

        if (args.length == 0) {
            if(!isEvent) {
                player.sendMessage(MsgColor("&cNenhum evento está ocorrendo!"));
                return true;

            }

            if(playersInEvent.contains(player)) {
                player.sendMessage(MsgColor("&cVocê ja está no evento!"));
                return true;
            }

            try {
                db.updatePlayerParticipations(player, db.getParticipations(player) + 1);
            } catch (SQLException ex) {
                player.sendMessage(MsgColor("&cUm erro ocorreu."));
                ex.printStackTrace();
                return true;
            }

            playersInEvent.add(player);
            player.teleport(eventLoc);
            player.sendMessage(MsgColor("&aVocê está participando do evento " + eventName + "."));


            return true;
        }

        if (args[0].equals("iniciar")) {

            if (!player.hasPermission("eventos.start")) {
                player.sendMessage(MsgColor("&cVocê não tem permissão para executar este comando!"));
                return true;
            }

            if (args.length < 2) {
                player.sendMessage(MsgColor("&cUse: /evento iniciar <nome do evento>"));
                return true;
            }

            if(isEvent) {
                player.sendMessage(MsgColor("&cUm evento está ocorrendo!"));
                return true;

            }

            isEvent = true;
            eventName = args[1];
            eventLoc = player.getLocation();

            player.sendMessage(MsgColor("&bVocê iniciou o evento: &a" + eventName + "!"));
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.showTitle(Title.title(Component.text("Evento " + eventName + " Começou!").color(TextColor.color(0xFF13)), Component.text("use /evento para participar!").color(TextColor.color(0xFEFF))));
            });


        } else if (args[0].equals("terminar")) {

            if (!player.hasPermission("eventos.terminar")) {
                player.sendMessage(MsgColor("&cVocê não tem permissão para executar este comando!"));
                return true;
            }

            if (args.length < 2) {
                player.sendMessage(MsgColor("&cUse: /evento terminar <player>"));
                return true;
            }

            if(!isEvent) {
                player.sendMessage(MsgColor("&Nenhum evento está ocorrendo!"));
                return true;

            }

            Player winner = Bukkit.getPlayer(args[1]);
            if (winner == null) {
                player.sendMessage(MsgColor("&cPlayer não encontrado."));
                return true;
            }

            isEvent = false;

            playersInEvent.forEach(p -> {
                p.teleport(p.getWorld().getSpawnLocation());
            });

            try {
                db.updatePlayerVictories(player, db.getVictories(player) + 1);
                playersInEvent.clear();
            } catch (SQLException ex) {
                player.sendMessage(MsgColor("&cUm erro ocorreu em adicionar a vitoria do ganhador!"));
                ex.printStackTrace();
            }

            Bukkit.getOnlinePlayers().forEach(p -> {
                p.sendMessage(MsgColor("&7[&cBiscoitoSMP&7] &bO evento &a" + eventName + " &bterminou! O ganhador foi: &a" + winner.getName() + "&b!"));
            });

        }
        return true;
    }
}
