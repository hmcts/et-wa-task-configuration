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
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class EmploymentTaskInitiationTest extends DmnDecisionTableBaseUnitTest {

    public static final String ADMIN = "Admin";
    public static final String JUDGE = "Judge";
    public static final String LEGALOFFICER = "Legal officer";
    public static final String YES = "Yes";
    public static final String REFERRALSUBJECT = "(Referral Subject)";
    public static final String REFERRALRULE21 = "Rule 21 Referral";
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
                Map.of(
                    "taskId", "Et1Vetting",
                    "name", "Et1 Vetting",
                    "workingDaysAllowed", 5,
                    "processCategories", "Vetting"
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
                Map.of(
                    "taskId", "ReviewReferralAdmin",
                    "name", "Review Referral - (Referral Subject)",
                    "workingDaysAllowed", 1,
                    "processCategories", "Vetting"
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
                Map.of(
                    "taskId", "ReviewReferralJudiciary",
                    "name", "Review Referral - (Referral Subject)",
                    "workingDaysAllowed", 1,
                    "processCategories", "Vetting"
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
                Map.of(
                    "taskId", "ReviewReferralLegalOps",
                    "name", "Review Referral - (Referral Subject)",
                    "workingDaysAllowed", 1,
                    "processCategories", "Vetting"
                )
            ),
            Arguments.of(
                "initialConsideration",
                "Accepted",
                null,
                Map.of(
                    "taskId", "IssueInitialConsiderationDirections",
                    "name", "Issue Initial Consideration Directions",
                    "workingDaysAllowed", 5,
                    "processCategories", "Hearing"
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
                Map.of(
                    "taskId", "ReviewReferralResponseAdmin",
                    "name", "Review Referral Response - (Referral Subject)",
                    "workingDaysAllowed", 1,
                    "processCategories", "processing"
                )
            ),
            Arguments.of(
                "updateHearing",
                "Accepted",
                null,
                Map.of(
                    "taskId", "DraftAndSignJudgment",
                    "name", "Draft And Sign Judgment",
                    "workingDaysAllowed", 28,
                    "processCategories", "Judgment"
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
                Map.of(
                    "taskId", "DraftAndSignJudgment",
                    "name", "Draft And Sign Judgment",
                    "workingDaysAllowed", 28,
                    "processCategories", "Judgment"
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
                Map.of(
                    "taskId", "IssuePostHearingDirection",
                    "name", "Issue Post Hearing Direction",
                    "workingDaysAllowed", 5,
                    "processCategories", "Hearing"
                )
            ),
            Arguments.of(
                "replyToReferral",
                "Submitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"referCaseTo\":\"" + JUDGE + "\",\n"
                                      + "      \"referralSubject\":\"" + REFERRALRULE21 + "\",\n"
                                      + "      \"isUrgent\":\"" + "Yes" + "\"\n"
                                      + "   }"
                                      + "}"),
                Map.of(
                    "taskId", "ReviewReferralResponseJudiciary",
                    "name", "Review Referral Response - Rule 21 Referral",
                    "workingDaysAllowed", 1,
                    "processCategories", "processing"
                )
            ),
            Arguments.of(
                "replyToReferral",
                "Accepted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"referCaseTo\":\"" + LEGALOFFICER + "\",\n"
                                      + "      \"referralSubject\":\"" + REFERRALSUBJECT + "\",\n"
                                      + "      \"isUrgent\":\"" + "Yes" + "\"\n"
                                      + "   }"
                                      + "}"),
                Map.of(
                    "taskId", "ReviewReferralResponseLegalOps",
                    "name", "Review Referral Response - (Referral Subject)",
                    "workingDaysAllowed", 1,
                    "processCategories", "processing"
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
                Map.of(
                    "taskId", "IssueJudgment",
                    "name", "Issue Judgment",
                    "workingDaysAllowed", 5,
                    "processCategories", "Hearing"
                )
            ),
            Arguments.of(
                "SUBMIT_CLAIMANT_TSE",
                "Accepted",
                null,
                Map.of(
                        "taskId", "ContactTribunalWithanApplication",
                        "name", "Contact Tribunal With An Application",
                        "workingDaysAllowed", 2,
                        "processCategories", "Application"
                )
            ),
            Arguments.of(
                "CLAIMANT_TSE_RESPOND",
                "Accepted",
                null,
                Map.of(
                    "taskId", "ContactTribunalWithanApplication",
                    "name", "Contact Tribunal With An Application",
                    "workingDaysAllowed", 2,
                    "processCategories", "Application"
                )
            ),
            Arguments.of(
                "respondentTSE",
                "Accepted",
                null,
                Map.of(
                    "taskId", "ContactTribunalWithanApplication",
                    "name", "Contact Tribunal With An Application",
                    "workingDaysAllowed", 2,
                    "processCategories", "Application"
                )
            ),
            Arguments.of(
                "tseRespond",
                "Accepted",
                null,
                Map.of(
                    "taskId", "ContactTribunalWithanApplication",
                    "name", "Contact Tribunal With An Application",
                    "workingDaysAllowed", 2,
                    "processCategories", "Application"
                )
            )

        );
    }

    @ParameterizedTest
    @MethodSource("scenarioProvider")
    void given_input_should_return_outcome_dmn(String eventId,
                                               String postEventState,
                                               Map<String, Object> map,
                                               Map<String, ? extends Serializable> expectedDmnOutcome) {
        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putAll(map);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList(), is(singletonList(expectedDmnOutcome)));
    }

    public static Stream<Arguments> scenarioProviderAcceptance() {
        return Stream.of(
            Arguments.of(
                "preAcceptanceCase",
                "Accepted",
                null,
                List.of(
                    Map.of(
                        "taskId", "ListServeClaim",
                        "name", "List/ Serve Claim",
                        "workingDaysAllowed", 1,
                        "processCategories", "Vetting"
                    ),
                    Map.of(
                        "taskId", "SendEt1Notification",
                        "name", "Send ET1 Notification",
                        "workingDaysAllowed", 1,
                        "processCategories", "Vetting"
                    )
                )
            ),
            Arguments.of(
                "preAcceptanceCase",
                "Rejected",
                null,
                List.of(
                    Map.of(
                        "taskId", "SendEt1Notification",
                        "name", "Send ET1 Notification",
                        "workingDaysAllowed", 1,
                        "processCategories", "Vetting"
                    )
                )
            )
        );
    }

    @ParameterizedTest(name = "event id: {0} post event state: {1} additional data: {2}")
    @MethodSource("scenarioProviderAcceptance")
    void given_multiple_event_ids_should_evaluate_dmn(String eventId,
                                                      String postEventState,
                                                      Map<String, Object> map,
                                                      List<Map<String, String>> expectation) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putValue("now", LocalDateTime.now().minusMinutes(10)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        inputVariables.putValue("additionalData", map);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    public static Stream<Arguments> scenarioProviderEt3Response() {
        return Stream.of(
            Arguments.of(
                "et3Response",
                "Accepted",
                null,
                List.of(
                    Map.of(
                        "taskId", "ET3Processing",
                        "name", "ET3 Processing",
                        "workingDaysAllowed", 3,
                        "processCategories", "processing"
                    ),
                    Map.of(
                        "taskId", "ReviewRule21Referral",
                        "name", "Review Rule 21 Referral",
                        "workingDaysAllowed", 2,
                        "processCategories", "processing"
                    )
                )
            )
        );
    }

    @ParameterizedTest(name = "event id: {0} post event state: {1} additional data: {2}")
    @MethodSource("scenarioProviderEt3Response")
    void given_multiple_event_ids_et3response_should_evaluate_dmn(String eventId,
                                                                  String postEventState,
                                                                  Map<String, Object> map,
                                                                  List<Map<String, String>> expectation) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putValue("now", LocalDateTime.now().minusMinutes(10)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        inputVariables.putValue("additionalData", map);

        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    public static Stream<Arguments> scenarioProvideret3Vetting() {
        return Stream.of(
            Arguments.of(
                "et3Vetting",
                "Accepted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "    \"et3Rule26\":\"" + "Yes" + "\"\n"
                                      + "   }"
                                      + "}"),
                List.of(
                    Map.of(
                        "taskId", "CompleteInitialConsideration",
                        "name", "Complete Initial Consideration",
                        "workingDaysAllowed", 2,
                        "processCategories", "processing"
                    ),
                    Map.of(
                        "taskId", "SendEt3Notification",
                        "name", "Send ET3 Notification",
                        "workingDaysAllowed", 1,
                        "processCategories", "processing"
                    )
                )
            )
        );
    }

    @ParameterizedTest(name = "event id: {0} post event state: {1} additional data: {2}")
    @MethodSource("scenarioProvideret3Vetting")
    void given_multiple_event_ids_et3vetting_should_evaluate_dmn(String eventId,
                                                      String postEventState,
                                                      Map<String, Object> map,
                                                      List<Map<String, Object>> expectation) {

        VariableMap inputVariables = new VariableMapImpl();
        inputVariables.putValue("eventId", eventId);
        inputVariables.putValue("postEventState", postEventState);
        inputVariables.putValue("now", LocalDateTime.now().minusMinutes(10)
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        inputVariables.putAll(map);
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);

        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getRules().size(), is(19));
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
