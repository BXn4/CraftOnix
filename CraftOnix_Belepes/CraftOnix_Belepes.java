package bxn4.craftonix_belepes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

import org.yaml.snakeyaml.Yaml;
public class CraftOnix_Belepes extends JavaPlugin implements CommandExecutor, Listener {
    private HashMap<UUID, String> terkep = new HashMap<>();
    private Path hely = Paths.get("");
    private String bemenet = "";
    private String jatekosUUID = "";

    public void onEnable() {
        getCommand("belep").setExecutor(this);
        getCommand("regisztral").setExecutor(this);
    }

    public boolean onCommand(CommandSender kuldo, Command parancs, String szoveg, String[] args) {
        if (parancs.getName().equalsIgnoreCase("regisztral")) {
            try {
                Player jatekos = (Player) kuldo;
                jatekosUUID = jatekos.getUniqueId().toString();
                String utvonalJatekos = hely.toAbsolutePath().toString() + "/plugins/CraftOnix/Fiokok/" + jatekosUUID + ".yaml";
                File jatekosFiok = new File(utvonalJatekos);
                if (jatekosFiok.exists()) {
                    jatekos.sendMessage(" ");
                    jatekos.sendMessage(" ");
                    jatekos.sendMessage("§8[§4>>§8] §7Ezt a parancsot nem használhatod!§r");
                } else {
                    bemenet = String.join(" ", args);
                    if (bemenet.length() > 5 && !bemenet.contains(" ")) {
                        cipher.init(Cipher.ENCRYPT_MODE, jatekosKulcs);
                        byte[] byteok = cipher.doFinal(bemenet.getBytes());
                        String jatekosU = Base64.getEncoder().encodeToString(byteok);
                        terkep.put(jatekos.getUniqueId(), jatekosU);
                        jatekos.sendMessage(" ");
                        jatekos.sendMessage(" ");
                        for (Map.Entry<UUID, String> entry : terkep.entrySet()) {
                            Yaml yaml = new Yaml();
                            Map<String, String> adat = new HashMap<>();
                            adat.put("UUID", String.valueOf(entry.getKey()));
                            adat.put("PASSWD", entry.getValue());
                            FileWriter iras = new FileWriter(hely.toAbsolutePath().toString() + "/plugins/CraftOnix/fiokok/" + jatekosUUID + ".yaml");
                            yaml.dump(adat, iras);
                            iras.close();
                        }
                        jatekos.sendMessage("§8[§2>>§8] §7Sikeres regisztráció! A legközelebbi csatlakozásnál a megadott jelszóval tudsz belépni.§r");
                        terkep.clear();
                        ossz = "";
                        bemenet = "";
                    } else {
                        jatekos.sendMessage(" ");
                        jatekos.sendMessage(" ");
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
                    String utvonalJatekos = hely.toAbsolutePath().toString() + "/plugins/CraftOnix/Fiokok/" + jatekosUUID + ".yaml";
                    File jatekosFiok = new File(utvonalJatekos);
                    if (jatekosFiok.exists()) {
                        Yaml yaml = new Yaml();
                        Map<String, String> data;
                        try (FileInputStream fis = new FileInputStream(utvonalJatekos)) {
                            data = yaml.load(fis);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        String jatekosVisszakapott = data.get("PASSWD");
                        jatekos.sendMessage("PASSWD A FILEBOL: " + jatekosVisszakapott);
                        jatekos.sendMessage(" ");
                        jatekos.sendMessage(" ");
                        cipher.init(Cipher.DECRYPT_MODE, jatekosKulcs);
                        byte[] byteok = cipher.doFinal(Base64.getDecoder().decode(jatekosVisszakapott));
                        String jatekosU = new String(byteok);
                        jatekos.sendMessage("jatekosU (decrypt): " + jatekosU);
                        jatekos.sendMessage("Bemenet (plain): " + bemenet);
                        if (jatekosU.equals(bemenet)){
                            jatekos.sendMessage("Bemenet (plain): " + bemenet);
                            jatekos.sendMessage("Bemenet (plain): " + bemenet);
                            jatekos.sendMessage("Bemenet (plain): " + bemenet);
                            jatekos.sendMessage("MUKIDIK");

                        }
                    } else {
                        jatekos.sendMessage(" ");
                        jatekos.sendMessage(" ");
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
