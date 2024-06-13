package uk.gov.hmcts.et.taskconfiguration.dmn.multiples;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.et.taskconfiguration.DmnDecisionTableBaseUnitTest;
import uk.gov.hmcts.et.taskconfiguration.utility.HelperService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_CONFIGURATION_MULTIPLE_ET_EW;
import static uk.gov.hmcts.et.taskconfiguration.utility.ConfigurationUtility.EXTRA_TEST_CALENDAR;
import static uk.gov.hmcts.et.taskconfiguration.utility.ConfigurationUtility.ISURGENT_REPLY_NO;
import static uk.gov.hmcts.et.taskconfiguration.utility.ConfigurationUtility.ISURGENT_REPLY_YES;
import static uk.gov.hmcts.et.taskconfiguration.utility.ConfigurationUtility.IS_URGENT;
import static uk.gov.hmcts.et.taskconfiguration.utility.ConfigurationUtility.NOT_URGENT;

class EmploymentTaskConfigurationMultipleTestScot extends DmnDecisionTableBaseUnitTest {

    private static final String DEFAULT_CALENDAR = "https://www.gov.uk/bank-holidays/england-and-wales.json";


    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CONFIGURATION_MULTIPLE_ET_EW;
    }

    private static Map<String, Object> getDefaultCaseData() {
        Map<String, Object> caseData = new HashMap<>();
        caseData.put("multipleName", "Big Multiple");

        return caseData;
    }

    @Test
    void testCaseName() {
        // Given
        Map<String, Object> caseData = getDefaultCaseData();

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", caseData);
        inputVariables.putValue("taskAttributes", Map.of("taskType", "Et1Vetting"));

        // When
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        // Then
        assertEquals("Big Multiple", dmnDecisionTableResult.getResultList().get(0).get("value"));
    }

    @ParameterizedTest
    @MethodSource("cmlAndCmc_ScenarioProvider")
    void testCMLandCMC(String regionId,
                       String baseLocation,
                       String managingOffice,
                       String cmCategory,
                       String expectedRegion,
                       String expectedLocation,
                       String expectedLocationName,
                       String expectedCMC) {
        // Given
        Map<String, Object> caseData = getDefaultCaseData();

        Map<String, Object> caseManagementLocation = new HashMap<>();
        if (!regionId.isBlank()) {
            caseManagementLocation.put("region", regionId);
        }
        if (!baseLocation.isBlank()) {
            caseManagementLocation.put("baseLocation", baseLocation);
        }
        if (!caseManagementLocation.isEmpty()) {
            caseData.put("caseManagementLocation", caseManagementLocation);
        }
        if (!managingOffice.isEmpty()) {
            caseData.put("managingOffice", managingOffice);
        }

        Map<String, Object> caseManagementCategory = new HashMap<>();
        if (!cmCategory.isBlank()) {
            caseManagementCategory.put("selectedLabel", cmCategory);
        }
        if (!caseManagementCategory.isEmpty()) {
            caseData.put("caseManagementCategory", caseManagementCategory);
        }

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", caseData);
        inputVariables.putValue("taskAttributes", Map.of("taskType", "Et1Vetting"));

        // When
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> resultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .toList();

        // Then
        assertEquals(expectedRegion, resultList.get(1).get("value"));
        assertEquals(expectedLocation, resultList.get(2).get("value"));
        assertEquals(expectedLocationName, resultList.get(3).get("value"));
        assertEquals(expectedCMC, resultList.get(4).get("value"));
    }

    public static Stream<Arguments> cmlAndCmc_ScenarioProvider() {
        return Stream.of(
            Arguments.of("", "", "", "", "1", "21153", "London Central", "Employment"),
            Arguments.of("3", "36313", "Leeds", "Test", "3", "36313", "Leeds", "Employment")
        );
    }

    @ParameterizedTest
    @MethodSource("hearingDate_ScenarioProvider")
    void testHearingDate(String nextListedDate,
                         String expectedNextHearingDate) {
        // Given
        Map<String, Object> caseData = getDefaultCaseData();
        caseData.put("nextListedDate", nextListedDate);

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", caseData);
        inputVariables.putValue("taskAttributes", Map.of("taskType", "Et1Vetting"));

        // When
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> resultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .toList();

        // Then
        assertEquals(expectedNextHearingDate, resultList.get(5).get("value"));
    }

    public static Stream<Arguments> hearingDate_ScenarioProvider() {
        return Stream.of(
            Arguments.of("2024-02-01", "2024-02-01"),
            Arguments.of(null, "")
        );
    }

    @ParameterizedTest
    @MethodSource("workType_ScenarioProvider")
    void when_taskId_then_return_workType(String taskType, List<Map<String, String>> expected) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", getDefaultCaseData());
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> resultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter((r) -> r.containsValue("workType"))
                .toList();

        assertEquals(expected.get(0).get("name"), resultList.get(0).get("name"));
        assertEquals(expected.get(0).get("value"), resultList.get(0).get("value"));
        assertEquals(expected.get(0).get("canReconfigure"), resultList.get(0).get("canReconfigure"));
    }

    public static Stream<Arguments> workType_ScenarioProvider() {
        List<Map<String, Object>> routineWork = List.of(Map.of(
            "name", "workType",
            "value", "routine_work",
            "canReconfigure", true
        ));
        List<Map<String, Object>> decisionMakingWork = List.of(Map.of(
            "name", "workType",
            "value", "decision_making_work",
            "canReconfigure", true
        ));

        return Stream.of(
            Arguments.of("ReviewReferralAdminMultiple", routineWork),
            Arguments.of("ReviewReferralLegalOpsMultiple", routineWork),
            Arguments.of("ReviewReferralJudiciaryMultiple", decisionMakingWork),
            Arguments.of("ReviewReferralResponseJudiciaryMultiple", decisionMakingWork)

        );
    }

    @ParameterizedTest
    @MethodSource("roleCategory_ScenarioProvider")
    void when_taskId_then_return_roleCategory(String taskType, List<Map<String, String>> expected) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", getDefaultCaseData());
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> resultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter(r -> r.containsValue("roleCategory"))
                .toList();

        assertEquals(expected.get(0).get("name"), resultList.get(0).get("name"));
        assertEquals(expected.get(0).get("value"), resultList.get(0).get("value"));
        assertEquals(expected.get(0).get("canReconfigure"), resultList.get(0).get("canReconfigure"));
    }

    public static Stream<Arguments> roleCategory_ScenarioProvider() {
        List<Map<String, Object>> administrator = List.of(Map.of(
            "name", "roleCategory",
            "value", "ADMIN",
            "canReconfigure", true
        ));
        List<Map<String, Object>> legalOps = List.of(Map.of(
            "name", "roleCategory",
            "value", "LEGAL_OPERATIONS",
            "canReconfigure", true
        ));
        List<Map<String, Object>> judicial = List.of(Map.of(
            "name", "roleCategory",
            "value", "JUDICIAL",
            "canReconfigure", true
        ));

        return Stream.of(
            Arguments.of("ReviewReferralAdminMultiple", administrator),
            Arguments.of("ReviewReferralLegalOpsMultiple", legalOps),
            Arguments.of("ReviewReferralJudiciaryMultiple", judicial),
            Arguments.of("MultiplesReviewReferralResponseLegalOps", legalOps)
        );
    }

    @ParameterizedTest
    @MethodSource("description_ScenarioProvider")
    void when_taskId_then_return_description(String taskType, List<Map<String, String>> expected) {
        VariableMap inputVariables = new VariableMapImpl();
        String roleAssignmentId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        inputVariables.putValue("caseData", getDefaultCaseData());
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType,
                                                         "roleAssignmentId", roleAssignmentId,
                                                         "taskId", taskId
        ));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> resultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter(r -> r.containsValue("description"))
                .toList();

        assertEquals(expected.get(0).get("name"), resultList.get(0).get("name"));
        assertEquals(expected.get(0).get("value")
                         .replace("${[roleAssignmentId]}", roleAssignmentId)
                         .replace("${[taskId]}", taskId), resultList.get(0).get("value"));
        assertEquals(expected.get(0).get("canReconfigure"), resultList.get(0).get("canReconfigure"));
    }

    public static Stream<Arguments> description_ScenarioProvider() {
        List<Map<String, Object>> descReferralTab = List.of(Map.of(
            "name", "description",
            "value", "[Review the Referral](/cases/case-details/${[CASE_REFERENCE]}#Referrals)",
            "canReconfigure", true
        ));


        return Stream.of(
            Arguments.of("ReviewReferralAdminMultiple", descReferralTab),
            Arguments.of("ReviewReferralLegalOpsMultiple", descReferralTab),
            Arguments.of("ReviewReferralJudiciaryMultiple", descReferralTab),
            Arguments.of("MultiplesReviewReferralResponseLegalOps", descReferralTab)
        );
    }

    @ParameterizedTest
    @MethodSource("priority_ScenarioProvider")
    void when_taskId_then_return_priority(String taskType,
                                          String rawReferralCollection,
                                          List<Map<String, String>> expectedIntervalDays,
                                          List<Map<String, String>> expectedMajor,
                                          List<Map<String, String>> expectedMinor,
                                          List<Map<String, String>> expectedPriorityDateOrigin,
                                          List<Map<String, String>> expectedPriorityDateEarliest) {
        Map<String, Object> caseData = getDefaultCaseData();

        if (!rawReferralCollection.isBlank()) {
            Map<String, Object> parsedReferralCollection = HelperService.mapData(rawReferralCollection);
            caseData.put("referralCollection", parsedReferralCollection.get("referralCollection"));
        }

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", caseData);
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> intervalDaysResultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter(r -> r.containsValue("dueDateIntervalDays"))
                .toList();

        assertEquals(expectedIntervalDays.get(0).get("name"), intervalDaysResultList.get(0).get("name"));
        assertEquals(expectedIntervalDays.get(0).get("value"), intervalDaysResultList.get(0).get("value"));
        assertEquals(
            expectedIntervalDays.get(0).get("canReconfigure"),
            intervalDaysResultList.get(0).get("canReconfigure")
        );

        List<Map<String, Object>> majorPriorityResultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter(r -> r.containsValue("majorPriority"))
                .toList();

        assertEquals(expectedMajor.get(0).get("name"), majorPriorityResultList.get(0).get("name"));
        assertEquals(expectedMajor.get(0).get("value"), majorPriorityResultList.get(0).get("value"));
        assertEquals(expectedMajor.get(0).get("canReconfigure"), majorPriorityResultList.get(0).get("canReconfigure"));

        List<Map<String, Object>> minorPriorityResultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter(r -> r.containsValue("minorPriority"))
                .toList();

        assertEquals(expectedMinor.get(0).get("name"), minorPriorityResultList.get(0).get("name"));
        assertEquals(expectedMinor.get(0).get("value"), minorPriorityResultList.get(0).get("value"));
        assertEquals(expectedMinor.get(0).get("canReconfigure"), minorPriorityResultList.get(0).get("canReconfigure"));

        if (expectedPriorityDateOrigin != null) {
            List<Map<String, Object>> priorityDateRefResultList =
                dmnDecisionTableResult
                    .getResultList()
                    .stream()
                    .filter(r -> r.containsValue("priorityDateOriginRef"))
                    .toList();

            assertEquals(
                expectedPriorityDateOrigin.get(0).get("name"),
                priorityDateRefResultList.get(0).get("name")
            );
            assertEquals(
                expectedPriorityDateOrigin.get(0).get("value"),
                priorityDateRefResultList.get(0).get("value")
            );
            assertEquals(
                expectedPriorityDateOrigin.get(0).get("canReconfigure"),
                priorityDateRefResultList.get(0).get("canReconfigure")
            );
        }

        if (expectedPriorityDateEarliest != null) {
            List<Map<String, Object>> priorityDateEarResultList =
                dmnDecisionTableResult
                    .getResultList()
                    .stream()
                    .filter(r -> r.containsValue("priorityDateOriginEarliest"))
                    .toList();

            assertEquals(
                expectedPriorityDateEarliest.get(0).get("name"),
                priorityDateEarResultList.get(0).get("name")
            );
            assertEquals(
                expectedPriorityDateEarliest.get(0).get("value"),
                priorityDateEarResultList.get(0).get("value")
            );
            assertEquals(
                expectedPriorityDateEarliest.get(0).get("canReconfigure"),
                priorityDateEarResultList.get(0).get("canReconfigure")
            );
        }
    }

    public static Stream<Arguments> priority_ScenarioProvider() {
        List<Map<String, Object>> dueDateIntervalDays1NoReconfigure = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "1",
            "canReconfigure", false
        ));
        List<Map<String, Object>> dueDateIntervalDays2NoReconfigure = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "2",
            "canReconfigure", false
        ));

        List<Map<String, Object>> defaultMajorPriorityNoReconfigure = List.of(Map.of(
            "name", "majorPriority",
            "value", "5000",
            "canReconfigure", false
        ));
        List<Map<String, Object>> defaultMinorPriorityNoReconfigure = List.of(Map.of(
            "name", "minorPriority",
            "value", "500",
            "canReconfigure", false
        ));
        List<Map<String, Object>> urgentMajorPriority = List.of(Map.of(
            "name", "majorPriority",
            "value", "1000",
            "canReconfigure", false
        ));
        List<Map<String, Object>> urgentMinorPriority = List.of(Map.of(
            "name", "minorPriority",
            "value", "100",
            "canReconfigure", false
        ));

        List<Map<String, Object>> priorityDateOriginEar = List.of(Map.of(
            "name", "priorityDateOriginEarliest",
            "value", "dueDate, nextHearingDate",
            "canReconfigure", true
        ));
        List<Map<String, Object>> priorityDateOriginRef = List.of(Map.of(
            "name", "priorityDateOriginRef",
            "value", "dueDate",
            "canReconfigure", true
        ));

        return Stream.of(
            Arguments.of("ReviewReferralAdminMultiple", IS_URGENT,
                         dueDateIntervalDays1NoReconfigure, urgentMajorPriority, urgentMinorPriority,
                         null, priorityDateOriginEar
            ),
            Arguments.of("ReviewReferralAdminMultiple", NOT_URGENT,
                         dueDateIntervalDays2NoReconfigure, defaultMajorPriorityNoReconfigure,
                         defaultMinorPriorityNoReconfigure, null, priorityDateOriginEar
            ),
            Arguments.of("ReviewReferralLegalOpsMultiple", IS_URGENT,
                         dueDateIntervalDays1NoReconfigure, urgentMajorPriority, urgentMinorPriority,
                         null, priorityDateOriginEar
            ),
            Arguments.of("ReviewReferralLegalOpsMultiple", NOT_URGENT,
                         dueDateIntervalDays2NoReconfigure, defaultMajorPriorityNoReconfigure,
                         defaultMinorPriorityNoReconfigure, null, priorityDateOriginEar
            ),
            Arguments.of("ReviewReferralJudiciaryMultiple", IS_URGENT,
                         dueDateIntervalDays1NoReconfigure, urgentMajorPriority, urgentMinorPriority,
                         null, priorityDateOriginEar
            ),
            Arguments.of("ReviewReferralJudiciaryMultiple", NOT_URGENT,
                         dueDateIntervalDays2NoReconfigure, defaultMajorPriorityNoReconfigure,
                         defaultMinorPriorityNoReconfigure, null, priorityDateOriginEar
            ),
            Arguments.of("MultiplesReviewReferralResponseLegalOps", ISURGENT_REPLY_YES,
                         dueDateIntervalDays1NoReconfigure, urgentMajorPriority, urgentMinorPriority,
                         priorityDateOriginRef, null
            ),
            Arguments.of("MultiplesReviewReferralResponseLegalOps", ISURGENT_REPLY_NO,
                         dueDateIntervalDays2NoReconfigure, defaultMajorPriorityNoReconfigure,
                         defaultMinorPriorityNoReconfigure, priorityDateOriginRef, null
            )
        );
    }

    @Test
    void when_any_taskId_then_return_due_date_variables() {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", "someTaskType"));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> resultList =
            dmnDecisionTableResult.getResultList().stream().toList();
        assertEquals(13, resultList.size());

        assertEquals(Map.of(
            "name", "calculatedDates",
            "value", "nextHearingDate,dueDate,priorityDate",
            "canReconfigure", true
        ), resultList.get(6));

        assertEquals(Map.of(
            "name", "dueDateTime",
            "value", "16:00",
            "canReconfigure", true
        ), resultList.get(8));

        assertEquals(Map.of(
            "name", "dueDateNonWorkingCalendar",
            "value", "https://www.gov.uk/bank-holidays/england-and-wales.json, "
                + "https://raw.githubusercontent.com/hmcts/civil-wa-task-configuration/"
                + "master/src/main/resources/privilege-calendar.json",
            "canReconfigure", true
        ), resultList.get(9));

        assertEquals(Map.of(
            "name", "dueDateNonWorkingDaysOfWeek",
            "value", "SATURDAY,SUNDAY",
            "canReconfigure", true
        ), resultList.get(10));

        assertEquals(Map.of(
            "name", "dueDateSkipNonWorkingDays",
            "value", "true",
            "canReconfigure", true
        ), resultList.get(11));

        assertEquals(Map.of(
            "name", "dueDateMustBeWorkingDay",
            "value", "Yes",
            "canReconfigure", true
        ), resultList.get(12));
    }

    @Test
    void when_caseData_and_taskType_then_return_expected_name_and_value_rows() {
        Map<String, Object> caseData = getDefaultCaseData();

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", caseData);
        inputVariables.putValue("taskAttributes", Map.of("taskType", "someTask"));

        List<Map<String, Object>> expectedResults = getExpectedValues();

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        List<Map<String, Object>> actualResults = dmnDecisionTableResult.getResultList();

        assertEquals(actualResults.size(), expectedResults.size());

        for (int idx = 0; idx < actualResults.size(); idx++) {
            assertEquals(
                actualResults.get(idx).get("name"),
                expectedResults.get(idx).get("name")
            );
            assertEquals(
                actualResults.get(idx).get("canReconfigure"),
                expectedResults.get(idx).get("canReconfigure")
            );
            if (!actualResults.get(idx).get("name").equals("dueDateOrigin")) {
                assertEquals(
                    actualResults.get(idx).get("value"),
                    expectedResults.get(idx).get("value")
                );
            }
        }
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();

        assertThat(logic.getRules().size(), is(27));
    }

    private List<Map<String, Object>> getExpectedValues() {
        List<Map<String, Object>> rules = new ArrayList<>();
        HelperService.getExpectedValueWithReconfigure(rules, "caseName", "Big Multiple", true);
        HelperService.getExpectedValueWithReconfigure(rules, "region", "1", true);
        HelperService.getExpectedValueWithReconfigure(rules, "location", "21153", true);
        HelperService.getExpectedValueWithReconfigure(rules, "locationName", "London Central", true);
        HelperService.getExpectedValueWithReconfigure(rules, "caseManagementCategory", "Employment", false);
        HelperService.getExpectedValueWithReconfigure(rules, "nextHearingDate", "", true);
        HelperService.getExpectedValueWithReconfigure(
            rules, "calculatedDates", "nextHearingDate,dueDate,priorityDate", true);
        HelperService.getExpectedValueWithReconfigure(rules, "dueDateOrigin", null, true);
        HelperService.getExpectedValueWithReconfigure(rules, "dueDateTime", "16:00", true);
        HelperService.getExpectedValueWithReconfigure(
            rules, "dueDateNonWorkingCalendar", DEFAULT_CALENDAR + ", " + EXTRA_TEST_CALENDAR, true);
        HelperService.getExpectedValueWithReconfigure(rules, "dueDateNonWorkingDaysOfWeek", "SATURDAY,SUNDAY", true);
        HelperService.getExpectedValueWithReconfigure(rules, "dueDateSkipNonWorkingDays", "true", true);
        HelperService.getExpectedValueWithReconfigure(rules, "dueDateMustBeWorkingDay", "Yes", true);
        return rules;
    }
}
