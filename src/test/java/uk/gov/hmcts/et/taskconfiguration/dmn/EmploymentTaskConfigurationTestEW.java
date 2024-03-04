package uk.gov.hmcts.et.taskconfiguration.dmn;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_CONFIGURATION_ET_EW;

class EmploymentTaskConfigurationTestEW extends DmnDecisionTableBaseUnitTest {

    private static final String DEFAULT_CALENDAR = "https://www.gov.uk/bank-holidays/england-and-wales.json";
    private static final String EXTRA_TEST_CALENDAR = "https://raw.githubusercontent.com/hmcts/"
        + "civil-wa-task-configuration/master/src/main/resources/privilege-calendar.json";

    public static final String IS_URGENT =
        "{\"referralCollection\": ["
            + "{\"value\": {\"isUrgent\": \"No\",\"referralNumber\": \"1\"}},"
            + "{\"value\": {\"isUrgent\": \"Yes\",\"referralNumber\": \"2\"}}"
            + "]}";
    public static final String NOT_URGENT =
        "{\"referralCollection\": ["
            + "{\"value\": {\"isUrgent\": \"Yes\",\"referralNumber\": \"1\"}},"
            + "{\"value\": {\"isUrgent\": \"No\",\"referralNumber\": \"2\"}}"
            + "]}";

