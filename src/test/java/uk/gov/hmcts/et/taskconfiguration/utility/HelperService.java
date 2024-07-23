package uk.gov.hmcts.et.taskconfiguration.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HelperService {

    private HelperService() {
        // Access through static methods
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final DateTimeFormatter OLD_DATE_TIME_PATTERN =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static final String APPLICATION_COLLECTION =
        "\"genericTseApplicationCollection\":[{\"value\": {\"number\": \"1\",\"type\": \"%s\"%s}}]";
    public static final String RESPOND_COLLECTION =
        ",\"respondCollection\": [{\"value\": {\"applicationType\": \"%s\",\"from\": \"%s\"}}]";

    public static final String REFERRAL_COLLECTION = "\"referralCollection\":[%s]";
    public static final String REFERRAL = "{\"value\": "
        + "{\"referralNumber\": \"%s\",\"referralSubject\":\"%s\",\"referCaseTo\":\"%s\",\"isUrgent\":\"%s\"%s}"
        + "}";
    public static final String REFERRALREPLY_COLLECTION = ",\"referralReplyCollection\": [%s]";
    public static final String REFERRALREPLY = "{\"value\":"
        + "{\"referralNumber\": \"%s\",\"referralSubject\":\"%s\",\"directionTo\":\"%s\""
        + ",\"isUrgentReply\":\"%s\",\"replyDateTime\":\"%s\"}"
        + "}";

    public static final String NOTIFICATION_COLLECTION =
        "\"sendNotificationCollection\": [%s]";
    public static final String NOTIFICATION =
        "{\"value\": {\"number\": \"%s\",\"sendNotificationResponsesCount\": \"%s\"%s}}";
    public static final String NOTIFICATION_RESPOND_COLLECTION =
        ",\"respondCollection\": [%s]";
    public static final String NOTIFICATION_RESPOND =
        "{\"value\": {\"from\": \"%s\",\"datetime\": \"%s\",\"isECC\": \"%s\"}}";

    public static Map<String, Object> mapExpectedOutput(String taskId, String name, String processCategories) {
        return Map.of(
            "taskId", taskId,
            "name", name,
            "processCategories", processCategories
        );
    }

    public static String createApplications(String applicationType, String respondFrom) {
        String respondCollection = "";
        if (!respondFrom.isEmpty()) {
            respondCollection = String.format(RESPOND_COLLECTION, applicationType, respondFrom);
        }

        return String.format(APPLICATION_COLLECTION, applicationType, respondCollection);
    }

    public static String createReferrals(
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

    public static String getReferralObjectString(String rawReferralCollection) {
        return "{" + rawReferralCollection + "}";
    }

    public static String createNotifications(List<String> notificationList) {
        return String.format(NOTIFICATION_COLLECTION,
                             String.join(",", notificationList));
    }

    public static String createNotification(
        String notificationNumber,
        String notificationResponseCount,
        String respondCollection) {
        return String.format(NOTIFICATION,
                             notificationNumber,
                             notificationResponseCount,
                             respondCollection);
    }

    public static String createNotificationRespondCollection(
        List<String> notificationFromList,
        List<String> notificationOffsetList,
        List<String> notificationSubjectList) {
        if (CollectionUtils.isEmpty(notificationFromList)) {
            return "";
        }

        List<String> respondCollection = new ArrayList<>();
        for (int i = 0; i < notificationFromList.size(); i++) {
            respondCollection.add(
                createNotificationResponse(
                    notificationFromList.get(i),
                    notificationOffsetList.get(i),
                    notificationSubjectList.get(i)
                )
            );
        }

        return String.format(NOTIFICATION_RESPOND_COLLECTION,
                             String.join(",", respondCollection));
    }

    public static String createNotificationResponse(
        String notificationFrom,
        String notificationOffset,
        String notificationSubject) {

        LocalDateTime now = LocalDateTime.now();
        return String.format(NOTIFICATION_RESPOND,
                             notificationFrom,
                             now.plusHours(Integer.parseInt(notificationOffset)).format(OLD_DATE_TIME_PATTERN),
                             notificationSubject);
    }

    public static Map<String, Object> mapAdditionalData(String additionalDataContent) {
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

    public static Map<String, Object> mapData(String source) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(source, new TypeReference<>() {});
        } catch (IOException exp) {
            return null;
        }
    }

    public static void getExpectedValueWithReconfigure(List<Map<String, Object>> rules, String name, String value,
                                                 Boolean reconfigure) {
        Map<String, Object> rule = new HashMap<>();
        rule.put("name", name);
        rule.put("value", value);
        rule.put("canReconfigure", reconfigure);
        rules.add(rule);
    }
}
