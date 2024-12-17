package uk.gov.hmcts.et.taskconfiguration.utility;

public final class ConfigurationUtility {

    private ConfigurationUtility() {
    }

    public static final String EXTRA_TEST_CALENDAR_ENGWALES = "https://raw.githubusercontent.com/hmcts/"
        + "et-wa-task-configuration/master/src/main/resources/privilege-calendar-engwales.json";
    public static final String EXTRA_TEST_CALENDAR_SCOTLAND = "https://raw.githubusercontent.com/hmcts/"
        + "et-wa-task-configuration/master/src/main/resources/privilege-calendar-scotland.json";

    public static final String ISURGENT_REPLY_YES =
        "{\"referralCollection\":["
            + "{\"value\":{\"referralReplyCollection\":["
            + "{\"value\":{\"isUrgentReply\":\"No\",\"replyDateTime\":\"2023-10-01T12:00:00.00\"}},"
            + "{\"value\":{\"isUrgentReply\":\"Yes\",\"replyDateTime\":\"2023-10-01T14:00:00.00\"}}"
            + "]}},"
            + "{\"value\":{\"referralReplyCollection\":["
            + "{\"value\":{\"isUrgentReply\":\"No\",\"replyDateTime\":\"2023-10-01T13:00:00.00\"}}"
            + "]}}]}";
    public static final String ISURGENT_REPLY_NO =
        "{\"referralCollection\":["
            + "{\"value\":{\"referralReplyCollection\":["
            + "{\"value\":{\"isUrgentReply\":\"Yes\",\"replyDateTime\":\"2023-10-01T12:00:00.00\"}},"
            + "{\"value\":{\"isUrgentReply\":\"No\",\"replyDateTime\":\"2023-10-01T14:00:00.00\"}}"
            + "]}},"
            + "{\"value\":{\"referralReplyCollection\":["
            + "{\"value\":{\"isUrgentReply\":\"Yes\",\"replyDateTime\":\"2023-10-01T13:00:00.00\"}}"
            + "]}}]}";

    public static final String IS_URGENT =
        "{\"referralCollection\": ["
            + "{\"value\": {\"isUrgent\": \"No\",\"referralNumber\": \"1\"}},"
            + "{\"value\": {\"isUrgent\": \"Yes\",\"referralNumber\": \"2\"}}"
            + "]}";
    public static final String NOT_URGENT =
        "{\"referralCollection\": ["
            + "{\"value\": {\"isUrgent\": \"Yes\",\"referralNumber\": \"1\"}},"
            + "{\"value\": {\"isUrgent\": \"No\",\"referralNumber\": \"2\"}}"
            + "]}";

}
