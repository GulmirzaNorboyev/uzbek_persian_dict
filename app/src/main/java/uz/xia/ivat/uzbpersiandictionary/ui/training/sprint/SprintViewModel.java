package uz.xia.ivat.uzbpersiandictionary.ui.training.sprint;

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
import uz.xia.ivat.uzbpersiandictionary.ui.training.sprint.adapter.Sprint;
import uz.xia.ivat.uzbpersiandictionary.util.SingleLiveEvent;

public class SprintViewModel extends AndroidViewModel
        implements ISprintViewModel {

    private final AppDatabase appDatabase;
    private final CompositeDisposable cd;
    private final MutableLiveData<List<Sprint>> liveQuestions;
    private final SingleLiveEvent<Boolean> liveSavedResult;

    public SprintViewModel(@NonNull Application application) {
        super(application);
        liveQuestions = new MutableLiveData<>();
        liveSavedResult = new SingleLiveEvent<>();
        appDatabase = AppDatabase.getInstance(application.getApplicationContext());
        cd = new CompositeDisposable();
    }

    @Override
    public LiveData<List<Sprint>> getLiveQuestions() {
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
                .loadAllSprintNoTrain()
                .subscribeOn(Schedulers.io())
                .subscribe(this::parseSprints,
                        throwable ->
                                Log.e(getClass().getSimpleName(), "loadQuestions() error: " + throwable.getMessage())
                );
        cd.add(d);
    }

    @Override
    public void saveResult(List<Sprint> correctQuestionList) {
        List<TrainEntity> correctTrainList = new ArrayList<>();
        for (Sprint question : correctQuestionList) {
            TrainEntity trainEntity = question.getCorrectAnswer();
            trainEntity.setCorrectSprint(true);
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

    private void parseSprints(List<TrainEntity> trainList) {
        Single<TrainEntity> single;
        List<Sprint> sprintList = new ArrayList<>();
        AtomicLong indexForId = new AtomicLong(0L);
        for (TrainEntity trainEntity : trainList) {
            single = appDatabase.getTrainDao()
                    .loadRandomSprintNo(trainEntity.getId(), 0);

            Disposable d = single.subscribe(
                    trainEntitySecond ->
                            parseTraining(
                                    indexForId.getAndIncrement(),
                                    trainList.size(),
                                    trainEntity,
                                    trainEntitySecond,
                                    sprintList),
                    throwable ->
                            Log.e(getClass().getSimpleName(), "loadRandomUzbPersianNo() error: " + throwable.getMessage())
            );
            cd.add(d);
        }
    }

    private void parseTraining(
            long indexForId,
            int trainingSize,
            TrainEntity trainEntity,
            TrainEntity inCorrectTrainEntity,
            List<Sprint> questionList
    ) {
        Sprint sprint = new Sprint(
                indexForId,
                trainEntity.getId(),
                inCorrectTrainEntity.getId(),
                trainEntity,
                inCorrectTrainEntity,
                false);
        questionList.add(sprint);
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
