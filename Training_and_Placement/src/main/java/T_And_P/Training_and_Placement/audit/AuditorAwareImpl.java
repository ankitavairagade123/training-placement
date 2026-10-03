package T_And_P.Training_and_Placement.audit;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/**
 * Supplies createdBy / updatedBy values for JPA auditing.
 * <p>
 * Currently returns SYSTEM because security login is not enabled.
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    private static final Logger log =
            LoggerFactory.getLogger(AuditorAwareImpl.class);

    /**
     * Returns the current auditor name stored on AuditEntity fields.
     *
     * @return current auditor
     */
    @Override
    public Optional<String> getCurrentAuditor() {

        log.debug("getCurrentAuditor() started");

        Optional<String> auditor = Optional.of("SYSTEM");

        log.debug("getCurrentAuditor() completed, auditor: {}", auditor.get());

        return auditor;
    }
}
