# jaxrs-range-helper
JAX-RS extension that helps with HTTP range requests.

The following JAX-RS endpoint delivers a movie files. If the HTTP range is set, only the requested part of the file 
will be to the client.
```java
@Path("/movie")
public class MovieEndpoint {

    @GET
    @Path("{id}")
    public Response get(@PathParam("id") String movieId, @HeaderParam(RangeHttpHeaders.RANGE) String range) {
        // find movie file by id
        File file = ...;
        long totalLength = file.length();
        
        // create response builder, add Accept-Ranges header and set media type
        Response.ResponseBuilder responseBuilder = Response.ok()
                .header(RangeHttpHeaders.ACCEPT_RANGES, "bytes")
                .type(MediaType.APPLICATION_OCTET_STREAM);

        // if range not specified, deliver the hole file to the client
        if (range == null) {
            return responseBuilder
                    .entity(file)
                    .header(HttpHeaders.CONTENT_LENGTH, totalLength)
                    .build();
        }

        // else if range is satisfiable, deliver the requested part of the file to the client
        else if (HttpRangeHelper.isSatisfiable(range, totalLength)) {
            long from = HttpRangeHelper.getFromValue(range);
            Long to = HttpRangeHelper.getToValue(range);
            long chunkSize = 1024 * 1024;
            long length = HttpRangeHelper.getLength(from, to, totalLength, chunkSize);

            return responseBuilder
                    .status(HttpStatus.PARTIAL_CONTENT.value())
                    .header(RangeHttpHeaders.CONTENT_RANGE, HttpRangeHelper.getContentRange(from, length, totalLength))
                    .header(HttpHeaders.CONTENT_LENGTH, length)
                    .entity(new MediaStream(file.toPath(), from, length))
                    .build();
        }

        // else, send range not satisfiable to the client
        return responseBuilder.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE.value()).build();
    }

}
```

This snippet depends on JAX-RS 2.1 and uses [HttpRangeHelper](src/main/java/com/github/j3t/jaxrs/HttpRangeHelper.java) 
to identify the HTTP range and [MediaStream](src/main/java/com/github/j3t/jaxrs/MediaStream.java) to deliver the 
requested part. There are also some HTTP header names in [RangeHttpHeaders](src/main/java/com/github/j3t/jaxrs/RangeHttpHeaders.java) 
that are not defined by JAX-RS.

The next snippets shows how the result would looks like with curl. Total file size is ~4GB and chunk size is 1MB.

without HTTP range header ...
```bash
$ curl http://localhost:8080/movie/1 -I
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0 4042M    0     0    0     0      0      0 --:--:--  0:00:01 --:--:--     0HTTP/1.1 200
Accept-Ranges: bytes
Content-Type: application/octet-stream
Content-Length: 4239261696
Date: Tue, 13 Mar 2018 15:51:04 GMT
```

with HTTP range header start index 0 ...
```bash
$ curl http://localhost:8080/movie/1 -I -H "Range: bytes=0-"
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0 1024k    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0HTTP/1.1 206
Content-Range: bytes 0-1048575/4239261696
Accept-Ranges: bytes
Content-Type: application/octet-stream
Content-Length: 1048576
Date: Tue, 13 Mar 2018 15:36:10 GMT
```

with HTTP range header start index 1048576 and end index 2097151 ...
```bash
$ curl http://localhost:8080/movie/1 -I -H "Range: bytes=1048576-2097151"
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0 1024k    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0HTTP/1.1 206
Content-Range: bytes 1048576-2097151/4239261696
Accept-Ranges: bytes
Content-Type: application/octet-stream
Content-Length: 1048576
Date: Tue, 13 Mar 2018 15:51:04 GMT
```

