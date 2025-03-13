package cn.lunadeer.dominion.misc;

import cn.lunadeer.dominion.api.dtos.DominionDTO;
import cn.lunadeer.dominion.api.dtos.GroupDTO;
import cn.lunadeer.dominion.api.dtos.PlayerDTO;
import cn.lunadeer.dominion.api.dtos.flag.Flag;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.cache.CacheManager;
import cn.lunadeer.dominion.dtos.TemplateDTO;
import cn.lunadeer.dominion.utils.XLogger;
import cn.lunadeer.dominion.utils.command.Argument;
import cn.lunadeer.dominion.utils.command.ConditionalArgument;
import cn.lunadeer.dominion.utils.command.Option;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.lunadeer.dominion.misc.Converts.toDominionDTO;

public class CommandArguments {

    /**
     * Represents an option for a boolean value.
     * This option provides "true" and "false" as possible values.
     */
    public static class BollenOption extends Option {
        public BollenOption() {
            super(List.of("true", "false"));
        }
    }

    /**
     * Constructs a new DominionArgument.
     * If the command sender is a player, it returns the list of dominion names
     * that the player administers as suggestions.
     * Otherwise, it returns the list of all dominion names.
     */
    public static class RequiredDominionArgument extends Argument {
        public RequiredDominionArgument() {
            super("dominion_name", true, (commandSender) -> {
                if (commandSender instanceof Player player) {
                    return CacheManager.instance.getPlayerManageDominionNames(player.getUniqueId());
                } else {
                    return CacheManager.instance.getAllDominionNames();
                }
            });
        }
    }

    /**
     * Represents an argument for a required player name.
     * This argument provides suggestions based on the last known names of all players.
     */
    public static class RequiredPlayerArgument extends Argument {
        public RequiredPlayerArgument() {
            super("player_name", true, (commandSender) -> cn.lunadeer.dominion.dtos.PlayerDTO.all().stream().map(PlayerDTO::getLastKnownName).toList());
        }
    }

    /**
     * Represents an argument for an environment flag name.
     * This argument provides suggestions based on all enabled environment flags.
     */
    public static class EnvFlagArgument extends Argument {
        public EnvFlagArgument() {
            super("env_flag_name", true, (commandSender) -> Flags.getAllEnvFlagsEnable().stream().map(Flag::getFlagName).toList());
        }
    }

    /**
     * Represents an argument for a guest flag name.
     * This argument provides suggestions based on all enabled privilege flags,
     * excluding the ADMIN flag.
     */
    public static class GuestFlagArgument extends Argument {
        public GuestFlagArgument() {
            super("guest_flag_name", true, (commandSender) -> Flags.getAllPriFlagsEnable().stream().filter(
                    flag -> !flag.equals(Flags.ADMIN)
            ).map(Flag::getFlagName).toList());
        }
    }

    /**
     * Represents an argument for a primary flag name.
     * This argument provides suggestions based on all enabled privilege flags.
     */
    public static class PriFlagArgument extends Argument {
        public PriFlagArgument() {
            super("pri_flag_name", true, (commandSender) -> Flags.getAllPriFlagsEnable().stream().map(Flag::getFlagName).toList());
        }
    }

    /**
     * Represents an optional argument for a page number.
     * This argument defaults to "1" if not provided.
     */
    public static class OptionalPageArgument extends Argument {
        public OptionalPageArgument() {
            super("page", "1");
        }
    }

    public static class RequiredTemplateArgument extends Argument {
        public RequiredTemplateArgument() {
            super("template_name", true, (commandSender) -> {
                if (commandSender instanceof Player player) {
                    try {
                        return TemplateDTO.selectAll(player.getUniqueId()).stream().map(TemplateDTO::getName).toList();
                    } catch (Exception e) {
                        XLogger.error(e);
                        return List.of();
                    }
                } else {
                    return List.of();
                }
            });
        }
    }

    public static class RequiredMemberArgument extends ConditionalArgument {

        public RequiredMemberArgument(Integer dominionArgumentIndex) {
            super("member_name", List.of(dominionArgumentIndex));
        }

        @Override
        public List<String> handelCondition(CommandSender sender) {
            DominionDTO dominion = toDominionDTO(getConditionArguments().get(0));
            return dominion.getMembers().stream().map(member -> member.getPlayer().getLastKnownName()).toList();
        }
    }

    public static class RequiredGroupArgument extends ConditionalArgument {
        public RequiredGroupArgument(Integer dominionArgumentIndex) {
            super("group_name", List.of(dominionArgumentIndex));
        }

        @Override
        public List<String> handelCondition(CommandSender sender) {
            DominionDTO dominion = toDominionDTO(getConditionArguments().get(0));
            return dominion.getGroups().stream().map(GroupDTO::getNamePlain).toList();
        }
    }

    public static class PlayerTitleIdArgument extends Argument {
        public PlayerTitleIdArgument() {
            super("title_id", true, (commandSender) -> {
                if (commandSender instanceof Player player) {
                    return CacheManager.instance.getPlayerCache().getPlayerGroupTitleList(player.getUniqueId()).stream().map(title -> title.getId().toString()).toList();
                } else {
                    return List.of();
                }
            });
        }
    }
}
