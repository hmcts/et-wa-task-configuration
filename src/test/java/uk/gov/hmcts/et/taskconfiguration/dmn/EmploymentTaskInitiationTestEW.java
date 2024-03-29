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

class EmploymentTaskInitiationTestEW extends DmnDecisionTableBaseUnitTest {

    public static final String REFERRAL_ADMIN =
        HelperService.createReferrals("Referral Subject 1","Referral Subject 2", "Admin", "Yes", "", "");
    public static final String REFERRAL_ADMIN_HEARING =
        HelperService.createReferrals("Referral Subject 1","Hearings", "Admin", "Yes", "", "");
    public static final String REFERRAL_ADMIN_JUDGMENT =
        HelperService.createReferrals("Referral Subject 1","Judgment", "Admin", "Yes", "", "");
    public static final String REFERRAL_JUDGE =
        HelperService.createReferrals("Referral Subject 1","ET1", "Judge", "Yes", "", "");
    public static final String REFERRAL_JUDGE_RULE21 =
        HelperService.createReferrals("Referral Subject 1","Rule 21", "Judge", "Yes", "", "");
    public static final String REFERRAL_LEGALOFFICER =
        HelperService.createReferrals("Referral Subject 1","Referral Subject 2", "Legal officer", "Yes", "", "");

    public static final String REFERRAL_REPLY_ADMIN =
        HelperService.createReferrals("Referral Subject 1","Referral Subject 2", "", "", "Admin", "Yes");
    public static final String REFERRAL_REPLY_JUDGE =
        HelperService.createReferrals("Referral Subject 1","Referral Subject 2", "", "", "Judge", "Yes");
    public static final String REFERRAL_REPLY_LEGALOFFICER =
        HelperService.createReferrals("Referral Subject 1","Referral Subject 2", "", "", "Legal officer", "Yes");

    public static final String IS_JUDGEMENT_TRUE =
        "\"draftAndSignJudgement\":{\"isJudgement\":true}";
    public static final String IS_JUDGEMENT_FALSE =
        "\"draftAndSignJudgement\":{\"isJudgement\":false}";

    public static final String ET3_FORM_RECEIVED =
        "\"respondentCollection\":[{\"value\":{\"responseReceived\":true}}]";
    public static final String ET3_FORM_NOT_RECEIVED =
        "\"respondentCollection\":[{\"value\":{\"responseReceived\":false}}]";

    public static final String IS_ET3_RESPONSE_TRUE =
        "\"respondentCollection\":[{\"value\":{\"et3Vetting\":{\"et3IsThereAnEt3Response\":true}}}]";
    public static final String IS_ET3_RESPONSE_FALSE =
        "\"respondentCollection\":[{\"value\":{\"et3Vetting\":{\"et3IsThereAnEt3Response\":false}}}]";

    public static final String LISTAHEARING_PROCEED_LISTED = "\"etICCanProceed\":true,"
        + "\"etICHearingAlreadyListed\":true,"
        + "\"etICHearingNotListedList\":["
        + "\"List for preliminary hearing\","
        + "\"Seek comments on the video hearing\","
        + "\"List for final hearing\","
        + "\"UDL hearing\"]";

    public static final String LISTAHEARING_PROCEED_NOTLISTED_PRELIM = "\"etICCanProceed\":true,"
        + "\"etICHearingAlreadyListed\":false,"
        + "\"etICHearingNotListedList\":["
        + "\"Seek comments on the video hearing\","
        + "\"List for preliminary hearing\","
        + "\"UDL hearing\"]";

    public static final String LISTAHEARING_PROCEED_NOTLISTED_FINAL = "\"etICCanProceed\":true,"
        + "\"etICHearingAlreadyListed\":false,"
        + "\"etICHearingNotListedList\":["
        + "\"Seek comments on the video hearing\","
        + "\"List for final hearing\","
        + "\"UDL hearing\"]";
    public static final String LISTAHEARING_PROCEED_NOTLISTED_FINAL_WITH_STRIKE_OUT_CLAIM = "\"etICCanProceed\":true,"
        + "\"etICHearingAlreadyListed\":false,"
        + "\"etICHearingNotListedList\":["
        + "\"Seek comments on the video hearing\","
        + "\"List for final hearing\","
        + "\"UDL hearing\"],"
        + "\"etInitialConsiderationRule27\": {"
        + "\"etICRule27ClaimToBe\": \"Dismissed in full\""
        + "}";

    public static final String LISTAHEARING_PROCEED_NOTLISTED_NONE = "\"etICCanProceed\":true,"
        + "\"etICHearingAlreadyListed\":false,"
        + "\"etICHearingNotListedList\":["
        + "\"Seek comments on the video hearing\","
        + "\"UDL hearing\"]";

