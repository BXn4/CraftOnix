package bxn4.craftonix_belepes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.yaml.snakeyaml.Yaml;

public class CraftOnix_Belepes extends JavaPlugin implements CommandExecutor, Listener {
    private HashMap<UUID, String> terkep = new HashMap<>();
    private HashMap<Player, String> bejelentkezetlen = new HashMap<Player, String>();
    private Path hely = Paths.get("");
    private String bemenet = "";
    private String jatekosUUID = "";
    PotionEffectType lassusag = PotionEffectType.SLOW;
    PotionEffectType vaksag = PotionEffectType.BLINDNESS;
    PotionEffectType ugras = PotionEffectType.JUMP;
    PotionEffectType lassuBanyaszas = PotionEffectType.SLOW_DIGGING;
    int idotartam = Integer.MAX_VALUE;
    int erosseg = 255;
    PotionEffect lassusagEffekt = new PotionEffect(lassusag, idotartam, erosseg);
    PotionEffect vaksagEffekt = new PotionEffect(vaksag, idotartam, erosseg);
    PotionEffect ugrasEffekt = new PotionEffect(ugras, idotartam, 250);
    PotionEffect lassuBanyaszasEffekt = new PotionEffect(lassuBanyaszas, idotartam, erosseg);

    public void onEnable() {
        getCommand("belep").setExecutor(this);
        getCommand("regisztral").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
    }
    @EventHandler
    public void jatekosCsatlakozik(PlayerJoinEvent event){
        String utvonalJatekos = hely.toAbsolutePath().toString() + "/plugins/CraftOnix/Fiokok/" + jatekosUUID + ".yml";
        File jatekosFiok = new File(utvonalJatekos);
        Player jatekos = event.getPlayer();
        jatekos.addPotionEffect(lassusagEffekt);
        jatekos.addPotionEffect(vaksagEffekt);
        jatekos.addPotionEffect(ugrasEffekt);
        jatekos.addPotionEffect(lassuBanyaszasEffekt);
        event.getPlayer().setNoDamageTicks(Integer.MAX_VALUE);
        bejelentkezetlen.put(jatekos.getPlayer(), "Nincs bejelentkezve!");
        if (jatekosFiok.exists()) {
            jatekos.sendMessage(" \n \n§8[§6>>§8] §7Üdv újra a szerveren!\n \nKérlek jelentkezz be a: §l§a/belep <jelszó> §r§7paranccsal!§r");
        } else {
            jatekos.sendMessage(" \n \n§8[§6>>§8] §7Üdv a szerveren!\n \nAhhoz, hogy tudj játszani regisztrálnod kell a szerverre. Ezt megteheted a: §l§a/regisztral <jelszó> §r§7paranccsal!§r");
        }
    }
    @EventHandler
    public void jatekosKilep(PlayerQuitEvent event){
        Bukkit.broadcastMessage("§8[§4-§8] §7§ " + event.getPlayer().getName());
    }
    @EventHandler
    public void jatekosMozgas(PlayerMoveEvent event) {
        if (bejelentkezetlen.containsKey(event.getPlayer())) {
            if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                event.getPlayer().teleport(event.getPlayer());
            }
        }
    }
    @EventHandler
    public void sebzes(EntityDamageEvent event, PlayerEvent e) {
        if(bejelentkezetlen.containsKey(e.getPlayer())){
            if (event.getEntity() instanceof Player) {
                event.setCancelled(true);
            }
        }
    }

    public boolean onCommand(CommandSender kuldo, Command parancs, String szoveg, String[] args) {
        if (parancs.getName().equalsIgnoreCase("regisztral")) {
            try {
                Player jatekos = (Player) kuldo;
                jatekosUUID = jatekos.getUniqueId().toString();
                String utvonalJatekos = hely.toAbsolutePath().toString() + "/plugins/CraftOnix/Fiokok/" + jatekosUUID + ".yml";
                File jatekosFiok = new File(utvonalJatekos);
                if (jatekosFiok.exists()) {
                    jatekos.sendMessage("§8[§4>>§8] §7Ezt a parancsot nem használhatod!§r");
                } else {
                    bemenet = String.join(" ", args);
                    if (bemenet.length() > 5 && !bemenet.contains(" ")) {
                        SecretKey jatekosKulcs = new SecretKeySpec(ossz.getBytes(), "AES");
                        Cipher cipher = Cipher.getInstance("AES");
                        cipher.init(Cipher.ENCRYPT_MODE, jatekosKulcs);
                        byte[] byteok = cipher.doFinal(bemenet.getBytes());
                        String jatekosU = Base64.getEncoder().encodeToString(byteok);
                        terkep.put(jatekos.getUniqueId(), jatekosU);
                        jatekos.sendMessage(" \n ");
                        for (Map.Entry<UUID, String> entry : terkep.entrySet()) {
                            Yaml yaml = new Yaml();
                            Map<String, String> adat = new HashMap<>();
                            adat.put("UUID", String.valueOf(entry.getKey()));
                            adat.put("PASSWD", entry.getValue());
                            FileWriter iras = new FileWriter(hely.toAbsolutePath().toString() + "/plugins/CraftOnix/fiokok/" + jatekosUUID + ".yml");
                            yaml.dump(adat, iras);
                            iras.close();
                        }
                        Bukkit.broadcastMessage("§8[§2+§8] §7§ " + jatekos.getName());
                        jatekos.sendMessage("§8[§2>>§8] §7Sikeres regisztráció! A legközelebbi csatlakozásnál a megadott jelszóval tudsz bejelentkezni.§r");
                        jatekos.removePotionEffect(lassusag);
                        jatekos.removePotionEffect(vaksag);
                        jatekos.removePotionEffect(ugras);
                        jatekos.removePotionEffect(lassuBanyaszas);
                        terkep.clear();
                        ossz = "";
                        bemenet = "";
                        String cim = "Üdv, " + jatekos.getName() + "!";
                        String alcim = "Érezd jól magad!";
                        int megelenes = 5;
                        int idotartam = 70;
                        int eltunes = 10;
                        jatekos.sendTitle(cim, alcim, megelenes, idotartam, eltunes);
                        jatekos.getPlayer().setNoDamageTicks(200);
                        bejelentkezetlen.remove(jatekos.getPlayer(), "Nincs bejelentkezve!");
                    } else {
                        jatekos.sendMessage("§8[§4>>§8] §7A jelszavadnak minimum §l6 §r§7karakternek kell lennie, illetve nem tartalmazhat szóközt!§r");
                    }
                }
            } catch (Exception e) {
                return false;
            }

        }
        if (parancs.getName().equalsIgnoreCase("belep")) {
            try {
                Player jatekos = (Player) kuldo;
                bemenet = String.join(" ", args);
                jatekosUUID = jatekos.getUniqueId().toString();
                String utvonalJatekos = hely.toAbsolutePath().toString() + "/plugins/CraftOnix/Fiokok/" + jatekosUUID + ".yml";
                File jatekosFiok = new File(utvonalJatekos);
                if (jatekosFiok.exists()) {
                    Yaml yaml = new Yaml();
                    Map<String, String> adat;
                    try (FileInputStream fis = new FileInputStream(utvonalJatekos)) {
                        adat = yaml.load(fis);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String jatekosVisszakapott = adat.get("PASSWD");
                    SecretKey jatekosKulcs = new SecretKeySpec(ossz.getBytes(), "AES");
                    Cipher cipher = Cipher.getInstance("AES");
                    cipher.init(Cipher.DECRYPT_MODE, jatekosKulcs);
                    byte[] byteok = cipher.doFinal(Base64.getDecoder().decode(jatekosVisszakapott));
                    String jatekosU = new String(byteok);
                    if (jatekosU.equals(bemenet)){
                        Bukkit.broadcastMessage("§8[§2+§8] §7§ " + jatekos.getName());
                        jatekos.sendMessage("§8[§2>>§8] §7Sikeres bejelentkezés!");
                        jatekos.removePotionEffect(lassusag);
                        jatekos.removePotionEffect(vaksag);
                        jatekos.removePotionEffect(ugras);
                        jatekos.removePotionEffect(lassuBanyaszas);
                        String cim = "Üdv újra, " + jatekos.getName() + "!";
                        String alcim = "Kellemes időtöltést!";
                        int megelenes = 5;
                        int idotartam = 70;
                        int eltunes = 10;
                        jatekos.sendTitle(cim, alcim, megelenes, idotartam, eltunes);
                        jatekos.getPlayer().setNoDamageTicks(200);
                        bejelentkezetlen.remove(jatekos.getPlayer(), "Nincs bejelentkezve!");
                    }
                    else {
                        jatekos.sendMessage("§8[§4>>§8] §7Helytelen jelszó!");
                    }
                } else {
                    jatekos.sendMessage("§8[§4>>§8] §7Ezt a parancsot nem használhatod!§r");
                }
            } catch (NoSuchPaddingException e) {
                throw new RuntimeException(e);
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return true;
    }
}
