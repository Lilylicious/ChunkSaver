package lilylicious.staticchunkmanager.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.HashMap;
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
        NBTTagCompound targetSection;
        NBTTagCompound highestSection;
        NBTTagList sectionList = new NBTTagList();
        byte[] blocks;
        byte[] add = new byte[4096];

        //All the blocks that exist in the normalChunk
        //Currently assuming that empty blockstorages are still saved
        ExtendedBlockStorage[] exBlockStorage = normalChunk.getBlockStorageArray();

        int blockIndex;
        int targetSectionY;
        int highestSectionY = 0;
        Map<Integer, NBTTagCompound> sectionNums = new HashMap();

        //Saves a map of all pre-existing sections in the prevTag
        for (int k = 0; k < prevTag.getCompoundTag("Level").getTagList("Sections", 10).tagCount(); ++k) {
            NBTTagCompound tag = prevTag.getCompoundTag("Level").getTagList("Sections", 10).getCompoundTagAt(k);
            sectionNums.put((int) tag.getByte("Y"), tag);
            if (tag.getByte("Y") > highestSectionY) highestSectionY = tag.getByte("Y");
        }

        highestSection = sectionNums.get(highestSectionY);
        blocks = highestSection.getByteArray("Blocks");

        //Rest of the level NBT creation
        levelNBT.setByte("V", (byte) 1);
        levelNBT.setInteger("xPos", normalChunk.xPosition);
        levelNBT.setInteger("zPos", normalChunk.zPosition);
        levelNBT.setIntArray("HeightMap", normalChunk.heightMap);
        levelNBT.setBoolean("LightPopulated", false);
        levelNBT.setBoolean("TerrainPopulated", true);

        //List of terrain IDs
        short[] terrainID = new short[]{0, 1, 2, 3, 12, 13};

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                int targetHeight = normalChunk.getHeightValue(x, z);
                targetSectionY = MathUtils.floorDiv(targetHeight, 16);
                
                if (sectionNums.containsKey(targetSectionY)) {
                    targetSection = sectionNums.get(targetSectionY);
                    for (int i = targetSectionY + 2; i <= highestSectionY; i++) {
                        sectionNums.remove(i);
                    }
                    
                } else {
                    //Generates a new section as the target, identical to the highest but at the right spot
                    targetSection = (NBTTagCompound) highestSection.copy();
                    targetSection.setByte("Y", (byte) targetSectionY);
                    sectionNums.put(targetSectionY, targetSection);
                    
                    //Re-makes sections lower than the target with the contents of a normal section from a normal chunk
                    for (int i = targetSectionY - 1; i >= 0; i--) {
                        NBTTagCompound newSection;
                        ExtendedBlockStorage bs = exBlockStorage[i];
                        newSection = newSection(bs);
                        sectionNums.put(i, newSection);
                    }
                }
                
                
                boolean hasAdd = highestSection.hasKey("Add");

                if (hasAdd) {
                    add = highestSection.getByteArray("Add");
                }

                int topBlockHeight = 0;
                short topBlockID = 0;

                for (int y = 15; y >= 0; y--) {

                    //Find topblock
                    blockIndex = y * 16 * 16 + z * 16 + x;
                    short blockID = (short) (blocks[blockIndex] + (hasAdd ? (Nibble4(add, blockIndex)) << 8 : 0));

                    if (ArrayUtils.contains(terrainID, blockID) && blockID != 0) {
                        topBlockHeight = y;
                        topBlockID = blockID;
                        break;
                    }
                }

                if (topBlockID != 0) {
                    while (targetHeight % 16 < topBlockHeight) {
                        // Move it down
                        ExtendedBlockStorage blockStorage = exBlockStorage[targetSectionY];
                        int y = topBlockHeight;
                        blockIndex = y * 16 * 16 + z * 16 + x;
                        int belowBlockIndex = (y > 0 ? (y - 1) : y) * 16 * 16 + z * 16 + x;

                        short belowBlockID = (short) (blocks[belowBlockIndex] + (hasAdd ? (Nibble4(add, belowBlockIndex)) << 8 : 0));

                        if (ArrayUtils.contains(terrainID, topBlockID)
                                && ArrayUtils.contains(terrainID, belowBlockID)
                                && y != 0) {
                            blocks[belowBlockIndex] = blocks[blockIndex];
                            blocks[blockIndex] = 0;
                        }

                        topBlockHeight--;
                    }
                    
                    while (targetHeight % 16 > topBlockHeight) {

                        // Move it up
                        short aboveBlockID = 0;
                        int y = topBlockHeight;
                        blockIndex = y * 16 * 16 + z * 16 + x - 1;
                        int aboveBlockIndex = (y < 15 ? (y + 1) : y) * 16 * 16 + z * 16 + x - 1;
                        if (blockIndex != aboveBlockIndex) {
                            aboveBlockID = (short) (blocks[aboveBlockIndex] + (hasAdd ? (Nibble4(add, aboveBlockIndex)) << 8 : 0));
                        }
                        Byte normalBlockID_a = exBlockStorage[targetSectionY].getBlockLSBArray()[blockIndex];
                        Byte normalBlockID_b = (exBlockStorage[targetSectionY].getBlockMSBArray() != null ? Nibble4(exBlockStorage[targetSectionY].getBlockMSBArray().data, blockIndex) : 0);

                        if (ArrayUtils.contains(terrainID, topBlockID)
                                && ArrayUtils.contains(terrainID, aboveBlockID)
                                && y != 0) {
                            
                            blocks[aboveBlockIndex] = blocks[blockIndex];
                            blocks[blockIndex] = normalBlockID_a;
                            reverseNibble(add, blockIndex, normalBlockID_b);
                        }

                        topBlockHeight++;
                    }
                }
            }
        }


        for (NBTTagCompound tag : sectionNums.values()) {
            sectionList.appendTag(tag);
        }

        levelNBT.setTag("Sections", sectionList);
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

    //VERY UNSURE ABOUT THIS
    private static void reverseNibble(byte[] arr, int index, byte b1) {
        byte i = (byte) (index % 2 == 0 ? (arr[index / 2] & 0x0F) : b1);
        byte i2 = (byte) (index % 2 != 0 ? ((arr[index / 2] >> 4) & 0x0F) : b1);

        arr[index / 2] = (byte) (i | i2);
    }

    private static NBTTagCompound newSection(ExtendedBlockStorage bs) {
        NBTTagCompound newSection = new NBTTagCompound();

        newSection.setByte("Y", (byte) (bs.getYLocation() >> 4 & 255));
        newSection.setByteArray("Blocks", bs.getBlockLSBArray());

        if (bs.getBlockMSBArray() != null) {
            newSection.setByteArray("Add", bs.getBlockMSBArray().data);
        }

        newSection.setByteArray("Data", bs.getMetadataArray().data);
        newSection.setByteArray("BlockLight", bs.getBlocklightArray().data);
        newSection.setByteArray("SkyLight", new byte[bs.getBlocklightArray().data.length]);

        return newSection;
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
