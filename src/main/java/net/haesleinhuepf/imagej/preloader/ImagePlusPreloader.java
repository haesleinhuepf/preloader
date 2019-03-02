package net.haesleinhuepf.imagej.preloader;

import ij.IJ;
import ij.ImagePlus;

public class ImagePlusPreloader {
    private String currentFileName = "";
    private String nextLoadedImageFileName = "";

    private Object loaderLock = new Object();

    private ImagePlus currentImage = null;
    private ImagePlus nextImage = null;


    private Loader loader = null;

    public ImagePlus load(String currentFileName, String nextFileName) {
        synchronized (loaderLock) {
            if (nextLoadedImageFileName.compareTo(currentFileName) != 0) {
                // images was not loaded yet
                if (loader == null) { // the image has never been requested before
                    currentImage = IJ.openImage(currentFileName);
                    this.currentFileName = currentFileName;
                } else { // if the loader was initialized earlier, wait for it to finish
                    // try again later
                    return sleepload(currentFileName, nextFileName);
                }
            } else {
                // next image was loaded already
                currentImage = nextImage;
                this.currentFileName = nextLoadedImageFileName;
            }

            // preload the next image in a separate thread
            loader = new Loader(nextFileName);
            loader.start();
        }
        return currentImage;
    }

    private ImagePlus sleepload(String currentFileName, String nextFileName) {
        IJ.log("Sleepload: loader active " + loader.isAlive());

        try {
            loader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return load(currentFileName, nextFileName);
    }

    private class Loader extends Thread {
        String filenameToload;

        public Loader(String filenameToload) {
            this.filenameToload = filenameToload;
        }

        @Override
        public void run() {
            synchronized (loaderLock) {
                nextImage = IJ.openImage(filenameToload);
                nextLoadedImageFileName = filenameToload;
            }
        }
    }
}
