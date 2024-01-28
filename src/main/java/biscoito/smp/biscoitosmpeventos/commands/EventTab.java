package biscoito.smp.biscoitosmpeventos.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EventTab implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if(args.length == 1) {
           completions.add("iniciar");
           completions.add("fechar");
           completions.add("terminar");

        } else if (args.length == 2) {
            if(args[0].equals("iniciar")) {
                completions.add("custom");
                completions.add("matar");
                completions.add("pegar");
                completions.add("quebrar");
                completions.add("colocar");
                completions.add("fabricar");
                completions.add("digitar");
            }
            } else if (args.length == 3) {
            if (args[1].equals("colocar") || args[1].equals("quebrar")) {
                for (Material m : Material.values()) {
                    if(m.isBlock()) {
                        completions.add(m.name().toLowerCase());
                    }
                }
            } else if (args[1].equals("fabricar") || args[1].equals("pegar")) {
                for (Material m : Material.values()) {
                    if(m.isItem()) {
                        completions.add(m.name().toLowerCase());
                    }
                }
            } else if (args[1].equals("matar")) {
                for (EntityType et : EntityType.values()) {
                   completions.add(et.name().toLowerCase());
                }
            } else if (args[1].equals("custom")) {
                completions.add("customizado");
            }

        }


        return completions;
    }
}
