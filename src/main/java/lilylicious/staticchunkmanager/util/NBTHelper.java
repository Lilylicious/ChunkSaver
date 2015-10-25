package lilylicious.staticchunkmanager.util;

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
            NBTTagCompound section = nbttaglist.getCompoundTagAt(k);
            byte b1 = section.getByte("Y");
            ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(b1 << 4, flag);
            extendedblockstorage.setBlockLSBArray(section.getByteArray("Blocks"));

            if (section.hasKey("Add", 7)) {
                extendedblockstorage.setBlockMSBArray(new NibbleArray(section.getByteArray("Add"), 4));
            }

            extendedblockstorage.setBlockMetadataArray(new NibbleArray(section.getByteArray("Data"), 4));
            extendedblockstorage.setBlocklightArray(new NibbleArray(section.getByteArray("BlockLight"), 4));

            if (flag) {
                extendedblockstorage.setSkylightArray(new NibbleArray(section.getByteArray("SkyLight"), 4));
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

    public static NBTTagCompound matchHeight(NBTTagCompound prevTag, Chunk normalChunk, boolean regenOre) {
        NBTTagCompound newNBT = new NBTTagCompound();
        NBTTagCompound levelNBT = new NBTTagCompound();
        NBTTagList sectionList = new NBTTagList();
        Byte[] oldBlocks = new Byte[4096];
        Byte[] blocks = new Byte[4096];

        
        //All the blocks that exist in the normalChunk
        //Currently assuming that empty blockstorages are still saved
        ExtendedBlockStorage[] exBlockStorage = normalChunk.getBlockStorageArray();

        int blockIndex;
        int targetSectionY;
        Map<Integer, NBTTagCompound> sectionNums = new HashMap();
        List<NBTTagCompound> newSections = new ArrayList();

        //Saves a map of all pre-existing sections in the prevTag
        for (int k = 0; k < prevTag.getCompoundTag("Level").getTagList("Sections", 10).tagCount(); ++k) {
            NBTTagCompound tag = prevTag.getCompoundTag("Level").getTagList("Sections", 10).getCompoundTagAt(k);
            sectionNums.put((int) tag.getByte("Y"), tag);
        }
        
        //Currently recreates each section that either exists in the prevtag or normalchunk
        //If prevtag section exists, use that. If not, use normalchunk.
        //Probably needs rethinking
        for (int k = 0; k < 16; ++k) {
            NBTTagCompound tag = new NBTTagCompound();
            boolean containsKey = sectionNums.containsKey(k);
            
            if (exBlockStorage[k] != null || containsKey) {
                tag.setByte("Y", (byte) k);
                tag.setByteArray("Blocks", containsKey ? sectionNums.get(k).getByteArray("Blocks") : exBlockStorage[k].getBlockLSBArray());
                if (containsKey ? sectionNums.get(k).hasKey("Add") : exBlockStorage[k].getBlockMSBArray() != null) {
                    tag.setByteArray("Add", containsKey ? sectionNums.get(k).getByteArray("Add") : exBlockStorage[k].getBlockMSBArray().data);
                }
                tag.setByteArray("Data", containsKey ? sectionNums.get(k).getByteArray("Data") : exBlockStorage[k].getMetadataArray().data);

                newSections.add(tag);
            }
        }
        //Rest of the level NBT creation
        levelNBT.setInteger("xPos", normalChunk.xPosition);
        levelNBT.setInteger("zPos", normalChunk.zPosition);
        levelNBT.setIntArray("HeightMap", normalChunk.heightMap);
        //levelNBT.setBoolean("TerrainPopulated", !populateTerrain);

        //Planned spot for the block replacement.
        //We want a byte array of the normal chunk, then
        //somehow we want to match the dirt/sand/stone ground level
        //to the equivalent in the new chunk, making any structures
        //move into a new chunk
        //
        //Maybe we want to do this differently.
        //Current thinking is that we look through all 4096
        //blocks in a section, then through some logic we transpose
        //the new section onto the old, retaining height data
        //but replacing blocks.
        
        //If newBlock == dirt/stone/sand && oldBlock+1Y == air && oldBlock != air, oldBlock = newBlock
        //Maybe
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 16; y++) {
                    blockIndex = y * 16 * 16 + z * 16 + x;
                    targetSectionY = MathUtils.floorDiv(normalChunk.getHeightValue(x, z), 16);

                    //What section do we actually care about here?
                    int section = 1;
                    Byte normalBlockID_a = exBlockStorage[section].getBlockLSBArray()[blockIndex];
                    Byte normalBlockID_b = Nibble4(exBlockStorage[section].getBlockMSBArray().data, blockIndex);
                    short normalBlockIDFull = (short) (normalBlockID_a + (normalBlockID_b << 8));

                }

            }
        }


        //Completion of the newNBT tag
        newNBT.setTag("Level", levelNBT);

        return newNBT;
    }

    //Gets the Add array of half-bytes that complete the block ID if present
    private static byte Nibble4(byte[] arr, int index) {
        int i = arr[index / 2] & 0x0F;
        int i2 = (arr[index / 2] >> 4) & 0x0F;
        byte b = index % 2 == 0 ? (byte) i : (byte) i2;
        return b;
    }
    
    
    
    
    /* //Kept around for reference purposes
        public static NBTTagCompound matchHeight(NBTTagCompound prevTag, Chunk normalChunk, boolean regenOre) {

        int heightGoal;
        int sectionTarget;
        int highestSectionY = 0;
        boolean targetSectionFound;
        NBTTagCompound highestSection = null;
        NBTTagCompound targetSection = null;
        NBTTagCompound nbtTag = (NBTTagCompound) prevTag.copy();

        ExtendedBlockStorage[] exBlockStorage = normalChunk.getBlockStorageArray();

        NBTTagList sectionList = nbtTag.getCompoundTag("Level").getTagList("Sections", 10);


        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                targetSectionFound = false;
                heightGoal = normalChunk.getHeightValue(x, y);
                sectionTarget = MathUtils.floorDiv(heightGoal, 16);

                // Finds the highest section and target section
                for (int k = 0; k < sectionList.tagCount(); ++k) {
                    NBTTagCompound section = sectionList.getCompoundTagAt(k);
                    byte b1 = section.getByte("Y");

                    if (b1 == sectionTarget) {
                        sectionList.removeTag(k);
                    }

                    if (b1 > highestSectionY) {
                        highestSectionY = b1;
                        highestSection = section;
                    }

                }
                    targetSection = new NBTTagCompound();

                //Set targetsection equal to the highest section and changes the Y
                if (highestSection != null && targetSection != null) {
                    targetSection.setByte("Y", (byte)(sectionTarget));
                    targetSection.setByteArray("Blocks", highestSection.getByteArray("Blocks"));
                    targetSection.setByteArray("Data", highestSection.getByteArray("Data"));
                    targetSection.setByteArray("SkyLight", highestSection.getByteArray("Skylight"));
                    targetSection.setByteArray("BlockLight", highestSection.getByteArray("Blocklight"));
                    sectionList.appendTag(targetSection);
                }


                //Deletes sections above the targetsection
                if (sectionTarget < highestSectionY) {
                    //Move down
                    for (int k = 0; k < sectionList.tagCount(); ++k) {
                        NBTTagCompound section = sectionList.getCompoundTagAt(k);
                        if (section.getByte("Y") > sectionTarget + 1) {

                            nbtTag.getCompoundTag("Level").getTagList("Sections", 10).removeTag(k);
                        }
                    }

                }


                //This code fills sections underneath the targetsection with the block data from normalchunk
                //This not only fills in the space below the surface of the new highest level, it also
                //does normal oregen. It only keeps the target section (the 16x16x16 chunk of the chunk
                //that is the highest Y level in the entire chunk)
                if (regenOre) {
                    for (int k = 0; k < sectionList.tagCount(); ++k) {
                        NBTTagCompound section = sectionList.getCompoundTagAt(k);
                        byte b1 = section.getByte("Y");
                        if (b1 < sectionTarget) {

                            ExtendedBlockStorage blockstorage = exBlockStorage[b1];

                            section.setByteArray("Blocks", blockstorage.getBlockLSBArray());

                            if (blockstorage.getBlockMSBArray() != null) {
                                section.setByteArray("Add", blockstorage.getBlockMSBArray().data);
                            }
                            section.setByteArray("Data", blockstorage.getMetadataArray().data);
                        }
                    }

                    //Need an alternative here to the method that regens ore.
                    //Needs to avoid filling in intentional caves/buildings with large empty space.
                    //Could search through blocks and try to find "interesting" structures
                    //Difficult to determine what is interesting and what isn't


                }
                //Match terrain using air as delete
                //Find top block in the current section, move it down to the top block
                //in the normalchunk

                //Actually, find the top block in normal chunk, then find the top block in
                //current section, set blocks to air until the positions are the same then
                //set that block to the top block in current section
            }
        }

        return nbtTag;
    } */


}
