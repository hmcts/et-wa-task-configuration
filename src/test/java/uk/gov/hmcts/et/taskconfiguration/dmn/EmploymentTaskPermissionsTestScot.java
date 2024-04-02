package uk.gov.hmcts.et.taskconfiguration.dmn;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableInputImpl;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableOutputImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.et.taskconfiguration.DmnDecisionTableBaseUnitTest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_PERMISSIONS_ET_SCOTLAND;

class EmploymentTaskPermissionsTestScot extends DmnDecisionTableBaseUnitTest {

    private static final Map<String, Serializable> taskSupervisor = Map.of(
        "autoAssignable", false,
        "name", "task-supervisor",
        "value", "Read, Manage, Claim, Unclaim, Assign, Unassign, Complete, Cancel"
    );

    private static final Map<String, Serializable> approverJudiciary = Map.of(
        "autoAssignable", false,
        "name", "specific-access-approver-judiciary",
        "roleCategory", "JUDICIAL",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn, Assign, Unassign"
    );
    private static final Map<String, Serializable> leadJudge = Map.of(
        "autoAssignable", true,
        "assignmentPriority", 1,
        "name", "lead-judge",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "JUDICIAL"
    );
    private static final Map<String, Serializable> hearingJudge = Map.of(
        "autoAssignable", true,
        "assignmentPriority", 2,
        "name", "hearing-judge",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "JUDICIAL"
    );
    private static final Map<String, Serializable> leadershipJudge = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 5,
        "name", "leadership-judge",
        "value", "Read, Execute, Manage, Claim, Assign, Unassign, Complete, Cancel",
        "roleCategory", "JUDICIAL"
    );
    private static final Map<String, Serializable> judge = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 3,
        "name", "judge",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "JUDICIAL"
    );
    private static final Map<String, Serializable> approverLegalOps = Map.of(
        "autoAssignable", false,
        "name", "specific-access-approver-legal-ops",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn, Assign, Unassign",
        "roleCategory", "LEGAL_OPERATIONS"
    );
    private static final Map<String, Serializable> allocatedTribunalCaseworker = Map.of(
        "autoAssignable", true,
        "assignmentPriority", 1,
        "name", "allocated-tribunal-caseworker",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "LEGAL_OPERATIONS"
    );
    private static final Map<String, Serializable> seniorTribunalCaseworker = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 4,
        "name", "senior-tribunal-caseworker",
        "value", "Read, Own, Manage, Claim, Unclaim, Assign, Unassign, Complete, Cancel",
        "roleCategory", "LEGAL_OPERATIONS"
    );
    private static final Map<String, Serializable> tribunalCaseworker = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 3,
        "name", "tribunal-caseworker",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "LEGAL_OPERATIONS"
    );

    private static final Map<String, Serializable> approverAdmin = Map.of(
        "autoAssignable", false,
        "name", "specific-access-approver-admin",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn, Assign, Unassign",
        "roleCategory", "ADMIN"
    );
    private static final Map<String, Serializable> allocatedAdminCaseworker = Map.of(
        "autoAssignable", true,
        "assignmentPriority", 2,
        "name", "allocated-admin-caseworker",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "ADMIN"
    );
    private static final Map<String, Serializable> hearingCentreTeamLeader = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 6,
        "name", "hearing-centre-team-leader",
        "value", "Read, Own, Manage, Claim, Unclaim, Assign, Unassign, Complete, Cancel",
        "roleCategory", "ADMIN"
    );
    private static final Map<String, Serializable> hearingCentreAdmin = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 4,
        "name", "hearing-centre-admin",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "ADMIN"
    );
    private static final Map<String, Serializable> regionalCentreTeamLeader = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 6,
        "name", "regional-centre-team-leader",
        "value", "Read, Own, Manage, Claim, Unclaim, Assign, Unassign, Complete, Cancel",
        "roleCategory", "ADMIN"
    );
    private static final Map<String, Serializable> regionalCentreAdmin = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 4,
        "name", "regional-centre-admin",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "ADMIN"
    );

    private static final Map<String, Serializable> approverCTSC = Map.of(
        "autoAssignable", true,
        "name", "specific-access-approver-ctsc",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn, Assign, Unassign",
        "roleCategory", "CTSC"
    );
    private static final Map<String, Serializable> allocatedCtscCaseworker = Map.of(
        "autoAssignable", true,
        "assignmentPriority", 1,
        "name", "allocated-ctsc-caseworker",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "CTSC"
    );
    private static final Map<String, Serializable> leaderCTSC = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 5,
        "name", "ctsc-team-leader",
        "value", "Read, Own, Manage, Claim, Unclaim, Assign, Unassign, Complete, Cancel",
        "roleCategory", "CTSC"
    );
    private static final Map<String, Serializable> ctsc = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 3,
        "name", "ctsc",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "CTSC"
    );

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_PERMISSIONS_ET_SCOTLAND;
    }

    public static Stream<Arguments> genericScenarioProvider() {
        return Stream.of(
            Arguments.of(
                "reviewSpecificAccessRequestJudiciary",
                List.of(
                    taskSupervisor,
                    approverJudiciary,
                    leadershipJudge
                )
            ),
            Arguments.of(
                "ReviewReferralJudiciary",
                List.of(
                    taskSupervisor,
                    leadJudge,
                    hearingJudge,
                    leadershipJudge,
                    judge,
                    allocatedTribunalCaseworker,
                    seniorTribunalCaseworker,
                    tribunalCaseworker
                )
            ),
            Arguments.of(
                "ReviewReferralResponseJudiciary",
                List.of(
                    taskSupervisor,
                    leadJudge,
                    hearingJudge,
                    leadershipJudge,
                    judge,
                    allocatedTribunalCaseworker,
                    seniorTribunalCaseworker,
                    tribunalCaseworker
                )
            ),
            Arguments.of(
                "CompleteInitialConsideration",
                List.of(
                    taskSupervisor,
                    leadJudge,
                    hearingJudge,
                    leadershipJudge,
                    judge,
                    allocatedTribunalCaseworker,
                    seniorTribunalCaseworker,
                    tribunalCaseworker
                )
            ),
            Arguments.of(
                "DraftAndSignJudgment",
                List.of(
                    taskSupervisor,
                    leadJudge,
                    hearingJudge,
                    leadershipJudge,
                    judge,
                    allocatedTribunalCaseworker,
                    seniorTribunalCaseworker,
                    tribunalCaseworker
                )
            ),

            Arguments.of(
                "reviewSpecificAccessRequestLegalOps",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    approverLegalOps
                )
            ),
            Arguments.of(
                "ReviewReferralLegalOps",
                List.of(
                    taskSupervisor,
                    leadJudge,
                    hearingJudge,
                    leadershipJudge,
                    judge,
                    allocatedTribunalCaseworker,
                    seniorTribunalCaseworker,
                    tribunalCaseworker
                )
            ),
            Arguments.of(
                "ReviewReferralResponseLegalOps",
                List.of(
                    taskSupervisor,
                    leadJudge,
                    hearingJudge,
                    leadershipJudge,
                    judge,
                    allocatedTribunalCaseworker,
                    seniorTribunalCaseworker,
                    tribunalCaseworker
                )
            ),
            Arguments.of(
                "ReviewRule21Referral",
                List.of(
                    taskSupervisor,
                    leadJudge,
                    hearingJudge,
                    leadershipJudge,
                    judge,
                    allocatedTribunalCaseworker,
                    seniorTribunalCaseworker,
                    tribunalCaseworker
                )
            ),

            Arguments.of(
                "reviewSpecificAccessRequestAdmin",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    approverAdmin
                )
            ),
            Arguments.of(
                "Et1Vetting",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin,
                    allocatedCtscCaseworker,
                    leaderCTSC,
                    ctsc
                )
            ),
            Arguments.of(
                "ReviewReferralAdmin",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin,
                    allocatedCtscCaseworker,
                    leaderCTSC,
                    ctsc
                )
            ),
            Arguments.of(
                "ReviewReferralResponseAdmin",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin,
                    allocatedCtscCaseworker,
                    leaderCTSC,
                    ctsc
                )
            ),
            Arguments.of(
                "SendEt1Notification",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin,
                    allocatedCtscCaseworker,
                    leaderCTSC,
                    ctsc
                )
            ),
            Arguments.of(
                "ListServeClaim",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin,
                    allocatedCtscCaseworker,
                    leaderCTSC,
                    ctsc
                )
            ),
            Arguments.of(
                "Rule21",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin
                )
            ),
            Arguments.of(
                "ET3Processing",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin
                )
            ),
            Arguments.of(
                "SendEt3Notification",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin
                )
            ),
            Arguments.of(
                "ListAHearing",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin,
                    regionalCentreTeamLeader,
                    regionalCentreAdmin
                )
            ),
            Arguments.of(
                "IssueInitialConsiderationDirections",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin
                )
            ),
            Arguments.of(
                "IssuePostHearingDirection",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin
                )
            ),
            Arguments.of(
                "IssueJudgment",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin
                )
            ),
            Arguments.of(
                "AmendClaimantDetails",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin
                )
            ),
            Arguments.of(
                "AmendRespondentDetails",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin
                )
            ),
            Arguments.of(
                "WithdrawAllOrPartOfCase",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin
                )
            ),
            Arguments.of(
                "ContactTribunalWithAnApplication",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin
                )
            ),
            Arguments.of(
                "ContactTribunalWithAnApplicationResponse",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin
                )
            ),

            Arguments.of(
                "reviewSpecificAccessRequestCTSC",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
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
        assertThat(logic.getRules().size(), is(20));
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
