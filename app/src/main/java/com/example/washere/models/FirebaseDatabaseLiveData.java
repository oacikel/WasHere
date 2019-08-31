package com.example.washere.models;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.example.washere.repositories.WasRepository;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

//Thisclass will be revisited later
public class FirebaseDatabaseLiveData extends LiveData<List<Was>> {

    @Override
    protected void onActive() {
        super.onActive();
        FirebaseFirestore.getInstance().collection("wasItems")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        List<Was> wasList;
                        if (e!=null){
                            Log.e("OCUL - Firestore","Error: "+e.getMessage());
                        }

                        else{
                            if (WasRepository.getInstance().getWasList().getValue()!=null){
                                Log.w("OCUL - Firestore", "Repository Was List is being retrieved to add new items.");
                                wasList=WasRepository.getInstance().getWasList().getValue();
                            }
                            else  {
                                Log.w("OCUL - Firestore", "Repository Was List is is null. Creating a new list from zero");
                                wasList=new ArrayList<>();
                            }
                            System.out.println("OCUL - Document change size is: "+value.getDocumentChanges().size());
                            System.out.println("OCUL - Document  size is: "+value.size());
                            for (DocumentChange document : value.getDocumentChanges()) {
                                switch (document.getType()) {
                                    case ADDED:
                                        // Log.d("OCUL - Firestore", "A New Was added: " + document.getDocument().getData());
                                        //wasList.add(createWasFromHashmap(document.getDocument().getData()));
                                        break;
                                    case MODIFIED:
                                        Log.d("OCUL - Firestore", "Modified A Was: " + document.getDocument().getData());
                                        break;
                                    case REMOVED:
                                        Log.d("OCUL - Firestore", "Removed A Was: " + document.getDocument().getData());
                                        break;
                                }

                            }
                            Log.d("OCUL - Firestore", "Final wasList Size:  " +wasList.size());
                            WasRepository.getInstance().getWasList().setValue(wasList);

                            //firebseStorageHelper.addAudioFileToWasObject(wasList);

                        }

                    }
                });
    }
}
