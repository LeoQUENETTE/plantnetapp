package com.example.plantnetapp.back;

import android.util.Log;

import com.example.plantnetapp.back.entity.Entity;
import com.example.plantnetapp.back.entity.Plant;
import com.example.plantnetapp.back.entity.PlantCollection;
import com.example.plantnetapp.back.entity.User;
import com.example.plantnetapp.back.tables.PlantCollectionTable;
import com.example.plantnetapp.back.tables.PlantTable;
import com.example.plantnetapp.back.tables.Table;
import com.example.plantnetapp.back.tables.UserTable;

import java.util.List;
import java.util.Objects;

public class SyncDatabase {

    private static UserTable userTable;
    private static PlantCollectionTable collectionTable;
    private static PlantTable plantTable;
    private static void launchTables(){
        userTable = UserTable.getInstance();
        collectionTable = PlantCollectionTable.getInstance();
        plantTable = PlantTable.getInstance();
    }
    public static void syncEI(User connectedUser){
        try {
            launchTables();
            syncUserEI(connectedUser);
        }catch (Exception e){
            Log.d("Sync E -> I Failed", Objects.requireNonNull(e.getMessage()));
        }
    }
    public static void syncIE(){
        try {
            launchTables();
            syncUserIE();
        }catch (Exception e){
            Log.d("Sync I -> E Failed", Objects.requireNonNull(e.getMessage()));
        }
    }
    private static void syncUserIE(){
        try {
            try{
                List<Entity> localUsers = userTable.selectAllData();
                if (localUsers == null || localUsers.isEmpty()){return;}
                for (Entity entity : localUsers){
                    if (!(entity instanceof User)){break;}
                    User localUser = (User) entity;
                    User externalUser = User.login(localUser.login, localUser.mdp);
                    if (externalUser == null){
                        User.addUserWithID(localUser);
                    }
                    syncCollectionsIE(localUser.id);
                }
            }catch (Table.EmptyTableException e){
                return;
            }
        }catch (Exception e){
            Log.d("Sync I -> E Failed User", Objects.requireNonNull(e.getMessage()));
        }
    }

    private static void syncCollectionsIE(String userID){
        try {
            List<PlantCollection> localCollections = collectionTable.selectAll(userID);
            if (localCollections == null || localCollections.isEmpty()){return;}
            for (PlantCollection col : localCollections){
                PlantCollection externalCollection = PlantCollection.getCollection(userID, col.name);
                if (externalCollection == null){
                    PlantCollection.addCollection(userID, col);
                }
                syncPlantsIE(col);
            }
        }catch (Exception e){
            Log.d("Sync I -> E Failed Collection", Objects.requireNonNull(e.getMessage()));
        }
    }
    private static void syncPlantsIE(PlantCollection col){
        try {
            List<Plant> localPlants = plantTable.selectAllData(col.id);
            if (localPlants  == null || localPlants.isEmpty()){return;}
            for (Plant plant : localPlants){
                if (plant == null){break;}
                Plant externalPlant = Plant.getPlant(col.id, plant.name);
                if (externalPlant == null){
                    Plant.addPlant(plant, col);
                }
            }
        }catch (Exception e){
            Log.d("Sync I -> E Failed Plants", Objects.requireNonNull(e.getMessage()));
        }

    }
    private static void syncUserEI(User connectedUser){
        try {
            try{
                userTable.selectData(connectedUser.id);
            }catch (Table.EmptyTableException e){
                userTable.addData(connectedUser);
            }
            syncCollectionsEI(connectedUser);
        }catch (Exception e){
            Log.d("Sync E -> I Failed User", Objects.requireNonNull(e.getMessage()));
        }
    }

    private static void syncCollectionsEI(User connectedUser){
        try {
            List<PlantCollection> collections = PlantCollection.getAllCollections(connectedUser.id);
            if (collections == null || collections.isEmpty()){return;}
            for (PlantCollection col : collections){
                try{
                    collectionTable.selectData(col.id);
                }catch (Table.EmptyTableException e){
                    collectionTable.addData(col);
                }
                syncPlantsEI(col);
            }
        }catch (Exception e){
            Log.d("Sync E -> I Failed Collection", Objects.requireNonNull(e.getMessage()));
        }
    }
    private static void syncPlantsEI(PlantCollection col){
        try {
            List<Plant> plants = Plant.getAllPlants(col.id);
            if (plants ==null || plants.isEmpty()){return;}
            for (Plant plant : plants){
                try{
                    plantTable.selectData(plant.id);
                }catch (Table.EmptyTableException e){
                    plantTable.addData(plant);
                }
            }
        }catch (Exception e){
            Log.d("Sync E -> I Failed Plants", Objects.requireNonNull(e.getMessage()));
        }

    }
}
