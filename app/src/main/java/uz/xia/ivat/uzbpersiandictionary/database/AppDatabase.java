package uz.xia.ivat.uzbpersiandictionary.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                WordEntity.class,
                WordFavorite.class,
                TrainEntity.class
        },
        version = 2,
        exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE = null;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "uzb_persian.db")
                    .createFromAsset("uzb_fors.sqlite")
//                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public abstract WordDao getWordDao();

    public abstract FavoriteDao getFavoriteDao();

    public abstract TrainDao getTrainDao();
}
