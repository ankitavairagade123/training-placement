-- =============================================================================
-- Training and Placement - table, index, and constraint script
-- Database : training_placement
-- Engine   : MySQL 8+
-- Keep this file as the single source for schema DDL.
-- =============================================================================

CREATE DATABASE IF NOT EXISTS training_placement
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE training_placement;

-- -----------------------------------------------------------------------------
-- 1. company_master
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS company_master (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    company_name    VARCHAR(255)    NOT NULL,
    company_code    VARCHAR(255)    NULL,
    company_type    VARCHAR(255)    NULL,
    industry        VARCHAR(255)    NULL,
    hr_name         VARCHAR(255)    NULL,
    address         VARCHAR(255)    NULL,
    email           VARCHAR(255)    NULL,
    website         VARCHAR(255)    NULL,
    contact_number  VARCHAR(255)    NULL,
    pincode         BIGINT          NULL,
    status          VARCHAR(20)     NULL DEFAULT 'ACTIVE',
    created_by      VARCHAR(255)    NULL,
    created_at      DATETIME        NULL,
    updated_by      VARCHAR(255)    NULL,
    updated_at      DATETIME        NULL,
    version         BIGINT          NULL,
    CONSTRAINT pk_company_master PRIMARY KEY (id),
    CONSTRAINT uk_company_master_company_code UNIQUE (company_code),
    CONSTRAINT chk_company_master_status CHECK (
        status IN ('DRAFT', 'ACTIVE', 'INACTIVE', 'REJECTED')
    ),
    INDEX idx_company_master_status (status),
    INDEX idx_company_master_name (company_name)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 2. student
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS student (
    student_id      BIGINT          NOT NULL AUTO_INCREMENT,
    student_name    VARCHAR(255)    NULL,
    email           VARCHAR(255)    NULL,
    ssc_percentage  DOUBLE          NULL,
    hsc_percentage  DOUBLE          NULL,
    ug_cgpa         DOUBLE          NULL,
    attendance      DOUBLE          NULL,
    active_backlogs INT             NULL,
    branch          VARCHAR(255)    NULL,
    semester        INT             NULL,
    passing_year    INT             NULL,
    resume_path     VARCHAR(255)    NULL,
    CONSTRAINT pk_student PRIMARY KEY (student_id),
    INDEX idx_student_name (student_name),
    INDEX idx_student_email (email),
    INDEX idx_student_branch (branch)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 3. eligibility_master
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eligibility_master (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    eligibility_type VARCHAR(255)    NOT NULL,
    status           VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    created_by       VARCHAR(255)    NULL,
    created_at       DATETIME        NULL,
    updated_by       VARCHAR(255)    NULL,
    updated_at       DATETIME        NULL,
    version          BIGINT          NULL,
    CONSTRAINT pk_eligibility_master PRIMARY KEY (id),
    CONSTRAINT uk_eligibility_master_type UNIQUE (eligibility_type),
    CONSTRAINT chk_eligibility_master_status CHECK (
        status IN ('DRAFT', 'ACTIVE', 'INACTIVE', 'REJECTED')
    ),
    INDEX idx_eligibility_master_status (status)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 4. application_field_master
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS application_field_master (
    field_id    BIGINT          NOT NULL AUTO_INCREMENT,
    field_name  VARCHAR(255)    NOT NULL,
    field_type  VARCHAR(30)     NOT NULL,
    status      VARCHAR(20)     NOT NULL,
    created_by  VARCHAR(255)    NULL,
    created_at  DATETIME        NULL,
    updated_by  VARCHAR(255)    NULL,
    updated_at  DATETIME        NULL,
    version     BIGINT          NULL,
    CONSTRAINT pk_application_field_master PRIMARY KEY (field_id),
    CONSTRAINT uk_application_field_master_name UNIQUE (field_name),
    CONSTRAINT chk_application_field_master_type CHECK (
        field_type IN (
            'TEXT',
            'EMAIL',
            'NUMBER',
            'FILE',
            'DATE',
            'TEXTAREA',
            'RADIO',
            'MULTI_SELECT'
        )
    ),
    CONSTRAINT chk_application_field_master_status CHECK (
        status IN ('DRAFT', 'ACTIVE', 'INACTIVE', 'REJECTED')
    ),
    INDEX idx_application_field_master_status (status)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 5. training_and_placement_planner_hdr
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS training_and_placement_planner_hdr (
    id                      BIGINT          NOT NULL AUTO_INCREMENT,
    planner_name            VARCHAR(255)    NULL,
    planner_description     VARCHAR(255)    NULL,
    planner_type            VARCHAR(30)     NULL,
    mode                    VARCHAR(20)     NULL,
    planner_schedule_type   VARCHAR(20)     NULL,
    status                  VARCHAR(20)     NULL,
    start_time              DATETIME        NULL,
    end_time                DATETIME        NULL,
    registration_start_date DATETIME        NULL,
    registration_end_date   DATETIME        NULL,
    max_student_count       INT             NULL,
    company_id              BIGINT          NOT NULL,
    venue                   VARCHAR(255)    NULL,
    website                 VARCHAR(255)    NULL,
    meeting_link            VARCHAR(255)    NULL,
    remarks                 VARCHAR(255)    NULL,
    attachment_path         VARCHAR(255)    NULL,
    published_by            VARCHAR(255)    NULL,
    published_at            DATETIME        NULL,
    created_by              VARCHAR(255)    NULL,
    created_at              DATETIME        NULL,
    updated_by              VARCHAR(255)    NULL,
    updated_at              DATETIME        NULL,
    version                 BIGINT          NULL,
    CONSTRAINT pk_planner_hdr PRIMARY KEY (id),
    CONSTRAINT fk_planner_hdr_company
        FOREIGN KEY (company_id)
        REFERENCES company_master (id),
    CONSTRAINT chk_planner_hdr_type CHECK (
        planner_type IN (
            'CAMPUS_PLACEMENT',
            'INTERNSHIP',
            'WORKSHOP',
            'INDUSTRIAL_VISIT',
            'SEMINAR',
            'HACKATHON',
            'TRAINING',
            'INTERVIEW'
        )
    ),
    CONSTRAINT chk_planner_hdr_mode CHECK (
        mode IN ('ONLINE', 'OFFLINE')
    ),
    CONSTRAINT chk_planner_hdr_schedule CHECK (
        planner_schedule_type IN ('FIXED', 'RANGE')
    ),
    CONSTRAINT chk_planner_hdr_status CHECK (
        status IN ('DRAFT', 'ACTIVE', 'INACTIVE', 'REJECTED')
    ),
    INDEX idx_planner_hdr_company_id (company_id),
    INDEX idx_planner_hdr_status (status),
    INDEX idx_planner_hdr_reg_window (
        registration_start_date,
        registration_end_date
    ),
    INDEX idx_planner_hdr_start_time (start_time),
    INDEX idx_planner_hdr_planner_type (planner_type)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 6. training_and_placement_planner_dtl
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS training_and_placement_planner_dtl (
    planner_dtl_id  BIGINT          NOT NULL AUTO_INCREMENT,
    planner_hdr_id  BIGINT          NOT NULL,
    eligibility_id  BIGINT          NOT NULL,
    mandatory       TINYINT(1)      NULL DEFAULT 0,
    criteria_rule   VARCHAR(40)     NULL,
    criteria_value  VARCHAR(255)    NULL,
    status          VARCHAR(20)     NULL,
    created_by      VARCHAR(255)    NULL,
    created_at      DATETIME        NULL,
    updated_by      VARCHAR(255)    NULL,
    updated_at      DATETIME        NULL,
    version         BIGINT          NULL,
    CONSTRAINT pk_planner_dtl PRIMARY KEY (planner_dtl_id),
    CONSTRAINT fk_planner_dtl_hdr
        FOREIGN KEY (planner_hdr_id)
        REFERENCES training_and_placement_planner_hdr (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_planner_dtl_eligibility
        FOREIGN KEY (eligibility_id)
        REFERENCES eligibility_master (id),
    CONSTRAINT chk_planner_dtl_rule CHECK (
        criteria_rule IN (
            'LESS_THAN',
            'GREATER_THAN',
            'LESS_THAN_EQUALS_TO',
            'GREATER_THAN_EQUALS_TO',
            'EQUALS',
            'IN'
        )
    ),
    CONSTRAINT chk_planner_dtl_status CHECK (
        status IN ('DRAFT', 'ACTIVE', 'INACTIVE', 'REJECTED')
    ),
    INDEX idx_planner_dtl_hdr_id (planner_hdr_id),
    INDEX idx_planner_dtl_eligibility_id (eligibility_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 7. planner_question
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS planner_question (
    question_id BIGINT          NOT NULL AUTO_INCREMENT,
    planner_id  BIGINT          NOT NULL,
    question    VARCHAR(255)    NOT NULL,
    field_type  VARCHAR(30)     NOT NULL,
    mandatory   TINYINT(1)      NULL DEFAULT 0,
    CONSTRAINT pk_planner_question PRIMARY KEY (question_id),
    CONSTRAINT fk_planner_question_hdr
        FOREIGN KEY (planner_id)
        REFERENCES training_and_placement_planner_hdr (id)
        ON DELETE CASCADE,
    CONSTRAINT chk_planner_question_field_type CHECK (
        field_type IN (
            'TEXT',
            'EMAIL',
            'NUMBER',
            'FILE',
            'DATE',
            'TEXTAREA',
            'RADIO',
            'MULTI_SELECT'
        )
    ),
    INDEX idx_planner_question_planner_id (planner_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 8. question_option
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS question_option (
    option_id     BIGINT          NOT NULL AUTO_INCREMENT,
    question_id   BIGINT          NOT NULL,
    option_text   VARCHAR(255)    NOT NULL,
    display_order INT             NULL,
    CONSTRAINT pk_question_option PRIMARY KEY (option_id),
    CONSTRAINT fk_question_option_question
        FOREIGN KEY (question_id)
        REFERENCES planner_question (question_id)
        ON DELETE CASCADE,
    INDEX idx_question_option_question_id (question_id),
    INDEX idx_question_option_display_order (
        question_id,
        display_order
    )
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 9. placement_application_hdr
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS placement_application_hdr (
    application_id      BIGINT          NOT NULL AUTO_INCREMENT,
    planner_id          BIGINT          NOT NULL,
    student_id          BIGINT          NOT NULL,
    application_status  VARCHAR(30)     NOT NULL,
    resume_path         VARCHAR(255)    NULL,
    terms_accepted      TINYINT(1)      NULL,
    offer_letter_path   VARCHAR(255)    NULL,
    joining_letter_path VARCHAR(255)    NULL,
    applied_date        DATETIME        NOT NULL,
    CONSTRAINT pk_placement_application_hdr PRIMARY KEY (application_id),
    CONSTRAINT uk_application_student_planner UNIQUE (
        student_id,
        planner_id
    ),
    CONSTRAINT fk_application_hdr_planner
        FOREIGN KEY (planner_id)
        REFERENCES training_and_placement_planner_hdr (id),
    CONSTRAINT fk_application_hdr_student
        FOREIGN KEY (student_id)
        REFERENCES student (student_id),
    CONSTRAINT chk_application_hdr_status CHECK (
        application_status IN (
            'APPLIED',
            'SHORTLISTED',
            'INTERVIEW_SCHEDULED',
            'SELECTED',
            'REJECTED',
            'OFFER_ACCEPTED'
        )
    ),
    INDEX idx_application_hdr_planner_id (planner_id),
    INDEX idx_application_hdr_student_id (student_id),
    INDEX idx_application_hdr_status (application_status),
    INDEX idx_application_hdr_applied_date (applied_date)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 10. placement_application_dtl
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS placement_application_dtl (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    application_id  BIGINT          NULL,
    field_name      VARCHAR(255)    NULL,
    field_value     VARCHAR(255)    NULL,
    CONSTRAINT pk_placement_application_dtl PRIMARY KEY (id),
    CONSTRAINT fk_application_dtl_hdr
        FOREIGN KEY (application_id)
        REFERENCES placement_application_hdr (application_id)
        ON DELETE CASCADE,
    INDEX idx_application_dtl_application_id (application_id),
    INDEX idx_application_dtl_field_name (field_name)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;