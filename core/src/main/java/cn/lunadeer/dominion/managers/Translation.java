package cn.lunadeer.dominion.managers;


import cn.lunadeer.dominion.dtos.Flag;
import cn.lunadeer.minecraftpluginutils.i18n.Localization;
import cn.lunadeer.minecraftpluginutils.i18n.i18n;
import cn.lunadeer.minecraftpluginutils.i18n.i18nField;
import org.bukkit.plugin.java.JavaPlugin;

public class Translation extends Localization {
    @i18nField(defaultValue = "用法: /dominion set <权限名称> <true/false> [领地名称]")
    public static i18n Commands_Dominion_SetFlagUsage;

    @i18nField(defaultValue = "用法: /dominion create <领地名称>")
    public static i18n Commands_Dominion_CreateDominionUsage;
    @i18nField(defaultValue = "请先使用工具选择领地的对角线两点，或使用 /dominion auto_create <领地名称> 创建自动领地")
    public static i18n Commands_Dominion_CreateSelectPointsFirst;
    @i18nField(defaultValue = "用法: /dominion create_sub <子领地名称> [父领地名称]")
    public static i18n Commands_Dominion_CreateSubDominionUsage;
    @i18nField(defaultValue = "请先使用工具选择子领地的对角线两点，或使用 /dominion auto_create_sub <子领地名称> [父领地名称] 创建自动子领地")
    public static i18n Commands_Dominion_CreateSubSelectPointsFirst;
    @i18nField(defaultValue = "用法: /dominion auto_create <领地名称>")
    public static i18n Commands_Dominion_AutoCreateDominionUsage;
    @i18nField(defaultValue = "用法: /dominion auto_create_sub <子领地名称> [父领地名称]")
    public static i18n Commands_Dominion_AutoCreateSubDominionUsage;
    @i18nField(defaultValue = "自动创建领地功能已关闭")
    public static i18n Commands_Dominion_AutoCreateDominionDisabled;
    @i18nField(defaultValue = "用法: /dominion expand [大小] [领地名称]")
    public static i18n Commands_Dominion_ExpandDominionUsage;
    @i18nField(defaultValue = "用法: /dominion contract [大小] [领地名称]")
    public static i18n Commands_Dominion_ContractDominionUsage;
    @i18nField(defaultValue = "大小应当为整数")
    public static i18n Commands_Dominion_SizeShouldBeInteger;
    @i18nField(defaultValue = "大小应当大于0")
    public static i18n Commands_Dominion_SizeShouldBePositive;
    @i18nField(defaultValue = "用法: /dominion delete <领地名称>")
    public static i18n Commands_Dominion_DeleteDominionUsage;
    @i18nField(defaultValue = "用法: /dominion set_enter_msg <提示语> [领地名称]")
    public static i18n Commands_Dominion_SetEnterMessageUsage;
    @i18nField(defaultValue = "用法: /dominion set_leave_msg <提示语> [领地名称]")
    public static i18n Commands_Dominion_SetLeaveMessageUsage;
    @i18nField(defaultValue = "用法: /dominion set_tp_location [领地名称]")
    public static i18n Commands_Dominion_SetTpLocationUsage;
    @i18nField(defaultValue = "用法: /dominion rename <原领地名称> <新领地名称>")
    public static i18n Commands_Dominion_RenameDominionUsage;
    @i18nField(defaultValue = "用法: /dominion give <领地名称> <玩家名称>")
    public static i18n Commands_Dominion_GiveDominionUsage;
    @i18nField(defaultValue = "用法: /dominion tp <领地名称>")
    public static i18n Commands_Dominion_TpDominionUsage;
    @i18nField(defaultValue = "领地不存在")
    public static i18n Commands_Dominion_DominionNotExist;
    @i18nField(defaultValue = "用法: /dominion set_map_color <颜色> [领地名称]")
    public static i18n Commands_Dominion_SetMapColorUsage;

    @i18nField(defaultValue = "用法: /dominion group create <领地名称> <权限组名称>")
    public static i18n Commands_Group_CreateGroupUsage;
    @i18nField(defaultValue = "用法: /dominion group delete <领地名称> <权限组名称>")
    public static i18n Commands_Group_DeleteGroupUsage;
    @i18nField(defaultValue = "用法: /dominion group rename <领地名称> <权限组旧名称> <新名称>")
    public static i18n Commands_Group_RenameGroupUsage;
    @i18nField(defaultValue = "用法: /dominion group set_flag <领地名称> <权限组名称> <权限名称> <true|false>")
    public static i18n Commands_Group_SetGroupFlagUsage;
    @i18nField(defaultValue = "用法: /dominion group add_member <领地名称> <权限组名称> <玩家名称>")
    public static i18n Commands_Group_AddGroupMemberUsage;
    @i18nField(defaultValue = "用法: /dominion group remove_member <领地名称> <权限组名称> <玩家名称>")
    public static i18n Commands_Group_RemoveGroupMemberUsage;
    @i18nField(defaultValue = "用法: /dominion group <create|delete|rename|set_flag|add_member|remove_member|select_member|setting|list>")
    public static i18n Commands_Group_GroupUsage;
    @i18nField(defaultValue = "新权限组名称")
    public static i18n Commands_Group_NewGroupName;

