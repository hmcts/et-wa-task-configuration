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
import uk.gov.hmcts.et.taskconfiguration.DmnDecisionTableBaseUnitTest;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_COMPLETION_ET_MULTIPLE_SCOTLAND;

class EmploymentTaskCompletionMultipleTestScot extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_COMPLETION_ET_MULTIPLE_SCOTLAND;
    }

    static Stream<Arguments> scenarioProvider() {
        return Stream.of(
            Arguments.of(
                "replyToReferral",
                List.of(
                    Map.of(
                        "taskType", "ReviewReferralAdminMultiple",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralJudiciaryMultiple",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralLegalOpsMultiple",
                        "completionMode", "Auto"
                    ),
                    Map.of("taskType", "MultiplesReviewReferralResponseLegalOps",
                           "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralResponseJudiciaryMultiple",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralResponseAdminMultiple",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "closeReferral",
                List.of(
                    Map.of(
                        "taskType", "ReviewReferralAdminMultiple",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralJudiciaryMultiple",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralLegalOpsMultiple",
                        "completionMode", "Auto"
                    ),
                    Map.of("taskType", "MultiplesReviewReferralResponseLegalOps",
                           "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralResponseJudiciaryMultiple",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralResponseAdminMultiple",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            )
        );
    }

    @ParameterizedTest(name = "event id: {0}")
    @MethodSource("scenarioProvider")
    void given_event_ids_should_evaluate_dmn(String eventId, List<Map<String, String>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getRules().size(), is(7));
    }
}
