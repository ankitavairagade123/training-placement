# Training and Placement API

Base URL: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

All requests use `Content-Type: application/json`.

---

## 1. Company Master

Manage companies that can be attached to a planner.

### Save company

Creates a company, or updates it when `id` is sent.

- **URL:** `POST /api/company`
- **Sample request:**

```json
{
  "id": null,
  "companyName": "Infosys",
  "companyCode": "INFY01",
  "companyType": "MNC",
  "industryType": "IT",
  "hrName": "Anita Sharma",
  "address": "Pune",
  "pincode": 411001,
  "website": "https://www.infosys.com",
  "email": "hr@infosys.com",
  "contactNumber": "9876543210",
  "status": "ACTIVE"
}
```

### Get all companies

Returns every company for the master grid.

- **URL:** `GET /api/company`

### Get company by id

Loads one company for view or edit.

- **URL:** `GET /api/company/1`

### Delete company

Deletes a company if it is not used by a planner.

- **URL:** `DELETE /api/company/1`

---

## 2. Eligibility Type

Master list of eligibility types (SSC, HSC, CGPA, Attendance, Branch, and so on). Only `ACTIVE` types can be used on a planner.

### Save eligibility type

Creates a type, or updates it when `id` is sent.

- **URL:** `POST /api/eligibilityType`
- **Sample request:**

```json
{
  "id": null,
  "eligibilityType": "SSC Percentage",
  "status": "ACTIVE"
}
```

### Get all eligibility types

Returns active and inactive types.

- **URL:** `GET /api/eligibilityType`

### Get active eligibility types

Use this list while creating a planner.

- **URL:** `GET /api/eligibilityType/active`

### Get eligibility type by id

- **URL:** `GET /api/eligibilityType/1`

### Delete eligibility type

- **URL:** `DELETE /api/eligibilityType/1`

---

## 3. Student Master

Stores student profile and academic values used during eligibility checks.

### Save student

Creates a student, or updates it when `studentId` is sent.

- **URL:** `POST /student-master/save`
- **Sample request:**

```json
{
  "studentId": null,
  "studentName": "Rahul Patil",
  "email": "rahul.patil@college.edu",
  "sscPercentage": 82.5,
  "hscPercentage": 78.0,
  "ugCgpa": 8.4,
  "attendance": 90.0,
  "activeBacklogs": 0,
  "branch": "CSE",
  "semester": 6,
  "passingYear": 2027,
  "resumePath": "/resumes/rahul-patil.pdf"
}
```

### Get student by id

- **URL:** `GET /student-master/1`

### Get all students

- **URL:** `GET /student-master/getAll`

---

## 4. Application Field Master

Optional extra form fields that can be used on applications.

### Save field

Creates a field, or updates it when `fieldId` is sent.

- **URL:** `POST /application-field/save`
- **Sample request:**

```json
{
  "fieldId": null,
  "fieldName": "Preferred Location",
  "fieldType": "TEXT",
  "status": "ACTIVE"
}
```

`fieldType` values: `TEXT`, `EMAIL`, `NUMBER`, `FILE`, `DATE`, `TEXTAREA`, `RADIO`, `MULTI_SELECT`

### Get all fields

- **URL:** `GET /application-field/getAll`

### Get field by id

- **URL:** `GET /application-field/1`

### Get active fields

- **URL:** `GET /application-field/active`

---

## 5. Planner

Create a draft planner, attach eligibility and questions, then publish or reject it.

`plannerType`: `CAMPUS_PLACEMENT`, `INTERNSHIP`, `WORKSHOP`, `INDUSTRIAL_VISIT`, `SEMINAR`, `HACKATHON`, `TRAINING`, `INTERVIEW`

`mode`: `ONLINE`, `OFFLINE`

`plannerScheduleType`: `FIXED`, `RANGE`

`criteriaRule`: `GREATER_THAN`, `GREATER_THAN_EQUALS_TO`, `LESS_THAN`, `LESS_THAN_EQUALS_TO`, `EQUALS`, `IN`

### Save planner

Creates a `DRAFT` planner, or updates an unpublished planner when `id` is sent.

- **URL:** `POST /api/planner`
- **Sample request:**

```json
{
  "id": null,
  "plannerName": "Infosys Campus Drive 2026",
  "plannerDesc": "On-campus recruitment for final year CSE and IT students",
  "plannerType": "CAMPUS_PLACEMENT",
  "plannerScheduleType": "RANGE",
  "startTime": "2026-10-15T10:00:00",
  "endTime": "2026-10-15T17:00:00",
  "registrationStartDate": "2026-10-01T00:00:00",
  "registrationEndDate": "2026-10-12T23:59:00",
  "mode": "OFFLINE",
  "maxStudents": 80,
  "companyId": 1,
  "venue": "Seminar Hall A",
  "website": "https://www.infosys.com/careers",
  "meetingLink": null,
  "remarks": "Carry college ID",
  "attachmentPath": "/attachments/infosys-jd.pdf",
  "plannerDetails": [
    {
      "eligibilityId": 1,
      "criteriaValue": "60",
      "criteriaRule": "GREATER_THAN_EQUALS_TO",
      "mandatory": true,
      "status": "ACTIVE"
    },
    {
      "eligibilityId": 2,
      "criteriaValue": "CSE,IT",
      "criteriaRule": "IN",
      "mandatory": true,
      "status": "ACTIVE"
    }
  ],
  "questions": [
    {
      "question": "Are you willing to relocate?",
      "fieldType": "RADIO",
      "mandatory": true,
      "options": [
        { "optionText": "Yes", "displayOrder": 1 },
        { "optionText": "No", "displayOrder": 2 }
      ]
    }
  ]
}
```

