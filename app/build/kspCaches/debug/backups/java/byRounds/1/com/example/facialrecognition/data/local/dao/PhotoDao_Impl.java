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
import com.example.facialrecognition.data.local.entity.Photo;
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
public final class PhotoDao_Impl implements PhotoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Photo> __insertionAdapterOfPhoto;

  private final EntityDeletionOrUpdateAdapter<Photo> __updateAdapterOfPhoto;

  public PhotoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPhoto = new EntityInsertionAdapter<Photo>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `photos` (`id`,`uri`,`dateAdded`,`isProcessed`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Photo entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getUri());
        statement.bindLong(3, entity.getDateAdded());
        final int _tmp = entity.isProcessed() ? 1 : 0;
        statement.bindLong(4, _tmp);
      }
    };
    this.__updateAdapterOfPhoto = new EntityDeletionOrUpdateAdapter<Photo>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `photos` SET `id` = ?,`uri` = ?,`dateAdded` = ?,`isProcessed` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Photo entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getUri());
        statement.bindLong(3, entity.getDateAdded());
        final int _tmp = entity.isProcessed() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Photo photo, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPhoto.insertAndReturnId(photo);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<Photo> photos, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPhoto.insert(photos);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Photo photo, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPhoto.handle(photo);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Photo>> getAllPhotos() {
    final String _sql = "SELECT * FROM photos ORDER BY dateAdded DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"photos"}, new Callable<List<Photo>>() {
      @Override
      @NonNull
      public List<Photo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUri = CursorUtil.getColumnIndexOrThrow(_cursor, "uri");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final List<Photo> _result = new ArrayList<Photo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Photo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUri;
            _tmpUri = _cursor.getString(_cursorIndexOfUri);
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            _item = new Photo(_tmpId,_tmpUri,_tmpDateAdded,_tmpIsProcessed);
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
  public Object getUnprocessedPhotos(final Continuation<? super List<Photo>> $completion) {
    final String _sql = "SELECT * FROM photos WHERE isProcessed = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Photo>>() {
      @Override
      @NonNull
      public List<Photo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUri = CursorUtil.getColumnIndexOrThrow(_cursor, "uri");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final List<Photo> _result = new ArrayList<Photo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Photo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUri;
            _tmpUri = _cursor.getString(_cursorIndexOfUri);
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            _item = new Photo(_tmpId,_tmpUri,_tmpDateAdded,_tmpIsProcessed);
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
  public Object getPhotoByUri(final String uri, final Continuation<? super Photo> $completion) {
    final String _sql = "SELECT * FROM photos WHERE uri = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, uri);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Photo>() {
      @Override
      @Nullable
      public Photo call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUri = CursorUtil.getColumnIndexOrThrow(_cursor, "uri");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final Photo _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUri;
            _tmpUri = _cursor.getString(_cursorIndexOfUri);
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            _result = new Photo(_tmpId,_tmpUri,_tmpDateAdded,_tmpIsProcessed);
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
  public Object getPhotosForPerson(final long personId,
      final Continuation<? super List<Photo>> $completion) {
    final String _sql = "SELECT photos.* FROM photos INNER JOIN faces ON photos.id = faces.photoId WHERE faces.personId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, personId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Photo>>() {
      @Override
      @NonNull
      public List<Photo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUri = CursorUtil.getColumnIndexOrThrow(_cursor, "uri");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final List<Photo> _result = new ArrayList<Photo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Photo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUri;
            _tmpUri = _cursor.getString(_cursorIndexOfUri);
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            _item = new Photo(_tmpId,_tmpUri,_tmpDateAdded,_tmpIsProcessed);
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
  public Flow<Integer> getPhotoCount() {
    final String _sql = "SELECT COUNT(*) FROM photos";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"photos"}, new Callable<Integer>() {
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

  @Override
  public Flow<Integer> getProcessedPhotoCount() {
    final String _sql = "SELECT COUNT(*) FROM photos WHERE isProcessed = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"photos"}, new Callable<Integer>() {
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

  @Override
  public Flow<List<Photo>> getRecentPhotos(final int limit) {
    final String _sql = "SELECT * FROM photos ORDER BY dateAdded DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"photos"}, new Callable<List<Photo>>() {
      @Override
      @NonNull
      public List<Photo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUri = CursorUtil.getColumnIndexOrThrow(_cursor, "uri");
          final int _cursorIndexOfDateAdded = CursorUtil.getColumnIndexOrThrow(_cursor, "dateAdded");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final List<Photo> _result = new ArrayList<Photo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Photo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUri;
            _tmpUri = _cursor.getString(_cursorIndexOfUri);
            final long _tmpDateAdded;
            _tmpDateAdded = _cursor.getLong(_cursorIndexOfDateAdded);
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            _item = new Photo(_tmpId,_tmpUri,_tmpDateAdded,_tmpIsProcessed);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
