package lilylicious.staticchunkmanager.chunk;

import lilylicious.staticchunkmanager.api.csAPI;
import lilylicious.staticchunkmanager.events.WorldChunkRegEvent;

import net.minecraft.world.World;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Iterator;

public class csWorldChunkManager {

    public static void handleWorldChunks(World world)
    {
        try {
            Chunks.worldChunkMap.clear();

            String fileversion = "v1";

            File worldchunksave = new File(world.getSaveHandler().getWorldDirectory() + "/scmworldchunks.txt");

            if (worldchunksave.exists()) {
                loadFinal(worldchunksave);
            } else {
                MinecraftForge.EVENT_BUS.post(new WorldChunkRegEvent(world));

                worldchunksave.createNewFile();

                FileWriter fw = new FileWriter(worldchunksave.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(fileversion);
                bw.newLine();

                Iterator entries = Chunks.worldChunkMap.entrySet().iterator();

                while (entries.hasNext()) {
                    Entry entry = (Entry)entries.next();
                    ChunkRequest chunkrequest = (ChunkRequest)entry.getValue();
                    Point point = (Point)entry.getKey();

                    bw.write(String.valueOf(chunkrequest.stream));
                    bw.newLine();
                    if (chunkrequest.stream) {
                        bw.write(chunkrequest.streampath);
                        bw.newLine();
                    } else {
                        bw.write(chunkrequest.file.getPath());
                        bw.newLine();
                    }
                    bw.write(String.valueOf(point.x));
                    bw.newLine();
                    bw.write(String.valueOf(point.y));
                    bw.newLine();
                    bw.write(String.valueOf(chunkrequest.matchBiomes));

                    if (entries.hasNext()) {
                        bw.newLine();
                    }
                }

                bw.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void loadFinal(File worldchunksave)
    {
        try {
            FileReader fr = new FileReader(worldchunksave.getAbsoluteFile());
            BufferedReader br = new BufferedReader(fr);

			boolean stream;
			File file = null;
			InputStream inStream = null;
			String streampath = "";
			int posx;
			int posy;
			boolean matchBiomes;

			String linecheck = "";

            br.readLine(); //The first line is the version so we skip it

            while ((linecheck = br.readLine()) != null) {
                stream = Boolean.valueOf(linecheck);

                if (stream) {
                    streampath = br.readLine();
                    inStream = csWorldChunkManager.class.getResourceAsStream(streampath);
                } else {
                    file = new File(br.readLine());
                }

                posx = Integer.parseInt(br.readLine());
                posy = Integer.parseInt(br.readLine());
                matchBiomes = Boolean.valueOf(br.readLine());

                //System.out.println(stream + " " + streampath + " " + posx + " " + posy + " " + matchBiomes);

                if (stream) {
                    csAPI.requestWorldChunk(new ChunkRequest(inStream, streampath, posx, posy, matchBiomes));
                } else {
                    csAPI.requestWorldChunk(new ChunkRequest(file, posx, posy, matchBiomes));
                }

                
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
