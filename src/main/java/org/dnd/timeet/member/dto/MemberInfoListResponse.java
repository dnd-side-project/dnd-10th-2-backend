package org.dnd.timeet.member.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInfoListResponse {


    private List<MemberInfoResponse> members;


    public MemberInfoListResponse(List<MemberInfoResponse> members) {
        this.members = members;
    }
}
