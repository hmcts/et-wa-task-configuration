package uk.gov.hmcts.et.taskconfiguration.utility;

import java.io.Serializable;
import java.util.Map;

public final class PermissionsUtility {

    private PermissionsUtility() {
    }

    public static final Map<String, Serializable> taskSupervisor = Map.of(
        "name", "task-supervisor",
        "value", "Read, Manage, Claim, Unclaim, Assign, Unassign, Complete, Cancel",
        "autoAssignable", false
    );

    public static final Map<String, Serializable> approverJudiciary = Map.of(
        "name", "specific-access-approver-judiciary",
        "roleCategory", "JUDICIAL",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn, Assign, Unassign",
        "autoAssignable", false
    );
    public static final Map<String, Serializable> leadJudge = Map.of(
        "assignmentPriority", 1,
        "name", "lead-judge",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "JUDICIAL",
        "autoAssignable", true
    );
    public static final Map<String, Serializable> hearingJudge = Map.of(
        "assignmentPriority", 2,
        "name", "hearing-judge",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "JUDICIAL",
        "autoAssignable", true
    );
    public static final Map<String, Serializable> leadershipJudge = Map.of(
        "assignmentPriority", 5,
        "name", "leadership-judge",
        "value", "Read, Execute, Manage, Claim, Assign, Unassign, Complete, Cancel",
        "roleCategory", "JUDICIAL",
        "autoAssignable", false
    );
    public static final Map<String, Serializable> judge = Map.of(
        "assignmentPriority", 3,
        "name", "judge",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "JUDICIAL",
        "autoAssignable", false
    );

    public static final Map<String, Serializable> approverLegalOps = Map.of(
        "name", "specific-access-approver-legal-ops",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn, Assign, Unassign",
        "roleCategory", "LEGAL_OPERATIONS",
        "autoAssignable", false
    );
    public static final Map<String, Serializable> allocatedTribunalCaseworker = Map.of(
        "assignmentPriority", 1,
        "name", "allocated-tribunal-caseworker",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "LEGAL_OPERATIONS",
        "autoAssignable", true
    );
    public static final Map<String, Serializable> seniorTribunalCaseworker = Map.of(
        "assignmentPriority", 4,
        "name", "senior-tribunal-caseworker",
        "value", "Assign, Unassign, Complete, Cancel",
        "roleCategory", "LEGAL_OPERATIONS",
        "autoAssignable", false
    );
    public static final Map<String, Serializable> tribunalCaseworker = Map.of(
        "assignmentPriority", 3,
        "name", "tribunal-caseworker",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "LEGAL_OPERATIONS",
        "autoAssignable", false
    );

    public static final Map<String, Serializable> approverAdmin = Map.of(
        "name", "specific-access-approver-admin",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn, Assign, Unassign",
        "roleCategory", "ADMIN",
        "autoAssignable", false
    );
    public static final Map<String, Serializable> allocatedAdminCaseworker = Map.of(
        "assignmentPriority", 2,
        "name", "allocated-admin-caseworker",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "ADMIN",
        "autoAssignable", true
    );
    public static final Map<String, Serializable> hearingCentreTeamLeader = Map.of(
        "assignmentPriority", 6,
        "name", "hearing-centre-team-leader",
        "value", "Assign, Unassign, Complete, Cancel",
        "roleCategory", "ADMIN",
        "autoAssignable", false
    );
    public static final Map<String, Serializable> hearingCentreAdmin = Map.of(
        "assignmentPriority", 4,
        "name", "hearing-centre-admin",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "ADMIN",
        "autoAssignable", false
    );
    public static final Map<String, Serializable> regionalCentreTeamLeader = Map.of(
        "assignmentPriority", 6,
        "name", "regional-centre-team-leader",
        "value", "Read, Own, Manage, Claim, Unclaim, Assign, Unassign, Complete, Cancel",
        "roleCategory", "ADMIN",
        "autoAssignable", false
    );
    public static final Map<String, Serializable> regionalCentreAdmin = Map.of(
        "assignmentPriority", 4,
        "name", "regional-centre-admin",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "ADMIN",
        "autoAssignable", false
    );

    public static final Map<String, Serializable> approverCTSC = Map.of(
        "name", "specific-access-approver-ctsc",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn, Assign, Unassign",
        "roleCategory", "CTSC",
        "autoAssignable", true
    );
    public static final Map<String, Serializable> allocatedCtscCaseworker = Map.of(
        "assignmentPriority", 1,
        "name", "allocated-ctsc-caseworker",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "CTSC",
        "autoAssignable", true
    );
    public static final Map<String, Serializable> leaderCTSC = Map.of(
        "assignmentPriority", 5,
        "name", "ctsc-team-leader",
        "value", "Assign, Unassign, Complete, Cancel",
        "roleCategory", "CTSC",
        "autoAssignable", false
    );
    public static final Map<String, Serializable> ctsc = Map.of(
        "assignmentPriority", 3,
        "name", "ctsc",
        "value", "Read, Own, Manage, Claim, Unclaim, UnclaimAssign, CompleteOwn, CancelOwn",
        "roleCategory", "CTSC",
        "autoAssignable", false
    );
}
