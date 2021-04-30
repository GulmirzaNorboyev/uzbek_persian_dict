package uz.xia.ivat.uzbpersiandictionary.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.R;
import uz.xia.ivat.uzbpersiandictionary.database.WordEntity;

public class DictionaryAdapter extends RecyclerView.Adapter<WordViewHolder> {

    private final AsyncListDiffer<WordEntity> asyncListDiffer;
    private final OnWordClickListener wordClickListener;

    public DictionaryAdapter(
            OnWordClickListener wordClickListener
    ) {
        this.wordClickListener = wordClickListener;
        asyncListDiffer = new AsyncListDiffer<>(this, new WordDiffer());
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view, wordClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        WordEntity wordEntity = asyncListDiffer.getCurrentList().get(position);
        holder.bind(wordEntity);
    }

    @Override
    public int getItemCount() {
        return asyncListDiffer.getCurrentList().size();
    }

    public void submitList(List<WordEntity> wordList) {
        asyncListDiffer.submitList(wordList);
    }

    public WordEntity getWordEntity(int position) {
        return asyncListDiffer.getCurrentList().get(position);
    }
}
