package uz.xia.ivat.uzbpersiandictionary.ui.training.edit;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import uz.xia.ivat.uzbpersiandictionary.database.AppDatabase;
import uz.xia.ivat.uzbpersiandictionary.database.TrainEntity;

public class EditViewModel extends AndroidViewModel implements
        IEditViewModel {

    private final MutableLiveData<List<TrainEntity>> liveTrainList;
    private final AppDatabase appDatabase;
    private final CompositeDisposable cd;

    public EditViewModel(Application app) {
        super(app);
        liveTrainList = new MutableLiveData<>();
        appDatabase = AppDatabase.getInstance(app.getApplicationContext());
        cd = new CompositeDisposable();
    }

    @Override
    public LiveData<List<TrainEntity>> getLiveTrainees() {
        return liveTrainList;
    }

    @Override
    public void loadAll() {
        Disposable d = appDatabase.getTrainDao()
                .loadAll()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(
                        liveTrainList::postValue,
                        throwable -> {
                            Log.e(getClass().getSimpleName(), "Error load: " + throwable.getMessage());
                            liveTrainList.postValue(Collections.emptyList());
                        }
                );
        cd.add(d);
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
                        Log.e(EditViewModel.this.getClass().getSimpleName(), "Deteted favorite");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(EditViewModel.this.getClass().getSimpleName(), e.getMessage());
                    }
                });
    }

    @Override
    public void saveTrainees(List<TrainEntity> trainList) {
        appDatabase.getTrainDao().saveAll(trainList)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cd.dispose();
    }
}