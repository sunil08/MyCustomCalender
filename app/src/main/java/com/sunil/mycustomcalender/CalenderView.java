package com.sunil.mycustomcalender;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Sunil on 30-12-2015.
 */
public class CalenderView extends LinearLayout
{
    // for logging
    private static final String LOGTAG = "Calendar View";

    // how many days to show, defaults to six weeks, 42 days
    private static final int DAYS_COUNT = 42;

    // default date format
    private static final String DATE_FORMAT = "MMM yyyy";

    // date format
    private String dateFormat;

    // current displayed month
    private Calendar currentDate = Calendar.getInstance();

    //event handling
    private EventHandler eventHandler = null;

    private static int position1;
    private static View cell1;

    // internal components
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView grid;

    // seasons' rainbow
    int[] rainbow = new int[] {
            R.color.summer,
            R.color.fall,
            R.color.winter,
            R.color.spring
    };

    List<Integer> monthnum = new ArrayList<Integer>();

    List<Integer> monthnum1 = new ArrayList<Integer>();
    List<Integer> monthnum2 = new ArrayList<Integer>();
    List<Integer> monthnum3 = new ArrayList<Integer>();

    public static int monthcount0=0 , monthcount1=0, monthcount2=0, monthcount3=0, monthcount4=0, monthcount5=0, monthcount6=0,
                        monthcount7=0, monthcount8=0, monthcount9=0, monthcount10=0, monthcount11=0;

    public static int l = -1;

    // month-season association (northern hemisphere, sorry australia :)
    int[] monthSeason = new int[] {2, 2, 3, 3, 3, 0, 0, 0, 1, 1, 1, 2};

    public CalenderView(Context context)
    {
        super(context);
    }

