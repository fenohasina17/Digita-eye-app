package com.webexert.digitaleye.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDataModel implements Parcelable {

    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("emergency_phone")
    @Expose
    private String state;
    @SerializedName("state")
    @Expose
    private String city;
    @SerializedName("city")
    @Expose
    private String zip;
    @SerializedName("zip")
    @Expose
    private String address;
    @SerializedName("address")
    @Expose
    private String emergencyPhone;
    @SerializedName("emergency_contact")
    @Expose
    private String emergencyContact;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("student_id")
    @Expose
    private String studentId;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("school_name")
    @Expose
    private String schoolName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("parent_id")
    @Expose
    private Integer parentId;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("survey")
    @Expose
    private Integer survey;

    protected UserDataModel(Parcel in) {
        fullName = in.readString();
        email = in.readString();
        phone = in.readString();
        address = in.readString();
        state = in.readString();
        city = in.readString();
        zip = in.readString();
        emergencyPhone = in.readString();
        emergencyContact = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        studentId = in.readString();
        gender = in.readString();
        schoolName = in.readString();
        image = in.readString();
        if (in.readByte() == 0) {
            parentId = null;
        } else {
            parentId = in.readInt();
        }
        updatedAt = in.readString();
        createdAt = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            survey = null;
        } else {
            survey = in.readInt();
        }
    }

    public static final Creator<UserDataModel> CREATOR = new Creator<UserDataModel>() {
        @Override
        public UserDataModel createFromParcel(Parcel in) {
            return new UserDataModel(in);
        }

        @Override
        public UserDataModel[] newArray(int size) {
            return new UserDataModel[size];
        }
    };

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
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

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSurvey() {
        return survey;
    }

    public void setSurvey(Integer survey) {
        this.survey = survey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(state);
        dest.writeString(city);
        dest.writeString(zip);
        dest.writeString(emergencyPhone);
        dest.writeString(emergencyContact);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(studentId);
        dest.writeString(gender);
        dest.writeString(schoolName);
        dest.writeString(image);
        if (parentId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(parentId);
        }
        dest.writeString(updatedAt);
        dest.writeString(createdAt);
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (survey == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(survey);
        }
    }
}
