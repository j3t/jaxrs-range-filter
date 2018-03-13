package com.github.j3t.jaxrs;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllBytes;
import static java.util.Arrays.copyOfRange;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MediaStreamTest {

    private Path path;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        // given path to the MediaStream.class file
        path = Paths.get(MediaStreamTest.class.getResource(MediaStream.class.getSimpleName() + ".class").toURI());
    }

    @Test
    public void testGetLengthFromFile() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new MediaStream(path, 0, Files.size(path)).write(outputStream);

        assertThat(outputStream.toByteArray(), is(readAllBytes(path)));
    }

    @Test
    public void testGetLengthFromRange() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new MediaStream(path, 0, 100).write(outputStream);

        assertThat(outputStream.toByteArray(), is(copyOfRange(readAllBytes(path), 0, 100)));
    }

    @Test
    public void testGetLengthFromRangeToNotSpecified() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new MediaStream(path, 100, 1).write(outputStream);

        assertThat(outputStream.toByteArray(), is(copyOfRange(readAllBytes(path), 100, 101)));
    }

}