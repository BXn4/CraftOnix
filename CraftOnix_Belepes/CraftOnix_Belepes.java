package me.bxn4.craftonix_belepes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import  java.io.*;

public class CraftOnix_Belepes extends JavaPlugin implements CommandExecutor {

    private HashMap<UUID, String> terkep = new HashMap<>();

    public void onEnable() {
        getCommand("belep").setExecutor(this);
        getCommand("regisztral").setExecutor(this);
    }

    public boolean onCommand(CommandSender kuldo, Command parancs, String szoveg, String[] args) {
        if (parancs.getName().equalsIgnoreCase("regisztral")) {
            Player jatekos = (Player) kuldo;
            String bemenet = String.join(" ", args);
            if (bemenet.length() > 6 && !bemenet.contains(" "))
            {
                terkep.put(jatekos.getUniqueId(), bemenet);
                jatekos.sendMessage("Sikeres regisztráció!" + jatekos.getName());
            }
            else{
                jatekos.sendMessage("§8[§4>>§8] §7A jelszavadnak minimum §l6 §r§7karakternek kell lennie, illetve nem tartalmazhat szóközt!§r" + jatekos.getName());
            }
        }
        return  true;
    }
}
