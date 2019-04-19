package im.zego.publishstreamtest.zgUtils;

import android.widget.Toast;

import im.zego.publishstreamtest.ZegoApplication;

public class ZegoAppInfoUtil {

    public static long convertAppidToLong(String s){

        return Long.parseLong(s);

    }

    public static byte[] convertAppKeyToByteArray(String appKey){

        String[] keys = appKey.split(",");

        try {
            if (keys.length != 32) {
                throw new NumberFormatException("App Sign Key Illegal");
            }
        }catch (NumberFormatException e){

            e.printStackTrace();

        }

        byte[] byteSignKey = new byte[32];
        for (int i = 0; i < 32; i++) {
            int data = Integer.valueOf(keys[i].trim().replace("0x", ""), 16);
            byteSignKey[i] = (byte) data;
        }
        return byteSignKey;

    }
}
