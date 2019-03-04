package net.haesleinhuepf.imagej.rawpreloader;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.plugin.Raw;

import java.io.File;

public class ImagePlusRawPreloader {
    private String currentFileName = "";
    private String nextLoadedImageFileName = "";

    private Object loaderLock = new Object();

    private ImagePlus currentImage = null;
    private ImagePlus nextImage = null;


    private Loader loader = null;

    public ImagePlus load(String currentFileName, String nextFileName, FileInfo fileInfo) {
        synchronized (loaderLock) {
            if (nextLoadedImageFileName.compareTo(currentFileName) != 0) {
                // images was not loaded yet
                if (loader == null || this.currentFileName.compareTo(currentFileName) != 0) { // the image has never been requested before
                    currentImage = openImage(currentFileName, fileInfo);
                    this.currentFileName = currentFileName;
                } else { // if the loader was initialized earlier, wait for it to finish
                    // try again later
                    return sleepload(currentFileName, nextFileName, fileInfo);
                }
            } else {
                // next image was loaded already
                currentImage = nextImage;
                this.currentFileName = nextLoadedImageFileName;
            }

            // preload the next image in a separate thread
            loader = new Loader(nextFileName, fileInfo);
            loader.start();
        }
        return currentImage;
    }

    private ImagePlus sleepload(String currentFileName, String nextFileName, FileInfo fileInfo) {
        try {
            loader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return load(currentFileName, nextFileName, fileInfo);
    }

    private class Loader extends Thread {
        String filenameToload;
        FileInfo fileInfo;

        public Loader(String filenameToload, FileInfo fileInfo) {
            this.filenameToload = filenameToload;
            this.fileInfo = fileInfo;
        }

        @Override
        public void run() {
            synchronized (loaderLock) {
                nextImage = openImage(filenameToload, fileInfo);
                nextLoadedImageFileName = filenameToload;
            }
        }
    }

    private static ImagePlus openImage(String filename, FileInfo fileinfo) {
        return Raw.open(filename, fileinfo);
        //run("Raw...", "open=C:/structure/data/2018-05-23-16-18-13-89-Florence_multisample/stacks/opticsprefused/000000.raw image=[16-bit Unsigned] width=512 height=1024 number=1000 little-endian");
    }
}
