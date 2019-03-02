package net.haesleinhuepf.imagej.preloader;


import fiji.util.gui.GenericDialogPlus;
import ij.IJ;
import ij.ImagePlus;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import java.io.FileNotFoundException;
import java.util.HashMap;

@Plugin(type = Command.class, menuPath = "File>Import>Preloader")
public class PreloaderCommand implements Command{

    private static HashMap<String, ImagePlusPreloader> loaderMap = new HashMap<String, ImagePlusPreloader>();

    @Override
    public void run() {
        String currentFileName = "";
        String nextFileName = "";
        String loaderId = "1";

        GenericDialogPlus gd = new GenericDialogPlus("ImagePlusPreloader");
        gd.addFileField("Current file", currentFileName);
        gd.addFileField("Next file", nextFileName);
        gd.addStringField("Loader ID", "1");
        gd.addMessage("Leave the loader ID as it is unless you work\nwith several scripts/macros in parallel.");

        gd.showDialog();
        if(gd.wasCanceled()){
            return;
        }

        currentFileName = gd.getNextString();
        nextFileName = gd.getNextString();
        loaderId = gd.getNextString();

        ImagePlusPreloader preloader = null;
        synchronized (loaderMap) {
            if (loaderMap.containsKey(loaderId)) {
                preloader = loaderMap.get(loaderId);
            } else {
                preloader = new ImagePlusPreloader();
                loaderMap.put(loaderId, preloader);
            }
        }

        ImagePlus result = preloader.load(currentFileName, nextFileName);
        if (result != null) {
            result.show();
        } else {
            IJ.log("Error while loading image.");
        }
    }
}
