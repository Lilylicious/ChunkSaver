package lilylicious.staticchunkmanager.util;

import lilylicious.staticchunkmanager.chunk.Chunks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NBTHelper {

    /**
     * Reads the data stored in the passed NBTTagCompound and creates a Chunk with that data in the passed World.
     * Returns the created Chunk.
     */
    public static Chunk readChunkFromNBT(World worldObj, NBTTagCompound nbtTag) {
        int i = nbtTag.getInteger("xPos");
        int j = nbtTag.getInteger("zPos");
        Chunk chunk = new Chunk(worldObj, i, j);
        chunk.heightMap = nbtTag.getIntArray("HeightMap");
        chunk.isTerrainPopulated = nbtTag.getBoolean("TerrainPopulated");
        chunk.isLightPopulated = nbtTag.getBoolean("LightPopulated");
        chunk.inhabitedTime = nbtTag.getLong("InhabitedTime");
        NBTTagList nbttaglist = nbtTag.getTagList("Sections", 10);
        byte b0 = 16;
        ExtendedBlockStorage[] aextendedblockstorage = new ExtendedBlockStorage[b0];
        boolean flag = !worldObj.provider.hasNoSky;

        for (int k = 0; k < nbttaglist.tagCount(); ++k) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(k);
            byte b1 = nbttagcompound1.getByte("Y");
            ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(b1 << 4, flag);
            extendedblockstorage.setBlockLSBArray(nbttagcompound1.getByteArray("Blocks"));

            if (nbttagcompound1.hasKey("Add", 7)) {
                extendedblockstorage.setBlockMSBArray(new NibbleArray(nbttagcompound1.getByteArray("Add"), 4));
            }

            extendedblockstorage.setBlockMetadataArray(new NibbleArray(nbttagcompound1.getByteArray("Data"), 4));
            extendedblockstorage.setBlocklightArray(new NibbleArray(nbttagcompound1.getByteArray("BlockLight"), 4));

            if (flag) {
                extendedblockstorage.setSkylightArray(new NibbleArray(nbttagcompound1.getByteArray("SkyLight"), 4));
            }

            extendedblockstorage.removeInvalidBlocks();
            aextendedblockstorage[b1] = extendedblockstorage;
        }

        chunk.setStorageArrays(aextendedblockstorage);

        if (nbtTag.hasKey("Biomes", 7)) {
            chunk.setBiomeArray(nbtTag.getByteArray("Biomes"));
        }

        // End this method here and split off entity loading to another method
        return chunk;
    }

    private static void matchSections(NBTTagCompound nbtTag) {

    }

    public static void matchBiome(NBTTagCompound nbtTag, Chunk normalChunk) {

        byte[] byteArray = normalChunk.getBiomeArray();

        byte[] finalByteArray = new byte[256];

        finalByteArray = byteArray.clone();

        if (nbtTag.getCompoundTag("Level").hasKey("Biomes", 7)) {
            nbtTag.getCompoundTag("Level").setByteArray("Biomes", finalByteArray);
        }

    }


}
