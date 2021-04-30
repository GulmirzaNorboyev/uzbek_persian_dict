package uz.xia.ivat.uzbpersiandictionary.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import uz.xia.ivat.uzbpersiandictionary.R;
import uz.xia.ivat.uzbpersiandictionary.database.WordEntity;
import uz.xia.ivat.uzbpersiandictionary.ui.home.adapter.DictionaryAdapter;
import uz.xia.ivat.uzbpersiandictionary.ui.home.adapter.OnWordClickListener;
import uz.xia.ivat.uzbpersiandictionary.ui.home.adapter.SwipeHelper;
import uz.xia.ivat.uzbpersiandictionary.ui.word.WordFragment;
import uz.xia.ivat.uzbpersiandictionary.util.Utils;

public class HomeFragment extends Fragment
        implements OnWordClickListener,
        MenuItem.OnActionExpandListener {

    private final CompositeDisposable cd = new CompositeDisposable();
    private final RecyclerView.OnScrollListener scrollListener = Utils.scrollListener;
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerDictionary;
    private DictionaryAdapter dictionaryAdapter;
    private final Observer<List<WordEntity>> observerWordList = list ->
            dictionaryAdapter.submitList(list);
    private DividerItemDecoration dividerItemDecoration;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        Log.e(HomeFragment.class.getSimpleName(), "onCreate()");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupObservers();
    }

    private void setupObservers() {
        homeViewModel.getLiveWordList().observe(getViewLifecycleOwner(), observerWordList);
    }

    private void setupViews(View view) {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        recyclerDictionary = view.findViewById(R.id.rv_dictionary);
        if (dictionaryAdapter == null) {
            dictionaryAdapter = new DictionaryAdapter(this);
        }
        recyclerDictionary.setAdapter(dictionaryAdapter);
        initializeSwipeHelper();
        recyclerDictionary.setHasFixedSize(true);
        if (dividerItemDecoration == null)
            dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerDictionary.addItemDecoration(dividerItemDecoration);
        recyclerDictionary.addOnScrollListener(scrollListener);
    }

    private void initializeSwipeHelper() {
        new SwipeHelper(requireContext(), recyclerDictionary) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new UnderlayButton(
                        requireContext(),
                        "Training",
                        R.drawable.ic_dumbell_unchecked_white,
                        Color.parseColor("#FF018786"),
                        pos -> {
                            WordEntity wordEntity = dictionaryAdapter.getWordEntity(pos);
                            if (wordEntity.isTraining()) {
                                homeViewModel.removeTraining(wordEntity.getId());
                                wordEntity.setTraining(false);
                                Toast.makeText(requireContext(), R.string.removed_train, Toast.LENGTH_SHORT).show();
                            } else {
                                homeViewModel.saveTraining(wordEntity);
                                wordEntity.setTraining(true);
                                Toast.makeText(requireContext(), R.string.saved_train, Toast.LENGTH_SHORT).show();
                            }
                            dictionaryAdapter.notifyItemChanged(pos);
                        }
                ));

                underlayButtons.add(new UnderlayButton(
                        requireContext(),
                        "Favorite",
                        R.drawable.ic_baseline_star_white,
                        Color.parseColor("#FF2196F3"),
                        pos -> {
                            WordEntity wordEntity = dictionaryAdapter.getWordEntity(pos);
                            if (wordEntity.isFavorite()) {
                                homeViewModel.removeFavorite(wordEntity.getId());
                                wordEntity.setFavorite(false);
                                Toast.makeText(requireContext(), R.string.removed_favorite, Toast.LENGTH_SHORT).show();
                            } else {
                                homeViewModel.saveFavorite(wordEntity);
                                wordEntity.setFavorite(true);
                                Toast.makeText(requireContext(), R.string.saved_favorite, Toast.LENGTH_SHORT).show();
                            }
                            dictionaryAdapter.notifyItemChanged(pos);
                        }
                ));
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        String lastQueryString = homeViewModel.getLastQuery();
        menuItem.setOnActionExpandListener(this);
        if (!lastQueryString.isEmpty()) {
            searchView.onActionViewExpanded();
            menuItem.expandActionView();
            searchView.setIconified(false);
            searchView.clearFocus();
            searchView.setQuery(lastQueryString, false);
        }

        Disposable searchViewDisposable = Observable.create((ObservableOnSubscribe<String>) emitter ->
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        emitter.onNext(newText);
                        return true;
                    }
                }))
                .subscribeOn(AndroidSchedulers.mainThread())
                .skip(1)
                .map(String::toLowerCase)
                .debounce(250, TimeUnit.MILLISECONDS)
                .subscribe(query -> {
                    Log.e(HomeFragment.class.getSimpleName(), "query-> " + query);
                    if (query.length() > 2)
                        homeViewModel.search(query);
                    else if (query.isEmpty()) {
                        if (homeViewModel.getLastQuery().isEmpty()) {
                            homeViewModel.clearSearching();
                            homeViewModel.loadAllWords();
                        }
                    }
                });
        cd.add(searchViewDisposable);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onWordClicked(int position, WordEntity word) {
        Utils.hideKeyboard(requireView());
        Bundle args = new Bundle();
        args.putParcelable(WordFragment.KEY_WORD_ENTITY, word);
        navController.navigate(R.id.nav_details, args);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        homeViewModel.loadAllWords();
        homeViewModel.clearSearching();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cd.dispose();
    }
}