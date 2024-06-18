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
import uk.gov.hmcts.et.taskconfiguration.utility.HelperService;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.*;

class EmploymentTaskInitiationMultipleTestEW extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = DmnDecisionTable.WA_TASK_INITIATION_MULTIPLE_ET_EW;
    }

    public static Stream<Arguments> scenarioProvider() {
        return Stream.of(
            Arguments.of(
                "createReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_ADMIN),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralAdminMultiple",
                        "Review Multiples Referral #2 - Referral Subject 2",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_LEGALOPS),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralLegalOpsMultiple",
                        "Review Multiples Referral #2 - Referral Subject 2",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_JUDGE),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralJudiciaryMultiple",
                        "Review Multiples Referral #2 - ET1",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "replyToReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_REPLY_LEGALOFFICER),
                List.of(
                    HelperService.mapExpectedOutput(
                        "MultiplesReviewReferralResponseLegalOps",
                        "Review Multiples Referral #1 - Referral Subject 1 Response",
                        "Processing"
                    )
                )
            ),
            Arguments.of(
                "replyToReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_REPLY_JUDGE),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralResponseJudiciaryMultiple",
                        "Review Multiples Referral #1 - Referral Subject 1 Response",
                        "Processing"
                    )
                )
            ),
            Arguments.of(
                "replyToReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_REPLY_ADMIN),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralResponseAdminMultiple",
                        "Review Multiples Referral #1 - Referral Subject 1 Response",
                        "Processing"
                    )
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("scenarioProvider")
    void given_input_should_return_outcome_dmn(String eventId,
                                               String postEventState,
                                               Map<String, Object> map,
                                               List<Map<String, Object>> expectation) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putAll(map);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getRules().size(), is(6));
    }
}
