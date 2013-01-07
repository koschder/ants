package ants.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import api.entities.Tile;

/**
 * Logger class that writes extra info for the visualizer to a separate log file.
 * 
 * @author kases1, kustl1
 * 
 */
public class LiveInfo {

    private static boolean isFirst = true;

    private static RandomAccessFile liveInfo;

    private static boolean enabled = true;

    static {
        if (enabled) {
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
        if (!enabled)
            return;
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
            message = String.format(message, parameters);
            String msg = message.replace("\"", "'").replace("<r", "&lt;r").replace("\n", "<br/>");
            String sLiveInfo = String.format("%s\n\"%s#%s#%s#%s\" : \"%s\"", delimiter, liveInfo.length(), turn,
                    tile.getRow(), tile.getCol(), msg);
            // liveInfo.write("\n".getBytes());
            liveInfo.write(sLiveInfo.getBytes());
            liveInfo.write("}".getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Close the liveInfo file
     */
    public static void close() {
        if (!enabled)
            return;
        try {
            liveInfo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
