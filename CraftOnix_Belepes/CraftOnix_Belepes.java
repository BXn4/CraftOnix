package bxn4.craftonix_belepes;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.yaml.snakeyaml.Yaml;

public class CraftOnix_Belepes extends JavaPlugin implements CommandExecutor, Listener {
    private final ArrayList<String> gyakoriJelszavak = new ArrayList<String>();
    private final HashMap<UUID, String> terkep = new HashMap<UUID, String>();
    private final HashMap<Player, String> bejelentkezetlen = new HashMap<Player, String>();
    private final HashMap<String, String> bejelentkezet = new HashMap<String, String>();
    private final Path hely = Paths.get("");
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
        Objects.requireNonNull(getCommand("belep")).setExecutor(this);
        Objects.requireNonNull(getCommand("regisztral")).setExecutor(this);
        Objects.requireNonNull(getCommand("b")).setExecutor(this);
        Objects.requireNonNull(getCommand("r")).setExecutor(this);
        Objects.requireNonNull(getCommand("kijelentkez")).setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        File gyakoriJelszavakFile = new File(hely.toAbsolutePath() + "/plugins/CraftOnix/gyakoriJelszavak.jelszavak");
        try {
            Scanner scanner = new Scanner(gyakoriJelszavakFile);
            while (scanner.hasNextLine()) {
                String sorok = scanner.nextLine();
                if (sorok.startsWith("#")) {
                    continue;
                }
                gyakoriJelszavak.add(sorok);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void jatekosCsatlakozik(PlayerJoinEvent event) {
        String ipCim = "";
        Player jatekos = event.getPlayer();
        try {
            URL urlcim = new URL("https://ipapi.co/" + Objects.requireNonNull(jatekos.getAddress()).getAddress().getHostAddress() + "/ip/");
            HttpURLConnection csatlakozas = (HttpURLConnection) urlcim.openConnection();
            csatlakozas.setRequestMethod("GET");
            BufferedReader olvas = new BufferedReader(new InputStreamReader(csatlakozas.getInputStream()));
            ipCim = olvas.readLine();
            olvas.close();
            csatlakozas.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bejelentkezet.containsKey(jatekos.getName())) {
            for (Map.Entry<String, String> ellenorzes : bejelentkezet.entrySet()) {
                String cim = ellenorzes.getValue();
                if (cim.equals(ipCim)) {
                    event.setJoinMessage("");
                    jatekos.sendMessage("§8[§2>>§8] §7Sikeresen újracsatlakoztál! Ha azonnal kiszeretnél jelentkezni, akkor használd a /kijelentkez parancsot!§r");
                    Bukkit.broadcastMessage("§8[§2+§8] §7§ " + jatekos.getName());
                }
                else {
                    jatekos.kickPlayer("Nem sikerült csatlakoznod a szerverre, mert más helyről próbáltál csatlakozni. Kérlek várj pár másodpercet, vagy csatlakozz arról az eszközről, amiről kiléptél!");
                }
            }
        }
        else{
            event.setJoinMessage("");
            jatekos.setGameMode(GameMode.ADVENTURE);
            jatekosUUID = jatekos.getUniqueId().toString();
            String utvonalJatekos = hely.toAbsolutePath() + "/plugins/CraftOnix/Fiokok/" + jatekosUUID + ".yml";
            File jatekosFiok = new File(utvonalJatekos);
            jatekos.addPotionEffect(lassusagEffekt);
            jatekos.addPotionEffect(vaksagEffekt);
            jatekos.addPotionEffect(ugrasEffekt);
            jatekos.addPotionEffect(lassuBanyaszasEffekt);
            jatekos.setNoDamageTicks(Integer.MAX_VALUE);
            bejelentkezetlen.put(jatekos.getPlayer(), "Nincs bejelentkezve!");
            if (jatekosFiok.exists()) {
                jatekos.sendMessage("§8[§6>>§8] §7Üdv újra a szerveren!\n \nKérlek jelentkezz be a: §l§a/belep <jelszó> §r§7paranccsal!§r");
                new BukkitRunnable() {
                    int i = 0;

                    @Override
                    public void run() {
                        if (bejelentkezetlen.containsKey(jatekos)) {
                            jatekos.addPotionEffect(lassusagEffekt);
                            jatekos.addPotionEffect(vaksagEffekt);
                            jatekos.addPotionEffect(ugrasEffekt);
                            jatekos.addPotionEffect(lassuBanyaszasEffekt);
                            jatekos.setNoDamageTicks(Integer.MAX_VALUE);
                            jatekos.sendMessage("§7Jelentkezz be a szerverre a: §l§a/belep <jelszó> §r§7paranccsal!§r");
                            i++;
                        } else {
                            cancel();
                        }
                        if (i > 4 && bejelentkezetlen.containsKey(jatekos)) {
                            jatekos.kickPlayer("Lejárt a belépésre alkalmas időd!");
                            cancel();
                        }
                    }
                }.runTaskTimer(this, 700, 700);
            } else {
                jatekos.sendMessage("§8[§6>>§8] §7Üdv a szerveren!\n \nAhhoz, hogy tudj játszani regisztrálnod kell a szerverre. Ezt  egyszerűen megteheted a: §l§a/regisztral <jelszó> <jelszó ismét> §r§7paranccsal!§r");
                new BukkitRunnable() {
                    int i = 0;

                    @Override
                    public void run() {
                        if (bejelentkezetlen.containsKey(jatekos)) {
                            jatekos.addPotionEffect(lassusagEffekt);
                            jatekos.addPotionEffect(vaksagEffekt);
                            jatekos.addPotionEffect(ugrasEffekt);
                            jatekos.addPotionEffect(lassuBanyaszasEffekt);
                            event.getPlayer().setNoDamageTicks(Integer.MAX_VALUE);
                            jatekos.sendMessage("§7Regisztrálj a szerverre a: §l§a/regisztral <jelszó> <jelszó ismét> §r§7paranccsal!§r");
                            i++;
                        } else {
                            cancel();
                        }
                        if (i > 4 && bejelentkezetlen.containsKey(jatekos)) {
                            jatekos.kickPlayer("Lejárt a regisztrálásra alkalmas időd!");
                            cancel();
                        }
                    }
                }.runTaskTimer(this, 700, 700);
            }
        }
    }

    @EventHandler
    public void jatekosKilep(PlayerQuitEvent event) {
        Player jatekos = event.getPlayer();
        event.setQuitMessage("");
        if(!bejelentkezetlen.containsKey(jatekos)) {
            Bukkit.broadcastMessage("§8[§4-§8] §7§ " +jatekos.getName());
        }
        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                if (!jatekos.isOnline()){
                    i++;
                } else {
                    cancel();
                }
                if (i > 4) {
                    bejelentkezet.remove(jatekos);
                    cancel();
                }
            }
        }.runTaskTimer(this, 300, 300);
    }

