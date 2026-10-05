 package T_And_P.Training_and_Placement.service;

import T_And_P.Training_and_Placement.constant.ApplicationStatus;
import T_And_P.Training_and_Placement.event.ApplicationActivityEvent;
import T_And_P.Training_and_Placement.event.PlannerPublishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
        log.info("sendPlannerPublishedEmails() started for plannerId={}",
                event == null ? null : event.getPlannerId());

        if (event == null || CollectionUtils.isEmpty(event.getEligibleStudentEmails())) {
            log.info("sendPlannerPublishedEmails() skipped, no eligible student emails");
            return;
        }

        String subject = "New placement planner published: " + event.getPlannerName();

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
                event.getRegistrationEndDate());

        event.getEligibleStudentEmails().stream()
                .filter(StringUtils::hasText)
                .forEach(email -> sendEmail(email, subject, body));

        log.info("sendPlannerPublishedEmails() completed for plannerId={}, recipients={}",
                event.getPlannerId(),
                event.getEligibleStudentEmails().size());
    }

    /**
     * Emails the student after apply, shortlist, interview, select, reject,
     * cancel or offer accept.
     */
    public void sendApplicationActivityEmail(ApplicationActivityEvent event) {
        log.info("sendApplicationActivityEmail() started for applicationId={}, status={}",
                event == null ? null : event.getApplicationId(),
                event == null ? null : event.getNewStatus());

        if (event == null || !StringUtils.hasText(event.getEmail())) {
            log.info("sendApplicationActivityEmail() skipped because email is blank");
            return;
        }

        String studentName = StringUtils.hasText(event.getStudentName())
                ? event.getStudentName()
                : "Student";

        String plannerName = StringUtils.hasText(event.getPlannerName())
                ? event.getPlannerName()
                : "the planner";

        String companyName = StringUtils.hasText(event.getCompanyName())
                ? event.getCompanyName()
                : "";

        String status = event.getNewStatus() == null
                ? ""
                : event.getNewStatus().trim().toUpperCase();

        String subject;
        String body;

        if (ApplicationStatus.APPLIED.name().equals(status)) {
            subject = "Application submitted: " + plannerName;
            body = "Hi " + studentName
                    + ", your application for " + plannerName
                    + (StringUtils.hasText(companyName)
                    ? " (" + companyName + ")"
                    : "")
                    + " has been submitted successfully.";

        } else if (ApplicationStatus.SHORTLISTED.name().equals(status)) {
            subject = "Application shortlisted: " + plannerName;
            body = "Hi " + studentName
                    + ", your application for " + plannerName
                    + " has been shortlisted. Please watch for interview updates.";

        } else if (ApplicationStatus.INTERVIEW_SCHEDULED.name().equals(status)) {
            subject = "Interview scheduled: " + plannerName;
            body = "Hi " + studentName
                    + ", your interview for " + plannerName
                    + " has been scheduled. Please check the planner details for venue or meeting link.";

        } else if (ApplicationStatus.SELECTED.name().equals(status)) {
            subject = "Congratulations, you are selected: " + plannerName;
            body = "Hi " + studentName
                    + ", congratulations. You have been selected for " + plannerName
                    + (StringUtils.hasText(companyName)
                    ? " (" + companyName + ")"
                    : "")
                    + ". You can now upload your offer letter / joining letter.";

        } else if (ApplicationStatus.REJECTED.name().equals(status)) {
            subject = "Application update: not selected for " + plannerName;
            body = "Hi " + studentName
                    + ", thank you for applying to " + plannerName
                    + ". Your application was not selected this time.";

        } else if (ApplicationStatus.CANCELLED.name().equals(status)) {
            subject = "Application cancelled: " + plannerName;
            body = "Hi " + studentName
                    + ", your application for " + plannerName
                    + " has been cancelled.";

        } else if (ApplicationStatus.OFFER_ACCEPTED.name().equals(status)) {
            subject = "Offer letter received: " + plannerName;
            body = "Hi " + studentName
                    + ", your offer / joining letter for " + plannerName
                    + " has been recorded. Status is now Offer Accepted.";

        } else {
            subject = "Application update: " + plannerName;
            body = "Hi " + studentName
                    + ", your application for " + plannerName
                    + " was updated to " + status + ".";
        }

        sendEmail(event.getEmail(), subject, body);

        log.info("sendApplicationActivityEmail() completed for email={}, status={}",
                event.getEmail(),
                status);
    }

    /**
     * Confirms to the student that their application was submitted.
     */
    public void sendApplicationSubmittedEmail(
            String email,
            String studentName,
            String plannerName) {

        log.info("sendApplicationSubmittedEmail() started for email={}, plannerName={}",
                email,
                plannerName);

        if (!StringUtils.hasText(email)) {
            log.info("sendApplicationSubmittedEmail() skipped because email is blank");
            return;
        }

        sendEmail(
                email,
                "Application submitted: " + plannerName,
                "Hi " + studentName
                        + ", your application for " + plannerName
                        + " has been submitted successfully.");

        log.info("sendApplicationSubmittedEmail() completed for email={}", email);
    }

    /**
     * Common email sender. Replace this with JavaMailSender when SMTP is configured.
     */
    public void sendEmail(String to, String subject, String body) {
        log.info("sendEmail() started for to={}, subject={}", to, subject);
        log.info("Email notification to={} subject={} body={}", to, subject, body);
        log.info("sendEmail() completed for to={}", to);
    }
}