package uz.xia.ivat.uzbpersiandictionary.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface TrainDao {

    @Query("SELECT * FROM uzb_persian_train")
    Single<List<TrainEntity>> loadAll();

    @Query("SELECT * FROM uzb_persian_train WHERE is_correct_persian = 0 ORDER BY RANDOM() LIMIT(20)")
    Single<List<TrainEntity>> loadAllPersianNoTrain();

    @Query("SELECT * FROM uzb_persian_train WHERE is_correct_uzb = 0 ORDER BY RANDOM() LIMIT(20)")
    Single<List<TrainEntity>> loadAllUzbNoTrain();

    @Query("SELECT * FROM uzb_persian_train WHERE is_correct_sprint = 0 ORDER BY RANDOM() LIMIT(20)")
    Single<List<TrainEntity>> loadAllSprintNoTrain();

    @Query("SELECT * FROM uzb_persian_train WHERE is_correct_constructor = 0 ORDER BY RANDOM() LIMIT(20)")
    Single<List<TrainEntity>> loadAllConstructNoTrain();

    @Query("SELECT COUNT(id) FROM uzb_persian_train")
    Flowable<Integer> loadCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable save(TrainEntity favorite);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveAll(List<TrainEntity> trainList);

    @Query("DELETE FROM uzb_persian_train WHERE word_id = :id")
    Completable delete(Long id);

    @Query("SELECT * FROM uzb_persian_train WHERE word_id = :id")
    Single<TrainEntity> load(Long id);

    @Query("SELECT COUNT(id) FROM uzb_persian_train WHERE is_correct_uzb = :inCorrect")
    Flowable<Integer> loadUzbPersianCount(int inCorrect);

    @Query("SELECT COUNT(id) FROM uzb_persian_train WHERE is_correct_persian = :inCorrect")
    Flowable<Integer> loadPersianUzbCount(int inCorrect);

    @Query("SELECT COUNT(id) FROM uzb_persian_train WHERE is_correct_sprint = :inCorrect")
    Flowable<Integer> loadSprintCount(int inCorrect);

    @Query("SELECT COUNT(id) FROM uzb_persian_train WHERE is_correct_constructor = :inCorrect")
    Flowable<Integer> loadConstructCount(int inCorrect);

    @Query("SELECT * FROM uzb_persian_train WHERE is_correct_uzb = :train AND NOT id = :wordId ORDER BY RANDOM() LIMIT(3)")
    Single<List<TrainEntity>> loadRandomUzbPersianNo(Long wordId, int train);

    @Query("SELECT * FROM uzb_persian_train WHERE is_correct_persian = :train AND NOT id = :wordId LIMIT(3)")
    Single<List<TrainEntity>> loadRandomPersianUzbNo(Long wordId, int train);

    @Query("SELECT * FROM uzb_persian_train WHERE is_correct_sprint = :train AND NOT id = :wordId ORDER BY RANDOM() LIMIT(1)")
    Single<TrainEntity> loadRandomSprintNo(Long wordId, int train);

    @Update
    Completable saveCorrectUzbPersian(List<TrainEntity> correctTrainList);
}
