package uz.xia.ivat.uzbpersiandictionary.ui.training;

import android.app.Application;
import android.util.Pair;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import uz.xia.ivat.uzbpersiandictionary.database.AppDatabase;

public class TrainingViewModel extends AndroidViewModel
        implements ITrainingViewModel {

    private final AppDatabase appDatabase;
    private final CompositeDisposable cd;
    private final MutableLiveData<Pair<Integer, Integer>> liveUzbPersian;
    private final MutableLiveData<Pair<Integer, Integer>> livePersianUzb;
    private final MutableLiveData<Pair<Integer, Integer>> liveSprint;
    private final MutableLiveData<Pair<Integer, Integer>> liveConstructor;

    public TrainingViewModel(Application app) {
        super(app);
        liveUzbPersian = new MutableLiveData<>();
        livePersianUzb = new MutableLiveData<>();
        liveSprint = new MutableLiveData<>();
        liveConstructor = new MutableLiveData<>();
        appDatabase = AppDatabase.getInstance(app.getApplicationContext());
        cd = new CompositeDisposable();
    }

    @Override
    public LiveData<Pair<Integer, Integer>> getLiveUzbPersianCount() {
        return liveUzbPersian;
    }

    @Override
    public LiveData<Pair<Integer, Integer>> getLivePersianUzbCount() {
        return livePersianUzb;
    }

    @Override
    public LiveData<Pair<Integer, Integer>> getLiveSprintCount() {
        return liveSprint;
    }

    @Override
    public LiveData<Pair<Integer, Integer>> getLiveConstructorCount() {
        return liveConstructor;
    }

    @Override
    public void loadAvailableTraining() {
        loadAvailableCount();
    }

    private void loadAvailableCount() {
        Disposable d = appDatabase.getTrainDao().loadCount()
                .subscribeOn(Schedulers.io())
                .subscribe(count -> {
                    loadUzbPersianCount(count);
                    loadPersianUzbCount(count);
                    loadSprintCount(count);
                    loadConstructCount(count);
                }, throwable -> {
                    loadUzbPersianCount(0);
                    loadPersianUzbCount(0);
                    loadSprintCount(0);
                    loadConstructCount(0);
                });
        cd.add(d);
    }

    private void loadUzbPersianCount(Integer count) {
        Disposable d = appDatabase.getTrainDao().loadUzbPersianCount(0)
                .subscribe(
                        integer -> liveUzbPersian.postValue(new Pair<>(count, integer)),
                        throwable -> liveUzbPersian.postValue(new Pair<>(count, 0))
                );
        cd.add(d);
    }

    private void loadPersianUzbCount(Integer count) {
        Disposable d = appDatabase.getTrainDao().loadPersianUzbCount(0)
                .subscribe(
                        integer -> livePersianUzb.postValue(new Pair<>(count, integer)),
                        throwable -> livePersianUzb.postValue(new Pair<>(count, 0))
                );
        cd.add(d);
    }

    private void loadSprintCount(Integer count) {
        Disposable d = appDatabase.getTrainDao().loadSprintCount(0)
                .subscribe(
                        integer -> liveSprint.postValue(new Pair<>(count, integer)),
                        throwable -> liveSprint.postValue(new Pair<>(count, 0))
                );
        cd.add(d);
    }

    private void loadConstructCount(Integer count) {
        Disposable d = appDatabase.getTrainDao().loadConstructCount(0)
                .subscribe(
                        integer -> liveConstructor.postValue(new Pair<>(count, integer)),
                        throwable -> liveConstructor.postValue(new Pair<>(count, 0))
                );
        cd.add(d);
    }
}