    @i18nField(defaultValue = "用法: /dominion member add <领地名称> <玩家名称>")
    public static i18n Commands_Member_DominionAddMemberUsage;
    @i18nField(defaultValue = "用法: /dominion member set_flag <领地名称> <玩家名称> <权限名称> <true/false>")
    public static i18n Commands_Member_DominionSetFlagUsage;
    @i18nField(defaultValue = "用法: /dominion member remove <领地名称> <玩家名称>")
    public static i18n Commands_Member_DominionRemoveMemberUsage;
    @i18nField(defaultValue = "用法: /dominion member apply_template <领地名称> <玩家名称> <模板名称>")
    public static i18n Commands_Member_DominionApplyTemplateUsage;
    @i18nField(defaultValue = "用法: /dominion member <add/set_flag/remove/apply_template/list/setting/select_player/select_template>")
    public static i18n Commands_Member_MemberUsage;

    @i18nField(defaultValue = "页码(可选)")
    public static i18n Commands_PageOptional;
    @i18nField(defaultValue = "参数不足")
    public static i18n Commands_ArgumentsNotEnough;
    @i18nField(defaultValue = "未知参数")
    public static i18n Commands_UnknownArgument;
    @i18nField(defaultValue = "大小(整数)")
    public static i18n Commands_SizeInteger;
    @i18nField(defaultValue = "领地名称")
    public static i18n Commands_DominionName;
    @i18nField(defaultValue = "子领地名称")
    public static i18n Commands_SubDominionName;
    @i18nField(defaultValue = "进入提示语内容")
    public static i18n Commands_EnterMessageContent;
    @i18nField(defaultValue = "离开提示语内容")
    public static i18n Commands_LeaveMessageContent;
    @i18nField(defaultValue = "输入颜色(16进制)")
    public static i18n Commands_InputColor;
    @i18nField(defaultValue = "新领地名称")
    public static i18n Commands_NewDominionName;

    @i18nField(defaultValue = "Residence 迁移功能没有开启")
    public static i18n Commands_Residence_MigrationDisabled;
    @i18nField(defaultValue = "用法: /dominion migrate <res领地名称>")
    public static i18n Commands_Residence_MigrateUsage;
    @i18nField(defaultValue = "你没有可迁移的数据")
    public static i18n Commands_Residence_NoMigrationData;
    @i18nField(defaultValue = "未找到指定的 Residence 领地")
    public static i18n Commands_Residence_NoResidenceDominion;
    @i18nField(defaultValue = "你不是该领地的所有者，无法迁移此领地")
    public static i18n Commands_Residence_ResidenceNotOwner;
    @i18nField(defaultValue = "迁移失败: %s")
    public static i18n Commands_Residence_MigrateFailed;
    @i18nField(defaultValue = "领地 %s 已从 Residence 迁移至 Dominion")
    public static i18n Commands_Residence_MigrateSuccess;

    @i18nField(defaultValue = "正在从数据库重新加载领地缓存...")
    public static i18n Commands_Operator_ReloadingDominionCache;
    @i18nField(defaultValue = "领地缓存已重新加载")
    public static i18n Commands_Operator_ReloadedDominionCache;
    @i18nField(defaultValue = "正在从数据库重新加载玩家权限缓存...")
    public static i18n Commands_Operator_ReloadingPrivilegeCache;
    @i18nField(defaultValue = "玩家权限缓存已重新加载")
    public static i18n Commands_Operator_ReloadedPrivilegeCache;
    @i18nField(defaultValue = "正在从数据库重新加载权限组缓存...")
    public static i18n Commands_Operator_ReloadingGroupCache;
    @i18nField(defaultValue = "权限组缓存已重新加载")
    public static i18n Commands_Operator_ReloadedGroupCache;
    @i18nField(defaultValue = "正在导出拥有领地的MCA文件列表...")
    public static i18n Commands_Operator_ExportingMCAList;
    @i18nField(defaultValue = "创建导出文件夹失败")
    public static i18n Commands_Operator_CreateExportFolderFailed;
    @i18nField(defaultValue = "正在导出 %s 的MCA文件列表...")
    public static i18n Commands_Operator_ExportingMCAListForWorld;
    @i18nField(defaultValue = "删除 %s 的MCA文件列表失败")
    public static i18n Commands_Operator_DeleteMCAListFailed;
    @i18nField(defaultValue = "创建 %s 的MCA文件列表失败")
    public static i18n Commands_Operator_CreateMCAListFailed;
    @i18nField(defaultValue = "写入 %s 失败")
    public static i18n Commands_Operator_WriteMCAListFailed;
    @i18nField(defaultValue = "导出 %s 的MCA文件列表失败")
    public static i18n Commands_Operator_ExportMCAListFailed;
    @i18nField(defaultValue = "MCA文件列表已导出到 %s")
    public static i18n Commands_Operator_ExportedMCAList;
    @i18nField(defaultValue = "正在重新加载配置文件...")
    public static i18n Commands_Operator_ReloadingConfig;
    @i18nField(defaultValue = "配置文件已重新加载")
    public static i18n Commands_Operator_ReloadedConfig;

