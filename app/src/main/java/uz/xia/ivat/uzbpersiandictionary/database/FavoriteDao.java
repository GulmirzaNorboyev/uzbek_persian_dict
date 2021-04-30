package uz.xia.ivat.uzbpersiandictionary.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface FavoriteDao {

    @Query("SELECT * FROM uzb_persian_favorite")
    Single<List<WordFavorite>> loadAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable save(WordFavorite favorite);

    @Query("DELETE FROM uzb_persian_favorite WHERE word_id = :id")
    Completable delete(Long id);

    @Query("SELECT * FROM uzb_persian_favorite WHERE word_id = :id")
    Single<WordFavorite> load(Long id);

    @Query("UPDATE uzb_persian_favorite SET is_training = :isTraining WHERE word_id = :wordId")
    Completable update(Long wordId, int isTraining);

    @Query("DELETE FROM uzb_persian_favorite")
    Completable clear();
}
