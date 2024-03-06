package awa.xunfeng.teamglow.config;

import awa.xunfeng.teamglow.TeamGlow;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamGlowConfig {
    private static final Map<String, TextColor> stringTextColorMap = new HashMap<>(){{
        put("black", TextColor.color(0,0,0));
        put("dark_blue", TextColor.color(0,0,170));
        put("dark_green", TextColor.color(0,170,0));
        put("dark_aqua", TextColor.color(0,170,170));
        put("dark_red", TextColor.color(170,0,0));
        put("dark_purple", TextColor.color(170,0,170));
        put("gold", TextColor.color(255,170,0));
        put("gray", TextColor.color(170,170,170));
        put("dark_gray", TextColor.color(85,85,85));
        put("blue", TextColor.color(85,85,255));
        put("green", TextColor.color(85,255,85));
        put("aqua", TextColor.color(85,255,255));
        put("red", TextColor.color(255,85,85));
        put("light_purple", TextColor.color(255,85,255));
        put("yellow", TextColor.color(255,255,85));
        put("white", TextColor.color(255,255,255));

    }};
    private static boolean enable = false;
    private static List<String> glowTeamList = new ArrayList<>();
    private static List<String> seeAllGlowTeamList = new ArrayList<>();
    private static List<TextColor> glowTeamTextColorList = new ArrayList<>();
    private static List<TextColor> seeAllGlowTeamTextColorList = new ArrayList<>();
    public static boolean isEnabled() {return enable;}
    public static List<TextColor> getGlowTeamList() {return glowTeamTextColorList;}
    public static List<TextColor> getSeeAllGlowTeamList() {return  seeAllGlowTeamTextColorList;}
    public static void load() {
        String configUrl = "config.yml";
        File file = new File(TeamGlow.getInstance().getDataFolder(), configUrl);
        if (!file.exists()) {
            TeamGlow.getInstance().saveResource(configUrl, false);
        }
        YamlConfiguration config = new YamlConfiguration();
        config.options().parseComments(true);
        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        enable = config.getBoolean("enable");
        glowTeamList = config.getStringList("Glow-TeamColors");
        glowTeamTextColorList = str2textColor(glowTeamList);
        seeAllGlowTeamList = config.getStringList("SeeAllGlow-TeamColors");
        seeAllGlowTeamTextColorList = str2textColor(seeAllGlowTeamList);
    }

    private static List<TextColor> str2textColor(List<String> ls) {
        List<TextColor> tcLs = new ArrayList<>();
        for (String colorStr : ls) {
            tcLs.add(stringTextColorMap.get(colorStr));
        }
        return tcLs;
    }
}
