package com.avenger.bmp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

public class JustPureImage {
    int width;
    int height;
    byte[] bitmap;
    Depth depth;

    /**
     * Create and return JustPureImage from outside image (Like a png,
     * jpeg, jpg and bmp)
     * <p>
     *
     * @param path  path to image (May be absolute)
     * @param depth image depth
     * @return JustPureImage
     */
    static JustPureImage fromImage(String path, Depth depth) throws IOException {
        if (!new File(path).exists()) throw new FileNotFoundException("File " + path + " not exists");

        BufferedImage bmp = ImageIO.read(new File(path));
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        byte[] output = new byte[width * height * depth.bytes + 5];
        output[0] = (byte) depth.bytes; //Encode depth (1 - 2)
        output[1] = (byte) (((short) width) >> 8); //Encode first byte of width
        output[2] = (byte) (((short) width) & 0xFF);
        output[3] = (byte) (((short) height) >> 8); //Encode first byte of height
        output[4] = (byte) (((short) height) & 0xFF);

        int off = 5;
        int x = 0, y = height - 1;
        for (int i = 0; i < width * height; i++) {
            byte[] c = depth.getColor(bmp.getRGB(x, y));
            if (depth.equals(Depth.TYPE_3BYTE)) {
                output[off] = c[0];
                output[off + 1] = c[1];
                output[off + 2] = c[2];
            } else {
                output[off] = c[0];
                output[off + 1] = c[1];
            }
            x++;
            if (x == width) {
                x = 0;
                y--;
            }
            off += depth.bytes;
        }
        return new JustPureImage(width, height, output, depth);
    }

    static JustPureImage load(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) throw new FileNotFoundException("File is non exists");
        byte[] bytes = Files.readAllBytes(file.toPath());
        if (bytes.length < 7) throw new NoSuchFileException("File is not JPI");
        int width = (bytes[1] & 0xFF) << 8 | (bytes[2] & 0xFF);
        int height = (bytes[3] & 0xFF) << 8 | (bytes[4] & 0xFF);
        int depth = bytes[0] & 0xFF;

        return new JustPureImage(width, height, bytes, Depth.get(depth));
    }

    public JustPureImage(int width, int height, byte[] bitmap, Depth depth) {
        this.width = width;
        this.height = height;
        this.bitmap = bitmap;
        this.depth = depth;
    }

    /**
     * Save this JPI image to file
     * <p>
     *
     * @param name name image (WITHOUT .jpi)
     */
    public void save(String name) throws IOException {
        FileOutputStream fos = new FileOutputStream(name + ".jpi");
        fos.write(bitmap);
        fos.close();
    }

    /**
     * Save this JPI image to classic image files
     * Like a png, jpg
     * <p>
     *
     * @param path       path to save your image
     * @param formatName format name for save
     */
    public void saveAsPng(String path, String formatName) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int y = height - 1;
        int x = 0;
        for (int b = 5; b < bitmap.length; b += depth.bytes) {
            if (x >= width) {
                y--;
                x = 0;
            }
            image.setRGB(x, y, depth.getRGB(bitmap, b)); //RGB888
            x++;
        }
        ImageIO.write(image, formatName, new File(path));
    }
}