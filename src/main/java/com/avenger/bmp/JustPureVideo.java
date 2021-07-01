package com.avenger.bmp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

public class JustPureVideo {
    int width;
    int height;
    Depth depth;
    JustPureImage[] keys;

    /**
     * Create and return JustPureVideo from outside images (Like a png,
     * jpeg, jpg bmp, JPI)
     * <p>
     *
     * @param path  path to images folder (May be absolute)
     * @param depth use this depth if folder constains not JPI files
     * @return JustPureVideo
     */
    static JustPureVideo loadVideo(String path, Depth depth) throws Exception {
        File folder = new File(path);
        if (!folder.exists()) throw new FileNotFoundException("Directory is not found");
        if (!folder.isDirectory())
            throw new NoSuchFileException("This path is not a directory!");

        JustPureImage[] keys = new JustPureImage[folder.list().length];
        int k = 0;
        for (File im : folder.listFiles()) {
            if (!im.isDirectory()) {
                if (im.getAbsolutePath().endsWith(".jpi")) {
                    keys[k] = JustPureImage.load(im.getPath());
                } else {
                    keys[k] = JustPureImage.fromImage(im.getPath(), depth);
                }
                k++;
            }
        }

        validate(keys);

        return new JustPureVideo(keys[0].width, keys[0].height, keys[0].depth, keys);
    }


    /**
     * Validate images
     * <p>
     *
     * @param arr Images array
     */
    private static void validate(JustPureImage[] arr) throws Exception {
        if (arr.length < 1) throw new Exception("Images array length less than 1");
        JustPureImage ft = arr[0];
        for (JustPureImage img : arr) {
            if (img.width != ft.width || img.height != ft.height || !img.depth.equals(ft.depth)) {
                throw new Exception("Images must be one width, height and depth!");
            }
        }
    }

    public JustPureVideo(int width, int height, Depth depth, JustPureImage[] keys) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.keys = keys;
    }

    /**
     * Save this video to file.jpv
     * <p>
     *
     * @param path path to saved video
     */
    void save(String path) throws IOException {
        byte[] bytes = new byte[width * height * depth.bytes * keys.length + 7];
        bytes[0] = (byte) depth.bytes;
        bytes[1] = (byte) (((short) width) >> 8); //Encode first byte of width
        bytes[2] = (byte) (((short) width) & 0xFF);
        bytes[3] = (byte) (((short) height) >> 8); //Encode first byte of height
        bytes[4] = (byte) (((short) height) & 0xFF);
        bytes[5] = (byte) (((short) keys.length) >> 8); //Encode first byte of keys count
        bytes[6] = (byte) (((short) keys.length) & 0xFF);

        int i = 0;
        for (JustPureImage img : keys) {
            for (int j = 5; j < img.bitmap.length; j++) {
                bytes[i + j] = img.bitmap[j];
            }
            i += img.bitmap.length - 5;
        }


        FileOutputStream fos = new FileOutputStream(path);
        fos.write(bytes);
        fos.close();
    }


    /**
     * Save this video to folderPath folder as images.jpi
     * <p>
     *
     * @param folderPath path to saved frames
     */
    void saveAsFrames(String folderPath) throws IOException {
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdir();
        if (!folder.isDirectory())
            throw new NoSuchFileException("This path is not a directory!");

        int i = 0;
        for (JustPureImage img : keys) {
            img.save(folderPath + "frame-" + i);
            i++;
        }
    }

    /**
     * Save this video to folderPath folder as images.png
     * <p>
     *
     * @param folderPath path to saved frames
     */
    void saveAsFramesImages(String folderPath) throws IOException {
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdir();
        if (!folder.isDirectory())
            throw new NoSuchFileException("This path is not a directory!");

        int i = 0;
        for (JustPureImage img : keys) {
            img.saveAsPng(folderPath + "frame-" + i + ".png", "PNG");
            i++;
        }
    }
}
