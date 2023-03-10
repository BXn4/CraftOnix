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
                    jatekos.sendMessage("??8[??2>>??8] ??7Sikeresen ??jracsatlakozt??l! Ha azonnal kiszeretn??l jelentkezni, akkor haszn??ld a /kijelentkez parancsot!??r");
                    Bukkit.broadcastMessage("??8[??2+??8] ??7?? " + jatekos.getName());
                }
                else {
                    jatekos.kickPlayer("Nem siker??lt csatlakoznod a szerverre, mert m??s helyr??l pr??b??lt??l csatlakozni. K??rlek v??rj p??r m??sodpercet, vagy csatlakozz arr??l az eszk??zr??l, amir??l kil??pt??l!");
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
                jatekos.sendMessage("??8[??6>>??8] ??7??dv ??jra a szerveren!\n \nK??rlek jelentkezz be a: ??l??a/belep <jelsz??> ??r??7paranccsal!??r");
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
                            jatekos.sendMessage("??7Jelentkezz be a szerverre a: ??l??a/belep <jelsz??> ??r??7paranccsal!??r");
                            i++;
                        } else {
                            cancel();
                        }
                        if (i > 4 && bejelentkezetlen.containsKey(jatekos)) {
                            jatekos.kickPlayer("Lej??rt a bel??p??sre alkalmas id??d!");
                            cancel();
                        }
                    }
                }.runTaskTimer(this, 700, 700);
            } else {
                jatekos.sendMessage("??8[??6>>??8] ??7??dv a szerveren!\n \nAhhoz, hogy tudj j??tszani regisztr??lnod kell a szerverre. Ezt  egyszer??en megteheted a: ??l??a/regisztral <jelsz??> <jelsz?? ism??t> ??r??7paranccsal!??r");
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
                            jatekos.sendMessage("??7Regisztr??lj a szerverre a: ??l??a/regisztral <jelsz??> <jelsz?? ism??t> ??r??7paranccsal!??r");
                            i++;
                        } else {
                            cancel();
                        }
                        if (i > 4 && bejelentkezetlen.containsKey(jatekos)) {
                            jatekos.kickPlayer("Lej??rt a regisztr??l??sra alkalmas id??d!");
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
            Bukkit.broadcastMessage("??8[??4-??8] ??7?? " +jatekos.getName());
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
                jatekos.sendMessage("??8[??4>>??8] ??7Ezt a parancsot nem haszn??lhatod!??r");
            }
        }
    }
    @EventHandler
    public void jatekosUzenetetKuld(AsyncPlayerChatEvent event) {
        Player jatekos = event.getPlayer();
        if(bejelentkezetlen.containsKey(event.getPlayer()))
        {
            jatekos.sendMessage("??8[??4>>??8] ??cAm??g nem vagy bejelentkezve, addig nem haszn??lhatod a chatet!??r");
            event.setMessage("");
            event.setCancelled(true);
        }
    }
    public boolean onCommand(CommandSender kuldo, Command parancs, String szoveg, String[] args) {
        Player jatekos = (Player) kuldo;
        String bemenet;
        if(parancs.getName().equalsIgnoreCase("kijelentkez")) {
            bejelentkezet.remove(jatekos.getName());
            jatekos.kickPlayer("Sikeresen kijelentkezt??l!");
        }
        if (parancs.getName().equalsIgnoreCase("regisztral") || parancs.getName().equalsIgnoreCase("r")) {
            try {
                //System.out.println("A j??t??kos be??rta a parancsot.");
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
                    //System.out.println("A j??t??kos l??tezik.");
                    jatekos.sendMessage("??8[??4>>??8] ??7Ezt a parancsot nem haszn??lhatod!??r");
                } else {
                    //System.out.println("A j??t??kos nem l??tezik.");
                    bemenet = String.join(" ", args);
                    if(gyakoriJelszavak.contains(jelszo) /*&& (bemenet == jatekos.getName())*/){
                        jatekos.sendMessage("??8[??4>>??8] ??7Ez a jelsz?? nem biztons??gos! K??rlek haszn??lj m??s jelsz??t!??r");
                    }
                    else {
                        if(jelszo.equals(megerosites)) {
                            if (jelszo.length() > 5) {
                                //System.out.println("A j??t??kos regisztr??l??sa.");
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
                                    //System.out.println("A j??t??kos regisztr??lva.");
                                }
                                //System.out.println("Kil??p??s a ciklusb??l");
                                jatekos.sendMessage("??8[??2>>??8] ??7Sikeres regisztr??ci??! A legk??zelebbi csatlakoz??sn??l a megadott jelsz??val tudsz bejelentkezni.??r");
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
                                Bukkit.broadcastMessage("??8[??2+??8] ??7?? " + jatekos.getName());
                                jatekos.setGameMode(GameMode.SURVIVAL);
                                jatekos.removePotionEffect(lassusag);
                                jatekos.removePotionEffect(vaksag);
                                jatekos.removePotionEffect(ugras);
                                jatekos.removePotionEffect(lassuBanyaszas);
                                terkep.clear();
                                String cim = "??a??dv, " + jatekos.getName() + "!";
                                String alcim = "??7??rezd j??l magad!";
                                int megelenes = 5;
                                int idotartam = 70;
                                int eltunes = 10;
                                jatekos.sendTitle(cim, alcim, megelenes, idotartam, eltunes);
                                Objects.requireNonNull(jatekos.getPlayer()).setNoDamageTicks(200);
                                bejelentkezetlen.remove(jatekos.getPlayer(), "Nincs bejelentkezve!");
                                jatekos.playSound(jatekos.getLocation(), "block.note_block.pling", SoundCategory.MASTER, 1.0f, 1.0f);
                            }
                            else {
                                jatekos.sendMessage("??8[??4>>??8] ??7A jelszavadnak minimum ??l6 ??r??7karakternek kell lennie, illetve nem tartalmazhat sz??k??zt!??r");
                            }
                        } else {
                            jatekos.sendMessage("??8[??4>>??8] ??7A megadott jelszavak nem egyeznek!??r");
                        }
                    }
                }
            } catch (Exception e) {
                jatekos.sendMessage("??7Regisztr??lj a szerverre a: ??l??a/regisztral <jelsz??> <jelsz?? ism??t> ??r??7paranccsal!??r");
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
                        jatekos.sendMessage("??8[??2>>??8] ??7Sikeres bejelentkez??s!");
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
                        Bukkit.broadcastMessage("??8[??2+??8] ??7?? " + jatekos.getName());
                        jatekos.setGameMode(GameMode.SURVIVAL);
                        jatekos.removePotionEffect(lassusag);
                        jatekos.removePotionEffect(vaksag);
                        jatekos.removePotionEffect(ugras);
                        jatekos.removePotionEffect(lassuBanyaszas);
                        String cim = "??a??dv ??jra, " + jatekos.getName() + "!";
                        String alcim = "??7Kellemes id??t??lt??st!";
                        int megelenes = 5;
                        int idotartam = 70;
                        int eltunes = 10;
                        jatekos.sendTitle(cim, alcim, megelenes, idotartam, eltunes);
                        Objects.requireNonNull(jatekos.getPlayer()).setNoDamageTicks(200);
                        bejelentkezetlen.remove(jatekos.getPlayer(), "Nincs bejelentkezve!");
                        jatekos.playSound(jatekos.getLocation(), "block.note_block.pling", SoundCategory.MASTER, 1.0f, 1.0f);
                    } else {
                        jatekos.sendMessage("??8[??4>>??8] ??7Helytelen jelsz??!");
                    }
                } else {
                    jatekos.sendMessage("??8[??4>>??8] ??7Ezt a parancsot nem haszn??lhatod!??r");
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
