package com.multicraft.util;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UUIDParserTests {

    @Test
    public void testFormattedStrings() {
        String correct = "14bff6a7-7407-4a54-a642-dc47d552f3ae";
        assertEquals(UUID.fromString(correct), UUIDParser.parse(correct));

        correct = "c12e4eb1-d205-46ab-a1b2-ce965ca95ec4";
        assertEquals(UUID.fromString(correct), UUIDParser.parse(correct));
    }

    @Test
    public void testUnformattedStrings() {
        String plain = "14bff6a774074a54a642dc47d552f3ae";
        String formatted = "14bff6a7-7407-4a54-a642-dc47d552f3ae";
        assertEquals(UUID.fromString(formatted), UUIDParser.parse(plain));

        plain = "c12e4eb1d20546aba1b2ce965ca95ec4";
        formatted = "c12e4eb1-d205-46ab-a1b2-ce965ca95ec4";
        assertEquals(UUID.fromString(formatted), UUIDParser.parse(plain));

        /* For the odd case where the dashes are incorrect */
        String improper = "c1-2e4eb1-d20546a-ba1b2ce965-ca95ec4";
        formatted = "c12e4eb1-d205-46ab-a1b2-ce965ca95ec4";
        assertEquals(UUID.fromString(formatted), UUIDParser.parse(improper));
    }

    @Test
    public void testInvalidStrings() {
        String illegal = "this is my illegal uuid";
        assertEquals(new UUID(0L, 0L), UUIDParser.parse(illegal));

        illegal = "this is my illegal uuid, it is very long and will pass the formatting, but not the actual parsing";
        assertEquals(new UUID(0L, 0L), UUIDParser.parse(illegal));
    }

}