    @i18nField(defaultValue = "最高Y坐标限制不能小于最低Y坐标限制")
    public static i18n Commands_SetConfig_MinYShouldBeLessThanMaxY;
    @i18nField(defaultValue = "最低Y坐标限制不能大于最高Y坐标限制")
    public static i18n Commands_SetConfig_MaxYShouldBeGreaterThanMinY;
    @i18nField(defaultValue = "X轴(东西)最大尺寸不能小于4")
    public static i18n Commands_SetConfig_SizeXShouldBeGreaterThan4;
    @i18nField(defaultValue = "Z轴(南北)最大尺寸不能小于4")
    public static i18n Commands_SetConfig_SizeZShouldBeGreaterThan4;
    @i18nField(defaultValue = "Y轴(垂直)最大尺寸不能小于4")
    public static i18n Commands_SetConfig_SizeYShouldBeGreaterThan4;
    @i18nField(defaultValue = "每个玩家领地数量限制不能小于0")
    public static i18n Commands_SetConfig_AmountShouldBeGreaterThan0;
    @i18nField(defaultValue = "领地深度限制不能小于0")
    public static i18n Commands_SetConfig_DepthShouldBeGreaterThan0;
    @i18nField(defaultValue = "传送延迟不能小于0")
    public static i18n Commands_SetConfig_TpDelayShouldBeGreaterThan0;
    @i18nField(defaultValue = "传送冷却时间不能小于0")
    public static i18n Commands_SetConfig_TpCoolDownShouldBeGreaterThan0;
    @i18nField(defaultValue = "每方块单价不能小于0")
    public static i18n Commands_SetConfig_PriceShouldBeGreaterThan0;
    @i18nField(defaultValue = "领地退款比例不能小于0")
    public static i18n Commands_SetConfig_RefundShouldBeGreaterThan0;
    @i18nField(defaultValue = "出生点保护半径不能小于或等于0")
    public static i18n Commands_SetConfig_SpawnProtectRadiusShouldBeGreaterThan0;

    @i18nField(defaultValue = "用法: /dominion template create <模板名称>")
    public static i18n Commands_Template_CreateTemplateUsage;
    @i18nField(defaultValue = "用法: /dominion template delete <模板名称>")
    public static i18n Commands_Template_DeleteTemplateUsage;
    @i18nField(defaultValue = "用法: /dominion template set_flag <模板名称> <权限名称> <true/false>")
    public static i18n Commands_Template_SetTemplateFlagUsage;
    @i18nField(defaultValue = "用法: /dominion template <list|setting|delete|create|set_flag>")
    public static i18n Commands_Template_TemplateUsage;
    @i18nField(defaultValue = "新模板名称")
    public static i18n Commands_Template_NewTemplateName;

    @i18nField(defaultValue = "用法: /dominion use_title <权限组ID>")
    public static i18n Commands_Title_UseTitleUsage;
    @i18nField(defaultValue = "成功卸下权限组称号")
    public static i18n Commands_Title_RemoveTitleSuccess;
    @i18nField(defaultValue = "权限组不存在")
    public static i18n Commands_Title_GroupNotExist;
    @i18nField(defaultValue = "权限组 %s 所属领地不存在")
    public static i18n Commands_Title_GroupDominionNotExist;
    @i18nField(defaultValue = "你不是领地 %s 的成员，无法使用其称号")
    public static i18n Commands_Title_NotDominionMember;
    @i18nField(defaultValue = "你不属于权限组 %s，无法使用其称号")
    public static i18n Commands_Title_NotGroupMember;
    @i18nField(defaultValue = "成功使用权限组 %s 称号")
    public static i18n Commands_Title_UseTitleSuccess;
    @i18nField(defaultValue = "使用称号失败：%s")
    public static i18n Commands_Title_UseTitleFailed;

    @i18nField(defaultValue = "你是OP，将忽略领地传送限制")
    public static i18n Messages_OpBypassTpLimit;
    @i18nField(defaultValue = "领地所在世界不存在")
    public static i18n Messages_WorldNotExist;
    @i18nField(defaultValue = "领地 %s 没有设置传送点，将尝试传送到中心点")
    public static i18n Messages_NoTpLocation;
    @i18nField(defaultValue = "领地 %s 传送点不在领地内，将尝试传送到中心点")
    public static i18n Messages_TpLocationNotInside;
    @i18nField(defaultValue = "已将你传送到 %s")
    public static i18n Messages_TpToDominion;
    @i18nField(defaultValue = "管理员没有开启领地传送功能")
    public static i18n Messages_TpDisabled;
    @i18nField(defaultValue = "此领地禁止传送")
    public static i18n Messages_DominionNoTp;
    @i18nField(defaultValue = "你所在的权限组组不被允许传送到这个领地")
    public static i18n Messages_GroupNoTp;
    @i18nField(defaultValue = "你不被允许传送到这个领地")
    public static i18n Messages_PrivilegeNoTp;
    @i18nField(defaultValue = "请等待 %d 秒后再传送")
    public static i18n Messages_TpCoolDown;
    @i18nField(defaultValue = "传送将在 %d 秒后执行")
    public static i18n Messages_TpDelay;
    @i18nField(defaultValue = "传送倒计时 %d 秒")
    public static i18n Messages_TpCountDown;
    @i18nField(defaultValue = "传送失败，请重试")
    public static i18n Messages_TpFailed;

