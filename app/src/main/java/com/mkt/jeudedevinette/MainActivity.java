package com.mkt.jeudedevinette;



import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    int rand;
    Button btnTenter;
    EditText nbre;
    TextView rsltTentative, tempsRestant, scoreTitre;
    int nbreTente;
    int nbreClick = 0;
    String hist = "";
    String histTotale = "";
    TextView txtHistorique, donner, historique;
    Button btnNvllePartie;
    RadioGroup rgNiveau;
    RadioButton debt, exprt;
    Button cmc;
    ScrollView scrView;
    /*
        Timer timer;
        TimerTask timerTask;
        private Handler handler;
        Integer icompteur;
        TextView tvcompteur;
    */
    TextView tvcompteur;
    TextView tvscore;
    int score = 100;
    int icompteur = 0;
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();


    private DBManager dbManager;
    // private SimpleCursorAdapter adapter;

    final String[] from = new String[]{DatabaseHelper._ID,
            DatabaseHelper.NAME, DatabaseHelper.SCORE};

    EditText pseudoEditText;
    String pseudo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvcompteur = (TextView) findViewById(R.id.tvcompteur);
        tvscore = (TextView) findViewById(R.id.tvscore);
        tempsRestant = (TextView) findViewById(R.id.tempsRestant);
        scoreTitre = (TextView) findViewById(R.id.scoreTitre);

        btnTenter = (Button) findViewById(R.id.btnTenter);
        nbre = (EditText) findViewById(R.id.nbreTente);
        rsltTentative = (TextView) findViewById(R.id.rsltTentative);
        txtHistorique = (TextView) findViewById(R.id.txtHistorique);

        btnNvllePartie = (Button) findViewById(R.id.nvllePartie);


        rgNiveau = (RadioGroup) findViewById(R.id.rgNiveau);
        debt = (RadioButton) findViewById(R.id.debt);
        exprt = (RadioButton) findViewById(R.id.exprt);
        donner = (TextView) findViewById(R.id.donner);
        cmc = (Button) findViewById(R.id.cmc);
        historique = (TextView) findViewById(R.id.historique);
        scrView = (ScrollView) findViewById(R.id.scrView);


        premierAffichage();

        compterSeconde();
        calculerScore();

        //initializeTimerTask();
        //startTimer();
        creerRandom();
        nouvellePartie();
        tenter();


    }

    public void creerRandom() {
        rand = (int) (Math.random() * 1000);
    }

    public void tenter() {
        txtHistorique.setMovementMethod(new ScrollingMovementMethod());
        btnTenter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // calculerScore();
                rsltTentative.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty((nbre.getText()).toString())) {
                    nbre.setError("Veuillez saisir un nombre");
                } else {
                    nbreTente = Integer.valueOf((nbre.getText()).toString());
                    if (nbreTente > 999) {
                        nbre.setError("Veuillez saisir un nombre compris entre 0 et 999");
                    } else {
                        showToast(("rand = " + rand));
                        showToast(("tentative = " + nbreTente));
//le compteur de seconde  decremente a chaque tentative
                        icompteur++;


                        nbreClick++;
                        calculerScore();
                        gameOver();

                        historique(nbreClick, nbreTente);
                        if (nbreTente < rand) {
                            rsltTentative.setText("Vous avez saisi un nombre plus petit");
                            nbre.setText("");
                        } else if (nbreTente > rand) {
                            rsltTentative.setText("Vous avez saisi un nombre plus grand");
                            nbre.setText("");
                        } else {
                            handler.removeCallbacksAndMessages(null);
                            rsltTentative.setText("Bravo, c'est gagné après " + nbreClick + " tentatives");
                            ajouterScore();
                            // pseudoDialog();
                            nbre.setText("");
                            nbre.setClickable(false);
                            btnTenter.setClickable(false);
                            nbre.setEnabled(false);
                        }
                    }
                }


            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void historique(int nbreCl, int nombre) {
        hist = "La tentative numero " + nbreCl + " est : " + nombre + "\n";
        histTotale = (txtHistorique.getText()).toString() + hist;
        txtHistorique.setText(histTotale);

    }

    public void nouvellePartie() {


        btnNvllePartie.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               /* finish();
                startActivity(getIntent());
                overridePendingTransition(0, 0);

                */
                handler.removeCallbacksAndMessages(null);
                initialiserScoreEtSeconde();

                creerRandom();
                txtHistorique.setText("");
                rsltTentative.setText("");
                nbre.setText("");
                nbreClick = 0;

                rgNiveau.setVisibility(View.VISIBLE);
                cmc.setVisibility(View.VISIBLE);
                donner.setVisibility(View.GONE);
                nbre.setVisibility(View.GONE);

                btnTenter.setVisibility(View.GONE);
                rsltTentative.setVisibility(View.GONE);
                historique.setVisibility(View.GONE);
                scrView.setVisibility(View.GONE);

                tempsRestant.setVisibility(View.GONE);
                tvcompteur.setVisibility(View.GONE);
                scoreTitre.setVisibility(View.GONE);
                tvscore.setVisibility(View.GONE);

                commencerPartie(true);
                nbre.setClickable(true);
                btnTenter.setClickable(true);
                nbre.setEnabled(true);
            }
        });
    }

    public void commencerPartie(Boolean inGame) {
        cmc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (inGame) {
                    compterSeconde();
                } else {
                    initialiserScoreEtSeconde();
                }
                if (debt.isChecked()) {
                    rgNiveau.setVisibility(View.GONE);
                    cmc.setVisibility(View.GONE);
                    donner.setVisibility(View.VISIBLE);
                    nbre.setVisibility(View.VISIBLE);

                    btnTenter.setVisibility(View.VISIBLE);
                    rsltTentative.setVisibility(View.VISIBLE);
                    historique.setVisibility(View.VISIBLE);
                    scrView.setVisibility(View.VISIBLE);

                    tempsRestant.setVisibility(View.VISIBLE);
                    tvcompteur.setVisibility(View.VISIBLE);
                    scoreTitre.setVisibility(View.VISIBLE);
                    tvscore.setVisibility(View.VISIBLE);
                    rsltTentative.setVisibility(View.GONE);
                    tvscore.setText("100");


                } else {
                    rgNiveau.setVisibility(View.GONE);
                    cmc.setVisibility(View.GONE);
                    donner.setVisibility(View.VISIBLE);
                    nbre.setVisibility(View.VISIBLE);

                    btnTenter.setVisibility(View.VISIBLE);
                    rsltTentative.setVisibility(View.VISIBLE);
                    historique.setVisibility(View.GONE);
                    scrView.setVisibility(View.GONE);

                    tempsRestant.setVisibility(View.VISIBLE);
                    tvcompteur.setVisibility(View.VISIBLE);
                    scoreTitre.setVisibility(View.VISIBLE);
                    tvscore.setVisibility(View.VISIBLE);
                    rsltTentative.setVisibility(View.GONE);
                    tvscore.setText("100");
                }
            }
        });
    }


    public void compterSeconde() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (++icompteur < 100) {
                    handler.postDelayed(this, 1000L);
                    tvcompteur.setText(Integer.toString((100 - icompteur)));

                    gameOver();
                    /*if(icompteur==99){
                        tvscore.setText("GAME OVER!!!");
                    }*/

                    return;
                }

                handler.removeCallbacks(this);
            }
        }, 1000L);
    }

    public void calculerScore() {

        //score = 100 - (icompteur + nbreClick);
        //le traitemengt de nombre de clique est effectué sur icompteur qui sera decrementé à chaque tentative
        score = 100 - icompteur;
        // score = 100 - nbreClick;
        tvscore.setText(Integer.toString(score));
    }

    public void initialiserScoreEtSeconde() {

        score = 100;
        icompteur = 0;
    }

    public void gameOver() {
        if (score <= 0 || icompteur == 99) {
            tvscore.setText("GAME OVER!!!");
            rsltTentative.setText("Le nombre à deviné était : " + rand);
            rsltTentative.setVisibility(View.VISIBLE);
            nbre.setClickable(false);
            nbre.setEnabled(false);
            btnTenter.setClickable(false);
        }
    }

    public void pauseSeconde() {
        handler.removeCallbacksAndMessages(null);
    }

    public void premierAffichage() {
        Log.d("ERROR", "erreur 1");
        handler.removeCallbacksAndMessages(null);
        initialiserScoreEtSeconde();

        /*creerRandom();
        txtHistorique.setText("");
        rsltTentative.setText("");
        nbre.setText("");
        nbreClick = 0;*/

        rgNiveau.setVisibility(View.VISIBLE);
        cmc.setVisibility(View.VISIBLE);
        donner.setVisibility(View.GONE);
        nbre.setVisibility(View.GONE);

        btnTenter.setVisibility(View.GONE);
        rsltTentative.setVisibility(View.GONE);
        historique.setVisibility(View.GONE);
        scrView.setVisibility(View.GONE);

        tempsRestant.setVisibility(View.GONE);
        tvcompteur.setVisibility(View.GONE);
        scoreTitre.setVisibility(View.GONE);
        tvscore.setVisibility(View.GONE);

        commencerPartie(false);
        nbre.setClickable(true);
        btnTenter.setClickable(true);
        nbre.setEnabled(true);

    }

    public void listeScore(View v) {


        dbManager = new DBManager(this);
        dbManager.open();
        //fetch for all data from the table
        //Cursor cursor = dbManager.fetch();
        //select using sql query
        String sql = "SELECT * FROM SCORES ORDER BY score DESC limit 10";
        Cursor cursor = dbManager.sqlQuery(sql);
        /*showToast(("DB =  " + cursor.getCount()));
        showToast(("score =  " + cursor.getInt(2) ));*/
        //alerDialog contenant la liste des meileeurs scores
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(android.R.drawable.ic_dialog_alert);
        builderSingle.setTitle("La liste des 10 meilleurs scores");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_item);

        if (cursor != null && cursor.getCount() != 0) {
            Log.d("if cursor != null", " if");

            //more to the first row
            cursor.moveToFirst();

            //iterate over rows
            for (int i = 0; i < cursor.getCount(); i++) {

                arrayAdapter.add(cursor.getString(1) + " : " + cursor.getString(2));

                cursor.moveToNext();
            }
        } else {
            Log.d("if cursor is null", " else");
            arrayAdapter.add("Pas de meilleur score pour le moment");
        }
        // final SimpleCursorAdapter adapter = new SimpleCursorAdapter(MainActivity.this, android.R.layout.select_dialog_singlechoice, cursor, from, to, 0);

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });



        builderSingle.setAdapter(arrayAdapter, null);
        builderSingle.show();
        cursor.close();
        dbManager.close();

    }

    public void ajouterScore() {
        String nom;
        dbManager = new DBManager(this);
        dbManager.open();
        /*Cursor cursor = dbManager.fetch();
        if(cursor.getCount()<10){
            *//*final String name = subjectEditText.getText().toString();
            final String desc = descEditText.getText().toString();

            dbManager.insert(name, desc);*//*
            dbManager.insert("Toumi Koussay", String.valueOf(score));
        }*/
        String sql = "SELECT * FROM SCORES ORDER BY score DESC limit 10";
        Cursor cursor = dbManager.sqlQuery(sql);
        Log.d("condition", String.valueOf((cursor != null && cursor.getCount() != 0)));
        if (cursor != null && cursor.getCount() != 0) {

            if (cursor.getCount() >= 10) {
                //more to the first row
                cursor.moveToLast();
                if (score > cursor.getInt(2)) {
                    Log.d("condition 1", String.valueOf((cursor.getInt(2))));
                    pseudoDialog();
                    //dbManager.insert(nom, String.valueOf(score));
                }
            } else {
                Log.d("condition 2", "String.valueOf((cursor.getInt(2)))");

                // pseudoDialog();
                pseudoDialog();
                //dbManager.insert(nom, String.valueOf(score));
                //dbManager.insert("Toumi Ayhem", String.valueOf(score));

            }
        } else {
            Log.d("condition 3", "String.valueOf((cursor.getInt(2)))");
            pseudoDialog();
            // dbManager.insert(nom, String.valueOf(score));
        }

        Log.d("pseudo in ajouterscore ", "pseudo: " + pseudo);
        cursor.close();
        dbManager.close();

    }

    public void pseudoDialog() {

        pseudoEditText = new EditText(this);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Bravo!!!")
                .setMessage("Vous êtes classés parmi les meilleurs avec un score : " + score)
                .setView(pseudoEditText)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pseudo = pseudoEditText.getText().toString();
                        if (pseudo == null || pseudo.isEmpty() || pseudo.equals("")) {
                            pseudo = "Inconnu";

                            //ajouterScore(pseudo);
                        } /*else {

                            //ajouterScore(pseudo);


                        }*/
                        Log.d("pseudo in onclick", "pseudo: " + pseudo);
                        dbManager = new DBManager(MainActivity.this);
                        dbManager.open();
                        dbManager.insert(pseudo, String.valueOf(score));

                        dbManager.close();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
        Log.d("pseudo in alertdialog", "pseudo: " + pseudo);
        // return pseudo;
    }

}

