package com.wellys.pharmacovigilance.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    @ColumnInfo(name = "email")
    public String email = "";

    @NonNull
    @ColumnInfo(name = "password")
    public String password = ""; // plain text — demo only

    @NonNull
    @ColumnInfo(name = "full_name")
    public String fullName = "";

    /** Stored as String ("PATIENT" / "HEALTHCARE_PRO"). */
    @NonNull
    @ColumnInfo(name = "user_type")
    public String userType = UserType.PATIENT.name();

    /** Only used when userType = HEALTHCARE_PRO. Nullable. */
    @Nullable
    @ColumnInfo(name = "profession")
    public String profession;

    public UserEntity() {}

    @Ignore
    public UserEntity(@NonNull String email,
                      @NonNull String password,
                      @NonNull String fullName,
                      @NonNull UserType userType,
                      @Nullable String profession) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.userType = userType.name();
        this.profession = profession;
    }

    public UserType getUserTypeEnum() {
        return UserType.valueOf(userType);
    }
}