    @i18nField(defaultValue = "创建领地失败")
    public static i18n Messages_CreateDominionFailed;
    @i18nField(defaultValue = "成功创建领地 %s")
    public static i18n Messages_CreateDominionSuccess;
    @i18nField(defaultValue = "领地名称不能为空")
    public static i18n Messages_DominionNameShouldNotEmpty;
    @i18nField(defaultValue = "领地名称不能包含空格或点")
    public static i18n Messages_DominionNameInvalid;
    @i18nField(defaultValue = "已经存在名称为 %s 的领地")
    public static i18n Messages_DominionNameExist;
    @i18nField(defaultValue = "两个选点世界不一致")
    public static i18n Messages_SelectPointsWorldNotSame;
    @i18nField(defaultValue = "禁止在世界 %s 创建领地")
    public static i18n Messages_CreateDominionDisabledWorld;
    @i18nField(defaultValue = "你的领地数量已达上限(%d个)")
    public static i18n Messages_DominionAmountLimit;
    @i18nField(defaultValue = "父领地 %s 不存在")
    public static i18n Messages_ParentDominionNotExist;
    @i18nField(defaultValue = "根领地丢失！")
    public static i18n Messages_RootDominionLost;
    @i18nField(defaultValue = "你不是父领地 %s 的拥有者，无法创建子领地")
    public static i18n Messages_NotParentDominionOwner;
    @i18nField(defaultValue = "父领地与子领地不在同一世界")
    public static i18n Messages_ParentDominionNotInSameWorld;
    @i18nField(defaultValue = "超出父领地 %s 范围")
    public static i18n Messages_OutOfParentDominionRange;
    @i18nField(defaultValue = "与出生点保护冲突")
    public static i18n Messages_ConflictWithSpawnProtect;
    @i18nField(defaultValue = "与领地 %s 冲突")
    public static i18n Messages_ConflictWithDominion;
    @i18nField(defaultValue = "数据库错误，请联系管理员")
    public static i18n Messages_DatabaseError;
    @i18nField(defaultValue = "无法获取你所处的领地，请指定名称")
    public static i18n Messages_CannotGetDominionAuto;
    @i18nField(defaultValue = "扩展领地失败")
    public static i18n Messages_ExpandDominionFailed;
    @i18nField(defaultValue = "领地所在世界丢失")
    public static i18n Messages_DominionWorldLost;
    @i18nField(defaultValue = "父领地丢失")
    public static i18n Messages_ParentDominionLost;
    @i18nField(defaultValue = "成功扩展领地 %s %d格")
    public static i18n Messages_ExpandDominionSuccess;
    @i18nField(defaultValue = "缩小领地失败")
    public static i18n Messages_ContractDominionFailed;
    @i18nField(defaultValue = "缩小后的领地无法包含子领地 %s")
    public static i18n Messages_ContractDominionConflict;
    @i18nField(defaultValue = "成功缩小领地 %s %d格")
    public static i18n Messages_ContractDominionSuccess;
    @i18nField(defaultValue = "删除领地失败")
    public static i18n Messages_DeleteDominionFailed;
    @i18nField(defaultValue = "领地 %s 及其所有子领地已删除")
    public static i18n Messages_DeleteDominionSuccess;
    @i18nField(defaultValue = "删除领地 %s 会同时删除其所有子领地，是否继续？")
    public static i18n Messages_DeleteDominionConfirm;
    @i18nField(defaultValue = "输入 /dominion delete %s force 确认删除")
    public static i18n Messages_DeleteDominionForceConfirm;
    @i18nField(defaultValue = "成功设置领地 %s 的进入消息")
    public static i18n Messages_SetEnterMessageSuccess;
    @i18nField(defaultValue = "成功设置领地 %s 的离开消息")
    public static i18n Messages_SetLeaveMessageSuccess;
    @i18nField(defaultValue = "设置领地传送点失败")
    public static i18n Messages_SetTpLocationFailed;
    @i18nField(defaultValue = "领地 %s 不存在")
    public static i18n Messages_DominionNotExist;
    @i18nField(defaultValue = "领地所在世界不存在")
    public static i18n Messages_DominionWorldNotExist;
    @i18nField(defaultValue = "成功设置领地 %s 的传送点 %d %d %d")
    public static i18n Messages_SetTpLocationSuccess;
    @i18nField(defaultValue = "传送点不在领地 %s 内")
    public static i18n Messages_TpLocationNotInDominion;
    @i18nField(defaultValue = "重命名领地失败")
    public static i18n Messages_RenameDominionFailed;
    @i18nField(defaultValue = "新名称与旧名称相同")
    public static i18n Messages_RenameDominionSameName;
    @i18nField(defaultValue = "成功将领地 %s 重命名为 %s")
    public static i18n Messages_RenameDominionSuccess;
    @i18nField(defaultValue = "转让领地失败")
    public static i18n Messages_GiveDominionFailed;
    @i18nField(defaultValue = "玩家 %s 不存在或没有登录过")
    public static i18n Messages_PlayerNotExist;
    @i18nField(defaultValue = "领地 %s 已经属于 %s，无需转移")
    public static i18n Messages_DominionAlreadyBelong;
    @i18nField(defaultValue = "子领地无法转让，你可以通过将 %s 设置为管理员来让其管理领地 %s")
    public static i18n Messages_SubDominionCannotGive;
    @i18nField(defaultValue = "转让领地 %s 给 %s 会同时转让其所有子领地，是否继续？")
    public static i18n Messages_GiveDominionConfirm;
    @i18nField(defaultValue = "输入 /dominion give %s %s force 确认转让")
    public static i18n Messages_GiveDominionForceConfirm;
    @i18nField(defaultValue = "成功将领地 %s 及其所有子领地转让给 %s")
    public static i18n Messages_GiveDominionSuccess;
    @i18nField(defaultValue = "设置领地地图颜色失败")
    public static i18n Messages_SetMapColorFailed;
    @i18nField(defaultValue = "颜色格式不正确")
    public static i18n Messages_MapColorInvalid;
    @i18nField(defaultValue = "成功设置领地 %s 的卫星地图颜色为 %s")
    public static i18n Messages_SetMapColorSuccess;
    @i18nField(defaultValue = "尺寸不合法")
    public static i18n Messages_SizeInvalid;
    @i18nField(defaultValue = "领地的任意一边长度不得小于4")
    public static i18n Messages_SizeShouldBeGreaterThan4;
    @i18nField(defaultValue = "领地X方向(东西)长度不能超过 %d")
    public static i18n Messages_SizeXShouldBeLessThan;
    @i18nField(defaultValue = "领地Y方向(上下)高度不能超过 %d")
    public static i18n Messages_SizeYShouldBeLessThan;
    @i18nField(defaultValue = "领地Z方向(南北)长度不能超过 %d")
    public static i18n Messages_SizeZShouldBeLessThan;
    @i18nField(defaultValue = "领地Y坐标上限不能超过 %d")
    public static i18n Messages_MaxYShouldBeLessThan;
    @i18nField(defaultValue = "领地Y坐标下限不能超过 %d")
    public static i18n Messages_MinYShouldBeLessThan;
    @i18nField(defaultValue = "子领地深度不合法")
    public static i18n Messages_DepthInvalid;
    @i18nField(defaultValue = "不允许创建子领地")
    public static i18n Messages_CreateSubDominionDisabled;
    @i18nField(defaultValue = "子领地嵌套深度不能超过 %d")
    public static i18n Messages_DepthShouldBeLessThan;
    @i18nField(defaultValue = "你不是领地 %s 的拥有者")
    public static i18n Messages_NotDominionOwner;
    @i18nField(defaultValue = "没有可用的经济插件系统，请联系服主")
    public static i18n Messages_NoEconomyPlugin;
    @i18nField(defaultValue = "你是OP，已跳过经济检查")
    public static i18n Messages_OpBypassEconomyCheck;
    @i18nField(defaultValue = "你的余额不足，需要 %.2f %s")
    public static i18n Messages_NotEnoughMoney;
    @i18nField(defaultValue = "已扣除 %.2f %s")
    public static i18n Messages_ChargeMoney;
    @i18nField(defaultValue = "已退还 %.2f %s")
    public static i18n Messages_RefundMoney;
    @i18nField(defaultValue = "无法获取你的位置")
    public static i18n Messages_CannotGetLocation;
    @i18nField(defaultValue = "禁止跨世界操作")
    public static i18n Messages_CrossWorldOperationDisallowed;
    @i18nField(defaultValue = "你不在领地 %s 内，无法执行此操作")
    public static i18n Messages_NotInDominion;
    @i18nField(defaultValue = "无法获取你的方向")
    public static i18n Messages_CannotGetDirection;
    @i18nField(defaultValue = "无效的方向 %s")
    public static i18n Messages_InvalidDirection;
    @i18nField(defaultValue = "缩小后的领地大小无效")
    public static i18n Messages_ContractSizeInvalid;
    @i18nField(defaultValue = "(子领地：%s)")
    public static i18n Messages_SubDominionList;

