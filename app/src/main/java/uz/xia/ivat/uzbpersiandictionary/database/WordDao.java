package uz.xia.ivat.uzbpersiandictionary.database;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface WordDao {

    @Query("SELECT * FROM uzb_persian_dictionary")
    Single<List<WordEntity>> loadAll();

    @Query("SELECT * FROM uzb_persian_dictionary WHERE word_persian LIKE :query")
    Single<List<WordEntity>> loadLikelyPersian(String query);

    @Query("SELECT * FROM uzb_persian_dictionary WHERE LOWER(word_uzb) LIKE :query")
    Single<List<WordEntity>> loadLikelyCyrillic(String query);
}
