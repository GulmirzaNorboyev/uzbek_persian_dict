package uz.xia.ivat.uzbpersiandictionary.ui.word;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import uz.xia.ivat.uzbpersiandictionary.database.AppDatabase;
import uz.xia.ivat.uzbpersiandictionary.database.TrainEntity;
import uz.xia.ivat.uzbpersiandictionary.database.WordEntity;
import uz.xia.ivat.uzbpersiandictionary.database.WordFavorite;

public class WordViewModel extends AndroidViewModel
        implements IWordViewModel {

    private final MutableLiveData<List<WordEntity>> liveWordList;
    private final AppDatabase appDatabase;
    private final CompositeDisposable cd;

    public WordViewModel(@NonNull Application app) {
        super(app);
        liveWordList = new MutableLiveData<>();
        appDatabase = AppDatabase.getInstance(app.getApplicationContext());
        cd = new CompositeDisposable();
    }

    @Override
    public LiveData<List<WordEntity>> getLiveWordList() {
        return liveWordList;
    }

    @Override
    public void loadLikedWords(WordEntity wordEntity) {
        Disposable trainDisposable = appDatabase.getTrainDao().load(wordEntity.getId())
                .subscribeOn(Schedulers.io())
                .subscribe(trainEntity -> {
                    wordEntity.setTraining(true);
                    checkFavorite(wordEntity);
                }, throwable -> {
                    wordEntity.setTraining(false);
                    checkFavorite(wordEntity);
                });
        cd.add(trainDisposable);
    }

    @Override
    public void checkFavorite(WordEntity wordEntity) {
        Disposable favDisposable = appDatabase.getFavoriteDao().load(wordEntity.getId())
                .subscribe(favorite -> {
                    wordEntity.setFavorite(true);
                    loadWords(wordEntity, wordEntity.getWordUzb());
                }, throwable -> {
                    wordEntity.setFavorite(false);
                    loadWords(wordEntity, wordEntity.getWordUzb());
                });
        cd.add(favDisposable);
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
                        Log.e(WordViewModel.this.getClass().getSimpleName(), "Deteted favorite");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(WordViewModel.this.getClass().getSimpleName(), e.getMessage());
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
                        Log.e(WordViewModel.this.getClass().getSimpleName(), "Success saved!");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(WordViewModel.this.getClass().getSimpleName(), e.getMessage());
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
                        Log.e(WordViewModel.this.getClass().getSimpleName(), "Deteted favorite");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(WordViewModel.this.getClass().getSimpleName(), e.getMessage());
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
                        Log.e(WordViewModel.this.getClass().getSimpleName(), "Success saved!");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(WordViewModel.this.getClass().getSimpleName(), e.getMessage());
                    }
                });
    }

    private void loadWords(WordEntity wordEntity, String query) {
        Disposable d = appDatabase.getWordDao().loadLikelyCyrillicNo(wordEntity.getId(), "%" + query.toLowerCase() + "%")
                .subscribe(values -> {
                            values.add(0, wordEntity);
                            liveWordList.postValue(values);
                        },
                        throwable -> {
                            Log.e(getClass().getSimpleName(), "Error like: " + throwable.getMessage());
                            List<WordEntity> wordEntities = new ArrayList<>();
                            wordEntities.add(0, wordEntity);
                            liveWordList.postValue(wordEntities);
                        });
        cd.add(d);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cd.dispose();
    }
}
