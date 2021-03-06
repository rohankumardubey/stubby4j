package io.github.azagniotov.stubby4j.stubs.websocket;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;
import static io.github.azagniotov.stubby4j.stubs.websocket.StubWebSocketMessageType.TEXT;
import static io.github.azagniotov.stubby4j.stubs.websocket.StubWebSocketServerResponsePolicy.ONCE;
import static io.github.azagniotov.stubby4j.utils.FileUtils.tempFileFromString;
import static io.github.azagniotov.stubby4j.utils.StringUtils.getBytesUtf8;

@RunWith(MockitoJUnitRunner.class)
public class StubWebSocketServerResponseTest {

    private StubWebSocketServerResponse.Builder builder;

    @Before
    public void setUp() throws Exception {
        builder = new StubWebSocketServerResponse.Builder();
    }

    @Test
    public void stubbedWebSocketServerResponseHasExpectedDefaults() throws Exception {

        final StubWebSocketServerResponse webSocketServerResponse = builder.build();
        assertThat(webSocketServerResponse.getDelay()).isEqualTo(0);
        assertThat(webSocketServerResponse.getPolicy()).isEqualTo(ONCE);
        assertThat(webSocketServerResponse.getMessageType()).isEqualTo(TEXT);
    }

    @Test
    public void returnsBodyAsExpectedBytesWhenOnlyBodyStubbed() throws Exception {
        final StubWebSocketServerResponse socketServerResponse =
                builder.withBody("OK")
                        .build();
        assertThat(socketServerResponse.getBodyAsBytes()).isEqualTo(getBytesUtf8("OK"));
    }

    @Test
    public void returnsBodyAsExpectedBytesWhenOnlyFileStubbed() throws Exception {
        final StubWebSocketServerResponse socketServerResponse =
                builder.withFile(tempFileFromString("Apple"))
                        .build();
        assertThat(socketServerResponse.getBodyAsBytes()).isEqualTo(getBytesUtf8("Apple"));
    }

    @Test
    public void returnsBodyAsExpectedFileBytesWhenBothBodyAndFileStubbed() throws Exception {
        final StubWebSocketServerResponse socketServerResponse =
                builder.withBody("OK")
                        .withFile(tempFileFromString("Banana"))
                        .build();
        assertThat(socketServerResponse.getBodyAsBytes()).isEqualTo(getBytesUtf8("Banana"));
    }

    @Test
    public void returnsBodyAsExpectedStringWhenOnlyBodyStubbed() throws Exception {
        final StubWebSocketServerResponse socketServerResponse =
                builder.withBody("OK")
                        .build();
        assertThat(socketServerResponse.getBodyAsString()).isEqualTo("OK");
    }

    @Test
    public void returnsBodyAsExpectedStringWhenOnlyFileStubbed() throws Exception {
        final StubWebSocketServerResponse socketServerResponse =
                builder.withFile(tempFileFromString("Apple"))
                        .build();
        assertThat(socketServerResponse.getBodyAsString()).isEqualTo("Apple");
    }

    @Test
    public void returnsBodyAsExpectedStringWhenBothBodyAndFileStubbed() throws Exception {
        final StubWebSocketServerResponse socketServerResponse =
                builder.withBody("OK")
                        .withFile(tempFileFromString("Banana"))
                        .build();
        assertThat(socketServerResponse.getBodyAsString()).isEqualTo("Banana");
    }
}