package com.github.j3t.jaxrs;

public class RangeHttpHeaders {

    private RangeHttpHeaders() {}

    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.5">HTTP/1.1 documentation</a>}.
     */
    public static final String ACCEPT_RANGES = "Accept-Ranges";

    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.16">HTTP/1.1 documentation</a>}.
     */
    public static final String CONTENT_RANGE = "Content-Range";

    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.35">HTTP/1.1 documentation</a>}.
     */
    public static final String RANGE = "Range";

}
