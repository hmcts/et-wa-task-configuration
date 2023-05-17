package uk.gov.hmcts.et.taskconfiguration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DmnDecisionTable {

    WA_TASK_CANCELLATION_ET_EW(
        "wa-task-cancellation-employment-et_englandwales",
        "wa-task-cancellation-employment-et_englandwales.dmn"
    ),
    WA_TASK_COMPLETION_ET_EW(
        "wa-task-completion-employment-et_englandwales",
        "wa-task-completion-employment-et_englandwales.dmn"
    ),
    WA_TASK_CONFIGURATION_ET_EW(
        "wa-task-configuration-employment-et_englandwales",
        "wa-task-configuration-employment-et_englandwales.dmn"
    ),
    WA_TASK_INITIATION_ET_EW(
        "wa-task-initiation-employment-et_englandwales",
        "wa-task-initiation-employment-et_englandwales.dmn"
    ),
    WA_TASK_PERMISSIONS_ET_EW(
        "wa-task-permissions-employment-et_englandwales",
        "wa-task-permissions-employment-et_englandwales.dmn"
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