    public static final String STRIKE_OUT_CLAIM =
        "\"etInitialConsiderationRule27\": {"
            + "\"etICRule27ClaimToBe\": \"Dismissed in full\""
            + "}";

    public static final String HEARING_DETAIL_COLLECTION_HEARD_VACATED =
        "\"hearingDetailsCollection\": ["
            + "{\"value\": {\"hearingDetailsStatus\": \"Heard\"}},"
            + "{\"value\": {\"hearingDetailsStatus\": \"Vacated\"}}"
            + "]";
    public static final String HEARING_DETAIL_COLLECTION_VACATED_ONLY =
        "\"hearingDetailsCollection\": [{\"value\": {\"hearingDetailsStatus\": \"Vacated\"}}]";

    public static final String SUBMISSION_REASON_CLAIMANT_AMEND =
        HelperService.createApplications("Amend my claim", "");
    public static final String SUBMISSION_REASON_CLAIMANT_CONTACT =
        HelperService.createApplications("Contact about something else", "");
    public static final String SUBMISSION_REASON_CLAIMANT_PERSONALDETAILS =
        HelperService.createApplications("Change my personal details", "");

    public static final String SUBMISSION_REASON_RESPONDENT_AMEND =
        HelperService.createApplications("Amend response", "");
    public static final String SUBMISSION_REASON_RESPONDENT_CONTACT =
        HelperService.createApplications("Contact the tribunal", "");
    public static final String SUBMISSION_REASON_RESPONDENT_PERSONALDETAILS =
        HelperService.createApplications("Change personal details", "");

    public static final String RESPONDENT_RESPONDING_TO_CLAIMANT_AMEND =
        HelperService.createApplications("Amend my claim", "Respondent");
    public static final String RESPONDENT_RESPONDING_TO_CLAIMANT_CONTACT =
        HelperService.createApplications("Contact about something else", "Respondent");
    public static final String RESPONDENT_RESPONDING_TO_CLAIMANT_PERSONALDETAILS =
        HelperService.createApplications("Change my personal details", "Respondent");

    public static final String CLAIMANT_RESPONDING_TO_RESPONDENT_AMEND =
        HelperService.createApplications("Amend response", "Claimant");
    public static final String CLAIMANT_RESPONDING_TO_RESPONDENT_CONTACT =
        HelperService.createApplications("Contact the tribunal", "Claimant");
    public static final String CLAIMANT_RESPONDING_TO_RESPONDENT_PERSONALDETAILS =
        HelperService.createApplications("Change personal details", "Claimant");

