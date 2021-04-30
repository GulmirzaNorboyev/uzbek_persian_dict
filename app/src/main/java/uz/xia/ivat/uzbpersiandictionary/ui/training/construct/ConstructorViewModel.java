package uz.xia.ivat.uzbpersiandictionary.ui.training.construct;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import uz.xia.ivat.uzbpersiandictionary.database.AppDatabase;
import uz.xia.ivat.uzbpersiandictionary.database.TrainEntity;
import uz.xia.ivat.uzbpersiandictionary.ui.training.construct.adapter.Construct;
import uz.xia.ivat.uzbpersiandictionary.util.SingleLiveEvent;

public class ConstructorViewModel extends AndroidViewModel
        implements IConstructViewModel {

    private final AppDatabase appDatabase;
    private final CompositeDisposable cd;
    private final MutableLiveData<List<Construct>> liveQuestions;
    private final SingleLiveEvent<Boolean> liveSavedResult;

    public ConstructorViewModel(@NonNull Application application) {
        super(application);
        liveQuestions = new MutableLiveData<>();
        liveSavedResult = new SingleLiveEvent<>();
        appDatabase = AppDatabase.getInstance(application.getApplicationContext());
        cd = new CompositeDisposable();
    }

    @Override
    public LiveData<List<Construct>> getLiveQuestions() {
        return liveQuestions;
    }

    @Override
    public LiveData<Boolean> getSavedResult() {
        return liveSavedResult;
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadQuestions() {
        Disposable d = appDatabase.getTrainDao()
                .loadAllConstructNoTrain()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::parseQuestions,
                        throwable ->
                                Log.e(getClass().getSimpleName(), "loadQuestions() error: " + throwable.getMessage())
                );
        cd.add(d);
    }

    @Override
    public void saveResult(List<Construct> correctQuestionList) {
        List<TrainEntity> correctTrainList = new ArrayList<>();
        for (Construct question : correctQuestionList) {
            TrainEntity trainEntity = question.getCorrectAnswer();
            trainEntity.setCorrectConstruct(true);
            correctTrainList.add(trainEntity);
        }
        appDatabase.getTrainDao()
                .saveCorrectUzbPersian(correctTrainList)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        cd.add(d);
                    }

                    @Override
                    public void onComplete() {
                        liveSavedResult.postValue(true);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e(getClass().getSimpleName(), "loadRandomUzbPersianNo() error: " + e.getMessage());
                    }
                });
    }

    private void parseQuestions(List<TrainEntity> trainList) {
        List<Construct> questionList = new ArrayList<>();
        for (int i = 0; i < trainList.size(); i++)
            questionList.add(new Construct((long) i, trainList.get(i), false));
        Collections.shuffle(questionList);
        liveQuestions.postValue(questionList);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cd.dispose();
    }
}
