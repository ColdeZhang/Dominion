package cn.lunadeer.dominion.configuration;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.flag.Flag;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.commands.*;
import cn.lunadeer.dominion.handler.DominionEventHandler;
import cn.lunadeer.dominion.handler.GroupEventHandler;
import cn.lunadeer.dominion.handler.MemberEventHandler;
import cn.lunadeer.dominion.handler.SelectPointEventsHandler;
import cn.lunadeer.dominion.managers.DatabaseTables;
import cn.lunadeer.dominion.managers.MultiServerManager;
import cn.lunadeer.dominion.managers.TeleportManager;
import cn.lunadeer.dominion.misc.Asserts;
import cn.lunadeer.dominion.misc.Converts;
import cn.lunadeer.dominion.misc.Others;
import cn.lunadeer.dominion.uis.cuis.*;
import cn.lunadeer.dominion.uis.tuis.AllDominion;
import cn.lunadeer.dominion.uis.tuis.MainMenu;
import cn.lunadeer.dominion.uis.tuis.MigrateList;
import cn.lunadeer.dominion.uis.tuis.TitleList;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionList;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.dominion.uis.tuis.dominion.copy.*;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.EnvSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.GuestSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.Info;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.SetSize;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.group.GroupList;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.group.GroupSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.group.SelectMember;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.MemberList;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.MemberSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.SelectPlayer;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.SelectTemplate;
import cn.lunadeer.dominion.uis.tuis.template.TemplateList;
import cn.lunadeer.dominion.uis.tuis.template.TemplateSetting;
import cn.lunadeer.dominion.utils.VaultConnect.VaultConnect;
import cn.lunadeer.dominion.utils.command.InvalidArgumentException;
import cn.lunadeer.dominion.utils.command.NoPermissionException;
import cn.lunadeer.dominion.utils.configuration.*;
import cn.lunadeer.dominion.utils.scui.CuiTextInput;
import cn.lunadeer.dominion.utils.webMap.BlueMapConnect;
import cn.lunadeer.dominion.utils.webMap.DynmapConnect;

public class Language extends ConfigurationFile {

    // languages file name list here will be saved to plugin data folder
    @HandleManually
    public enum LanguageCode {
        en_us,
        zh_cn,
        ja_jp,
        ru_ru,
        de_de,
        fi_fi,
    }

    public static Dominion.DominionText dominionText = new Dominion.DominionText();

    public static MultiServerManager.MultiServerManagerText multiServerManagerText = new MultiServerManager.MultiServerManagerText();

    public static Asserts.AssertsText assertsText = new Asserts.AssertsText();
    public static Converts.ConvertsText convertsText = new Converts.ConvertsText();
    public static Others.OthersText othersText = new Others.OthersText();

    public static VaultConnect.VaultConnectText vaultConnectText = new VaultConnect.VaultConnectText();

    // Event Handler
    public static DominionEventHandler.DominionEventHandlerText dominionEventHandlerText = new DominionEventHandler.DominionEventHandlerText();
    public static MemberEventHandler.MemberEventHandlerText memberEventHandlerText = new MemberEventHandler.MemberEventHandlerText();
    public static GroupEventHandler.GroupEventHandlerText groupEventHandlerText = new GroupEventHandler.GroupEventHandlerText();
    public static SelectPointEventsHandler.SelectPointEventsHandlerText selectPointEventsHandlerText = new SelectPointEventsHandler.SelectPointEventsHandlerText();

    // TUI
    public static MainMenu.MenuTuiText menuTuiText = new MainMenu.MenuTuiText();
    public static DominionList.DominionListTuiText dominionListTuiText = new DominionList.DominionListTuiText();
    public static DominionManage.DominionManageTuiText dominionManageTuiText = new DominionManage.DominionManageTuiText();
    public static SetSize.SetSizeTuiText setSizeTuiText = new SetSize.SetSizeTuiText();
    public static EnvSetting.EnvSettingTuiText envSettingTuiText = new EnvSetting.EnvSettingTuiText();
    public static GuestSetting.GuestSettingTuiText guestSettingTuiText = new GuestSetting.GuestSettingTuiText();
    public static Info.SizeInfoTuiText sizeInfoTuiText = new Info.SizeInfoTuiText();
    public static MigrateList.MigrateListText migrateListText = new MigrateList.MigrateListText();
    public static MemberList.MemberListTuiText memberListTuiText = new MemberList.MemberListTuiText();
    public static SelectPlayer.SelectPlayerTuiText selectPlayerTuiText = new SelectPlayer.SelectPlayerTuiText();
    public static MemberSetting.MemberSettingTuiText memberSettingTuiText = new MemberSetting.MemberSettingTuiText();
    public static TemplateList.TemplateListTuiText templateListTuiText = new TemplateList.TemplateListTuiText();
    public static SelectTemplate.SelectTemplateTuiText selectTemplateTuiText = new SelectTemplate.SelectTemplateTuiText();
    public static GroupList.GroupListTuiText groupListTuiText = new GroupList.GroupListTuiText();
    public static GroupSetting.GroupSettingTuiText groupSettingTuiText = new GroupSetting.GroupSettingTuiText();
    public static SelectMember.SelectMemberTuiText selectMemberTuiText = new SelectMember.SelectMemberTuiText();
    public static TitleList.TitleListTuiText titleListTuiText = new TitleList.TitleListTuiText();
    public static AllDominion.AllDominionTuiText allDominionTuiText = new AllDominion.AllDominionTuiText();
    public static TemplateSetting.TemplateSettingText templateSettingText = new TemplateSetting.TemplateSettingText();
    public static CopyMenu.CopyMenuTuiText copyMenuTuiText = new CopyMenu.CopyMenuTuiText();
    public static EnvCopy.EnvCopyTuiText envCopyTuiText = new EnvCopy.EnvCopyTuiText();
    public static GuestCopy.GuestCopyTuiText guestCopyTuiText = new GuestCopy.GuestCopyTuiText();
    public static MemberCopy.MemberCopyTuiText memberCopyTuiText = new MemberCopy.MemberCopyTuiText();
    public static GroupCopy.GroupCopyTuiText groupCopyTuiText = new GroupCopy.GroupCopyTuiText();

