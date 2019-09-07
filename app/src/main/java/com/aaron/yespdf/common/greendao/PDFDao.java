package com.aaron.yespdf.common.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.aaron.yespdf.common.bean.PDF;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PDF".
*/
public class PDFDao extends AbstractDao<PDF, Long> {

    public static final String TABLENAME = "PDF";

    /**
     * Properties of entity PDF.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Path = new Property(1, String.class, "path", false, "PATH");
        public final static Property Dir = new Property(2, String.class, "dir", false, "DIR");
        public final static Property Name = new Property(3, String.class, "name", false, "NAME");
        public final static Property Cover = new Property(4, String.class, "cover", false, "COVER");
        public final static Property Progress = new Property(5, String.class, "progress", false, "PROGRESS");
        public final static Property CurPage = new Property(6, int.class, "curPage", false, "CUR_PAGE");
        public final static Property TotalPage = new Property(7, int.class, "totalPage", false, "TOTAL_PAGE");
        public final static Property BookmarkPage = new Property(8, String.class, "bookmarkPage", false, "BOOKMARK_PAGE");
        public final static Property LatestRead = new Property(9, long.class, "latestRead", false, "LATEST_READ");
    }


    public PDFDao(DaoConfig config) {
        super(config);
    }
    
    public PDFDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PDF\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"PATH\" TEXT UNIQUE ," + // 1: path
                "\"DIR\" TEXT," + // 2: dir
                "\"NAME\" TEXT," + // 3: name
                "\"COVER\" TEXT," + // 4: cover
                "\"PROGRESS\" TEXT," + // 5: progress
                "\"CUR_PAGE\" INTEGER NOT NULL ," + // 6: curPage
                "\"TOTAL_PAGE\" INTEGER NOT NULL ," + // 7: totalPage
                "\"BOOKMARK_PAGE\" TEXT," + // 8: bookmarkPage
                "\"LATEST_READ\" INTEGER NOT NULL );"); // 9: latestRead
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PDF\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, PDF entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(2, path);
        }
 
        String dir = entity.getDir();
        if (dir != null) {
            stmt.bindString(3, dir);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(4, name);
        }
 
        String cover = entity.getCover();
        if (cover != null) {
            stmt.bindString(5, cover);
        }
 
        String progress = entity.getProgress();
        if (progress != null) {
            stmt.bindString(6, progress);
        }
        stmt.bindLong(7, entity.getCurPage());
        stmt.bindLong(8, entity.getTotalPage());
 
        String bookmarkPage = entity.getBookmarkPage();
        if (bookmarkPage != null) {
            stmt.bindString(9, bookmarkPage);
        }
        stmt.bindLong(10, entity.getLatestRead());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, PDF entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(2, path);
        }
 
        String dir = entity.getDir();
        if (dir != null) {
            stmt.bindString(3, dir);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(4, name);
        }
 
        String cover = entity.getCover();
        if (cover != null) {
            stmt.bindString(5, cover);
        }
 
        String progress = entity.getProgress();
        if (progress != null) {
            stmt.bindString(6, progress);
        }
        stmt.bindLong(7, entity.getCurPage());
        stmt.bindLong(8, entity.getTotalPage());
 
        String bookmarkPage = entity.getBookmarkPage();
        if (bookmarkPage != null) {
            stmt.bindString(9, bookmarkPage);
        }
        stmt.bindLong(10, entity.getLatestRead());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public PDF readEntity(Cursor cursor, int offset) {
        PDF entity = new PDF( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // path
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // dir
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // name
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // cover
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // progress
            cursor.getInt(offset + 6), // curPage
            cursor.getInt(offset + 7), // totalPage
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // bookmarkPage
            cursor.getLong(offset + 9) // latestRead
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, PDF entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPath(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDir(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCover(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setProgress(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCurPage(cursor.getInt(offset + 6));
        entity.setTotalPage(cursor.getInt(offset + 7));
        entity.setBookmarkPage(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setLatestRead(cursor.getLong(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(PDF entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(PDF entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(PDF entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
