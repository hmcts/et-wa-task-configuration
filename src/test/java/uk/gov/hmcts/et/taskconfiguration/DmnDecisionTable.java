package uk.gov.hmcts.et.taskconfiguration;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DmnDecisionTable {

    WA_TASK_CANCELLATION_ET_EW(
        "wa-task-cancellation-employment-et_englandwales",
        "wa-task-cancellation-employment-et_englandwales.dmn"
    ),
    WA_TASK_CANCELLATION_MULTIPLE_ET_EW(
        "wa-task-cancellation-employment-et_englandwales_multiple",
        "wa-task-cancellation-employment-et_englandwales_multiple.dmn"
    ),
    WA_TASK_COMPLETION_ET_EW(
        "wa-task-completion-employment-et_englandwales",
        "wa-task-completion-employment-et_englandwales.dmn"
    ),
    WA_TASK_COMPLETION_MULTIPLE_ET_EW(
        "wa-task-completion-employment-et_englandwales_multiple",
        "wa-task-completion-employment-et_englandwales_multiple.dmn"
    ),
    WA_TASK_CONFIGURATION_ET_EW(
        "wa-task-configuration-employment-et_englandwales",
        "wa-task-configuration-employment-et_englandwales.dmn"
    ),
    WA_TASK_CONFIGURATION_MULTIPLE_ET_EW(
        "wa-task-configuration-employment-et_englandwales_multiple",
        "wa-task-configuration-employment-et_englandwales_multiple.dmn"
    ),
    WA_TASK_INITIATION_ET_EW(
        "wa-task-initiation-employment-et_englandwales",
        "wa-task-initiation-employment-et_englandwales.dmn"
    ),
    WA_TASK_INITIATION_MULTIPLE_ET_EW(
        "wa-task-initiation-employment-et_englandwales_multiple",
        "wa-task-initiation-employment-et_englandwales_multiple.dmn"
    ),
    WA_TASK_PERMISSIONS_ET_EW(
        "wa-task-permissions-employment-et_englandwales",
        "wa-task-permissions-employment-et_englandwales.dmn"
    ),
    WA_TASK_PERMISSIONS_MULTIPLE_ET_EW(
        "wa-task-permissions-employment-et_englandwales_multiple",
        "wa-task-permissions-employment-et_englandwales_multiple.dmn"
    ),
    WA_TASK_TYPE_ET_EW(
        "wa-task-types-employment-et_englandwales",
        "wa-task-types-employment-et_englandwales.dmn"
    ),
    WA_TASK_CANCELLATION_ET_SCOTLAND(
        "wa-task-cancellation-employment-et_scotland",
        "wa-task-cancellation-employment-et_scotland.dmn"
    ),
    WA_TASK_CANCELLATION_MULTIPLE_ET_SCOTLAND(
        "wa-task-cancellation-employment-et_scotland_multiple",
        "wa-task-cancellation-employment-et_scotland_multiple.dmn"
    ),
    WA_TASK_COMPLETION_ET_SCOTLAND(
        "wa-task-completion-employment-et_scotland",
        "wa-task-completion-employment-et_scotland.dmn"
    ),
    WA_TASK_COMPLETION_ET_MULTIPLE_SCOTLAND(
        "wa-task-completion-employment-et_scotland_multiple",
        "wa-task-completion-employment-et_scotland_multiple.dmn"
    ),
    WA_TASK_CONFIGURATION_ET_SCOTLAND(
        "wa-task-configuration-employment-et_scotland",
        "wa-task-configuration-employment-et_scotland.dmn"
    ),
    WA_TASK_CONFIGURATION_MULTIPLE_ET_SCOTLAND(
        "wa-task-configuration-employment-et_scotland_multiple",
        "wa-task-configuration-employment-et_scotland_multiple.dmn"
    ),
    WA_TASK_INITIATION_ET_SCOTLAND(
        "wa-task-initiation-employment-et_scotland",
        "wa-task-initiation-employment-et_scotland.dmn"
    ),
    WA_TASK_INITIATION_MULTIPLE_ET_SCOTLAND(
        "wa-task-initiation-employment-et_scotland_multiple",
        "wa-task-initiation-employment-et_scotland_multiple.dmn"
    ),
    WA_TASK_PERMISSIONS_ET_SCOTLAND(
        "wa-task-permissions-employment-et_scotland",
        "wa-task-permissions-employment-et_scotland.dmn"
    ),
    WA_TASK_PERMISSIONS_MULTIPLE_ET_SCOTLAND(
        "wa-task-permissions-employment-et_scotland_multiple",
        "wa-task-permissions-employment-et_scotland_multiple.dmn"
    ),
    WA_TASK_TYPE_ET_SCOTLAND(
        "wa-task-types-employment-et_scotland",
        "wa-task-types-employment-et_scotland.dmn"
    );

    @JsonValue
    private final String key;
    private final String fileName;

    DmnDecisionTable(String key, String fileName) {
        this.key = key;
        this.fileName = fileName;
    }

}
