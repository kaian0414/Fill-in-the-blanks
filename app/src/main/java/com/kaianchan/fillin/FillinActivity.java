package com.kaianchan.fillin;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class FillinActivity extends AppCompatActivity {

    private static final String URL_FILLIN = "http://ian14.online/fyp_php/getFillin.php";
    ArrayList<Fillin> loadQuList;

    TextView fillin_question, fillin_seeAnswer, fillin_correctAnswer;
    Button fillin_submit;
    EditText fillin_input;

    int questionsLength;

//    ArrayList<QuestionsItem> questionsList;

    int currectQuestion = 0;
    int numOfCorr = 0, numOfWrong = 0;

    // 每次出幾多條數
    int howManyQu = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fillin);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fill in the blanks");

        // init stuff
        fillin_question = findViewById(R.id.fillin_question);
        fillin_correctAnswer = findViewById(R.id.fillin_correctAnswer);
        fillin_seeAnswer = findViewById(R.id.fillin_seeAnswer);

        fillin_submit = findViewById(R.id.fillin_submit);
        fillin_input = findViewById(R.id.fillin_input);

        loadQuList = new ArrayList<>();

        loadFillin();
        Log.d("0004", String.valueOf(loadQuList.size()));
    }

    public void FillinQuestions() {
        Log.d("0005", String.valueOf(loadQuList.size()));
        // 控制題數
        if (loadQuList.size() > howManyQu) {
            questionsLength = howManyQu;
        } else {
            questionsLength = loadQuList.size();
        }

        // Shuffle the questions
        Collections.shuffle(loadQuList);

        // Start game
        setQuestion(currectQuestion);
        setAnswer(currectQuestion);


        // 看答案
        fillin_seeAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillin_correctAnswer.setVisibility(View.VISIBLE);
            }
        });

        // 提交
        fillin_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String myAnswer = fillin_input.getText().toString().toLowerCase().trim();
                final String corrAnswer = fillin_correctAnswer.getText().toString().toLowerCase().trim();

                // 需要設定, 答錯同看了答案都是numOfWrong++
                // 判斷: 無填 --> 正確 --> 錯誤 --> ERROR
                if (myAnswer.isEmpty()) {
                    Toast noansToast = FancyToast.makeText(FillinActivity.this,"Pls fill in!",FancyToast.LENGTH_SHORT,FancyToast.CONFUSING,true);
                    noansToast.setGravity(Gravity.BOTTOM, 0, 350);
                    noansToast.show();
                } else if (myAnswer.equals(corrAnswer)) {
                    // check下有沒有看答案
                    if (fillin_correctAnswer.getVisibility() != View.VISIBLE) {
                        // 無看答案, 加分
                        numOfCorr++;
                        currectQuestion++;

                        Toast corrToast = FancyToast.makeText(FillinActivity.this,"Correct!",FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,true);
                        corrToast.setGravity(Gravity.BOTTOM, 0, 350);
                        corrToast.show();
                    } else {
                        // 看了答案
                        numOfWrong++;
                        currectQuestion++;

                        Toast seeansToast = FancyToast.makeText(FillinActivity.this,"Correct! But you have seen the model answer!",FancyToast.LENGTH_SHORT,FancyToast.INFO,true);
                        seeansToast.setGravity(Gravity.BOTTOM, 0, 350);
                        seeansToast.show();
                    }

                    // 不可見
                    fillin_correctAnswer.setVisibility(View.INVISIBLE);

                    // Next Question
                    if (currectQuestion < questionsLength) {
                        // 有下一題
                        setQuestion(currectQuestion);
                        setAnswer(currectQuestion);

                        // 清返input了的內容
                        fillin_input.getText().clear();
                    } else {
                        // 無下一題
                        // Game over -- Win
                        endGame();
                    }
                } else if (!(myAnswer.equals(corrAnswer))) {
                    // Longer Toast, 3.5sec
                    Toast wrongToast = FancyToast.makeText(FillinActivity.this,"Wrong! Correct answer is " + corrAnswer,FancyToast.LENGTH_SHORT,FancyToast.ERROR,true);
                    wrongToast.setGravity(Gravity.BOTTOM, 0, 350);
                    wrongToast.show();

                    numOfWrong++;
                    currectQuestion++;

                    wrong();

                    // 不可見
                    fillin_correctAnswer.setVisibility(View.INVISIBLE);

                } else {
                    Toast.makeText(FillinActivity.this, "Error...", Toast.LENGTH_SHORT).show();
                    // Display Error Page
                    Intent intent = new Intent(FillinActivity.this, ErrorActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    // Show question and correct answer on the screen
    private void setQuestion(int number) {
        fillin_question.setText(loadQuList.get(number).getFillinQu());
    }

    private void setAnswer(int number) {
        fillin_correctAnswer.setText(loadQuList.get(number).getFillinAns());
    }

    // Game Over
    private void endGame() {
        // Close the current activity
        finish();

        // Start Result activity
        Intent intent = new Intent(FillinActivity.this, ResultActivity.class);

        // 判斷是不是win
        if (numOfCorr >= numOfWrong) {
            Toast.makeText(FillinActivity.this, "You Win!!!", Toast.LENGTH_SHORT).show();
            intent.putExtra("winOrLost", "win");
            intent.putExtra("numOfCorr", numOfCorr);
            intent.putExtra("numOfWrong", numOfWrong);
        } else {
            Toast.makeText(FillinActivity.this, "You Lost!!!", Toast.LENGTH_SHORT).show();
            intent.putExtra("winOrLost", "lost");
            intent.putExtra("numOfCorr", numOfCorr);
            intent.putExtra("numOfWrong", numOfWrong);
        }
        startActivity(intent);
//        Log.d("CORRECTNESS: ", "corr: " + numOfCorr + ", wrong: " +numOfWrong); // OK
    }

    @SuppressLint("ResourceAsColor")
    private void wrong() {
        LayoutInflater factory = LayoutInflater.from(FillinActivity.this);
        final View errorview = factory.inflate(R.layout.alert_wrong,null);

        AlertDialog.Builder inputDialog = new AlertDialog.Builder(FillinActivity.this);
        inputDialog.setView(errorview);
        inputDialog.setPositiveButton(
                "next",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 按下alertl中的alert
                        // Next Question
                        if (currectQuestion < questionsLength) {
                            // 有下一題
                            setQuestion(currectQuestion);
                            setAnswer(currectQuestion);

                            // 清返input了的內容
                            fillin_input.getText().clear();
                        } else {
                            // 無下一題
                            // Game over -- Win
                            endGame();
                        }
                    }
                    //}).show();
                });

        // Wrong alert
        AlertDialog dialog = inputDialog.create();
        dialog.setCancelable(false); // 點撃dialog外, 不能取消個alert
        final Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable());
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
    }

    // Database get questions
    public void loadFillin() {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                URL_FILLIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray voArray = new JSONArray(response);

                            for (int i = 0; i < voArray.length(); i++) {
                                JSONObject voObj = voArray.getJSONObject(i); // index[i]

                                String fillinQu = voObj.getString("fillinQu");
                                String fillinAns = voObj.getString("fillinAns");
                                int fillinChapter = voObj.getInt("chapterId");

                                Fillin fillin = new Fillin(fillinQu, fillinAns, fillinChapter); // Add into chapterList

                                loadQuList.add(fillin);
                            }
                            Log.d("0001", String.valueOf(loadQuList.size()));
                            FillinQuestions();

//                            stringArray = new String[mVocabList.size()];
//                            for (int y = 0; y < mVocabList.size(); y++) {
//                                stringArray[y] = mVocabList.get(y).getvPort();
//                                Log.d("9999", "stringArray[]" + y + " is " + stringArray[y]);
//                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("0002", String.valueOf(loadQuList.size()));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(FillinActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // RequestQueue
        Volley.newRequestQueue(this).add(stringRequest);
        Log.d("0003", String.valueOf(loadQuList.size()));
    }

    // the inner activity Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String msg = "";
        switch (item.getItemId()) {
            case R.id.toolbar_home:
                msg = "Home";
//                Intent intent = new Intent(FillinActivity.this, MainActivity.class);
//                startActivity(intent);
                break;
            case R.id.toolbar_back:
                msg = "Back";
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
