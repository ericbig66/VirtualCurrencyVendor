package com.greeting.currencyprojectvendor.ui.main;

import android.content.Context;
import android.util.EventLog;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.greeting.currencyprojectvendor.EventAttendList;
import com.greeting.currencyprojectvendor.R;
import com.greeting.currencyprojectvendor.RedEnvelopeDiary;
import com.greeting.currencyprojectvendor.SellDiary;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    //頁面名稱
    private static final int[] TAB_TITLES = new int[]{R.string.Sell, R.string.RedEnvelop, R.string.AttendList};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
//        return PlaceholderFragment.newInstance(position + 1);
        switch (position){
            case 0:
                return SellDiary.newInstance();
            case 1:
                return RedEnvelopeDiary.newInstance();
            case 2:
                return EventAttendList.newInstance();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 3;
    }
}