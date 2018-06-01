package kr.ac.inha.nsl.mindnavigator;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Tools {
    // region Variables
    static final short
            RES_OK = 0,
            RES_SRV_ERR = -1,
            RES_FAIL = 1;

    private static int cellWidth, cellHeight;

    private static Queue<MyRunnable> execQueue = new LinkedList<>();
    private static ExecutorService masterExec = Executors.newCachedThreadPool();
    private static ExecutorService slaveExec = Executors.newCachedThreadPool();
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

    static void execute(MyRunnable runnable, boolean interrupt_current) {
        execQueue.add(runnable);
    }
}

abstract class MyRunnable implements Runnable {
    MyRunnable(Object... args) {
        this.args = Arrays.copyOf(args, args.length);
    }

    private Object[] args;
}

class Event implements Parcelable {
    Event(String title, int stressLevel, Calendar startTime, Calendar endTime) {
        setTitle(title);
        setStressLevel(stressLevel);
        setStartTime(startTime);
        setEndTime(endTime);
        id = System.currentTimeMillis() / 1000;

        events.add(this);
    }

    private Event(Parcel in) {
        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();

        id = in.readLong();
        startTime.setTimeInMillis(in.readLong());
        endTime.setTimeInMillis(in.readLong());
        stressLevel = in.readInt();
        title = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public static void init(Activity activity) {
        stressColors[0] = activity.getColor(R.color.slvl0_color);
        stressColors[1] = activity.getColor(R.color.slvl1_color);
        stressColors[2] = activity.getColor(R.color.slvl2_color);

        events.clear();
    }

    public static ArrayList<Event> getOneDayEvents(Calendar day) {
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
    private static ArrayList<Event> events = new ArrayList<>();

    private long id;
    private int stressLevel;
    private String title;
    private Calendar startTime;
    private Calendar endTime;
    //endregion

    private void setStartTime(Calendar startTime) {
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);
        this.startTime = (Calendar) startTime.clone();
    }

    public Calendar getStartTime() {
        return startTime;
    }

    private void setEndTime(Calendar endTime) {
        endTime.set(Calendar.SECOND, 0);
        endTime.set(Calendar.MILLISECOND, 0);
        this.endTime = (Calendar) endTime.clone();
    }

    public Calendar getEndTime() {
        return endTime;
    }

    private void setStressLevel(int stressLevel) {
        this.stressLevel = stressLevel;
    }

    public int getStressLevel() {
        return stressLevel;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @SuppressWarnings("unused")
    public long getEventId() {
        return id;
    }

    public int getStressColor() {
        return stressColors[stressLevel];
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(startTime.getTimeInMillis());
        dest.writeLong(endTime.getTimeInMillis());
        dest.writeInt(stressLevel);
        dest.writeString(title);
    }
}
