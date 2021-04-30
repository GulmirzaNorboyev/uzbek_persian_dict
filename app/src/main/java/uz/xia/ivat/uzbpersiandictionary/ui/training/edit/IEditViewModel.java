package uz.xia.ivat.uzbpersiandictionary.ui.training.edit;

import androidx.lifecycle.LiveData;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.database.TrainEntity;

public interface IEditViewModel {
    LiveData<List<TrainEntity>> getLiveTrainees();

    void loadAll();

    void removeTraining(Long id);

    void saveTrainees(List<TrainEntity> trainList);
}
