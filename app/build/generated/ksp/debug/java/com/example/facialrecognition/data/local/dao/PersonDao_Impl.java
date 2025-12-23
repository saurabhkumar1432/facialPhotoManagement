package com.example.facialrecognition.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.facialrecognition.data.local.entity.Person;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PersonDao_Impl implements PersonDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Person> __insertionAdapterOfPerson;

  private final EntityDeletionOrUpdateAdapter<Person> __updateAdapterOfPerson;

  public PersonDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPerson = new EntityInsertionAdapter<Person>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `people` (`id`,`name`,`coverPhotoId`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Person entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getCoverPhotoId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getCoverPhotoId());
        }
      }
    };
    this.__updateAdapterOfPerson = new EntityDeletionOrUpdateAdapter<Person>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `people` SET `id` = ?,`name` = ?,`coverPhotoId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Person entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getCoverPhotoId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getCoverPhotoId());
        }
        statement.bindLong(4, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Person person, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPerson.insertAndReturnId(person);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Person person, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPerson.handle(person);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Person>> getAllPeople() {
    final String _sql = "SELECT * FROM people ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"people"}, new Callable<List<Person>>() {
      @Override
      @NonNull
      public List<Person> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCoverPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPhotoId");
          final List<Person> _result = new ArrayList<Person>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Person _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final Long _tmpCoverPhotoId;
            if (_cursor.isNull(_cursorIndexOfCoverPhotoId)) {
              _tmpCoverPhotoId = null;
            } else {
              _tmpCoverPhotoId = _cursor.getLong(_cursorIndexOfCoverPhotoId);
            }
            _item = new Person(_tmpId,_tmpName,_tmpCoverPhotoId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllPeopleList(final Continuation<? super List<Person>> $completion) {
    final String _sql = "SELECT * FROM people";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Person>>() {
      @Override
      @NonNull
      public List<Person> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCoverPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPhotoId");
          final List<Person> _result = new ArrayList<Person>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Person _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final Long _tmpCoverPhotoId;
            if (_cursor.isNull(_cursorIndexOfCoverPhotoId)) {
              _tmpCoverPhotoId = null;
            } else {
              _tmpCoverPhotoId = _cursor.getLong(_cursorIndexOfCoverPhotoId);
            }
            _item = new Person(_tmpId,_tmpName,_tmpCoverPhotoId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPersonById(final long id, final Continuation<? super Person> $completion) {
    final String _sql = "SELECT * FROM people WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Person>() {
      @Override
      @Nullable
      public Person call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCoverPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "coverPhotoId");
          final Person _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final Long _tmpCoverPhotoId;
            if (_cursor.isNull(_cursorIndexOfCoverPhotoId)) {
              _tmpCoverPhotoId = null;
            } else {
              _tmpCoverPhotoId = _cursor.getLong(_cursorIndexOfCoverPhotoId);
            }
            _result = new Person(_tmpId,_tmpName,_tmpCoverPhotoId);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<Integer> getPeopleCount() {
    final String _sql = "SELECT COUNT(*) FROM people";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"people"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
