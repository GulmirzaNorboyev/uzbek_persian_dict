package uz.xia.ivat.uzbpersiandictionary.ui.favorite;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import uz.xia.ivat.uzbpersiandictionary.database.AppDatabase;
import uz.xia.ivat.uzbpersiandictionary.database.TrainEntity;
import uz.xia.ivat.uzbpersiandictionary.database.WordFavorite;
import uz.xia.ivat.uzbpersiandictionary.util.GlobalEvents;

public class FavoriteViewModel extends AndroidViewModel
        implements IFavoriteViewModel,
        Observer<Boolean> {

    private final AppDatabase appDatabase;
    private final CompositeDisposable cd;
    private MutableLiveData<List<WordFavorite>> liveFavorites;
    private final Observer<Pair<Long, Boolean>> observableTraining = trainPair -> {
        if (liveFavorites.getValue() == null) return;
        for (WordFavorite favorite : liveFavorites.getValue())
            if (favorite.getWordId().equals(trainPair.first)) {
                favorite.setTraining(trainPair.second);
                break;
            }
        updateFavoriteForTrain(trainPair);
    };

    public FavoriteViewModel(Application app) {
        super(app);
        liveFavorites = new MutableLiveData<>();
        appDatabase = AppDatabase.getInstance(app.getApplicationContext());
        cd = new CompositeDisposable();
        GlobalEvents.liveFavoriteDetailsChanged.observeForever(this);
        GlobalEvents.liveTrainDetailsChanged.observeForever(observableTraining);
    }

    @Override
    public LiveData<List<WordFavorite>> getLiveFavorites() {
        return liveFavorites;
    }

    @Override
    public void loadAll() {
        Disposable d = appDatabase.getFavoriteDao()
                .loadAll()
                .subscribeOn(Schedulers.io())
                .subscribe(favoriteList -> {
                            if (favoriteList.isEmpty()) {
                                List<WordFavorite> emptyModelList = new ArrayList<>();
                                emptyModelList.add(new WordFavorite(-1L, -1L, "", "", "", false, false));
                                liveFavorites.postValue(emptyModelList);
                            } else
                                liveFavorites.postValue(favoriteList);
                        },
                        throwable -> {
                            Log.e(FavoriteViewModel.this.getClass().getSimpleName(), throwable.getMessage());
                            List<WordFavorite> emptyModelList = new ArrayList<>();
                            emptyModelList.add(new WordFavorite(-1L, -1L, "", "", "", false, false));
                            liveFavorites.postValue(emptyModelList);
                        });
        cd.add(d);
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
                        Log.e(FavoriteViewModel.this.getClass().getSimpleName(), "Deteted favorite");
                        GlobalEvents.liveFavoriteChanged.postValue(new Pair<>(id, false));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(FavoriteViewModel.this.getClass().getSimpleName(), e.getMessage());
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
                        Log.e(FavoriteViewModel.this.getClass().getSimpleName(), "Deteted favorite");
                        GlobalEvents.liveTrainChanged.postValue(new Pair<>(id, false));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(FavoriteViewModel.this.getClass().getSimpleName(), e.getMessage());
                    }
                });
    }

    @Override
    public void saveTraining(WordFavorite wordFavorite) {
        TrainEntity trainEntity = new TrainEntity(
                wordFavorite.getId(),
                wordFavorite.getId(),
                wordFavorite.getWordUzb(),
                wordFavorite.getWordPersian(),
                wordFavorite.getWordTranscription(),
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
                        Log.e(FavoriteViewModel.this.getClass().getSimpleName(), "Success saved!");
                        GlobalEvents.liveTrainChanged.postValue(new Pair<>(wordFavorite.getId(), true));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(FavoriteViewModel.this.getClass().getSimpleName(), e.getMessage());
                    }
                });
    }

    @Override
    public void removeAll() {
        appDatabase.getFavoriteDao().clear()
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        cd.add(d);
                    }

                    @Override
                    public void onComplete() {
                        List<WordFavorite> emptyModelList = new ArrayList<>();
                        emptyModelList.add(new WordFavorite(-1L, -1L, "", "", "", false, false));
                        liveFavorites.postValue(emptyModelList);
                        GlobalEvents.liveFavoriteDeleted.postValue(true);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(getClass().getSimpleName(), e.getMessage());
                    }
                });
    }

    @Override
    public void onChanged(Boolean aBoolean) {
        Log.e(getClass().getSimpleName(), "liveFavoriteDetailsChanged");
        loadAll();
    }

    private void updateFavoriteForTrain(Pair<Long, Boolean> trainPair) {
        appDatabase.getFavoriteDao().update(trainPair.first, trainPair.second ? 1 : 0)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        cd.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(FavoriteViewModel.class.getSimpleName(), "Updated");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(FavoriteViewModel.class.getSimpleName(), "Error updating: " + e.getMessage());
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cd.dispose();
        GlobalEvents.liveTrainDetailsChanged.removeObserver(observableTraining);
    }
}