    public static final String CLAIMANT_WITHDRAW_ALL_OR_PART_OF_CASE =
        HelperService.createApplications("Withdraw all/part of claim", "Claimant");

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = DmnDecisionTable.WA_TASK_INITIATION_ET_EW;
    }

    public static Stream<Arguments> scenarioProvider() {
        return Stream.of(
            Arguments.of(
                "SUBMIT_CASE_DRAFT",
                "Submitted",
                null,
                List.of(
                    HelperService.mapExpectedOutput(
                        "Et1Vetting",
                        "Et1 Vetting",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_ADMIN),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralAdmin",
                        "Review Referral - Admin",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_ADMIN_HEARING),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralAdmin",
                        "Review Referral - Admin",
                        "Vetting"
                    ),
                    HelperService.mapExpectedOutput(
                        "IssuePostHearingDirection",
                        "Issue Post Hearing Direction",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_ADMIN_JUDGMENT),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralAdmin",
                        "Review Referral - Admin",
                        "Vetting"
                    ),
                    HelperService.mapExpectedOutput(
                        "IssueJudgment",
                        "Issue Judgment",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_JUDGE),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralJudiciary",
                        "Review Referral - Judicial",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_JUDGE_RULE21),
                List.of(
                    HelperService.mapExpectedOutput(
                        "DraftAndSignJudgment",
                        "Draft And Sign Judgment",
                        "Judgment"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_LEGALOFFICER),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralLegalOps",
                        "Review Referral - Legal Ops",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "replyToReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_REPLY_ADMIN),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralResponseAdmin",
                        "Review Referral #1 - Referral Subject 1 Response",
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
                        "ReviewReferralResponseJudiciary",
                        "Review Referral #1 - Referral Subject 1 Response",
                        "Processing"
                    )
                )
            ),
            Arguments.of(
                "replyToReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_REPLY_LEGALOFFICER),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralResponseLegalOps",
                        "Review Referral #1 - Referral Subject 1 Response",
                        "Processing"
                    )
                )
            ),
            Arguments.of(
                "draftAndSignJudgement",
                null,
                HelperService.mapAdditionalData(IS_JUDGEMENT_TRUE),
                List.of(
                    HelperService.mapExpectedOutput(
                        "IssueJudgment",
                        "Issue Judgment",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "draftAndSignJudgement",
                null,
                HelperService.mapAdditionalData(IS_JUDGEMENT_FALSE),
                List.of()
            ),
            Arguments.of(
                "preAcceptanceCase",
                "Accepted",
                null,
                List.of(
                    HelperService.mapExpectedOutput(
                        "ListServeClaim",
                        "List/ Serve Claim",
                        "Vetting"
                    ),
                    HelperService.mapExpectedOutput(
                        "SendEt1Notification",
                        "Send ET1 Notification",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "preAcceptanceCase",
                "Rejected",
                null,
                List.of(
                    HelperService.mapExpectedOutput(
                        "SendEt1Notification",
                        "Send ET1 Notification",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "WA_REVIEW_RULE21_REFERRAL",
                "Accepted",
                null,
                List.of(
                    HelperService.mapExpectedOutput(
                        "Rule21",
                        "Rule 21",
                        "Rule21"
                    )
                )
            ),
            Arguments.of(
                "submitEt3",
                "Accepted",
                null,
                List.of(
                    HelperService.mapExpectedOutput(
                        "ET3Processing",
                        "ET3 Processing",
                        "Processing"
                    )
                )
            ),
            Arguments.of(
                "amendRespondentDetails",
                null,
                HelperService.mapAdditionalData(ET3_FORM_RECEIVED),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ET3Processing",
                        "ET3 Processing",
                        "Processing"
                    )
                )
            ),
            Arguments.of(
                "amendRespondentDetails",
                null,
                HelperService.mapAdditionalData(ET3_FORM_NOT_RECEIVED),
                List.of()
            ),
            Arguments.of(
                "et3Vetting",
                "Accepted",
                HelperService.mapAdditionalData(IS_ET3_RESPONSE_FALSE),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewRule21Referral",
                        "Review Rule 21 Referral",
                        "Rule21"
                    )
                )
            ),
            Arguments.of(
                "et3Vetting",
                "Accepted",
                HelperService.mapAdditionalData(IS_ET3_RESPONSE_TRUE),
                List.of(
                    HelperService.mapExpectedOutput(
                        "CompleteInitialConsideration",
                        "Complete Initial Consideration",
                        "Processing"
                    ),
                    HelperService.mapExpectedOutput(
                        "SendEt3Notification",
                        "Send ET3 Notification",
                        "Processing"
                    )
                )
            ),
            Arguments.of(
                "initialConsideration",
                "Accepted",
                HelperService.mapAdditionalData(STRIKE_OUT_CLAIM),
                List.of(
                    HelperService.mapExpectedOutput(
                        "IssueInitialConsiderationDirections",
                        "Issue Initial Consideration Directions",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "initialConsideration",
                "Accepted",
                HelperService.mapAdditionalData(LISTAHEARING_PROCEED_LISTED),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ListAHearing",
                        "List A Hearing",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "initialConsideration",
                "Accepted",
                HelperService.mapAdditionalData(LISTAHEARING_PROCEED_NOTLISTED_PRELIM),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ListAHearing",
                        "List A Hearing",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "initialConsideration",
                "Accepted",
                HelperService.mapAdditionalData(LISTAHEARING_PROCEED_NOTLISTED_FINAL),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ListAHearing",
                        "List A Hearing",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "initialConsideration",
                "Accepted",
                HelperService.mapAdditionalData(LISTAHEARING_PROCEED_NOTLISTED_NONE),
                List.of()
            ),
            Arguments.of(
                "initialConsideration",
                "Accepted",
                HelperService.mapAdditionalData(LISTAHEARING_PROCEED_NOTLISTED_FINAL_WITH_STRIKE_OUT_CLAIM),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ListAHearing",
                        "List A Hearing",
                        "Hearing"
                    ),
                    HelperService.mapExpectedOutput(
                        "IssueInitialConsiderationDirections",
                        "Issue Initial Consideration Directions",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "issueInitialConsiderationDirectionsWA",
                "Accepted",
                null,
                List.of(
                    HelperService.mapExpectedOutput(
                        "IssueInitialConsiderationDirections",
                        "Issue Initial Consideration Directions",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "updateHearing",
                "Accepted",
                HelperService.mapAdditionalData(HEARING_DETAIL_COLLECTION_HEARD_VACATED),
                List.of(
                    HelperService.mapExpectedOutput(
                        "DraftAndSignJudgment",
                        "Draft And Sign Judgment",
                        "Judgment"
                    )
                )
            ),
            Arguments.of(
                "updateHearing",
                "Accepted",
                HelperService.mapAdditionalData(HEARING_DETAIL_COLLECTION_VACATED_ONLY),
                List.of()
            ),
            Arguments.of(
                "SUBMIT_CLAIMANT_TSE",
                "Accepted",
                HelperService.mapAdditionalData(SUBMISSION_REASON_CLAIMANT_AMEND),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ContactTribunalWithAnApplication",
                        "Application - Amend my claim",
                        "Application"
                    )
                )
            ),
            Arguments.of(
                "SUBMIT_CLAIMANT_TSE",
                "Accepted",
                HelperService.mapAdditionalData(SUBMISSION_REASON_CLAIMANT_CONTACT),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ContactTribunalWithAnApplication",
                        "Contact the tribunal",
                        "Application"
                    )
                )
            ),
            Arguments.of(
                "CLAIMANT_TSE_RESPOND",
                "Accepted",
                HelperService.mapAdditionalData(CLAIMANT_RESPONDING_TO_RESPONDENT_AMEND),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ContactTribunalWithAnApplication",
                        "Application Response - Amend response",
                        "Application"
                    )
                )
            ),
            Arguments.of(
                "CLAIMANT_TSE_RESPOND",
                "Accepted",
                HelperService.mapAdditionalData(CLAIMANT_RESPONDING_TO_RESPONDENT_CONTACT),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ContactTribunalWithAnApplication",
                        "Contact the tribunal Response",
                        "Application"
                    )
                )
            ),
            Arguments.of(
                "respondentTSE",
                "Accepted",
                HelperService.mapAdditionalData(SUBMISSION_REASON_RESPONDENT_AMEND),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ContactTribunalWithAnApplication",
                        "Application - Amend response",
                        "Application"
                    )
                )
            ),
            Arguments.of(
                "respondentTSE",
                "Accepted",
                HelperService.mapAdditionalData(SUBMISSION_REASON_RESPONDENT_CONTACT),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ContactTribunalWithAnApplication",
                        "Contact the tribunal",
                        "Application"
                    )
                )
            ),
            Arguments.of(
                "tseRespond",
                "Accepted",
                HelperService.mapAdditionalData(RESPONDENT_RESPONDING_TO_CLAIMANT_AMEND),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ContactTribunalWithAnApplication",
                        "Application Response - Amend my claim",
                        "Application"
                    )
                )
            ),
            Arguments.of(
                "tseRespond",
                "Accepted",
                HelperService.mapAdditionalData(RESPONDENT_RESPONDING_TO_CLAIMANT_CONTACT),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ContactTribunalWithAnApplication",
                        "Contact the tribunal Response",
                        "Application"
                    )
                )
            ),
            Arguments.of(
                "SUBMIT_CLAIMANT_TSE",
                null,
                HelperService.mapAdditionalData(SUBMISSION_REASON_CLAIMANT_PERSONALDETAILS),
                List.of(
                    HelperService.mapExpectedOutput(
                        "AmendClaimantDetails",
                        "Amend Party Details",
                        "Amendments"
                    )
                )
            ),
            Arguments.of(
                "CLAIMANT_TSE_RESPOND",
                null,
                HelperService.mapAdditionalData(CLAIMANT_RESPONDING_TO_RESPONDENT_PERSONALDETAILS),
                List.of(
                    HelperService.mapExpectedOutput(
                        "AmendRespondentDetails",
                        "Amend Party Details",
                        "Amendments"
                    )
                )
            ),
            Arguments.of(
                "respondentTSE",
                null,
                HelperService.mapAdditionalData(SUBMISSION_REASON_RESPONDENT_PERSONALDETAILS),
                List.of(
                    HelperService.mapExpectedOutput(
                        "AmendRespondentDetails",
                        "Amend Party Details",
                        "Amendments"
                    )
                )
            ),
            Arguments.of(
                "tseRespond",
                null,
                HelperService.mapAdditionalData(RESPONDENT_RESPONDING_TO_CLAIMANT_PERSONALDETAILS),
                List.of(
                    HelperService.mapExpectedOutput(
                        "AmendClaimantDetails",
                        "Amend Party Details",
                        "Amendments"
                    )
                )
            ),
            Arguments.of(
                "SUBMIT_CLAIMANT_TSE",
                "Accepted",
                HelperService.mapAdditionalData(CLAIMANT_WITHDRAW_ALL_OR_PART_OF_CASE),
                List.of(
                    HelperService.mapExpectedOutput(
                        "WithdrawAllOrPartOfCase",
                        "Withdraw All or Part of Case",
                        "Amendments"
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
        assertThat(logic.getRules().size(), is(32));
    }
}
