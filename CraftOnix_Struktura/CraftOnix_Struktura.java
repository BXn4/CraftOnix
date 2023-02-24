package bxn4.craftonix_struktura;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.swing.*;

public final class CraftOnix_Struktura extends JavaPlugin {
    private Path hely = Paths.get("");
    Map<String, Map<String, Object>> rangokAdatai = new LinkedHashMap<>();
    Map<String, Object> tagAdatok = new LinkedHashMap<>();
    Map<String, Object> adminAdatok = new LinkedHashMap<>();
    Map<String, Object> tulajdonosAdatok = new LinkedHashMap<>();
    DumperOptions dumper = new DumperOptions();

    public void onEnable() {
        File CraftOnixMappa = new File(hely.toAbsolutePath().toString() + "/plugins/CraftOnix");
        File FiokokMappa = new File(hely.toAbsolutePath().toString() + "/plugins/CraftOnix/Fiokok");
        File gyakoriJelszavakFile = new File(hely.toAbsolutePath().toString() + "/plugins/CraftOnix/gyakoriJelszavak.jelszavak");
        File rangokMappa = new File(hely.toAbsolutePath().toString() + "/plugins/CraftOnix/Rangok");
        File rangokFile = new File(hely.toAbsolutePath().toString() + "/plugins/CraftOnix/Rangok/rangok.yml");
        File rangokFelhasznalok = new File(hely.toAbsolutePath().toString() + "/plugins/CraftOnix/Rangok/felhasznalok.yml");
        if (!CraftOnixMappa.exists()) {
            CraftOnixMappa.mkdir();
        }
        if (!FiokokMappa.exists()) {
            FiokokMappa.mkdir();
        }
        if (!gyakoriJelszavakFile.exists()) {
            String[] gyakoriJelszavak = {"# Ez a fájl a szerver biztonságáért felel. Kérlek ne távolítsd el az alábbi elemeket!",
                    "# A leggyakoribb jelszavak listája 2020.",
                    "# Ezekkel a jelszavakkal nem lehet regisztálni, mivel gyakoriak, és könnyen feltörhetők.",
                    "# Forrás: https://github.com/danielmiessler/SecLists/blob/master/Passwords/2020-200_most_used_passwords.txt",
                    "123456", "123456789", "picture1", "password", "12345678", "111111", "123123", "12345", "1234567890", "senha",
                    "1234567", "qwerty", "abc123", "Million2", "000000", "1234", "iloveyou", "aaron431", "password1", "qqww1122",
                    "123", "omgpop", "123321", "654321", "qwertyuiop", "qwer123456", "123456a", "a123456", "666666", "asdfghjkl",
                    "ashley", "987654321", "unknown", "zxcvbnm", "112233", "chatbooks", "20100728", "123123123", "princess",
                    "jacket025", "evite", "123abc", "123qwe", "sunshine", "121212", "dragon", "1q2w3e4r", "5201314", "159753",
                    "pokemon", "qwerty123", "Bangbang123", "jobandtalent", "monkey", "1qaz2wsx", "abcd1234", "default", "aaaaaa",
                    "soccer", "123654", "ohmnamah23", "12345678910", "zing", "shadow", "102030", "11111111", "asdfgh", "147258369",
                    "qazwsx", "qwe123", "michael", "football", "baseball", "1q2w3e4r5t", "party", "daniel", "asdasd", "222222",
                    "myspace1", "asd123", "555555", "a123456789", "888888", "7777777", "fuckyou", "1234qwer", "superman", "147258",
                    "999999", "159357", "love123", "tigger", "purple", "samantha", "charlie", "babygirl", "88888888", "jordan23",
                    "789456123", "jordan", "anhyeuem", "killer", "basketball", "michelle", "1q2w3e", "lol123", "qwerty1", "789456",
                    "6655321", "nicole", "naruto", "master", "chocolate", "maggieown", "computer", "hannah", "jessica", "123456789a",
                    "password123", "hunter", "686584", "iloveyou1", "justin", "cookie", "hello", "blink182", "andrew", "25251325",
                    "love", "987654", "bailey", "princess1", "101010", "12341234", "a801016", "1111", "1111111", "anthony", "yugioh",
                    "fuckyou1", "amanda", "asdf1234", "trustno1", "butterfly", "x4ivygA51F", "iloveu", "batman", "starwars",
                    "summer", "michael1", "00000000", "lovely", "jakcgt333", "buster", "jennifer", "babygirl1", "family", "456789",
                    "azerty", "andrea", "q1w2e3r4", "qwer1234", "hello123", "10203", "matthew", "pepper", "12345a", "letmein",
                    "joshua", "131313", "123456b", "madison", "Sample123", "777777", "football1", "jesus1", "taylor", "b123456",
                    "whatever", "welcome", "ginger", "flower", "333333", "1111111111", "robert", "samsung", "a12345", "loveme",
                    "gabriel", "alexander", "cheese", "passw0rd", "142536", "peanut", "11223344", "thomas", "angel1", "jelszo",
                    "jelszó", "minecraft"};
            try {
                FileWriter iras = new FileWriter(gyakoriJelszavakFile);
                for (String jelszo : gyakoriJelszavak) {
                    iras.write(jelszo + System.lineSeparator());
                }
                iras.close();
                gyakoriJelszavak = null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!rangokMappa.exists()) {
            rangokMappa.mkdir();
        }
        if (!rangokFile.exists()) {
            tagAdatok.put("prefix", "Tag");
            tagAdatok.put("nevSzin", "§2");
            tagAdatok.put("jogok", Arrays.asList("nincs"));
            rangokAdatai.put("Tag", tagAdatok);
            adminAdatok.put("prefix", "Admin");
            adminAdatok.put("nevSzin", "§4");
            adminAdatok.put("jogok", Arrays.asList("nincs"));
            rangokAdatai.put("Admin", adminAdatok);
            adminAdatok.put("prefix", "Admin");
            adminAdatok.put("jogok", Arrays.asList("nincs"));
            rangokAdatai.put("Admin", adminAdatok);
            tulajdonosAdatok.put("prefix", "Tulajdonos");
            tulajdonosAdatok.put("nevSzin", "§e");
            tulajdonosAdatok.put("jogok", Arrays.asList("nincs"));
            rangokAdatai.put("Tulajdonos", tulajdonosAdatok);
            dumper.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(dumper);
            try {
                FileWriter iras = new FileWriter(rangokFile);
                yaml.dump(rangokAdatai, iras);
                iras.close();
                rangokAdatai.clear();
                tagAdatok.clear();
                adminAdatok.clear();
                tulajdonosAdatok.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!rangokFelhasznalok.exists()) {
            try {
                FileWriter iras = new FileWriter(rangokFelhasznalok);
                iras.write("Tag:\n");
                iras.write("  -\n");
                iras.write("Admin:\n");
                iras.write("  -\n");
                iras.write("Tulajdonos:\n");
                iras.write("  -\n");
                iras.close();
            } catch (IOException e) {
                return;
            }
        }
    }
}
