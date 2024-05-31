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
import uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable;
import uk.gov.hmcts.et.taskconfiguration.DmnDecisionTableBaseUnitTest;
import uk.gov.hmcts.et.taskconfiguration.utility.HelperService;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class EmploymentTaskInitiationMultipleTestScot extends DmnDecisionTableBaseUnitTest {

    public static final String REFERRAL_ADMIN =
        HelperService.createReferrals("Referral Subject 1", "Referral Subject 2", "Admin", "Yes", "", "");
    public static final String REFERRAL_JUDGE =
        HelperService.createReferrals("Referral Subject 1","ET1", "Judge", "Yes", "", "");

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = DmnDecisionTable.WA_TASK_INITIATION_MULTIPLE_ET_SCOTLAND;
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
                        "Review Referral #2 - Referral Subject 2",
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
                        "Review Referral #2 - ET1",
                        "Vetting"
                    )
                )
            ));
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
        assertThat(logic.getRules().size(), is(2));
    }
}
