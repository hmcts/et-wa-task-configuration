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
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_PERMISSIONS_MULTIPLE_ET_SCOTLAND;

class EmploymentTaskPermissionsMultipleTestScot extends DmnDecisionTableBaseUnitTest {

    private static final Map<String, Serializable> taskSupervisor = Map.of(
        "autoAssignable", false,
        "name", "task-supervisor",
        "value", "Read, Manage, Claim, Unclaim, Assign, Unassign, Complete, Cancel"
    );

    private static final Map<String, Serializable> leadershipJudge = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 5,
        "name", "leadership-judge",
        "value", "Read, Execute, Manage, Claim, Assign, Unassign, Complete, Cancel",
        "roleCategory", "JUDICIAL"
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
        "value", "Assign, Unassign, Complete, Cancel",
        "roleCategory", "ADMIN"
    );
    private static final Map<String, Serializable> hearingCentreAdmin = Map.of(
        "autoAssignable", false,
        "assignmentPriority", 4,
        "name", "hearing-centre-admin",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "ADMIN"
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
        "value", "Assign, Unassign, Complete, Cancel",
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
                "ReviewReferralResponseAdminMultiple",
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
        assertThat(logic.getRules().size(), is(8));
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
