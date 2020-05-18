package by.ignot.labs.sheetsapi;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.common.util.concurrent.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GoogleSheetsApi {
    private static final List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GoogleSheetsApi.class.getResourceAsStream(Constant.CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + Constant.CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(Constant.JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, Constant.JSON_FACTORY, clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(Constant.TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private static Sheets getService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new Sheets.Builder(httpTransport, Constant.JSON_FACTORY, getCredentials(httpTransport))
                .setApplicationName(Constant.APPLICATION_NAME)
                .build();
    }

    private static ValueRange getResponse() throws IOException, GeneralSecurityException {

        final String range = "MyTestList!A2:C";

        ValueRange response = getService().spreadsheets().values()
                .get(Constant.SPREADSHEET_ID, range)
                .execute();

        return response;
    }



    public static ArrayList<StudentRow> getAll() throws IOException, GeneralSecurityException {
        List<List<Object>> values = getResponse().getValues();

        ArrayList<StudentRow> studentRows = new ArrayList<>(){
            @Override
            public String toString(){
                StringBuilder sb = new StringBuilder("Name, State, Activity:\n");
                this.forEach(studentRow -> {
                    sb.append(studentRow);
                });
                return sb.toString();
            }
        };

        values.forEach(value ->{
            studentRows.add(new StudentRow(value.get(0).toString(), value.get(1).toString(), value.get(2).toString()));
        });
        return studentRows;
    }

    public static StudentRow getStudentByRowNumber(int row) throws IOException, GeneralSecurityException {
        ArrayList<StudentRow> studentRows = getAll();

        return getAll().get(row - 1);
    }

    public static void addStudent(StudentRow studentRow) throws GeneralSecurityException, IOException {
        final String listName = "MyTestList";

        Sheets service = getService();

        ValueRange requestBody = new ValueRange().setValues(Collections.singletonList(studentRow.toList()));

        Sheets.Spreadsheets.Values.Append request = service.spreadsheets().values()
                .append(Constant.SPREADSHEET_ID, listName, requestBody)
                .setValueInputOption("USER_ENTERED")
                .setInsertDataOption("INSERT_ROWS");

        AppendValuesResponse response = request.execute();

        System.out.println(response);
    }

    public static void updateStudentByRowNumber(int rowNumber, StudentRow studentRow) throws GeneralSecurityException, IOException {
        final String listName = "MyTestList";
        Sheets service = getService();

        ValueRange requestBody = new ValueRange().setValues(Collections.singletonList(studentRow.toList()));

        Sheets.Spreadsheets.Values.Update request = service
                .spreadsheets()
                .values()
                .update(Constant.SPREADSHEET_ID, listName + "!A" + rowNumber + ":C" + rowNumber, requestBody)
                .setValueInputOption("RAW");

        UpdateValuesResponse response = request.execute();

        System.out.println(response);
    }
}
