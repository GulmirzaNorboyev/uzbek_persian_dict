package uz.xia.ivat.uzbpersiandictionary.ui.word.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.R;
import uz.xia.ivat.uzbpersiandictionary.database.WordEntity;
import uz.xia.ivat.uzbpersiandictionary.ui.home.adapter.OnWordClickListener;
import uz.xia.ivat.uzbpersiandictionary.ui.home.adapter.WordDiffer;
import uz.xia.ivat.uzbpersiandictionary.ui.home.adapter.WordViewHolder;

public class DetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final OnWordClickListener wordClickListener;
    private final OnFavoriteTrainChangedListener favoriteTrainChangedListener;
    private final AsyncListDiffer<WordEntity> asyncListDiffer = new AsyncListDiffer<>(this, new WordDiffer());

    public DetailsAdapter(OnWordClickListener wordClickListener, OnFavoriteTrainChangedListener favoriteTrainChangedListener) {
        this.wordClickListener = wordClickListener;
        this.favoriteTrainChangedListener = favoriteTrainChangedListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == 0) {
            view = inflater.inflate(R.layout.item_detail, parent, false);
            return new DetailViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_word, parent, false);
            return new WordViewHolder(view, wordClickListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WordEntity wordEntity = asyncListDiffer.getCurrentList().get(position);
        if (holder instanceof DetailViewHolder)
            ((DetailViewHolder) holder).bind(wordEntity);
        else if (holder instanceof WordViewHolder)
            ((WordViewHolder) holder).bind(wordEntity);
    }

    @Override
    public int getItemCount() {
        return asyncListDiffer.getCurrentList().size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? 0 : 1;
    }

    public void submitList(List<WordEntity> wordList) {
        asyncListDiffer.submitList(wordList);
    }

    public interface OnFavoriteTrainChangedListener {
        void onFavorite(boolean isChecked, WordEntity wordEntity);

        void onTrainee(boolean isChecked, WordEntity wordEntity);
    }

    class DetailViewHolder extends RecyclerView.ViewHolder implements
            CompoundButton.OnCheckedChangeListener {
        private final AppCompatTextView textWordPersian, textWordUzb, textWordTranscription;
        private final AppCompatToggleButton toggleFavorite, toggleTrain;
        private WordEntity wordEntity;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            textWordPersian = itemView.findViewById(R.id.tv_word_persian);
            textWordUzb = itemView.findViewById(R.id.tv_word_uzb);
            textWordTranscription = itemView.findViewById(R.id.tv_word_transcription);
            toggleFavorite = itemView.findViewById(R.id.tb_fav);
            toggleTrain = itemView.findViewById(R.id.tb_training);
        }

        public void bind(WordEntity wordEntity) {
            this.wordEntity = wordEntity;
            textWordPersian.setText(wordEntity.getWordPersian());
            textWordUzb.setText(wordEntity.getWordUzb());
            textWordTranscription.setText(String.format("[%s]", wordEntity.getWordTranscription()));
            toggleFavorite.setOnCheckedChangeListener(null);
            toggleFavorite.setChecked(wordEntity.isFavorite());
            toggleFavorite.setOnCheckedChangeListener(this);
            toggleTrain.setOnCheckedChangeListener(null);
            toggleTrain.setChecked(wordEntity.isTraining());
            toggleTrain.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.tb_fav)
                favoriteTrainChangedListener.onFavorite(isChecked, wordEntity);
            else
                favoriteTrainChangedListener.onTrainee(isChecked, wordEntity);
        }
    }
}
