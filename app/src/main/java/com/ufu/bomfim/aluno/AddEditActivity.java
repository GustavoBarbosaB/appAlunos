package com.ufu.bomfim.aluno;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.ufu.bomfim.aluno.helper.AlunoDbHelper;
import com.ufu.bomfim.aluno.model.Aluno;
import com.ufu.bomfim.aluno.util.CircleTransform;

public class AddEditActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView imageView;
    String mCurrentImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        final EditText nome = (EditText) findViewById(R.id.EditTextNomeID);
        final EditText telefone = (EditText) findViewById(R.id.EditTextTelefoneID);
        final EditText site = (EditText) findViewById(R.id.EditTextSiteID);
        final EditText email = (EditText) findViewById(R.id.EditTextEmailID);
        final EditText endereco = (EditText) findViewById(R.id.EditTextEnderecoID);
        final SeekBar nota = (SeekBar) findViewById(R.id.SeekBarNotaID);
        Button salvar = (Button) findViewById(R.id.buttonSalvarID);
        Button cancel = (Button) findViewById(R.id.buttonCancelarID);
        imageView = (ImageView) findViewById(R.id.imageViewID);


        // ------ SeekBar Nota
        nota.setMax(10);
        nota.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress=0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress=progressValue;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(),String.valueOf(progress),Toast.LENGTH_SHORT).show();
            }
        });

        int id = getIntent().getIntExtra("ID",-1);
        final Aluno aluno = AlunoDbHelper.getInstance().getAlunoById(id);
        if(id>-1){
            if(aluno!=null){
                nome.setText(aluno.getNome());
                telefone.setText(aluno.getTelefone());
                site.setText(aluno.getSite());
                email.setText(aluno.getEmail());
                endereco.setText(aluno.getEndereco());
                nota.setProgress(aluno.getNota());
                loadAlunoImage(aluno.getImagePath());
            }
        }

        // ------ Botão Cancelar
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ------ Botão Salvar
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Aluno alunoTosave;
                if (aluno != null) {
                    alunoTosave = aluno;
                } else {
                    alunoTosave = new Aluno();
                }

                alunoTosave.setNome(nome.getText().toString());
                alunoTosave.setTelefone(telefone.getText().toString());
                alunoTosave.setSite(site.getText().toString());
                alunoTosave.setEmail(email.getText().toString());
                alunoTosave.setEndereco(endereco.getText().toString());
                alunoTosave.setNota(nota.getProgress());
                alunoTosave.setImagePath(mCurrentImagePath);

                if (canSave(alunoTosave)) {
                    AlunoDbHelper.getInstance().createOrUpdate(alunoTosave);
                    finish();
                }
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void loadAlunoImage(String path) {
        if(path != null && !path.isEmpty()) {
            Uri imageUri = Uri.parse(path);
            Picasso.with(this)
                    .load(imageUri)
                    .placeholder(ContextCompat.getDrawable(this, R.drawable.minha_imagem))
                    .transform(new CircleTransform())
                    .into(imageView);
        }
    }

    private boolean canSave(Aluno aluno){

        if(!isValidName(aluno.getNome())) {
            Toast.makeText(this, getText(R.string.contact_save_name_error), Toast.LENGTH_LONG).show();
            return false;
        }
        if(!isValidPhone(aluno.getTelefone())) {
            Toast.makeText(this, getText(R.string.contact_save_phone_error), Toast.LENGTH_LONG).show();
            return false;
        }
        if(!isValidEmail(aluno.getEmail())) {
            Toast.makeText(this, getText(R.string.contact_save_email_error), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    private boolean isValidName(CharSequence target) {
        return !TextUtils.isEmpty(target);
    }

    private boolean isValidPhone(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.PHONE.matcher(target).matches();
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE){
            // Get the resultant image's URI.
            final Uri selectedImageUri = (data != null) ? data.getData() : null;

            // Ensure the image exists.
            if (selectedImageUri != null) {
                // Add image to gallery if this is an image captured with the camera
                //Otherwise no need to re-add to the gallery if the image already exists
                final Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(selectedImageUri);
                sendBroadcast(mediaScanIntent);

                mCurrentImagePath = selectedImageUri.toString();

                // Update client's picture
                if (mCurrentImagePath != null && !mCurrentImagePath.isEmpty()) {
                    Picasso.with(this)
                            .load(selectedImageUri)
                            .placeholder(ContextCompat.getDrawable(this, R.drawable.minha_imagem))
                            .transform(new CircleTransform())
                            .into(imageView);
                }

            }
        }

    }

}
