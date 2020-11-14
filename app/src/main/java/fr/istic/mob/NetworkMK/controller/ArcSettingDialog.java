package fr.istic.mob.NetworkMK.controller;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.istic.mob.NetworkMK.R;
import fr.istic.mob.NetworkMK.model.Arc;

/**
 * Classe pour gérer la fenetre de l'edition de l'étiquette d'un arc
 *
 */
public class ArcSettingDialog extends Dialog {
    private EditText mEtiquette;
    private EditText mSize;
    private Spinner mColor;
    private Button mBtnSave;
    private Button mBtnDelete;
    private ImageView mBtnCancel;
    private EditText mBoucleSize;
    private TextView mBoucleSizeTV;
    private EditText mEpaisseur;
    private Arc arcSelected;

    private MainActivity mainActivity;

    /**
     * @param mainActivity le context de l'activitée principale
     */
    public ArcSettingDialog(@NonNull MainActivity mainActivity) {
        super(mainActivity);
        this.mainActivity = mainActivity;
        arcSelected = mainActivity.getArcSelected();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.arc_editing);

        mEtiquette = findViewById(R.id.te_etiquette);
        mColor = findViewById(R.id.te_color);
        mEpaisseur = findViewById(R.id.te_epaisseur);
        mSize = findViewById(R.id.te_size);
        mBoucleSize = findViewById(R.id.te_boucle_size);
        mBoucleSizeTV = findViewById(R.id.tv_boucle_size);
        mBtnSave = findViewById(R.id.btn_valider);
        mBtnDelete = findViewById(R.id.btn_delete_arc);
        mBtnCancel = findViewById(R.id.btn_cancel);

        if(!arcSelected.isLoop()){
            mBoucleSize.setVisibility(View.GONE);
            mBoucleSizeTV.setVisibility(View.GONE);}

        mEtiquette.setText(this.mainActivity.getArcSelected().getLabel());
        mColor.setSelection(getIndex(this.mainActivity.getArcSelected().getColor()));
        mEpaisseur.setText(String.valueOf(this.mainActivity.getArcSelected().getmWidth()));
        mSize.setText(String.valueOf(this.mainActivity.getArcSelected().getTexSize()));

        mEtiquette.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.getGraph().removeArc(mainActivity.getArcSelected());
                mainActivity.getSupportView().postInvalidate();
                dismiss();
            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Les vérifs
                Pattern p = Pattern.compile("[^0-9.]", Pattern.CASE_INSENSITIVE);
                Matcher matchForSize = p.matcher(mSize.getText().toString());
                Matcher matchFormEpaisseur = p.matcher(mEpaisseur.getText().toString());
                boolean b1 = matchForSize.find();
                boolean b2 = matchFormEpaisseur.find();

                if(mEtiquette.getText().length()==0 || mEpaisseur.getText().length()==0 || mSize.getText().length()==0 || b1 || b2){
                    Toast toast = Toast.makeText (getContext().getApplicationContext (), getContext().getString(R.string.wrong_entry) , Toast.LENGTH_SHORT);
                    toast.setMargin ( 50 , 50 );
                    toast.show ();
                }
                else {
                    arcSelected.setLabel(mEtiquette.getText().toString());
                    // For spinner
                    // To get a String
                    // String size = mColor.getSelectedItem().toString();
                    int spinner_pos = mColor.getSelectedItemPosition();
                    int[] colors_values = mainActivity.getResources().getIntArray(R.array.colors_values);
                    int color = Integer.valueOf(colors_values[spinner_pos]);
                    arcSelected.setColor(color);
                    arcSelected.setmWidth(Integer.valueOf(mEpaisseur.getText().toString()));
                    arcSelected.setTexSize(Integer.valueOf(mSize.getText().toString()));
                    mainActivity.getSupportView().postInvalidate();
                    dismiss();
                }
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    /**
     *
     * @param currentColor Couleur de l'arc à éditer
     * @return
     */
    private int getIndex(int currentColor){
        int[] colors_values = mainActivity.getResources().getIntArray(R.array.colors_values);
        for (int i=0;i<colors_values.length;i++){
            if (colors_values[i] == currentColor){
                return i;
            }
        }
        return 0;
    }
}
