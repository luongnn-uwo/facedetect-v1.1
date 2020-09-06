package com.luongnguyen.facedetect;

public class StudentInfo{
    private String ID;
    private String Name;
    private String Status;
    private String Date;
    private String ClassID;

    //--------------------------------------------------------------------------------------------//
    //                    A class to control all information about student
    //--------------------------------------------------------------------------------------------//

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getClassID() {return ClassID;}

    public void setClassID(String classID) {ClassID = classID;}

    @Override
    public String toString() {
        return "StudentInfo{" +
                "ID='" + ID + '\'' +
                ", Name='" + Name + '\'' +
                ", Status='" + Status + '\'' +
                ", Date='" + Date + '\'' +
                ", ClassID='" + Date + '\'' +
                '}';
    }
}
