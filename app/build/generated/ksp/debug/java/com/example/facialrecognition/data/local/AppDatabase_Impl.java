package com.example.facialrecognition.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.facialrecognition.data.local.dao.FaceDao;
import com.example.facialrecognition.data.local.dao.FaceDao_Impl;
import com.example.facialrecognition.data.local.dao.PersonDao;
import com.example.facialrecognition.data.local.dao.PersonDao_Impl;
import com.example.facialrecognition.data.local.dao.PhotoDao;
import com.example.facialrecognition.data.local.dao.PhotoDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile PhotoDao _photoDao;

  private volatile FaceDao _faceDao;

  private volatile PersonDao _personDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `photos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `uri` TEXT NOT NULL, `dateAdded` INTEGER NOT NULL, `isProcessed` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `faces` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `photoId` INTEGER NOT NULL, `personId` INTEGER, `embedding` TEXT, `boundingBoxJson` TEXT, FOREIGN KEY(`photoId`) REFERENCES `photos`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`personId`) REFERENCES `people`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_faces_photoId` ON `faces` (`photoId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_faces_personId` ON `faces` (`personId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `people` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `coverPhotoId` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '0fdd4512c46415ae5286b58ac133eec5')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `photos`");
        db.execSQL("DROP TABLE IF EXISTS `faces`");
        db.execSQL("DROP TABLE IF EXISTS `people`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsPhotos = new HashMap<String, TableInfo.Column>(4);
        _columnsPhotos.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("uri", new TableInfo.Column("uri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("dateAdded", new TableInfo.Column("dateAdded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhotos.put("isProcessed", new TableInfo.Column("isProcessed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPhotos = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPhotos = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPhotos = new TableInfo("photos", _columnsPhotos, _foreignKeysPhotos, _indicesPhotos);
        final TableInfo _existingPhotos = TableInfo.read(db, "photos");
        if (!_infoPhotos.equals(_existingPhotos)) {
          return new RoomOpenHelper.ValidationResult(false, "photos(com.example.facialrecognition.data.local.entity.Photo).\n"
                  + " Expected:\n" + _infoPhotos + "\n"
                  + " Found:\n" + _existingPhotos);
        }
        final HashMap<String, TableInfo.Column> _columnsFaces = new HashMap<String, TableInfo.Column>(5);
        _columnsFaces.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFaces.put("photoId", new TableInfo.Column("photoId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFaces.put("personId", new TableInfo.Column("personId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFaces.put("embedding", new TableInfo.Column("embedding", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFaces.put("boundingBoxJson", new TableInfo.Column("boundingBoxJson", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFaces = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysFaces.add(new TableInfo.ForeignKey("photos", "CASCADE", "NO ACTION", Arrays.asList("photoId"), Arrays.asList("id")));
        _foreignKeysFaces.add(new TableInfo.ForeignKey("people", "SET NULL", "NO ACTION", Arrays.asList("personId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesFaces = new HashSet<TableInfo.Index>(2);
        _indicesFaces.add(new TableInfo.Index("index_faces_photoId", false, Arrays.asList("photoId"), Arrays.asList("ASC")));
        _indicesFaces.add(new TableInfo.Index("index_faces_personId", false, Arrays.asList("personId"), Arrays.asList("ASC")));
        final TableInfo _infoFaces = new TableInfo("faces", _columnsFaces, _foreignKeysFaces, _indicesFaces);
        final TableInfo _existingFaces = TableInfo.read(db, "faces");
        if (!_infoFaces.equals(_existingFaces)) {
          return new RoomOpenHelper.ValidationResult(false, "faces(com.example.facialrecognition.data.local.entity.Face).\n"
                  + " Expected:\n" + _infoFaces + "\n"
                  + " Found:\n" + _existingFaces);
        }
        final HashMap<String, TableInfo.Column> _columnsPeople = new HashMap<String, TableInfo.Column>(3);
        _columnsPeople.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeople.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeople.put("coverPhotoId", new TableInfo.Column("coverPhotoId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPeople = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPeople = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPeople = new TableInfo("people", _columnsPeople, _foreignKeysPeople, _indicesPeople);
        final TableInfo _existingPeople = TableInfo.read(db, "people");
        if (!_infoPeople.equals(_existingPeople)) {
          return new RoomOpenHelper.ValidationResult(false, "people(com.example.facialrecognition.data.local.entity.Person).\n"
                  + " Expected:\n" + _infoPeople + "\n"
                  + " Found:\n" + _existingPeople);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "0fdd4512c46415ae5286b58ac133eec5", "afdcc9f682374ccb1768ae4043eb0919");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "photos","faces","people");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `photos`");
      _db.execSQL("DELETE FROM `faces`");
      _db.execSQL("DELETE FROM `people`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(PhotoDao.class, PhotoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FaceDao.class, FaceDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PersonDao.class, PersonDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public PhotoDao photoDao() {
    if (_photoDao != null) {
      return _photoDao;
    } else {
      synchronized(this) {
        if(_photoDao == null) {
          _photoDao = new PhotoDao_Impl(this);
        }
        return _photoDao;
      }
    }
  }

  @Override
  public FaceDao faceDao() {
    if (_faceDao != null) {
      return _faceDao;
    } else {
      synchronized(this) {
        if(_faceDao == null) {
          _faceDao = new FaceDao_Impl(this);
        }
        return _faceDao;
      }
    }
  }

  @Override
  public PersonDao personDao() {
    if (_personDao != null) {
      return _personDao;
    } else {
      synchronized(this) {
        if(_personDao == null) {
          _personDao = new PersonDao_Impl(this);
        }
        return _personDao;
      }
    }
  }
}
