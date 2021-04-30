package uz.xia.ivat.uzbpersiandictionary.ui.training.test;

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
import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import uz.xia.ivat.uzbpersiandictionary.database.AppDatabase;
import uz.xia.ivat.uzbpersiandictionary.database.TrainEntity;
import uz.xia.ivat.uzbpersiandictionary.ui.training.test.adapter.Question;
import uz.xia.ivat.uzbpersiandictionary.util.SingleLiveEvent;

public class TestViewModel extends AndroidViewModel
        implements ITestViewModel {

    private final AppDatabase appDatabase;
    private final CompositeDisposable cd;
    private final MutableLiveData<List<Question>> liveQuestions;
    private final SingleLiveEvent<Boolean> liveSavedResult;

    public TestViewModel(@NonNull Application application) {
        super(application);
        liveQuestions = new MutableLiveData<>();
        liveSavedResult = new SingleLiveEvent<>();
        appDatabase = AppDatabase.getInstance(application.getApplicationContext());
        cd = new CompositeDisposable();
    }

    @Override
    public LiveData<List<Question>> getLiveQuestions() {
        return liveQuestions;
    }

    @Override
    public LiveData<Boolean> getSavedResult() {
        return liveSavedResult;
    }

    @SuppressLint("CheckResult")
    @Override
    public void loadQuestions(boolean isUzbPersian) {
        Single<List<TrainEntity>> entitySingle;
        if (isUzbPersian)
            entitySingle = appDatabase.getTrainDao()
                    .loadAllUzbNoTrain();
        else
            entitySingle = appDatabase.getTrainDao()
                    .loadAllPersianNoTrain();
        Disposable d = entitySingle
                .subscribeOn(Schedulers.io())
                .subscribe(trainList -> parseQuestions(trainList, isUzbPersian),
                        throwable ->
                                Log.e(getClass().getSimpleName(), "loadQuestions() error: " + throwable.getMessage())
                );
        cd.add(d);
    }

    @Override
    public void saveResult(List<Question> correctQuestionList, boolean isUzbekPersian) {
        List<TrainEntity> correctTrainList = new ArrayList<>();
        for (Question question : correctQuestionList) {
            TrainEntity trainEntity = question.getCorrectAnswer();
            if (isUzbekPersian)
                trainEntity.setCorrectUzb(true);
            else
                trainEntity.setCorrectPersian(true);
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

    private void parseQuestions(List<TrainEntity> trainList, boolean isUzbPersian) {
        Single<List<TrainEntity>> single;
        List<Question> questionList = new ArrayList<>();
        AtomicLong indexForId = new AtomicLong(0L);
        for (TrainEntity trainEntity : trainList) {
            if (isUzbPersian)
                single = appDatabase.getTrainDao()
                        .loadRandomUzbPersianNo(trainEntity.getId(), 0);
            else
                single = appDatabase.getTrainDao()
                        .loadRandomPersianUzbNo(trainEntity.getId(), 0);
            Disposable d = single.subscribe(
                    trainEntities ->
                            parseTraining(
                                    indexForId.getAndIncrement(),
                                    trainList.size(),
                                    trainEntity,
                                    trainEntities,
                                    questionList),
                    throwable -> Log.e(getClass().getSimpleName(), "loadRandomUzbPersianNo() error: " + throwable.getMessage())
            );
            cd.add(d);
        }
    }

    private void parseTraining(
            long indexForId,
            int trainingSize, TrainEntity trainEntity,
            List<TrainEntity> trainEntities,
            List<Question> questionList
    ) {
        trainEntities.add(trainEntity);
        Collections.shuffle(trainEntities);
        Question question = new Question(indexForId, trainEntity.getId(), trainEntity, trainEntities);
        questionList.add(question);
        if (indexForId == trainingSize - 1) {
            Collections.shuffle(questionList);
            liveQuestions.postValue(questionList);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cd.dispose();
    }
}
