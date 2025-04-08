package com.example.plantapp;

import static org.junit.Assert.assertTrue;
import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.plantnetapp.back.DBHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DBHelperTest {

    private Context context;
    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void initializeTest(){
        DBHelper.getInstance(context, null);
        assertTrue(true);
    }
}
