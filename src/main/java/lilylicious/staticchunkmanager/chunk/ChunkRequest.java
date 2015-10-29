package lilylicious.staticchunkmanager.chunk;

import lilylicious.staticchunkmanager.util.NBTHelper;
import lilylicious.staticchunkmanager.util.csLogger;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class ChunkRequest {

    public File file;
    public int cx;
    public int cy;
    public boolean stream;
    public boolean matchBiomes;
    public InputStream inStream;

    /**
     * 
     * @param file
     * @param x
     * @param y
     * @param extras - Whether or not to match biomes
     * @param 
     */
    public ChunkRequest(File file, int x, int y, boolean... extras) {
        this.file = file;
        this.cx = x;
        this.cy = y;
        this.stream = false;
        this.matchBiomes = extras.length > 0 ? extras[0] : false;
    }

    public ChunkRequest(InputStream is, int x, int y, boolean... extras) throws IOException {
        this.file = File.createTempFile(x + "." + y, "chunk");
        this.file.deleteOnExit();
        try (FileOutputStream fout = new FileOutputStream(this.file)) {
            IOUtils.copy(is, fout);
        }
        this.cx = x;
        this.cy = y;
        this.stream = true;
        this.matchBiomes = extras.length > 0 ? extras[0] : false;
    }

    public NBTTagCompound readFile() throws IOException {
        if (stream) return CompressedStreamTools.readCompressed(inStream);
        else return CompressedStreamTools.read(file);
    }


    public Chunk readChunk(World worldObj, Chunk normalChunk) throws IOException {
        NBTTagCompound nbtCompound = readFile();

        if (!nbtCompound.hasKey("Level", 10)) {
            csLogger.logWarn("Chunk file at " + this.cx + "," + this.cy + " is missing level data, skipping");
            return null;
        } else if (!nbtCompound.getCompoundTag("Level").hasKey("Sections", 9)) {
            csLogger.logWarn("Chunk file at " + this.cx + "," + this.cy + " is missing block data, skipping");
            return null;
        } else {

            if (this.matchBiomes) {
                NBTHelper.matchBiome(nbtCompound, normalChunk);
            }

            Chunk chunk = NBTHelper.readChunkFromNBT(worldObj, nbtCompound.getCompoundTag("Level"));

            if (!chunk.isAtLocation(cx, cy)) {
                chunk = null;
                nbtCompound.getCompoundTag("Level").setInteger("xPos", cx);
                nbtCompound.getCompoundTag("Level").setInteger("zPos", cy);
                // Have to move tile entities since we don't load them at this stage
                NBTTagList tileEntities = nbtCompound.getCompoundTag("Level").getTagList("TileEntities", 10);

                if (tileEntities != null) {
                    for (int te = 0; te < tileEntities.tagCount(); te++) {
                        NBTTagCompound tileEntity = (NBTTagCompound) tileEntities.getCompoundTagAt(te);
                        int x = tileEntity.getInteger("x") - chunk.xPosition * 16;
                        int z = tileEntity.getInteger("z") - chunk.zPosition * 16;
                        tileEntity.setInteger("x", cx * 16 + x);
                        tileEntity.setInteger("z", cy * 16 + z);
                    }
                }

                chunk = NBTHelper.readChunkFromNBT(worldObj, nbtCompound.getCompoundTag("Level"));
            }

            return chunk;
        }
    }


}