    public static final String ISURGENT_REPLY_YES =
        "{\"referralCollection\":["
            + "{\"value\":{\"referralReplyCollection\":["
            + "{\"value\":{\"isUrgentReply\":\"No\",\"replyDateTime\":\"2023-10-01T12:00:00.00\"}},"
            + "{\"value\":{\"isUrgentReply\":\"Yes\",\"replyDateTime\":\"2023-10-01T14:00:00.00\"}}"
            + "]}},"
            + "{\"value\":{\"referralReplyCollection\":["
            + "{\"value\":{\"isUrgentReply\":\"No\",\"replyDateTime\":\"2023-10-01T13:00:00.00\"}}"
            + "]}}]}";
    public static final String ISURGENT_REPLY_NO =
        "{\"referralCollection\":["
            + "{\"value\":{\"referralReplyCollection\":["
            + "{\"value\":{\"isUrgentReply\":\"Yes\",\"replyDateTime\":\"2023-10-01T12:00:00.00\"}},"
            + "{\"value\":{\"isUrgentReply\":\"No\",\"replyDateTime\":\"2023-10-01T14:00:00.00\"}}"
            + "]}},"
            + "{\"value\":{\"referralReplyCollection\":["
            + "{\"value\":{\"isUrgentReply\":\"Yes\",\"replyDateTime\":\"2023-10-01T13:00:00.00\"}}"
            + "]}}]}";

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
    @MethodSource("respondentCollection_ScenarioProvider")
    void testCaseNameWithRespondentCollection(String rawRespondentCollection, String expectedCaseName) {
        // Given
        Map<String, Object> caseData = getDefaultCaseData();

        if (!rawRespondentCollection.isBlank()) {
            Map<String, Object> parsedRespondentCollection = HelperService.mapData(rawRespondentCollection);
            caseData.put("respondentCollection", parsedRespondentCollection.get("respondentCollection"));
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
                .filter((r) -> r.containsValue("caseName"))
                .toList();

        // Then
        assertEquals(expectedCaseName, resultList.get(0).get("value"));
    }

    public static Stream<Arguments> respondentCollection_ScenarioProvider() {
        return Stream.of(
            // No respondentCollection
            Arguments.of(
                "",
                "George Jetson"
            ),
            // empty respondentCollection
            Arguments.of(
                "{\"respondentCollection\":[]}",
                "George Jetson"
            ),
            // respondentCollection with one Respondent
            Arguments.of(
                "{\"respondentCollection\":["
                    + "{ \"value\":{ \"respondent_name\":\"Cosmo Spacely\" }}"
                    + "]}",
                "George Jetson v Cosmo Spacely"
            ),
            // respondentCollection with 2+
            Arguments.of(
                "{\"respondentCollection\":["
                    + "{ \"value\":{ \"respondent_name\":\"Cosmo Spacely\" }},"
                    + "{ \"value\":{ \"respondent_name\":\"Coswell Cogs\" }}"
                    + "]}",
                "George Jetson v Cosmo Spacely"
            )
        );
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
            Arguments.of("3", "36313", "Leeds", "Test", "3", "36313", "Leeds", "Test")
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
            Arguments.of("IssueInitialConsiderationDirections", routineWork),

            Arguments.of("ReviewReferralJudiciary", decisionMakingWork),
            Arguments.of("ReviewReferralResponseJudiciary", decisionMakingWork),
            Arguments.of("DraftAndSignJudgment", decisionMakingWork),
            Arguments.of("CompleteInitialConsideration", decisionMakingWork),

            Arguments.of("IssuePostHearingDirection", hearingWork),
            Arguments.of("IssueJudgment", hearingWork),

            Arguments.of("ContactTribunalWithAnApplication", applications),
            Arguments.of("AmendClaimantDetails", applications),
            Arguments.of("AmendRespondentDetails", applications),
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
                .toList();

        assertEquals(expected.get(0).get("name"), resultList.get(0).get("name"));
        assertEquals(expected.get(0).get("value"), resultList.get(0).get("value"));
        assertEquals(expected.get(0).get("canReconfigure"), resultList.get(0).get("canReconfigure"));
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

        return Stream.of(
            Arguments.of("reviewSpecificAccessRequestJudiciary", judicial),
            Arguments.of("ReviewReferralJudiciary", judicial),
            Arguments.of("ReviewReferralResponseJudiciary", judicial),
            Arguments.of("CompleteInitialConsideration", judicial),
            Arguments.of("DraftAndSignJudgment", judicial),

            Arguments.of("reviewSpecificAccessRequestLegalOps", legalOperations),
            Arguments.of("ReviewRule21Referral", legalOperations),
            Arguments.of("ReviewReferralLegalOps", legalOperations),
            Arguments.of("ReviewReferralResponseLegalOps", legalOperations),

            Arguments.of("Et1Vetting", administrator),
            Arguments.of("ReviewReferralAdmin", administrator),
            Arguments.of("ReviewReferralResponseAdmin", administrator),
            Arguments.of("ListServeClaim", administrator),
            Arguments.of("SendEt1Notification", administrator),
            Arguments.of("reviewSpecificAccessRequestAdmin", administrator),
            Arguments.of("ET3Processing", administrator),
            Arguments.of("SendEt3Notification", administrator),
            Arguments.of("IssueInitialConsiderationDirections", administrator),
            Arguments.of("ListAHearing", administrator),
            Arguments.of("IssuePostHearingDirection", administrator),
            Arguments.of("IssueJudgment", administrator),
            Arguments.of("ContactTribunalWithAnApplication", administrator),
            Arguments.of("AmendClaimantDetails", administrator),
            Arguments.of("AmendRespondentDetails", administrator),
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
                .toList();

        assertEquals(expected.get(0).get("name"), resultList.get(0).get("name"));
        assertEquals(expected.get(0).get("value")
                         .replace("${[roleAssignmentId]}", roleAssignmentId)
                         .replace("${[taskId]}", taskId), resultList.get(0).get("value"));
        assertEquals(expected.get(0).get("canReconfigure"), resultList.get(0).get("canReconfigure"));
    }

    public static Stream<Arguments> description_ScenarioProvider() {
        List<Map<String, Object>> descET1Vetting = List.of(Map.of(
            "name", "description",
            "value", "[ET1 Vetting](/cases/case-details/${[CASE_REFERENCE]}/trigger/et1Vetting/et1Vetting1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> descReferralTab = List.of(Map.of(
            "name", "description",
            "value", "[Review the Referral](/cases/case-details/${[CASE_REFERENCE]}#Referrals)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> descUploadDocForServing = List.of(Map.of(
            "name", "description",
            "value", "[Upload Document For Serving](/cases/case-details/${[CASE_REFERENCE]}"
                + "/trigger/uploadDocumentForServing/uploadDocumentForServing1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> descIssueDirections = List.of(Map.of(
            "name", "description",
            "value", "[Issue Directions](/cases/case-details/${[CASE_REFERENCE]}"
                + "/trigger/generateCorrespondence/generateCorrespondence1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> descListHearing = List.of(Map.of(
            "name", "description",
            "value", "[List Hearing](/cases/case-details/${[CASE_REFERENCE]}"
                + "/trigger/addAmendHearing/addAmendHearing1) "
                + "or [Generate Issue Initial Consideration Directions Task](/cases/case-details/${[CASE_REFERENCE]}"
                + "/trigger/issueInitialConsiderationDirectionsWA/submit)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> descInitialConsideration = List.of(Map.of(
            "name", "description",
            "value", "[Initial Consideration](/cases/case-details/${[CASE_REFERENCE]}"
                + "/trigger/initialConsideration/initialConsideration1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> descIssueET3Notification = List.of(Map.of(
            "name", "description",
            "value", "[Issue ET3 Notification](/cases/case-details/${[CASE_REFERENCE]}"
                + "/trigger/et3Notification/et3Notification1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> descET3Processing = List.of(Map.of(
            "name", "description",
            "value", "[ET3 Processing](/cases/case-details/${[CASE_REFERENCE]}/trigger/et3Vetting/et3Vetting1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> descReviewRule21Referral = List.of(Map.of(
            "name", "description",
            "value", "[Review Rule 21 Referral](/cases/case-details/${[CASE_REFERENCE]}#Respondent)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> descDraftJudgment = List.of(Map.of(
            "name", "description",
            "value", "[Draft and Sign Judgement](/cases/case-details/${[CASE_REFERENCE]}"
                + "/trigger/draftAndSignJudgement/draftAndSignJudgement1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> descApplicationsTab = List.of(Map.of(
            "name", "description",
            "value", "[Review Application](/cases/case-details/${[CASE_REFERENCE]}#Applications)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> descIssueJudgment = List.of(Map.of(
            "name", "description",
            "value", "[View Judgment](/cases/case-details/${[CASE_REFERENCE]}#Judgements) and "
                + "[Issue Judgment](/cases/case-details/${[CASE_REFERENCE]}"
                + "/trigger/addAmendJudgment/addAmendJudgment1)",
            "canReconfigure", true
        ));
        List<Map<String, Object>> reviewAccessRequest = List.of(Map.of(
            "name", "description",
            "value", "[Review Access Request](/role-access/${[taskId]}/assignment/${[roleAssignmentId]}/"
                + "specific-access)",
            "canReconfigure", true
        ));

        return Stream.of(
            Arguments.of("Et1Vetting", descET1Vetting),

            Arguments.of("ReviewReferralAdmin", descReferralTab),
            Arguments.of("ReviewReferralJudiciary", descReferralTab),
            Arguments.of("ReviewReferralLegalOps", descReferralTab),
            Arguments.of("ReviewReferralResponseAdmin", descReferralTab),
            Arguments.of("ReviewReferralResponseJudiciary", descReferralTab),
            Arguments.of("ReviewReferralResponseLegalOps", descReferralTab),

            Arguments.of("ListServeClaim", descUploadDocForServing),

            Arguments.of("SendEt1Notification", descIssueDirections),
            Arguments.of("IssuePostHearingDirection", descIssueDirections),
            Arguments.of("IssueInitialConsiderationDirections", descIssueDirections),

            Arguments.of("ListAHearing", descListHearing),

            Arguments.of("CompleteInitialConsideration", descInitialConsideration),

            Arguments.of("SendEt3Notification", descIssueET3Notification),

            Arguments.of("ET3Processing", descET3Processing),

            Arguments.of("ReviewRule21Referral", descReviewRule21Referral),

            Arguments.of("DraftAndSignJudgment", descDraftJudgment),

            Arguments.of("ContactTribunalWithAnApplication", descApplicationsTab),
            Arguments.of("AmendClaimantDetails", descApplicationsTab),
            Arguments.of("AmendRespondentDetails", descApplicationsTab),
            Arguments.of("WithdrawAllOrPartOfCase", descApplicationsTab),

            Arguments.of("IssueJudgment", descIssueJudgment),

            Arguments.of("reviewSpecificAccessRequestJudiciary", reviewAccessRequest),
            Arguments.of("reviewSpecificAccessRequestAdmin", reviewAccessRequest),
            Arguments.of("reviewSpecificAccessRequestLegalOps", reviewAccessRequest),
            Arguments.of("reviewSpecificAccessRequestCTSC", reviewAccessRequest)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "reviewSpecificAccessRequestJudiciary",
        "reviewSpecificAccessRequestLegalOps",
        "reviewSpecificAccessRequestAdmin",
        "reviewSpecificAccessRequestCTSC"
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
                .toList();

        assertTrue(resultList.contains(Map.of(
            "name", "additionalProperties_roleAssignmentId",
            "value", roleAssignmentId,
            "canReconfigure", true
        )));
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
                .filter((r) -> r.containsValue("dueDateIntervalDays"))
                .toList();

        assertEquals(expectedIntervalDays.get(0).get("name"), intervalDaysResultList.get(0).get("name"));
        assertEquals(expectedIntervalDays.get(0).get("value"), intervalDaysResultList.get(0).get("value"));

        List<Map<String, Object>> majorPriorityResultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter((r) -> r.containsValue("majorPriority"))
                .toList();

        assertEquals(expectedMajor.get(0).get("name"), majorPriorityResultList.get(0).get("name"));
        assertEquals(expectedMajor.get(0).get("value"), majorPriorityResultList.get(0).get("value"));

        List<Map<String, Object>> minorPriorityResultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter((r) -> r.containsValue("minorPriority"))
                .toList();

        assertEquals(expectedMinor.get(0).get("name"), minorPriorityResultList.get(0).get("name"));
        assertEquals(expectedMinor.get(0).get("value"), minorPriorityResultList.get(0).get("value"));
        assertEquals(expectedMinor.get(0).get("canReconfigure"), minorPriorityResultList.get(0).get("canReconfigure"));

        if (expectedPriorityDateOrigin != null) {
            List<Map<String, Object>> priorityDateRefResultList =
                dmnDecisionTableResult
                    .getResultList()
                    .stream()
                    .filter((r) -> r.containsValue("priorityDateOriginRef"))
                    .toList();

            assertEquals(expectedPriorityDateOrigin.get(0).get("name"),
                         priorityDateRefResultList.get(0).get("name"));
            assertEquals(expectedPriorityDateOrigin.get(0).get("value"),
                         priorityDateRefResultList.get(0).get("value"));
            assertEquals(expectedPriorityDateOrigin.get(0).get("canReconfigure"),
                         priorityDateRefResultList.get(0).get("canReconfigure"));
        }

        if (expectedPriorityDateEarliest != null) {
            List<Map<String, Object>> priorityDateEarResultList =
                dmnDecisionTableResult
                    .getResultList()
                    .stream()
                    .filter((r) -> r.containsValue("priorityDateOriginEarliest"))
                    .toList();

            assertEquals(expectedPriorityDateEarliest.get(0).get("name"),
                         priorityDateEarResultList.get(0).get("name"));
            assertEquals(expectedPriorityDateEarliest.get(0).get("value"),
                         priorityDateEarResultList.get(0).get("value"));
            assertEquals(expectedMinor.get(0).get("canReconfigure"),
                         priorityDateEarResultList.get(0).get("canReconfigure")
            );
        }
    }

    public static Stream<Arguments> priority_ScenarioProvider() {
        List<Map<String, Object>> dueDateIntervalDays1 = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "1",
            "canReconfigure", true
        ));
        List<Map<String, Object>> dueDateIntervalDays2 = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "2",
            "canReconfigure", true
        ));
        List<Map<String, Object>> dueDateIntervalDays3 = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "3",
            "canReconfigure", true
        ));
        List<Map<String, Object>> dueDateIntervalDays5 = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "5",
            "canReconfigure", true
        ));
        List<Map<String, Object>> dueDateIntervalDays10 = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "10",
            "canReconfigure", true
        ));
        List<Map<String, Object>> dueDateIntervalDays28 = List.of(Map.of(
            "name", "dueDateIntervalDays",
            "value", "28",
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

        List<Map<String, Object>> priorityDateOriginRef = List.of(Map.of(
            "name", "priorityDateOriginRef",
            "value", "dueDate",
            "canReconfigure", true
        ));
        List<Map<String, Object>> priorityDateOriginEar = List.of(Map.of(
            "name", "priorityDateOriginEarliest",
            "value", "dueDate, nextHearingDate",
            "canReconfigure", true
        ));

        return Stream.of(
            Arguments.of("ListServeClaim", NOT_URGENT,
                         dueDateIntervalDays1, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),
            Arguments.of("SendEt1Notification", NOT_URGENT,
                         dueDateIntervalDays1, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),
            Arguments.of("SendEt3Notification", NOT_URGENT,
                         dueDateIntervalDays1, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),
            Arguments.of("AmendClaimantDetails", NOT_URGENT,
                         dueDateIntervalDays1, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),
            Arguments.of("AmendRespondentDetails", NOT_URGENT,
                         dueDateIntervalDays1, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),
            Arguments.of("WithdrawAllOrPartOfCase", NOT_URGENT,
                         dueDateIntervalDays1, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),

            Arguments.of("ReviewRule21Referral", NOT_URGENT,
                         dueDateIntervalDays2, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),
            Arguments.of("CompleteInitialConsideration", NOT_URGENT,
                         dueDateIntervalDays2, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),
            Arguments.of("ContactTribunalWithAnApplication", NOT_URGENT,
                         dueDateIntervalDays2, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),

            Arguments.of("ET3Processing", NOT_URGENT,
                         dueDateIntervalDays3, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),

            Arguments.of("Et1Vetting", NOT_URGENT,
                         dueDateIntervalDays5, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),
            Arguments.of("IssueInitialConsiderationDirections", NOT_URGENT,
                         dueDateIntervalDays5, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),
            Arguments.of("IssuePostHearingDirection", NOT_URGENT,
                         dueDateIntervalDays5, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),
            Arguments.of("IssueJudgment", NOT_URGENT,
                         dueDateIntervalDays5, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),

            Arguments.of("ListAHearing", NOT_URGENT,
                         dueDateIntervalDays10, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),

            Arguments.of("DraftAndSignJudgment", NOT_URGENT,
                         dueDateIntervalDays28, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),

            Arguments.of("ReviewReferralAdmin", IS_URGENT,
                         dueDateIntervalDays1, urgentMajorPriority, urgentMinorPriority, null, priorityDateOriginEar
            ),
            Arguments.of("ReviewReferralResponseAdmin", ISURGENT_REPLY_YES,
                         dueDateIntervalDays1, urgentMajorPriority, urgentMinorPriority, priorityDateOriginRef, null
            ),
            Arguments.of("ReviewReferralJudiciary", IS_URGENT,
                         dueDateIntervalDays1, urgentMajorPriority, urgentMinorPriority, null, priorityDateOriginEar
            ),
            Arguments.of("ReviewReferralResponseJudiciary", ISURGENT_REPLY_YES,
                         dueDateIntervalDays1, urgentMajorPriority, urgentMinorPriority, priorityDateOriginRef, null
            ),
            Arguments.of("ReviewReferralLegalOps", IS_URGENT,
                         dueDateIntervalDays1, urgentMajorPriority, urgentMinorPriority, null, priorityDateOriginEar
            ),
            Arguments.of("ReviewReferralResponseLegalOps", ISURGENT_REPLY_YES,
                         dueDateIntervalDays1, urgentMajorPriority, urgentMinorPriority, priorityDateOriginRef, null
            ),

            Arguments.of("ReviewReferralAdmin", NOT_URGENT,
                         dueDateIntervalDays2, defaultMajorPriority, defaultMinorPriority, null, priorityDateOriginEar
            ),
            Arguments.of("ReviewReferralResponseAdmin", ISURGENT_REPLY_NO,
                         dueDateIntervalDays2, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),
            Arguments.of("ReviewReferralJudiciary", NOT_URGENT,
                         dueDateIntervalDays2, defaultMajorPriority, defaultMinorPriority, null, priorityDateOriginEar
            ),
            Arguments.of("ReviewReferralResponseJudiciary", ISURGENT_REPLY_NO,
                         dueDateIntervalDays2, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
            ),
            Arguments.of("ReviewReferralLegalOps", NOT_URGENT,
                         dueDateIntervalDays2, defaultMajorPriority, defaultMinorPriority, null, priorityDateOriginEar
            ),
            Arguments.of("ReviewReferralResponseLegalOps", ISURGENT_REPLY_NO,
                         dueDateIntervalDays2, defaultMajorPriority, defaultMinorPriority, priorityDateOriginRef, null
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

        assertThat(logic.getRules().size(), is(52));
    }

    private List<Map<String, Object>> getExpectedValues() {
        List<Map<String, Object>> rules = new ArrayList<>();
        HelperService.getExpectedValueWithReconfigure(rules, "caseName", "George Jetson", true);
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
            rules, "dueDateNonWorkingCalendar",DEFAULT_CALENDAR + ", " + EXTRA_TEST_CALENDAR, true);
        HelperService.getExpectedValueWithReconfigure(rules, "dueDateNonWorkingDaysOfWeek", "SATURDAY,SUNDAY", true);
        HelperService.getExpectedValueWithReconfigure(rules, "dueDateSkipNonWorkingDays", "true", true);
        HelperService.getExpectedValueWithReconfigure(rules, "dueDateMustBeWorkingDay", "Yes", true);
        return rules;
    }
}
