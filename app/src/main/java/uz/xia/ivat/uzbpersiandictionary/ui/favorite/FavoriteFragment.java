package uz.xia.ivat.uzbpersiandictionary.ui.favorite;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.R;
import uz.xia.ivat.uzbpersiandictionary.database.WordFavorite;
import uz.xia.ivat.uzbpersiandictionary.ui.favorite.adapter.FavoriteAdapter;
import uz.xia.ivat.uzbpersiandictionary.ui.favorite.adapter.OnFavoriteListener;
import uz.xia.ivat.uzbpersiandictionary.ui.home.adapter.SwipeHelper;

import static uz.xia.ivat.uzbpersiandictionary.ui.favorite.detail.FavoriteDetailsFragment.KEY_FAVORITE_ID;

public class FavoriteFragment extends Fragment
        implements OnFavoriteListener {

    private FavoriteViewModel favoriteViewModel;
    private FavoriteAdapter favoriteAdapter;
    private final Observer<List<WordFavorite>> observerFavorites = wordFavorites ->
            favoriteAdapter.submitList(wordFavorites);
    private RecyclerView recyclerFavorite;
    private DividerItemDecoration itemDecoration;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        favoriteViewModel.loadAll();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupObservers();
    }

    private void setupObservers() {
        favoriteViewModel.getLiveFavorites().observe(getViewLifecycleOwner(), observerFavorites);
    }

    private void setupViews(View view) {
        recyclerFavorite = view.findViewById(R.id.rv_favorite);
        if (favoriteAdapter == null)
            favoriteAdapter = new FavoriteAdapter(this);
        recyclerFavorite.setAdapter(favoriteAdapter);
        if (itemDecoration == null)
            itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerFavorite.addItemDecoration(itemDecoration);
        initializeSwipeHelper();
        recyclerFavorite.setHasFixedSize(true);
        if (navController == null)
            navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    private void initializeSwipeHelper() {
        new SwipeHelper(requireContext(), recyclerFavorite) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new UnderlayButton(
                        requireContext(),
                        "Training",
                        R.drawable.ic_dumbell_unchecked_white,
                        Color.parseColor("#FF018786"),
                        pos -> {
                            WordFavorite favorite = favoriteAdapter.getFavoriteEntity(pos);
                            if (favorite.getTraining()) {
                                favoriteViewModel.removeTraining(favorite.getWordId());
                                favorite.setTraining(false);
                                Toast.makeText(requireContext(), R.string.removed_train, Toast.LENGTH_SHORT).show();
                            } else {
                                favoriteViewModel.saveTraining(favorite);
                                favorite.setTraining(true);
                                Toast.makeText(requireContext(), R.string.saved_train, Toast.LENGTH_SHORT).show();
                            }
                            favoriteAdapter.notifyItemChanged(pos);
                        }
                ));

                underlayButtons.add(new UnderlayButton(
                        requireContext(),
                        "Delete",
                        R.drawable.ic_baseline_delete,
                        Color.parseColor("#FFF4511E"),
                        pos -> {
                            WordFavorite wordEntity = favoriteAdapter.getFavoriteEntity(pos);
                            favoriteViewModel.removeFavorite(wordEntity.getWordId());
                            Toast.makeText(requireContext(), R.string.removed_favorite, Toast.LENGTH_SHORT).show();
                            favoriteAdapter.removeFavorite(pos);
                        }
                ));
            }
        };
    }

    @Override
    public void onFavoriteClicked(int position, WordFavorite favorite) {
        Bundle args = new Bundle();
        args.putLong(KEY_FAVORITE_ID, favorite.getWordId());
        navController.navigate(R.id.nav_favorite_details, args);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.favorite_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_clear)
            if (favoriteAdapter.getItemCount() > 0)
                showDeleteDialog();
            else
                Toast.makeText(
                        requireContext(),
                        R.string.already_empty_favorite,
                        Toast.LENGTH_SHORT)
                        .show();
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_warning)
                .setMessage(R.string.title_warning_all_favorite)
                .setPositiveButton(R.string.answer_yes, (dialog, which) ->
                        favoriteViewModel.removeAll())
                .setNegativeButton(R.string.answer_no, null);
        builder.create();
        builder.show();
    }
}