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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class EmploymentTaskInitiationTest extends DmnDecisionTableBaseUnitTest {

    public static final String REFERCASETO_ADMIN = "\"referCaseTo\":\"Admin\"";
    public static final String REFERCASETO_JUDGE = "\"referCaseTo\":\"Judge\"";
    public static final String REFERCASETO_LEGALOFFICER = "\"referCaseTo\":\"Legal officer\"";

    public static final String REFERRAL_SUBJECT = "\"referralSubject\":\"(Referral Subject)\"";
    public static final String REFERRAL_RULE21 = "\"referralSubject\":\"Rule 21\"";
    public static final String REFERRAL_HEARINGS = "\"referralSubject\":\"Hearings\"";
    public static final String REFERRAL_JUDGMENT = "\"referralSubject\":\"Judgment\"";

    public static final String ISURGENT_YES = "\"isUrgent\":\"Yes\"";
    public static final String RULE26_YES = "\"et3Rule26\":\"Yes\"";

    public static final String APPLICATION_COLLECTION =
            "\"genericTseApplicationCollection\":[{\"value\": {\"type\": \"%s\"%s}}]";
    public static final String RESPOND_COLLECTION =
            ",\"respondCollection\": [{\"value\": {\"from\": \"%s\"}}]";

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
    public static final String DIRECTIONTO_ADMIN = "\"directionTo\":\"Admin\"";
    public static final String DIRECTIONTO_JUDGE = "\"directionTo\":\"Judge\"";
    public static final String DIRECTIONTO_LEGALOFFICER = "\"directionTo\":\"Legal officer\"";
    public static final String ISURGENT_REPLY_YES = "\"isUrgentReply\":\"Yes\"";

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
                "Submitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "   \"referralCollection\":[{\n"
                                      + "   \"value\":{\n"
                                      + REFERCASETO_ADMIN + ","
                                      + REFERRAL_SUBJECT + ","
                                      + ISURGENT_YES
                                      + "   }}]}"
                                      + "}"),
                List.of(
                    mapExpectedOutput(
                        "ReviewReferralAdmin",
                        "Review Referral - (Referral Subject)",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                "Submitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "   \"referralCollection\":[{\n"
                                      + "   \"value\":{\n"
                                      + REFERCASETO_JUDGE + ","
                                      + REFERRAL_SUBJECT + ","
                                      + ISURGENT_YES
                                      + "   }}]}"
                                      + "}"),
                List.of(
                    mapExpectedOutput(
                        "ReviewReferralJudiciary",
                        "Review Referral - (Referral Subject)",
                        "Vetting"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                "Submitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "   \"referralCollection\":[{\n"
                                      + "   \"value\":{\n"
                                      + REFERCASETO_LEGALOFFICER + ","
                                      + REFERRAL_SUBJECT + ","
                                      + ISURGENT_YES
                                      + "   }}]}"
                                      + "}"),
                List.of(
                    mapExpectedOutput(
                        "ReviewReferralLegalOps",
                        "Review Referral - (Referral Subject)",
                        "Vetting"
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
                "replyToReferral",
                "Submitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "   \"referralCollection\":[{\n"
                                      + "   \"value\":{\n"
                                      + REFERCASETO_ADMIN + ","
                                      + REFERRAL_SUBJECT + ","
                                      + ISURGENT_YES + ","
                                      + "   \"referralReplyCollection\":[{\n"
                                      + "   \"value\":{\n"
                                      + DIRECTIONTO_ADMIN + ","
                                      + ISURGENT_REPLY_YES
                                      + "     }}]"
                                      + "   }}]}"
                                      + "}"),
                List.of(
                    mapExpectedOutput(
                        "ReviewReferralResponseAdmin",
                        "Review Referral Response - (Referral Subject)",
                        "processing"
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
                "createReferral",
                "Accepted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "   \"referralCollection\":[{\n"
                                      + "   \"value\":{\n"
                                      + REFERCASETO_JUDGE + ","
                                      + REFERRAL_RULE21 + ","
                                      + ISURGENT_YES
                                      + "   }}]}"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "   \"referralCollection\":[{\n"
                                      + "   \"value\":{\n"
                                      + REFERCASETO_ADMIN + ","
                                      + REFERRAL_HEARINGS
                                      + "   }}]}"
                                      + "}"),
                List.of(
                    mapExpectedOutput(
                        "IssuePostHearingDirection",
                        "Issue Post Hearing Direction",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "replyToReferral",
                "Submitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "   \"referralCollection\":[{\n"
                                      + "   \"value\":{\n"
                                      + REFERCASETO_JUDGE + ","
                                      + REFERRAL_RULE21 + ","
                                      + ISURGENT_YES + ","
                                      + "   \"referralReplyCollection\":[{\n"
                                      + "   \"value\":{\n"
                                      + DIRECTIONTO_JUDGE + ","
                                      + ISURGENT_REPLY_YES
                                      + "     }}]"
                                      + "   }}]}"
                                      + "}"),
                List.of(
                    mapExpectedOutput(
                        "ReviewReferralResponseJudiciary",
                        "Review Referral Response - Rule 21",
                        "processing"
                    )
                )
            ),
            Arguments.of(
                "replyToReferral",
                "Accepted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "   \"referralCollection\":[{\n"
                                      + "   \"value\":{\n"
                                      + REFERCASETO_LEGALOFFICER + ","
                                      + REFERRAL_SUBJECT + ","
                                      + ISURGENT_YES + ","
                                      + "   \"referralReplyCollection\":[{\n"
                                      + "   \"value\":{\n"
                                      + DIRECTIONTO_LEGALOFFICER + ","
                                      + ISURGENT_REPLY_YES
                                      + "     }}]"
                                      + "   }}]}"
                                      + "}"),
                List.of(
                    mapExpectedOutput(
                        "ReviewReferralResponseLegalOps",
                        "Review Referral Response - (Referral Subject)",
                        "processing"
                    )
                )
            ),
            Arguments.of(
                "createReferral",
                "Accepted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "   \"referralCollection\":[{\n"
                                      + "   \"value\":{\n"
                                      + REFERCASETO_ADMIN + ","
                                      + REFERRAL_JUDGMENT
                                      + "   }}]}"
                                      + "}"),
                List.of(
                    mapExpectedOutput(
                        "IssueJudgment",
                        "Issue Judgment",
                        "Hearing"
                    )
                )
            ),
            Arguments.of(
                "et3Vetting",
                "Accepted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + RULE26_YES
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + SUBMISSION_REASON_CLAIMANT_AMEND
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + CLAIMANT_RESPONDING_TO_RESPONDENT_AMEND
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + SUBMISSION_REASON_RESPONDENT_AMEND
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + RESPONDENT_RESPONDING_TO_CLAIMANT_AMEND
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + SUBMISSION_REASON_CLAIMANT_PERSONALDETAILS
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + CLAIMANT_RESPONDING_TO_RESPONDENT_PERSONALDETAILS
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + SUBMISSION_REASON_RESPONDENT_PERSONALDETAILS
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + RESPONDENT_RESPONDING_TO_CLAIMANT_PERSONALDETAILS
                                      + "   }"
                                      + "}"),
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
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + CLAIMANT_WITHDRAW_ALL_OR_PART_OF_CASE
                                      + "   }"
                                      + "}"),
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

    private static String createApplications(String appliciationType, String respondFrom) {
        String respondCollection = "";
        if (respondFrom != "") {
            respondCollection = String.format(RESPOND_COLLECTION, respondFrom);
        }

        return String.format(APPLICATION_COLLECTION, appliciationType, respondCollection);
    }

    private static Map<String, Object> mapAdditionalData(String additionalData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {
            };
            return Map.of("additionalData", mapper.readValue(additionalData, typeRef));
        } catch (IOException exp) {
            return null;
        }
    }
}
