package cn.lunadeer.dominion.dtos;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.managers.Translation;
import cn.lunadeer.minecraftpluginutils.JsonFile;
import cn.lunadeer.minecraftpluginutils.XLogger;
import cn.lunadeer.minecraftpluginutils.i18n.Localization;
import com.alibaba.fastjson.JSONObject;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.text.Collator;
import java.util.*;

public enum Flag implements cn.lunadeer.dominion.api.dtos.Flag {
    ANCHOR("anchor", "重生锚", "是否允许设置/使用重生锚", false, false, true),
    ANIMAL_KILLING("animal_killing", "对动物造成伤害", "是否允许对动物造成伤害", false, false, true),
    ANIMAL_SPAWN("animal_spawn", "动物生成（繁殖）", "是否允许动物生成（包括繁殖）", true, true, false),
    ANIMAL_MOVE("animal_move", "动物移动", "是否允许动物在（向）领地内移动", true, true, false),
    ANVIL("anvil", "使用铁砧", "是否允许使用铁砧", false, false, true),
    BEACON("beacon", "信标交互", "是否允许与信标交互", false, false, true),
    BED("bed", "床交互", "是否允许使用床睡觉或设置重生点", false, false, true),
    BREW("brew", "使用酿造台", "是否可以使用酿造台", false, false, true),
    BREAK_BLOCK("break", "破坏方块", "是否可以破坏方块（包括：一般方块、展示框、盔甲架）", false, false, true),
    BUTTON("button", "使用按钮", "是否可以使用各种材质的按钮", false, false, true),
    CAKE("cake", "吃蛋糕", "是否可以吃蛋糕", false, false, true),
    CONTAINER("container", "一般容器", "包含：箱子/木桶/潜影盒/盔甲架/展示框", false, false, true),
    CRAFT("craft", "使用工作台", "是否可以使用工作台", false, false, true),
    CRAFTER("crafter", "合成器", "是否可以修改自动合成器", false, false, true),
    CREEPER_EXPLODE("creeper_explode", "生物破坏/非TNT爆炸", "包含：苦力怕/凋零头颅/末影水晶/火球/床/重生锚爆炸等", false, true, true),
    COMPARER("comparer", "比较器交互", "是否可以修改比较器状态", false, false, true),
    DOOR("door", "门交互", "是否可以使用各种材质的门（包括活板门）", false, false, true),
    DRAGON_BREAK_BLOCK("dragon_break_block", "末影龙破坏方块", "末影龙冲撞是否可以破坏方块", false, true, true),
    DRAGON_EGG("dragon_egg", "触碰龙蛋", "是否可以触碰龙蛋", false, false, true),
    DYE("dye", "染色", "是否可以使用染料（对羊、狗项圈、猫项圈）染色", false, false, true),
    EDIT_SIGN("edit_sign", "编辑告示牌", "是否可以编辑告示牌", false, false, true),
    EGG("egg", "扔鸡蛋", "是否可以扔鸡蛋", false, false, true),
    ENCHANT("enchant", "使用附魔台", "是否可以使用附魔台", false, false, true),
    ENDER_MAN("ender_man", "末影人行为", "包含：末影人是否可以生成、瞬移", false, true, true),
    ENDER_PEARL("ender_pearl", "投掷末影珍珠", "是否可以使用末影珍珠", false, false, true),
    FEED("feed", "喂养动物", "是否可以喂养动物", false, false, true),
    FIRE_SPREAD("fire_spread", "火焰蔓延", "是否可以火焰蔓延", false, true, true),
    FLOW_IN_PROTECTION("flow_in_protection", "外部流体是否可以进入", "包含：岩浆、水（不会阻止领地内部的流体蔓延）", false, true, true),
    FLY("fly", "飞行", "不是翅鞘飞行，是类似于创造模式的飞行", false, false, false),
    GLOW("glow", "玩家发光", "类似光灵箭的高亮效果", false, false, true),
    GRAVITY_BLOCK("gravity_block", "允许外部重力方块落入", "如果禁止则领地外重力方块进入领地会变为掉落物", false, true, true),
    HARVEST("harvest", "收获", "收获庄稼、作物", false, false, true),
    HONEY("honey", "蜂巢交互", "是否可以采蜂蜜", false, false, true),
    HOOK("hook", "使用钓钩", "是否可以使用钓钩", false, false, true),
    HOPPER("hopper", "特殊容器", "包含：漏斗/熔炉/发射器/投掷器/高炉/烟熏炉", false, false, true),
    HOPPER_OUTSIDE("hopper_outside", "领地外漏斗对领地内箱子是否生效", "领地外的漏斗是否可以对领地内的箱子生效", false, true, true),
    IGNITE("ignite", "点燃", "是否可以使用打火石点火", false, false, true),
    ITEM_FRAME_INTERACTIVE("item_frame_interactive", "展示框交互", "是否可以与物品展示框交互（旋转展示框的东西）", false, false, true),
    ITEM_FRAME_PROJ_DAMAGE("item_frame_proj_damage", "投掷物是否可以破坏展示框/画", "非玩家发出的投掷物是否可以破坏展示框/画等悬挂物", false, true, true),
    LEVER("lever", "使用拉杆", "是否可以使用拉杆", false, false, true),
    MOB_DROP_ITEM("mob_drop_item", "生物战利品掉落", "生物死亡时是否产生掉落物", true, true, true),
    MONSTER_KILLING("monster_killing", "对怪物造成伤害", "玩家是否可以对怪物造成伤害", false, false, true),
    MONSTER_SPAWN("monster_spawn", "怪物生成", "是否可以生成怪物", false, true, false),
    MONSTER_MOVE("monster_move", "怪物移动", "是否可以在（向）领地内移动", true, true, false),
    MOVE("move", "移动", "是否可以移动", true, false, true),
    NOTE_BLOCK("note_block", "点击音符盒", "红石音乐或者某些红石机器会用到...", false, false, true),
    PLACE("place", "放置方块", "是否可以放置方块（包括：一般方块、展示框、岩浆、水）", false, false, true),
    PISTON_OUTSIDE("piston_outside", "活塞是否可以跨领地推动方块", "活塞是否可以往领地内推东西或推东西到领地外", false, true, true),
    PRESSURE("pressure", "压力板交互", "是否可以触发各种材质的压力板", false, false, true),
    RIDING("riding", "骑乘载具", "是否可以骑乘各种载具", false, false, true),
    REPEATER("repeater", "中继器交互", "是否可以与中继器交互", false, false, true),
    SHEAR("shear", "剪羊毛", "是否可以剪羊毛", false, false, true),
    SHOOT("shoot", "投掷型武器", "包括：射箭/雪球/三叉戟/风弹", false, false, true),
    SHOW_BORDER("show_border", "显示领地边界", "是否显示领地边界", true, true, true),
    TELEPORT("teleport", "领地传送", "是否开启领地传送", false, false, true),
    TNT_EXPLODE("tnt_explode", "TNT爆炸", "TNT是否可以爆炸", false, true, true),
    TRADE("trade", "村民交易", "是否可以与村民交易", false, false, true),
    TRAMPLE("trample", "作物践踏", "是否可以践踏作物（关闭意味着保护耕地）", false, true, true),
    TRIG_PRESSURE_PROJ("trig_pressure_proj", "投掷物触发压力板", "投掷物（箭/风弹/雪球）是否可以触发压力板", false, true, true),
    TRIG_PRESSURE_MOB("trig_pressure_mob", "生物触发压力板", "生物（不包含玩家）是否可以触发压力板", false, true, true),
    TRIG_PRESSURE_DROP("trig_pressure_drop", "掉落物触发压力板", "掉落物是否可以触发压力板", false, true, true),
    VEHICLE_DESTROY("vehicle_destroy", "破坏载具", "是否可以破坏载具（主要是矿车）", false, false, true),
    VEHICLE_SPAWN("vehicle_spawn", "生成载具", "是否可以生成载具（主要是矿车）", false, false, true),
    VILLAGER_KILLING("villager_killing", "对村民造成伤害", "是否可以对村民造成伤害", false, false, true),
    VILLAGER_SPAWN("villager_spawn", "村民繁殖", "是否允许村民繁殖（包括村民蛋）", true, true, true),
    WITHER_SPAWN("wither_spawn", "凋零生成", "凋零生成以及凋零生成时产生的爆炸", false, true, true),
    ;
    private final String flag_name;
    private String display_name;
    private String description;
    private Boolean default_value;
    private Boolean enable;
    private final Boolean dominion_only;
    private final String default_display_name;
    private final String default_description;

