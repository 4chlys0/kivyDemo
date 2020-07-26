package org.kamranzafar.jtar;

public class Octal {
    public static long parseOctal(byte[] header, int offset, int length) {
        boolean stillPadding = true;
        int end = offset + length;
        long result = 0;
        int i = offset;
        while (i < end && header[i] != 0) {
            if (header[i] == 32 || header[i] == 48) {
                if (!stillPadding) {
                    if (header[i] == 32) {
                        break;
                    }
                } else {
                    continue;
                    i++;
                }
            }
            stillPadding = false;
            result = (result << 3) + ((long) (header[i] - TarHeader.LF_NORMAL));
            i++;
        }
        return result;
    }

    public static int getOctalBytes(long value, byte[] buf, int offset, int length) {
        int idx;
        int idx2 = length - 1;
        buf[offset + idx2] = 0;
        int idx3 = idx2 - 1;
        buf[offset + idx3] = 32;
        int idx4 = idx3 - 1;
        if (value == 0) {
            buf[offset + idx4] = TarHeader.LF_NORMAL;
            idx = idx4 - 1;
        } else {
            int idx5 = idx4;
            long val = value;
            while (idx5 >= 0 && val > 0) {
                buf[offset + idx5] = (byte) (((byte) ((int) (7 & val))) + TarHeader.LF_NORMAL);
                val >>= 3;
                idx5--;
            }
            idx = idx5;
        }
        while (idx >= 0) {
            buf[offset + idx] = 32;
            idx--;
        }
        return offset + length;
    }

    public static int getCheckSumOctalBytes(long value, byte[] buf, int offset, int length) {
        getOctalBytes(value, buf, offset, length);
        buf[(offset + length) - 1] = 32;
        buf[(offset + length) - 2] = 0;
        return offset + length;
    }

    public static int getLongOctalBytes(long value, byte[] buf, int offset, int length) {
        byte[] temp = new byte[(length + 1)];
        getOctalBytes(value, temp, 0, length + 1);
        System.arraycopy(temp, 0, buf, offset, length);
        return offset + length;
    }
}
