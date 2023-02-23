package bxn4.craftonix_parancsok;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class CraftOnix_Parancsok extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("cc").setExecutor(this);

    }

    public boolean onCommand(CommandSender kuldo, Command parancs, String szoveg, String[] args) {
        Player jatekos = (Player) kuldo;
        if (parancs.getName().equalsIgnoreCase("cc")) {
            for (int i = 0; i < 100; i++) {
                jatekos.sendMessage("");
            }
        }
        return true;
    }
}
