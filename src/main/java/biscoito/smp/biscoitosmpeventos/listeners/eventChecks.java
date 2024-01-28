package biscoito.smp.biscoitosmpeventos.listeners;

import biscoito.smp.biscoitosmpeventos.BiscoitoEventos;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;

import java.util.List;
import java.util.Objects;

public class eventChecks implements Listener {

    private final List<String> playersInEvent;
    public eventChecks(List<String> playersInEvent) {
        this.playersInEvent = playersInEvent;
    }

    public void VerifyEventPlayer(Player player, String action, String itemName) {
        if(playersInEvent.isEmpty()) return;
        if(playersInEvent.contains(player.getName())) {
            if(BiscoitoEventos.getEventType().get(0).equals(action)) {

                if (BiscoitoEventos.getEventType().get(1).equals("none")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aVocê fex o objetivo do evento!"));
                    Bukkit.getOnlinePlayers().forEach(p -> {
                        if(p.hasPermission("eventos.admin")) {
                            p.sendMessage("O jogador " + player.getName() + " realizou a ação em: " + player.getLocation().getX() + " " + player.getLocation().getY() + " " + player.getLocation().getZ() + " " + player.getLocation().getWorld());
                        }
                    });
                } else if(BiscoitoEventos.getEventType().get(1).equals(itemName)) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aVocê fex o objetivo do evento!"));

                    Bukkit.getOnlinePlayers().forEach(p -> {
                        if(p.hasPermission("eventos.admin")) {
                            p.sendMessage("O jogador " + player.getName() + " realizou a ação com o bloco/item " + itemName + " em: " + player.getLocation().getX() + " " + player.getLocation().getY() + " " + player.getLocation().getZ() + " " + player.getLocation().getWorld());
                        }
                    });
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        VerifyEventPlayer(player, "quebrar", event.getBlock().getType().name().toLowerCase());

    }

    @EventHandler
    public void onBlockPace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        VerifyEventPlayer(player, "colocar", event.getBlock().getType().name().toLowerCase());

    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent event) {
        Player player = (Player) event.getView().getPlayer();

        if(event.getRecipe() == null) {
            return;
        }

                    VerifyEventPlayer(player, "fabricar", event.getRecipe().getResult().getType().name().toLowerCase());
        }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        VerifyEventPlayer(player, "pegar", event.getItem().getType().name().toLowerCase());

    }
    @EventHandler
    public void onKill(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if(player != null) {
            if(BiscoitoEventos.getEventType().isEmpty()) return;
            if(!BiscoitoEventos.getEventType().get(0).equals("matar")) return;

        if (!event.getEntityType().name().equalsIgnoreCase(BiscoitoEventos.getEventType().get(1))) { return; }


            VerifyEventPlayer(player, "matar", "none");
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if(BiscoitoEventos.getEventType().isEmpty()) return;
        if(!BiscoitoEventos.getEventType().get(0).equals("digitar")) return;


        if(event.getMessage().equalsIgnoreCase(BiscoitoEventos.getEventType().get(1))) {


            VerifyEventPlayer(player, "digitar", "none");
        }
    }

}
