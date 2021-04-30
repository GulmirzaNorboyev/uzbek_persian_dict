package uz.xia.ivat.uzbpersiandictionary.ui.training;

import android.util.Pair;

import androidx.lifecycle.LiveData;

public interface ITrainingViewModel {
    LiveData<Pair<Integer, Integer>> getLiveUzbPersianCount();

    LiveData<Pair<Integer, Integer>> getLivePersianUzbCount();

    LiveData<Pair<Integer, Integer>> getLiveSprintCount();

    LiveData<Pair<Integer, Integer>> getLiveConstructorCount();

    void loadAvailableTraining();
}
