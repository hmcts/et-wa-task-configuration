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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class EmploymentTaskInitiationTest extends DmnDecisionTableBaseUnitTest {

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = DmnDecisionTable.WA_TASK_INITIATION_ET_EW;
    }

    public static Stream<Arguments> scenarioProvider() {
        return Stream.of(
            Arguments.of(
                "INITIATE_CASE_DRAFT",
                "AWAITING_SUBMISSION_TO_HMCTS",
                null,
                Map.of(
                    "taskId", "draftCaseCreated",
                    "name", "Draft Case Created",
                    "workingDaysAllowed", 5
                )
            ),
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
                                      + "      \"referCaseTo\":\"" + "Admin" + "\",\n"
                                      + "      \"referralSubject\":\"" + "(Referral Subject)" + "\",\n"
                                      + "      \"isUrgent\":\"" + "Yes" + "\"\n"
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
                                      + "      \"referCaseTo\":\"" + "Judge" + "\",\n"
                                      + "      \"referralSubject\":\"" + "(Referral Subject)" + "\",\n"
                                      + "      \"isUrgent\":\"" + "Yes" + "\"\n"
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
                                      + "      \"referCaseTo\":\"" + "Legal officer" + "\",\n"
                                      + "      \"referralSubject\":\"" + "(Referral Subject)" + "\",\n"
                                      + "      \"isUrgent\":\"" + "Yes" + "\"\n"
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
                "et3Response",
                "Accepted",
                null,
                Map.of(
                    "taskId", "ET3Processing",
                    "name", "ET3 Processing",
                    "workingDaysAllowed", 3,
                    "processCategories", "processing"
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
                "preAcceptanceCase",
                "Accepted",
                null,
                Map.of(
                    "taskId", "ListServeClaim",
                    "name", "List/ Serve Claim",
                    "workingDaysAllowed", 1,
                    "processCategories", "Vetting"
                )
            ),
            Arguments.of(
                "replyToReferral",
                "Submitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"referCaseTo\":\"" + "Admin" + "\",\n"
                                      + "      \"referralSubject\":\"" + "(Referral Subject)" + "\",\n"
                                      + "      \"isUrgent\":\"" + "Yes" + "\"\n"
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
                "replyToReferral",
                "Submitted",
                mapAdditionalData("{\n"
                                      + "   \"Data\":{\n"
                                      + "      \"referCaseTo\":\"" + "Judge" + "\",\n"
                                      + "      \"referralSubject\":\"" + "(Referral Subject)" + "\",\n"
                                      + "      \"isUrgent\":\"" + "Yes" + "\"\n"
                                      + "   }"
                                      + "}"),
                Map.of(
                    "taskId", "ReviewReferralResponseJudiciary",
                    "name", "Review Referral Response - (Referral Subject)",
                    "workingDaysAllowed", 1,
                    "processCategories", "processing"
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

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {
        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getRules().size(), is(10));
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
