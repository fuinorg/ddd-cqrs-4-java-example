package org.fuin.cqrs4j.example.quarkus.query;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativePersonResourceIT extends PersonResourceTest {

    // Execute the same tests but in native mode.
}