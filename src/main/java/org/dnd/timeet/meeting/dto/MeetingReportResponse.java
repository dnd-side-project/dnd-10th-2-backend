package org.dnd.timeet.meeting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingReportResponse {

    private MeetingReportInfoResponse report;

    public MeetingReportResponse(MeetingReportInfoResponse report) {
        this.report = report;
    }
}
