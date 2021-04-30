package uz.xia.ivat.uzbpersiandictionary.ui.training.construct.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class ConstructDiffer extends DiffUtil.ItemCallback<Construct> {
    @Override
    public boolean areItemsTheSame(@NonNull Construct oldItem, @NonNull Construct newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Construct oldItem, @NonNull Construct newItem) {
        return oldItem.equals(newItem);
    }
}
