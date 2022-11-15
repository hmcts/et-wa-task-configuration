package uk.gov.hmcts.et.taskconfiguration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DmnDecisionTable {

    WA_TASK_COMPLETION_WA_WACASETYPE(
        "wa-task-completion-wa-wacasetype",
        "wa-task-completion-wa-wacasetype.dmn"
    ),
    WA_TASK_PERMISSIONS_WA_WACASETYPE(
        "wa-task-permissions-wa-wacasetype",
        "wa-task-permissions-wa-wacasetype.dmn"
    ),
    WA_TASK_CONFIGURATION_WA_WACASETYPE(
        "wa-task-configuration-wa-wacasetype",
        "wa-task-configuration-wa-wacasetype.dmn"
    ),
    WA_TASK_CANCELLATION_WA_WACASETYPE(
        "wa-task-cancellation-wa-wacasetype",
        "wa-task-cancellation-wa-wacasetype.dmn"
    ),
    WA_TASK_INITIATION_WA_WACASETYPE(
        "wa-task-initiation-wa-wacasetype",
        "wa-task-initiation-wa-wacasetype.dmn"
    ),
    WA_TASK_TYPES_WA_WACASETYPE(
        "wa-task-types-wa-wacasetype",
        "wa-task-types-wa-wacasetype.dmn"
    ),
    WA_TASK_INITIATION_ET_VETTING(
        "wa-task-initiation-et-vetting",
        "wa-task-initiation-et-vetting.dmn"
    );

    @JsonValue
    private final String key;
    private final String fileName;

    DmnDecisionTable(String key, String fileName) {
        this.key = key;
        this.fileName = fileName;
    }

    public String getKey() {
        return key;
    }

    public String getFileName() {
        return fileName;
    }
}
