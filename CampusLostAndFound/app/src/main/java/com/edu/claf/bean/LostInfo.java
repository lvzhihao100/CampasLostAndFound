package com.edu.claf.bean;


import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 失物信息实体
 */
public class LostInfo extends BmobObject {

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

    /** 标题*/
    private String lostTitle;

    /**丢失时间*/
    private BmobDate lostDate;

    /**丢失地点*/
    private String lostLocation;

    /**联系电话*/
    private String contactNumber;

    /**失物*/
    private String lostWhat;

    /**失物类型 */
    private String lostType;

    /** 失物描述*/
    private String lostDesc;

    /**丢失地点经度*/
    private Float lostLatitude;
    /**
     * 丢失地点纬度
     */
    private Float lostLongtude;

    /**发布失物时间*/
    private BmobDate publishTime;

    /**发布失物信息电话*/
    private String commitPerson;

    /**发布人所属学校*/
    private String publishUserSchool;

    private String shareNumber;

    private String commentsNumber;

    private String lookPersons;

    /**丢失物品图片*/
    private String lostImage;

    /**发布人*/
    private User publishUser;

    /**发布人地点*/
    private String personLocation;

    /**发布人学校*/
    private String personSchool;

    public User getPublishUser() {
        return publishUser;
    }


    public void setPublishUser(User publishUser) {
        this.publishUser = publishUser;
    }

    public String getPersonSchool() {
        return personSchool;
    }

    public void setPersonSchool(String personSchool) {
        this.personSchool = personSchool;
    }


    public String getPublishUserSchool() {
        return publishUserSchool;
    }

    public void setPublishUserSchool(String publishUserSchool) {
        this.publishUserSchool = publishUserSchool;
    }

    public String getLostType() {
        return lostType;
    }

    public void setLostType(String lostType) {
        this.lostType = lostType;
    }

    public String getLostWhat() {
        return lostWhat;
    }

    public void setLostWhat(String lostWhat) {
        this.lostWhat = lostWhat;
    }

    public Float getLostLatitude() {
        return lostLatitude;
    }

    public void setLostLatitude(Float lostLatitude) {
        this.lostLatitude = lostLatitude;
    }

    public Float getLostLongtude() {
        return lostLongtude;
    }

    public void setLostLongtude(Float lostLongtude) {
        this.lostLongtude = lostLongtude;
    }

    public String getCommitPerson() {
        return commitPerson;
    }

    public void setCommitPerson(String commitPerson) {
        this.commitPerson = commitPerson;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getShareNumber() {
        return shareNumber;
    }

    public void setShareNumber(String shareNumber) {
        this.shareNumber = shareNumber;
    }

    public String getCommentsNumber() {
        return commentsNumber;
    }

    public void setCommentsNumber(String commentsNumber) {
        this.commentsNumber = commentsNumber;
    }

    public String getLookPersons() {
        return lookPersons;
    }

    public void setLookPersons(String lookPersons) {
        this.lookPersons = lookPersons;
    }

    public BmobDate getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(BmobDate publishTime) {
        this.publishTime = publishTime;
    }

    public String getLostImage() {
        return lostImage;
    }

    public void setLostImage(String lostImage) {
        this.lostImage = lostImage;
    }


    public String getPersonLocation() {
        return personLocation;
    }

    public void setPersonLocation(String personLocation) {
        this.personLocation = personLocation;
    }

    public BmobDate getLostDate() {
        return lostDate;
    }

    public void setLostDate(BmobDate lostDate) {
        this.lostDate = lostDate;
    }

    public String getLostTitle() {
        return lostTitle;
    }

    public void setLostTitle(String lostTitle) {
        this.lostTitle = lostTitle;
    }

    public String getLostLocation() {
        return lostLocation;
    }

    public void setLostLocation(String lostLocation) {
        this.lostLocation = lostLocation;
    }

    public String getLostDesc() {
        return lostDesc;
    }

    public void setLostDesc(String lostDesc) {
        this.lostDesc = lostDesc;
    }
}
