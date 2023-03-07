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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_CONFIGURATION_ET_EW;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class EmploymentTaskConfigurationTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CONFIGURATION_ET_EW;
    }

    private static Map<String, Object> getDefaultCaseData() {
        return Map.of(
            "applicant1", Map.of(
                "partyName ", "applicant1"
            ),
            "applicant2", Map.of(
                "partyName ", "applicant2"
            ),
            "caseManagementLocation", Map.of(
                "region", "some region",
                "baseLocation", "some location code"
            ),
            "staffLocation", "some location name"
        );
    }

    @ParameterizedTest
    @MethodSource("workType_ScenarioProvider")
    void when_taskId_then_return_workType(String taskType, List<Map<String, String>> expected) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("caseData", getDefaultCaseData());
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        List<Map<String, Object>> workTypeResultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter((r) -> r.containsValue("workType"))
                .collect(Collectors.toList());

        assertEquals(expected, workTypeResultList);
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
        List<Map<String, String>> amendments = List.of(Map.of(
            "name", "workType",
            "value", "Amendments"
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
            Arguments.of("ET1Vetting", routineWork),
            Arguments.of("ET1ReferralLegalOps", routineWork),
            Arguments.of("ET1Notification", routineWork),
            Arguments.of("ListServeClaim", routineWork),
            Arguments.of("ET3Processing", routineWork),
            Arguments.of("ET3ReferralLegalOps", routineWork),
            Arguments.of("ET3Notification", routineWork),
            Arguments.of("InitialConsiderationDirections", routineWork),

            Arguments.of("ET1ReferralJudiciary", decisionMakingWork),
            Arguments.of("ET3ReferralJudiciary", decisionMakingWork),
            Arguments.of("Rule21Referral", decisionMakingWork),
            Arguments.of("InitialConsideration", decisionMakingWork),
            Arguments.of("DraftAndSignJudgment", decisionMakingWork),
            Arguments.of("withdrawalReferralJudiciary", decisionMakingWork),
            Arguments.of("applicationReferralJudiciary", decisionMakingWork),

            Arguments.of("PostHearingPromulgation", hearingWork),
            Arguments.of("JudgmentPromulgation", hearingWork),

            Arguments.of("AmendPartyDetails", amendments),
            Arguments.of("WithdrawAllOrPartOfCase", amendments),

            Arguments.of("ApplicationSubmission", applications),
            Arguments.of("ApplicationReferralLegalOps", applications),

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

        List<Map<String, Object>> workTypeResultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter((r) -> r.containsValue("role_Category"))
                .collect(Collectors.toList());

        assertEquals(expected, workTypeResultList);
    }

    public static Stream<Arguments> roleCategory_ScenarioProvider() {
        List<Map<String, String>> judicial = List.of(Map.of(
            "name", "role_Category",
            "value", "JUDICIAL"
        ));
        List<Map<String, String>> legalOperations = List.of(Map.of(
            "name", "role_Category",
            "value", "LEGAL_OPERATIONS"
        ));
        List<Map<String, String>> administrator = List.of(Map.of(
            "name", "role_Category",
            "value", "ADMINISTRATOR"
        ));
        List<Map<String, String>> ctsc = List.of(Map.of(
            "name", "role_Category",
            "value", "CTSC"
        ));

        return Stream.of(
            Arguments.of("draftCaseCreated", judicial),
            Arguments.of("reviewSpecificAccessRequestJudiciary", judicial),
            Arguments.of("ET1ReferralJudiciary", judicial),
            Arguments.of("ET3ReferralJudiciary", judicial),
            Arguments.of("InitialConsideration", judicial),
            Arguments.of("DraftAndSignJudgment", judicial),
            Arguments.of("withdrawalReferralJudiciary", judicial),
            Arguments.of("applicationReferralJudiciary", judicial),

            Arguments.of("reviewSpecificAccessRequestLegalOps", legalOperations),
            Arguments.of("Rule21Referral", legalOperations),
            Arguments.of("ET1ReferralLegalOps", legalOperations),
            Arguments.of("ET3ReferralLegalOps", legalOperations),
            Arguments.of("ApplicationReferralLegalOps", legalOperations),

            Arguments.of("reviewSpecificAccessRequestAdmin", administrator),
            Arguments.of("ET1Vetting", administrator),
            Arguments.of("ET1Notification", administrator),
            Arguments.of("ListServeClaim", administrator),
            Arguments.of("ET3Processing", administrator),
            Arguments.of("ET3Notification", administrator),
            Arguments.of("InitialConsiderationDirections", administrator),
            Arguments.of("PostHearingPromulgation", administrator),
            Arguments.of("JudgmentPromulgation", administrator),
            Arguments.of("ApplicationSubmission", administrator),
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

        List<Map<String, Object>> workTypeResultList =
            dmnDecisionTableResult
                .getResultList()
                .stream()
                .filter((r) -> r.containsValue("description"))
                .collect(Collectors.toList());

        assertEquals(expected, workTypeResultList);
    }

    public static Stream<Arguments> description_ScenarioProvider() {
        List<Map<String, String>> reviewTheReferral = List.of(Map.of(
            "name", "description",
            "value", "[Review the Referral](/cases/case-details/${[CASE_REFERENCE]}/referrals)"
        ));
        List<Map<String, String>> reviewET1Submission = List.of(Map.of(
            "name", "description",
            "value", "[Review ET1 Submission](cases/case-details/${[CASE_REFERENCE]}/trigger/et1Vetting/et1Vetting1)"
        ));
        List<Map<String, String>> issueET1Notification = List.of(Map.of(
            "name", "description",
            "value", "[Issue relevant ET1 Notification](cases/case-details/${[CASE_REFERENCE]}/trigger/generateCorrespondence/generateCorrespondence1)"
        ));
        List<Map<String, String>> draftJudgement = List.of(Map.of(
            "name", "description",
            "value", "[Draft Judgment and then refer to judge](cases/case-details/${[CASE_REFERENCE]}/trigger/createReferral/createReferral1)"
        ));
        List<Map<String, String>> listServe = List.of(Map.of(
            "name", "description",
            "value", "[list hearing if required and then upload document for serving](cases/case -details/${[CASE_REFERENCE]}/trigger/uploadDocument/uploadDocument1)"
        ));
        List<Map<String, String>> reviewET3Submission = List.of(Map.of(
            "name", "description",
            "value", "[Review ET3 Submission](cases/case-details/${[CASE_REFERENCE]}/trigger/et3Vetting/et3Vetting1)"
        ));
        List<Map<String, String>> issueET3Notification = List.of(Map.of(
            "name", "description",
            "value", "[Issue relevant ET3 Notification](cases/case-details/${[CASE_REFERENCE]}/trigger/generateCorrespondence/generateCorrespondence1)"
        ));
        List<Map<String, String>> initialConsideration = List.of(Map.of(
            "name", "description",
            "value", "[provide your initial consideration](/cases/case-details/${[CASE_REFERENCE]}/trigger/initialConsideration/initialConsideration1referrals)"
        ));
        List<Map<String, String>> reviewInitialConsideration = List.of(Map.of(
            "name", "description",
            "value", "[Review Initial consideration, update case and then issue relevant correspondence](cases/case-details/${[CASE_REFERENCE]}/trigger/generateCorrespondence/generateCorrespondence1)"
        ));
        List<Map<String, String>> reviewReferral = List.of(Map.of(
            "name", "description",
            "value", "[Review Referral, then issue relevant correspondence](cases/case-details/${[CASE_REFERENCE]}/trigger/generateCorrespondence/generateCorrespondence1)"
        ));
        List<Map<String, String>> referJudgmentPromulgation = List.of(Map.of(
            "name", "description",
            "value", "[Refer the judgment for promulgation, once signed](cases/case-details/${[CASE_REFERENCE]}/trigger/createReferral/createReferral1)"
        ));
        List<Map<String, String>> reviewJudgmentReferral = List.of(Map.of(
            "name", "description",
            "value", "[Review Judgment Referral, then issue relevant correspondence](cases/case-details/${[CASE_REFERENCE]}/trigger/generateCorrespondence/generateCorrespondence1)"
        ));
        List<Map<String, String>> updateClaimantRespondentDetails = List.of(Map.of(
            "name", "description",
            "value", "[Update Claimant Details](cases/case-details/${[CASE_REFERENCE]}/trigger/amendClaimantDetails/amendClaimantDetails1) [OR Respondent Details](cases/case-details/${[CASE_REFERENCE]}/trigger/amendClaimantDetails/amendClaimantDetails1)[, as instructed]"
        ));
        List<Map<String, String>> withdraw = List.of(Map.of(
            "name", "description",
            "value", "[Withdraw all or part of the case](cases/case-details/${[CASE_REFERENCE]}/trigger/disposeCase/disposeCase1)"
        ));
        List<Map<String, String>> reviewApplication = List.of(Map.of(
            "name", "description",
            "value", "[Review Application and refer to judge](cases/case-details/${[CASE_REFERENCE]}/trigger/createReferral/createReferral1)"
        ));
        List<Map<String, String>> reviewAccessRequestJudiciary = List.of(Map.of(
            "name", "description",
            "value", "[Review Access Request](/cases/case-details/${[CASE_REFERENCE]}/trigger/reviewSpecificAccessRequestJudiciary)"
        ));
        List<Map<String, String>> reviewAccessRequestAdmin = List.of(Map.of(
            "name", "description",
            "value", "[Review Access Request](/cases/case-details/${[CASE_REFERENCE]}/trigger/reviewSpecificAccessRequestAdmin)"
        ));
        List<Map<String, String>> reviewAccessRequestLegalOps = List.of(Map.of(
            "name", "description",
            "value", "[Review Access Request](/cases/case-details/${[CASE_REFERENCE]}/trigger/reviewSpecificAccessRequestLegalOps)"
        ));
        List<Map<String, String>> reviewAccessRequestCTSC = List.of(Map.of(
            "name", "description",
            "value", "[Review Access Request](/cases/case-details/${[CASE_REFERENCE]}/trigger/reviewSpecificAccessRequestCTSC)"
        ));

        return Stream.of(
            Arguments.of("draftCaseCreated", reviewTheReferral),
            Arguments.of("ET1ReferralJudiciary", reviewTheReferral),
            Arguments.of("ET1ReferralLegalOps", reviewTheReferral),
            Arguments.of("ET3ReferralJudiciary", reviewTheReferral),
            Arguments.of("ET3ReferralLegalOps", reviewTheReferral),
            Arguments.of("withdrawalReferralJudiciary", reviewTheReferral),
            Arguments.of("applicationReferralJudiciary", reviewTheReferral),
            Arguments.of("ApplicationReferralLegalOps", reviewTheReferral),

            Arguments.of("ET1Vetting", reviewET1Submission),

            Arguments.of("ET1Notification", issueET1Notification),

            Arguments.of("Rule21Referral", draftJudgement),

            Arguments.of("ListServeClaim", listServe),

            Arguments.of("ET3Processing", reviewET3Submission),

            Arguments.of("ET3Notification", issueET3Notification),

            Arguments.of("InitialConsideration", initialConsideration),

            Arguments.of("InitialConsiderationDirections", reviewInitialConsideration),

            Arguments.of("PostHearingPromulgation", reviewReferral),

            Arguments.of("DraftAndSignJudgment", referJudgmentPromulgation),

            Arguments.of("JudgmentPromulgation", reviewJudgmentReferral),

            Arguments.of("AmendPartyDetails", updateClaimantRespondentDetails),

            Arguments.of("WithdrawAllOrPartOfCase", withdraw),

            Arguments.of("ApplicationSubmission", reviewApplication),

            Arguments.of("reviewSpecificAccessRequestJudiciary", reviewAccessRequestJudiciary),

            Arguments.of("reviewSpecificAccessRequestAdmin", reviewAccessRequestAdmin),

            Arguments.of("reviewSpecificAccessRequestLegalOps", reviewAccessRequestLegalOps),

            Arguments.of("reviewSpecificAccessRequestCTSC", reviewAccessRequestCTSC)
        );
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getRules().size(), is(34));
    }
}