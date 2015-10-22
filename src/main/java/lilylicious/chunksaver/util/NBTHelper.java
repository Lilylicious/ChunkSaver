package lilylicious.chunksaver.util;

import lilylicious.chunksaver.chunk.Chunks;
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
    //Changed the return! EDITED HERE
    public static Chunk checkedReadChunkFromNBT__Async(World worldObj, int cx, int cy, NBTTagCompound nbtCompound) {
        if (!nbtCompound.hasKey("Level", 10)) {
            csLogger.logWarn("Chunk file at " + cx + "," + cy + " is missing level data, skipping");
            return null;
        } else if (!nbtCompound.getCompoundTag("Level").hasKey("Sections", 9)) {
            csLogger.logWarn("Chunk file at " + cx + "," + cy + " is missing block data, skipping");
            return null;
        } else {
            
            matchBiome(nbtCompound);
            
            Chunk chunk = readChunkFromNBT(worldObj, nbtCompound.getCompoundTag("Level"));

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

                chunk = readChunkFromNBT(worldObj, nbtCompound.getCompoundTag("Level"));
            }

            Object[] data = new Object[2];
            data[0] = chunk;
            data[1] = nbtCompound;
            // event is fired in ChunkIOProvider.callStage2 since it must be fired after TE's load.
            // MinecraftForge.EVENT_BUS.post(new ChunkDataEvent.Load(chunk, par4NBTTagCompound));
            return chunk; //Edited here
        }
    }

    /**
     * Reads the data stored in the passed NBTTagCompound and creates a Chunk with that data in the passed World.
     * Returns the created Chunk.
     */
    private static Chunk readChunkFromNBT(World worldObj, NBTTagCompound nbtTag) {
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

    private static void matchBiome(NBTTagCompound nbtTag) {

        NBTTagCompound leftChunk = Chunks.chunkData(nbtTag.getCompoundTag("Level").getInteger("xPos") - 1, nbtTag.getCompoundTag("Level").getInteger("zPos"));
        NBTTagCompound rightChunk = Chunks.chunkData(nbtTag.getCompoundTag("Level").getInteger("xPos") + 1, nbtTag.getCompoundTag("Level").getInteger("zPos"));
        NBTTagCompound topChunk = Chunks.chunkData(nbtTag.getCompoundTag("Level").getInteger("xPos"), nbtTag.getCompoundTag("Level").getInteger("zPos") + 1);
        NBTTagCompound downChunk = Chunks.chunkData(nbtTag.getCompoundTag("Level").getInteger("xPos"), nbtTag.getCompoundTag("Level").getInteger("zPos") - 1);


        List<byte[]> biomeArray = new ArrayList<byte[]>();
        biomeArray.add(leftChunk.getCompoundTag("Level").getByteArray("Biomes"));
        biomeArray.add(rightChunk.getCompoundTag("Level").getByteArray("Biomes"));
        biomeArray.add(topChunk.getCompoundTag("Level").getByteArray("Biomes"));
        biomeArray.add(downChunk.getCompoundTag("Level").getByteArray("Biomes"));

        Map<Byte, Integer> byteMap = new HashMap();

        for (byte[] byteArray : biomeArray) {
            for (Byte b : byteArray) {
                byteMap.put(b, 1 + (byteMap.containsKey(b) ? byteMap.get(b) : 0));
            }
        }

        Byte mostFrequent = null;
        int maxCount = 0;

        for (Map.Entry entry : byteMap.entrySet()) {
            if ((int) entry.getValue() > maxCount) {
                mostFrequent = (Byte)entry.getKey();
                maxCount = (int) entry.getValue();                
            }
        }
        
        byte[] finalByteArray = new byte[256];
        
        for(int i = 0; i < finalByteArray.length; i++){
            finalByteArray[i] = mostFrequent;
        }

        if (nbtTag.getCompoundTag("Level").hasKey("Biomes", 7)) {
            nbtTag.getCompoundTag("Level").setByteArray("Biomes", finalByteArray);
        }

    }


}
