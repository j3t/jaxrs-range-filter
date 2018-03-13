package com.github.j3t.jaxrs;

import org.junit.Test;

import java.util.UUID;

import static com.github.j3t.jaxrs.HttpRangeHelper.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

public class HttpRangeHelperTest {

    @Test
    public void testContentLength() {
        assertThat(getContentRange(6, 0, 100), is("bytes 6-5/100"));
        assertThat(getContentRange(6, 1, 100), is("bytes 6-6/100"));
        assertThat(getContentRange(6, 2, 100), is("bytes 6-7/100"));
        assertThat(getContentRange(6, 5, 100), is("bytes 6-10/100"));
    }

    @Test
    public void testGetFrom() {
        assertThat(getFromValue(null), nullValue());
        assertThat(getFromValue(UUID.randomUUID().toString()), nullValue());
        assertThat(getFromValue("bytes=0-"), is(0L));
        assertThat(getFromValue("bytes=0-9"), is(0L));
        assertThat(getFromValue("bytes=1-"), is(1L));
        assertThat(getFromValue("bytes=10-9"), is(10L));
    }

    @Test
    public void testGetTo() {
        assertThat(getToValue(null), nullValue());
        assertThat(getToValue(UUID.randomUUID().toString()), nullValue());
        assertThat(getToValue("bytes=0-"), nullValue());
        assertThat(getToValue("bytes=0-9"), is(9L));
        assertThat(getToValue("bytes=1-"), nullValue());
        assertThat(getToValue("bytes=10-0"), is(0L));
        assertThat(getToValue("bytes=1-100"), is(100L));
    }

    @Test
    public void testIsSatisfiable() {
        assertThat(isSatisfiable(null, 0L), is(false));
        assertThat(isSatisfiable(null, 10L), is(false));
        assertThat(isSatisfiable(UUID.randomUUID().toString(), 0L), is(false));
        assertThat(isSatisfiable(UUID.randomUUID().toString(), 10L), is(false));
        assertThat(isSatisfiable("bytes=0-", 0L), is(false));
        assertThat(isSatisfiable("bytes=0-", 1L), is(true));
        assertThat(isSatisfiable("bytes=0-", 10L), is(true));
        assertThat(isSatisfiable("bytes=0-9", 10L), is(true));
        assertThat(isSatisfiable("bytes=1-0", 10L), is(false));
        assertThat(isSatisfiable("bytes=1-", 10L), is(true));
        assertThat(isSatisfiable("bytes=0-10", 10L), is(false));
        assertThat(isSatisfiable("bytes=9-0", 10L), is(false));
        assertThat(isSatisfiable("bytes=9-0", 100L), is(false));
        assertThat(isSatisfiable("bytes=1-100", 1024 * 1025), is(true));
        assertThat(isSatisfiable("bytes=100-", 100L), is(false));
    }

    @Test
    public void testLength() {
        assertThat(getLength(null, null, 0L, 0L), nullValue());
        assertThat(getLength(null, null, 10L, 5L), nullValue());
        assertThat(getLength(null, null, 0L, 0L), nullValue());
        assertThat(getLength(null, null, 10L, 5L), nullValue());
        assertThat(getLength(0L, null, 0L, 5L), nullValue());
        assertThat(getLength(0L, null, 1L, 5L), is(1L));
        assertThat(getLength(0L, null, 10L, 5L), is(5L));
        assertThat(getLength(0L, 9L, 10L, 5L), is(5L));
        assertThat(getLength(0L, 9L, 10L, 20L), is(10L));
        assertThat(getLength(1L, 0L, 10L, 5L), nullValue());
        assertThat(getLength(1L, null, 10L, 5L), is(5L));
        assertThat(getLength(0L, 10L, 10L, 11L), nullValue());
        assertThat(getLength(9L, 0L, 10L, 5L), nullValue());
        assertThat(getLength(9L, 0L, 100L, 5L), nullValue());
        assertThat(getLength(1L, 100L, 1024 * 1025, 1024 * 1024), is(100L));
        assertThat(getLength(100L, null, 100L, 5L), nullValue());
    }
}