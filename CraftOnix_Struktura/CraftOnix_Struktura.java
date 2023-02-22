package bxn4.craftonix_struktura;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public final class CraftOnix_Struktura extends JavaPlugin {
    private Path hely = Paths.get("");
    public void onEnable() {
        File CraftOnixMappa = new File(hely.toAbsolutePath().toString() + "/plugins/CraftOnix");
        File FiokokMappa = new File(hely.toAbsolutePath().toString() + "/plugins/CraftOnix/Fiokok");
        if(!CraftOnixMappa.exists())
        {
            CraftOnixMappa.mkdir();
        }
        if(!FiokokMappa.exists())
        {
            FiokokMappa.mkdir();
        }
    }
}
