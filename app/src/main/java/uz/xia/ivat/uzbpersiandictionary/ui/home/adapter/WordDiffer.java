package uz.xia.ivat.uzbpersiandictionary.ui.home.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import uz.xia.ivat.uzbpersiandictionary.database.WordEntity;

public class WordDiffer extends DiffUtil.ItemCallback<WordEntity> {
    @Override
    public boolean areItemsTheSame(@NonNull WordEntity oldItem, @NonNull WordEntity newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull WordEntity oldItem, @NonNull WordEntity newItem) {
        return oldItem.equals(newItem);
    }
};
