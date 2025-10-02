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

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.hmcts.et.taskconfiguration.DmnDecisionTable.WA_TASK_COMPLETION_ET_EW;

class EmploymentTaskCompletionTestEW extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    static void initialization() {
        CURRENT_DMN_DECISION_TABLE = WA_TASK_COMPLETION_ET_EW;
    }

    static Stream<Arguments> scenarioProvider() {
        return Stream.of(
            Arguments.of(
                "preAcceptanceCase",
                asList(
                    Map.of(
                        "taskType", "Et1Vetting",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralJudiciary",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralLegalOps",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralResponseJudiciary",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralResponseLegalOps",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "replyToReferral",
                asList(
                    Map.of(
                        "taskType", "ReviewReferralAdmin",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralResponseAdmin",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralJudiciary",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralLegalOps",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralResponseJudiciary",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralResponseLegalOps",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "closeReferral",
                asList(
                    Map.of(
                        "taskType", "ReviewReferralAdmin",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralResponseAdmin",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralJudiciary",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralLegalOps",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralResponseJudiciary",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewReferralResponseLegalOps",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "uploadDocumentForServing",
                asList(
                    Map.of(
                        "taskType", "ListServeClaim",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "generateCorrespondence",
                asList(
                    Map.of(
                        "taskType", "SendEt1Notification",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "IssuePostHearingDirection",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "IssueInitialConsiderationDirections",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "addAmendHearing",
                asList(
                    Map.of(
                        "taskType", "ListAHearing",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "issueInitialConsiderationDirectionsWA",
                asList(
                    Map.of(
                        "taskType", "ListAHearing",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "initialConsideration",
                asList(
                    Map.of(
                        "taskType", "CompleteInitialConsideration",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "et3Notification",
                asList(
                    Map.of(
                        "taskType", "SendEt3Notification",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "et3Vetting",
                asList(
                    Map.of(
                        "taskType", "Rule21",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ET3Processing",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "createReferral",
                asList(
                    Map.of(
                        "taskType", "ReviewRule21Referral",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "ReviewECCResponse",
                        "completionMode", "Auto"
                    ),
                    Map.of(
                        "taskType", "WithdrawAllOrPartOfCase",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "draftAndSignJudgement",
                asList(
                    Map.of(
                        "taskType", "DraftAndSignJudgment",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "amendClaimantDetails",
                asList(
                    Map.of(
                        "taskType", "AmendClaimantDetails",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "amendRespondentDetails",
                asList(
                    Map.of(
                        "taskType", "AmendRespondentDetails",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "addAmendJudgment",
                asList(
                    Map.of(
                        "taskType", "IssueJudgment",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "disposeCase",
                asList(
                    Map.of(
                        "taskType", "WithdrawAllOrPartOfCase",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "tseAdmin",
                asList(
                    Map.of("taskType", "ContactTribunalWithAnApplication",
                           "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "tseAdmReply",
                asList(
                    Map.of("taskType", "ContactTribunalWithAnApplication",
                           "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "tseAdminCloseAnApplication",
                asList(
                    Map.of("taskType", "ContactTribunalWithAnApplication",
                           "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "broughtForward",
                asList(
                    Map.of(
                        "taskType", "ExpiredBfAction",
                        "completionMode", "Auto"
                    ),
                    emptyMap()
                )
            ),
            Arguments.of(
                "sendNotification",
                asList(
                    Map.of(
                        "taskType", "IssueOrder",
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
        assertThat(logic.getRules().size(), is(27));
    }
}
