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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EmploymentTaskTypeTest extends DmnDecisionTableBaseUnitTest {

    public static final String REFERRALSUBJECT = "(Referral Subject)";

    @BeforeAll
    public static void initialization() {
        CURRENT_DMN_DECISION_TABLE = DmnDecisionTable.WA_TASK_TYPE_ET_EW;
    }

    static Stream<Arguments> scenarioProvider() {

        return Stream.of(
            Arguments.of(
                asList(
                    Map.of(
                        "taskTypeId", "draftCaseCreated",
                        "taskTypeName", "Draft Case Created"
                    ),
                    Map.of(
                        "taskTypeId", "Et1Vetting",
                        "taskTypeName", "Et1 Vetting"
                    ),
                    Map.of(
                        "taskTypeId", "ReviewReferralAdmin",
                        "taskTypeName", "Review Referral - (Referral Subject)"
                    ),
                    Map.of(
                        "taskTypeId", "ReviewReferralJudiciary",
                        "taskTypeName", "Review Referral - (Referral Subject)"
                    ),
                    Map.of(
                        "taskTypeId", "ReviewReferralLegalOps",
                        "taskTypeName", "Review Referral - (Referral Subject)"
                    ),
                    Map.of(
                        "taskTypeId", "ET3Processing",
                        "taskTypeName", "ET3 Processing"
                    ),
                    Map.of(
                        "taskTypeId", "IssueInitialConsiderationDirections",
                        "taskTypeName", "Issue Initial Consideration Directions"
                    ),
                    Map.of(
                        "taskTypeId", "ListServeClaim",
                        "taskTypeName", "List/ Serve Claim"
                    ),
                    Map.of(
                        "taskTypeId", "SendEt1Notification",
                        "taskTypeName", "SendEt1Notification"
                    ),
                    Map.of(
                        "taskTypeId", "ReviewReferralResponseAdmin",
                        "taskTypeName", "Review Referral Response - (Referral Subject)"
                    ),
                    Map.of(
                        "taskTypeId", "DraftAndSignJudgment",
                        "taskTypeName", "Draft And Sign Judgment"
                    ),
                    Map.of(
                        "taskTypeId", "IssuePostHearingDirection",
                        "taskTypeName", "Issue Post Hearing Direction"
                    ),
                    Map.of(
                        "taskTypeId", "ReviewReferralResponseJudiciary",
                        "taskTypeName", "Review Referral Response - (Referral Subject)"
                    ),
                    Map.of(
                        "taskTypeId", "ReviewReferralResponseLegalOps",
                        "taskTypeName", "Review Referral Response - (Referral Subject)"
                    )
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("scenarioProvider")
    void should_evaluate_dmn_and_verify_task_types(List<Map<String, Object>> expectation) {

        VariableMap inputVariables = new VariableMapImpl();
        DmnDecisionTableResult dmnDecisionTableResult = evaluateDmnTable(inputVariables);
        assertThat(dmnDecisionTableResult.getResultList(), is(expectation));
    }

    @Test
    void if_this_test_fails_needs_updating_with_your_changes() {

        //The purpose of this test is to prevent adding new rows without being tested
        DmnDecisionTableImpl logic = (DmnDecisionTableImpl) decision.getDecisionLogic();
        assertThat(logic.getRules().size(), is(14));

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
