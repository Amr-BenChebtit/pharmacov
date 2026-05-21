package com.wellys.pharmacovigilance.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.wellys.pharmacovigilance.data.local.entity.CaseEntity;

import java.util.List;

@Dao
public interface CaseDao {

    @Insert
    long insert(CaseEntity c);

    @Query("SELECT * FROM cases WHERE user_id = :userId ORDER BY submitted_at DESC")
    LiveData<List<CaseEntity>> observeCasesForUser(long userId);

    @Query("SELECT * FROM cases WHERE id = :id LIMIT 1")
    LiveData<CaseEntity> observeById(long id);

    @Query("UPDATE cases SET status = :newStatus WHERE id = :id")
    void updateStatus(long id, String newStatus);
}
