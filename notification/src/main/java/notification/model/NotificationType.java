package notification.model;

import lombok.Getter;

@Getter
public enum NotificationType {

    LEAVE_APPLIED("Leave Applied","Your leave request has been submitted."),
    LEAVE_APPROVED("Leave Approved","Your leave request has been approved."),
    LEAVE_CANCELED("Leave Canceled","Your leave request has been canceled."),
    LEAVE_REJECTED("Leave Rejected","Your leave request has been rejected.");

    private final String title;
    private final String message;

    NotificationType(String title,String message) {
       this.title = title;
       this.message = message;
    }
}
