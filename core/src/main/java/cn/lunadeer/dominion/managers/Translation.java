package cn.lunadeer.dominion.managers;


import cn.lunadeer.dominion.api.dtos.flag.Flag;
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
    @i18nField(defaultValue = "本插件使用 Dominion 而非 Residence 作为领地，详细请查询 /dominion help")
    public static i18n Commands_Residence_Command;

    @i18nField(defaultValue = "用法: /dominion group create <领地名称> <权限组名称>")
    public static i18n Commands_Group_CreateGroupUsage;
    @i18nField(defaultValue = "用法: /dominion group delete <领地名称> <权限组名称>")
    public static i18n Commands_Group_DeleteGroupUsage;
    @i18nField(defaultValue = "用法: /dominion group rename <领地名称> <权限组旧名称> <新名称>")
    public static i18n Commands_Group_RenameGroupUsage;
    @i18nField(defaultValue = "用法: /dominion group set_flag <领地名称> <权限组名称> <权限名称> <true/false>")
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
    @i18nField(defaultValue = "你正在尝试导出数据库表，此行为会踢出所有玩家并关闭服务器，如确认继续请输入 /dominion export_db confirm")
    public static i18n Commands_Operator_ExportDBConfirm;
    @i18nField(defaultValue = "正在导出数据库表...")
    public static i18n Commands_Operator_ExportDBBegin;
    @i18nField(defaultValue = "数据库表导出完成")
    public static i18n Commands_Operator_ExportDBSuccess;
    @i18nField(defaultValue = "你正在尝试导入数据库表，此行为会踢出所有玩家并关闭服务器，如确认继续请输入 /dominion import_db confirm")
    public static i18n Commands_Operator_ImportDBConfirm;
    @i18nField(defaultValue = "正在导入数据库表...")
    public static i18n Commands_Operator_ImportDBBegin;
    @i18nField(defaultValue = "数据库表导入完成")
    public static i18n Commands_Operator_ImportDBSuccess;
    @i18nField(defaultValue = "没有可导入的数据")
    public static i18n Commands_Operator_ImportDBFail;
    @i18nField(defaultValue = "导入失败，数据不完整，请重新导出文件")
    public static i18n Commands_Operator_ImportDBIncompleteFail;

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
    @i18nField(defaultValue = "领地数量已达上限(%d个)")
    public static i18n Messages_DominionAmountLimit;
    @i18nField(defaultValue = "父领地 %s 不存在")
    public static i18n Messages_ParentDominionNotExist;
    @i18nField(defaultValue = "根领地丢失！")
    public static i18n Messages_RootDominionLost;
    @i18nField(defaultValue = "你不是父领地 %s 的拥有者")
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
    @i18nField(defaultValue = "设置领地进入消息失败")
    public static i18n Messages_SetEnterMessageFailed;
    @i18nField(defaultValue = "成功设置领地 %s 的离开消息")
    public static i18n Messages_SetLeaveMessageSuccess;
    @i18nField(defaultValue = "设置领地离开消息失败")
    public static i18n Messages_SetLeaveMessageFailed;
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
    @i18nField(defaultValue = "玩家 %s 不在线")
    public static i18n Messages_PlayerNotOnline;
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
    @i18nField(defaultValue = "领地X方向(东西)长度不能超过 %d")
    public static i18n Messages_SizeXShouldBeLessThan;
    @i18nField(defaultValue = "领地Y方向(上下)高度不能超过 %d")
    public static i18n Messages_SizeYShouldBeLessThan;
    @i18nField(defaultValue = "领地Z方向(南北)长度不能超过 %d")
    public static i18n Messages_SizeZShouldBeLessThan;
    @i18nField(defaultValue = "领地X方向(东西)长度不能少于 %d")
    public static i18n Messages_SizeXShouldBeGreaterThan;
    @i18nField(defaultValue = "领地Y方向(上下)高度不能少于 %d")
    public static i18n Messages_SizeYShouldBeGreaterThan;
    @i18nField(defaultValue = "领地Z方向(南北)长度不能少于 %d")
    public static i18n Messages_SizeZShouldBeGreaterThan;
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
    @i18nField(defaultValue = "你没有权限访问此页面")
    public static i18n Messages_PageNoPermission;

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
    @i18nField(defaultValue = "<div>%s</div><div>所有人：%s</div>")
    public static i18n Messages_MapInfoDetail;

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

    @i18nField(defaultValue = "主菜单")
    public static i18n TUI_Navigation_Menu;
    @i18nField(defaultValue = "我的领地")
    public static i18n TUI_Navigation_DominionList;
    @i18nField(defaultValue = "管理界面")
    public static i18n TUI_Navigation_Manage;
    @i18nField(defaultValue = "环境设置")
    public static i18n TUI_Navigation_EnvSetting;
    @i18nField(defaultValue = "访客权限")
    public static i18n TUI_Navigation_GuestSetting;
    @i18nField(defaultValue = "成员列表")
    public static i18n TUI_Navigation_MemberList;
    @i18nField(defaultValue = "成员权限")
    public static i18n TUI_Navigation_MemberSetting;
    @i18nField(defaultValue = "权限组列表")
    public static i18n TUI_Navigation_GroupList;
    @i18nField(defaultValue = "权限组管理")
    public static i18n TUI_Navigation_GroupSetting;
    @i18nField(defaultValue = "所有领地")
    public static i18n TUI_Navigation_AllDominion;
    @i18nField(defaultValue = "模板列表")
    public static i18n TUI_Navigation_TemplateList;
    @i18nField(defaultValue = "模板管理")
    public static i18n TUI_Navigation_TemplateSetting;
    @i18nField(defaultValue = "Res数据列表")
    public static i18n TUI_Navigation_MigrateList;
    @i18nField(defaultValue = "权限组称号列表")
    public static i18n TUI_Navigation_TitleList;

    @i18nField(defaultValue = "管理")
    public static i18n TUI_ManageButton;
    @i18nField(defaultValue = "删除")
    public static i18n TUI_DeleteButton;
    @i18nField(defaultValue = "搜索")
    public static i18n TUI_SearchButton;
    @i18nField(defaultValue = "返回")
    public static i18n TUI_BackButton;
    @i18nField(defaultValue = "选择")
    public static i18n TUI_SelectButton;
    @i18nField(defaultValue = "编辑")
    public static i18n TUI_EditButton;

    @i18nField(defaultValue = "Dominion 领地系统")
    public static i18n TUI_Menu_Title;
    @i18nField(defaultValue = "创建领地")
    public static i18n TUI_Menu_CreateDominionButton;
    @i18nField(defaultValue = "以你为中心自动创建一个新的领地")
    public static i18n TUI_Menu_CreateDominionDescription;
    @i18nField(defaultValue = "我的领地")
    public static i18n TUI_Menu_MyDominionButton;
    @i18nField(defaultValue = "查看我的领地列表")
    public static i18n TUI_Menu_MyDominionDescription;
    @i18nField(defaultValue = "称号列表")
    public static i18n TUI_Menu_TitleListButton;
    @i18nField(defaultValue = "查看/使用权限组称号")
    public static i18n TUI_Menu_TitleListDescription;
    @i18nField(defaultValue = "模板列表")
    public static i18n TUI_Menu_TemplateListButton;
    @i18nField(defaultValue = "成员权限模板列表")
    public static i18n TUI_Menu_TemplateListDescription;
    @i18nField(defaultValue = "指令帮助")
    public static i18n TUI_Menu_CommandHelpButton;
    @i18nField(defaultValue = "查看指令列表")
    public static i18n TUI_Menu_CommandHelpDescription;
    @i18nField(defaultValue = "使用文档")
    public static i18n TUI_Menu_DocumentButton;
    @i18nField(defaultValue = "在浏览器中打开使用文档")
    public static i18n TUI_Menu_DocumentDescription;
    @i18nField(defaultValue = "迁移数据")
    public static i18n TUI_Menu_MigrateButton;
    @i18nField(defaultValue = "把你的领地从Residence迁移到Dominion")
    public static i18n TUI_Menu_MigrateDescription;
    @i18nField(defaultValue = "所有领地")
    public static i18n TUI_Menu_AllDominionButton;
    @i18nField(defaultValue = "查看所有领地")
    public static i18n TUI_Menu_AllDominionDescription;
    @i18nField(defaultValue = "重载缓存")
    public static i18n TUI_Menu_ReloadCacheButton;
    @i18nField(defaultValue = "手动刷新缓存可解决一些玩家操作无效问题，不建议频繁操作")
    public static i18n TUI_Menu_ReloadCacheDescription;
    @i18nField(defaultValue = "重载配置")
    public static i18n TUI_Menu_ReloadConfigButton;
    @i18nField(defaultValue = "重载配置文件")
    public static i18n TUI_Menu_ReloadConfigDescription;
    @i18nField(defaultValue = "--- 以下选项仅OP可见 ---")
    public static i18n TUI_Menu_OpOnlySection;

    @i18nField(defaultValue = "我的领地列表")
    public static i18n TUI_DominionList_Title;
    @i18nField(defaultValue = "--- 以下为你拥有管理员权限的领地 ---")
    public static i18n TUI_DominionList_AdminSection;

    @i18nField(defaultValue = "领地 %s 管理界面")
    public static i18n TUI_DominionManage_Title;
    @i18nField(defaultValue = "详细信息")
    public static i18n TUI_DominionManage_InfoButton;
    @i18nField(defaultValue = "查看领地详细信息")
    public static i18n TUI_DominionManage_InfoDescription;
    @i18nField(defaultValue = "环境设置")
    public static i18n TUI_DominionManage_EnvSettingButton;
    @i18nField(defaultValue = "设置领地内的一些环境行为")
    public static i18n TUI_DominionManage_EnvSettingDescription;
    @i18nField(defaultValue = "访客权限")
    public static i18n TUI_DominionManage_GuestSettingButton;
    @i18nField(defaultValue = "配置访客在此领地的权限")
    public static i18n TUI_DominionManage_GuestSettingDescription;
    @i18nField(defaultValue = "成员管理")
    public static i18n TUI_DominionManage_MemberListButton;
    @i18nField(defaultValue = "管理此领地成员的权限")
    public static i18n TUI_DominionManage_MemberListDescription;
    @i18nField(defaultValue = "权限组")
    public static i18n TUI_DominionManage_GroupListButton;
    @i18nField(defaultValue = "管理此领地的权限组")
    public static i18n TUI_DominionManage_GroupListDescription;
    @i18nField(defaultValue = "设置传送点")
    public static i18n TUI_DominionManage_SetTpLocationButton;
    @i18nField(defaultValue = "设置你当前位置为此领地传送点")
    public static i18n TUI_DominionManage_SetTpLocationDescription;
    @i18nField(defaultValue = "重命名")
    public static i18n TUI_DominionManage_RenameButton;
    @i18nField(defaultValue = "重命名领地")
    public static i18n TUI_DominionManage_RenameDescription;
    @i18nField(defaultValue = "编辑欢迎提示语")
    public static i18n TUI_DominionManage_EditJoinMessageButton;
    @i18nField(defaultValue = "当玩家进入领地时显示的消息")
    public static i18n TUI_DominionManage_EditJoinMessageDescription;
    @i18nField(defaultValue = "编辑离开提示语")
    public static i18n TUI_DominionManage_EditLeaveMessageButton;
    @i18nField(defaultValue = "当玩家离开领地时显示的消息")
    public static i18n TUI_DominionManage_EditLeaveMessageDescription;
    @i18nField(defaultValue = "设置地块颜色")
    public static i18n TUI_DominionManage_SetMapColorButton;
    @i18nField(defaultValue = "设置卫星地图上的地块颜色")
    public static i18n TUI_DominionManage_SetMapColorDescription;
    @i18nField(defaultValue = "你不在任何领地内，请指定领地名称 /dominion manage <领地名称>")
    public static i18n TUI_DominionManage_NotInDominion;

    @i18nField(defaultValue = "领地 %s 环境设置")
    public static i18n TUI_EnvSetting_Title;
    @i18nField(defaultValue = "用法: /dominion env_setting <领地名称> [页码]")
    public static i18n TUI_EnvSetting_Usage;

    @i18nField(defaultValue = "领地 %s 访客权限")
    public static i18n TUI_GuestSetting_Title;
    @i18nField(defaultValue = "用法: /dominion guest_setting <领地名称> [页码]")
    public static i18n TUI_GuestSetting_Usage;

    @i18nField(defaultValue = "领地 %s 的尺寸信息")
    public static i18n TUI_SizeInfo_Title;
    @i18nField(defaultValue = "领地所有者：")
    public static i18n TUI_SizeInfo_Owner;
    @i18nField(defaultValue = "领地大小：")
    public static i18n TUI_SizeInfo_Size;
    @i18nField(defaultValue = "中心坐标：")
    public static i18n TUI_SizeInfo_Center;
    @i18nField(defaultValue = "垂直高度：")
    public static i18n TUI_SizeInfo_Vertical;
    @i18nField(defaultValue = "Y轴坐标：")
    public static i18n TUI_SizeInfo_VertY;
    @i18nField(defaultValue = "水平面积：")
    public static i18n TUI_SizeInfo_Square;
    @i18nField(defaultValue = "领地体积：")
    public static i18n TUI_SizeInfo_Volume;
    @i18nField(defaultValue = "传送点坐标：")
    public static i18n TUI_SizeInfo_TpLocation;
    @i18nField(defaultValue = "无")
    public static i18n TUI_SizeInfo_NoneTp;
    @i18nField(defaultValue = "你不在任何领地内，请指定领地名称 /dominion info [领地名称]")
    public static i18n TUI_SizeInfo_Usage;

    @i18nField(defaultValue = "领地 %s 成员列表")
    public static i18n TUI_MemberList_Title;
    @i18nField(defaultValue = "添加成员")
    public static i18n TUI_MemberList_AddButton;
    @i18nField(defaultValue = "权限")
    public static i18n TUI_MemberList_FlagButton;
    @i18nField(defaultValue = "配置成员权限")
    public static i18n TUI_MemberList_FlagDescription;
    @i18nField(defaultValue = "移除")
    public static i18n TUI_MemberList_RemoveButton;
    @i18nField(defaultValue = "将此成员移出（变为访客）")
    public static i18n TUI_MemberList_RemoveDescription;
    @i18nField(defaultValue = "你不是领地主人，无法编辑管理员权限")
    public static i18n TUI_MemberList_NoPermissionSet;
    @i18nField(defaultValue = "你不是领地主人，无法移除管理员")
    public static i18n TUI_MemberList_NoPermissionRemove;
    @i18nField(defaultValue = "此成员属于权限组 %s 无法单独编辑权限")
    public static i18n TUI_MemberList_BelongToGroup;
    @i18nField(defaultValue = "用法: /dominion member list <领地名称> [页码]")
    public static i18n TUI_MemberList_Usage;
    @i18nField(defaultValue = "这是一个管理员")
    public static i18n TUI_MemberList_AdminTag;
    @i18nField(defaultValue = "这是一个普通成员")
    public static i18n TUI_MemberList_NormalTag;
    @i18nField(defaultValue = "这是一个黑名单成员")
    public static i18n TUI_MemberList_BlacklistTag;
    @i18nField(defaultValue = "这个成员在一个权限组里")
    public static i18n TUI_MemberList_GroupTag;

    @i18nField(defaultValue = "玩家 %s 在领地 %s 的权限设置")
    public static i18n TUI_MemberSetting_Title;
    @i18nField(defaultValue = "套用模板")
    public static i18n TUI_MemberSetting_ApplyTemplateButton;
    @i18nField(defaultValue = "选择一个权限模板套用")
    public static i18n TUI_MemberSetting_ApplyTemplateDescription;
    @i18nField(defaultValue = "用法: /dominion member setting <领地名称> <玩家名称> [页码]")
    public static i18n TUI_MemberSetting_Usage;

    @i18nField(defaultValue = "选择玩家添加为成员")
    public static i18n TUI_SelectPlayer_Title;
    @i18nField(defaultValue = "只能选择已经登录过的玩家")
    public static i18n TUI_SelectPlayer_Description;
    @i18nField(defaultValue = "用法: /dominion member select_player <领地名称> [页码]")
    public static i18n TUI_SelectPlayer_Usage;

    @i18nField(defaultValue = "选择一个模板")
    public static i18n TUI_SelectTemplate_Title;
    @i18nField(defaultValue = "套用在领地 %s 的成员 %s 身上")
    public static i18n TUI_SelectTemplate_Description;
    @i18nField(defaultValue = "用法: /dominion member select_template <领地名称> <玩家名称>  [页码]")
    public static i18n TUI_SelectTemplate_Usage;

    @i18nField(defaultValue = "领地 %s 权限组列表")
    public static i18n TUI_GroupList_Title;
    @i18nField(defaultValue = "创建权限组")
    public static i18n TUI_GroupList_CreateButton;
    @i18nField(defaultValue = "创建一个新的权限组")
    public static i18n TUI_GroupList_CreateDescription;
    @i18nField(defaultValue = "删除权限组 %s")
    public static i18n TUI_GroupList_DeleteDescription;
    @i18nField(defaultValue = "编辑权限组 %s")
    public static i18n TUI_GroupList_EditDescription;
    @i18nField(defaultValue = "添加成员到权限组 %s")
    public static i18n TUI_GroupList_AddMemberDescription;
    @i18nField(defaultValue = "把 %s 移出权限组 %s")
    public static i18n TUI_GroupList_RemoveMemberDescription;
    @i18nField(defaultValue = "用法: /dominion group list <领地名称> [页码]")
    public static i18n TUI_GroupList_Usage;

    @i18nField(defaultValue = "重命名此权限组")
    public static i18n TUI_GroupSetting_RenameButton;
    @i18nField(defaultValue = "用法: /dominion group setting <领地名称> <权限组名称> [页码]")
    public static i18n TUI_GroupSetting_Usage;
    @i18nField(defaultValue = "权限组 ")
    public static i18n TUI_GroupSetting_TitleL;
    @i18nField(defaultValue = " 管理")
    public static i18n TUI_GroupSetting_TitleR;
    @i18nField(defaultValue = "重命名权限组 %s")
    public static i18n TUI_GroupSetting_RenameDescription;

    @i18nField(defaultValue = "选择成员")
    public static i18n TUI_SelectMember_Title;
    @i18nField(defaultValue = "选择成员添加到权限组 %s")
    public static i18n TUI_SelectMember_Description;
    @i18nField(defaultValue = "用法: /dominion group select_member <领地名称> <权限组名称> [回显页码] [页码]")
    public static i18n TUI_SelectMember_Usage;

    @i18nField(defaultValue = "成员权限模板列表")
    public static i18n TUI_TemplateList_Title;
    @i18nField(defaultValue = "创建成员权限模板")
    public static i18n TUI_TemplateList_CreateButton;
    @i18nField(defaultValue = "创建一个新的成员权限模板")
    public static i18n TUI_TemplateList_CreateDescription;

    @i18nField(defaultValue = "模板 %s 权限管理")
    public static i18n TUI_TemplateSetting_Title;

    @i18nField(defaultValue = "从 Residence 迁移数据")
    public static i18n TUI_Migrate_Title;
    @i18nField(defaultValue = "你没有可迁移的数据")
    public static i18n TUI_Migrate_NoData;
    @i18nField(defaultValue = "迁移")
    public static i18n TUI_Migrate_Button;
    @i18nField(defaultValue = "子领地无法手动迁移，会随父领地自动迁移")
    public static i18n TUI_Migrate_SubDominion;

    @i18nField(defaultValue = "我可使用的权限组称号")
    public static i18n TUI_TitleList_Title;
    @i18nField(defaultValue = "卸下")
    public static i18n TUI_TitleList_RemoveButton;
    @i18nField(defaultValue = "使用")
    public static i18n TUI_TitleList_ApplyButton;
    @i18nField(defaultValue = "来自领地：")
    public static i18n TUI_TitleList_FromDominion;

    @i18nField(defaultValue = "修改尺寸")
    public static i18n TUI_ResizeButton;
    @i18nField(defaultValue = "修改领地 %s 的尺寸")
    public static i18n TUI_ResizeDominion;
    @i18nField(defaultValue = "北(Y-)")
    public static i18n TUI_ResizeDominion_North;
    @i18nField(defaultValue = "东(X+)")
    public static i18n TUI_ResizeDominion_East;
    @i18nField(defaultValue = "南(Y+)")
    public static i18n TUI_ResizeDominion_South;
    @i18nField(defaultValue = "西(X-)")
    public static i18n TUI_ResizeDominion_West;
    @i18nField(defaultValue = "上(Z+)")
    public static i18n TUI_ResizeDominion_Up;
    @i18nField(defaultValue = "下(Z-)")
    public static i18n TUI_ResizeDominion_Down;
    @i18nField(defaultValue = "扩大")
    public static i18n TUI_ResizeDominion_Expand;
    @i18nField(defaultValue = "缩小")
    public static i18n TUI_ResizeDominion_Contract;

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
    @i18nField(defaultValue = "%s 领地 %s 的边界 方向：%s")
    public static i18n CUI_Input_ResizeDominion;

    @i18nField(defaultValue = "AutoCreateRadius 不能等于 0，已重置为 10")
    public static i18n Config_Check_AutoCreateRadiusError;
    @i18nField(defaultValue = "MessageDisplay 不能设置为 %s，已重置为 ACTION_BAR")
    public static i18n Config_Check_MessageDisplayError;
    @i18nField(defaultValue = "AutoCleanAfterDays 不能等于 0，已重置为 180")
    public static i18n Config_Check_AutoCleanAfterDaysError;
    @i18nField(defaultValue = "Tool 名称设置错误，已重置为 ARROW")
    public static i18n Config_Check_ToolNameError;
    @i18nField(defaultValue = "InfoTool 名称设置错误，已重置为 STRING")
    public static i18n Config_Check_InfoToolNameError;
    @i18nField(defaultValue = "%s 的 MinY 不能大于等于 MaxY，已重置为 -64 和 320")
    public static i18n Config_Check_GroupMinYError;
    @i18nField(defaultValue = "%s 的 Size.MaxX 设置过小，已重置为 128")
    public static i18n Config_Check_GroupSizeMaxXError;
    @i18nField(defaultValue = "%s 的 Size.MaxY 设置过小，已重置为 64")
    public static i18n Config_Check_GroupSizeMaxYError;
    @i18nField(defaultValue = "%s 的 Size.MaxZ 设置过小，已重置为 128")
    public static i18n Config_Check_GroupSizeMaxZError;
    @i18nField(defaultValue = "%s 的 Size.MinX 设置过小，已重置为 4")
    public static i18n Config_Check_GroupSizeMinXError;
    @i18nField(defaultValue = "%s 的 Size.MinY 设置过小，已重置为 4")
    public static i18n Config_Check_GroupSizeMinYError;
    @i18nField(defaultValue = "%s 的 Size.MinZ 设置过小，已重置为 4")
    public static i18n Config_Check_GroupSizeMinZError;
    @i18nField(defaultValue = "%s 的 Size.MaxX 不能小于 MinX，已重置为 128 和 4")
    public static i18n Config_Check_GroupMaxMinXError;
    @i18nField(defaultValue = "%s 的 Size.MinY 不能小于 MinY，已重置为 64 和 4")
    public static i18n Config_Check_GroupMaxMinYError;
    @i18nField(defaultValue = "%s 的 Size.MaxZ 不能小于 MinZ，已重置为 128 和 4")
    public static i18n Config_Check_GroupMaxMinZError;
    @i18nField(defaultValue = "%s 的 Amount 设置不合法，已重置为 10")
    public static i18n Config_Check_GroupAmountError;
    @i18nField(defaultValue = "%s 的 Depth 设置不合法，已重置为 3")
    public static i18n Config_Check_GroupDepthError;
    @i18nField(defaultValue = "%s 的 Price 设置不合法，已重置为 10.0")
    public static i18n Config_Check_GroupPriceError;
    @i18nField(defaultValue = "%s 的 Refund 设置不合法，已重置为 0.85")
    public static i18n Config_Check_GroupRefundError;
    @i18nField(defaultValue = "读取权限配置失败：%s")
    public static i18n Config_Check_LoadFlagError;
    @i18nField(defaultValue = "传送延迟不能小于 0，已重置为 0")
    public static i18n Config_Check_TpDelayError;
    @i18nField(defaultValue = "传送冷却不能小于 0，已重置为 0")
    public static i18n Config_Check_TpCoolDownError;

    @i18nField(defaultValue = "语言设置，参考 languages 文件夹下的文件名")
    public static i18n Config_Comment_Language;
    @i18nField(defaultValue = "自动创建领地的半径，单位为方块")
    public static i18n Config_Comment_AutoCreateRadius;
    @i18nField(defaultValue = "默认进入领地提示消息")
    public static i18n Config_Comment_DefaultJoinMessage;
    @i18nField(defaultValue = "默认离开领地提示消息")
    public static i18n Config_Comment_DefaultLeaveMessage;
    @i18nField(defaultValue = "提示消息显示位置（BOSS_BAR, ACTION_BAR, TITLE, SUBTITLE, CHAT）")
    public static i18n Config_Comment_MessageDisplay;
    @i18nField(defaultValue = "玩家没有权限时的提示消息位置")
    public static i18n Config_Comment_MessageDisplayNoPermission;
    @i18nField(defaultValue = "进入/离开领地时的提示消息位置")
    public static i18n Config_Comment_MessageDisplayJoinLeave;
    @i18nField(defaultValue = "设置菜单中文档链接指向地址")
    public static i18n Config_Comment_DocLink;
    @i18nField(defaultValue = "设置菜单中指令帮助链接指向地址")
    public static i18n Config_Comment_CommandLink;
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
    public static i18n Config_Comment_SizeMaxX;
    @i18nField(defaultValue = "Y方向最大长度")
    public static i18n Config_Comment_SizeMaxY;
    @i18nField(defaultValue = "Z方向最大长度")
    public static i18n Config_Comment_SizeMaxZ;
    @i18nField(defaultValue = "X方向最小长度")
    public static i18n Config_Comment_SizeMinX;
    @i18nField(defaultValue = "Y方向最小长度")
    public static i18n Config_Comment_SizeMinY;
    @i18nField(defaultValue = "Z方向最小长度")
    public static i18n Config_Comment_SizeMinZ;
    @i18nField(defaultValue = "最大领地数量")
    public static i18n Config_Comment_Amount;
    @i18nField(defaultValue = "子领地深度")
    public static i18n Config_Comment_Depth;
    @i18nField(defaultValue = "0表示不开启")
    public static i18n Config_Comment_ZeroDisabled;
    @i18nField(defaultValue = "是否自动延伸到 MaxY 和 MinY")
    public static i18n Config_Comment_Vert;
    @i18nField(defaultValue = "是否允许OP无视领地限制")
    public static i18n Config_Comment_OpBypass;
    @i18nField(defaultValue = "单独设置某个世界的圈地规则（如不设置则使用以上规则）")
    public static i18n Config_Comment_WorldSettings;
    @i18nField(defaultValue = "传送延迟 秒")
    public static i18n Config_Comment_TpDelay;
    @i18nField(defaultValue = "传送冷却 秒")
    public static i18n Config_Comment_TpCoolDown;
    @i18nField(defaultValue = "自动清理长时间未上线玩家的领地（天）")
    public static i18n Config_Comment_AutoCleanAfterDays;
    @i18nField(defaultValue = "圈地工具名称")
    public static i18n Config_Comment_ToolName;
    @i18nField(defaultValue = "查询领地信息工具名称")
    public static i18n Config_Comment_InfoToolName;
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
    @i18nField(defaultValue = ">---------------------------------<")
    public static i18n Config_Comment_GroupLine1;
    @i18nField(defaultValue = "|       圈地限制特殊权限组配置       |")
    public static i18n Config_Comment_GroupLine2;
    @i18nField(defaultValue = ">---------------------------------<")
    public static i18n Config_Comment_GroupLine3;
    @i18nField(defaultValue = "此文件可以作为模板，你可以将此文件复制后重命名为你想要的")
    public static i18n Config_Comment_GroupLine4;
    @i18nField(defaultValue = "权限组名，然后修改里面的配置如果你想给赞助玩家（或者VIP）")
    public static i18n Config_Comment_GroupLine5;
    @i18nField(defaultValue = "一些特殊优惠，例如更少的圈地价格、更大的领地等，你可以在")
    public static i18n Config_Comment_GroupLine6;
    @i18nField(defaultValue = "这里配置。详细说明参阅以下链接：")
    public static i18n Config_Comment_GroupLine7;
    @i18nField(defaultValue = "> https://dominion.lunadeer.cn/%s/operator/privilege.html")
    public static i18n Config_Comment_GroupLine8DocumentAddress;

    @i18nField(defaultValue = "管理员")
    public static i18n Flags_admin_DisplayName;
    @i18nField(defaultValue = "管理领地内的其他成员权限")
    public static i18n Flags_admin_Description;

    @i18nField(defaultValue = "已选择第一个点: %d %d %d")
    public static i18n Tool_SelectFirstPoint;
    @i18nField(defaultValue = "已选择第二个点: %d %d %d")
    public static i18n Tool_SelectSecondPoint;
    @i18nField(defaultValue = "两个点不在同一个世界")
    public static i18n Tool_NotSameWorld;
    @i18nField(defaultValue = "已选择两个点，可以使用 /dominion create <领地名称> 创建领地")
    public static i18n Tool_SelectTwoPoints;
    @i18nField(defaultValue = "预计领地创建价格为 %.2f %s")
    public static i18n Tool_CreateDominionPrice;
    @i18nField(defaultValue = "尺寸： %d x %d x %d")
    public static i18n Tool_DominionSize;
    @i18nField(defaultValue = "面积： %d")
    public static i18n Tool_DominionSquare;
    @i18nField(defaultValue = "体积： %d")
    public static i18n Tool_DominionVolume;
    @i18nField(defaultValue = "高度： %d")
    public static i18n Tool_DominionHeight;
    @i18nField(defaultValue = "这个方块(%d, %d, %d)不在任何领地内")
    public static i18n Tool_LocationNotInDominion;
    @i18nField(defaultValue = "这个方块(%d, %d, %d)在领地 %s 内")
    public static i18n Tool_LocationInDominion;
    @i18nField(defaultValue = "领地主人: %s")
    public static i18n Tool_DominionOwner;


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
