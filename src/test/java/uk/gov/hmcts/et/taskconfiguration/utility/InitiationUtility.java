package uk.gov.hmcts.et.taskconfiguration.utility;

import java.util.Arrays;
import java.util.List;

import static uk.gov.hmcts.et.taskconfiguration.utility.HelperService.createNotification;
import static uk.gov.hmcts.et.taskconfiguration.utility.HelperService.createNotificationRespondCollection;

public final class InitiationUtility {

    private InitiationUtility() {
    }

    public static final String REFERRAL_REPLY_LEGALOFFICER =
        HelperService.createReferrals("Referral Subject 1","Referral Subject 2", "", "", "Legal officer", "Yes");

    public static final String REFERRAL_ADMIN =
        HelperService.createReferrals("Referral Subject 1", "Referral Subject 2", "Admin", "Yes", "", "");

    public static final String REFERRAL_LEGALOPS =
        HelperService.createReferrals("Referral Subject 1", "Referral Subject 2", "Legal officer", "Yes", "", "");

    public static final String REFERRAL_ADMIN_HEARING =
        HelperService.createReferrals("Referral Subject 1","Hearings", "Admin", "Yes", "", "");
    public static final String REFERRAL_ADMIN_JUDGMENT =
        HelperService.createReferrals("Referral Subject 1","Judgment", "Admin", "Yes", "", "");
    public static final String REFERRAL_JUDGE =
        HelperService.createReferrals("Referral Subject 1","ET1", "Judge", "Yes", "", "");
    public static final String REFERRAL_JUDGE_RULE21 =
        HelperService.createReferrals("Referral Subject 1","Rule 21", "Judge", "Yes", "", "");
    public static final String REFERRAL_LEGALOFFICER =
        HelperService.createReferrals("Referral Subject 1","Referral Subject 2", "Legal officer", "Yes", "", "");

    public static final String REFERRAL_REPLY_ADMIN =
        HelperService.createReferrals("Referral Subject 1","Referral Subject 2", "", "", "Admin", "Yes");
    public static final String REFERRAL_REPLY_JUDGE =
        HelperService.createReferrals("Referral Subject 1","Referral Subject 2", "", "", "Judge", "Yes");

    public static final String DRAFT_AND_SIGN_JUDGEMENT = "\"draftAndSignJudgement\":{}";

    public static final String ET3_FORM_NOT_RECEIVED =
        "\"respondentCollection\":[{\"value\":{\"responseReceived\":false,\"responseReceivedCount\":null}}]";
    public static final String ET3_FORM_RECEIVED_ONCE =
        "\"respondentCollection\":[{\"value\":{\"responseReceived\":true,\"responseReceivedCount\":\"1\"}}]";
    public static final String ET3_FORM_RECEIVED_MORE =
        "\"respondentCollection\":[{\"value\":{\"responseReceived\":true,\"responseReceivedCount\":\"2\"}}]";

    public static final String IS_ET3_RESPONSE_TRUE =
        "\"respondentCollection\""
            + ":[{\"value\":{\"et3Vetting\":{\"et3IsThereAnEt3Response\":true,\"et3ContractClaimSection7\":false }}}]";
    public static final String IS_ET3_RESPONSE_FALSE =
        "\"respondentCollection\""
            +  ":[{\"value\":{\"et3Vetting\":{\"et3IsThereAnEt3Response\":false ,\"et3ContractClaimSection7\":true}}}]";

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

    public static final String SUBMISSION_REASON_CLAIMANT_AMEND =
        HelperService.createApplications("Amend my claim", "");
    public static final String SUBMISSION_REASON_CLAIMANT_CONTACT =
        HelperService.createApplications("Contact about something else", "");
    public static final String SUBMISSION_REASON_CLAIMANT_PERSONALDETAILS =
        HelperService.createApplications("Change my personal details", "");

    public static final String SUBMISSION_REASON_RESPONDENT_AMEND =
        HelperService.createApplications("Amend response", "");
    public static final String SUBMISSION_REASON_RESPONDENT_CONTACT =
        HelperService.createApplications("Contact the tribunal", "");
    public static final String SUBMISSION_REASON_RESPONDENT_PERSONALDETAILS =
        HelperService.createApplications("Change personal details", "");

    public static final String RESPONDENT_RESPONDING_TO_CLAIMANT_AMEND =
        HelperService.createApplications("Amend my claim", "Respondent");
    public static final String RESPONDENT_RESPONDING_TO_CLAIMANT_CONTACT =
        HelperService.createApplications("Contact about something else", "Respondent");
    public static final String RESPONDENT_RESPONDING_TO_CLAIMANT_PERSONALDETAILS =
        HelperService.createApplications("Change my personal details", "Respondent");

    public static final String CLAIMANT_RESPONDING_TO_RESPONDENT_AMEND =
        HelperService.createApplications("Amend response", "Claimant");
    public static final String CLAIMANT_RESPONDING_TO_RESPONDENT_CONTACT =
        HelperService.createApplications("Contact the tribunal", "Claimant");
    public static final String CLAIMANT_RESPONDING_TO_RESPONDENT_PERSONALDETAILS =
        HelperService.createApplications("Change personal details", "Claimant");

    public static final String CLAIMANT_WITHDRAW_ALL_OR_PART_OF_CASE =
        HelperService.createApplications("Withdraw all/part of claim", "Claimant");

    public static final String NOTIFICATION_1 =
        createNotification("1", "0", "");
    public static final String NOTIFICATION_2 =
        createNotification(
            "2",
            "1",
            createNotificationRespondCollection(
                List.of("Claimant"),
                List.of("2"),
                List.of("Yes")
            )
        );
    public static final String NOTIFICATION_3 =
        createNotification(
            "3",
            "2",
            createNotificationRespondCollection(
                Arrays.asList("Claimant", "Respondent"),
                Arrays.asList("1", "3"),
                Arrays.asList("Yes", "No")
            )
        );

    public static final String NOTIFICATIONS_LIST =
        HelperService.createNotifications(
            Arrays.asList(
                NOTIFICATION_1,
                NOTIFICATION_2,
                NOTIFICATION_3
            )
        );
}
