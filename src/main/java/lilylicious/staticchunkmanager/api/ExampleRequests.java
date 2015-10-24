package lilylicious.staticchunkmanager.api;

import lilylicious.staticchunkmanager.chunk.ChunkRequest;

import java.io.IOException;
import java.io.InputStream;

public class ExampleRequests {

    public static void requestChunks() {
        InputStream is;
        
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/stairUpS.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 0, true));
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/snMid.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 1, true));
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/snMid.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 2, true));
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/snMid.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 3, true));
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/stairUpN.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 4, true));
        
    }
}
