package uz.xia.ivat.uzbpersiandictionary.ui.training.test.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class TrainDiffer extends DiffUtil.ItemCallback<Question> {
    @Override
    public boolean areItemsTheSame(@NonNull Question oldItem, @NonNull Question newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Question oldItem, @NonNull Question newItem) {
        return oldItem.equals(newItem);
    }
}
