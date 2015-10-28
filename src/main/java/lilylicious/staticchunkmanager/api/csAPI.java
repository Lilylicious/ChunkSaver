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
     * added to the map of globally requested chunks.
     * @param cReq
     */
    public static void requestChunk(ChunkRequest cReq) {

        if (!cReq.stream && !FilenameUtils.getExtension(cReq.file.getPath()).equals("nbt")) {
            csLogger.logWarn("Mod %s tried to load an invalid chunk file.", Loader.instance().activeModContainer().getModId());
        } else if (cReq.stream || cReq.file.exists()) {
            Chunks.addChunk(cReq);
            csLogger.logInfo("Mod %s successfully requested a chunk.", Loader.instance().activeModContainer().getModId());
        } else {
            csLogger.logWarn("Specified file does not exist.");
        }

    }

    /**
     * Requests the specified ChunkRequest to be 
     * added to the map of requested chunks for the
     * current world. This is useful if you are
     * implementing your own per world randomization.
     */
    public static void requestWorldChunk(ChunkRequest cReq) {

        if (!cReq.stream && !FilenameUtils.getExtension(cReq.file.getPath()).equals("nbt")) {
            csLogger.logWarn("Mod %s tried to load an invalid world chunk file.", Loader.instance().activeModContainer().getModId());
        } else if (cReq.stream && cReq.streampath == null) {
            csLogger.logWarn("Mod %s did not specify a stream path, this is required for streamed world chunks.", Loader.instance().activeModContainer().getModId());
        } else if (cReq.stream || cReq.file.exists()) {
            Chunks.addWorldChunk(cReq);
            csLogger.logInfo("Mod %s successfully requested a world chunk.", Loader.instance().activeModContainer().getModId());
        } else {
            csLogger.logWarn("Specified world chunk file does not exist.");
        }

    }
}
