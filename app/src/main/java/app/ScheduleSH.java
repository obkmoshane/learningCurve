package app;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by Obakeng Moshane on 2016-02-29.
 */
public class ScheduleSH extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ScheduleDB";
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_TABLENAME = "Schedule";
    private static final String Key_ID = "ID";
    private static final String Key_cfirstname = "cfirstname";

    private static final String Key_clastname= "clastname";
    private static final String Key_cemail="cemail";
    private static final String Key_ccontact ="ccontact";
    private static final String Key_datebooked="datebooked";
    private static final String Key_timebooked="timebooked";
    private static final String Key_stylistid="stylistid";
    private static final String Key_sfirstname="sfirstname";
    private static final String Key_slastname="slastname";
    private static final String Key_bookingtype="bookingtype";

    public ScheduleSH(Context context)
    {

        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
@Override
public void onCreate(SQLiteDatabase db)
{

    String createTeksieTable = "CREATE TABLE Schedule (" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cfirstname TEXT," +
            "clastname Varchar(100)," +
            "cemail Varchar(100)," +
            "ccontact TEXT," +
            "datebooked TEXT," +
            "timebooked TEXT," +
            "stylistid TEXT," +
            "sfirstname TEXT," +
            "slastname TEXT," +
            "bookingtype TEXT)";
    db.execSQL(createTeksieTable); //Execute method to create table
}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXIST");
        this.onCreate(db);
    }

    public void AddSchedule(Schedule schedule)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
    }




}
