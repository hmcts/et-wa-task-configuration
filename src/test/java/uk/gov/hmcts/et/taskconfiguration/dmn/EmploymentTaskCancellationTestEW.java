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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_CANCELLATION_ET_EW;

class EmploymentTaskCancellationTestEW extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_CANCELLATION_ET_EW;
    }

    public static Stream<Arguments> scenarioProvider() {
        return Stream.of(
            Arguments.of(
                null,
                "disposeCase",
                null,
                List.of(
                    Map.of(
                        "action", "Cancel",
                        "processCategories", "Vetting"
                    ),
                   Map.of(
                        "action", "Cancel",
                        "processCategories", "Processing"
                    ),
                    Map.of(
                        "action", "Cancel",
                        "processCategories", "Hearing"
                    ),
                    Map.of(
                        "action", "Cancel",
                        "processCategories", "Judgment"
                    ),
                    Map.of(
                        "action", "Cancel",
                        "processCategories", "Application"
                    ),
                    Map.of(
                        "action", "Cancel",
                        "processCategories", "Amendments"
                    ),
                    Map.of(
                        "action", "Cancel",
                        "processCategories", "ReviewRule21Referral"
                    )
                )
            ),
            Arguments.of(
                null,
                "et3Response",
                null,
                List.of(
                    Map.of(
                        "action", "Cancel",
                        "processCategories", "ReviewRule21Referral"
                    )
                )
            ),
            Arguments.of(
                null,
                "addAmendHearing",
                null,
                List.of(
                    Map.of(
                        "action", "Reconfigure"
                    )
                )
            ),
            Arguments.of(
                null,
                "addAmendJurisdiction",
                null,
                List.of(
                    Map.of(
                        "action", "Reconfigure"
                    )
                )
            ),
            Arguments.of(
                null,
                "caseTransferDifferentCountry",
                null,
                List.of(
                    Map.of(
                        "action", "Reconfigure"
                    )
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("scenarioProvider")
    void given_input_should_return_outcome_dmn(String fromState,
                                               String event,
                                               String state,
                                               List<Map<String, ? extends Serializable>> expectedDmnOutcome) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("fromState", fromState);
        inputVariables.putValue("event", event);
        inputVariables.putValue("state", state);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        assertThat(dmnDecisionTableResult.getResultList(), is(expectedDmnOutcome));
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getRules().size(), is(11));
    }
}
