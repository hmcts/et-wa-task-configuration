package uk.gov.hmcts.et.taskconfiguration.dmn;

import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.hmcts.et.taskconfiguration.utility.HelperService;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IssueDay7NotificationTaskTest {
    private static final String TASK_TYPE = "IssueDay7Notification";
    private static final String GROUP_CLAIMS = "Group claims";
    private static final String CONSIDERING_CLAIMS = "Considering claims together";
    private static final String OTHER_GROUP_NOTIFICATION = "You're now part of a group claim";

    private DmnEngine engine;

    @BeforeEach
    void setUp() {
        engine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();
    }

    static Stream<Arguments> initiationScenarios() {
        Map<String, Object> matching = notification("1", List.of(GROUP_CLAIMS), CONSIDERING_CLAIMS);
        Map<String, Object> unrelated = notification("2", List.of("Hearing"), CONSIDERING_CLAIMS);
        Map<String, Object> otherGroup = notification("2", List.of(GROUP_CLAIMS), OTHER_GROUP_NOTIFICATION);
        return Stream.of("englandwales", "scotland").flatMap(region -> Stream.of(
            Arguments.of(region, "sendNotification", collection(matching), true),
            Arguments.of(region, "sendNotification", collection(notification(
                "1", List.of("Hearing", GROUP_CLAIMS), CONSIDERING_CLAIMS)), true),
            Arguments.of(region, "sendNotification", collection(notification(
                "10", List.of(GROUP_CLAIMS), CONSIDERING_CLAIMS), unrelated), true),
            Arguments.of(region, "sendNotification", collection(matching, unrelated), false),
            Arguments.of(region, "sendNotification", collection(unrelated, matching), false),
            Arguments.of(region, "sendNotification", collection(matching, otherGroup), false),
            Arguments.of(region, "sendNotification", collection(notification(
                "1", List.of("Hearing"), CONSIDERING_CLAIMS)), false),
            Arguments.of(region, "sendNotification", collection(notification(
                "1", List.of(GROUP_CLAIMS), OTHER_GROUP_NOTIFICATION)), false),
            Arguments.of(region, "sendNotification", collection(notification(
                "1", null, CONSIDERING_CLAIMS)), false),
            Arguments.of(region, "sendNotification", collection(notification(
                "1", List.of(), CONSIDERING_CLAIMS)), false),
            Arguments.of(region, "sendNotification", collection(notification(
                "1", List.of(GROUP_CLAIMS), null)), false),
            Arguments.of(region, "sendNotification", collection(), false),
            Arguments.of(region, "sendNotification", Map.of(), false),
            Arguments.of(region, "sendNotification", null, false),
            Arguments.of(region, "generateCorrespondence", collection(matching), false),
            Arguments.of(region, "UPDATE_NOTIFICATION_STATE", collection(matching), false),
            Arguments.of(region, "sendNotification", Map.of(
                "sendNotificationSubject", List.of(GROUP_CLAIMS),
                "sendNotificationGroupClaims", CONSIDERING_CLAIMS), false)
        ));
    }

    @ParameterizedTest
    @MethodSource("initiationScenarios")
    void createsTaskOnlyForLatestMatchingNotification(String region, String eventId,
                                                     Map<String, Object> caseData,
                                                     boolean expected) throws IOException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("eventId", eventId);
        variables.put("postEventState", "Accepted");
        variables.put("additionalData", caseData == null ? null : Map.of("Data", caseData));

        List<Map<String, Object>> result = evaluate(region, "initiation", variables);

        assertEquals(expected ? List.of(HelperService.mapExpectedOutput(
            TASK_TYPE, "Issue day 7 notification", "Processing"))
            : List.of(), result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"englandwales", "scotland"})
    void missingAdditionalDataDoesNotCreateTask(String region) throws IOException {
        assertEquals(List.of(), evaluate(region, "initiation", Map.of("eventId", "sendNotification")));
        assertEquals(List.of(), evaluate(region, "initiation", Map.of(
            "eventId", "sendNotification", "additionalData", Map.of())));
    }

    static Stream<Arguments> deadlineScenarios() {
        return Stream.of("englandwales", "scotland").flatMap(region -> Stream.of(
            Arguments.of(region, "27 October 2026", LocalDate.of(2026, 10, 27), LocalDate.of(2026, 11, 4)),
            Arguments.of(region, "25 December 2026", LocalDate.of(2026, 12, 25), LocalDate.of(2027, 1, 2)),
            Arguments.of(region, "25 February 2028", LocalDate.of(2028, 2, 25), LocalDate.of(2028, 3, 4))
        ));
    }

    @ParameterizedTest
    @MethodSource("deadlineScenarios")
    void configuresAdminTaskDueEightCalendarDaysAfterNotification(String region, String sentDate,
                                                                 LocalDate expectedOrigin,
                                                                 LocalDate expectedDueDate) throws IOException {
        Map<String, Object> latest = notification("10", List.of(GROUP_CLAIMS), CONSIDERING_CLAIMS);
        latest.put("date", sentDate);
        Map<String, Object> old = notification("2", List.of("Hearing"), null);
        old.put("date", "1 January 2025");
        Map<String, Object> variables = Map.of(
            "caseData", collection(latest, old),
            "taskAttributes", Map.of("taskType", TASK_TYPE)
        );

        Map<String, Map<String, Object>> properties = evaluate(region, "configuration", variables).stream()
            .collect(Collectors.toMap(row -> (String) row.get("name"), row -> row));

        assertProperty(properties, "workType", "routine_work", true);
        assertProperty(properties, "roleCategory", "ADMIN", true);
        assertProperty(properties, "majorPriority", "5000", false);
        assertProperty(properties, "minorPriority", "500", false);
        assertProperty(properties, "priorityDateOriginRef", "dueDate", false);
        String caseType = "englandwales".equals(region) ? "ET_EnglandWales" : "ET_Scotland";
        assertProperty(properties, "description",
            "[Send day 7 notification](/cases/case-details/EMPLOYMENT/" + caseType + "/${[CASE_REFERENCE]}"
                + "/trigger/sendNotification/sendNotification1)", true);
        assertEquals("[Send day 7 notification](/cases/case-details/EMPLOYMENT/" + caseType
            + "/1786551851384057/trigger/sendNotification/sendNotification1)",
            properties.get("description").get("value").toString()
                .replace("${[CASE_REFERENCE]}", "1786551851384057"));
        assertProperty(properties, "dueDateOrigin", expectedOrigin.toString(), false);
        assertProperty(properties, "dueDateIntervalDays", "8", false);
        assertProperty(properties, "dueDateSkipNonWorkingDays", "false", false);
        assertProperty(properties, "dueDateMustBeWorkingDay", "No", false);
        assertEquals(expectedDueDate, LocalDate.parse(properties.get("dueDateOrigin").get("value").toString())
            .plusDays(Long.parseLong(properties.get("dueDateIntervalDays").get("value").toString())));
        assertTrue(properties.get("dueDateTime").get("value").toString().startsWith("16:00"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"englandwales", "scotland"})
    void hasAdminPermissionsButNoRoleBasedAutoAssignment(String region) throws IOException {
        List<Map<String, Object>> permissions = evaluate(region, "permissions", Map.of(
            "taskAttributes", Map.of("taskType", TASK_TYPE)));
        Set<Map<String, Object>> expected = evaluate(region, "permissions", Map.of(
            "taskAttributes", Map.of("taskType", "SendEt3Notification"))).stream()
            .filter(row -> !"allocated-admin-caseworker".equals(row.get("name")))
            .collect(Collectors.toSet());
        assertEquals(expected, Set.copyOf(permissions));
        assertTrue(permissions.stream().noneMatch(row -> Boolean.TRUE.equals(row.get("autoAssignable"))));
        assertTrue(permissions.stream().anyMatch(row ->
            "ADMIN".equals(row.get("roleCategory")) && "hearing-centre-admin".equals(row.get("name"))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"englandwales", "scotland"})
    void doesNotConfigureAssignmentCorrelation(String region) throws IOException {
        List<Map<String, Object>> properties = evaluate(region, "configuration", Map.of(
            "taskAttributes", Map.of("taskType", TASK_TYPE)));
        assertFalse(properties.stream().anyMatch(row ->
            "additionalProperties_day7NotificationNumber".equals(row.get("name"))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"englandwales", "scotland"})
    void sendingNotificationDoesNotAutomaticallyCompleteNewTask(String region) throws IOException {
        assertFalse(evaluate(region, "completion", Map.of("eventId", "sendNotification")).stream()
            .anyMatch(row -> TASK_TYPE.equals(row.get("taskType"))));
    }

    private static Map<String, Object> notification(String number, List<String> subjects, String groupClaims) {
        Map<String, Object> value = new HashMap<>();
        value.put("number", number);
        value.put("sendNotificationSubject", subjects);
        value.put("sendNotificationGroupClaims", groupClaims);
        value.put("date", "27 October 2026");
        return value;
    }

    @SafeVarargs
    private static Map<String, Object> collection(Map<String, Object>... notifications) {
        return Map.of("sendNotificationCollection", Stream.of(notifications)
            .map(value -> Map.of("value", value)).toList());
    }

    private static void assertProperty(Map<String, Map<String, Object>> properties, String name,
                                       String value, boolean canReconfigure) {
        assertEquals(Map.of("name", name, "value", value, "canReconfigure", canReconfigure), properties.get(name));
    }

    private List<Map<String, Object>> evaluate(String region, String kind,
                                                Map<String, Object> variables) throws IOException {
        String key = "wa-task-" + kind + "-employment-et_" + region;
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(key + ".dmn")) {
            assertNotNull(stream);
            return engine.evaluateDecisionTable(engine.parseDecision(key, stream), variables).getResultList();
        }
    }
}