    @i18nField(defaultValue = "成功设置领地权限 %s 为 %s")
    public static i18n Messages_SetDominionFlagSuccess;
    @i18nField(defaultValue = "未知的权限 %s")
    public static i18n Messages_UnknownFlag;

    @i18nField(defaultValue = "创建权限组 %s 失败")
    public static i18n Messages_CreateGroupFailed;
    @i18nField(defaultValue = "创建权限组 %s 成功")
    public static i18n Messages_CreateGroupSuccess;
    @i18nField(defaultValue = "权限组名称不能包含空格")
    public static i18n Messages_GroupNameInvalid;
    @i18nField(defaultValue = "领地 %s 已存在名为 %s 的权限组")
    public static i18n Messages_GroupNameExist;
    @i18nField(defaultValue = "删除权限组 %s 失败")
    public static i18n Messages_DeleteGroupFailed;
    @i18nField(defaultValue = "删除权限组 %s 成功")
    public static i18n Messages_DeleteGroupSuccess;
    @i18nField(defaultValue = "领地 %s 不存在名为 %s 的权限组")
    public static i18n Messages_GroupNotExist;
    @i18nField(defaultValue = "设置权限组 %s 的权限 %s 为 %s 失败")
    public static i18n Messages_SetGroupFlagFailed;
    @i18nField(defaultValue = "设置权限组 %s 的权限 %s 为 %s 成功")
    public static i18n Messages_SetGroupFlagSuccess;
    @i18nField(defaultValue = "你不是领地 %s 的拥有者，无法修改管理员权限组权限")
    public static i18n Messages_NotDominionOwnerForGroup;
    @i18nField(defaultValue = "重命名权限组 %s 为 %s 失败")
    public static i18n Messages_RenameGroupFailed;
    @i18nField(defaultValue = "重命名权限组 %s 为 %s 成功")
    public static i18n Messages_RenameGroupSuccess;
    @i18nField(defaultValue = "添加成员 %s 到权限组 %s 失败")
    public static i18n Messages_AddGroupMemberFailed;
    @i18nField(defaultValue = "添加成员 %s 到权限组 %s 成功")
    public static i18n Messages_AddGroupMemberSuccess;
    @i18nField(defaultValue = "你没有权限修改领地 %s 的权限组 %s 成员")
    public static i18n Messages_NoPermissionForGroupMember;
    @i18nField(defaultValue = "你不是领地 %s 的拥有者，无法添加成员到管理员权限组")
    public static i18n Messages_NotDominionOwnerForGroupMember;
    @i18nField(defaultValue = "玩家 %s 不是领地 %s 的成员，无法直接加入权限组")
    public static i18n Messages_PlayerNotDominionMember;
    @i18nField(defaultValue = "玩家 %s 已在权限组 %s 中")
    public static i18n Messages_PlayerAlreadyInGroup;
    @i18nField(defaultValue = "%s 是管理员，你不是领地 %s 的拥有者，无法添加管理员到权限组")
    public static i18n Messages_PlayerIsOwnerForGroupMember;
    @i18nField(defaultValue = "从权限组 %s 移除成员 %s 失败")
    public static i18n Messages_RemoveGroupMemberFailed;
    @i18nField(defaultValue = "从权限组 %s 移除成员 %s 成功")
    public static i18n Messages_RemoveGroupMemberSuccess;
    @i18nField(defaultValue = "你没有权限移除领地 %s 的权限组 %s 成员")
    public static i18n Messages_NoPermissionForRemoveGroupMember;
    @i18nField(defaultValue = "你不是领地 %s 的拥有者，无法从管理员权限组移除成员")
    public static i18n Messages_NotDominionOwnerForRemoveGroupMember;
    @i18nField(defaultValue = "玩家 %s 不是领地 %s 的成员")
    public static i18n Messages_PlayerNotMember;
    @i18nField(defaultValue = "玩家 %s 不在权限组 %s 中")
    public static i18n Messages_PlayerNotInGroup;

