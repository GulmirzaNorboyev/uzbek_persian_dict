package uz.xia.ivat.uzbpersiandictionary.ui.favorite.detail;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import uz.xia.ivat.uzbpersiandictionary.R;
import uz.xia.ivat.uzbpersiandictionary.database.WordFavorite;
import uz.xia.ivat.uzbpersiandictionary.util.GlobalEvents;

public class FavoriteDetailsFragment extends Fragment
        implements CompoundButton.OnCheckedChangeListener {

    public static String KEY_FAVORITE_ID = "KEY_FAVORITE_ID";

    private FavoriteDetailsViewModel wordViewModel;
    private Long favoriteId;
    private AppCompatTextView textWordPersian, textWordUzb, textWordTranscription;
    private AppCompatToggleButton toggleFavorite, toggleTrain;
    private WordFavorite wordFavorite;
    private final Observer<WordFavorite> observerFavorite = wordFavorite -> {
        this.wordFavorite = wordFavorite;
        textWordPersian.setText(wordFavorite.getWordPersian());
        textWordUzb.setText(wordFavorite.getWordUzb());
        textWordTranscription.setText(String.format("[%s]", wordFavorite.getWordTranscription()));
        toggleFavorite.setOnCheckedChangeListener(null);
        toggleFavorite.setChecked(wordFavorite.getFavorite());
        toggleFavorite.setOnCheckedChangeListener(this);
        toggleTrain.setOnCheckedChangeListener(null);
        toggleTrain.setChecked(wordFavorite.getTraining());
        toggleTrain.setOnCheckedChangeListener(this);
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wordViewModel = new ViewModelProvider(this).get(FavoriteDetailsViewModel.class);
        favoriteId = requireArguments().getLong(KEY_FAVORITE_ID);
        wordViewModel.loadFavorite(favoriteId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupObservers();
    }

    private void setupObservers() {
        wordViewModel.getLiveFavorite().observe(getViewLifecycleOwner(), observerFavorite);
    }

    private void setupViews(View view) {
        textWordPersian = view.findViewById(R.id.tv_word_persian);
        textWordUzb = view.findViewById(R.id.tv_word_uzb);
        textWordTranscription = view.findViewById(R.id.tv_word_transcription);
        toggleFavorite = view.findViewById(R.id.tb_fav);
        toggleTrain = view.findViewById(R.id.tb_training);
        toggleTrain.setOnCheckedChangeListener(this);
    }

    private void onFavorite(boolean isChecked, WordFavorite wordEntity) {
        if (isChecked) {
            wordViewModel.saveFavorite(wordEntity);
            wordEntity.setFavorite(true);
            GlobalEvents.liveFavoriteDetailsChanged.postValue(true);
            Toast.makeText(requireContext(), R.string.saved_favorite, Toast.LENGTH_SHORT).show();
        } else {
            wordViewModel.removeFavorite(wordEntity.getId());
            wordEntity.setFavorite(false);
            GlobalEvents.liveFavoriteDetailsChanged.postValue(false);
            Toast.makeText(requireContext(), R.string.removed_favorite, Toast.LENGTH_SHORT).show();
        }
    }

    private void onTrainee(boolean isChecked, WordFavorite wordEntity) {
        if (isChecked) {
            wordViewModel.saveTraining(wordEntity);
            wordEntity.setTraining(true);
            GlobalEvents.liveTrainDetailsChanged.postValue(new Pair<>(wordEntity.getWordId(), true));
            Toast.makeText(requireContext(), R.string.saved_train, Toast.LENGTH_SHORT).show();
        } else {
            wordViewModel.removeTraining(wordEntity.getId());
            wordEntity.setTraining(false);
            GlobalEvents.liveTrainDetailsChanged.postValue(new Pair<>(wordEntity.getWordId(), false));
            Toast.makeText(requireContext(), R.string.removed_favorite, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.tb_fav)
            onFavorite(isChecked, wordFavorite);
        else
            onTrainee(isChecked, wordFavorite);
    }
}
