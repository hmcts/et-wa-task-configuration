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

    public static final String RULE26_YES = "\"et3Rule26\":\"Yes\"";

    public static final String APPLICATION_COLLECTION =
        "\"genericTseApplicationCollection\":[{\"value\": {\"type\": \"%s\"%s}}]";
    public static final String RESPOND_COLLECTION =
        ",\"respondCollection\": [{\"value\": {\"applicationType\": \"%s\",\"from\": \"%s\"}}]";

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
            createApplications("Strike out all/part of response", "Claimant");

    public static final String REFERRAL_COLLECTION = "\"referralCollection\":[%s]";
    public static final String REFERRAL = "{\"value\": "
        + "{\"referralNumber\": \"%s\",\"referralSubject\":\"%s\",\"referCaseTo\":\"%s\",\"isUrgent\":\"%s\"%s}"
        + "}";
    public static final String REFERRALREPLY_COLLECTION = ",\"referralReplyCollection\": [%s]";
    public static final String REFERRALREPLY = "{\"value\":"
            + "{\"referralSubject\":\"%s\",\"directionTo\":\"%s\",\"isUrgentReply\":\"%s\",\"replyDateTime\":\"%s\"}"
            + "}";

    public static final String REFERRAL_ADMIN =
        createReferrals("Referral Subject 1","Referral Subject 2", "Admin", "Yes", "", "");
    public static final String REFERRAL_ADMIN_HEARING =
        createReferrals("Referral Subject 1","Hearings", "Admin", "Yes", "", "");
    public static final String REFERRAL_ADMIN_JUDGMENT =
        createReferrals("Referral Subject 1","Judgment", "Admin", "Yes", "", "");
    public static final String REFERRAL_JUDGE =
        createReferrals("Referral Subject 1","Referral Subject 2", "Judge", "Yes", "", "");
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
                        "Review Referral - Referral Subject 2",
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
                        "Review Referral - Hearings",
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
                        "Review Referral - Judgment",
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
                        "Review Referral - Referral Subject 2",
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
                        "ReviewReferralJudiciary",
                        "Review Referral - Rule 21",
                        "Vetting"
                    ),
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
                        "Review Referral - Referral Subject 2",
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
                        "Review Referral Response - Referral Subject 1",
                        "processing"
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
                        "Review Referral Response - Referral Subject 1",
                        "processing"
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
                        "Review Referral Response - Referral Subject 1",
                        "processing"
                    )
                )
            ),
            Arguments.of(
                "et3Response",
                "Accepted",
                null,
                List.of(
                    mapExpectedOutput(
                        "ET3Processing",
                        "ET3 Processing",
                        "processing"
                    ),
                    mapExpectedOutput(
                        "ReviewRule21Referral",
                        "Review Rule 21 Referral",
                        "processing"
                    )
                )
            ),
            Arguments.of(
                "initialConsideration",
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
                "updateHearing",
                "Accepted",
                null,
                List.of(
                    mapExpectedOutput(
                        "DraftAndSignJudgment",
                        "Draft And Sign Judgment",
                        "Judgment"
                    )
                )
            ),
            Arguments.of(
                "et3Vetting",
                "Accepted",
                mapAdditionalData(RULE26_YES),
                List.of(
                    mapExpectedOutput(
                        "CompleteInitialConsideration",
                        "Complete Initial Consideration",
                        "processing"
                    ),
                    mapExpectedOutput(
                        "SendEt3Notification",
                        "Send ET3 Notification",
                        "processing"
                    )
                )
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
                "Accepted",
                mapAdditionalData(SUBMISSION_REASON_CLAIMANT_PERSONALDETAILS),
                List.of(
                    mapExpectedOutput(
                        "AmendPartyDetails",
                        "Amend Party Details",
                        "Amendments"
                    )
                )
            ),
            Arguments.of(
                "CLAIMANT_TSE_RESPOND",
                "Accepted",
                mapAdditionalData(CLAIMANT_RESPONDING_TO_RESPONDENT_PERSONALDETAILS),
                List.of(
                    mapExpectedOutput(
                        "AmendPartyDetails",
                        "Amend Party Details",
                        "Amendments"
                    )
                )
            ),
            Arguments.of(
                "respondentTSE",
                "Accepted",
                mapAdditionalData(SUBMISSION_REASON_RESPONDENT_PERSONALDETAILS),
                List.of(
                    mapExpectedOutput(
                        "AmendPartyDetails",
                        "Amend Party Details",
                        "Amendments"
                    )
                )
            ),
            Arguments.of(
                "tseRespond",
                "Accepted",
                mapAdditionalData(RESPONDENT_RESPONDING_TO_CLAIMANT_PERSONALDETAILS),
                List.of(
                    mapExpectedOutput(
                        "AmendPartyDetails",
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
                                               List<Map<String, String>> expectation) {
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
        assertThat(logic.getRules().size(), is(27));
    }

    private static Map<String, String> mapExpectedOutput(String taskId, String name, String processCategories) {
        return Map.of(
                "taskId", taskId,
                "name", name,
                "processCategories", processCategories
        );
    }

    private static String createApplications(String applicationType, String respondFrom) {
        String respondCollection = "";
        if (!"".equals(respondFrom)) {
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
        if (!"".equals(referralDirectionTo)) {
            LocalDateTime now = LocalDateTime.now();
            String reply1 = String.format(REFERRALREPLY,
                                          referralSubject1,
                                          referralDirectionTo,
                                          referralReplyUrgency,
                                          now.plusHours(1).format(OLD_DATE_TIME_PATTERN));
            String reply2 = String.format(REFERRALREPLY,
                                          referralSubject2,
                                          referralDirectionTo,
                                          referralReplyUrgency,
                                          now.plusHours(2).format(OLD_DATE_TIME_PATTERN));
            String reply3 = String.format(REFERRALREPLY,
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
