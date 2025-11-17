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

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_TYPE_ET_SCOTLAND;

class EmploymentTaskTypeTestScot extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_TYPE_ET_SCOTLAND;
    }

    static Stream<Arguments> scenarioProvider() {
        return Stream.of(
            Arguments.of(
                List.of(
                    Map.of("taskTypeId", "AmendClaimantDetails",
                           "taskTypeName","Amend Party Details (Claimant)"),

                    Map.of("taskTypeId", "AmendRespondentDetails",
                           "taskTypeName","Amend Party Details (Respondent)"),

                    Map.of("taskTypeId", "CompleteInitialConsideration",
                           "taskTypeName","Complete Initial Consideration"),

                    Map.of("taskTypeId", "ContactTribunalWithAnApplication",
                           "taskTypeName","Contact Tribunal With An Application"),

                    Map.of("taskTypeId", "DraftAndSignJudgment", "taskTypeName", "Draft And Sign Judgment/Order"),

                    Map.of("taskTypeId", "Et1Vetting", "taskTypeName", "ET1 Vetting"),

                    Map.of("taskTypeId", "ET3Processing", "taskTypeName", "ET3 Processing"),

                    Map.of("taskTypeId", "IssueInitialConsiderationDirections", "taskTypeName",
                           "Issue Initial Consideration Directions"),

                    Map.of("taskTypeId", "IssueJudgment", "taskTypeName","Issue Judgment"),

                    Map.of("taskTypeId", "IssuePostHearingDirection",
                           "taskTypeName", "Issue Post Hearing Direction"),

                    Map.of("taskTypeId", "ListAHearing", "taskTypeName", "List A Hearing"),

                    Map.of("taskTypeId", "ListServeClaim", "taskTypeName", "List/ Serve Claim"),

                    Map.of("taskTypeId", "ReviewECCResponse", "taskTypeName",
                           "Reply to Employer's Contract Claim received"),

                    Map.of("taskTypeId", "ReviewReferralAdmin", "taskTypeName", "Review Referral - Admin"),

                    Map.of("taskTypeId", "ReviewReferralJudiciary", "taskTypeName", "Review Referral - Judicial"),

                    Map.of("taskTypeId", "ReviewReferralLegalOps", "taskTypeName", "Review Referral - Legal Ops"),

                    Map.of("taskTypeId", "ReviewReferralResponseAdmin",
                           "taskTypeName", "Review Referral Response - Admin"),

                    Map.of("taskTypeId", "ReviewReferralResponseJudiciary",
                           "taskTypeName", "Review Referral Response - Judicial"),

                    Map.of("taskTypeId", "ReviewReferralResponseLegalOps",
                           "taskTypeName","Review Referral Response - Legal Ops"),

                    Map.of("taskTypeId", "ReviewRule21Referral","taskTypeName","Review Rule 22 Referral"),

                    Map.of("taskTypeId", "Rule21","taskTypeName","Rule 22"),

                    Map.of("taskTypeId", "ReferEmployersContractClaim",
                           "taskTypeName","Refer Employer's Contract Claim"),

                    Map.of("taskTypeId", "ExpiredBfAction","taskTypeName","Expired Bf Action"),

                    Map.of("taskTypeId", "SendEt1Notification","taskTypeName", "Send ET1 Notification"),

                    Map.of("taskTypeId", "SendEt3Notification", "taskTypeName","Send ET3 Notification"),

                    Map.of("taskTypeId", "WithdrawAllOrPartOfCase","taskTypeName","Withdraw All Or Part Of Case"),

                    Map.of("taskTypeId", "reviewSpecificAccessRequestAdmin",
                           "taskTypeName","Review Specific Access Request (Admin)"),

                    Map.of("taskTypeId", "reviewSpecificAccessRequestCTSC",
                           "taskTypeName","Review Specific Access Request (CTSC)"),

                    Map.of("taskTypeId", "reviewSpecificAccessRequestJudiciary",
                           "taskTypeName","Review Specific Access Request (Judiciary)"),

                    Map.of("taskTypeId", "reviewSpecificAccessRequestLegalOps",
                           "taskTypeName","Review Specific Access Request (Legal Ops)"),

                    Map.of("taskTypeId", "IssueOrder", "taskTypeName","Issue Order")
                )
            )
        );
    }

    @ParameterizedTest(name = "retrieve all task type data")
    @MethodSource("scenarioProvider")
    void should_evaluate_dmn_return_all_task_type_fields(List<Map<String, Object>> expectedTaskTypes) {
        VariableMap inputVariables = new VariableMapImpl();
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList(), is(expectedTaskTypes));
    }

    @Test
    void check_dmn_changed() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getInputs().size(), is(1));
        assertThat(logic.getOutputs().size(), is(2));
        assertThat(logic.getRules().size(), is(31));
    }
}
