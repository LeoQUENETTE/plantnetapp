package com.example.plantapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.plantnetapp.back.DBHelper;
import com.example.plantnetapp.back.entity.Entity;
import com.example.plantnetapp.back.entity.User;
import com.example.plantnetapp.back.tables.Table;
import com.example.plantnetapp.back.tables.UserTable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class UserTableTest {
    private DBHelper db;
    private UserTable userTable;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = DBHelper.getInstance(context, null);
        userTable = UserTable.getInstance();
        Table.deleteTable(UserTable.TABLE_NAME);
    }
    @After
    public void reset(){
        Table.dropTable(UserTable.TABLE_NAME);
        UserTable.destroyInstance();
        DBHelper.destroyInstance();
    }


    @Test
    public void created(){
        assertTrue(db.tableExist(UserTable.TABLE_NAME));
    }


    @Test
    public void addDataNoIDTest() throws Exception {
        User user = new User("azerty","123456","Léo","Quenette", "leoettag@gmail.com","0766190489");
        userTable.addData(user);
        assertEquals(1, Table.getTotalNbRows(UserTable.TABLE_NAME));
        User newUser = (User) userTable.selectData(1);
        assertTrue("Comparaison sans les identifiants", newUser.equalsWithoutId(user));
    }
    @Test
    public void addDataTest() throws Exception {
        User user = new User(1,"azerty","123456","Léo","Quenette", "leoettag@gmail.com","0766190489");
        userTable.addData(user);
        assertEquals(1, Table.getTotalNbRows(UserTable.TABLE_NAME));
        User newUser = (User) userTable.selectData(1);
        assertTrue("Comparaison sans les identifiants", newUser.equalsWithoutId(user));
    }

    @Test
    public void deleteDataNoIdTest() throws Exception {
        User user = new User("azerty","123456","Léo","Quenette", "leoettag@gmail.com","0766190489");
        userTable.addData(user);
        userTable.deleteData(user);
        assertEquals(0, Table.getTotalNbRows(UserTable.TABLE_NAME));
    }

    @Test
    public void deleteDataWithIDTest() throws Exception {
        User user = new User(1,"azerty","123456","Léo","Quenette", "leoettag@gmail.com","0766190489");
        userTable.addData(user);
        userTable.deleteData(user);
        assertEquals(0, Table.getTotalNbRows(UserTable.TABLE_NAME));
    }

    @Test
    public void selectAllDataIDTest() throws Exception {
        User user = new User(1,"azerty","123456","Léo","Quenette", "leoettag@gmail.com","0766190489");
        userTable.addData(user);
        List<User> userList = new ArrayList<>();
        userList.add(user);

        List<Entity> bddUserList = userTable.selectAllData();
        assertEquals(userList, bddUserList);
    }
    @Test
    public void selectAllDataNoIDTest() throws Exception {
        User user = new User("azerty","123456","Léo","Quenette", "leoettag@gmail.com","0766190489");
        userTable.addData(user);
        List<User> userList = new ArrayList<>();
        userList.add(user);

        List<Entity> bddUserList = userTable.selectAllData();
        assertEquals(userList, bddUserList);
    }

    @Test
    public void selectDataNoIDTest() throws Exception {
        User user = new User("azerty","123456","Léo","Quenette", "leoettag@gmail.com","0766190489");
        userTable.addData(user);
        Entity bddUser = userTable.selectData(1);
        assertEquals(user, bddUser);
    }
    @Test
    public void selectDataIDTest() throws Exception {
        User user = new User(1,"azerty","123456","Léo","Quenette", "leoettag@gmail.com","0766190489");
        userTable.addData(user);
        Entity bddUser = userTable.selectData(user.id);
        assertEquals(user, bddUser);
    }

}
