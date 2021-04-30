package uz.xia.ivat.uzbpersiandictionary.ui.favorite.detail;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import uz.xia.ivat.uzbpersiandictionary.database.AppDatabase;
import uz.xia.ivat.uzbpersiandictionary.database.WordFavorite;
import uz.xia.ivat.uzbpersiandictionary.util.GlobalEvents;

public class FavoriteDetailsViewModel extends AndroidViewModel
        implements IFavoriteDetailsViewModel {

    private final MutableLiveData<WordFavorite> liveWordList;
    private final AppDatabase appDatabase;
    private final CompositeDisposable cd;

    public FavoriteDetailsViewModel(@NonNull Application app) {
        super(app);
        liveWordList = new MutableLiveData<>();
        appDatabase = AppDatabase.getInstance(app.getApplicationContext());
        cd = new CompositeDisposable();
    }

    @Override
    public LiveData<WordFavorite> getLiveFavorite() {
        return liveWordList;
    }

    @Override
    public void loadFavorite(Long id) {
        Disposable d = appDatabase.getFavoriteDao().load(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(liveWordList::postValue,
                        throwable -> {
                            Log.e(getClass().getSimpleName(), "Error like: " + throwable.getMessage());
                            liveWordList.postValue(null);
                        });
        cd.add(d);
    }

    @Override
    public void removeFavorite(Long id) {
        appDatabase.getFavoriteDao().delete(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        cd.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(FavoriteDetailsViewModel.this.getClass().getSimpleName(), "Deteted favorite");
                        GlobalEvents.liveFavoriteChanged.postValue(new Pair<>(id, false));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(FavoriteDetailsViewModel.this.getClass().getSimpleName(), e.getMessage());
                    }
                });
    }

    @Override
    public void saveFavorite(WordFavorite wordEntity) {
        WordFavorite favorite = new WordFavorite(
                wordEntity.getId(),
                wordEntity.getId(),
                wordEntity.getWordUzb(),
                wordEntity.getWordPersian(),
                wordEntity.getWordTranscription(),
                true,
                wordEntity.getTraining()
        );
        appDatabase.getFavoriteDao()
                .save(favorite)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        cd.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(FavoriteDetailsViewModel.this.getClass().getSimpleName(), "Success saved!");
                        GlobalEvents.liveFavoriteChanged.postValue(new Pair<>(wordEntity.getWordId(), true));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(FavoriteDetailsViewModel.this.getClass().getSimpleName(), e.getMessage());
                    }
                });
    }

    @Override
    public void removeTraining(Long id) {
        appDatabase.getTrainDao().delete(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        cd.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(FavoriteDetailsViewModel.this.getClass().getSimpleName(), "Deteted favorite");
                        GlobalEvents.liveTrainChanged.postValue(new Pair<>(id, false));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(FavoriteDetailsViewModel.this.getClass().getSimpleName(), e.getMessage());
                    }
                });
    }

    @Override
    public void saveTraining(WordFavorite wordEntity) {
        WordFavorite favorite = new WordFavorite(
                wordEntity.getId(),
                wordEntity.getId(),
                wordEntity.getWordUzb(),
                wordEntity.getWordPersian(),
                wordEntity.getWordTranscription(),
                true,
                wordEntity.getTraining()
        );
        appDatabase.getFavoriteDao()
                .save(favorite)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        cd.add(d);
                    }

                    @Override
                    public void onComplete() {
                        Log.e(FavoriteDetailsViewModel.this.getClass().getSimpleName(), "Success saved!");
                        GlobalEvents.liveTrainChanged.postValue(new Pair<>(wordEntity.getWordId(), true));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(FavoriteDetailsViewModel.this.getClass().getSimpleName(), e.getMessage());
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cd.dispose();
    }
}
