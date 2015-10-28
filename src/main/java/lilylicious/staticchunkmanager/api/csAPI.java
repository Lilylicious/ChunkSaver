package lilylicious.staticchunkmanager.api;

import cpw.mods.fml.common.Loader;
import lilylicious.staticchunkmanager.chunk.ChunkRequest;
import lilylicious.staticchunkmanager.chunk.Chunks;
import lilylicious.staticchunkmanager.util.csLogger;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class csAPI {


    /**
     * Requests the specified ChunkRequest to be
     * added to the map of requested chunks.
     * @param cReq
     */
    public static void requestChunk(ChunkRequest cReq) {
            Chunks.addChunk(cReq);
    }
}
