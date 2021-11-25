package uz.xia.ivat.uzbpersiandictionary.ui.home;

import android.app.Application;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import uz.xia.ivat.uzbpersiandictionary.database.AppDatabase;
import uz.xia.ivat.uzbpersiandictionary.database.TrainEntity;
import uz.xia.ivat.uzbpersiandictionary.database.WordEntity;
import uz.xia.ivat.uzbpersiandictionary.database.WordFavorite;
import uz.xia.ivat.uzbpersiandictionary.util.GlobalEvents;

public class HomeViewModel extends AndroidViewModel implements
        IHomeViewModel,
        Observer<Pair<Long, Boolean>>,
        OnCompleteListener<QuerySnapshot>,
        OnFailureListener,
        EventListener<QuerySnapshot> {

    private final AppDatabase appDatabase;
    private final CompositeDisposable cd;
    private final List<WordFavorite> favoriteList = new ArrayList<>();
    private final List<TrainEntity> trainList = new ArrayList<>();
    private final FirebaseFirestore fireStore;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private MutableLiveData<List<WordEntity>> liveWordList;
    private final Observer<Boolean> observerDeleted = isDeleted -> {
        trainList.clear();
        if (liveWordList.getValue() != null)
            for (WordEntity wordEntity : liveWordList.getValue())
                wordEntity.setFavorite(false);
    };
    private Disposable searchingDisposable;
    private String lastQuery = "";

    public HomeViewModel(Application app) {
        super(app);
        liveWordList = new MutableLiveData<>();
        appDatabase = AppDatabase.getInstance(app.getApplicationContext());
        cd = new CompositeDisposable();
        fireStore = FirebaseFirestore.getInstance();
        loadFromFireStore();
        updateFromFireStore();
        loadFavoritesFirst();
        GlobalEvents.liveFavoriteChanged.observeForever(this);
        GlobalEvents.liveFavoriteDeleted.observeForever(observerDeleted);
    }

    @Override
    public LiveData<List<WordEntity>> getLiveWordList() {
        return liveWordList;
    }

    @Override
    public String getLastQuery() {
        return lastQuery;
    }

    public void loadFavoritesFirst() {
        Disposable d = appDatabase.getFavoriteDao().loadAll()
                .subscribeOn(Schedulers.io())
                .subscribe(favorites -> {
                            favoriteList.clear();
                            favoriteList.addAll(favorites);
                            loadTrainFirst();
                        },
                        throwable -> {
                            favoriteList.clear();
                            loadTrainFirst();
                        });
        cd.add(d);
    }

    public void loadTrainFirst() {
        Disposable d = appDatabase.getTrainDao().loadAll()
                .subscribeOn(Schedulers.io())
                .subscribe(favorites -> {
                            trainList.clear();
                            trainList.addAll(favorites);
                            loadAllWords();
                        },
                        throwable -> {
                            trainList.clear();
                            loadAllWords();
                        });
        cd.add(d);
    }

    public void loadFavorites() {
        Disposable d = appDatabase.getFavoriteDao().loadAll()
                .subscribeOn(Schedulers.io())
                .subscribe(favorites -> {
                            favoriteList.clear();
                            favoriteList.addAll(favorites);
                        },
                        throwable -> favoriteList.clear());
        cd.add(d);
    }

    public void loadTrain() {
        Disposable d = appDatabase.getTrainDao().loadAll()
                .subscribeOn(Schedulers.io())
                .subscribe(favorites -> {
                            trainList.clear();
                            trainList.addAll(favorites);
                        },
                        throwable -> trainList.clear());
        cd.add(d);
    }

    @Override
    public void loadAllWords() {
        Disposable d = appDatabase.getWordDao()
                .loadAll()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(
                        list -> {
                            differTraining(list);
                            differFavorites(list);
                        },
                        throwable -> {
                            Log.e(getClass().getSimpleName(), "Error load: " + throwable.getMessage());
                            liveWordList.postValue(Collections.emptyList());
                        }
                );
        cd.add(d);
    }

    private void differFavorites(List<WordEntity> list) {
        for (WordFavorite favorite : favoriteList)
            for (WordEntity wordEntity : list)
                wordEntity.setFavorite(
                        wordEntity.getId().equals(favorite.getWordId())
                );
        liveWordList.postValue(list);
    }

    private void differTraining(List<WordEntity> list) {
        for (TrainEntity favorite : trainList)
            for (WordEntity wordEntity : list)
                wordEntity.setTraining(
                        wordEntity.getId().equals(favorite.getWordId())
                );
    }

    @Override
    public void removeFavorite(Long id) {
        appDatabase.getFavoriteDao().delete(id)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        cd.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(HomeViewModel.this.getClass().getSimpleName(), "Deteted favorite");
                        loadFavorites();
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(HomeViewModel.this.getClass().getSimpleName(), e.getMessage());
                    }
                });
    }

    @Override
    public void saveFavorite(WordEntity wordEntity) {
        WordFavorite favorite = new WordFavorite(
                wordEntity.getId(),
                wordEntity.getId(),
                wordEntity.getWordUzb(),
                wordEntity.getWordPersian(),
                wordEntity.getWordTranscription(),
                true,
                false
        );
        appDatabase.getFavoriteDao()
                .save(favorite)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        cd.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(HomeViewModel.this.getClass().getSimpleName(), "Success saved!");
                        loadFavorites();
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(HomeViewModel.this.getClass().getSimpleName(), e.getMessage());
                    }
                });
    }

    @Override
    public void removeTraining(Long id) {
        appDatabase.getTrainDao().delete(id)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        cd.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(HomeViewModel.this.getClass().getSimpleName(), "Deteted favorite");
                        loadTrain();
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(HomeViewModel.this.getClass().getSimpleName(), e.getMessage());
                    }
                });
    }

    @Override
    public void saveTraining(WordEntity wordEntity) {
        TrainEntity trainEntity = new TrainEntity(
                wordEntity.getId(),
                wordEntity.getId(),
                wordEntity.getWordUzb(),
                wordEntity.getWordPersian(),
                wordEntity.getWordTranscription(),
                false,
                false,
                false,
                false
        );
        appDatabase.getTrainDao()
                .save(trainEntity)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        cd.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(HomeViewModel.this.getClass().getSimpleName(), "Success saved!");
                        loadTrain();
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(HomeViewModel.this.getClass().getSimpleName(), e.getMessage());
                    }
                });
    }

    @Override
    public void search(String newText) {
        lastQuery = newText;
        searchCyrillic(newText);
    }

    @Override
    public void searchPersian(String newText) {
        if (searchingDisposable != null)
            searchingDisposable.dispose();
        searchingDisposable = appDatabase.getWordDao()
                .loadLikelyPersian("%" + newText + "%")
                .subscribeOn(Schedulers.io())
                .subscribe(
                        list -> {
                            differTraining(list);
                            differFavorites(list);
                        },
                        throwable -> liveWordList.postValue(Collections.emptyList())
                );
        cd.add(searchingDisposable);
    }

    @Override
    public void searchCyrillic(String newText) {
        if (searchingDisposable != null)
            searchingDisposable.dispose();
        searchingDisposable = appDatabase.getWordDao()
                .loadLikelyCyrillic("%" + newText + "%")
                .subscribeOn(Schedulers.io())
                .subscribe(
                        list -> {
                            differTraining(list);
                            differFavorites(list);
                        },
                        throwable -> liveWordList.postValue(Collections.emptyList())
                );
        cd.add(searchingDisposable);
    }

    @Override
    public void clearSearching() {
        lastQuery = "";
        if (searchingDisposable != null)
            searchingDisposable.dispose();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (searchingDisposable != null)
            searchingDisposable.dispose();
        cd.dispose();
        GlobalEvents.liveFavoriteChanged.removeObserver(this);
        GlobalEvents.liveFavoriteDeleted.removeObserver(observerDeleted);
    }

    @Override
    public void onChanged(Pair<Long, Boolean> favoritePair) {
        for (WordFavorite favorite : favoriteList)
            if (favorite.getWordId().equals(favoritePair.first)) {
                favorite.setFavorite(favoritePair.second);
                break;
            }
        for (WordEntity wordEntity : liveWordList.getValue())
            if (wordEntity.getId().equals(favoritePair.first)) {
                wordEntity.setFavorite(favoritePair.second);
                break;
            }
    }

    @Override
    public void loadFromFireStore() {
        new Handler().postDelayed((Runnable) () -> {
            fireStore.collection("dictionary")
                    .get()
                    .addOnCompleteListener(this)
                    .addOnFailureListener(this);
        }, 5000);
    }

    @Override
    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
        List<WordEntity> entitiesFromFirestore = new ArrayList<>();
        if (task.isSuccessful()) {
            if (task.getResult() == null) return;
            for (QueryDocumentSnapshot document : task.getResult()) {
                if (document.getString("uzb") == null) continue;
                if (document.getString("fors") == null) continue;
                entitiesFromFirestore.add(
                        new WordEntity(
                                getStringDocument(document, "uzb"),
                                getStringDocument(document, "fors"),
                                getStringDocument(document, "read"),
                                document.getId()
                        )
                );
            }

            if (entitiesFromFirestore.isEmpty()) return;
            Disposable d = Completable.create(emitter ->
                    appDatabase.getWordDao()
                            .saveAll(entitiesFromFirestore))
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            () -> Log.e(getClass().getSimpleName(), "Saved entities from Firebase Firestore!"),
                            throwable -> Log.e(getClass().getSimpleName(), "onComplete: ", throwable)
                    );
            cd.add(d);
        } else
            Log.e(getClass().getSimpleName(), "onComplete: ", task.getException());
    }

    private String getStringDocument(QueryDocumentSnapshot document, String field) {
        return (document.getString(field) == null) ? "" : document.getString(field);
    }

    @Override
    public void onFailure(@NonNull @NotNull Exception e) {
        Log.e(getClass().getSimpleName(), e.getMessage());
    }

    @Override
    public void updateFromFireStore() {
        new Handler().postDelayed((Runnable) () -> {
            fireStore.collection("dictionary")
                    .addSnapshotListener(executor, this);
        }, 5000);
    }

    @Override
    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
        if (value != null) {
            List<WordEntity> entitiesFromFirestore = new ArrayList<>();
            for (QueryDocumentSnapshot document : value) {
                if (document.getString("uzb") == null) continue;
                if (document.getString("fors") == null) continue;
                entitiesFromFirestore.add(
                        new WordEntity(
                                getStringDocument(document, "uzb"),
                                getStringDocument(document, "fors"),
                                getStringDocument(document, "read"),
                                document.getId()
                        )
                );
            }

            if (entitiesFromFirestore.isEmpty()) return;
            Disposable d = Completable.create(emitter ->
                    appDatabase.getWordDao()
                            .saveAll(entitiesFromFirestore))
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            () -> Log.e(getClass().getSimpleName(), "Saved entities from Firebase Firestore!"),
                            throwable -> Log.e(getClass().getSimpleName(), "onComplete: ", throwable)
                    );
            cd.add(d);
        }

        if (error != null)
            Log.e(getClass().getSimpleName(), error.getMessage());
    }
}