    @EventHandler
    public void jatekosMozgas(PlayerMoveEvent event) {
        Player jatekos = event.getPlayer();
        if (bejelentkezetlen.containsKey(event.getPlayer())) {
            if (event.getFrom().getBlockX() != Objects.requireNonNull(event.getTo()).getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
                jatekos.teleport(jatekos);
            }
        }
    }
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player jatekos = event.getPlayer();
        String parancs = event.getMessage();
        if (bejelentkezetlen.containsKey(jatekos)) {
            if (!parancs.startsWith("/belep") && !parancs.startsWith("/b") && !parancs.startsWith("/regisztral") && !parancs.startsWith("/r")) {
                event.setCancelled(true);
                jatekos.sendMessage("§8[§4>>§8] §7Ezt a parancsot nem használhatod!§r");
            }
        }
    }
    @EventHandler
    public void jatekosUzenetetKuld(AsyncPlayerChatEvent event) {
        Player jatekos = event.getPlayer();
        if(bejelentkezetlen.containsKey(event.getPlayer()))
        {
            jatekos.sendMessage("§8[§4>>§8] §cAmíg nem vagy bejelentkezve, addig nem használhatod a chatet!§r");
            event.setMessage("");
            event.setCancelled(true);
        }
    }
    public boolean onCommand(CommandSender kuldo, Command parancs, String szoveg, String[] args) {
        Player jatekos = (Player) kuldo;
        String bemenet;
        if(parancs.getName().equalsIgnoreCase("kijelentkez")) {
            bejelentkezet.remove(jatekos.getName());
            jatekos.kickPlayer("Sikeresen kijelentkeztél!");
        }
        if (parancs.getName().equalsIgnoreCase("regisztral") || parancs.getName().equalsIgnoreCase("r")) {
            try {
                //System.out.println("A játékos beírta a parancsot.");
                String jelszo = "nem";
                String megerosites = "nem";
                if(!(args[0].length() == 0)) {
                    jelszo = args[0];
                    if(!(args[1].length() == 0)) {
                        megerosites = args[1];
                    }
                }
                jatekosUUID = jatekos.getUniqueId().toString();
                String utvonalJatekos = hely.toAbsolutePath() + "/plugins/CraftOnix/Fiokok/" + jatekosUUID + ".yml";
                File jatekosFiok = new File(utvonalJatekos);
                if (jatekosFiok.exists()) {
                    //System.out.println("A játékos létezik.");
                    jatekos.sendMessage("§8[§4>>§8] §7Ezt a parancsot nem használhatod!§r");
                } else {
                    //System.out.println("A játékos nem létezik.");
                    bemenet = String.join(" ", args);
                    if(gyakoriJelszavak.contains(jelszo) /*&& (bemenet == jatekos.getName())*/){
                        jatekos.sendMessage("§8[§4>>§8] §7Ez a jelszó nem biztonságos! Kérlek használj más jelszót!§r");
                    }
                    else {
                        if(jelszo.equals(megerosites)) {
                            if (jelszo.length() > 5) {
                                //System.out.println("A játékos regisztrálása.");
                                MessageDigest md = MessageDigest.getInstance("SHA-512");
                                byte[] hash = md.digest(jelszo.getBytes());
                                StringBuilder sb = new StringBuilder();
                                for (byte b : hash) {
                                    sb.append(String.format("%02x", b));
                                }
                                String jatekosU = sb.toString();
                                terkep.put(jatekos.getUniqueId(), jatekosU);
                                for (Map.Entry<UUID, String> entry : terkep.entrySet()) {
                                    Yaml yaml = new Yaml();
                                    Map<String, String> adat = new HashMap<>();
                                    adat.put("UUID", String.valueOf(entry.getKey()));
                                    adat.put("PASSWD", entry.getValue());
                                    FileWriter iras = new FileWriter(hely.toAbsolutePath() + "/plugins/CraftOnix/Fiokok/" + jatekosUUID + ".yml");
                                    yaml.dump(adat, iras);
                                    iras.close();
                                    //System.out.println("A játékos regisztrálva.");
                                }
                                //System.out.println("Kilépés a ciklusból");
                                jatekos.sendMessage("§8[§2>>§8] §7Sikeres regisztráció! A legközelebbi csatlakozásnál a megadott jelszóval tudsz bejelentkezni.§r");
                                String ipCim = "";
                                try {
                                    URL urlcim = new URL("https://ipapi.co/" + Objects.requireNonNull(Objects.requireNonNull(jatekos.getPlayer()).getAddress()).getAddress().getHostAddress() + "/ip/");
                                    HttpURLConnection csatlakozas = (HttpURLConnection) urlcim.openConnection();
                                    csatlakozas.setRequestMethod("GET");
                                    BufferedReader olvas = new BufferedReader(new InputStreamReader(csatlakozas.getInputStream()));
                                    ipCim = olvas.readLine();
                                    olvas.close();
                                    csatlakozas.disconnect();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                bejelentkezet.put(jatekos.getName(), ipCim);
                                Bukkit.broadcastMessage("§8[§2+§8] §7§ " + jatekos.getName());
                                jatekos.setGameMode(GameMode.SURVIVAL);
                                jatekos.removePotionEffect(lassusag);
                                jatekos.removePotionEffect(vaksag);
                                jatekos.removePotionEffect(ugras);
                                jatekos.removePotionEffect(lassuBanyaszas);
                                terkep.clear();
                                String cim = "§aÜdv, " + jatekos.getName() + "!";
                                String alcim = "§7Érezd jól magad!";
                                int megelenes = 5;
                                int idotartam = 70;
                                int eltunes = 10;
                                jatekos.sendTitle(cim, alcim, megelenes, idotartam, eltunes);
                                Objects.requireNonNull(jatekos.getPlayer()).setNoDamageTicks(200);
                                bejelentkezetlen.remove(jatekos.getPlayer(), "Nincs bejelentkezve!");
                                jatekos.playSound(jatekos.getLocation(), "block.note_block.pling", SoundCategory.MASTER, 1.0f, 1.0f);
                            }
                            else {
                                jatekos.sendMessage("§8[§4>>§8] §7A jelszavadnak minimum §l6 §r§7karakternek kell lennie, illetve nem tartalmazhat szóközt!§r");
                            }
                        } else {
                            jatekos.sendMessage("§8[§4>>§8] §7A megadott jelszavak nem egyeznek!§r");
                        }
                    }
                }
            } catch (Exception e) {
                jatekos.sendMessage("§7Regisztrálj a szerverre a: §l§a/regisztral <jelszó> <jelszó ismét> §r§7paranccsal!§r");
            }
        }
        if (parancs.getName().equalsIgnoreCase("belep") || parancs.getName().equalsIgnoreCase("b")) {
            try {
                bemenet = String.join(" ", args);
                jatekosUUID = jatekos.getUniqueId().toString();
                String utvonalJatekos = hely.toAbsolutePath() + "/plugins/CraftOnix/Fiokok/" + jatekosUUID + ".yml";
                File jatekosFiok = new File(utvonalJatekos);
                if (jatekosFiok.exists() && bejelentkezetlen.containsKey(jatekos.getPlayer())) {
                    Yaml yaml = new Yaml();
                    Map<String, String> adat;
                    try (FileInputStream fis = new FileInputStream(utvonalJatekos)) {
                        adat = yaml.load(fis);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    String jatekosVisszakapott = adat.get("PASSWD");
                    MessageDigest md = MessageDigest.getInstance("SHA-512");
                    byte[] hash = md.digest(bemenet.getBytes());
                    StringBuilder sb = new StringBuilder();
                    for (byte b : hash) {
                        sb.append(String.format("%02x", b));
                    }
                    String jatekosU = sb.toString();
                    if (jatekosU.equals(jatekosVisszakapott)) {
                        jatekos.sendMessage("§8[§2>>§8] §7Sikeres bejelentkezés!");
                        String ipCim = "";
                        try {
                            URL urlcim = new URL("https://ipapi.co/" + Objects.requireNonNull(Objects.requireNonNull(jatekos.getPlayer()).getAddress()).getAddress().getHostAddress() + "/ip/");
                            HttpURLConnection csatlakozas = (HttpURLConnection) urlcim.openConnection();
                            csatlakozas.setRequestMethod("GET");
                            BufferedReader olvas = new BufferedReader(new InputStreamReader(csatlakozas.getInputStream()));
                            ipCim = olvas.readLine();
                            olvas.close();
                            csatlakozas.disconnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        bejelentkezet.put(jatekos.getName(), ipCim);
                        Bukkit.broadcastMessage("§8[§2+§8] §7§ " + jatekos.getName());
                        jatekos.setGameMode(GameMode.SURVIVAL);
                        jatekos.removePotionEffect(lassusag);
                        jatekos.removePotionEffect(vaksag);
                        jatekos.removePotionEffect(ugras);
                        jatekos.removePotionEffect(lassuBanyaszas);
                        String cim = "§aÜdv újra, " + jatekos.getName() + "!";
                        String alcim = "§7Kellemes időtöltést!";
                        int megelenes = 5;
                        int idotartam = 70;
                        int eltunes = 10;
                        jatekos.sendTitle(cim, alcim, megelenes, idotartam, eltunes);
                        Objects.requireNonNull(jatekos.getPlayer()).setNoDamageTicks(200);
                        bejelentkezetlen.remove(jatekos.getPlayer(), "Nincs bejelentkezve!");
                        jatekos.playSound(jatekos.getLocation(), "block.note_block.pling", SoundCategory.MASTER, 1.0f, 1.0f);
                    } else {
                        jatekos.sendMessage("§8[§4>>§8] §7Helytelen jelszó!");
                    }
                } else {
                    jatekos.sendMessage("§8[§4>>§8] §7Ezt a parancsot nem használhatod!§r");
                }
            }
            catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return true;
    }
}
