package uk.gov.hmcts.et.taskconfiguration.dmn.multiples;

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
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_PERMISSIONS_MULTIPLE_ET_SCOTLAND;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.allocatedAdminCaseworker;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.allocatedCtscCaseworker;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.allocatedTribunalCaseworker;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.ctsc;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.hearingCentreAdmin;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.hearingCentreTeamLeader;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.hearingJudge;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.judge;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.leadJudge;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.leaderCTSC;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.leadershipJudge;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.seniorTribunalCaseworker;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.taskSupervisor;
import static uk.gov.hmcts.et.taskconfiguration.utility.PermissionsUtility.tribunalCaseworker;

class EmploymentTaskPermissionsMultipleTestScot extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_PERMISSIONS_MULTIPLE_ET_SCOTLAND;
    }

    public static Stream<Arguments> genericScenarioProvider() {
        return Stream.of(
            Arguments.of(
                "ReviewReferralAdminMultiple",
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
                "MultiplesReviewReferralResponseLegalOps",
                List.of(
                    taskSupervisor,
                    leadJudge,
                    hearingJudge,
                    judge,
                    leadershipJudge,
                    allocatedTribunalCaseworker,
                    seniorTribunalCaseworker,
                    tribunalCaseworker
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
