package lilylicious.staticchunkmanager.api;

import lilylicious.staticchunkmanager.chunk.ChunkRequest;

import java.io.IOException;
import java.io.InputStream;

public class ExampleRequests {

    public static void requestChunks() {
        InputStream is;
        
/*        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/stairUpS.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 0, true, true));
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/snMid.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 1, true, true));
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/snMid.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 2, true, false));
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/snMid.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 3, true, true));
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/stairUpN.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 4, true, true));
        */
        
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 0, true, true));
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 1, true, false));
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 2, false, true));
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 3, false, false));
    }
}
