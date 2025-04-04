package com.example.plantapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.plantnetapp.back.DBHelper;
import com.example.plantnetapp.back.entity.User;
import com.example.plantnetapp.back.tables.UserTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserTableTest {
    private DBHelper db;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = DBHelper.getInstance(context, null);
    }

    @Test
    public void created(){
        assertTrue(db.tableExist(UserTable.TABLE_NAME));
    }


    @Test
    public void addDataTest() throws Exception {
        UserTable userTable = UserTable.getInstance();
        User user = new User("azerty","123456","Léo","Quenette","Admin","leoettag@gmail.com","0766190489");

        userTable.addData(user);
        User newUser = (User) userTable.selectData(1);
        assertEquals(1, userTable.getTotalNbRows(UserTable.TABLE_NAME));
        assertTrue("Comparaison sans les identifiants", newUser.equalsWithoutId(user));

    }
}
