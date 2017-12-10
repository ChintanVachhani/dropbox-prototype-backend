package com.dropboxPrototypeBackend.entity;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

public class UserAccount implements Serializable {

    @Id
    private String _id;

    private String email;
    private String firstName;
    private String lastName;
    private String work;
    private String education;
    private String address;
    private String country;
    private String city;
    private String zipcode;
    private String interests;

    public UserAccount() {
    }

    public UserAccount(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.work = "";
        this.education = "";
        this.address = "";
        this.country = "";
        this.city = "";
        this.zipcode = "";
        this.interests = "";
    }

    public UserAccount(String email, String firstName, String lastName, String work, String education, String address, String country, String city, String zipcode, String interests) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.work = work;
        this.education = education;
        this.address = address;
        this.country = country;
        this.city = city;
        this.zipcode = zipcode;
        this.interests = interests;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }
}
