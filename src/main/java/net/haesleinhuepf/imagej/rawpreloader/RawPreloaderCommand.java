package net.haesleinhuepf.imagej.rawpreloader;


import fiji.util.gui.GenericDialogPlus;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.ImportDialog;
import ij.io.OpenDialog;
import net.haesleinhuepf.imagej.preloader.ImagePlusPreloader;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import java.io.File;
import java.util.HashMap;

@Plugin(type = Command.class, menuPath = "File>Import>Raw Preloader")
public class RawPreloaderCommand implements Command{

    private static HashMap<String, ImagePlusRawPreloader> loaderMap = new HashMap<String, ImagePlusRawPreloader>();

    @Override
    public void run() {
        String currentFileName = "";
        String nextFileName = "";
        String loaderId = "1";

        GenericDialogPlus gd = new GenericDialogPlus("ImagePlusRawPreloader");
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

        File file = new File(currentFileName);
        String directory = file.getParent();
        String fileName = file.getName();
        ImportDialog d = new ImportDialog(fileName, directory);
        FileInfo fileInfo = d.getFileInfo();
        //d.openImage();


        ImagePlusRawPreloader preloader = null;
        synchronized (loaderMap) {
            if (loaderMap.containsKey(loaderId)) {
                preloader = loaderMap.get(loaderId);
            } else {
                preloader = new ImagePlusRawPreloader();
                loaderMap.put(loaderId, preloader);
            }
        }

        ImagePlus result = preloader.load(currentFileName, nextFileName, fileInfo);
        if (result != null) {
            result.show();
        } else {
            IJ.log("Error while loading image.");
        }
    }
}
