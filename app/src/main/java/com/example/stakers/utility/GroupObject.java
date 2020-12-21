package com.example.stakers.utility;

import java.io.Serializable;
import java.util.List;

public class GroupObject implements Serializable {
    private String timestamp;
    private String groupUid, groupType, groupPackage, groupName, groupDetail, pic_cover, pic_img1, pic_img2, pic_img3;
    private float groupPrice, groupSize;
    private List<StakerMember> stakerMember;

    public GroupObject() {

    }

    public GroupObject(String groupUid, String timestamp, String groupType, String groupPackage, float groupSize, float groupPrice, String groupName, String groupDetail, String pic_cover,
                       String pic_img1, String pic_img2, String pic_img3, List<StakerMember> stakerMember) {
        this.groupUid = groupUid;
        this.timestamp = timestamp;
        this.groupType = groupType;
        this.groupPackage = groupPackage;
        this.groupSize = groupSize;
        this.groupPrice = groupPrice;
        this.groupName = groupName;
        this.groupDetail = groupDetail;
        this.pic_cover = pic_cover;
        this.pic_img1 = pic_img1;
        this.pic_img2 = pic_img2;
        this.pic_img3 = pic_img3;
        this.stakerMember = stakerMember;
    }

    public List<StakerMember> getStakerMember() {
        return stakerMember;
    }

    public void setStakerMember(List<StakerMember> stakerMember) {
        this.stakerMember = stakerMember;
    }

    public String getGroupUid() {
        return groupUid;
    }

    public void setGroupUid(String groupUid) {
        this.groupUid = groupUid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupPackage() {
        return groupPackage;
    }

    public void setGroupPackage(String groupPackage) {
        this.groupPackage = groupPackage;
    }

    public float getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(float groupSize) {
        this.groupSize = groupSize;
    }

    public float getGroupPrice() {
        return groupPrice;
    }

    public void setGroupPrice(float groupPrice) {
        this.groupPrice = groupPrice;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDetail() {
        return groupDetail;
    }

    public void setGroupDetail(String groupDetail) {
        this.groupDetail = groupDetail;
    }

    public String getPic_cover() {
        return pic_cover;
    }

    public void setPic_cover(String pic_cover) {
        this.pic_cover = pic_cover;
    }

    public String getPic_img1() {
        return pic_img1;
    }

    public void setPic_img1(String pic_img1) {
        this.pic_img1 = pic_img1;
    }

    public String getPic_img2() {
        return pic_img2;
    }

    public void setPic_img2(String pic_img2) {
        this.pic_img2 = pic_img2;
    }

    public String getPic_img3() {
        return pic_img3;
    }

    public void setPic_img3(String pic_img3) {
        this.pic_img3 = pic_img3;
    }
}