    Flag(String flagName, String displayName, String desc, boolean defaultValue, boolean dominion_only, boolean enable) {
        this.flag_name = flagName;
        this.default_display_name = displayName;
        this.display_name = displayName;
        this.default_description = desc;
        this.description = desc;
        this.default_value = defaultValue;
        this.dominion_only = dominion_only;
        this.enable = enable;
    }

    @Override
    public String getFlagName() {
        return flag_name;
    }

    @Override
    public String getDisplayName() {
        return display_name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Boolean getDefaultValue() {
        return default_value;
    }

    @Override
    public Boolean getEnable() {
        return enable;
    }

    public void setDisplayName(String displayName) {
        this.display_name = displayName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDefaultValue(Boolean defaultValue) {
        this.default_value = defaultValue;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getDisplayNameKey() {
        return "Flags." + flag_name + ".DisplayName";
    }

    public String getDescriptionKey() {
        return "Flags." + flag_name + ".Description";
    }

    public static List<Flag> getAllFlags() {
        return Arrays.asList(Flag.values());
    }

    public static List<Flag> getDominionOnlyFlagsEnabled() {
        List<Flag> flags = new ArrayList<>();
        for (Flag flag : Flag.values()) {
            if (!flag.dominion_only) {
                continue;
            }
            if (!flag.enable) {
                continue;
            }
            flags.add(flag);
        }
        Comparator<Object> comparator = Collator.getInstance(java.util.Locale.CHINA);
        flags.sort((o1, o2) -> comparator.compare(o1.getDisplayName(), o2.getDisplayName()));
        return flags;
    }

    public static boolean isDominionOnlyFlag(String flagName) {
        return getFlag(flagName).dominion_only;
    }

    public static List<Flag> getDominionFlagsEnabled() {
        List<Flag> flags = new ArrayList<>();
        for (Flag flag : Flag.values()) {
            if (!flag.enable) {
                continue;
            }
            flags.add(flag);
        }
        Comparator<Object> comparator = Collator.getInstance(java.util.Locale.CHINA);
        flags.sort((o1, o2) -> comparator.compare(o1.getDisplayName(), o2.getDisplayName()));
        return flags;
    }

    public static List<Flag> getAllDominionFlags() {
        return new ArrayList<>(Arrays.asList(Flag.values()));
    }


    public static List<Flag> getPrivilegeFlagsEnabled() {
        List<Flag> flags = new ArrayList<>();
        for (Flag flag : Flag.values()) {
            if (flag.dominion_only) {
                continue;
            }
            if (!flag.enable) {
                continue;
            }
            flags.add(flag);
        }
        Comparator<Object> comparator = Collator.getInstance(java.util.Locale.CHINA);
        flags.sort((o1, o2) -> comparator.compare(o1.getDisplayName(), o2.getDisplayName()));
        return flags;
    }

    public static List<Flag> getAllPrivilegeFlags() {
        List<Flag> flags = new ArrayList<>();
        for (Flag flag : Flag.values()) {
            if (flag.dominion_only) {
                continue;
            }
            flags.add(flag);
        }
        return flags;
    }

    public static Flag getFlag(String flagName) {
        return Arrays.stream(Flag.values()).filter(flag -> flag.getFlagName().equals(flagName)).findFirst().orElse(null);
    }

    /**
     * 从文件中加载Flag配置
     */
    public static void loadFromFile() {
        try {
            loadLegacyJsonFlags();
            loadFlagsConfiguration();
        } catch (Exception e) {
            XLogger.err(Translation.Config_Check_LoadFlagError, e.getMessage());
        }
    }

    private static void loadLegacyJsonFlags() throws Exception {
        File jsonFile = new File(Dominion.instance.getDataFolder(), "flags.json");
        if (jsonFile.exists()) {
            JSONObject jsonObject = JsonFile.loadFromFile(jsonFile);
            if (jsonObject != null) {
                deserializeFromJson(jsonObject);
            }
            jsonFile.delete();
        }
    }

    private static void loadFlagsConfiguration() throws IOException {
        File yamlFile = new File(Dominion.instance.getDataFolder(), "flags.yml");
        if (!yamlFile.exists()) {
            Dominion.instance.saveResource("flags.yml", false);
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlFile);
        for (Flag flag : getAllFlags()) {
            // load flags name & description translations
            ((Translation)(Localization.instance)).loadOrSetFlagTranslation(flag);
            // load flags default value & enable
            String defaultValueKey;
            String enableKey;
            String descriptionKey;
            if (flag.dominion_only) {
                descriptionKey = "environment." + flag.getFlagName();
                defaultValueKey = "environment." + flag.getFlagName() + ".default";
                enableKey = "environment." + flag.getFlagName() + ".enable";
            } else {
                descriptionKey = "privilege." + flag.getFlagName();
                defaultValueKey = "privilege." + flag.getFlagName() + ".default";
                enableKey = "privilege." + flag.getFlagName() + ".enable";
            }
            if (yaml.contains(defaultValueKey)) {
                flag.setDefaultValue(yaml.getBoolean(defaultValueKey));
            } else {
                yaml.set(defaultValueKey, flag.getDefaultValue());
            }
            if (yaml.contains(enableKey)) {
                flag.setEnable(yaml.getBoolean(enableKey));
            } else {
                yaml.set(enableKey, flag.getEnable());
            }
            yaml.setInlineComments(descriptionKey, Collections.singletonList(flag.getDisplayName() + "-" + flag.getDescription()));
        }
        yaml.save(yamlFile);
    }

    @Deprecated
    public static void deserializeFromJson(JSONObject jsonObject) {
        for (Flag flag : getAllFlags()) {
            try {
                JSONObject flagJson = (JSONObject) jsonObject.get(flag.getFlagName());
                if (flagJson != null) {
                    flag.setDefaultValue((Boolean) flagJson.getOrDefault("default_value", flag.getDefaultValue()));
                    flag.setEnable((Boolean) flagJson.getOrDefault("enable", flag.getEnable()));
                }
            } catch (Exception ignored) {
            }
        }
    }
}
