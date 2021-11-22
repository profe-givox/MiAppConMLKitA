package net.ivanvega.miappconmlkita;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceLandmark;

import net.ivanvega.miappconmlkita.databinding.FragmentFirstBinding;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private ActivityResultLauncher<Uri> launcherTakePic;
    private Uri uriFoto;
    private Context ctx;
    private InputImage fotoProcess;
    private GraphicOverlay mGraphicOverlay;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.ctx = context;
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
        mGraphicOverlay = binding.graphicOverlay;
        binding.btnMLF.setOnClickListener(view -> {
            tomarFoto();

        });

        return binding.getRoot();

    }

    private void procesarFoto(){
        Toast.makeText(getActivity(), "Foto tomada",Toast.LENGTH_LONG)
                .show();

        binding.imageView.setImageURI(uriFoto);

        try {
             fotoProcess = InputImage.fromFilePath(this.ctx,
                    this.uriFoto);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FaceDetector  faceDetector =
                FaceDetection.getClient();

        faceDetector.process(this.fotoProcess).
                addOnSuccessListener(
                        new OnSuccessListener<List<Face>>() {
                            @Override
                            public void onSuccess(@NonNull List<Face> faces) {
                                //processFaces(faces);
                                processFaceContourDetectionResult(faces);
                            }
                        }
                ).
        addOnFailureListener(e -> {
            e.printStackTrace();
        })
        ;

    }

    private void processFaceContourDetectionResult(List<Face> faces) {
        // Replace with code from the codelab to process the face contour detection result.
        // Task completed successfully
        if (faces.size() == 0) {
            //showToast("No face found");
            return;
        }
        mGraphicOverlay.clear();
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.get(i);
            FaceContourGraphic faceGraphic = new FaceContourGraphic(mGraphicOverlay);
            mGraphicOverlay.add(faceGraphic);
            faceGraphic.updateFace(face);
        }
    }

    private void processFaces(List<Face> faces) {
        for (Face face : faces) {
            Rect bounds = face.getBoundingBox();
            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
            // nose available):
            FaceLandmark leftEar = face.getLandmark(FaceLandmark.LEFT_EAR);
            if (leftEar != null) {
                PointF leftEarPos = leftEar.getPosition();
            }

            // If contour detection was enabled:
            List<PointF> leftEyeContour =
                    face.getContour(FaceContour.LEFT_EYE).getPoints();
            List<PointF> upperLipBottomContour =
                    face.getContour(FaceContour.UPPER_LIP_BOTTOM).getPoints();

            // If classification was enabled:
            if (face.getSmilingProbability() != null) {
                float smileProb = face.getSmilingProbability();
            }
            if (face.getRightEyeOpenProbability() != null) {
                float rightEyeOpenProb = face.getRightEyeOpenProbability();
            }

            // If face tracking was enabled:
            if (face.getTrackingId() != null) {
                int id = face.getTrackingId();
            }
        }
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