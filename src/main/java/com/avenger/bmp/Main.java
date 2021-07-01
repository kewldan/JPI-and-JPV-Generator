package com.avenger.bmp;

public class Main {
    public static void main(String args[]) throws Exception {
        JustPureVideo vid = JustPureVideo.loadVideo("./keys/", Depth.TYPE_2BYTE);
        vid.save("./vid.jpv");
        vid.saveAsFrames("./keysIm/");
        vid.saveAsFramesImages("./keysIm2/");
    }
}
