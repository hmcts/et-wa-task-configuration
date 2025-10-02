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
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.CLAIMANT_RESPONDING_TO_RESPONDENT_AMEND;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.CLAIMANT_RESPONDING_TO_RESPONDENT_CONTACT;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.CLAIMANT_RESPONDING_TO_RESPONDENT_PERSONALDETAILS;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.CLAIMANT_WITHDRAW_ALL_OR_PART_OF_CASE;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.DRAFT_AND_SIGN_JUDGEMENT;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.DRAFT_AND_SIGN_JUDGEMENT_ORDER;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.ET3_FORM_NOT_RECEIVED;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.ET3_FORM_RECEIVED_MORE;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.ET3_FORM_RECEIVED_ONCE;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.IS_ET3_RESPONSE_FALSE;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.IS_ET3_RESPONSE_TRUE;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.LISTAHEARING_PROCEED_LISTED;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.LISTAHEARING_PROCEED_NOTLISTED_FINAL;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.LISTAHEARING_PROCEED_NOTLISTED_NONE;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.LISTAHEARING_PROCEED_NOTLISTED_PRELIM;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.NOTIFICATIONS_LIST;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.REFERRAL_ADMIN;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.REFERRAL_ADMIN_HEARING;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.REFERRAL_ADMIN_JUDGMENT;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.REFERRAL_JUDGE;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.REFERRAL_JUDGE_RULE21;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.REFERRAL_LEGALOFFICER;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.REFERRAL_OTHER_SUBJECT;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.REFERRAL_REPLY_ADMIN;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.REFERRAL_REPLY_JUDGE;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.REFERRAL_REPLY_LEGALOFFICER;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.REFERRAL_REPLY_OTHER_SUBJECT;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.RESPONDENT_RESPONDING_TO_CLAIMANT_AMEND;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.RESPONDENT_RESPONDING_TO_CLAIMANT_CONTACT;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.RESPONDENT_RESPONDING_TO_CLAIMANT_PERSONALDETAILS;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.SUBMISSION_REASON_CLAIMANT_AMEND;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.SUBMISSION_REASON_CLAIMANT_CONTACT;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.SUBMISSION_REASON_CLAIMANT_PERSONALDETAILS;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.SUBMISSION_REASON_RESPONDENT_AMEND;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.SUBMISSION_REASON_RESPONDENT_CONTACT;
import static uk.gov.hmcts.et.taskconfiguration.utility.InitiationUtility.SUBMISSION_REASON_RESPONDENT_PERSONALDETAILS;

class EmploymentTaskInitiationTestScot extends DmnDecisionTableBaseUnitTest {

    public static final String HEARING_DETAIL_COLLECTION_HEARD_VACATED =
        "\"hearingDetailsCollection\": ["
            + "{\"value\": {\"hearingDetailsStatus\": \"Heard\"}},"
            + "{\"value\": {\"hearingDetailsStatus\": \"Heard\"}},"
            + "{\"value\": {\"hearingDetailsStatus\": \"Vacated\"}}"
            + "]";
    public static final String HEARING_DETAIL_COLLECTION_POSTPONED =
        "\"hearingDetailsCollection\": ["
            + "{\"value\": {\"hearingDetailsStatus\": \"Heard\"}},"
            + "{\"value\": {\"hearingDetailsStatus\": \"Postponed\"}}"
            + "]";

