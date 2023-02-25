package bxn4.craftonix_rangok;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
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
    Map<Object, String> jatekosokRangja = new HashMap<Object, String>();
    Map<String, String> rangokChatSzinezes = new HashMap<String, String>();
    public Map<String, String> getRangokChatSzinezes() {
        return rangokChatSzinezes;
    }
    Permission rangokLekerdezeseJog = new Permission("craftonix_rangok.rangok", "A szerveren elérhető rangok lekérdezése.");
    Permission rangokLetrehozasaJog = new Permission("craftonix_rangok.letrehozas", "Rangok létrehozása.");
    Permission rangokTorleseJog = new Permission("craftonix_rangok.torles", "Rangok törlése.");
    Permission rangokAlpertelmezettJog = new Permission("craftonix_rangok.alapertelmezett", "Alapértelmezett rang a szerveren");
    Permission rangokhozJatekosHozzaadasaJog = new Permission("craftonix_rangok.hozzaad", "Játékos rangjának kiosztása.");
    Permission rangokhozJatekosEltavolitasaJog = new Permission("craftonix_rangok.eltavolit", "Játékos rangjának megvonása.");

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().addPermission(rangokLekerdezeseJog);
        getCommand("rangok").setExecutor(new rangokParancsok(this));
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
            //System.out.println(entry.getKey());
            String rang = ertek.getKey();
            Object tagok = ertek.getValue();
            if (tagok instanceof List) {
                List<String> tagList = (List<String>) tagok;
                for (String tag : tagList) {
                    jatekosokRangja.put(tag, rang);
                }
            } else {
                jatekosokRangja.put(tagok, rang);
            }
        }
        //System.out.println(jatekosokRangja);

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
        //System.out.println(rangokChatSzinezes);
    }


    @EventHandler
    public void jatekosUzenetetKuld(AsyncPlayerChatEvent event) {
        Player jatekos = event.getPlayer();
        String rang = (jatekosokRangja.get(jatekos.getName()));
        System.out.println(rangokChatSzinezes);
        String szinezesEsPrefixRang = (rangokChatSzinezes.get(rang));
        String prefixSzin = szinezesEsPrefixRang.substring(1, szinezesEsPrefixRang.length() - 1);
        String[] elvalaszt = prefixSzin.split(",");
        String prefix = "";
        String nevSzin = "";
        for (String szoveg : elvalaszt) {
            String[] kulcsErtek = szoveg.split("=");
            String kulcs = kulcsErtek[0].trim();
            String ertek = kulcsErtek[1].trim();
            if (kulcs.equals("prefix")) {
                prefix = ertek;  // fix here
            } else if (kulcs.equals("nevSzin")) {
                nevSzin = ertek;
            }
            //System.out.println(prefix);
            String jatekosUzeneteRanggal = prefix + nevSzin + jatekos.getName() + "§7 >> §r" + event.getMessage();
            event.setFormat(jatekosUzeneteRanggal);
        }
    }
}
