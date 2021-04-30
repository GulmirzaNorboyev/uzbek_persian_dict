package uz.xia.ivat.uzbpersiandictionary.ui.word;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.R;
import uz.xia.ivat.uzbpersiandictionary.database.WordEntity;
import uz.xia.ivat.uzbpersiandictionary.ui.home.adapter.OnWordClickListener;
import uz.xia.ivat.uzbpersiandictionary.ui.word.adapter.DetailsAdapter;

public class WordFragment extends Fragment implements
        DetailsAdapter.OnFavoriteTrainChangedListener,
        OnWordClickListener {

    public static String KEY_WORD_ENTITY = "KEY_WORD_ENTITY";
    private RecyclerView recyclerWord;
    private DetailsAdapter detailsAdapter;
    private DividerItemDecoration dividerItemDecoration;
    private WordViewModel wordViewModel;
    private WordEntity wordEntity;
    private final Observer<List<WordEntity>> observerWordList = list -> {
        list.add(0, wordEntity);
        detailsAdapter.submitList(list);
    };
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        wordEntity = requireArguments().getParcelable(KEY_WORD_ENTITY);
        wordViewModel.loadLikedWords(wordEntity);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_word, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupObservers();
    }

    private void setupObservers() {
        wordViewModel.getLiveWordList().observe(getViewLifecycleOwner(), observerWordList);
    }

    private void setupViews(View view) {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        recyclerWord = view.findViewById(R.id.rv_word);
        if (detailsAdapter == null)
            detailsAdapter = new DetailsAdapter(this, this);
        recyclerWord.setAdapter(detailsAdapter);
        if (dividerItemDecoration == null)
            dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerWord.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onWordClicked(int position, WordEntity word) {
        if (word == null || word.getWordPersian() == null)
            return;
        Bundle args = new Bundle();
        args.putParcelable(KEY_WORD_ENTITY, word);
        navController.navigate(R.id.nav_details, args);
    }

    @Override
    public void onFavorite(boolean isChecked, WordEntity wordEntity) {
        if (isChecked) {
            wordViewModel.saveFavorite(wordEntity);
            wordEntity.setFavorite(true);
            Toast.makeText(requireContext(), R.string.saved_favorite, Toast.LENGTH_SHORT).show();
        } else {
            wordViewModel.removeFavorite(wordEntity.getId());
            wordEntity.setFavorite(false);
            Toast.makeText(requireContext(), R.string.removed_favorite, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTrainee(boolean isChecked, WordEntity wordEntity) {
        if (isChecked) {
            wordViewModel.saveTraining(wordEntity);
            wordEntity.setTraining(true);
            Toast.makeText(requireContext(), R.string.saved_train, Toast.LENGTH_SHORT).show();
        } else {
            wordViewModel.removeTraining(wordEntity.getId());
            wordEntity.setTraining(false);
            Toast.makeText(requireContext(), R.string.removed_train, Toast.LENGTH_SHORT).show();
        }
    }
}
