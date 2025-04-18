package cn.lunadeer.dominion.misc;

import cn.lunadeer.dominion.commands.*;
import cn.lunadeer.dominion.uis.tuis.MainMenu;

public class InitCommands {
    public InitCommands() {
        // cn.lunadeer.dominion.commands
        new AdministratorCommand();
        new DominionCreateCommand();
        new DominionFlagCommand();
        new DominionOperateCommand();
        new GroupCommand();
        new GroupTitleCommand();
        new MemberCommand();
        new MigrationCommand();
        new TemplateCommand();
        new CopyCommand();
        //
        new MainMenu();
    }
}
