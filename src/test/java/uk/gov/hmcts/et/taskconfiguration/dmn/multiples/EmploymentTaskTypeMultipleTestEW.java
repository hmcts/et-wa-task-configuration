package uk.gov.hmcts.et.taskconfiguration.dmn.multiples;

import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.impl.DmnDecisionTableImpl;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.impl.VariableMapImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable;
import uk.gov.hmcts.et.taskconfiguration.DmnDecisionTableBaseUnitTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EmploymentTaskTypeMultipleTestEW extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = DmnDecisionTable.WA_TASK_TYPE_MULTIPLE_ET_EW;
    }

    static Stream<Arguments> scenarioProvider() {
        return Stream.of(
            Arguments.of(
                List.of(
                    Map.of("taskTypeId",
                           "ReviewReferralAdminMultiple",
                           "taskTypeName",
                           "Review Multiples Referral - Admin"
                    ),
                    Map.of("taskTypeId",
                           "ReviewReferralJudiciaryMultiple",
                           "taskTypeName",
                           "Review Multiples Referral - Judicial"
                    ),
                    Map.of("taskTypeId",
                           "ReviewReferralLegalOpsMultiple",
                           "taskTypeName",
                           "Review Multiples Referral - Legal Ops"),
                    Map.of(
                        "taskTypeId",
                        "MultiplesReviewReferralResponseLegalOps",
                        "taskTypeName",
                        "Review Referral Response - Legal Ops"
                    )
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
        assertThat(logic.getRules().size(), is(4));
    }
}
