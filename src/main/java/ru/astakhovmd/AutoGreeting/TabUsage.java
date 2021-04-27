package ru.astakhovmd.AutoGreeting;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TabUsage implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        ArrayList<String> fill = new ArrayList<>();
        if (!command.getName().equalsIgnoreCase("autowb")) {
            return null;
        }
        fill.add("on");
        fill.add("off");
        fill.add("set");
        if (args.length>1){
            if (args[0].equalsIgnoreCase("set")){
                if (args.length>2){
                    fill.clear();
                    fill.add("on");
                    fill.add("off");
                    if (args.length>3){
                        fill.clear();
                    }else{
                        fill.clear();
                    }
                }else{
                    fill = null;
                }
            }else {
                fill.clear();
            }
        }

        Bukkit.getConsoleSender().sendMessage("args size"+ args.length);
        return fill;
    }
}
