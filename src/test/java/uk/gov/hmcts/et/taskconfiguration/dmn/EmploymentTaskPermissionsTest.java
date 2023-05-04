package uk.gov.hmcts.et.taskconfiguration.dmn;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_PERMISSIONS_ET_EW;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableInputImpl;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableOutputImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable;
import uk.gov.hmcts.et.taskconfiguration.DmnDecisionTableBaseUnitTest;

class EmploymentTaskPermissionsTest extends DmnDecisionTableBaseUnitTest {

    private static final Map<String, Serializable> taskSupervisor = Map.of(
        "autoAssignable", false,
        "name", "task-supervisor",
        "value", "Read,Assign,Unassign,Manage,Cancel,Complete"
    );

    private static final Map<String, Serializable> approverJudiciary = Map.of(
        "autoAssignable", true,
        "name", "specific-access-approver-judiciary",
        "roleCategory", "JUDICIAL",
        "value", "Read,Own,Claim,Unclaim,UnclaimAssign,CompleteOwn,CancelOwn"
    );
    private static final Map<String, Serializable> leadershipJudge = Map.of(
        "autoAssignable", false,
        "name", "leadership-judge",
        "value", "Read,Execute,Own,Claim,Manage,Unassign,Assign,Complete,Cancel",
        "roleCategory", "JUDICIAL"
    );
    private static final Map<String, Serializable> judge = Map.of(
        "autoAssignable", false,
        "name", "judge",
        "value", "Read,Execute,Own,Claim,Manage,Unassign,Assign,Complete,Cancel",
        "roleCategory", "JUDICIAL"
    );
    private static final Map<String, Serializable> hearingJudge = Map.of(
        "autoAssignable", true,
        "name", "hearing-judge",
        "value", "Read,Own,Claim,CompleteOwn,CancelOwn",
        "roleCategory", "JUDICIAL"
    );

    private static final Map<String, Serializable> approverLegalOps = Map.of(
        "autoAssignable", false,
        "name", "specific-access-approver-legal-ops",
        "value", "Read,Own,Claim,Unclaim,UnclaimAssign,CompleteOwn,CancelOwn",
        "roleCategory", "LEGAL_OPERATIONS"
    );
    private static final Map<String, Serializable> seniorTribunalCaseworker = Map.of(
        "autoAssignable", false,
        "name", "senior-tribunal-caseworker",
        "value", "Read,Own,Claim,Unclaim,UnclaimAssign,CompleteOwn,CancelOwn",
        "roleCategory", "LEGAL_OPERATIONS"
    );
    private static final Map<String, Serializable> legalCaseworker = Map.of(
        "autoAssignable", true,
        "name", "legal-caseworker",
        "value", "Read,Own,Claim,Unclaim,CompleteOwn,CancelOwn",
        "roleCategory", "LEGAL_OPERATIONS"
    );

    private static final Map<String, Serializable> approverAdmin = Map.of(
        "autoAssignable", false,
        "name", "specific-access-approver-admin",
        "value", "Read,Own,Claim,Unclaim,UnclaimAssign,CompleteOwn,CancelOwn",
        "roleCategory", "ADMINISTRATOR"
    );
    private static final Map<String, Serializable> hearingCentreTeamLeader = Map.of(
        "autoAssignable", true,
        "assignmentPriority", 10,
        "name", "hearing-centre-team-leader",
        "value", "Read,Own,Claim,Unclaim,UnclaimAssign,CompleteOwn,CancelOwn",
        "roleCategory", "ADMINISTRATOR"
    );
    private static final Map<String, Serializable> adminCaseworker = Map.of(
        "autoAssignable", true,
        "assignmentPriority", 10,
        "name", "admin-caseworker",
        "value", "Read,Own,Claim,Unclaim,CompleteOwn,CancelOwn",
        "roleCategory", "ADMINISTRATOR"
    );

