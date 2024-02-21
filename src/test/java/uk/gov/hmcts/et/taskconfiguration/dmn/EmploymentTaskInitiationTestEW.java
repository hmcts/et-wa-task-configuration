package uk.gov.hmcts.et.taskconfiguration.dmn;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class EmploymentTaskInitiationTestEW extends DmnDecisionTableBaseUnitTest {

    public static final DateTimeFormatter OLD_DATE_TIME_PATTERN =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static final String REFERRAL_COLLECTION = "\"referralCollection\":[%s]";
    public static final String REFERRAL = "{\"value\": "
        + "{\"referralNumber\": \"%s\",\"referralSubject\":\"%s\",\"referCaseTo\":\"%s\",\"isUrgent\":\"%s\"%s}"
        + "}";
    public static final String REFERRALREPLY_COLLECTION = ",\"referralReplyCollection\": [%s]";
    public static final String REFERRALREPLY = "{\"value\":"
        + "{\"referralNumber\": \"%s\",\"referralSubject\":\"%s\",\"directionTo\":\"%s\""
        + ",\"isUrgentReply\":\"%s\",\"replyDateTime\":\"%s\"}"
        + "}";

    public static final String REFERRAL_ADMIN =
        createReferrals("Referral Subject 1","Referral Subject 2", "Admin", "Yes", "", "");
    public static final String REFERRAL_ADMIN_HEARING =
        createReferrals("Referral Subject 1","Hearings", "Admin", "Yes", "", "");
    public static final String REFERRAL_ADMIN_JUDGMENT =
        createReferrals("Referral Subject 1","Judgment", "Admin", "Yes", "", "");
    public static final String REFERRAL_JUDGE =
        createReferrals("Referral Subject 1","ET1", "Judge", "Yes", "", "");
    public static final String REFERRAL_JUDGE_RULE21 =
        createReferrals("Referral Subject 1","Rule 21", "Judge", "Yes", "", "");
    public static final String REFERRAL_LEGALOFFICER =
        createReferrals("Referral Subject 1","Referral Subject 2", "Legal officer", "Yes", "", "");

    public static final String REFERRAL_REPLY_ADMIN =
        createReferrals("Referral Subject 1","Referral Subject 2", "", "", "Admin", "Yes");
    public static final String REFERRAL_REPLY_JUDGE =
        createReferrals("Referral Subject 1","Referral Subject 2", "", "", "Judge", "Yes");
    public static final String REFERRAL_REPLY_LEGALOFFICER =
        createReferrals("Referral Subject 1","Referral Subject 2", "", "", "Legal officer", "Yes");

    public static final DateTimeFormatter BF_DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final int BROUGHT_FORWARD_AMOUNT = 2;
    public static final String BROUGHT_FORWARD = "\"bfActions\":["
        + "{\"value\":{\"bfDate\":\""
        + LocalDateTime.now().plusDays(BROUGHT_FORWARD_AMOUNT).format(BF_DATE_PATTERN)
        + "\"}}]";

    public static final String ET3_FORM_RECEIVED =
        "\"respondentCollection\":[{\"value\":{\"responseReceived\":true}}]";
    public static final String ET3_FORM_NOT_RECEIVED =
        "\"respondentCollection\":[{\"value\":{\"responseReceived\":false}}]";

    public static final String RULE26_YES =
        "\"respondentCollection\":[{\"value\":{\"et3Vetting\":{\"et3Rule26\":true}}}]";

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

    public static final String APPLICATION_COLLECTION =
        "\"genericTseApplicationCollection\":[{\"value\": {\"number\": \"1\",\"type\": \"%s\"%s}}]";
    public static final String RESPOND_COLLECTION =
        ",\"respondCollection\": [{\"value\": {\"applicationType\": \"%s\",\"from\": \"%s\"}}]";

    public static final String HEARING_DETAIL_COLLECTION_HEARD =
        "\"hearingDetailsCollection\": [{\"value\": {\"hearingDetailsStatus\": \"Heard\"}}]";
    public static final String HEARING_DETAIL_COLLECTION_VACATED =
        "\"hearingDetailsCollection\": [{\"value\": {\"hearingDetailsStatus\": \"Vacated\"}}]";

    public static final String SUBMISSION_REASON_CLAIMANT_AMEND =
            createApplications("Amend my claim", "");
    public static final String SUBMISSION_REASON_CLAIMANT_PERSONALDETAILS =
            createApplications("Change my personal details", "");

    public static final String SUBMISSION_REASON_RESPONDENT_AMEND =
            createApplications("Amend response", "");
    public static final String SUBMISSION_REASON_RESPONDENT_PERSONALDETAILS =
            createApplications("Change personal details", "");

    public static final String RESPONDENT_RESPONDING_TO_CLAIMANT_AMEND =
            createApplications("Amend my claim", "Respondent");
    public static final String RESPONDENT_RESPONDING_TO_CLAIMANT_PERSONALDETAILS =
            createApplications("Change my personal details", "Respondent");

    public static final String CLAIMANT_RESPONDING_TO_RESPONDENT_AMEND =
            createApplications("Amend response", "Claimant");
    public static final String CLAIMANT_RESPONDING_TO_RESPONDENT_PERSONALDETAILS =
            createApplications("Change personal details", "Claimant");
    public static final String CLAIMANT_WITHDRAW_ALL_OR_PART_OF_CASE =
            createApplications("Withdraw all/part of claim", "Claimant");

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
                    mapExpectedOutput(
                        "Et1Vetting",
                        "Et1 Vetting",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                mapAdditionalData(REFERRAL_ADMIN),
                List.of(
                    mapExpectedOutput(
                        "ReviewReferralAdmin",
                        "Review Referral #2 - Referral Subject 2",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                mapAdditionalData(REFERRAL_ADMIN_HEARING),
                List.of(
                    mapExpectedOutput(
                        "ReviewReferralAdmin",
                        "Review Referral #2 - Hearings",
                        "Vetting"
                    ),
                    mapExpectedOutput(
                        "IssuePostHearingDirection",
                        "Issue Post Hearing Direction",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                mapAdditionalData(REFERRAL_ADMIN_JUDGMENT),
                List.of(
                    mapExpectedOutput(
                        "ReviewReferralAdmin",
                        "Review Referral #2 - Judgment",
                        "Vetting"
                    ),
                    mapExpectedOutput(
                        "IssueJudgment",
                        "Issue Judgment",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                mapAdditionalData(REFERRAL_JUDGE),
                List.of(
                    mapExpectedOutput(
                        "ReviewReferralJudiciary",
                        "Review Referral #2 - ET1",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                mapAdditionalData(REFERRAL_JUDGE_RULE21),
                List.of(
                    mapExpectedOutput(
                        "DraftAndSignJudgment",
                        "Draft And Sign Judgment",
                        "Judgment"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                null,
                mapAdditionalData(REFERRAL_LEGALOFFICER),
                List.of(
                    mapExpectedOutput(
                        "ReviewReferralLegalOps",
                        "Review Referral #2 - Referral Subject 2",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "replyToReferral",
                null,
                mapAdditionalData(REFERRAL_REPLY_ADMIN),
                List.of(
                    mapExpectedOutput(
                        "ReviewReferralResponseAdmin",
                        "Review Referral #1 - Referral Subject 1 Response",
                        "Processing"
                    )
                )
            ),
            Arguments.of(
                "replyToReferral",
                null,
                mapAdditionalData(REFERRAL_REPLY_JUDGE),
                List.of(
                    mapExpectedOutput(
                        "ReviewReferralResponseJudiciary",
                        "Review Referral #1 - Referral Subject 1 Response",
                        "Processing"
                    )
                )
            ),
            Arguments.of(
                "replyToReferral",
                null,
                mapAdditionalData(REFERRAL_REPLY_LEGALOFFICER),
                List.of(
                    mapExpectedOutput(
                        "ReviewReferralResponseLegalOps",
                        "Review Referral #1 - Referral Subject 1 Response",
                        "Processing"
                    )
                )
            ),
            Arguments.of(
                "preAcceptanceCase",
                "Accepted",
                null,
                List.of(
                    mapExpectedOutput(
                        "ListServeClaim",
                        "List/ Serve Claim",
                        "Vetting"
                    ),
                    mapExpectedOutput(
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
                    mapExpectedOutput(
                        "SendEt1Notification",
                        "Send ET1 Notification",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "WA_REVIEW_RULE21_REFERRAL",
                "Accepted",
                mapAdditionalData(BROUGHT_FORWARD),
                List.of(
                    mapExpectedOutput(
                        "ReviewRule21Referral",
                        "Review Rule 21 Referral",
                        "ReviewRule21Referral"
                    )
                )
            ),
            Arguments.of(
                "submitEt3",
                "Accepted",
                null,
                List.of(
                    mapExpectedOutput(
                        "ET3Processing",
                        "ET3 Processing",
                        "Processing"
                    )
                )
            ),
            Arguments.of(
                "amendRespondentDetails",
                null,
                mapAdditionalData(ET3_FORM_RECEIVED),
                List.of(
                    mapExpectedOutput(
                        "ET3Processing",
                        "ET3 Processing",
                        "Processing"
                    )
                )
            ),
            Arguments.of(
                "amendRespondentDetails",
                null,
                mapAdditionalData(ET3_FORM_NOT_RECEIVED),
                List.of()
            ),
            Arguments.of(
                "et3Vetting",
                "Accepted",
                null,
                List.of(
                    mapExpectedOutput(
                        "CompleteInitialConsideration",
                        "Complete Initial Consideration",
                        "Processing"
                    ),
                    mapExpectedOutput(
                        "SendEt3Notification",
                        "Send ET3 Notification",
                        "Processing"
                    )
                )
            ),
            Arguments.of(
                "initialConsideration",
                "Accepted",
                mapAdditionalData(STRIKE_OUT_CLAIM),
                List.of(
                    mapExpectedOutput(
                        "IssueInitialConsiderationDirections",
                        "Issue Initial Consideration Directions",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "initialConsideration",
                "Accepted",
                mapAdditionalData(LISTAHEARING_PROCEED_LISTED),
                List.of(
                    mapExpectedOutput(
                        "ListAHearing",
                        "List A Hearing",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "initialConsideration",
                "Accepted",
                mapAdditionalData(LISTAHEARING_PROCEED_NOTLISTED_PRELIM),
                List.of(
                    mapExpectedOutput(
                        "ListAHearing",
                        "List A Hearing",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "initialConsideration",
                "Accepted",
                mapAdditionalData(LISTAHEARING_PROCEED_NOTLISTED_FINAL),
                List.of(
                    mapExpectedOutput(
                        "ListAHearing",
                        "List A Hearing",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "initialConsideration",
                "Accepted",
                mapAdditionalData(LISTAHEARING_PROCEED_NOTLISTED_NONE),
                List.of()
            ),
            Arguments.of(
                "initialConsideration",
                "Accepted",
                mapAdditionalData(LISTAHEARING_PROCEED_NOTLISTED_FINAL_WITH_STRIKE_OUT_CLAIM),
                List.of(
                    mapExpectedOutput(
                        "ListAHearing",
                        "List A Hearing",
                        "Hearing"
                    ),
                    mapExpectedOutput(
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
                    mapExpectedOutput(
                        "IssueInitialConsiderationDirections",
                        "Issue Initial Consideration Directions",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "updateHearing",
                "Accepted",
                mapAdditionalData(HEARING_DETAIL_COLLECTION_HEARD),
                List.of(
                    mapExpectedOutput(
                        "DraftAndSignJudgment",
                        "Draft And Sign Judgment",
                        "Judgment"
                    )
                )
            ),
            Arguments.of(
                "updateHearing",
                "Accepted",
                mapAdditionalData(HEARING_DETAIL_COLLECTION_VACATED),
                List.of()
            ),
            Arguments.of(
                "SUBMIT_CLAIMANT_TSE",
                "Accepted",
                mapAdditionalData(SUBMISSION_REASON_CLAIMANT_AMEND),
                List.of(
                    mapExpectedOutput(
                        "ContactTribunalWithAnApplication",
                        "Contact Tribunal With An Application",
                        "Application"
                    )
                )
            ),
            Arguments.of(
                "CLAIMANT_TSE_RESPOND",
                "Accepted",
                mapAdditionalData(CLAIMANT_RESPONDING_TO_RESPONDENT_AMEND),
                List.of(
                    mapExpectedOutput(
                        "ContactTribunalWithAnApplication",
                        "Contact Tribunal With An Application",
                        "Application"
                    )
                )
            ),
            Arguments.of(
                "respondentTSE",
                "Accepted",
                mapAdditionalData(SUBMISSION_REASON_RESPONDENT_AMEND),
                List.of(
                    mapExpectedOutput(
                        "ContactTribunalWithAnApplication",
                        "Contact Tribunal With An Application",
                        "Application"
                    )
                )
            ),
            Arguments.of(
                "tseRespond",
                "Accepted",
                mapAdditionalData(RESPONDENT_RESPONDING_TO_CLAIMANT_AMEND),
                List.of(
                    mapExpectedOutput(
                        "ContactTribunalWithAnApplication",
                        "Contact Tribunal With An Application",
                        "Application"
                    )
                )
            ),
            Arguments.of(
                "SUBMIT_CLAIMANT_TSE",
                null,
                mapAdditionalData(SUBMISSION_REASON_CLAIMANT_PERSONALDETAILS),
                List.of(
                    mapExpectedOutput(
                        "AmendClaimantDetails",
                        "Amend Party Details",
                        "Amendments"
                    )
                )
            ),
            Arguments.of(
                "CLAIMANT_TSE_RESPOND",
                null,
                mapAdditionalData(CLAIMANT_RESPONDING_TO_RESPONDENT_PERSONALDETAILS),
                List.of(
                    mapExpectedOutput(
                        "AmendRespondentDetails",
                        "Amend Party Details",
                        "Amendments"
                    )
                )
            ),
            Arguments.of(
                "respondentTSE",
                null,
                mapAdditionalData(SUBMISSION_REASON_RESPONDENT_PERSONALDETAILS),
                List.of(
                    mapExpectedOutput(
                        "AmendRespondentDetails",
                        "Amend Party Details",
                        "Amendments"
                    )
                )
            ),
            Arguments.of(
                "tseRespond",
                null,
                mapAdditionalData(RESPONDENT_RESPONDING_TO_CLAIMANT_PERSONALDETAILS),
                List.of(
                    mapExpectedOutput(
                        "AmendClaimantDetails",
                        "Amend Party Details",
                        "Amendments"
                    )
                )
            ),
            Arguments.of(
                "SUBMIT_CLAIMANT_TSE",
                "Accepted",
                mapAdditionalData(CLAIMANT_WITHDRAW_ALL_OR_PART_OF_CASE),
                List.of(
                    mapExpectedOutput(
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
        assertThat(logic.getRules().size(), is(30));
    }

    private static Map<String, Object> mapExpectedOutput(String taskId, String name, String processCategories) {
        return Map.of(
            "taskId", taskId,
            "name", name,
            "processCategories", processCategories
        );
    }

    private static Map<String, Object> mapExpectedOutput(String taskId,
                                                         String name,
                                                         String processCategories,
                                                         int delayDuration) {
        return Map.of(
            "name", name,
            "processCategories", processCategories,
            "delayDuration", delayDuration,
            "taskId", taskId
        );
    }

    private static String createApplications(String applicationType, String respondFrom) {
        String respondCollection = "";
        if (!respondFrom.isEmpty()) {
            respondCollection = String.format(RESPOND_COLLECTION, applicationType, respondFrom);
        }

        return String.format(APPLICATION_COLLECTION, applicationType, respondCollection);
    }

    private static String createReferrals(
        String referralSubject1,
        String referralSubject2,
        String referralReferCaseTo,
        String referralUrgency,
        String referralDirectionTo,
        String referralReplyUrgency) {

        String replyCollection1 = "";
        String replyCollection2 = "";
        if (!referralDirectionTo.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            String reply1 = String.format(REFERRALREPLY,
                                          "1",
                                          referralSubject1,
                                          referralDirectionTo,
                                          referralReplyUrgency,
                                          now.plusHours(1).format(OLD_DATE_TIME_PATTERN));
            String reply2 = String.format(REFERRALREPLY,
                                          "2",
                                          referralSubject2,
                                          referralDirectionTo,
                                          referralReplyUrgency,
                                          now.plusHours(2).format(OLD_DATE_TIME_PATTERN));
            String reply3 = String.format(REFERRALREPLY,
                                          "1",
                                          referralSubject1,
                                          referralDirectionTo,
                                          referralReplyUrgency,
                                          now.plusHours(3).format(OLD_DATE_TIME_PATTERN));

            replyCollection1 = String.format(REFERRALREPLY_COLLECTION, reply1 + "," + reply3);
            replyCollection2 = String.format(REFERRALREPLY_COLLECTION, reply2);
        }

        String referralCollection =
            String.format(REFERRAL,"1",referralSubject1,referralReferCaseTo,referralUrgency,replyCollection1)
            + ","
            + String.format(REFERRAL,"2",referralSubject2,referralReferCaseTo,referralUrgency,replyCollection2);

        return String.format(REFERRAL_COLLECTION, referralCollection);
    }

    private static Map<String, Object> mapAdditionalData(String additionalDataContent) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
            };
            String addedDataProperty = "{\"Data\":{" + additionalDataContent + "}}";
            return Map.of("additionalData", mapper.readValue(addedDataProperty, typeRef));
        } catch (IOException exp) {
            return null;
        }
    }
}