### Get all planners

Returns draft, active, and rejected planners.

- **URL:** `GET /api/planner`

### Get active planners

Returns published planners that are currently open for student registration.

- **URL:** `GET /api/planner/active`

### Get planner by id

Returns header, eligibility rows, and questions.

- **URL:** `GET /api/planner/1`

### Publish planner

Validates company, eligibility, and dates, then sets status to `ACTIVE`. Eligible-student mail is published to Kafka topic `planner-published` and sent by the listener.

- **URL:** `POST /api/planner/1/publish`
- **Header (optional):** `X-User-Name: TPO`

### Reject planner

Marks a draft planner as `REJECTED`.

- **URL:** `POST /api/planner/1/reject`

### Delete planner

Deletes a planner that is not published.

- **URL:** `DELETE /api/planner/1`

---

## 6. Placement Application

Student apply flow, faculty status update, and offer-letter path save.

Application status values: `APPLIED`, `SHORTLISTED`, `INTERVIEW_SCHEDULED`, `SELECTED`, `REJECTED`, `CANCELLED`, `OFFER_ACCEPTED`

Every status change emails the student through Kafka topic `application-activity`.

### Check eligibility

Checks a student against planner rules without creating an application.

- **URL:** `GET /api/placement-application/eligibility?studentId=1&plannerId=1`

### Apply for a drive

Student applies to a published planner. Eligibility, registration window, resume, terms, and mandatory questions are validated. Confirmation mail is published to Kafka topic `application-activity`.

- **URL:** `POST /api/placement-application/apply`
- **Sample request:**

```json
{
  "studentId": 1,
  "plannerId": 1,
  "resumePath": "/resumes/rahul-patil.pdf",
  "termsAccepted": true,
  "applicationDetails": [
    {
      "fieldName": "Are you willing to relocate?",
      "fieldValue": "Yes"
    }
  ]
}
```

### Get applications by student

- **URL:** `GET /api/placement-application/student/1`

### Get applications by planner

Faculty list for one drive.

- **URL:** `GET /api/placement-application/planner/1`

### Update application status

Faculty moves the application through interview and result stages. The student receives mail for `SHORTLISTED`, `INTERVIEW_SCHEDULED`, `SELECTED`, `REJECTED`, or `CANCELLED`.

- **URL:** `PUT /api/placement-application/status`
- **Sample request:**

```json
{
  "applicationId": 1,
  "applicationStatus": "SELECTED"
}
```

Cancel sample:

```json
{
  "applicationId": 1,
  "applicationStatus": "CANCELLED"
}
```

### Save offer letter paths

Allowed only when status is `SELECTED` or `OFFER_ACCEPTED`. Status becomes `OFFER_ACCEPTED`.

- **URL:** `POST /api/placement-application/1/offer-letter`
- **Sample request:**

```json
{
  "offerLetterPath": "/offers/rahul-infosys-offer.pdf",
  "joiningLetterPath": "/offers/rahul-infosys-joining.pdf"
}
```

### Delete application

Deletes the application and emails the student that it was cancelled.

- **URL:** `DELETE /api/placement-application/1`

---

## 7. Dashboard

TPO home, planner-wise faculty cards, and student portal cards.

### TPO dashboard

KPI cards, application funnel, company-wise counts, last 10 applications, and upcoming published planners.

- **URL:** `GET /api/dashboard`

Sample response fields: `totalCompanies`, `totalStudents`, `totalPlanners`, `activePlanners`, `totalApplications`, `appliedCount`, `shortlistedCount`, `interviewScheduledCount`, `selectedCount`, `rejectedCount`, `cancelledCount`, `offerAcceptedCount`, `companyWise`, `recentApplications`, `upcomingPlanners`.

### Planner dashboard

Application funnel for one drive.

- **URL:** `GET /api/dashboard/planner/1`

### Student dashboard

Student application funnel, my applications, and upcoming planners.

- **URL:** `GET /api/dashboard/student/1`

---

## 8. Background mail (Kafka)

Kafka broker: `localhost:9092`

| Event | Topic | Listener | Mail |
| --- | --- | --- | --- |
| Planner published | `planner-published` | `PlannerPublishedEventListener` | Eligible students |
| Application submitted (legacy) | `application-submitted` | `ApplicationSubmittedEventListener` | Applicant |
| Application activity | `application-activity` | `ApplicationActivityEventListener` | Applicant for applied, shortlisted, interview, selected, rejected, cancelled, offer accepted |

Publish, apply, and status-update APIs still succeed if Kafka or mail fails.
