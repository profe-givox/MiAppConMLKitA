package net.ivanvega.miappconmlkita;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import net.ivanvega.miappconmlkita.databinding.FragmentFirstBinding;

import java.io.File;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private ActivityResultLauncher<Uri> launcherTakePic;
    private Uri uriFoto;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        launcherTakePic = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if(result){
                        procesarFoto();
                    }
                }
                );
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        binding.btnMLF.setOnClickListener(view -> {
            tomarFoto();

        });

        return binding.getRoot();

    }

    private void procesarFoto(){
        Toast.makeText(getActivity(), "Foto tomada",Toast.LENGTH_LONG)
                .show();

        binding.imageView.setImageURI(uriFoto);

    }

    private void tomarFoto() {

        File rutaBase =
                getActivity().getFilesDir();

        File picFoto = new File(rutaBase, "mifoto.jpg");

        uriFoto = FileProvider.getUriForFile(
                getActivity(),
                "net.ivanvega.miappconmlkita.mifileprovider",
                picFoto
        );

        launcherTakePic.launch(uriFoto);

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}