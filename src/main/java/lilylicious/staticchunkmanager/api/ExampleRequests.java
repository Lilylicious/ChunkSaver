package lilylicious.staticchunkmanager.api;

import lilylicious.staticchunkmanager.chunk.ChunkRequest;

import java.io.IOException;
import java.io.InputStream;

public class ExampleRequests {

    public static void requestChunks() {
        InputStream is;
        
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 0, true));
        is = ExampleRequests.class.getResourceAsStream("/assets/staticchunkmanager/storedchunks/test.nbt");
        csAPI.requestChunk(new ChunkRequest(is, 0, 1, false));
        
    }
}