    @BeforeAll
    static void initialization() {
        CURRENT_DMN_DECISION_TABLE = DmnDecisionTable.WA_TASK_INITIATION_ET_SCOTLAND;
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
                "initiateCase",
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
                HelperService.mapAdditionalData(REFERRAL_OTHER_SUBJECT),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralAdmin",
                        "Review Referral",
                        "reviewReferralSerialNumberAndSubject_2 - Random Subject"
                    )
                )
            ),
            Arguments.of(
                "replyToReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_REPLY_OTHER_SUBJECT),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralResponseJudiciary",
                        "Review Referral Response",
                        "reviewReferralResponseSerialNumberAndSubject_1 - Random Subject"
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
                        "Review Referral",
                        "reviewReferralSerialNumberAndSubject_2 - Referral Subject 2"
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
                        "Review Referral",
                        "reviewReferralSerialNumberAndSubject_2 - Hearings"
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
                        "Review Referral",
                        "reviewReferralSerialNumberAndSubject_2 - Judgment"
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
                        "Review Referral",
                        "reviewReferralSerialNumberAndSubject_2 - ET1"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                HelperService.mapAdditionalData(REFERRAL_JUDGE_RULE21),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewReferralJudiciary",
                        "Review Referral",
                        "reviewReferralSerialNumberAndSubject_2 - Rule 21"
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
                        "Review Referral",
                        "reviewReferralSerialNumberAndSubject_2 - Referral Subject 2"
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
                        "Review Referral Response",
                        "reviewReferralResponseSerialNumberAndSubject_1 - Referral Subject 1"
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
                        "Review Referral Response",
                        "reviewReferralResponseSerialNumberAndSubject_1 - Referral Subject 1"
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
                        "Review Referral Response",
                        "reviewReferralResponseSerialNumberAndSubject_1 - Referral Subject 1"
                    )
                )
            ),
            Arguments.of(
                "draftAndSignJudgement",
                null,
                HelperService.mapAdditionalData(DRAFT_AND_SIGN_JUDGEMENT),
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
                HelperService.mapAdditionalData(DRAFT_AND_SIGN_JUDGEMENT_ORDER),
                List.of(
                    HelperService.mapExpectedOutput(
                        "IssueOrder",
                        "Issue Order",
                        "Hearing"
                    )
                )
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
                        "Rule 22",
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
                HelperService.mapAdditionalData(ET3_FORM_NOT_RECEIVED),
                List.of()
            ),
            Arguments.of(
                "amendRespondentDetails",
                null,
                HelperService.mapAdditionalData(ET3_FORM_RECEIVED_ONCE),
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
                HelperService.mapAdditionalData(ET3_FORM_RECEIVED_MORE),
                List.of()
            ),
            Arguments.of(
                "et3Vetting",
                "Accepted",
                HelperService.mapAdditionalData(IS_ET3_RESPONSE_FALSE),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewRule21Referral",
                        "Review Rule 22 Referral",
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
                "initialConsideration",
                "Accepted",
                HelperService.mapAdditionalData(LISTAHEARING_PROCEED_LISTED),
                List.of(
                    HelperService.mapExpectedOutput(
                        "IssueInitialConsiderationDirections",
                        "Issue Initial Consideration Directions",
                        "Hearing"
                    ),
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
                        "IssueInitialConsiderationDirections",
                        "Issue Initial Consideration Directions",
                        "Hearing"
                    ),
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
                        "IssueInitialConsiderationDirections",
                        "Issue Initial Consideration Directions",
                        "Hearing"
                    ),
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
                List.of(
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
                        "Draft And Sign Judgment/Order",
                        "Judgment"
                    )
                )
            ),
            Arguments.of(
                "updateHearing",
                "Accepted",
                HelperService.mapAdditionalData(HEARING_DETAIL_COLLECTION_POSTPONED),
                List.of()
            ),
            Arguments.of(
                "SUBMIT_CLAIMANT_TSE",
                "Accepted",
                HelperService.mapAdditionalData(SUBMISSION_REASON_CLAIMANT_AMEND),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ContactTribunalWithAnApplication",
                        "Review Application - Amend my claim",
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
                        "Review Application Response - Amend response",
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
                        "Review Application - Amend response",
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
                        "Review Application Response - Amend my claim",
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
            ),
            Arguments.of(
                "UPDATE_NOTIFICATION_RESPONSE",
                null,
                HelperService.mapAdditionalData(NOTIFICATIONS_LIST),
                List.of(
                    HelperService.mapExpectedOutput(
                        "ReviewECCResponse",
                        "Review ECC Reply",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "submitEt1Draft",
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
                "SUBMIT_ET3_FORM",
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
                "createEcmCase",
                "Submitted",
                null,
                List.of(
                    HelperService.mapExpectedOutput(
                        "Et1Vetting",
                        "Et1 Vetting",
                        "Vetting"
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
        assertThat(logic.getRules().size(), is(46));
    }
}
