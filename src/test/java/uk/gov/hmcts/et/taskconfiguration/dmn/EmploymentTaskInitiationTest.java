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

    public static final String ADMIN = "Admin";
    public static final String JUDGE = "Judge";
    public static final String LEGALOFFICER = "Legal officer";
    public static final String YES = "Yes";
    public static final String REFERRALSUBJECT = "(Referral Subject)";
    public static final String REFERRALRULE21 = "Rule 21";
    public static final String REFERRALHEARING = "Hearings";
    public static final String JUDGMENT = "Judgment";

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
                    + "      \"referCaseTo\":\"" + ADMIN + "\",\n"
                    + "      \"referralSubject\":\"" + REFERRALSUBJECT + "\",\n"
                    + "      \"isUrgent\":\"" + YES + "\"\n"
                    + "   }"
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
                    + "      \"referCaseTo\":\"" + JUDGE + "\",\n"
                    + "      \"referralSubject\":\"" + REFERRALSUBJECT + "\",\n"
                    + "      \"isUrgent\":\"" + YES + "\"\n"
                    + "   }"
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
                    + "      \"referCaseTo\":\"" + LEGALOFFICER + "\",\n"
                    + "      \"referralSubject\":\"" + REFERRALSUBJECT + "\",\n"
                    + "      \"isUrgent\":\"" + YES + "\"\n"
                    + "   }"
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
                    + "      \"referCaseTo\":\"" + ADMIN + "\",\n"
                    + "      \"referralSubject\":\"" + REFERRALSUBJECT + "\",\n"
                    + "      \"isUrgent\":\"" + YES + "\"\n"
                    + "   }"
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
                    + "      \"referCaseTo\":\"" + JUDGE + "\",\n"
                    + "      \"referralSubject\":\"" + REFERRALRULE21 + "\",\n"
                    + "      \"isUrgent\":\"" + YES + "\"\n"
                    + "   }"
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
                    + "      \"referCaseTo\":\"" + ADMIN + "\",\n"
                    + "      \"referralSubject\":\"" + REFERRALHEARING + "\"\n"
                    + "   }"
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
                    + "      \"referCaseTo\":\"" + JUDGE + "\",\n"
                    + "      \"referralSubject\":\"" + REFERRALRULE21 + "\",\n"
                    + "      \"isUrgent\":\"" + YES + "\"\n"
                    + "   }"
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
                    + "      \"referCaseTo\":\"" + LEGALOFFICER + "\",\n"
                    + "      \"referralSubject\":\"" + REFERRALSUBJECT + "\",\n"
                    + "      \"isUrgent\":\"" + YES + "\"\n"
                    + "   }"
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
                    + "      \"referCaseTo\":\"" + ADMIN + "\",\n"
                    + "      \"referralSubject\":\"" + JUDGMENT + "\"\n"
                    + "   }"
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
                    + "    \"et3Rule26\":\"" + YES + "\"\n"
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
                null,
                List.of(
                    mapExpectedOutput(
                        "ContactTribunalWithanApplication",
                        "Contact Tribunal With An Application",
                        "Application"
                    ),
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
                null,
                List.of(
                    mapExpectedOutput(
                        "ContactTribunalWithanApplication",
                        "Contact Tribunal With An Application",
                        "Application"
                    ),
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
                null,
                List.of(
                    mapExpectedOutput(
                        "ContactTribunalWithanApplication",
                        "Contact Tribunal With An Application",
                        "Application"
                    ),
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
                null,
                List.of(
                    mapExpectedOutput(
                        "ContactTribunalWithanApplication",
                        "Contact Tribunal With An Application",
                        "Application"
                    ),
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
                    + "   \"submissionReason\":\"" + "-" + "\"\n"
                    + "   }"
                    + "}"),
                List.of(
                    mapExpectedOutput(
                        "ContactTribunalWithanApplication",
                        "Contact Tribunal With An Application",
                        "Application"
                    ),
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
                    + "   \"submissionReason\":\"" + "-" + "\"\n"
                    + "   }"
                    + "}"),
                List.of(
                    mapExpectedOutput(
                        "ContactTribunalWithanApplication",
                        "Contact Tribunal With An Application",
                        "Application"
                    ),
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
                    + "   \"submissionReason\":\"" + "-" + "\"\n"
                    + "   }"
                    + "}"),
                List.of(
                    mapExpectedOutput(
                        "ContactTribunalWithanApplication",
                        "Contact Tribunal With An Application",
                        "Application"
                    ),
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
                    + "   \"submissionReason\":\"" + "-" + "\"\n"
                    + "   }"
                    + "}"),
                List.of(
                    mapExpectedOutput(
                        "ContactTribunalWithanApplication",
                        "Contact Tribunal With An Application",
                        "Application"
                    ),
                    mapExpectedOutput(
                        "AmendPartyDetails",
                        "Amend Party Details",
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
        assertThat(logic.getRules().size(), is(23));
    }

    private static Map<String, String> mapExpectedOutput(String taskId, String name, String processCategories) {
        return Map.of(
            "taskId", taskId,
            "name", name,
            "processCategories", processCategories
        );
    }

    private static Map<String, Object> mapAdditionalData(String additionalData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            TypeReference<HashMap<String, Object>> typeRef = new TypeReference<>() {};
            return Map.of("additionalData", mapper.readValue(additionalData, typeRef));
        } catch (IOException exp) {
            return null;
        }
    }
}
