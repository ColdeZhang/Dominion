package cn.lunadeer.dominion.misc;

import static cn.lunadeer.dominion.utils.Misc.formatString;

public class DominionException extends RuntimeException {

    public String formatMessage;

    public DominionException(String message, Object... args) {
        super(message);
        formatMessage = formatString(message, args);
    }

    @Override
    public String getMessage() {
        return formatMessage;
    }
}
