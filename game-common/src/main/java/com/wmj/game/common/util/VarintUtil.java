package com.wmj.game.common.util;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/19
 * @Description:
 */
public final class VarintUtil {

    public static int rawVarint32(byte[] data, int pos) {
        fastPath:
        {
            int tempPos = pos;
            int x;
            if ((x = data[tempPos++]) >= 0) {
                return x;
            } else if ((x ^= (data[tempPos++] << 7)) < 0) {
                x ^= (~0 << 7);
            } else if ((x ^= (data[tempPos++] << 14)) >= 0) {
                x ^= (~0 << 7) ^ (~0 << 14);
            } else if ((x ^= (data[tempPos++] << 21)) < 0) {
                x ^= (~0 << 7) ^ (~0 << 14) ^ (~0 << 21);
            } else {
                int y = data[tempPos++];
                x ^= y << 28;
                x ^= (~0 << 7) ^ (~0 << 14) ^ (~0 << 21) ^ (~0 << 28);
                if (y < 0
                        && data[tempPos++] < 0
                        && data[tempPos++] < 0
                        && data[tempPos++] < 0
                        && data[tempPos++] < 0
                        && data[tempPos++] < 0) {
                    break fastPath;
                }
            }
            return x;
        }
        return 0;
    }
}
