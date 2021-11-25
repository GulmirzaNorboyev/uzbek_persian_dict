package uz.xia.ivat.uzbpersiandictionary.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public abstract class WordDao {

    @Query("SELECT * FROM uzb_persian_dictionary ORDER BY id DESC")
    public abstract Flowable<List<WordEntity>> loadAll();

    @Query("SELECT * FROM uzb_persian_dictionary ORDER BY id DESC")
    public abstract List<WordEntity> getAll();

    @Query("SELECT * FROM uzb_persian_dictionary WHERE word_persian LIKE :query")
    public abstract Single<List<WordEntity>> loadLikelyPersian(String query);

    @Query("SELECT * FROM uzb_persian_dictionary WHERE LOWER(word_uzb) LIKE :query")
    public abstract Single<List<WordEntity>> loadLikelyCyrillic(String query);

    @Query("SELECT * FROM uzb_persian_dictionary WHERE LOWER(word_uzb) LIKE :query AND id != :currentId")
    public abstract Single<List<WordEntity>> loadLikelyCyrillicNo(Long currentId, String query);

    @Query("SELECT * FROM uzb_persian_dictionary WHERE firebase_id = :firestoreId")
    public abstract WordEntity loadByFirestoreKey(String firestoreId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Long save(WordEntity entity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void update(WordEntity entity);

    @Delete
    public abstract void deleteItems(List<WordEntity> entities);

    @Transaction
    public void saveAll(List<WordEntity> entitiesFromFirestore) {
        WordEntity oldFirebaseEntity;

        for (WordEntity entity : entitiesFromFirestore) {
            oldFirebaseEntity = loadByFirestoreKey(entity.getFirebaseId());
            if (oldFirebaseEntity != null) {
                entity.setId(oldFirebaseEntity.getId());
                update(entity);
            } else
                save(entity);
        }

        List<WordEntity> allEntities = getAll();
        List<WordEntity> ignoredEntities = allEntities.stream()
                .filter(wordEntity -> {
                    boolean isNotEmptyFirebaseId = !wordEntity.getFirebaseId().isEmpty();
                    Optional<WordEntity> opt = entitiesFromFirestore
                            .stream()
                            .filter(firebaseEntity ->
                                    wordEntity.getFirebaseId().equals(firebaseEntity.getFirebaseId()))
                            .findAny();

                    return isNotEmptyFirebaseId && !opt.isPresent();
                })
                .collect(Collectors.toList());
        if (!ignoredEntities.isEmpty())
            deleteItems(ignoredEntities);
    }
}