    @i18nField(defaultValue = "将玩家 %s 从领地 %s 移除失败")
    public static i18n Messages_RemoveMemberFailed;
    @i18nField(defaultValue = "将玩家 %s 从领地 %s 移除成功")
    public static i18n Messages_RemoveMemberSuccess;
    @i18nField(defaultValue = "设置玩家 %s 在领地 %s 的权限 %s 为 %s 失败")
    public static i18n Messages_SetMemberFlagFailed;
    @i18nField(defaultValue = "设置玩家 %s 在领地 %s 的权限 %s 为 %s 成功")
    public static i18n Messages_SetMemberFlagSuccess;
    @i18nField(defaultValue = "玩家 %s 属于 %s 权限组，无法单独设置权限")
    public static i18n Messages_PlayerBelongToGroup;
    @i18nField(defaultValue = "将玩家 %s 添加到领地成员 %s 失败")
    public static i18n Messages_AddMemberFailed;
    @i18nField(defaultValue = "将玩家 %s 添加到领地成员 %s 成功")
    public static i18n Messages_AddMemberSuccess;
    @i18nField(defaultValue = "玩家 %s 已经是领地 %s 的成员")
    public static i18n Messages_PlayerAlreadyMember;
    @i18nField(defaultValue = "应用模板 %s 到玩家 %s 在领地 %s 的权限成功")
    public static i18n Messages_ApplyTemplateSuccess;
    @i18nField(defaultValue = "应用模板 %s 到玩家 %s 在领地 %s 的权限失败")
    public static i18n Messages_ApplyTemplateFailed;
    @i18nField(defaultValue = "模板 %s 不存在")
    public static i18n Messages_TemplateNotExist;
    @i18nField(defaultValue = "你不是领地 %s 的拥有者，无法移除一个领地管理员")
    public static i18n Messages_NotDominionOwnerForRemoveAdmin;
    @i18nField(defaultValue = "你不是领地 %s 的拥有者，无法修改其他玩家管理员的权限")
    public static i18n Messages_NotDominionOwnerForSetAdmin;
    @i18nField(defaultValue = "玩家 %s 是领地 %s 的拥有者，不可以被添加为成员")
    public static i18n Messages_OwnerCannotBeMember;

    @i18nField(defaultValue = "创建模板 %s 成功")
    public static i18n Messages_CreateTemplateSuccess;
    @i18nField(defaultValue = "创建模板 %s 失败")
    public static i18n Messages_CreateTemplateFailed;
    @i18nField(defaultValue = "模板名称不能包含空格")
    public static i18n Messages_TemplateNameInvalid;
    @i18nField(defaultValue = "已经存在名为 %s 的权限模板")
    public static i18n Messages_TemplateNameExist;
    @i18nField(defaultValue = "删除模板 %s 成功")
    public static i18n Messages_DeleteTemplateSuccess;
    @i18nField(defaultValue = "删除模板 %s 失败")
    public static i18n Messages_DeleteTemplateFailed;
    @i18nField(defaultValue = "设置模板 %s 的权限 %s 为 %s 成功")
    public static i18n Messages_SetTemplateFlagSuccess;
    @i18nField(defaultValue = "设置模板 %s 的权限 %s 为 %s 失败")
    public static i18n Messages_SetTemplateFlagFailed;

    @i18nField(defaultValue = "该命令只能由玩家执行")
    public static i18n Messages_CommandPlayerOnly;
    @i18nField(defaultValue = "你没有 %s 权限执行此命令")
    public static i18n Messages_NoPermission;

    @i18nField(defaultValue = "你不是领地 %s 的拥有者或管理员，无权修改权限")
    public static i18n Messages_NotDominionOwnerOrAdmin;
    @i18nField(defaultValue = "你当前在子领地内，请指定要操作的领地名称")
    public static i18n Messages_InSubDominion;
    @i18nField(defaultValue = "你没有 %s (%s) 权限")
    public static i18n Messages_NoPermissionForFlag;

