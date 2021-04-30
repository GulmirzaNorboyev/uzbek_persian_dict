package uz.xia.ivat.uzbpersiandictionary.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import uz.xia.ivat.uzbpersiandictionary.BuildConfig;
import uz.xia.ivat.uzbpersiandictionary.R;

import static android.content.Intent.ACTION_VIEW;

public class AboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
    }

    private void setupViews(View view) {
        AppCompatTextView textVersion = view.findViewById(R.id.tv_version);
        textVersion.setText(String.format(getString(R.string.version), BuildConfig.VERSION_NAME));
        AppCompatTextView textTelegram = view.findViewById(R.id.tv_telegram);
        AppCompatTextView textEmail = view.findViewById(R.id.tv_email);
        textTelegram.setOnClickListener(v -> telegram());
        textEmail.setOnClickListener(v -> composeEmail("yusufjasur87@gmail.ru"));
    }

    private void telegram() {
        try {
            Intent intent = new Intent(ACTION_VIEW, Uri.parse("https://t.me/uzb_persian_dictionary"));
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
                startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(requireContext(), R.string.error_telegram_app, Toast.LENGTH_SHORT).show();
        }
    }

    private void composeEmail(String addresses) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", addresses, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        try {
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null)
                startActivity(intent);
        } catch (Exception e) {
            Log.e("ussd", "Error open mail app");
        }
    }
}
