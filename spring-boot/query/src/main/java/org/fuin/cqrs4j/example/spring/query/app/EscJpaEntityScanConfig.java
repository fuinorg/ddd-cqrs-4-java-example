package org.fuin.cqrs4j.example.spring.query.app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Registers the relational {@code esc-jpa} event-store entities (the event tables and the per-aggregate Person
 * stream tables) for the query service, but <b>only</b> under the {@code esc-jpa} backend-portability profile.
 * Under the default KurrentDB backend these tables are unused, so scanning them would only create (and, with
 * {@code create-drop}, try to drop) empty event-store tables in the read-model database - producing harmless
 * but noisy schema warnings. Keeping the scan profile-scoped avoids that.
 */
@Configuration
@Profile("esc-jpa")
@EntityScan({
        "org.fuin.esc.jpa",
        "org.fuin.cqrs4j.example.spring.shared.jpa"
})
public class EscJpaEntityScanConfig {

}
