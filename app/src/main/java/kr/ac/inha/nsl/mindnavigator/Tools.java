package kr.ac.inha.nsl.mindnavigator;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Tools {
    // region Variables
    static final short
            RES_OK = 0,
            RES_SRV_ERR = -1,
            RES_FAIL = 1;

    private static int cellWidth, cellHeight;

    private static ExecutorService executor = Executors.newCachedThreadPool();
    // endregion

    static void setCellSize(int width, int height) {
        cellWidth = width;
        cellHeight = height;
    }

    static void cellClearOut(ViewGroup[][] grid, int row, int col, Activity activity, ViewGroup parent, LinearLayout.OnClickListener cellClickListener) {
        if (grid[row][col] == null) {
            activity.getLayoutInflater().inflate(R.layout.date_cell, parent, true);
            ViewGroup res = (ViewGroup) parent.getChildAt(parent.getChildCount() - 1);
            res.getLayoutParams().width = cellWidth;
            res.getLayoutParams().height = cellHeight;
            res.setOnClickListener(cellClickListener);
            grid[row][col] = res;
        } else {
            TextView date_text = grid[row][col].findViewById(R.id.date_text_view);
            date_text.setTextColor(activity.getColor(R.color.textColor));
            date_text.setBackgroundResource(R.drawable.bg_cell);

            while (grid[row][col].getChildCount() > 1)
                grid[row][col].removeViewAt(1);
        }
    }

    static String post(String _url, JSONObject json_body) {
        try {
            URL url = new URL(_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(json_body != null);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.connect();

            if (json_body != null) {
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(json_body.toString());
                wr.flush();
                wr.close();
            }

            int status = con.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                con.disconnect();
                return null;
            } else {
                byte[] buf = new byte[1024];
                int rd;
                StringBuilder sb = new StringBuilder();
                BufferedInputStream is = new BufferedInputStream(con.getInputStream());
                while ((rd = is.read(buf)) > 0)
                    sb.append(new String(buf, 0, rd, "utf-8"));
                is.close();
                con.disconnect();
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static void execute(MyRunnable runnable) {
        executor.execute(runnable);
    }

    static void toggle_keyboard(@NonNull Activity activity, EditText editText, boolean show) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            if (show)
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            else
                imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    static void copy_date(long fromMillis, Calendar toCal) {
        Calendar fromCal = Calendar.getInstance();
        fromCal.setTimeInMillis(fromMillis);

        toCal.set(Calendar.YEAR, fromCal.get(Calendar.YEAR));
        toCal.set(Calendar.MONTH, fromCal.get(Calendar.MONTH));
        toCal.set(Calendar.DAY_OF_MONTH, fromCal.get(Calendar.DAY_OF_MONTH));
    }

    static void copy_time(long fromMillis, Calendar toCal) {
        Calendar fromCal = Calendar.getInstance();
        fromCal.setTimeInMillis(fromMillis);

        toCal.set(Calendar.HOUR_OF_DAY, fromCal.get(Calendar.HOUR_OF_DAY));
        toCal.set(Calendar.MINUTE, fromCal.get(Calendar.MINUTE));
        toCal.set(Calendar.SECOND, 0);
        toCal.set(Calendar.MILLISECOND, 0);
    }
}

abstract class MyRunnable implements Runnable {
    MyRunnable(Object... args) {
        this.args = Arrays.copyOf(args, args.length);
    }

    Object[] args;
}

class Event {
    Event() {
        id = System.currentTimeMillis() / 1000;
    }

    static void init(@NonNull Activity activity) {
        stressColors[0] = activity.getColor(R.color.slvl0_color);
        stressColors[1] = activity.getColor(R.color.slvl1_color);
        stressColors[2] = activity.getColor(R.color.slvl2_color);
    }

    static ArrayList<Event> getOneDayEvents(@NonNull Event[] events, @NonNull Calendar day) {
        ArrayList<Event> res = new ArrayList<>();

        Calendar comDay = (Calendar) day.clone();

        comDay.set(Calendar.HOUR, 0);
        comDay.set(Calendar.MINUTE, 0);
        comDay.set(Calendar.SECOND, 0);
        comDay.set(Calendar.MILLISECOND, 0);
        long fromTime = comDay.getTimeInMillis();

        comDay.add(Calendar.DAY_OF_MONTH, 1);
        comDay.add(Calendar.MINUTE, -1);
        long toTime = comDay.getTimeInMillis();

        for (Event event : events) {
            long start = event.getStartTime().getTimeInMillis();
            long end = event.getEndTime().getTimeInMillis();

            if ((start >= fromTime && start < toTime) || (end >= fromTime && end < toTime))
                res.add(event);
        }

        return res;
    }

    //region Variables
    @ColorInt
    private static int[] stressColors = new int[3];

    private long id;
    private int stressLevel;
    private String title;
    private Calendar startTime;
    private Calendar endTime;
    private String intervention;
    private String stressType;
    private String stressCause;
    private boolean is_shared;
    //endregion

    long getEventId() {
        return id;
    }

    void setStartTime(Calendar startTime) {
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);
        this.startTime = (Calendar) startTime.clone();
    }

    Calendar getStartTime() {
        return startTime;
    }

    void setEndTime(Calendar endTime) {
        endTime.set(Calendar.SECOND, 0);
        endTime.set(Calendar.MILLISECOND, 0);
        this.endTime = (Calendar) endTime.clone();
    }

    Calendar getEndTime() {
        return endTime;
    }

    void setStressLevel(int stressLevel) {
        this.stressLevel = stressLevel;
    }

    int getStressColor() {
        return stressColors[stressLevel];
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getTitle() {
        return title;
    }

    void setIntervention(String intervention) {
        this.intervention = intervention;
    }

    String getIntervention() {
        return intervention;
    }

    void setStressType(String stressType) {
        this.stressType = stressType;
    }

    String getStressType() {
        return stressType;
    }

    void setStressCause(String stressCause) {
        this.stressCause = stressCause;
    }

    String getStressCause() {
        return stressCause;
    }

    void setSharing(boolean isShared) {
        this.is_shared = isShared;
    }

    boolean isShared() {
        return is_shared;
    }
}
