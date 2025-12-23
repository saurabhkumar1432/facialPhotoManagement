package com.example.facialrecognition.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.facialrecognition.data.local.Converters;
import com.example.facialrecognition.data.local.entity.Face;
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
public final class FaceDao_Impl implements FaceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Face> __insertionAdapterOfFace;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<Face> __updateAdapterOfFace;

  public FaceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFace = new EntityInsertionAdapter<Face>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `faces` (`id`,`photoId`,`personId`,`embedding`,`boundingBoxJson`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Face entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPhotoId());
        if (entity.getPersonId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getPersonId());
        }
        final String _tmp = __converters.floatArrayToString(entity.getEmbedding());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        if (entity.getBoundingBoxJson() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getBoundingBoxJson());
        }
      }
    };
    this.__updateAdapterOfFace = new EntityDeletionOrUpdateAdapter<Face>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `faces` SET `id` = ?,`photoId` = ?,`personId` = ?,`embedding` = ?,`boundingBoxJson` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Face entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPhotoId());
        if (entity.getPersonId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getPersonId());
        }
        final String _tmp = __converters.floatArrayToString(entity.getEmbedding());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        if (entity.getBoundingBoxJson() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getBoundingBoxJson());
        }
        statement.bindLong(6, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Face face, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFace.insertAndReturnId(face);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Face face, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFace.handle(face);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getFacesForPerson(final long personId,
      final Continuation<? super List<Face>> $completion) {
    final String _sql = "SELECT * FROM faces WHERE personId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, personId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Face>>() {
      @Override
      @NonNull
      public List<Face> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photoId");
          final int _cursorIndexOfPersonId = CursorUtil.getColumnIndexOrThrow(_cursor, "personId");
          final int _cursorIndexOfEmbedding = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding");
          final int _cursorIndexOfBoundingBoxJson = CursorUtil.getColumnIndexOrThrow(_cursor, "boundingBoxJson");
          final List<Face> _result = new ArrayList<Face>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Face _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final Long _tmpPersonId;
            if (_cursor.isNull(_cursorIndexOfPersonId)) {
              _tmpPersonId = null;
            } else {
              _tmpPersonId = _cursor.getLong(_cursorIndexOfPersonId);
            }
            final float[] _tmpEmbedding;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfEmbedding)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfEmbedding);
            }
            _tmpEmbedding = __converters.fromFloatArray(_tmp);
            final String _tmpBoundingBoxJson;
            if (_cursor.isNull(_cursorIndexOfBoundingBoxJson)) {
              _tmpBoundingBoxJson = null;
            } else {
              _tmpBoundingBoxJson = _cursor.getString(_cursorIndexOfBoundingBoxJson);
            }
            _item = new Face(_tmpId,_tmpPhotoId,_tmpPersonId,_tmpEmbedding,_tmpBoundingBoxJson);
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
  public Object getAllFaces(final Continuation<? super List<Face>> $completion) {
    final String _sql = "SELECT * FROM faces";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Face>>() {
      @Override
      @NonNull
      public List<Face> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photoId");
          final int _cursorIndexOfPersonId = CursorUtil.getColumnIndexOrThrow(_cursor, "personId");
          final int _cursorIndexOfEmbedding = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding");
          final int _cursorIndexOfBoundingBoxJson = CursorUtil.getColumnIndexOrThrow(_cursor, "boundingBoxJson");
          final List<Face> _result = new ArrayList<Face>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Face _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final Long _tmpPersonId;
            if (_cursor.isNull(_cursorIndexOfPersonId)) {
              _tmpPersonId = null;
            } else {
              _tmpPersonId = _cursor.getLong(_cursorIndexOfPersonId);
            }
            final float[] _tmpEmbedding;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfEmbedding)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfEmbedding);
            }
            _tmpEmbedding = __converters.fromFloatArray(_tmp);
            final String _tmpBoundingBoxJson;
            if (_cursor.isNull(_cursorIndexOfBoundingBoxJson)) {
              _tmpBoundingBoxJson = null;
            } else {
              _tmpBoundingBoxJson = _cursor.getString(_cursorIndexOfBoundingBoxJson);
            }
            _item = new Face(_tmpId,_tmpPhotoId,_tmpPersonId,_tmpEmbedding,_tmpBoundingBoxJson);
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
  public Object getFacesForPhoto(final long photoId,
      final Continuation<? super List<Face>> $completion) {
    final String _sql = "SELECT * FROM faces WHERE photoId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, photoId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Face>>() {
      @Override
      @NonNull
      public List<Face> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoId = CursorUtil.getColumnIndexOrThrow(_cursor, "photoId");
          final int _cursorIndexOfPersonId = CursorUtil.getColumnIndexOrThrow(_cursor, "personId");
          final int _cursorIndexOfEmbedding = CursorUtil.getColumnIndexOrThrow(_cursor, "embedding");
          final int _cursorIndexOfBoundingBoxJson = CursorUtil.getColumnIndexOrThrow(_cursor, "boundingBoxJson");
          final List<Face> _result = new ArrayList<Face>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Face _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPhotoId;
            _tmpPhotoId = _cursor.getLong(_cursorIndexOfPhotoId);
            final Long _tmpPersonId;
            if (_cursor.isNull(_cursorIndexOfPersonId)) {
              _tmpPersonId = null;
            } else {
              _tmpPersonId = _cursor.getLong(_cursorIndexOfPersonId);
            }
            final float[] _tmpEmbedding;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfEmbedding)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfEmbedding);
            }
            _tmpEmbedding = __converters.fromFloatArray(_tmp);
            final String _tmpBoundingBoxJson;
            if (_cursor.isNull(_cursorIndexOfBoundingBoxJson)) {
              _tmpBoundingBoxJson = null;
            } else {
              _tmpBoundingBoxJson = _cursor.getString(_cursorIndexOfBoundingBoxJson);
            }
            _item = new Face(_tmpId,_tmpPhotoId,_tmpPersonId,_tmpEmbedding,_tmpBoundingBoxJson);
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
  public Flow<Integer> getFaceCount() {
    final String _sql = "SELECT COUNT(*) FROM faces";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"faces"}, new Callable<Integer>() {
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
