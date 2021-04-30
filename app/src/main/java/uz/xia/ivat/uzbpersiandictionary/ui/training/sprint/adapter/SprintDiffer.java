package uz.xia.ivat.uzbpersiandictionary.ui.training.sprint.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class SprintDiffer extends DiffUtil.ItemCallback<Sprint> {
    @Override
    public boolean areItemsTheSame(@NonNull Sprint oldItem, @NonNull Sprint newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Sprint oldItem, @NonNull Sprint newItem) {
        return oldItem.equals(newItem);
    }
}
