package lilylicious.chunksaver.chunk;


import lilylicious.chunksaver.util.csLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.chunk.storage.RegionFile;

import java.awt.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Chunks {

    public static Map<Point, NBTTagCompound> chunkMap = new HashMap();

    public static void addChunk(File file, int cx, int cy) {
        NBTTagCompound nbt = null;
        boolean missingData = false;

        try {
            nbt = CompressedStreamTools.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!nbt.hasKey("Level", 10)) {
            csLogger.logWarn("Chunk file at " + cx + "," + cy + " is missing level data, skipping");
            missingData = true;
        } else if (!nbt.getCompoundTag("Level").hasKey("Sections", 9)) {
            csLogger.logWarn("Chunk file at " + cx + "," + cy + " is missing block data, skipping");
            missingData = true;
        }

        if (nbt != null && !missingData) {
            chunkMap.put(new Point(cx, cy), nbt);
        }
    }

    public static NBTTagCompound chunkData(int cx, int cy) {
        NBTTagCompound chunkNBT = null;

        int regionX = Math.floorDiv(cx, 32);
        int regionZ = Math.floorDiv(cy, 32);

        IntegratedServer intServ = Minecraft.getMinecraft().getIntegratedServer();

        File worldFolder = new File("saves\\" + intServ.getFolderName());
        RegionFile region = new RegionFile(new File(worldFolder, "\\region\\r." + regionX + "." + regionZ + ".mca"));

        try {
            // chunkX - regionX*32
            DataInputStream datainputstream = region.getChunkDataInputStream(cx - (regionX * 32), cy - (regionZ * 32));
            if (datainputstream == null) {
                csLogger.logWarn("Failed to fetch input stream");
            } else {
                chunkNBT = CompressedStreamTools.read(datainputstream);
            }
            datainputstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chunkNBT;
    }


}
