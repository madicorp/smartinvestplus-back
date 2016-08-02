package net.madicorp.smartinvestplus.web.rest;

import net.madicorp.smartinvestplus.config.JacksonConfiguration;
import net.madicorp.smartinvestplus.config.audit.AuditEventConverter;
import net.madicorp.smartinvestplus.domain.PersistentAuditEvent;
import net.madicorp.smartinvestplus.repository.PersistenceAuditEventRepository;
import net.madicorp.smartinvestplus.test.HttpTestRule;
import net.madicorp.smartinvestplus.test.ResponseAssertion;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static net.madicorp.smartinvestplus.test.HttpTestRule.param;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for the AuditResource REST controller.
 */
@SpringApplicationConfiguration({IntTestConfiguration.class, JacksonConfiguration.class})
public class AuditResourceIntTest {
    @ClassRule
    public static final HttpTestRule rule = new HttpTestRule();

    private static final String SAMPLE_PRINCIPAL = "SAMPLE_PRINCIPAL";
    private static final String SAMPLE_TYPE = "SAMPLE_TYPE";
    private static final LocalDateTime SAMPLE_TIMESTAMP = LocalDateTime.parse("2015-08-04T10:11:30");

    @Inject
    private static PersistenceAuditEventRepository mockAuditEventRepo;

    @Inject
    private static AuditEventConverter auditEventConverter;

    @Before
    public void setup() {
        Mockito.reset(mockAuditEventRepo);
    }

    @Test
    public void should_retrieve_all_audits() throws Exception {
        // GIVEN
        when(mockAuditEventRepo.findAll(Matchers.<Pageable>any())).thenReturn(new PageImpl<>(singletonAuditEvent()));

        // WHEN
        Response actual = rule.get("/management/jhipster/audits");

        // THEN
        ResponseAssertion.assertThat(actual)
                         .ok()
                         .contains("$.[*].principal", SAMPLE_PRINCIPAL);
    }

    @Test
    public void should_retrieve_audit_by_id() throws Exception {
        // GIVEN
        PersistentAuditEvent auditEvent = auditEvent();
        when(mockAuditEventRepo.findOne(auditEvent.getId())).thenReturn(auditEvent);

        // WHEN
        Response actual = rule.get("/management/jhipster/audits/" + auditEvent.getId());

        // THEN
        ResponseAssertion.assertThat(actual)
                         .ok()
                         .contains("$.principal", SAMPLE_PRINCIPAL);
    }

    @Test
    public void should_retrieve_audit_between_2_dates() throws Exception {
        // GIVEN
        when(mockAuditEventRepo.findAllByAuditEventDateBetween(any(), any(), any()))
            .thenReturn(new PageImpl<>(singletonAuditEvent()));
        String fromDate = "2016-07-01";
        String toDate = "2016-07-02";

        // WHEN
        Response actual = rule.get("/management/jhipster/audits", param("fromDate", fromDate), param("toDate", toDate));

        // THEN
        ResponseAssertion.assertThat(actual)
                         .ok()
                         .contains("$.[*].principal", SAMPLE_PRINCIPAL);
    }

    @Test
    public void should_send_404_for_nonexistent_audit_event() throws Exception {
        // GIVEN
        // No audit event in repo

        // WHEN
        Response actual = rule.get("/management/jhipster/audits/" + Long.MAX_VALUE);

        // THEN
        ResponseAssertion.assertThat(actual)
                         .notFound();
    }

    private static List<PersistentAuditEvent> singletonAuditEvent() {
        PersistentAuditEvent auditEvent = new PersistentAuditEvent();
        auditEvent.setAuditEventType(SAMPLE_TYPE);
        auditEvent.setPrincipal(SAMPLE_PRINCIPAL);
        auditEvent.setAuditEventDate(SAMPLE_TIMESTAMP);
        return Collections.singletonList(auditEvent());
    }

    private static PersistentAuditEvent auditEvent() {
        PersistentAuditEvent auditEvent = new PersistentAuditEvent();
        auditEvent.setId("dummyId");
        auditEvent.setAuditEventType(SAMPLE_TYPE);
        auditEvent.setPrincipal(SAMPLE_PRINCIPAL);
        auditEvent.setAuditEventDate(SAMPLE_TIMESTAMP);
        return auditEvent;
    }

}
