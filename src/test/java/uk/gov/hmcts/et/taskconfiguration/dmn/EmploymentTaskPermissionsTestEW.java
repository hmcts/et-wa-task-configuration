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
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_PERMISSIONS_ET_EW;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.allocatedAdminCaseworker;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.allocatedCtscCaseworker;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.allocatedTribunalCaseworker;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.approverAdmin;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.approverCTSC;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.approverJudiciary;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.approverLegalOps;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.ctsc;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.feepaidjudge;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.hearingCentreAdmin;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.hearingCentreTeamLeader;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.hearingJudge;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.judge;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.leadJudge;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.leaderCTSC;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.leadershipJudge;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.regionalCentreAdmin;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.regionalCentreTeamLeader;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.seniorTribunalCaseworker;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.taskSupervisor;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.tribunalCaseworker;

class EmploymentTaskPermissionsTestEW extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_PERMISSIONS_ET_EW;
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
                    feepaidjudge,
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
                    feepaidjudge,
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
                    feepaidjudge,
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
                    feepaidjudge,
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
                    feepaidjudge,
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
                    feepaidjudge,
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
                    feepaidjudge,
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
                "ReviewECCResponse",
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
            ),

            Arguments.of(
                "ReferEmployersContractClaim",
                List.of(
                    taskSupervisor,
                    leadershipJudge,
                    allocatedAdminCaseworker,
                    hearingCentreTeamLeader,
                    hearingCentreAdmin
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
        assertThat(logic.getRules().size(), is(21));
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