    // CUI
    public static ResizeDominion.ResizeDominionCuiText resizeDominionCuiText = new ResizeDominion.ResizeDominionCuiText();
    public static EditEnterMessage.EditEnterMessageCuiText editEnterMessageCuiText = new EditEnterMessage.EditEnterMessageCuiText();
    public static EditLeaveMessage.EditLeaveMessageCuiText editLeaveMessageCuiText = new EditLeaveMessage.EditLeaveMessageCuiText();
    public static RenameDominion.RenameDominionCuiText renameDominionCuiText = new RenameDominion.RenameDominionCuiText();
    public static CreateDominion.CreateDominionCuiText createDominionCuiText = new CreateDominion.CreateDominionCuiText();
    public static SetMapColor.SetMapColorCuiText setMapColorCuiText = new SetMapColor.SetMapColorCuiText();
    public static SearchPlayer.SearchPlayerCuiText searchPlayerCuiText = new SearchPlayer.SearchPlayerCuiText();
    public static CreateTemplate.CreateTemplateCuiText createTemplateCuiText = new CreateTemplate.CreateTemplateCuiText();
    public static CreateGroup.CreateGroupCuiText createGroupCuiText = new CreateGroup.CreateGroupCuiText();
    public static RenameGroup.RenameGroupCuiText renameGroupCuiText = new RenameGroup.RenameGroupCuiText();

    // Commands
    public static AdministratorCommand.AdministratorCommandText administratorCommandText = new AdministratorCommand.AdministratorCommandText();
    public static MigrationCommand.MigrationCommandText migrationCommandText = new MigrationCommand.MigrationCommandText();
    public static TemplateCommand.TemplateCommandText templateCommandText = new TemplateCommand.TemplateCommandText();
    public static GroupTitleCommand.GroupTitleCommandText groupTitleCommandText = new GroupTitleCommand.GroupTitleCommandText();
    public static CopyCommand.CopyCommandText copyCommandText = new CopyCommand.CopyCommandText();


    public static Configuration.ConfigurationText configurationText = new Configuration.ConfigurationText();

    public static Limitation.LimitationText limitationText = new Limitation.LimitationText();

    public static DatabaseTables.DatabaseManagerText databaseManagerText = new DatabaseTables.DatabaseManagerText();

    public static TeleportManager.TeleportManagerText teleportManagerText = new TeleportManager.TeleportManagerText();

    // web map render
    public static BlueMapConnect.BlueMapConnectText blueMapConnectText = new BlueMapConnect.BlueMapConnectText();
    public static DynmapConnect.DynmapConnectText dynmapConnectText = new DynmapConnect.DynmapConnectText();


    public static CommandExceptionText commandExceptionText = new CommandExceptionText();

    public static class CommandExceptionText extends ConfigurationPart {
        public String noPermission = "You do not have permission {0} to do this.";
        public String invalidArguments = "Invalid arguments, usage e.g. {0}.";
    }

    public static CuiInputText cuiInputText = new CuiInputText();

    public static class CuiInputText extends ConfigurationPart {
        public String cuiNotAvailable = "CUI is not available on no-paper (fork) core server.";
        public String cuiSuggestCommand = "You can use command {0} instead.";
        public String cuiButton = "Left Click: Confirm | Right Click: Cancel";
        public String cuiInputInvalid = "Input can not contain space.";
    }

    @PreProcess
    public void loadFlagsText() {
        for (Flag flag : Flags.getAllFlags()) {
            if (getYaml().contains(flag.getDisplayNameKey())) {
                flag.setDisplayName(getYaml().getString(flag.getDisplayNameKey()));
            } else {
                getYaml().set(flag.getDisplayNameKey(), flag.getDisplayName());
            }
            if (getYaml().contains(flag.getDescriptionKey())) {
                flag.setDescription(getYaml().getString(flag.getDescriptionKey()));
            } else {
                getYaml().set(flag.getDescriptionKey(), flag.getDescription());
            }
        }
    }

    @PostProcess
    public static void setOtherStaticText() {
        // cn.lunadeer.dominion.utils.command
        InvalidArgumentException.MSG = commandExceptionText.invalidArguments;
        NoPermissionException.MSG = commandExceptionText.noPermission;
        // cn.lunadeer.dominion.utils.scui.CuiTextInput
        CuiTextInput.CUI_NOT_AVAILABLE = cuiInputText.cuiNotAvailable;
        CuiTextInput.CUI_SUGGEST_COMMAND = cuiInputText.cuiSuggestCommand;
        CuiTextInput.CUI_BUTTON = cuiInputText.cuiButton;
        CuiTextInput.CUI_INPUT_INVALID = cuiInputText.cuiInputInvalid;
    }

}
