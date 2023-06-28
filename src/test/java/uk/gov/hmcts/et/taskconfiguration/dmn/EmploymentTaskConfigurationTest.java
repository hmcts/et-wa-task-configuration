package uk.gov.hmcts.et.taskconfiguration.dmn;

import lombok.Builder;
import lombok.Value;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.hmcts.et.taskconfiguration.DmnDecisionTableBaseUnitTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_CONFIGURATION_ET_EW;

class EmploymentTaskConfigurationTest extends DmnDecisionTableBaseUnitTest {

    private static final String DEFAULT_CALENDAR = "https://www.gov.uk/bank-holidays/england-and-wales.json";
    private static final String EXTRA_TEST_CALENDAR = "https://raw.githubusercontent.com/hmcts/"
        + "ia-task-configuration/master/src/test/resources/extra-non-working-day-calendar.json";

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CONFIGURATION_ET_EW;
    }

    private static Map<String, Object> getDefaultCaseData() {
        Map<String, Object> claimant = new HashMap<>();
        claimant.put("claimant_first_names", "George");
        claimant.put("claimant_last_name", "Jetson");

        Map<String, Object> caseData = new HashMap<>();
        caseData.put("claimantIndType", claimant);

        return caseData;
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
                .collect(Collectors.toList());

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
        List<Map<String, Object>> hearingWork = List.of(Map.of(
            "name", "workType",
            "value", "hearing_work",
            "canReconfigure", true
        ));
        List<Map<String, Object>> applications = List.of(Map.of(
            "name", "workType",
            "value", "applications",
            "canReconfigure", true
        ));
        List<Map<String, Object>> accessRequests = List.of(Map.of(
            "name", "workType",
            "value", "access_requests",
            "canReconfigure", true
        ));

        return Stream.of(
            Arguments.of("Et1Vetting", routineWork),
            Arguments.of("ReviewReferralLegalOps", routineWork),
            Arguments.of("ReviewReferralAdmin", routineWork),
            Arguments.of("SendEt1Notification", routineWork),
            Arguments.of("SendEt3Notification", routineWork),
            Arguments.of("ListServeClaim", routineWork),
            Arguments.of("ET3Processing", routineWork),
            Arguments.of("ReviewReferralResponseLegalOps", routineWork),
            Arguments.of("ReviewReferralResponseAdmin", routineWork),
            Arguments.of("ET3Notification", routineWork),
            Arguments.of("IssueInitialConsiderationDirections", routineWork),

            Arguments.of("ReviewReferralJudiciary", decisionMakingWork),
            Arguments.of("ReviewReferralResponseJudiciary", decisionMakingWork),
            Arguments.of("DraftAndSignJudgment", decisionMakingWork),
            Arguments.of("CompleteInitialConsideration", decisionMakingWork),

            Arguments.of("IssuePostHearingDirection", hearingWork),
            Arguments.of("IssueJudgment", hearingWork),
            Arguments.of("ContactTribunalWithanApplication", applications),
            Arguments.of("AmendPartyDetails", applications),
            Arguments.of("WithdrawAllOrPartOfCase", applications),

            Arguments.of("reviewSpecificAccessRequestJudiciary", accessRequests),
            Arguments.of("reviewSpecificAccessRequestLegalOps", accessRequests),
            Arguments.of("reviewSpecificAccessRequestAdmin", accessRequests),
            Arguments.of("reviewSpecificAccessRequestCTSC", accessRequests)
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
                .filter((r) -> r.containsValue("roleCategory"))
                .collect(Collectors.toList());

        assertEquals(expected.get(0).get("name"), resultList.get(0).get("name"));
        assertEquals(expected.get(0).get("value"), resultList.get(0).get("value"));
    }

    public static Stream<Arguments> roleCategory_ScenarioProvider() {
        List<Map<String, Object>> judicial = List.of(Map.of(
            "name", "roleCategory",
            "value", "JUDICIAL",
            "canReconfigure", true
        ));
        List<Map<String, Object>> legalOperations = List.of(Map.of(
            "name", "roleCategory",
            "value", "LEGAL_OPERATIONS",
            "canReconfigure", true
        ));
        List<Map<String, Object>> administrator = List.of(Map.of(
            "name", "roleCategory",
            "value", "ADMIN",
            "canReconfigure", true
        ));
        List<Map<String, Object>> ctsc = List.of(Map.of(
            "name", "roleCategory",
            "value", "CTSC",
            "canReconfigure", true
        ));
        List<Map<String, Object>> adminctsc = List.of(Map.of(
            "name", "roleCategory",
            "value", "ADMIN,CTSC",
            "canReconfigure", true
        ));

        return Stream.of(
            Arguments.of("reviewSpecificAccessRequestJudiciary", judicial),
            Arguments.of("ReviewReferralJudiciary", judicial),
            Arguments.of("ReviewReferralResponseJudiciary", judicial),
            Arguments.of("CompleteInitialConsideration", judicial),
            Arguments.of("DraftAndSignJudgment", judicial),

            Arguments.of("reviewSpecificAccessRequestLegalOps", legalOperations),
            Arguments.of("ReviewRule21Referral", legalOperations),
            Arguments.of("ReviewReferralLegalOps", legalOperations),
            Arguments.of("ET3ReferralLegalOps", legalOperations),
            Arguments.of("ReviewReferralResponseLegalOps", legalOperations),

            Arguments.of("Et1Vetting", adminctsc),
            Arguments.of("ReviewReferralAdmin", adminctsc),
            Arguments.of("ReviewReferralResponseAdmin", adminctsc),
            Arguments.of("ListServeClaim", adminctsc),
            Arguments.of("SendEt1Notification", adminctsc),

            Arguments.of("reviewSpecificAccessRequestAdmin", administrator),
            Arguments.of("ET3Processing", administrator),
            Arguments.of("SendEt3Notification", administrator),
            Arguments.of("IssueInitialConsiderationDirections", administrator),
            Arguments.of("IssuePostHearingDirection", administrator),
            Arguments.of("IssueJudgment", administrator),
            Arguments.of("ContactTribunalWithanApplication", administrator),
            Arguments.of("AmendPartyDetails", administrator),
            Arguments.of("WithdrawAllOrPartOfCase", administrator),

            Arguments.of("reviewSpecificAccessRequestCTSC", ctsc)
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
                                                         "taskId", taskId));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> resultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter((r) -> r.containsValue("description"))
                .collect(Collectors.toList());

        assertEquals(expected.get(0).get("name"), resultList.get(0).get("name"));
        assertEquals(expected.get(0).get("value").replace("${[roleAssignmentId]}", roleAssignmentId)
                         .replace("${[taskId]}", taskId), resultList.get(0).get("value"));
    }

    public static Stream<Arguments> description_ScenarioProvider() {
        List<Map<String, Object>> reviewTheReferralCreate = List.of(Map.of(
            "name", "description",
            "value", "[Review the Referral](/cases/case-details/${[CASE_REFERENCE]}/createReferral1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> reviewTheReferralReply = List.of(Map.of(
            "name", "description",
            "value", "[Review the Referral](/cases/case-details/${[CASE_REFERENCE]}/replyToReferral1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> reviewET1Submission = List.of(Map.of(
            "name", "description",
            "value", "[Review ET1 Submission](cases/case-details/${[CASE_REFERENCE]}/trigger/et1Vetting/et1Vetting1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> issueET1Notification = List.of(Map.of(
            "name", "description",
            "value", "[Issue relevant ET1 Notification](cases/case-details/${[CASE_REFERENCE]}/trigger/"
                + "generateCorrespondence/generateCorrespondence1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> draftJudgment = List.of(Map.of(
            "name", "description",
            "value", "[Draft Judgment and then refer to judge](cases/case-details/${[CASE_REFERENCE]}/"
                + "trigger/createReferral/createReferral1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> listHearingUploadDocument = List.of(Map.of(
            "name", "description",
            "value", "[list hearing if required and then upload document for serving](cases/case-details/"
                + "${[CASE_REFERENCE]}/trigger/uploadDocument/uploadDocument1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> reviewET3Submission = List.of(Map.of(
            "name", "description",
            "value", "[Review ET3 Submission](cases/case-details/${[CASE_REFERENCE]}/trigger/et3Vetting/et3Vetting1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> issueET3Notification = List.of(Map.of(
            "name", "description",
            "value", "[Issue relevant ET3 Notification](cases/case-details/${[CASE_REFERENCE]}/trigger/"
                + "generateCorrespondence/generateCorrespondence1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> initialConsideration = List.of(Map.of(
            "name", "description",
            "value", "[provide your initial consideration](/cases/case-details/${[CASE_REFERENCE]}/trigger/"
                + "initialConsideration/initialConsideration1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> reviewInitialConsideration = List.of(Map.of(
            "name", "description",
            "value", "[Review Initial consideration, update case and then issue relevant correspondence](cases/"
                + "case-details/${[CASE_REFERENCE]}/trigger/generateCorrespondence/generateCorrespondence1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> reviewTheReferralCorrespondence = List.of(Map.of(
            "name", "description",
            "value", "[Review Referral, then issue relevant correspondence](cases/case-details/${[CASE_REFERENCE]}/"
                + "trigger/generateCorrespondence/generateCorrespondence1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> reviewJudgmentPromulgation = List.of(Map.of(
            "name", "description",
            "value", "[Refer the judgment for promulgation, once signed](cases/case-details/${[CASE_REFERENCE]}/"
                + "trigger/addAmendJudgment/addAmendJudgment1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> reviewJudgmentReferral = List.of(Map.of(
            "name", "description",
            "value", "[Review Judgment Referral, then issue relevant correspondence](cases/case-details/"
                + "${[CASE_REFERENCE]}/trigger/generateCorrespondence/generateCorrespondence1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> updatePartyDetails = List.of(Map.of(
            "name", "description",
            "value", "[Update Claimant Details](cases/case-details/${[CASE_REFERENCE]}/trigger/amendClaimantDetails/"
                + "amendClaimantDetails1) [OR Respondent Details](cases/case-details/${[CASE_REFERENCE]}/trigger/"
                + "amendClaimantDetails/amendClaimantDetails1)[, as instructed]",
            "canReconfigure", true
        ));
        List<Map<String, Object>> withdrawCase = List.of(Map.of(
            "name", "description",
            "value", "[Withdraw all or part of the case](cases/case-details/${[CASE_REFERENCE]}/trigger/"
                + "disposeCase/disposeCase1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> reviewApplication = List.of(Map.of(
            "name", "description",
            "value", "[Review Application and refer to judge](cases/case-details/${[CASE_REFERENCE]}/"
                + "trigger/createReferral/createReferral1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> reviewAccessRequest = List.of(Map.of(
            "name", "description",
            "value","[Review Access Request](/role-access/${[taskId]}/assignment/${[roleAssignmentId]}/"
                + "specific-access)",
            "canReconfigure", true
        ));

        return Stream.of(
            Arguments.of("ReviewReferralJudiciary", reviewTheReferralCreate),
            Arguments.of("ReviewReferralLegalOps", reviewTheReferralCreate),
            Arguments.of("ReviewReferralAdmin", reviewTheReferralCreate),

            Arguments.of("ReviewReferralResponseJudiciary", reviewTheReferralReply),
            Arguments.of("ReviewReferralResponseLegalOps", reviewTheReferralReply),
            Arguments.of("ReviewReferralResponseAdmin", reviewTheReferralReply),

            Arguments.of("Et1Vetting", reviewET1Submission),

            Arguments.of("SendEt1Notification", issueET1Notification),

            Arguments.of("Rule21Referral", draftJudgment),

            Arguments.of("ListServeClaim", listHearingUploadDocument),

            Arguments.of("ET3Processing", reviewET3Submission),

            Arguments.of("SendEt3Notification", issueET3Notification),

            Arguments.of("CompleteInitialConsideration", initialConsideration),

            Arguments.of("IssueInitialConsiderationDirections", reviewInitialConsideration),

            Arguments.of("IssuePostHearingDirection", reviewTheReferralCorrespondence),

            Arguments.of("DraftAndSignJudgment", reviewJudgmentPromulgation),

            Arguments.of("IssueJudgment", reviewJudgmentReferral),

            Arguments.of("AmendPartyDetails", updatePartyDetails),

            Arguments.of("WithdrawAllOrPartOfCase", withdrawCase),

            Arguments.of("ContactTribunalWithanApplication", reviewApplication),

            Arguments.of("reviewSpecificAccessRequestJudiciary", reviewAccessRequest),

            Arguments.of("reviewSpecificAccessRequestAdmin", reviewAccessRequest),

            Arguments.of("reviewSpecificAccessRequestLegalOps", reviewAccessRequest),

            Arguments.of("reviewSpecificAccessRequestCTSC", reviewAccessRequest)
        );
    }

    @ParameterizedTest
    @MethodSource("priority_ScenarioProvider")
    void when_taskId_then_return_priority(String taskType,
                                          String urgency,
                                          List<Map<String, String>> expectedMajor,
                                          List<Map<String, String>> expectedMinor) {
        Map<String, Object> caseData = getDefaultCaseData();
        caseData.put("isUrgent", urgency);

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", caseData);
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> majorPriorityResultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter((r) -> r.containsValue("majorPriority"))
                .collect(Collectors.toList());

        assertEquals(expectedMajor.get(0).get("name"), majorPriorityResultList.get(0).get("name"));
        assertEquals(expectedMajor.get(0).get("value"), majorPriorityResultList.get(0).get("value"));

        List<Map<String, Object>> minorPriorityResultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter((r) -> r.containsValue("minorPriority"))
                .collect(Collectors.toList());

        assertEquals(expectedMinor.get(0).get("name"), minorPriorityResultList.get(0).get("name"));
        assertEquals(expectedMinor.get(0).get("value"), minorPriorityResultList.get(0).get("value"));
        assertEquals(expectedMinor.get(0).get("canReconfigure"), minorPriorityResultList.get(0).get("canReconfigure"));
    }

    public static Stream<Arguments> priority_ScenarioProvider() {

        List<Map<String, Object>> defaultDatePriority = List.of(Map.of(
            "name", "priorityDateOriginRef",
            "value", "dueDate",
            "canReconfigure", true
        ));

        List<Map<String, Object>> defaultMajorPriority = List.of(Map.of(
            "name", "majorPriority",
            "value", "5000",
            "canReconfigure", true
        ));
        List<Map<String, Object>> defaultMinorPriority = List.of(Map.of(
            "name", "minorPriority",
            "value", "500",
            "canReconfigure", true
        ));
        List<Map<String, Object>> urgentMajorPriority = List.of(Map.of(
            "name", "majorPriority",
            "value", "1000",
            "canReconfigure", true
        ));
        List<Map<String, Object>> urgentMinorPriority = List.of(Map.of(
            "name", "minorPriority",
            "value", "100",
            "canReconfigure", true
        ));

        return Stream.of(
            Arguments.of("Et1Vetting", "No", defaultMajorPriority, defaultMinorPriority),
            Arguments.of("et3Response", "No", defaultMajorPriority, defaultMinorPriority),
            Arguments.of("DraftAndSignJudgment", "No", defaultMajorPriority, defaultMinorPriority),
            Arguments.of("IssuePostHearingDirection", "No", defaultMajorPriority, defaultMinorPriority),
            Arguments.of("ReviewReferralAdmin", "Yes", urgentMajorPriority, urgentMinorPriority),
            Arguments.of("ReviewReferralResponseAdmin", "Yes", urgentMajorPriority, urgentMinorPriority),
            Arguments.of("ReviewReferralResponseJudiciary", "Yes", urgentMajorPriority, urgentMinorPriority),
            Arguments.of("ReviewReferralJudiciary", "Yes", urgentMajorPriority, urgentMinorPriority),
            Arguments.of("ReviewReferralLegalOps", "Yes", urgentMajorPriority, urgentMinorPriority),
            Arguments.of("ReviewReferralResponseLegalOps", "Yes", urgentMajorPriority, urgentMinorPriority),
            Arguments.of("IssueJudgment", "No", defaultMajorPriority, defaultMinorPriority),
            Arguments.of("CompleteInitialConsideration", "No", defaultMajorPriority, defaultMinorPriority),
            Arguments.of("SendEt3Notification", "No", defaultMajorPriority, defaultMinorPriority),
            Arguments.of("ContactTribunalWithanApplication", "No", defaultMajorPriority, defaultMinorPriority)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "reviewSpecificAccessRequestJudiciary", "reviewSpecificAccessRequestLegalOps",
        "reviewSpecificAccessRequestAdmin","reviewSpecificAccessRequestCTSC"
    })
    void should_return_request_value_when_role_assignment_id_exists_in_task_attributes(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();
        String roleAssignmentId = UUID.randomUUID().toString();
        String taskId = UUID.randomUUID().toString();
        inputVariables.putValue("caseData", getDefaultCaseData());
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType,
                                                         "roleAssignmentId", roleAssignmentId,
                                                         "taskId", taskId));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> resultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter((r) -> r.containsValue("additionalProperties_roleAssignmentId"))
                .collect(Collectors.toList());

        assertTrue(resultList.contains(Map.of(
            "name", "additionalProperties_roleAssignmentId",
            "value", roleAssignmentId,
            "canReconfigure", true
        )));
    }

    @ParameterizedTest
    @MethodSource("nameAndValueScenarioProvider")
    void when_caseData_and_taskType_then_return_expected_name_and_value_rows(Scenario scenario) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", scenario.caseData);
        inputVariables.putValue("taskAttributes", scenario.getTaskAttributes());

        List<Map<String, Object>> expected = getExpectedValues(scenario);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList().size(), is(expected.size()));
    }

    @Value
    @Builder
    private static class Scenario {
        Map<String, Object> caseData;
        Map<String, Object> taskAttributes;
        String expectedCaseNameValue;
        String expectedAppealTypeValue;
        String expectedRegionValue;
        String expectedLocationValue;
        String expectedLocationNameValue;
        String expectedCaseManagementCategoryValue;
        String expectedWorkType;
        String expectedRoleCategory;
        String expectedDescriptionValue;
        String expectedReconfigureValue;
        String expectedDueDateOrigin;
        String expectedDueDateTime;
        String expectedDueDateIntervalDays;
    }

    private static Stream<Scenario> nameAndValueScenarioProvider() {
        String dateOrigin = ZonedDateTime.now(ZoneId.of("UTC")).toString();
        Scenario givenSomeCaseDataAndTaskTypeIsEmptyThenExpectNoWorkTypeRuleScenario =
            Scenario.builder()
                .caseData(Map.of(
                    "appellantGivenNames", "some appellant given names",
                    "appellantFamilyName", "some appellant family name",
                    "caseManagementCategory","Employment"
                    )
                )
                .expectedCaseNameValue("some appellant given names some appellant family name")
                .expectedCaseManagementCategoryValue("Employment")
                .expectedDueDateOrigin(dateOrigin)
                .build();

        return Stream.of(
            givenSomeCaseDataAndTaskTypeIsEmptyThenExpectNoWorkTypeRuleScenario
        );
    }

    private List<Map<String, Object>> getExpectedValues(Scenario scenario) {
        List<Map<String, Object>> rules = new ArrayList<>();
        getExpectedValueWithReconfigure(
            rules,
            "caseName",
            scenario.getExpectedCaseNameValue(),
            scenario.getExpectedReconfigureValue()
        );
        getExpectedValue(rules, "caseManagementCategory", "Employment");
        getExpectedValue(rules, "dueDateOrigin", scenario.getExpectedDueDateOrigin());
        getExpectedValue(rules, "dueDateTime", scenario.getExpectedDueDateTime());
        getExpectedValue(rules, "dueDateNonWorkingCalendar", DEFAULT_CALENDAR + ", " + EXTRA_TEST_CALENDAR);
        getExpectedValue(rules, "priorityDateOriginRef", "dueDate");
        getExpectedValue(rules, "dueDateNonWorkingDaysOfWeek", "SATURDAY,SUNDAY");
        getExpectedValue(rules, "dueDateSkipNonWorkingDays", "false");
        getExpectedValue(rules, "dueDateMustBeWorkingDay", "No");
        getExpectedValue(rules, "calculatedDates", "nextHearingDate,dueDate,priorityDate");
        return rules;
    }

    private void getExpectedValue(List<Map<String, Object>> rules, String name, String value) {
        Map<String, Object> rule = new HashMap<>();
        rule.put("name", name);
        rule.put("value", value);
        rule.put("canReconfigure", true);
        rules.add(rule);
    }

    private void getExpectedValueWithReconfigure(List<Map<String, Object>> rules, String name, String value,
                                                 String reconfigure) {
        Map<String, Object> rule = new HashMap<>();
        rule.put("name", name);
        rule.put("value", value);
        rule.put("canReconfigure", Boolean.valueOf(reconfigure));
        rules.add(rule);
    }

    private boolean validNow(ZonedDateTime expected, ZonedDateTime actual) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        return actual != null
            && (expected.isEqual(actual) || expected.isBefore(actual))
            && (now.isEqual(actual) || now.isAfter(actual));
    }

    @Test
    void when_any_task_then_return_expected_non_working_days_of_week_config() {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", "draftCaseCreated"));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertTrue(dmnDecisionTableResult.getResultList().contains(Map.of(
            "name", "dueDateNonWorkingDaysOfWeek",
            "value", "SATURDAY,SUNDAY",
            "canReconfigure", true
        )));
    }

    @ParameterizedTest
    @CsvSource({
        "draftCaseCreated"
    })
    void when_taskId_then_return_due_date_skip_non_working_days_false(String taskType) {
        VariableMap inputVariables = new VariableMapImpl();

        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> dueDateSkipNonWorkingDaysResultList = dmnDecisionTableResult.getResultList().stream()
            .filter((r) -> r.containsValue("dueDateSkipNonWorkingDays"))
            .collect(Collectors.toList());

        assertEquals(1, dueDateSkipNonWorkingDaysResultList.size());

        assertEquals(Map.of(
            "name", "dueDateSkipNonWorkingDays",
            "value", "false",
            "canReconfigure", true
        ), dueDateSkipNonWorkingDaysResultList.get(0));
    }

    private ZonedDateTime parseCamundaTimestamp(String datetime) {
        String[] parts = datetime.split("[Z+]");
        String zone = datetime.substring(datetime.indexOf("[") + 1, datetime.lastIndexOf("]"));
        return ZonedDateTime.of(LocalDateTime.parse(parts[0]), ZoneId.of(zone));
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();

        assertThat(logic.getRules().size(), is(42));
    }
}
