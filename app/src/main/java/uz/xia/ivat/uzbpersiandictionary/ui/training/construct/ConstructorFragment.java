package uz.xia.ivat.uzbpersiandictionary.ui.training.construct;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import uz.xia.ivat.uzbpersiandictionary.R;
import uz.xia.ivat.uzbpersiandictionary.ui.training.construct.adapter.Construct;
import uz.xia.ivat.uzbpersiandictionary.ui.training.construct.adapter.ConstructorAdapter;

public class ConstructorFragment extends Fragment
        implements ConstructorAdapter.OnAnswerListener,
        Observer<Boolean>,
        Runnable {

    private ViewPager2 viewPagerQuestions;
    private ConstructorAdapter constructorAdapter;
    private final Observer<List<Construct>> observerQuestionList = questions -> {
        Log.e(getClass().getSimpleName(), "size: " + questions.size());
        constructorAdapter.submitList(questions);
    };
    private ConstructorViewModel constructorViewModel;
    private NavController navController;
    private ObjectAnimator correctColorAnimator, inCorrectColorAnimator, correctEndAnimator, inCorrectEndAnimator;
    private Handler handler;
    private boolean isCorrect = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        constructorViewModel = new ViewModelProvider(this).get(ConstructorViewModel.class);
        constructorViewModel.loadQuestions();
        handler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_construct, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        setupObservers();
    }

    private void setupObservers() {
        constructorViewModel.getLiveQuestions().observe(getViewLifecycleOwner(), observerQuestionList);
        constructorViewModel.getSavedResult().observe(getViewLifecycleOwner(), this);
    }

    private void setupViews(View view) {
        viewPagerQuestions = view.findViewById(R.id.vp_construct);
        viewPagerQuestions.setUserInputEnabled(false);
        if (constructorAdapter == null)
            constructorAdapter = new ConstructorAdapter(this);
        viewPagerQuestions.setAdapter(constructorAdapter);
        if (navController == null)
            navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        correctColorAnimator = ObjectAnimator.ofObject(
                viewPagerQuestions,
                "backgroundColor",
                new ArgbEvaluator(),
                0xFFFFFFFF,
                0xFF1EFF67);
        inCorrectColorAnimator = ObjectAnimator.ofObject(
                viewPagerQuestions,
                "backgroundColor",
                new ArgbEvaluator(),
                0xFFFFFFFF,
                0xFFFF5656);
        correctEndAnimator = ObjectAnimator.ofObject(
                viewPagerQuestions,
                "backgroundColor",
                new ArgbEvaluator(),
                0xFF2196F3,
                0xFFD5E1DC);
        inCorrectEndAnimator = ObjectAnimator.ofObject(
                viewPagerQuestions,
                "backgroundColor",
                new ArgbEvaluator(),
                0xFFA0FFBF,
                0xFFFDD6D6);
    }

    @SuppressLint("StringFormatMatches")
    private void showResultDialog() {
        List<Construct> correctQuestionList = constructorAdapter.getCorrectQuestions();
        int correctCount = correctQuestionList.size();
        int testSize = constructorAdapter.getItemCount();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_result_construct)
                .setMessage(String.format(getString(R.string.message_construct_result), testSize, correctCount))
                .setPositiveButton(R.string.answer_ok, (dialog, which) -> {
                    if (testSize > 0)
                        constructorViewModel.saveResult(correctQuestionList);
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

    @Override
    public void onNext(int position) {
        viewPagerQuestions.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white));
        viewPagerQuestions.setCurrentItem(viewPagerQuestions.getCurrentItem() + 1, false);
    }

    @Override
    public void onAnswered(boolean isCorrect) {
        this.isCorrect = isCorrect;
        if (isCorrect)
            correctColorAnimator.start();
        else
            inCorrectColorAnimator.start();
        handler.postDelayed(this, 300);
    }

    @Override
    public void onFinish() {
        showResultDialog();
    }

    @Override
    public void run() {
        if (isCorrect)
            correctEndAnimator.start();
        else
            inCorrectEndAnimator.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(this);
    }
}
