package ro.pub.cs.systems.eim.lab07.calculatorwebservice.view;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ro.pub.cs.systems.eim.lab07.calculatorwebservice.R;
import ro.pub.cs.systems.eim.lab07.calculatorwebservice.general.Constants;

public class CalculatorWebServiceActivity extends AppCompatActivity {

    private EditText operator1EditText, operator2EditText;
    private TextView resultTextView;
    private Spinner operationsSpinner, methodsSpinner;
    private Button displayResultButton;

    private DisplayResultButtonClickListener displayResultButtonClickListener = new DisplayResultButtonClickListener();
    private class DisplayResultButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String operator1 = operator1EditText.getText().toString();
            String operator2 = operator2EditText.getText().toString();
            String operation = operationsSpinner.getSelectedItem().toString();
            String method = String.valueOf(methodsSpinner.getSelectedItemPosition());

            CalculatorWebServiceAsyncTask calculatorWebServiceAsyncTask = new CalculatorWebServiceAsyncTask();
            calculatorWebServiceAsyncTask.execute(operator1, operator2, operation, method);
        }
    }

    private class CalculatorWebServiceAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String operator1 = params[0];
            String operator2 = params[1];
            String operation = params[2];
            int method = Integer.parseInt(params[3]);

            // TODO: exercise 4
            // signal missing values through error messages
            if (operator1.isEmpty() || operator2.isEmpty()) {
                return Constants.ERROR_MESSAGE_EMPTY;
            }

            // create an instance of a HttpClient object
            HttpClient httpClient = new DefaultHttpClient();

            // get method used for sending request from methodsSpinner
            switch (method) {
                // 1. GET
                // a) build the URL into a HttpGet object (append the operators / operations to the Internet address)
                // b) create an instance of a ResultHandler object
                // c) execute the request, thus generating the result
                case Constants.GET_OPERATION:
                    HttpGet httpGet = new HttpGet(Constants.GET_WEB_SERVICE_ADDRESS +
                            "?" + Constants.OPERATION_ATTRIBUTE + "=" + operation +
                            "&" + Constants.OPERATOR1_ATTRIBUTE + "=" + operator1 +
                            "&" + Constants.OPERATOR2_ATTRIBUTE + "=" + operator2);

                    ResponseHandler<String> responseHandlerGet = new BasicResponseHandler();
                    try {
                        return httpClient.execute(httpGet, responseHandlerGet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                // 2. POST
                // a) build the URL into a HttpPost object
                // b) create a list of NameValuePair objects containing the attributes and their values (operators, operation)
                // c) create an instance of a UrlEncodedFormEntity object using the list and UTF-8 encoding and attach it to the post request
                // d) create an instance of a ResultHandler object
                // e) execute the request, thus generating the result
                case Constants.POST_OPERATION:
                    HttpPost httpPost = new HttpPost(Constants.POST_WEB_SERVICE_ADDRESS);
                    List<NameValuePair> parameters = new ArrayList<>();

                    parameters.add(new BasicNameValuePair(Constants.OPERATION_ATTRIBUTE, operation));
                    parameters.add(new BasicNameValuePair(Constants.OPERATOR1_ATTRIBUTE, operator1));
                    parameters.add(new BasicNameValuePair(Constants.OPERATOR2_ATTRIBUTE, operator2));

                    try {
                        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                        httpPost.setEntity(urlEncodedFormEntity);
                    } catch (Exception e) {
                        ;
                    }

                    ResponseHandler<String> responseHandlerPost = new BasicResponseHandler();
                    try {
                        return httpClient.execute(httpPost, responseHandlerPost);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
            }

            return null;
        }

        @Override
        public void onPostExecute(String result) {
            // display the result in resultTextView
            resultTextView.setText(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_web_service);

        operator1EditText = (EditText)findViewById(R.id.operator1_edit_text);
        operator2EditText = (EditText)findViewById(R.id.operator2_edit_text);

        resultTextView = (TextView)findViewById(R.id.result_text_view);

        operationsSpinner = (Spinner)findViewById(R.id.operations_spinner);
        methodsSpinner = (Spinner)findViewById(R.id.methods_spinner);

        displayResultButton = (Button) findViewById(R.id.display_result_button);
        displayResultButton.setOnClickListener(displayResultButtonClickListener);
    }
}
