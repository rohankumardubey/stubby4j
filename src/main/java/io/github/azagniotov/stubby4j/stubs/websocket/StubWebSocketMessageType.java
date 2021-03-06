package io.github.azagniotov.stubby4j.stubs.websocket;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.github.azagniotov.stubby4j.utils.StringUtils.toLower;

public enum StubWebSocketMessageType {

    TEXT("text"),
    BINARY("binary");

    private static final Map<String, StubWebSocketMessageType> PROPERTY_NAME_TO_ENUM_MEMBER;

    static {
        PROPERTY_NAME_TO_ENUM_MEMBER = new HashMap<>();
        for (final StubWebSocketMessageType enumMember : EnumSet.allOf(StubWebSocketMessageType.class)) {
            PROPERTY_NAME_TO_ENUM_MEMBER.put(enumMember.toString(), enumMember);
        }
    }

    private final String value;

    StubWebSocketMessageType(final String value) {
        this.value = value;
    }

    public static boolean isUnknownProperty(final String stubbedProperty) {
        return !PROPERTY_NAME_TO_ENUM_MEMBER.containsKey(toLower(stubbedProperty));
    }

    public static Optional<StubWebSocketMessageType> ofNullableProperty(final String stubbedProperty) {
        return Optional.ofNullable(PROPERTY_NAME_TO_ENUM_MEMBER.get(toLower(stubbedProperty)));
    }

    @Override
    public String toString() {
        return toLower(this.value);
    }
}
