package bxn4.craftonix_rangok;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class rangokParancsok implements CommandExecutor {
    private final CraftOnix_Rangok plugin;
    private final Path hely = Paths.get("");

    public rangokParancsok(CraftOnix_Rangok plugin) {
        this.plugin = plugin;
    }
    PluginManager pluginManager = Bukkit.getPluginManager();
    Plugin RangokPlugin = pluginManager.getPlugin("CraftOnix_Rangok");
    DumperOptions dumper = new DumperOptions();
    Map<String, Map<String, Object>> ujRang = new LinkedHashMap<>();
    Map<String, Object> ujrangAdatai = new LinkedHashMap<>();

    @Override
    public boolean onCommand(CommandSender kuldo, Command parancs, String label, String[] args) {
        Player jatekos = (Player) kuldo;
        if (args.length == 0 || args[0].equalsIgnoreCase("rangok")) {
            Map<String, String> rangokChatSzinezes = plugin.getRangokChatSzinezes();
            Set<String> rangok = rangokChatSzinezes.keySet();
            jatekos.sendMessage("§8[§2>>§8] §7A szerveren megtalálható rangok: " + rangok.toString());
        } else if (args[0].equalsIgnoreCase("letrehoz")) {
            if (args.length == 1) {
                jatekos.sendMessage("Nem adtad meg az új rangot!");
            }
            ujrangAdatai.put("prefix", args[1]);
            ujrangAdatai.put("nevSzin", "§2");
            ujrangAdatai.put("jogok", Arrays.asList("nincs"));
            ujRang.put(args[1], ujrangAdatai);
            dumper.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(dumper);
            try {
                FileWriter iras = new FileWriter(hely.toAbsolutePath() + "/plugins/CraftOnix/Rangok/rangok.yml", true);
                yaml.dump(ujRang, iras);
                iras.close();
                ujRang.clear();
                ujrangAdatai.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pluginManager.disablePlugin(RangokPlugin);
            pluginManager.enablePlugin(RangokPlugin);
            jatekos.sendMessage("Sikeresen létrehoztad a " + args[1] + " nevű rangot!");
        } else if (args[0].equalsIgnoreCase("letrehoz")) {
            if (args.length == 1) {
                jatekos.sendMessage("Nem adtad meg az új rangot!");
            } else {
                ujrangAdatai.put("prefix", args[1]);
                ujrangAdatai.put("nevSzin", "§2");
                ujrangAdatai.put("jogok", Arrays.asList("nincs"));
                ujRang.put(args[1], ujrangAdatai);
                dumper.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                Yaml yaml = new Yaml(dumper);
                try {
                    FileWriter iras = new FileWriter(hely.toAbsolutePath() + "/plugins/CraftOnix/Rangok/rangok.yml", true);
                    yaml.dump(ujRang, iras);
                    iras.close();
                    ujRang.clear();
                    ujrangAdatai.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pluginManager.disablePlugin(RangokPlugin);
                pluginManager.enablePlugin(RangokPlugin);
                jatekos.sendMessage("Sikeresen létrehoztad a(z) " + args[1] + " nevű rangot!");

            }
        }
            else if (args[0].equalsIgnoreCase("torol")) {
            if (args.length == 1) {
                jatekos.sendMessage("Nem adtad meg a törlendő rangot!");
            } else {
                Yaml yaml = new Yaml();
                InputStream utvonal = null;
                try {
                    utvonal = Files.newInputStream(hely.toAbsolutePath().resolve("plugins/CraftOnix/Rangok/rangok.yml"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Map<String, Object> adat = yaml.load(utvonal);
                adat.remove(args[1]);
                try {
                    FileWriter iras = new FileWriter(hely.toAbsolutePath() + "/plugins/CraftOnix/Rangok/rangok.yml");
                    yaml.dump(adat, iras);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                pluginManager.disablePlugin(RangokPlugin);
                pluginManager.enablePlugin(RangokPlugin);
                jatekos.sendMessage("Sikeresen törölted a(z) " + args[1] + " nevű rangot!");
            }
        }else {
            jatekos.sendMessage("Ismeretlen parancs! Az elérhető parancsokért használd a /rangok help parancsot!");
        }
        return true;
    }
}