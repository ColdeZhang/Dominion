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
    @i18nField(defaultValue = "Limit.Amount 设置不合法，已重置为 10")
    public static i18n Config_AmountError;
    @i18nField(defaultValue = "Limit.Depth 设置不合法，已重置为 3")
    public static i18n Config_DepthError;

    @i18nField(defaultValue = "语言设置，参考 languages 文件夹下的文件名")
    public static i18n ConfigComment_Language;
    @i18nField(defaultValue = "自动创建领地的半径，单位为方块")
    public static i18n ConfigComment_AutoCreateRadius;
    @i18nField(defaultValue = "-1表示不开启")
    public static i18n ConfigComment_NegativeOneDisabled;
    @i18nField(defaultValue = "默认玩家圈地限制")
    public static i18n ConfigComment_DefaultLimit;
    @i18nField(defaultValue = "出生点保护半径 出生点此范围内不允许圈地")
    public static i18n ConfigComment_SpawnProtectRadius;
    @i18nField(defaultValue = "最小Y坐标")
    public static i18n ConfigComment_MinY;
    @i18nField(defaultValue = "最大Y坐标")
    public static i18n ConfigComment_MaxY;
    @i18nField(defaultValue = "-1表示不限制")
    public static i18n ConfigComment_NegativeOneUnlimited;
    @i18nField(defaultValue = "X方向最大长度")
    public static i18n ConfigComment_SizeX;
    @i18nField(defaultValue = "Y方向最大长度")
    public static i18n ConfigComment_SizeY;
    @i18nField(defaultValue = "Z方向最大长度")
    public static i18n ConfigComment_SizeZ;
    @i18nField(defaultValue = "最大领地数量")
    public static i18n ConfigComment_Amount;
    @i18nField(defaultValue = "子领地深度")
    public static i18n ConfigComment_Depth;
    @i18nField(defaultValue = "0表示不开启")
    public static i18n ConfigComment_ZeroDisabled;
    @i18nField(defaultValue = "是否自动延伸到 MaxY 和 MinY")
    public static i18n ConfigComment_Vert;
    @i18nField(defaultValue = "不允许圈地的世界列表")
    public static i18n ConfigComment_DisabledWorlds;
    @i18nField(defaultValue = "是否允许OP无视领地限制")
    public static i18n ConfigComment_OpBypass;
    @i18nField(defaultValue = "传送延迟 秒")
    public static i18n ConfigComment_TpDelay;
    @i18nField(defaultValue = "传送冷却 秒")
    public static i18n ConfigComment_TpCoolDown;
    @i18nField(defaultValue = "自动清理长时间未上线玩家的领地（天）")
    public static i18n ConfigComment_AutoCleanAfterDays;
    @i18nField(defaultValue = "圈地工具名称")
    public static i18n ConfigComment_ToolName;
    @i18nField(defaultValue = "经济设置")
    public static i18n ConfigComment_Economy;
    @i18nField(defaultValue = "需要安装 Vault 前置及插件")
    public static i18n ConfigComment_VaultRequired;
    @i18nField(defaultValue = "圈地价格 单位每方块")
    public static i18n ConfigComment_Price;
    @i18nField(defaultValue = "是否只计算xz平面积")
    public static i18n ConfigComment_OnlyXZ;
    @i18nField(defaultValue = "删除或缩小领地时的退款比例")
    public static i18n ConfigComment_Refund;
    @i18nField(defaultValue = "飞行权限节点 - 拥有以下任意一个权限节点的玩家不会被本插件拦截飞行")
    public static i18n ConfigComment_FlyPermission;
    @i18nField(defaultValue = "是否允许玩家从 Residence 迁移领地数据")
    public static i18n ConfigComment_ResidenceMigration;
    @i18nField(defaultValue = "权限组称号 - 使用权限组当作称号(需要PlaceholderAPI插件)")
    public static i18n ConfigComment_GroupTitle;
    @i18nField(defaultValue = "变量: %dominion_group_title%")
    public static i18n ConfigComment_GroupTitleVariable;
    @i18nField(defaultValue = "前后缀如需要加颜色请使用这种格式 &#ffffff")
    public static i18n ConfigComment_GroupTitleColor;
    @i18nField(defaultValue = "性能测试计时器")
    public static i18n ConfigComment_PerformanceTimer;


    public Translation(JavaPlugin plugin) {
        super(plugin);
    }
}
