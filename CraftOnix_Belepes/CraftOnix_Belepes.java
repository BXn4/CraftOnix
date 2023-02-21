package me.bxn4.craftonix_belepes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CraftOnix_Belepes extends JavaPlugin implements CommandExecutor, Listener {
    private HashMap<UUID, String> terkep = new HashMap<>();
    public void onEnable() {
        getCommand("belep").setExecutor(this);
        getCommand("regisztral").setExecutor(this);
    }
    public boolean onCommand(CommandSender kuldo, Command parancs, String szoveg, String[] args) {
        if (parancs.getName().equalsIgnoreCase("regisztral")) {
            try {
                if (parancs.getName().equalsIgnoreCase("regisztral")) {
                    Player jatekos = (Player) kuldo;
                    String bemenet = String.join(" ", args);
                    if (bemenet.length() > 5 && !bemenet.contains(" ")) {
                        String kulcsString = "KULCS:)";
                        SecretKey kulcs = new SecretKeySpec(kulcsString.getBytes(), "AES");
                        Cipher cipher = Cipher.getInstance("AES");
                        cipher.init(Cipher.ENCRYPT_MODE, kulcs);
                        byte[] byteok = cipher.doFinal(bemenet.getBytes());
                        String jelszo = Base64.getEncoder().encodeToString(byteok);
                        terkep.put(jatekos.getUniqueId(), jelszo);
                        jatekos.sendMessage(" ");
                        jatekos.sendMessage("§8[§2>>§8] §7Sikeres regisztráció! A legközelebbi csatlakozásnál a megadott jelszóval tudsz belépni.§r");
                    } else {
                        jatekos.sendMessage(" ");
                        jatekos.sendMessage("§8[§4>>§8] §7A jelszavadnak minimum §l6 §r§7karakternek kell lennie, illetve nem tartalmazhat szóközt!§r");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
        return true;
    }
}
