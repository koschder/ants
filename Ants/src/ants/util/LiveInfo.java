package ants.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import pathfinder.entities.Tile;

/**
 * Logger class that writes application logs to a separate log file.
 * 
 * @author kases1,kustl1
 * 
 */
public class LiveInfo {

    private static boolean isFirst = true;

    private static RandomAccessFile liveInfo;

    static {
        try {
            String jsonfile = "logs/additionalInfo.json";
            File f = new File(jsonfile);
            if (f.exists())
                f.delete();
            liveInfo = new RandomAccessFile(new File(jsonfile), "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write a message to the liveInfo file that is used to display additional information in the game replay.
     * 
     * @param turn
     * @param tile
     * @param message
     * @param parameters
     */
    public static void liveInfo(int turn, Tile tile, String message, Object... parameters) {
        try {
            String delimiter = "";
            if (isFirst) {
                liveInfo.write("{".getBytes());
                isFirst = false;
            } else {
                liveInfo.seek(0);
                delimiter = ",";
                liveInfo.seek(liveInfo.length() - 1); // this basically reads n bytes in the file
            }
            String msg = String.format(message, parameters).replace("\"", "'").replace("<r", "&lt;r")
                    .replace("\n", "<br/>");
            String sLiveInfo = String.format("%s\n\"%s#%s#%s\" : \"%s\"", delimiter, turn, tile.getRow(),
                    tile.getCol(), msg);
            // liveInfo.write("\n".getBytes());
            liveInfo.write(sLiveInfo.getBytes());
            liveInfo.write("}".getBytes());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
