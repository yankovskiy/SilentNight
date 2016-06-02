/*******************************************************************************
 * Copyright (C) 2013 Artem Yankovskiy (artemyankovskiy@gmail.com).
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package ru.neverdark.silentnight;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

/**
 * Implements time selection dialog to service control activity
 */
public class TimePreference extends DialogPreference {
    private Calendar mCalendar;
    private TimePicker mPicker = null;
    public static final int MASK = 255;

    public TimePreference(Context ctxt) {
        this(ctxt, null);
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, 0);
    }

    public TimePreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);
        setPositiveButtonText(R.string.pref_set);
        setNegativeButtonText(R.string.pref_cancel);
        mCalendar = new GregorianCalendar();
    }

    @Override
    public CharSequence getSummary() {
        if (mCalendar == null) {
            return null;
        }
        return DateFormat.getTimeFormat(getContext()).format(
                new Date(mCalendar.getTimeInMillis()));
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        mPicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mPicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
    }

    @Override
    protected View onCreateDialogView() {
        mPicker = new TimePicker(getContext());
        mPicker.setIs24HourView(true);
        return (mPicker);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            mCalendar.set(Calendar.HOUR_OF_DAY, mPicker.getCurrentHour());
            mCalendar.set(Calendar.MINUTE, mPicker.getCurrentMinute());

            setSummary(getSummary());
            if (callChangeListener(mCalendar.getTimeInMillis())) {
                int time = (mCalendar.get(Calendar.HOUR_OF_DAY))
                        | (mCalendar.get(Calendar.MINUTE) << 8);
                persistInt(time);
                notifyChanged();
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        int time = 0;
        if (restoreValue) {
            time = getPersistedInt(0);

            if (time == 0) {
                mCalendar.setTimeInMillis(System.currentTimeMillis());
            } else {
                mCalendar.set(Calendar.HOUR_OF_DAY, MASK & time);
                mCalendar.set(Calendar.MINUTE, MASK & time >>> 8);
            }
        } else {
            if (defaultValue == null) {
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                time = (mCalendar.get(Calendar.HOUR_OF_DAY))
                        | (mCalendar.get(Calendar.MINUTE) << 8);
            } else {
                time = Integer.parseInt((String) defaultValue);
                mCalendar.set(Calendar.HOUR_OF_DAY, MASK & time);
                mCalendar.set(Calendar.MINUTE, MASK & time >>> 8);
            }
        }
        setSummary(getSummary());
    }
}