    @i18nField(defaultValue = "无法连接 BlueMap 插件，如果你不打算使用卫星地图渲染建议前往配置文件关闭此功能以避免下方的报错")
    public static i18n Messages_BlueMapConnectFailed;
    @i18nField(defaultValue = "Dynmap 成功注册")
    public static i18n Messages_DynmapRegisterSuccess;
    @i18nField(defaultValue = "无法连接到 Dynmap，如果你不打算使用卫星地图渲染建议前往配置文件关闭此功能")
    public static i18n Messages_DynmapConnectFailed;
    @i18nField(defaultValue = "未找到 PlaceholderAPI 插件，无法使用权限组称号功能，已自动关闭")
    public static i18n Messages_PlaceholderAPINotFound;
    @i18nField(defaultValue = "领地插件已启动")
    public static i18n Messages_PluginEnabled;
    @i18nField(defaultValue = "版本：%s")
    public static i18n Messages_PluginVersion;
    @i18nField(defaultValue = "成功注册 PlaceholderAPI 扩展")
    public static i18n Messages_PlaceholderAPIRegisterSuccess;
    @i18nField(defaultValue = "共加载了 %d 个领地组")
    public static i18n Messages_LoadedGroupAmount;

    @i18nField(defaultValue = "开始自动清理长时间未登录玩家领地数据")
    public static i18n Messages_AutoCleanStart;
    @i18nField(defaultValue = "已清理玩家 %s 的领地数据")
    public static i18n Messages_AutoCleanPlayer;
    @i18nField(defaultValue = "自动清理完成")
    public static i18n Messages_AutoCleanEnd;

    @i18nField(defaultValue = "你不是领地 %s 的拥有者或管理员，无权访问此页面")
    public static i18n TUI_NotDominionOwnerOrAdminForPage;
    @i18nField(defaultValue = "领地插件命令帮助")
    public static i18n TUI_CommandHelp_Title;
    @i18nField(defaultValue = "<>表示必填参数 []表示可选参数")
    public static i18n TUI_CommandHelp_SubTitle;

    @i18nField(defaultValue = "输入要创建的领地名称")
    public static i18n CUI_Input_CreateDominion;
    @i18nField(defaultValue = "输入要创建的权限组名称")
    public static i18n CUI_Input_CreateGroup;
    @i18nField(defaultValue = "输入要创建的模板名称")
    public static i18n CUI_Input_CreateTemplate;
    @i18nField(defaultValue = "编辑进入领地提示语内容")
    public static i18n CUI_Input_EditEnterMessage;
    @i18nField(defaultValue = "编辑离开领地提示语内容")
    public static i18n CUI_Input_EditLeaveMessage;
    @i18nField(defaultValue = "输入玩家名称以添加为成员")
    public static i18n CUI_Input_AddMember;
    @i18nField(defaultValue = "领地重命名")
    public static i18n CUI_Input_RenameDominion;
    @i18nField(defaultValue = "权限组重命名")
    public static i18n CUI_Input_RenameGroup;
    @i18nField(defaultValue = "输入卫星地图地块颜色（16进制）")
    public static i18n CUI_Input_SetMapColor;

    @i18nField(defaultValue = "AutoCreateRadius 不能等于 0，已重置为 10")
    public static i18n Config_Check_AutoCreateRadiusError;
    @i18nField(defaultValue = "AutoCleanAfterDays 不能等于 0，已重置为 180")
    public static i18n Config_Check_AutoCleanAfterDaysError;
    @i18nField(defaultValue = "工具名称设置错误，已重置为 ARROW")
    public static i18n Config_Check_ToolNameError;
    @i18nField(defaultValue = "Limit.SizeX 尺寸不能小于 4，已重置为 128")
    public static i18n Config_Check_LimitSizeXError;
    @i18nField(defaultValue = "Limit.SizeY 尺寸不能小于 4，已重置为 64")
    public static i18n Config_Check_LimitSizeYError;
    @i18nField(defaultValue = "Limit.SizeZ 尺寸不能小于 4，已重置为 128")
    public static i18n Config_Check_LimitSizeZError;
    @i18nField(defaultValue = "Limit.MinY 不能大于或等于 Limit.MaxY，已重置为 -64 320")
    public static i18n Config_Check_LimitMinYError;
    @i18nField(defaultValue = "Economy.Refund 设置不合法，已重置为 0.85")
    public static i18n Config_Check_RefundError;
    @i18nField(defaultValue = "Economy.Price 设置不合法，已重置为 10.0")
    public static i18n Config_Check_PriceError;
    @i18nField(defaultValue = "启用 Limit.Vert 时 Limit.SizeY 不能小于 Limit.MaxY - Limit.MinY，已自动调整为 %d")
    public static i18n Config_Check_LimitSizeYAutoAdjust;
    @i18nField(defaultValue = "Limit.Amount 设置不合法，已重置为 10")
    public static i18n Config_Check_AmountError;
    @i18nField(defaultValue = "Limit.Depth 设置不合法，已重置为 3")
    public static i18n Config_Check_DepthError;
    @i18nField(defaultValue = "权限组 %s 的 MinY 不能大于等于 MaxY，已重置为 -64 和 320")
    public static i18n Config_Check_GroupMinYError;
    @i18nField(defaultValue = "权限组 %s 的 SizeX 设置过小，已重置为 128")
    public static i18n Config_Check_GroupSizeXError;
    @i18nField(defaultValue = "权限组 %s 的 SizeY 设置过小，已重置为 64")
    public static i18n Config_Check_GroupSizeYError;
    @i18nField(defaultValue = "权限组 %s 的 SizeZ 设置过小，已重置为 128")
    public static i18n Config_Check_GroupSizeZError;
    @i18nField(defaultValue = "权限组 %s 的 Amount 设置不合法，已重置为 10")
    public static i18n Config_Check_GroupAmountError;
    @i18nField(defaultValue = "权限组 %s 的 Depth 设置不合法，已重置为 3")
    public static i18n Config_Check_GroupDepthError;
    @i18nField(defaultValue = "权限组 %s 的 Price 设置不合法，已重置为 10.0")
    public static i18n Config_Check_GroupPriceError;
    @i18nField(defaultValue = "权限组 %s 的 Refund 设置不合法，已重置为 0.85")
    public static i18n Config_Check_GroupRefundError;
    @i18nField(defaultValue = "读取权限配置失败：%s")
    public static i18n Config_Check_LoadFlagError;

