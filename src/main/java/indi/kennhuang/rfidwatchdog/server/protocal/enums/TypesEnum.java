package indi.kennhuang.rfidwatchdog.server.protocal.enums;

public class TypesEnum {
    public enum types {
        RESPONSE,
        PING,
        CARD_CHECK,
        AUTH
    }

    public static int decode(types type) {
        switch (type) {
            case RESPONSE:
                return 1;
            case PING:
                return 2;
            case CARD_CHECK:
                return 3;
            case AUTH:
                return 4;
        }
        return 0;
    }

    public static types encode(int type) {
        switch (type) {
            case 1:
                return types.RESPONSE;
            case 2:
                return types.PING;
            case 3:
                return types.CARD_CHECK;
            case 4:
                return types.AUTH;
            default:
                return null;
        }
    }
}
