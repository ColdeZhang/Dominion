package cn.lunadeer.dominion.configuration;

import cn.lunadeer.dominion.Dominion;
import cn.lunadeer.dominion.api.dtos.flag.Flag;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.commands.AdministratorCommand;
import cn.lunadeer.dominion.commands.MigrationCommand;
import cn.lunadeer.dominion.commands.TemplateCommand;
import cn.lunadeer.dominion.handler.DominionEventHandler;
import cn.lunadeer.dominion.handler.MemberEventHandler;
import cn.lunadeer.dominion.managers.DatabaseTables;
import cn.lunadeer.dominion.misc.Asserts;
import cn.lunadeer.dominion.misc.Converts;
import cn.lunadeer.dominion.misc.Others;
import cn.lunadeer.dominion.uis.cuis.*;
import cn.lunadeer.dominion.uis.tuis.MainMenu;
import cn.lunadeer.dominion.uis.tuis.MigrateList;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionList;
import cn.lunadeer.dominion.uis.tuis.dominion.DominionManage;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.EnvSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.GuestSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.Info;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.SetSize;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.MemberList;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.MemberSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.SelectPlayer;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.member.SelectTemplate;
import cn.lunadeer.dominion.uis.tuis.template.TemplateList;
import cn.lunadeer.dominion.utils.VaultConnect.VaultConnect;
import cn.lunadeer.dominion.utils.command.InvalidArgumentException;
import cn.lunadeer.dominion.utils.command.NoPermissionException;
import cn.lunadeer.dominion.utils.configuration.ConfigurationFile;
import cn.lunadeer.dominion.utils.configuration.ConfigurationPart;
import cn.lunadeer.dominion.utils.configuration.PostProcess;
import cn.lunadeer.dominion.utils.configuration.PreProcess;

public class Language extends ConfigurationFile {

    public static Dominion.DominionText dominionText = new Dominion.DominionText();

    public static Asserts.AssertsText assertsText = new Asserts.AssertsText();
    public static Converts.ConvertsText convertsText = new Converts.ConvertsText();
    public static Others.OthersText othersText = new Others.OthersText();

    public static VaultConnect.VaultConnectText vaultConnectText = new VaultConnect.VaultConnectText();

    // Event Handler
    public static DominionEventHandler.DominionEventHandlerText dominionEventHandlerText = new DominionEventHandler.DominionEventHandlerText();
    public static MemberEventHandler.MemberEventHandlerText memberEventHandlerText = new MemberEventHandler.MemberEventHandlerText();

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

    // CUI
    public static ResizeDominion.ResizeDominionCuiText resizeDominionCuiText = new ResizeDominion.ResizeDominionCuiText();
    public static EditEnterMessage.EditEnterMessageCuiText editEnterMessageCuiText = new EditEnterMessage.EditEnterMessageCuiText();
    public static EditLeaveMessage.EditLeaveMessageCuiText editLeaveMessageCuiText = new EditLeaveMessage.EditLeaveMessageCuiText();
    public static RenameDominion.RenameDominionCuiText renameDominionCuiText = new RenameDominion.RenameDominionCuiText();
    public static CreateDominion.CreateDominionCuiText createDominionCuiText = new CreateDominion.CreateDominionCuiText();
    public static SetMapColor.SetMapColorCuiText setMapColorCuiText = new SetMapColor.SetMapColorCuiText();
    public static SearchPlayer.SearchPlayerCuiText searchPlayerCuiText = new SearchPlayer.SearchPlayerCuiText();
    public static CreateTemplate.CreateTemplateCuiText createTemplateCuiText = new CreateTemplate.CreateTemplateCuiText();

    // Commands
    public static AdministratorCommand.AdministratorCommandText administratorCommandText = new AdministratorCommand.AdministratorCommandText();
    public static MigrationCommand.MigrationCommandText migrationCommandText = new MigrationCommand.MigrationCommandText();
    public static TemplateCommand.TemplateCommandText templateCommandText = new TemplateCommand.TemplateCommandText();


    public static Configuration.ConfigurationText configurationText = new Configuration.ConfigurationText();

    public static Limitation.LimitationText limitationText = new Limitation.LimitationText();

    public static DatabaseTables.DatabaseManagerText databaseManagerText = new DatabaseTables.DatabaseManagerText();


    public static CommandExceptionText commandExceptionText = new CommandExceptionText();

    public static class CommandExceptionText extends ConfigurationPart {
        public String noPermission = "You do not have permission {0} to do this.";
        public String invalidArguments = "Invalid arguments, usage e.g. {0}.";
    }

    @PreProcess
    public void loadFlagsText() {
        for (Flag flag : Flags.getAllFlags()) {
            if (yaml.contains(flag.getDisplayNameKey())) {
                flag.setDisplayName(yaml.getString(flag.getDisplayNameKey()));
            } else {
                yaml.set(flag.getDisplayNameKey(), flag.getDisplayName());
            }
            if (yaml.contains(flag.getDescriptionKey())) {
                flag.setDescription(yaml.getString(flag.getDescriptionKey()));
            } else {
                yaml.set(flag.getDescriptionKey(), flag.getDescription());
            }
        }
    }

    @PostProcess
    public static void setCommandExceptionText() {
        InvalidArgumentException.MSG = commandExceptionText.invalidArguments;
        NoPermissionException.MSG = commandExceptionText.noPermission;
    }

}