    @i18nField(defaultValue = "语言设置，参考 languages 文件夹下的文件名")
    public static i18n Config_Comment_Language;
    @i18nField(defaultValue = "自动创建领地的半径，单位为方块")
    public static i18n Config_Comment_AutoCreateRadius;
    @i18nField(defaultValue = "-1表示不开启")
    public static i18n Config_Comment_NegativeOneDisabled;
    @i18nField(defaultValue = "默认玩家圈地限制")
    public static i18n Config_Comment_DefaultLimit;
    @i18nField(defaultValue = "出生点保护半径 出生点此范围内不允许圈地")
    public static i18n Config_Comment_SpawnProtectRadius;
    @i18nField(defaultValue = "最小Y坐标")
    public static i18n Config_Comment_MinY;
    @i18nField(defaultValue = "最大Y坐标")
    public static i18n Config_Comment_MaxY;
    @i18nField(defaultValue = "-1表示不限制")
    public static i18n Config_Comment_NegativeOneUnlimited;
    @i18nField(defaultValue = "X方向最大长度")
    public static i18n Config_Comment_SizeX;
    @i18nField(defaultValue = "Y方向最大长度")
    public static i18n Config_Comment_SizeY;
    @i18nField(defaultValue = "Z方向最大长度")
    public static i18n Config_Comment_SizeZ;
    @i18nField(defaultValue = "最大领地数量")
    public static i18n Config_Comment_Amount;
    @i18nField(defaultValue = "子领地深度")
    public static i18n Config_Comment_Depth;
    @i18nField(defaultValue = "0表示不开启")
    public static i18n Config_Comment_ZeroDisabled;
    @i18nField(defaultValue = "是否自动延伸到 MaxY 和 MinY")
    public static i18n Config_Comment_Vert;
    @i18nField(defaultValue = "不允许圈地的世界列表")
    public static i18n Config_Comment_DisabledWorlds;
    @i18nField(defaultValue = "是否允许OP无视领地限制")
    public static i18n Config_Comment_OpBypass;
    @i18nField(defaultValue = "传送延迟 秒")
    public static i18n Config_Comment_TpDelay;
    @i18nField(defaultValue = "传送冷却 秒")
    public static i18n Config_Comment_TpCoolDown;
    @i18nField(defaultValue = "自动清理长时间未上线玩家的领地（天）")
    public static i18n Config_Comment_AutoCleanAfterDays;
    @i18nField(defaultValue = "圈地工具名称")
    public static i18n Config_Comment_ToolName;
    @i18nField(defaultValue = "经济设置")
    public static i18n Config_Comment_Economy;
    @i18nField(defaultValue = "需要安装 Vault 前置及插件")
    public static i18n Config_Comment_VaultRequired;
    @i18nField(defaultValue = "圈地价格 单位每方块")
    public static i18n Config_Comment_Price;
    @i18nField(defaultValue = "是否只计算xz平面积")
    public static i18n Config_Comment_OnlyXZ;
    @i18nField(defaultValue = "删除或缩小领地时的退款比例")
    public static i18n Config_Comment_Refund;
    @i18nField(defaultValue = "飞行权限节点 - 拥有以下任意一个权限节点的玩家不会被本插件拦截飞行")
    public static i18n Config_Comment_FlyPermission;
    @i18nField(defaultValue = "是否允许玩家从 Residence 迁移领地数据")
    public static i18n Config_Comment_ResidenceMigration;
    @i18nField(defaultValue = "权限组称号 - 使用权限组当作称号(需要PlaceholderAPI插件)")
    public static i18n Config_Comment_GroupTitle;
    @i18nField(defaultValue = "变量: %dominion_group_title%")
    public static i18n Config_Comment_GroupTitleVariable;
    @i18nField(defaultValue = "前后缀如需要加颜色请使用这种格式 &#ffffff")
    public static i18n Config_Comment_GroupTitleColor;
    @i18nField(defaultValue = "性能测试计时器")
    public static i18n Config_Comment_PerformanceTimer;


    public Translation(JavaPlugin plugin) {
        super(plugin);
    }

    public void loadOrSetFlagTranslation(Flag flag) {
        String displayNameTranslation = loadOrSet(flag.getDisplayNameKey(), flag.getDisplayName());
        String descriptionTranslation = loadOrSet(flag.getDescriptionKey(), flag.getDescription());
        flag.setDisplayName(displayNameTranslation);
        flag.setDescription(descriptionTranslation);
    }

    public void saveFlagTranslation(Flag flag) {
        set(flag.getDisplayNameKey(), flag.getDisplayName());
        set(flag.getDescriptionKey(), flag.getDescription());
    }

    public void loadFlagTranslation(Flag flag) {
        String displayNameTranslation = load(flag.getDisplayNameKey());
        if (displayNameTranslation != null) {
            flag.setDisplayName(displayNameTranslation);
        }
        String descriptionTranslation = load(flag.getDescriptionKey());
        if (descriptionTranslation != null) {
            flag.setDescription(descriptionTranslation);
        }
    }
}
