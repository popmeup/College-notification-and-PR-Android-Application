package com.telecom.project4t.testactivity;

public class UserProfile {
    public String userId;
    public String userName;
    public String userSurname;
    public String userEmail;
    public String userStatus;
    public String userOrganization;
    public String userOrganizationEmailAddress;
    public String userOrganizationPhoneNumber;
    public String userParentOrganization;
    public String userImageURL;

    public UserProfile() {

    }

    public UserProfile(String userId, String userName, String userSurname, String userEmail, String userStatus,
                       String userOrganization, String userOrganizationEmailAddress, String userOrganizationPhoneNumber,
                       String userParentOrganization, String userImageURL) {
        this.userId = userId;
        this.userName = userName;
        this.userSurname = userSurname;
        this.userEmail = userEmail;
        this.userStatus = userStatus;
        this.userOrganization = userOrganization;
        this.userOrganizationEmailAddress = userOrganizationEmailAddress;
        this.userOrganizationPhoneNumber = userOrganizationPhoneNumber;
        this.userParentOrganization = userParentOrganization;
        this.userImageURL = userImageURL;
    }

    public String getuserId() {
        return userId;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public String getuserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public String getuserSurname() {
        return userSurname;
    }

    public void setuserSurname(String userSurname)
    {
        this.userSurname = userSurname;
    }

    public String getuserEmail() {
        return userEmail;
    }

    public void setuserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getuserStatus() {
        return userStatus;
    }

    public void setuserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getuserOrganization() {
        return userOrganization;
    }

    public void setuserOrganization(String userOrganization) {
        this.userOrganization = userOrganization;
    }

    public String getuserOrganizationEmailAddress() {
        return userOrganizationEmailAddress;
    }

    public void setuserOrganizationEmailAddress(String userOrganizationEmailAddress) {
        this.userOrganizationEmailAddress = userOrganizationEmailAddress;
    }

    public String getuserOrganizationPhoneNumber() {
        return userOrganizationPhoneNumber;
    }

    public void setuserOrganizationPhoneNumber(String userOrganizationPhoneNumber) {
        this.userOrganizationPhoneNumber = userOrganizationPhoneNumber;
    }

    public String getuserParentOrganization() {
        return userParentOrganization;
    }

    public void setuserParentOrganization(String userParentOrganization) {
        this.userParentOrganization = userParentOrganization;
    }

    public String getuserImageURL() {
        return userImageURL;
    }

    public void setuserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }

}
