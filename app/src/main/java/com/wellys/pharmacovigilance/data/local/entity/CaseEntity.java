package com.wellys.pharmacovigilance.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "cases",
    foreignKeys = @ForeignKey(
        entity = UserEntity.class,
        parentColumns = "id",
        childColumns = "user_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("user_id")}
)
public class CaseEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "user_id")
    public long userId;

    @ColumnInfo(name = "submitted_at")
    public long submittedAt;

    // ---------------- Patient ----------------
    @NonNull
    @ColumnInfo(name = "patient_initials")
    public String patientInitials = "";

    @NonNull
    @ColumnInfo(name = "patient_sex")
    public String patientSex = "M";

    @ColumnInfo(name = "patient_age")
    public int patientAge;

    // ---------------- Product ----------------
    @NonNull
    @ColumnInfo(name = "product_name")
    public String productName = "";

    @Nullable
    @ColumnInfo(name = "product_barcode")
    public String productBarcode;

    // ---------------- Event ------------------
    @NonNull
    @ColumnInfo(name = "event_description")
    public String eventDescription = "";

    @NonNull
    @ColumnInfo(name = "severity")
    public String severity = Severity.MINOR.name();

    @ColumnInfo(name = "onset_date")
    public long onsetDate;

    @NonNull
    @ColumnInfo(name = "status")
    public String status = CaseStatus.RECEIVED.name();

    @Nullable
    @ColumnInfo(name = "attachment_path")
    public String attachmentPath;

    public CaseEntity() {}

    public Severity getSeverityEnum() { return Severity.valueOf(severity); }
    public CaseStatus getStatusEnum() { return CaseStatus.valueOf(status); }
}
