package uz.xia.ivat.uzbpersiandictionary.ui.training.edit.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import uz.xia.ivat.uzbpersiandictionary.database.TrainEntity;

public class TrainingDiffer extends DiffUtil.ItemCallback<TrainEntity> {
    @Override
    public boolean areItemsTheSame(@NonNull TrainEntity oldItem, @NonNull TrainEntity newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull TrainEntity oldItem, @NonNull TrainEntity newItem) {
        return oldItem.equals(newItem);
    }
}