    private static final Map<String, Serializable> approverCTSC = Map.of(
        "autoAssignable", false,
        "name", "specific-access-approver-ctsc",
        "value", "Read,Own,Claim,Unclaim,UnclaimAssign,CompleteOwn,CancelOwn",
        "roleCategory", "CTSC"
    );
    private static final Map<String, Serializable> leaderCTSC = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 100,
        "name", "ctsc-team-leader",
        "value", "Read,Own,Claim,Unclaim,UnclaimAssign,CompleteOwn,CancelOwn",
        "roleCategory", "CTSC"
    );
    private static final Map<String, Serializable> ctsc = Map.of(
        "autoAssignable", true,
        "assignmentPriority", 100,
        "name", "ctsc",
        "value", "Read,Own,Claim,Unclaim,CompleteOwn,CancelOwn",
        "roleCategory", "CTSC"
    );

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_PERMISSIONS_ET_EW;
    }

    public static Stream<Arguments> genericScenarioProvider() {
        return Stream.of(
            Arguments.of(
                "draftCaseCreated",
                List.of(
                    taskSupervisor,
                    approverJudiciary,
                    leadershipJudge,
                    judge,
                    hearingJudge,
                    approverLegalOps,
                    seniorTribunalCaseworker,
                    legalCaseworker,
                    approverAdmin,
                    hearingCentreTeamLeader,
                    adminCaseworker,
                    approverCTSC,
                    leaderCTSC,
                    ctsc
                )
            ),
            Arguments.of(
                "reviewSpecificAccessRequestJudiciary",
                List.of(
                    taskSupervisor,
                    approverJudiciary
                )
            ),
            Arguments.of(
                "ET1ReferralJudiciary",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    judge,
                    hearingJudge
                )
            ),
            Arguments.of(
                "ET3ReferralJudiciary",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    judge,
                    hearingJudge
                )
            ),
            Arguments.of(
                "Rule21Referral",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    judge,
                    hearingJudge,
                    seniorTribunalCaseworker,
                    legalCaseworker
                )
            ),
            Arguments.of(
                "InitialConsideration",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    judge,
                    hearingJudge
                )
            ),
            Arguments.of(
                "DraftAndSignJudgment",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    judge,
                    hearingJudge
                )
            ),
            Arguments.of(
                "withdrawalReferralJudiciary",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    judge,
                    hearingJudge
                )
            ),
            Arguments.of(
                "applicationReferralJudiciary",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    judge,
                    hearingJudge
                )
            ),

            Arguments.of(
                "reviewSpecificAccessRequestLegalOps",
                List.of(
                    taskSupervisor,
                    approverLegalOps
                )
            ),
            Arguments.of(
                "ET1ReferralLegalOps",
                List.of(
                    taskSupervisor,
                    seniorTribunalCaseworker,
                    legalCaseworker
                )
            ),
            Arguments.of(
                "ET3ReferralLegalOps",
                List.of(
                    taskSupervisor,
                    seniorTribunalCaseworker,
                    legalCaseworker
                )
            ),
            Arguments.of(
                "ApplicationReferralLegalOps",
                List.of(
                    taskSupervisor,
                    seniorTribunalCaseworker,
                    legalCaseworker
                )
            ),

            Arguments.of(
                "reviewSpecificAccessRequestAdmin",
                List.of(
                    taskSupervisor,
                    approverAdmin
                )
            ),
            Arguments.of(
                "ET1Vetting",
                List.of(
                    taskSupervisor,
                    hearingCentreTeamLeader,
                    adminCaseworker,
                    leaderCTSC,
                    ctsc
                )
            ),
            Arguments.of(
                "ET1Notification",
                List.of(
                    taskSupervisor,
                    hearingCentreTeamLeader,
                    adminCaseworker,
                    leaderCTSC,
                    ctsc
                )
            ),
            Arguments.of(
                "ListServeClaim",
                List.of(
                    taskSupervisor,
                    hearingCentreTeamLeader,
                    adminCaseworker
                )
            ),
            Arguments.of(
                "ET3Processing",
                List.of(
                    taskSupervisor,
                    hearingCentreTeamLeader,
                    adminCaseworker
                )
            ),
            Arguments.of(
                "ET3Notification",
                List.of(
                    taskSupervisor,
                    hearingCentreTeamLeader,
                    adminCaseworker
                )
            ),
            Arguments.of(
                "InitialConsiderationDirections",
                List.of(
                    taskSupervisor,
                    hearingCentreTeamLeader,
                    adminCaseworker
                )
            ),
            Arguments.of(
                "PostHearingPromulgation",
                List.of(
                    taskSupervisor,
                    hearingCentreTeamLeader,
                    adminCaseworker
                )
            ),
            Arguments.of(
                "JudgmentPromulgation",
                List.of(
                    taskSupervisor,
                    hearingCentreTeamLeader,
                    adminCaseworker
                )
            ),
            Arguments.of(
                "ApplicationSubmission",
                List.of(
                    taskSupervisor,
                    hearingCentreTeamLeader,
                    adminCaseworker
                )
            ),
            Arguments.of(
                "AmendPartyDetails",
                List.of(
                    taskSupervisor,
                    hearingCentreTeamLeader,
                    adminCaseworker
                )
            ),
            Arguments.of(
                "WithdrawAllOrPartOfCase",
                List.of(
                    taskSupervisor,
                    hearingCentreTeamLeader,
                    adminCaseworker
                )
            ),

            Arguments.of(
                "reviewSpecificAccessRequestCTSC",
                List.of(
                    taskSupervisor,
                    approverCTSC
                )
            )
        );
    }

    /*
        important: permissions rules in the DMN are in order, in case you can't find why your test fails.
     */
    @ParameterizedTest
    @MethodSource("genericScenarioProvider")
    void given_taskType_and_CaseData_when_evaluate_then_returns_expected_rules(
        String taskType,
        List<Map<String, Serializable>> expectedRules) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("taskAttributes", Map.of("taskType", taskType));

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        Assertions.assertEquals(expectedRules, dmnDecisionTableResult.getResultList());
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        // The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();

        List<String> inputColumnIds = asList("taskType", "case");
        //Inputs
        assertThat(logic.getInputs().size(), is(2));
        assertThatInputContainInOrder(inputColumnIds, logic.getInputs());
        //Outputs
        List<String> outputColumnIds = asList(
            "caseAccessCategory",
            "name",
            "value",
            "roleCategory",
            "authorisations",
            "assignmentPriority",
            "autoAssignable"
        );
        assertThat(logic.getOutputs().size(), is(7));
        assertThatOutputContainInOrder(outputColumnIds, logic.getOutputs());
        //Rules
        assertThat(logic.getRules().size(), is(14));
    }

    private void assertThatInputContainInOrder(List<String> inputColumnIds, List<DmnDecisionTableInputImpl> inputs) {
        IntStream.range(0, inputs.size())
            .forEach(i -> assertThat(inputs.get(i).getInputVariable(), is(inputColumnIds.get(i))));
    }

    private void assertThatOutputContainInOrder(List<String> outputColumnIds, List<DmnDecisionTableOutputImpl> output) {
        IntStream.range(0, output.size())
            .forEach(i -> assertThat(output.get(i).getOutputName(), is(outputColumnIds.get(i))));
    }
}
