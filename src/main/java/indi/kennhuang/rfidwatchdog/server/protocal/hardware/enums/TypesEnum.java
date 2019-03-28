package indi.kennhuang.rfidwatchdog.server.protocal.hardware.enums;

public class TypesEnum {
    public enum types {
        RESPONSE,
        PING,
        CARD_CHECK,
        AUTH,
        PONG,
        OK,
        UNAUTHORIZED
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
            case PONG:
                return 5;
            case OK:
                return 6;
            case UNAUTHORIZED:
                return 7;
            default:
                return 0;
        }
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
            case 5:
                return types.PONG;
            case 6:
                return types.OK;
            case 7:
                return types.UNAUTHORIZED;
            default:
                return null;
        }
    }
}
