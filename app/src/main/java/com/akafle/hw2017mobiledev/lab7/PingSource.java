package com.akafle.hw2017mobiledev.lab7;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PingSource {

    public interface PingListener {
        void onPingsReceived(List<Ping> pingList);
    }

    private static PingSource sNewsSource;

    private Context mContext;

    public static PingSource get(Context context) {
        if (sNewsSource == null) {
            sNewsSource = new PingSource(context);
        }
        return sNewsSource;
    }

    private PingSource(Context context) {
        mContext = context;
    }

    // Firebase methods for you to implement.

    public void getPings(final PingListener pingListener) {
        DatabaseReference pingsRef = FirebaseDatabase.getInstance().getReference();
        Query last50PingsQuery = pingsRef.limitToLast(50);
        DatabaseReference mqueryRef = pingsRef.child("pings");
        mqueryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Ping> mpingList = new ArrayList<>();
                Iterable<DataSnapshot> pingSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot articleSnapshot:pingSnapshots){
                    Ping sharedArticle = new Ping(articleSnapshot);
                    mpingList.add(sharedArticle);
                }
                pingListener.onPingsReceived(mpingList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getPingsForUserId(String userId, final PingListener pingListener) {
        DatabaseReference pingsRef= FirebaseDatabase.getInstance().getReference();
        DatabaseReference mpingRef = pingsRef.child("pings");
        Query uquery =mpingRef.orderByChild("userId").equalTo(userId).limitToLast(50);
        uquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List <Ping> mpingList = new ArrayList<Ping>();
                Iterable<DataSnapshot> pingSnapshots = dataSnapshot.getChildren();
                for(DataSnapshot articleSnapshot:pingSnapshots){
                    Ping sharedArticle = new Ping(articleSnapshot);
                    mpingList.add(sharedArticle);
                }
                pingListener.onPingsReceived(mpingList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendPing(Ping ping) {
        DatabaseReference  pingsRef= FirebaseDatabase.getInstance().getReference();
        DatabaseReference mpingRef = pingsRef.child("pings");
        DatabaseReference newmpingRef = mpingRef.push();
        Map<String,Object> pingValMap = new HashMap<String, Object>();
        pingValMap.put("userName",ping.getUserName());
        pingValMap.put("userId",ping.getUserId());
        pingValMap.put("timestamp", ServerValue.TIMESTAMP);
        newmpingRef.setValue(pingValMap);
    }
}
