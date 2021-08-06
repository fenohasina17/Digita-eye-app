package com.webexert.digitaleye.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateProfileResponseModel {

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Data implements Parcelable {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("is_admin")
        @Expose
        private Integer isAdmin;
        @SerializedName("active")
        @Expose
        private Integer active;
        @SerializedName("first_name")
        @Expose
        private String firstName;
        @SerializedName("last_name")
        @Expose
        private String lastName;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("gender")
        @Expose
        private String gender;
        @SerializedName("is_parent")
        @Expose
        private String isParent;
        @SerializedName("is_employee")
        @Expose
        private String isEmployee;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        protected Data(Parcel in) {
            if (in.readByte() == 0) {
                id = null;
            } else {
                id = in.readInt();
            }
            name = in.readString();
            email = in.readString();
            if (in.readByte() == 0) {
                isAdmin = null;
            } else {
                isAdmin = in.readInt();
            }
            if (in.readByte() == 0) {
                active = null;
            } else {
                active = in.readInt();
            }
            firstName = in.readString();
            lastName = in.readString();
            phone = in.readString();
            image = in.readString();
            gender = in.readString();
            isParent = in.readString();
            isEmployee = in.readString();
            createdAt = in.readString();
            updatedAt = in.readString();
        }

        public static final Creator<Data> CREATOR = new Creator<Data>() {
            @Override
            public Data createFromParcel(Parcel in) {
                return new Data(in);
            }

            @Override
            public Data[] newArray(int size) {
                return new Data[size];
            }
        };

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getIsAdmin() {
            return isAdmin;
        }

        public void setIsAdmin(Integer isAdmin) {
            this.isAdmin = isAdmin;
        }

        public Integer getActive() {
            return active;
        }

        public void setActive(Integer active) {
            this.active = active;
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

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getIsParent() {
            return isParent;
        }

        public void setIsParent(String isParent) {
            this.isParent = isParent;
        }

        public String getIsEmployee() {
            return isEmployee;
        }

        public void setIsEmployee(String isEmployee) {
            this.isEmployee = isEmployee;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (id == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(id);
            }
            dest.writeString(name);
            dest.writeString(email);
            if (isAdmin == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(isAdmin);
            }
            if (active == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(active);
            }
            dest.writeString(firstName);
            dest.writeString(lastName);
            dest.writeString(phone);
            dest.writeString(image);
            dest.writeString(gender);
            dest.writeString(isParent);
            dest.writeString(isEmployee);
            dest.writeString(createdAt);
            dest.writeString(updatedAt);
        }
    }

}