    public CalenderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initControl(context, attrs);
    }

    public CalenderView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    /**
     * Load control xml layout
     */
    private void initControl(Context context, AttributeSet attrs)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_calender, this);

        loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();

        updateCalendar();
    }

    private void loadDateFormat(AttributeSet attrs)
    {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try
        {
            // try to load provided date format, and fallback to default otherwise
            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;
        }
        finally
        {
            ta.recycle();
        }
    }
    private void assignUiElements()
    {
        // layout is inflated, assign local variables to components
        header = (LinearLayout)findViewById(R.id.calenderheader);
        btnPrev = (ImageView)findViewById(R.id.previousarrow);
        btnNext = (ImageView)findViewById(R.id.nextarrow);
        txtDate = (TextView)findViewById(R.id.monthname);
        grid = (GridView)findViewById(R.id.gridview);
    }

    private void assignClickHandlers()
    {
        // add one month and refresh UI
        btnNext.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentDate.add(Calendar.MONTH, 1);
                updateCalendar();
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });

        // long-pressing a day
       /* grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> view, View cell, int position, long id)
            {
                // handle long-press
                if (eventHandler == null)
                    return false;

                //position1 = position;

                if(cell1!=null)
                cell1.setBackgroundResource(0);
                cell1 = cell;

                cell.setBackgroundResource(R.drawable.circular_shape);

                eventHandler.onDayLongPress((Date)view.getItemAtPosition(position));
                return true;
            }
        });*/

        // single press a day
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View cell, int position, long id) {

                // handle long-press
                //if (eventHandler == null)
                // return false;

                //position1 = position;

                if (cell1 != null)
                    cell1.setBackgroundResource(0);
                cell1 = cell;

                cell.setBackgroundResource(R.drawable.circular_shape);

                eventHandler.onDayLongPress((Date) parent.getItemAtPosition(position));
                //return true;

            }
        });
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar()
    {
        updateCalendar(null);
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar(HashSet<Date> events)
    {
        monthcount0 = 0;
        monthcount1 = 0;
        monthcount2 = 0;
        monthcount3 = 0;
        monthcount4 = 0;
        monthcount5 = 0;
        monthcount6 = 0;
        monthcount7 = 0;
        monthcount8 = 0;
        monthcount9 = 0;
        monthcount10 = 0;
        monthcount11 = 0;

        monthnum1.clear();

        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (GregorianCalendar)currentDate.clone();

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        monthnum.clear();

        // fill cells
        while (cells.size() < DAYS_COUNT)
        {
            Log.e("calender time",""+calendar.getTime());
            cells.add(calendar.getTime());
            monthnum.add(calendar.get(Calendar.MONTH));
            Log.e("calender time new", "" + calendar.get(Calendar.MONTH));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        for(int co = 0 ; co < monthnum.size(); co++)
        {
            switchfunc(monthnum.get(co));
        }

/*        Log.e("monthcount0 ", "" + monthcount0);
        Log.e("monthcount1 ", "" + monthcount1);
        Log.e("monthcount2 ", "" + monthcount2);
        Log.e("monthcount3 ", "" + monthcount3);
        Log.e("monthcount4 ", "" + monthcount4);
        Log.e("monthcount5 ", "" + monthcount5);
        Log.e("monthcount6 ", "" + monthcount6);
        Log.e("monthcount7 ", "" + monthcount7);
        Log.e("monthcount8 ", "" + monthcount8);
        Log.e("monthcount9 ", "" + monthcount9);
        Log.e("monthcount10 ", "" + monthcount10);
        Log.e("monthcount11 ", "" + monthcount11);*/

        // update grid
        grid.setAdapter(new CalendarAdapter(getContext(), cells, events));

        // update title
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        txtDate.setText(sdf.format(currentDate.getTime()));

        // set header color according to current season
        int month = currentDate.get(Calendar.MONTH);
        int season = monthSeason[month];
        int color = rainbow[season];

        header.setBackgroundColor(getResources().getColor(color));
    }

    public void switchfunc(int k)
    {
            switch (k) {
                case 0:
                    monthcount0++;
                    if(!monthnum1.contains(0))
                    monthnum1.add(k);
                    break;
                case 1:
                    monthcount1++;
                    if(!monthnum1.contains(1))
                        monthnum1.add(k);
                    break;
                case 2:
                    monthcount2++;
                    if(!monthnum1.contains(2))
                        monthnum1.add(k);
                    break;
                case 3:
                    monthcount3++;
                    if(!monthnum1.contains(3))
                        monthnum1.add(k);
                    break;
                case 4:
                    monthcount4++;
                    if(!monthnum1.contains(4))
                        monthnum1.add(k);
                    break;
                case 5:
                    monthcount5++;
                    if(!monthnum1.contains(5))
                        monthnum1.add(k);
                    break;
                case 6:
                    monthcount6++;
                    if(!monthnum1.contains(6))
                        monthnum1.add(k);
                    break;
                case 7:
                    monthcount7++;
                    if(!monthnum1.contains(7))
                        monthnum1.add(k);
                    break;
                case 8:
                    monthcount8++;
                    if(!monthnum1.contains(8))
                        monthnum1.add(k);
                    break;
                case 9:
                    monthcount9++;
                    if(!monthnum1.contains(9))
                        monthnum1.add(k);
                    break;
                case 10:
                    monthcount10++;
                    if(!monthnum1.contains(10))
                        monthnum1.add(k);
                    break;
                case 11:
                    monthcount11++;
                    if(!monthnum1.contains(11))
                        monthnum1.add(k);
                    break;
            }
    }

    private class CalendarAdapter extends ArrayAdapter<Date>
    {
        // days with events
        private HashSet<Date> eventDays;

        // for view inflation
        private LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays)
        {
            super(context, R.layout.control_calendar_day, days);
            this.eventDays = eventDays;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            // day in question
            Calendar calendar = new GregorianCalendar();
            Date date = getItem(position);
            calendar.setTime(date);
            int day = calendar.get(Calendar.DATE);
            Log.e("my day is ",""+day);
            int month = calendar.get(Calendar.MONTH);
            Log.e("my month is ",""+month);
            int year = calendar.get(Calendar.YEAR);
            Log.e("my year is ",""+year);

            // today
            Date today = new Date();

            // inflate item if it does not exist yet
            if (view == null)
                view = inflater.inflate(R.layout.control_calendar_day, parent, false);

            // if this day has an event, specify event image
            view.setBackgroundResource(0);
            if (eventDays != null)
            {
                for (Date eventDate : eventDays)
                {
                    if (eventDate.getDate() == day &&
                            eventDate.getMonth() == month &&
                            eventDate.getYear() == year)
                    {
                        // mark this day for event
                        // view.setBackgroundResource(R.drawable.circular_shape);               // commented by sunil me
                        break;
                    }
                }
            }

            // clear styling
            ((TextView)view).setTypeface(null, Typeface.NORMAL);
            ((TextView)view).setTextColor(Color.BLACK);

            Log.e("Month is ", "" + month);
            Log.e("today.getMonth() is ", "" + today.getMonth());
            Log.e("month num 1"," "+monthnum1);


            if(monthnum1.size()==3) {
                if (month == monthnum1.get(0)) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.greyed_out));
                }
                else if (month == monthnum1.get(2)) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.greyed_out));
                }
            }
            else
            {
/*                if (date.getMonth() == monthnum1.get(0)) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.greyed_out));
                }*/
                if (month == monthnum1.get(1)) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.greyed_out));
                }
            }
/*            if ((month != today.getMonth() || year != today.getYear()))
            {
                // if this day is outside current month, grey it out
                ((TextView)view).setTextColor(getResources().getColor(R.color.greyed_out));
            }
            else if (day == today.getDate())
            {
                // if it is today, set it to blue/bold
                ((TextView)view).setTypeface(null, Typeface.BOLD);
                ((TextView)view).setTextColor(getResources().getColor(R.color.today));
            }*/

            // set text
            ((TextView)view).setText(String.valueOf(day));

            return view;
        }
    }

    /**
     * Assign event handler to be passed needed events
     */
    public void setEventHandler(EventHandler eventHandler)
    {
        this.eventHandler = eventHandler;
    }

    /**
     * This interface defines what events to be reported to
     * the outside world
     */
    public interface EventHandler
    {
        void onDayLongPress(Date date);
    }
}