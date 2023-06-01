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
import uk.gov.hmcts.et.taskconfiguration.DmnDecisionTableBaseUnitTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_CONFIGURATION_ET_EW;

class EmploymentTaskConfigurationTest extends DmnDecisionTableBaseUnitTest {

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
    }

    public static Stream<Arguments> workType_ScenarioProvider() {
        List<Map<String, String>> routineWork = List.of(Map.of(
            "name", "workType",
            "value", "routine_work"
        ));
        List<Map<String, String>> decisionMakingWork = List.of(Map.of(
            "name", "workType",
            "value", "decision_making_work"
        ));
        List<Map<String, String>> hearingWork = List.of(Map.of(
            "name", "workType",
            "value", "hearing_work"
        ));
        List<Map<String, String>> applications = List.of(Map.of(
            "name", "workType",
            "value", "Applications"
        ));
        List<Map<String, String>> accessRequests = List.of(Map.of(
            "name", "workType",
            "value", "access_requests"
        ));

        return Stream.of(
            Arguments.of("draftCaseCreated", routineWork),
            Arguments.of("Et1Vetting", routineWork),
            Arguments.of("ReviewReferralLegalOps", routineWork),
            Arguments.of("ReviewReferralAdmin", routineWork),
            Arguments.of("SendEt1Notification", routineWork),
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
            Arguments.of("ContactTribunalWithAnApplication", applications),
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
        List<Map<String, String>> judicial = List.of(Map.of(
            "name", "roleCategory",
            "value", "JUDICIAL"
        ));
        List<Map<String, String>> legalOperations = List.of(Map.of(
            "name", "roleCategory",
            "value", "LEGAL_OPERATIONS"
        ));
        List<Map<String, String>> administrator = List.of(Map.of(
            "name", "roleCategory",
            "value", "ADMIN"
        ));
        List<Map<String, String>> ctsc = List.of(Map.of(
            "name", "roleCategory",
            "value", "CTSC"
        ));
        List<Map<String, String>> adminctsc = List.of(Map.of(
            "name", "roleCategory",
            "value", "ADMIN,CTSC"
        ));

        return Stream.of(
            Arguments.of("draftCaseCreated", judicial),
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
            Arguments.of("SendET3Notification", administrator),
            Arguments.of("IssueInitialConsiderationDirections", administrator),
            Arguments.of("IssuePostHearingDirection", administrator),
            Arguments.of("IssueJudgment", administrator),
            Arguments.of("ContactTribunalWithAnApplication", administrator),
            Arguments.of("AmendPartyDetails", administrator),
            Arguments.of("WithdrawAllOrPartOfCase", administrator),

            Arguments.of("reviewSpecificAccessRequestCTSC", ctsc)
        );
    }

    @ParameterizedTest
    @MethodSource("description_ScenarioProvider")
    void when_taskId_then_return_description(String taskType, List<Map<String, String>> expected) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", getDefaultCaseData());
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> resultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter((r) -> r.containsValue("description"))
                .collect(Collectors.toList());

        assertEquals(expected.get(0).get("name"), resultList.get(0).get("name"));
        assertEquals(expected.get(0).get("value"), resultList.get(0).get("value"));
    }

    public static Stream<Arguments> description_ScenarioProvider() {
        List<Map<String, String>> reviewTheReferralCreate = List.of(Map.of(
            "name", "description",
            "value", "[Review the Referral](/cases/case-details/${[CASE_REFERENCE]}/createReferral1)"
        ));
        List<Map<String, String>> reviewTheReferralReply = List.of(Map.of(
            "name", "description",
            "value", "[Review the Referral](/cases/case-details/${[CASE_REFERENCE]}/replyToReferral1)"
        ));
        List<Map<String, String>> reviewET1Submission = List.of(Map.of(
            "name", "description",
            "value", "[Review ET1 Submission](cases/case-details/${[CASE_REFERENCE]}/trigger/et1Vetting/et1Vetting1)"
        ));
        List<Map<String, String>> issueET1Notification = List.of(Map.of(
            "name", "description",
            "value", "[Issue relevant ET1 Notification](cases/case-details/${[CASE_REFERENCE]}/trigger/"
                + "generateCorrespondence/generateCorrespondence1)"
        ));
        List<Map<String, String>> draftJudgment = List.of(Map.of(
            "name", "description",
            "value", "[Draft Judgment and then refer to judge](cases/case-details/${[CASE_REFERENCE]}/"
                + "trigger/createReferral/createReferral1)"
        ));
        List<Map<String, String>> listHearingUploadDocument = List.of(Map.of(
            "name", "description",
            "value", "[list hearing if required and then upload document for serving](cases/case-details/"
                + "${[CASE_REFERENCE]}/trigger/uploadDocument/uploadDocument1)"
        ));
        List<Map<String, String>> reviewET3Submission = List.of(Map.of(
            "name", "description",
            "value", "[Review ET3 Submission](cases/case-details/${[CASE_REFERENCE]}/trigger/et3Vetting/et3Vetting1)"
        ));
        List<Map<String, String>> issueET3Notification = List.of(Map.of(
            "name", "description",
            "value", "[Issue relevant ET3 Notification](cases/case-details/${[CASE_REFERENCE]}/trigger/"
                + "generateCorrespondence/generateCorrespondence1)"
        ));
        List<Map<String, String>> initialConsideration = List.of(Map.of(
            "name", "description",
            "value", "[provide your initial consideration](/cases/case-details/${[CASE_REFERENCE]}/trigger/"
                + "initialConsideration/initialConsideration1)"
        ));
        List<Map<String, String>> reviewInitialConsideration = List.of(Map.of(
            "name", "description",
            "value", "[Review Initial consideration, update case and then issue relevant correspondence](cases/"
                + "case-details/${[CASE_REFERENCE]}/trigger/generateCorrespondence/generateCorrespondence1)"
        ));
        List<Map<String, String>> reviewTheReferralCorrespondence = List.of(Map.of(
            "name", "description",
            "value", "[Review Referral, then issue relevant correspondence](cases/case-details/${[CASE_REFERENCE]}/"
                + "trigger/generateCorrespondence/generateCorrespondence1)"
        ));
        List<Map<String, String>> reviewJudgmentPromulgation = List.of(Map.of(
            "name", "description",
            "value", "[Refer the judgment for promulgation, once signed](cases/case-details/${[CASE_REFERENCE]}/"
                + "trigger/addAmendJudgment/addAmendJudgment1)"
        ));
        List<Map<String, String>> reviewJudgmentReferral = List.of(Map.of(
            "name", "description",
            "value", "[Review Judgment Referral, then issue relevant correspondence](cases/case-details/"
                + "${[CASE_REFERENCE]}/trigger/generateCorrespondence/generateCorrespondence1)"
        ));
        List<Map<String, String>> updatePartyDetails = List.of(Map.of(
            "name", "description",
            "value", "[Update Claimant Details](cases/case-details/${[CASE_REFERENCE]}/trigger/amendClaimantDetails/"
                + "amendClaimantDetails1) [OR Respondent Details](cases/case-details/${[CASE_REFERENCE]}/trigger/"
                + "amendClaimantDetails/amendClaimantDetails1)[, as instructed]"
        ));
        List<Map<String, String>> withdrawCase = List.of(Map.of(
            "name", "description",
            "value", "[Withdraw all or part of the case](cases/case-details/${[CASE_REFERENCE]}/trigger/"
                + "disposeCase/disposeCase1)"
        ));
        List<Map<String, String>> reviewApplication = List.of(Map.of(
            "name", "description",
            "value", "[Review Application and refer to judge](cases/case-details/${[CASE_REFERENCE]}/"
                + "trigger/createReferral/createReferral1)"
        ));
        List<Map<String, String>> reviewAccessRequestJudiciary = List.of(Map.of(
            "name", "description",
            "value", "[Review Access Request](/cases/case-details/${[CASE_REFERENCE]}/trigger/"
                + "reviewSpecificAccessRequestJudiciary)"
        ));
        List<Map<String, String>> reviewAccessRequestAdmin = List.of(Map.of(
            "name", "description",
            "value", "[Review Access Request](/cases/case-details/${[CASE_REFERENCE]}/trigger/"
                + "reviewSpecificAccessRequestAdmin)"
        ));
        List<Map<String, String>> reviewAccessRequestLegalOps = List.of(Map.of(
            "name", "description",
            "value", "[Review Access Request](/cases/case-details/${[CASE_REFERENCE]}/trigger/"
                + "reviewSpecificAccessRequestLegalOps)"
        ));
        List<Map<String, String>> reviewAccessRequestCTSC = List.of(Map.of(
            "name", "description",
            "value", "[Review Access Request](/cases/case-details/${[CASE_REFERENCE]}/trigger/"
                + "reviewSpecificAccessRequestCTSC)"
        ));

        return Stream.of(
            Arguments.of("draftCaseCreated", reviewTheReferralCreate),
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

            Arguments.of("SendET3Notification", issueET3Notification),

            Arguments.of("CompleteInitialConsideration", initialConsideration),

            Arguments.of("IssueInitialConsiderationDirections", reviewInitialConsideration),

            Arguments.of("IssuePostHearingDirection", reviewTheReferralCorrespondence),

            Arguments.of("DraftAndSignJudgment", reviewJudgmentPromulgation),

            Arguments.of("IssueJudgment", reviewJudgmentReferral),

            Arguments.of("AmendPartyDetails", updatePartyDetails),

            Arguments.of("WithdrawAllOrPartOfCase", withdrawCase),

            Arguments.of("ContactTribunalWithanApplication", reviewApplication),

            Arguments.of("reviewSpecificAccessRequestJudiciary", reviewAccessRequestJudiciary),

            Arguments.of("reviewSpecificAccessRequestAdmin", reviewAccessRequestAdmin),

            Arguments.of("reviewSpecificAccessRequestLegalOps", reviewAccessRequestLegalOps),

            Arguments.of("reviewSpecificAccessRequestCTSC", reviewAccessRequestCTSC)
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
    }

    public static Stream<Arguments> priority_ScenarioProvider() {
        List<Map<String, String>> defaultMajorPriority = List.of(Map.of(
            "name", "majorPriority",
            "value", "5000"
        ));
        List<Map<String, String>> defaultMinorPriority = List.of(Map.of(
            "name", "minorPriority",
            "value", "500"
        ));
        List<Map<String, String>> urgentMajorPriority = List.of(Map.of(
            "name", "majorPriority",
            "value", "1000"
        ));
        List<Map<String, String>> urgentMinorPriority = List.of(Map.of(
            "name", "minorPriority",
            "value", "100"
        ));

        return Stream.of(
            Arguments.of("draftCaseCreated", "No", defaultMajorPriority, defaultMinorPriority),
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
            Arguments.of("IssueJudgment", "No", defaultMajorPriority, defaultMinorPriority)
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getRules().size(), is(36));
    }

    private static List<Map<String, String>> concatTwoLists(List<Map<String, String>> list1,
                                                            List<Map<String, String>> list2) {
        return Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList());
    }
}
