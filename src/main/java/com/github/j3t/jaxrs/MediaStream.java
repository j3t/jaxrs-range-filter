package com.github.j3t.jaxrs;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

public class MediaStream implements StreamingOutput {
    private final Path path;
    private final long offset;
    private final long length;


    public MediaStream(Path path, long offset, long length) {
        this.path = path;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public void write(OutputStream output) throws WebApplicationException, IOException {
        try (FileChannel inputChannel = FileChannel.open(path)) {
            try (WritableByteChannel outputChannel = Channels.newChannel(output)) {
                inputChannel.transferTo(offset, length, outputChannel);
            }
        }
    }

}
