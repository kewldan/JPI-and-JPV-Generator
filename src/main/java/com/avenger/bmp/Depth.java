package com.avenger.bmp;

public enum Depth {
    TYPE_3BYTE(3),
    TYPE_2BYTE(2);

    public int bytes;

    Depth(int bytes) {
        this.bytes = bytes;
    }

    static Depth get(int bytes) {
        for (Depth d : values()) {
            if (d.bytes == bytes) return d;
        }
        return null;
    }

    byte[] getColor(int rgb) {
        if (bytes == 3) {
            return new byte[]{
                    (byte) (rgb >> 16 & 0xFF),
                    (byte) (rgb >> 8 & 0xFF),
                    (byte) (rgb & 0xFF)
            };
        } else if (bytes == 2) {
            int r = rgb >> 16 & 0xFF;
            int g = rgb >> 8 & 0xFF;
            int b = rgb & 0xFF;
            short col16num = (short) (((r & 248) << 8) | ((g & 252) << 3) | (b >> 3));
            byte x = (byte) (col16num >> 8 & 0xFF);
            byte y = (byte) (col16num & 0xFF);
            return new byte[]{
                    x, y
            };
        }
        return null;
    }

    int getRGB(byte[] bigarr, int offset) {
        if (bytes == 3) {
            return (
                    (bigarr[offset] & 0xff) << 16) |
                    ((bigarr[offset + 1] & 0xff) << 8) |
                    (bigarr[offset + 2] & 0xff);
        } else if (bytes == 2) {
            int col16num = (bigarr[offset] & 0xFF) << 8 | (bigarr[offset + 1] & 0xFF);
            int r16 = col16num >> 11 & 31;
            int g16 = col16num >> 5 & 63;
            int b16 = col16num & 31;
            int r = (r16 * 527 + 23) >> 6;
            int g = (g16 * 259 + 33) >> 6;
            int b = (b16 * 527 + 23) >> 6;
            return r << 16 | g << 8 | b;
        }
        return -1;
    }
}
