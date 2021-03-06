/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.material.picker;

import com.google.android.material.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import com.google.android.material.resources.MaterialAttributes;
import androidx.core.util.Pair;
import android.text.format.DateUtils;
import android.widget.TextView;
import java.util.Calendar;

/**
 * A {@link GridSelector} that uses a {@link Pair} of {@link Calendar} objects to represent a
 * selected range.
 *
 * @hide
 */
@RestrictTo(Scope.LIBRARY_GROUP)
public class DateRangeGridSelector implements GridSelector<Pair<Calendar, Calendar>> {

  private Calendar selectedStartItem = null;
  private Calendar selectedEndItem = null;

  @Override
  public void select(Calendar selection) {
    if (selectedStartItem == null) {
      selectedStartItem = selection;
    } else if (selectedEndItem == null && selection.after(selectedStartItem)) {
      selectedEndItem = selection;
    } else {
      selectedEndItem = null;
      selectedStartItem = selection;
    }
  }

  @Override
  public void drawCell(TextView cell, Calendar item) {
    Context context = cell.getContext();
    int rangeCalendarStyle =
        MaterialAttributes.resolveOrThrow(
            context,
            R.attr.materialDateRangePickerStyle,
            MaterialCalendar.class.getCanonicalName());

    int style;
    TypedArray stylesList =
        context.obtainStyledAttributes(rangeCalendarStyle, R.styleable.MaterialCalendar);
    if (item.equals(selectedStartItem) || item.equals(selectedEndItem)) {
      style = stylesList.getResourceId(R.styleable.MaterialCalendar_daySelectedStyle, 0);
    } else if (DateUtils.isToday(item.getTimeInMillis())) {
      style = stylesList.getResourceId(R.styleable.MaterialCalendar_dayTodayStyle, 0);
    } else {
      style = stylesList.getResourceId(R.styleable.MaterialCalendar_dayStyle, 0);
    }
    stylesList.recycle();

    CalendarGridSelectors.colorCell(cell, style);
  }

  @Override
  @Nullable
  public Pair<Calendar, Calendar> getSelection() {
    Calendar start = getStart();
    Calendar end = getEnd();
    if (start == null || end == null) {
      return null;
    }
    return new Pair<>(getStart(), getEnd());
  }

  /** Returns a {@link java.util.Calendar} representing the start of the range */
  @Nullable
  public Calendar getStart() {
    return selectedStartItem;
  }

  /** Returns a {@link java.util.Calendar} representing the end of the range */
  @Nullable
  public Calendar getEnd() {
    return selectedEndItem;
  }

  /* Parcelable interface */

  /** {@link Parcelable.Creator} */
  public static final Parcelable.Creator<DateRangeGridSelector> CREATOR =
      new Parcelable.Creator<DateRangeGridSelector>() {
        @Override
        public DateRangeGridSelector createFromParcel(Parcel source) {
          DateRangeGridSelector dateRangeGridSelector = new DateRangeGridSelector();
          dateRangeGridSelector.selectedStartItem = (Calendar) source.readSerializable();
          dateRangeGridSelector.selectedEndItem = (Calendar) source.readSerializable();
          return dateRangeGridSelector;
        }

        @Override
        public DateRangeGridSelector[] newArray(int size) {
          return new DateRangeGridSelector[size];
        }
      };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeSerializable(selectedStartItem);
    dest.writeSerializable(selectedEndItem);
  }
}
