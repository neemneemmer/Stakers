package com.example.stakers.utility;

public class MemberApproval {
    private String groupUid, receiverEmail, senderEmail, senderPic, groupName, timestamp, groupPicCover;
    private int typeNotify;

    public MemberApproval() {

    }

    public MemberApproval(String groupUid, String receiverEmail, String senderEmail, String senderPic, String groupName, String timestamp, String groupPicCover, int typeNotify) {
        this.groupUid = groupUid;
        this.receiverEmail = receiverEmail;
        this.senderEmail = senderEmail;
        this.senderPic = senderPic;
        this.groupName = groupName;
        this.timestamp = timestamp;
        this.groupPicCover = groupPicCover;
        this.typeNotify = typeNotify;
    }

    public String getGroupUid() {
        return groupUid;
    }

    public void setGroupUid(String groupUid) {
        this.groupUid = groupUid;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderPic() {
        return senderPic;
    }

    public void setSenderPic(String senderPic) {
        this.senderPic = senderPic;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getTypeNotify() {
        return typeNotify;
    }

    public void setTypeNotify(int typeNotify) {
        this.typeNotify = typeNotify;
    }

    public String getGroupPicCover() {
        return groupPicCover;
    }

    public void setGroupPicCover(String groupPicCover) {
        this.groupPicCover = groupPicCover;
    }
}
