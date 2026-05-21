package com.wellys.pharmacovigilance.data.local;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.wellys.pharmacovigilance.data.local.entity.CaseEntity;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class CaseDao_Impl implements CaseDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<CaseEntity> __insertAdapterOfCaseEntity;

  public CaseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfCaseEntity = new EntityInsertAdapter<CaseEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `cases` (`id`,`user_id`,`submitted_at`,`patient_initials`,`patient_sex`,`patient_age`,`product_name`,`product_barcode`,`event_description`,`severity`,`onset_date`,`status`,`attachment_path`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final CaseEntity entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.userId);
        statement.bindLong(3, entity.submittedAt);
        if (entity.patientInitials == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.patientInitials);
        }
        if (entity.patientSex == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.patientSex);
        }
        statement.bindLong(6, entity.patientAge);
        if (entity.productName == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.productName);
        }
        if (entity.productBarcode == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.productBarcode);
        }
        if (entity.eventDescription == null) {
          statement.bindNull(9);
        } else {
          statement.bindText(9, entity.eventDescription);
        }
        if (entity.severity == null) {
          statement.bindNull(10);
        } else {
          statement.bindText(10, entity.severity);
        }
        statement.bindLong(11, entity.onsetDate);
        if (entity.status == null) {
          statement.bindNull(12);
        } else {
          statement.bindText(12, entity.status);
        }
        if (entity.attachmentPath == null) {
          statement.bindNull(13);
        } else {
          statement.bindText(13, entity.attachmentPath);
        }
      }
    };
  }

  @Override
  public long insert(final CaseEntity c) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfCaseEntity.insertAndReturnId(_connection, c);
    });
  }

  @Override
  public LiveData<List<CaseEntity>> observeCasesForUser(final long userId) {
    final String _sql = "SELECT * FROM cases WHERE user_id = ? ORDER BY submitted_at DESC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"cases"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "user_id");
        final int _columnIndexOfSubmittedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "submitted_at");
        final int _columnIndexOfPatientInitials = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "patient_initials");
        final int _columnIndexOfPatientSex = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "patient_sex");
        final int _columnIndexOfPatientAge = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "patient_age");
        final int _columnIndexOfProductName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "product_name");
        final int _columnIndexOfProductBarcode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "product_barcode");
        final int _columnIndexOfEventDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "event_description");
        final int _columnIndexOfSeverity = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "severity");
        final int _columnIndexOfOnsetDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "onset_date");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfAttachmentPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachment_path");
        final List<CaseEntity> _result = new ArrayList<CaseEntity>();
        while (_stmt.step()) {
          final CaseEntity _item;
          _item = new CaseEntity();
          _item.id = _stmt.getLong(_columnIndexOfId);
          _item.userId = _stmt.getLong(_columnIndexOfUserId);
          _item.submittedAt = _stmt.getLong(_columnIndexOfSubmittedAt);
          if (_stmt.isNull(_columnIndexOfPatientInitials)) {
            _item.patientInitials = null;
          } else {
            _item.patientInitials = _stmt.getText(_columnIndexOfPatientInitials);
          }
          if (_stmt.isNull(_columnIndexOfPatientSex)) {
            _item.patientSex = null;
          } else {
            _item.patientSex = _stmt.getText(_columnIndexOfPatientSex);
          }
          _item.patientAge = (int) (_stmt.getLong(_columnIndexOfPatientAge));
          if (_stmt.isNull(_columnIndexOfProductName)) {
            _item.productName = null;
          } else {
            _item.productName = _stmt.getText(_columnIndexOfProductName);
          }
          if (_stmt.isNull(_columnIndexOfProductBarcode)) {
            _item.productBarcode = null;
          } else {
            _item.productBarcode = _stmt.getText(_columnIndexOfProductBarcode);
          }
          if (_stmt.isNull(_columnIndexOfEventDescription)) {
            _item.eventDescription = null;
          } else {
            _item.eventDescription = _stmt.getText(_columnIndexOfEventDescription);
          }
          if (_stmt.isNull(_columnIndexOfSeverity)) {
            _item.severity = null;
          } else {
            _item.severity = _stmt.getText(_columnIndexOfSeverity);
          }
          _item.onsetDate = _stmt.getLong(_columnIndexOfOnsetDate);
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _item.status = null;
          } else {
            _item.status = _stmt.getText(_columnIndexOfStatus);
          }
          if (_stmt.isNull(_columnIndexOfAttachmentPath)) {
            _item.attachmentPath = null;
          } else {
            _item.attachmentPath = _stmt.getText(_columnIndexOfAttachmentPath);
          }
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<CaseEntity> observeById(final long id) {
    final String _sql = "SELECT * FROM cases WHERE id = ? LIMIT 1";
    return __db.getInvalidationTracker().createLiveData(new String[] {"cases"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "user_id");
        final int _columnIndexOfSubmittedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "submitted_at");
        final int _columnIndexOfPatientInitials = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "patient_initials");
        final int _columnIndexOfPatientSex = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "patient_sex");
        final int _columnIndexOfPatientAge = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "patient_age");
        final int _columnIndexOfProductName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "product_name");
        final int _columnIndexOfProductBarcode = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "product_barcode");
        final int _columnIndexOfEventDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "event_description");
        final int _columnIndexOfSeverity = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "severity");
        final int _columnIndexOfOnsetDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "onset_date");
        final int _columnIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _columnIndexOfAttachmentPath = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "attachment_path");
        final CaseEntity _result;
        if (_stmt.step()) {
          _result = new CaseEntity();
          _result.id = _stmt.getLong(_columnIndexOfId);
          _result.userId = _stmt.getLong(_columnIndexOfUserId);
          _result.submittedAt = _stmt.getLong(_columnIndexOfSubmittedAt);
          if (_stmt.isNull(_columnIndexOfPatientInitials)) {
            _result.patientInitials = null;
          } else {
            _result.patientInitials = _stmt.getText(_columnIndexOfPatientInitials);
          }
          if (_stmt.isNull(_columnIndexOfPatientSex)) {
            _result.patientSex = null;
          } else {
            _result.patientSex = _stmt.getText(_columnIndexOfPatientSex);
          }
          _result.patientAge = (int) (_stmt.getLong(_columnIndexOfPatientAge));
          if (_stmt.isNull(_columnIndexOfProductName)) {
            _result.productName = null;
          } else {
            _result.productName = _stmt.getText(_columnIndexOfProductName);
          }
          if (_stmt.isNull(_columnIndexOfProductBarcode)) {
            _result.productBarcode = null;
          } else {
            _result.productBarcode = _stmt.getText(_columnIndexOfProductBarcode);
          }
          if (_stmt.isNull(_columnIndexOfEventDescription)) {
            _result.eventDescription = null;
          } else {
            _result.eventDescription = _stmt.getText(_columnIndexOfEventDescription);
          }
          if (_stmt.isNull(_columnIndexOfSeverity)) {
            _result.severity = null;
          } else {
            _result.severity = _stmt.getText(_columnIndexOfSeverity);
          }
          _result.onsetDate = _stmt.getLong(_columnIndexOfOnsetDate);
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _result.status = null;
          } else {
            _result.status = _stmt.getText(_columnIndexOfStatus);
          }
          if (_stmt.isNull(_columnIndexOfAttachmentPath)) {
            _result.attachmentPath = null;
          } else {
            _result.attachmentPath = _stmt.getText(_columnIndexOfAttachmentPath);
          }
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void updateStatus(final long id, final String newStatus) {
    final String _sql = "UPDATE cases SET status = ? WHERE id = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (newStatus == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, newStatus);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
