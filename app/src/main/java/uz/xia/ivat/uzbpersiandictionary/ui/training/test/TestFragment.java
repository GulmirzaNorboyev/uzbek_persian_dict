package uz.xia.ivat.uzbpersiandictionary.ui.training.test;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.R;
import uz.xia.ivat.uzbpersiandictionary.ui.training.test.adapter.Question;
import uz.xia.ivat.uzbpersiandictionary.ui.training.test.adapter.QuestionAdapter;

public class TestFragment extends Fragment
        implements QuestionAdapter.OnAnswerListener,
        Observer<Boolean> {

    public static final String KEY_HAS_UZB_PERSIAN = "KEY_HAS_UZB_PERSIAN";
    private boolean isUzbekPersian = false;
    private ViewPager2 viewPagerQuestions;
    private QuestionAdapter questionAdapter;
    private final Observer<List<Question>> observerQuestionList = questions ->
            questionAdapter.submitList(questions);
    private TestViewModel testViewModel;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        testViewModel = new ViewModelProvider(this).get(TestViewModel.class);
        Bundle args = requireArguments();
        isUzbekPersian = args.getBoolean(KEY_HAS_UZB_PERSIAN);
        testViewModel.loadQuestions(isUzbekPersian);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupObservers();
    }

    private void setupObservers() {
        testViewModel.getLiveQuestions().observe(getViewLifecycleOwner(), observerQuestionList);
        testViewModel.getSavedResult().observe(getViewLifecycleOwner(), this);
    }

    private void setupViews(View view) {
        viewPagerQuestions = view.findViewById(R.id.vp_questions);
        viewPagerQuestions.setUserInputEnabled(false);
        if (questionAdapter == null)
            questionAdapter = new QuestionAdapter(isUzbekPersian, this);
        viewPagerQuestions.setAdapter(questionAdapter);
        if (navController == null)
            navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    @Override
    public void onNext(int position) {
        if (position != questionAdapter.getItemCount() - 1)
            viewPagerQuestions.setCurrentItem(viewPagerQuestions.getCurrentItem() + 1);
        else
            showResultDialog();
    }

    @SuppressLint("StringFormatMatches")
    private void showResultDialog() {
        List<Question> correctQuestionList = questionAdapter.getCorrectQuestions();
        int correctCount = correctQuestionList.size();
        int testSize = questionAdapter.getItemCount();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_result_test)
                .setMessage(String.format(getString(R.string.message_test_result), testSize, correctCount))
                .setPositiveButton(R.string.answer_ok, (dialog, which) -> {
                    if (testSize > 0)
                        testViewModel.saveResult(correctQuestionList, isUzbekPersian);
                    else
                        navController.popBackStack();
                })
                .setCancelable(false);
        builder.create().show();
    }

    @Override
    public void onChanged(Boolean b) {
        navController.popBackStack();
    }
}
