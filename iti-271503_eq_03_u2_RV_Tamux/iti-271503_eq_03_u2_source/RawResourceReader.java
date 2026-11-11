package upv_pm.may_ago_26.iti_271503.pgu2_eq03;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class RawResourceReader {
    private RawResourceReader() {}

    public static String readTextFileFromRawResource(Context context, int resourceId) {
        StringBuilder body = new StringBuilder();
        try (InputStream inputStream = context.getResources().openRawResource(resourceId);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine).append('\n');
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo leer el shader raw.", e);
        }
        return body.toString();
    }
}
