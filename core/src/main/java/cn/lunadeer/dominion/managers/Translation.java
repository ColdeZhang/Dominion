package cn.lunadeer.dominion.managers;


import cn.lunadeer.minecraftpluginutils.i18n.Localization;
import cn.lunadeer.minecraftpluginutils.i18n.i18n;
import cn.lunadeer.minecraftpluginutils.i18n.i18nField;
import org.bukkit.plugin.java.JavaPlugin;

public class Translation extends Localization {
    @i18nField(defaultValue = "用法: /dominion set <权限名称> <true/false> [领地名称]")
    public static i18n Commands_SetDominionFlagUsage;

    @i18nField(defaultValue = "用法: /dominion create <领地名称>")
    public static i18n Commands_CreateDominionUsage;
    @i18nField(defaultValue = "请先使用工具选择领地的对角线两点，或使用 /dominion auto_create <领地名称> 创建自动领地")
    public static i18n Commands_CreateSelectPointsFirst;
    @i18nField(defaultValue = "用法: /dominion create_sub <子领地名称> [父领地名称]")
    public static i18n Commands_CreateSubDominionUsage;
    @i18nField(defaultValue = "请先使用工具选择子领地的对角线两点，或使用 /dominion auto_create_sub <子领地名称> [父领地名称] 创建自动子领地")
    public static i18n Commands_CreateSubSelectPointsFirst;
    @i18nField(defaultValue = "用法: /dominion auto_create <领地名称>")
    public static i18n Commands_AutoCreateDominionUsage;
    @i18nField(defaultValue = "用法: /dominion auto_create_sub <子领地名称> [父领地名称]")
    public static i18n Commands_AutoCreateSubDominionUsage;
    @i18nField(defaultValue = "自动创建领地功能已关闭")
    public static i18n Commands_AutoCreateDominionDisabled;
    @i18nField(defaultValue = "用法: /dominion expand [大小] [领地名称]")
    public static i18n Commands_ExpandDominionUsage;
    @i18nField(defaultValue = "用法: /dominion contract [大小] [领地名称]")
    public static i18n Commands_ContractDominionUsage;
    @i18nField(defaultValue = "大小应当为整数")
    public static i18n Commands_SizeShouldBeInteger;
    @i18nField(defaultValue = "大小应当大于0")
    public static i18n Commands_SizeShouldBePositive;
    @i18nField(defaultValue = "用法: /dominion delete <领地名称>")
    public static i18n Commands_DeleteDominionUsage;
    @i18nField(defaultValue = "用法: /dominion set_enter_msg <提示语> [领地名称]")
    public static i18n Commands_SetEnterMessageUsage;
    @i18nField(defaultValue = "用法: /dominion set_leave_msg <提示语> [领地名称]")
    public static i18n Commands_SetLeaveMessageUsage;
    @i18nField(defaultValue = "用法: /dominion set_tp_location [领地名称]")
    public static i18n Commands_SetTpLocationUsage;
    @i18nField(defaultValue = "用法: /dominion rename <原领地名称> <新领地名称>")
    public static i18n Commands_RenameDominionUsage;
    @i18nField(defaultValue = "用法: /dominion give <领地名称> <玩家名称>")
    public static i18n Commands_GiveDominionUsage;
    @i18nField(defaultValue = "用法: /dominion tp <领地名称>")
    public static i18n Commands_TpDominionUsage;
    @i18nField(defaultValue = "领地不存在")
    public static i18n Commands_DominionNotExist;
    @i18nField(defaultValue = "你是OP，将忽略领地传送限制")
    public static i18n Commands_OpBypassTpLimit;
    @i18nField(defaultValue = "领地所在世界不存在")
    public static i18n Commands_WorldNotExist;
    @i18nField(defaultValue = "领地 %s 没有设置传送点，将尝试传送到中心点")
    public static i18n Commands_DominionNoTpLocation;
    @i18nField(defaultValue = "领地 %s 传送点不在领地内，将尝试传送到中心点")
    public static i18n Commands_DominionTpLocationNotInDominion;
    @i18nField(defaultValue = "已将你传送到 %s")
    public static i18n Commands_TpToDominion;
    @i18nField(defaultValue = "管理员没有开启领地传送功能")
    public static i18n Commands_TpDisabled;
    @i18nField(defaultValue = "此领地禁止传送")
    public static i18n Commands_DominionNoTp;
    @i18nField(defaultValue = "你所在的权限组组不被允许传送到这个领地")
    public static i18n Commands_GroupNoTp;
    @i18nField(defaultValue = "你不被允许传送到这个领地")
    public static i18n Commands_PrivilegeNoTp;
    @i18nField(defaultValue = "请等待 %d 秒后再传送")
    public static i18n Commands_TpCoolDown;
    @i18nField(defaultValue = "传送将在 %d 秒后执行")
    public static i18n Commands_TpDelay;
    @i18nField(defaultValue = "传送倒计时 %d 秒")
    public static i18n Commands_TpCountDown;
    @i18nField(defaultValue = "传送失败，请重试")
    public static i18n Commands_TpFailed;
    @i18nField(defaultValue = "用法: /dominion set_map_color <颜色> [领地名称]")
    public static i18n Commands_SetMapColorUsage;

    @i18nField(defaultValue = "AutoCreateRadius 不能等于 0，已重置为 10")
    public static i18n Config_AutoCreateRadiusError;
    @i18nField(defaultValue = "AutoCleanAfterDays 不能等于 0，已重置为 180")
    public static i18n Config_AutoCleanAfterDaysError;
    @i18nField(defaultValue = "工具名称设置错误，已重置为 ARROW")
    public static i18n Config_ToolNameError;
    @i18nField(defaultValue = "Limit.SizeX 尺寸不能小于 4，已重置为 128")
    public static i18n Config_LimitSizeXError;
    @i18nField(defaultValue = "Limit.SizeY 尺寸不能小于 4，已重置为 64")
    public static i18n Config_LimitSizeYError;
    @i18nField(defaultValue = "Limit.SizeZ 尺寸不能小于 4，已重置为 128")
    public static i18n Config_LimitSizeZError;
    @i18nField(defaultValue = "Limit.MinY 不能大于或等于 Limit.MaxY，已重置为 -64 320")
    public static i18n Config_LimitMinYError;
    @i18nField(defaultValue = "Economy.Refund 设置不合法，已重置为 0.85")
    public static i18n Config_RefundError;
    @i18nField(defaultValue = "Economy.Price 设置不合法，已重置为 10.0")
    public static i18n Config_PriceError;
    @i18nField(defaultValue = "启用 Limit.Vert 时 Limit.SizeY 不能小于 Limit.MaxY - Limit.MinY，已自动调整为 %d")
    public static i18n Config_LimitSizeYAutoAdjust;

    public Translation(JavaPlugin plugin) {
        super(plugin);
    }
}
