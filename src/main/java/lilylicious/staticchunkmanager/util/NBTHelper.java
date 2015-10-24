package lilylicious.staticchunkmanager.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

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

    //Pass it a level nbtTag
    public static void matchHeight(NBTTagCompound nbtTag, Chunk normalChunk, boolean regenOre) {

        int heightGoal;
        int sectionTarget;
        int highestSectionY = 0;
        NBTTagCompound highestSection = null;
        NBTTagCompound targetSection = null;

        ExtendedBlockStorage[] exBlockStorage = normalChunk.getBlockStorageArray();

        NBTTagList sectionList = nbtTag.getTagList("Sections", 10);


        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                heightGoal = normalChunk.getHeightValue(x, y);
                sectionTarget = MathUtils.floorDiv(heightGoal, 16);

                // Finds the highest section and target section
                for (int k = 0; k < sectionList.tagCount(); ++k) {
                    NBTTagCompound section = sectionList.getCompoundTagAt(k);
                    byte b1 = section.getByte("Y");

                    if (b1 == sectionTarget) {
                        targetSection = section;
                    }

                    if (b1 > highestSectionY) {
                        highestSectionY = b1;
                        highestSection = section;
                    }

                }

                //Set targetsection equal to the highest section and changes the Y
                if (highestSection != null && targetSection != null && highestSection != targetSection) {
                    targetSection = (NBTTagCompound) highestSection.copy();
                    targetSection.setByte("Y", (byte) sectionTarget);
                }


                //Deletes sections above the targetsection
                if (sectionTarget < highestSectionY) {
                    //Move down
                    for (int k = 0; k < sectionList.tagCount(); ++k) {
                        NBTTagCompound section = sectionList.getCompoundTagAt(k);
                        if (section.getByte("Y") > sectionTarget) {

                            nbtTag.getTagList("Sections", 10).removeTag(k);
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
    }


}
