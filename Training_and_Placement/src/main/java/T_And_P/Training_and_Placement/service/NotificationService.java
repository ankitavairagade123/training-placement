package T_And_P.Training_and_Placement.service;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import T_And_P.Training_and_Placement.event.PlannerPublishedEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * Sends student notifications after planner publish and application submit.
 * Currently logs the email content. SMTP can be wired later without changing callers.
 */
@Slf4j
@Service
public class NotificationService {

    /**
     * Emails every eligible student when a planner is published.
     */
    public void sendPlannerPublishedEmails(PlannerPublishedEvent event) {

        log.info(
                "sendPlannerPublishedEmails() started for plannerId={}",
                event == null ? null : event.getPlannerId()
        );

        if (event == null || CollectionUtils.isEmpty(event.getEligibleStudentEmails())) {
            log.info(
                    "sendPlannerPublishedEmails() skipped, no eligible student emails"
            );
            return;
        }

        String subject =
                "New placement planner published: " + event.getPlannerName();

        String body = String.format(
                "A new planner is now open for applications.%n%n"
                        + "Planner: %s%n"
                        + "Type: %s%n"
                        + "Company: %s (%s)%n"
                        + "Registration: %s to %s",
                event.getPlannerName(),
                event.getPlannerType(),
                event.getCompanyName(),
                event.getCompanyCode(),
                event.getRegistrationStartDate(),
                event.getRegistrationEndDate()
        );

        event.getEligibleStudentEmails()
                .stream()
                .filter(StringUtils::hasText)
                .forEach(email -> sendEmail(email, subject, body));

        log.info(
                "sendPlannerPublishedEmails() completed for plannerId={}, recipients={}",
                event.getPlannerId(),
                event.getEligibleStudentEmails().size()
        );
    }

    /**
     * Confirms to the student that their application was submitted.
     */
    public void sendApplicationSubmittedEmail(
            String email,
            String studentName,
            String plannerName) {

        log.info(
                "sendApplicationSubmittedEmail() started for email={}, plannerName={}",
                email,
                plannerName
        );

        if (!StringUtils.hasText(email)) {
            log.info(
                    "sendApplicationSubmittedEmail() skipped because email is blank"
            );
            return;
        }

        sendEmail(
                email,
                "Application submitted: " + plannerName,
                "Hi " + studentName
                        + ", your application for " + plannerName
                        + " has been submitted successfully."
        );

        log.info(
                "sendApplicationSubmittedEmail() completed for email={}",
                email
        );
    }

    /**
     * Common email sender. Replace this with JavaMailSender when SMTP is configured.
     */
    public void sendEmail(
            String to,
            String subject,
            String body) {

        log.info(
                "sendEmail() started for to={}, subject={}",
                to,
                subject
        );

        log.info(
                "Email notification to={} subject={} body={}",
                to,
                subject,
                body
        );

        log.info(
                "sendEmail() completed for to={}",
                to
        );
    }
}