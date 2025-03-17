package cn.lunadeer.dominion.commands;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import cn.lunadeer.dominion.events.dominion.modify.DominionSetEnvFlagEvent;
import cn.lunadeer.dominion.events.dominion.modify.DominionSetGuestFlagEvent;
import cn.lunadeer.dominion.misc.CommandArguments;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.EnvSetting;
import cn.lunadeer.dominion.uis.tuis.dominion.manage.GuestSetting;
import cn.lunadeer.dominion.utils.Notification;
import cn.lunadeer.dominion.utils.command.SecondaryCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

import static cn.lunadeer.dominion.Dominion.defaultPermission;
import static cn.lunadeer.dominion.misc.Converts.*;


public class DominionFlagCommand {
    /**
     * Secondary command for setting an environment flag on a dominion.
     * Requires the following arguments:
     * - RequiredDominionArgument: The dominion to set the flag on.
     * - EnvFlagArgument: The environment flag to set.
     * - BollenOption: The value to set the flag to.
     * - OptionalPageArgument: The page to display after setting the flag.
     */
    public static SecondaryCommand SetEnvFlag = new SecondaryCommand("set_env", List.of(
            new CommandArguments.RequiredDominionArgument(),
            new CommandArguments.EnvFlagArgument(),
            new CommandArguments.BollenOption(),
            new CommandArguments.OptionalPageArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            setEnv(sender, getArgumentValue(0), getArgumentValue(1), getArgumentValue(2), getArgumentValue(3));
        }
    }.needPermission(defaultPermission).register();

    /**
     * Sets an environment flag on a dominion.
     *
     * @param sender       The command sender who initiates the flag setting.
     * @param dominionName The name of the dominion to set the flag on.
     * @param flagName     The name of the environment flag to set.
     * @param valueStr     The value to set the flag to.
     * @param pageStr      The page to display after setting the flag.
     */
    public static void setEnv(CommandSender sender, String dominionName, String flagName, String valueStr, String pageStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            EnvFlag flag = toEnvFlag(flagName);
            boolean value = toBoolean(valueStr);
            new DominionSetEnvFlagEvent(sender, dominion, flag, value).call();
            EnvSetting.show(sender, dominionName, pageStr);
        } catch (Exception e) {
            Notification.error(sender, e);
        }
    }

    /**
     * Secondary command for setting a guest flag on a dominion.
     * Requires the following arguments:
     * - RequiredDominionArgument: The dominion to set the flag on.
     * - PriFlagArgument: The flag to set.
     * - BollenOption: The value to set the flag to.
     * - OptionalPageArgument: The page to display after setting the flag.
     */
    public static SecondaryCommand SetGuestFlag = new SecondaryCommand("set_guest", List.of(
            new CommandArguments.RequiredDominionArgument(),
            new CommandArguments.GuestFlagArgument(),
            new CommandArguments.BollenOption(),
            new CommandArguments.OptionalPageArgument()
    )) {
        @Override
        public void executeHandler(CommandSender sender) {
            setGuest(sender, getArgumentValue(0), getArgumentValue(1), getArgumentValue(2), getArgumentValue(3));
        }
    }.needPermission(defaultPermission).register();

    /**
     * Sets a guest flag on a dominion.
     *
     * @param sender       The command sender who initiates the flag setting.
     * @param dominionName The name of the dominion to set the flag on.
     * @param flagName     The name of the flag to set.
     * @param valueStr     The value to set the flag to.
     * @param pageStr      The page to display after setting the flag.
     */
    public static void setGuest(CommandSender sender, String dominionName, String flagName, String valueStr, String pageStr) {
        try {
            DominionDTO dominion = toDominionDTO(dominionName);
            PriFlag flag = toPriFlag(flagName);
            boolean value = toBoolean(valueStr);
            new DominionSetGuestFlagEvent(sender, dominion, flag, value).call();
            GuestSetting.show(sender, dominionName, pageStr);
        } catch (Exception e) {
            Notification.error(sender, e);
        }
    }
}
