package org.fuin.cqrs4j.example.quarkus.query;

import io.kurrent.dbclient.CreateProjectionOptions;
import io.kurrent.dbclient.EventData;
import io.kurrent.dbclient.EventDataBuilder;
import io.kurrent.dbclient.KurrentDBClient;
import io.kurrent.dbclient.KurrentDBClientSettings;
import io.kurrent.dbclient.KurrentDBProjectionManagementClient;
import io.kurrent.dbclient.ResolvedEvent;
import io.kurrent.dbclient.SubscribeToStreamOptions;
import io.kurrent.dbclient.Subscription;
import io.kurrent.dbclient.SubscriptionListener;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Isolation test: uses the raw {@link KurrentDBClient} directly (no fuin esc/cqrs library) inside a
 * running Quarkus process to subscribe to a plain stream and receive an appended event.
 * <p>
 * Purpose: determine whether "Quarkus + kurrentdb-client streaming subscription" works at all. If this
 * receives the event, the {@code PersonListResourceIT} failure is specific to the library's projection
 * consumption; if it does NOT, the problem is fundamental to running the KurrentDB client's
 * server-streaming subscription under Quarkus.
 */
@QuarkusTest
class KurrentDbDirectSubscriptionIT {

    private static final Logger LOG = LoggerFactory.getLogger(KurrentDbDirectSubscriptionIT.class);

    @Test
    void rawClientSubscriptionReceivesAppendedEvent() throws Exception {

        final KurrentDBClientSettings settings = KurrentDBClientSettings.builder()
                .addHost("localhost", 2113)
                .tls(false)
                .defaultCredentials("admin", "changeit")
                .buildConnectionSettings();

        final KurrentDBClient client = KurrentDBClient.create(settings);
        final String streamName = "direct-sub-test-" + UUID.randomUUID();
        final CountDownLatch received = new CountDownLatch(1);
        final AtomicReference<String> receivedType = new AtomicReference<>();

        try {
            // Subscribe from the start of the (not-yet-existing) stream.
            client.subscribeToStream(streamName, new SubscriptionListener() {
                @Override
                public void onConfirmation(final Subscription subscription) {
                    LOG.info("DIRECT-SUB onConfirmation: {}", subscription.getSubscriptionId());
                }

                @Override
                public void onEvent(final Subscription subscription, final ResolvedEvent event) {
                    final String type = event.getOriginalEvent().getEventType();
                    LOG.info("DIRECT-SUB onEvent: type={}", type);
                    receivedType.set(type);
                    received.countDown();
                }

                @Override
                public void onCaughtUp(final Subscription s, final java.time.Instant t, final Long l, final io.kurrent.dbclient.Position p) {
                    LOG.info("DIRECT-SUB onCaughtUp");
                }

                @Override
                public void onCancelled(final Subscription subscription, final Throwable throwable) {
                    LOG.warn("DIRECT-SUB onCancelled", throwable);
                }
            }).get();

            // Give the subscription a moment to be established, then append an event.
            Thread.sleep(500);
            final EventData eventData = EventDataBuilder
                    .json(UUID.randomUUID(), "DirectTestEvent", "{\"hello\":\"world\"}".getBytes(StandardCharsets.UTF_8))
                    .build();
            client.appendToStream(streamName, eventData).get();
            LOG.info("DIRECT-SUB appended event to stream {}", streamName);

            final boolean got = received.await(15, TimeUnit.SECONDS);

            assertThat(got).as("raw KurrentDB client subscription delivered the appended event").isTrue();
            assertThat(receivedType.get()).isEqualTo("DirectTestEvent");

        } finally {
            client.shutdown();
        }
    }

    /**
     * Replicates the fuin library's mechanism with the raw client: a server-side projection
     * ({@code fromAll().foreachStream().when({...linkTo(target)})}) links events into a target stream,
     * and the subscription reads that target (link) stream with {@code resolveLinkTos}. If this receives
     * the event, the projection→subscription path is fine and the {@code PersonListResourceIT} failure is
     * higher up in the library (ViewSubscriptions / QuarkusViewManager dispatch, threading or context).
     */
    @Test
    void rawClientSubscriptionToProjectionLinkStream() throws Exception {

        final KurrentDBClientSettings settings = KurrentDBClientSettings.builder()
                .addHost("localhost", 2113)
                .tls(false)
                .defaultCredentials("admin", "changeit")
                .buildConnectionSettings();

        final KurrentDBClient client = KurrentDBClient.create(settings);
        final KurrentDBProjectionManagementClient projections = KurrentDBProjectionManagementClient.create(settings);

        final String suffix = UUID.randomUUID().toString().substring(0, 8);
        final String eventType = "ProjTestEvent" + suffix;
        final String targetStream = "direct-proj-target-" + suffix;
        final String projectionName = "direct-proj-" + suffix;
        final String sourceStream = "PERSON-" + suffix;
        final String js = "fromAll().foreachStream().when({\"" + eventType
                + "\": function(state, ev){ linkTo(\"" + targetStream + "\", ev); }})";

        final CountDownLatch received = new CountDownLatch(1);
        final AtomicReference<String> receivedType = new AtomicReference<>();

        try {
            // Create + enable the link-to projection, like the library's GrpcProjectionAdminEventStore does.
            projections.create(projectionName, js,
                    CreateProjectionOptions.get().emitEnabled(true).trackEmittedStreams(true)).get();
            projections.enable(projectionName).get();
            Thread.sleep(1500);

            // Subscribe to the projection's link/target stream, resolving link-tos (as esc does).
            client.subscribeToStream(targetStream, new SubscriptionListener() {
                @Override
                public void onConfirmation(final Subscription s) {
                    LOG.info("PROJ-SUB onConfirmation");
                }

                @Override
                public void onEvent(final Subscription s, final ResolvedEvent event) {
                    final String type = event.getEvent().getEventType();
                    LOG.info("PROJ-SUB onEvent (resolved): type={}", type);
                    receivedType.set(type);
                    received.countDown();
                }

                @Override
                public void onCancelled(final Subscription s, final Throwable t) {
                    LOG.warn("PROJ-SUB onCancelled", t);
                }
            }, SubscribeToStreamOptions.get().fromStart().resolveLinkTos()).get();

            Thread.sleep(500);
            final EventData eventData = EventDataBuilder
                    .json(UUID.randomUUID(), eventType, "{\"hello\":\"proj\"}".getBytes(StandardCharsets.UTF_8))
                    .build();
            client.appendToStream(sourceStream, eventData).get();
            LOG.info("PROJ-SUB appended {} to {}", eventType, sourceStream);

            final boolean got = received.await(15, TimeUnit.SECONDS);

            assertThat(got).as("subscription to the projection link stream delivered the linked event").isTrue();
            assertThat(receivedType.get()).isEqualTo(eventType);

        } finally {
            client.shutdown();
            projections.shutdown();
        }
    }

}
