package bxn4.craftonix_rangok;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class CraftOnix_Rangok extends JavaPlugin implements Listener {
    private final Path hely = Paths.get("");
    Map<String, Object> jatekosokRangja = new HashMap<String, Object>();
    Map<String, String> rangokChatSzinezes = new HashMap<String, String>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        File felhasznalokFile = new File(hely.toAbsolutePath() + "/plugins/CraftOnix/Rangok/felhasznalok.yml");
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(felhasznalokFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Yaml yaml = new Yaml();
        Map<String, Object> felhasznalokTerkep = (Map<String, Object>) yaml.load(inputStream);
        for (Map.Entry<String, Object> ertek : felhasznalokTerkep.entrySet()) {
            String rang = ertek.getKey();
            Object tagok = ertek.getValue();
            jatekosokRangja.put(rang,tagok);
        }
        File rangokFile = new File(hely.toAbsolutePath() + "/plugins/CraftOnix/Rangok/rangok.yml");
        InputStream rangokInputStream = null;
        try {
            rangokInputStream = new FileInputStream(rangokFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        yaml = new Yaml();
        Map<String, Map<String, Object>> rangokMap = (Map<String, Map<String, Object>>) yaml.load(rangokInputStream);
        for (Map.Entry<String, Map<String, Object>> entry : rangokMap.entrySet()) {
            String rang = entry.getKey();
            Map<String, String> ertek = new HashMap<String, String>();
            ertek.put("prefix", (String) entry.getValue().get("prefix"));
            ertek.put("nevSzin", (String) entry.getValue().get("nevSzin"));
            rangokChatSzinezes.put(rang, ertek.toString());
        }
        System.out.println(rangokChatSzinezes);
    }
}
