package biscoito.smp.biscoitosmpeventos.commands;

import biscoito.smp.biscoitosmpeventos.BiscoitoEventos;
import biscoito.smp.biscoitosmpeventos.database.EventsDatabase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class Event implements CommandExecutor {

    private boolean isEvent;
    private boolean eventFinish;
    private String eventName;
    private List<String> playersInEvent;

    private HashMap<String, Location> pLoc = new HashMap<>();
    private Location eventLoc;

    public Event(List<String> playersInEvent) {
        this.playersInEvent = playersInEvent;
    }

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
                player.sendMessage(MsgColor("&cNenhum evento está ocorrendo ou está fechado!"));
                return true;

            }

            if(playersInEvent.contains(player.getName())) {
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

            playersInEvent.add(player.getName());
            pLoc.put(player.getName(), player.getLocation());
            player.teleport(eventLoc);
            player.sendMessage(MsgColor("&aVocê está participando do evento " + eventName + "."));
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "/lp user " + player.getName() + " parent set eventos");

            return true;
        }

        switch (args[0]) {
            case "iniciar":

                if (!player.hasPermission("eventos.start")) {
                    player.sendMessage(MsgColor("&cVocê não tem permissão para executar este comando!"));
                    return true;
                }

                if (args.length < 4) {
                    player.sendMessage(MsgColor("&cUse: /evento iniciar <tipo do evento> <bloco/item/palavra> <nome do evento>"));
                    return true;
                }

                if (isEvent) {
                    player.sendMessage(MsgColor("&cUm evento já está ocorrendo!"));
                    return true;
                }

                List<String> materials = new ArrayList<>();
                for (Material m : Material.values()) {
                    materials.add(m.name().toLowerCase());
                }


                switch (args[1]) {
                    case "custom":
                        BiscoitoEventos.setEventType("custom", "none");
                        break;
                    case "matar":
                        BiscoitoEventos.setEventType("matar", args[2]);
                        break;
                    case "digitar":
                        BiscoitoEventos.setEventType("digitar", args[2]);
                        break;
                    case "quebrar":
                        if (!materials.contains(args[2])) {
                            player.sendMessage(MsgColor("&cItem/Bloco invalido"));
                            return true;
                        }
                        BiscoitoEventos.setEventType("quebrar", args[2]);
                        break;
                    case "colocar":
                        if (!materials.contains(args[2])) {
                            player.sendMessage(MsgColor("&cItem/Bloco invalido"));
                            return true;
                        }
                        BiscoitoEventos.setEventType("colocar", args[2]);
                        break;

                    case "fabricar":
                        if (!materials.contains(args[2])) {
                            player.sendMessage(MsgColor("&cItem/Bloco invalido"));
                            return true;
                        }
                        BiscoitoEventos.setEventType("fabricar", args[2]);
                        break;
                    case "pegar":
                        if (!materials.contains(args[2])) {
                            player.sendMessage(MsgColor("&cItem/Bloco invalido"));
                            return true;
                        }
                        BiscoitoEventos.setEventType("pegar", args[2]);
                        break;
                    default:
                        player.sendMessage(MsgColor("&cVocê digitou um tipo de evento errado!"));
                        return true;
                }


                isEvent = true;
                eventName = args[3];
                eventLoc = player.getLocation();

                player.sendMessage(MsgColor("&bVocê iniciou o evento: &a" + eventName + "!"));
                Bukkit.getOnlinePlayers().forEach(p -> {
                    p.sendMessage(MsgColor("&7[&cBiscoitoSMP&7] &bO evento &a" + eventName + " &fdo tipo &a" + BiscoitoEventos.getEventType() + " &bcomeçou use /evento para entrar!"));

                    p.showTitle(Title.title(Component.text("Evento " + eventName + " Começou!").color(TextColor.color(0xFF13)), Component.text("use /evento para participar!").color(TextColor.color(0xFEFF))));
                });

                break;
            case "fechar":

                if (!isEvent) {
                    player.sendMessage(MsgColor("&Nenhum evento está ocorrendo!"));
                    return true;

                }

                if (!player.hasPermission("eventos.fechar")) {
                    player.sendMessage(MsgColor("&cVocê não tem permissão para executar este comando!"));
                    return true;
                }
                player.sendMessage(MsgColor("&bVocê fechou o evento, ninguem mais conseguirá entrar!"));

                isEvent = false;
                eventFinish = true;

                break;
            case "terminar":

                if (!player.hasPermission("eventos.terminar")) {
                    player.sendMessage(MsgColor("&cVocê não tem permissão para executar este comando!"));
                    return true;
                }

                if (args.length < 2) {
                    player.sendMessage(MsgColor("&cUse: /evento terminar <player>"));
                    return true;
                }

                if (!eventFinish) {
                    player.sendMessage(MsgColor("&cO evento ainda não acabou."));
                    return true;

                }

                Player winner = Bukkit.getPlayer(args[1]);
                if (winner == null) {
                    player.sendMessage(MsgColor("&cPlayer não encontrado."));
                    return true;
                }

                BiscoitoEventos.clearEventType();
                isEvent = false;
                eventFinish = false;
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (playersInEvent.contains(p.getName())) {
                        p.teleport(pLoc.get(p.getName()));
                    }
                });
                pLoc.clear();
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "/lp user " + player.getName() + " parent remove eventos");


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

                break;
        }
        return true;
    }
}
