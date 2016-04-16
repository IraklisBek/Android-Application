package com.example.irakl_000.maps.server;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.irakl_000.maps.maps.ClusterItems;
import com.example.irakl_000.maps.user.UserProfile;
import com.facebook.Profile;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.mongodb.client.model.Filters.near;

/**
 * Created by Irakl_000 on 6/3/2016.
 */
public class ServerRequest {
    ProgressDialog pd;
    MongoCollection<Document> texts;
    MongoCollection<Document> fb_users;


    public ServerRequest(Context context){
        pd = new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setTitle("Proccesing");
        pd.setMessage("Please wait...");
        texts = getCollectionn("mongodb://...:....mlab.com:.../...", "...");
        fb_users = getCollectionn("mongodb://...:....mlab.com:.../...", "...");

        /*
        String SERVER_ADDRESS = "http://irakmpek.webpages.auth.gr/android";
        URL url = null;
        BufferedReader reader;
        try {
            url = new URL(SERVER_ADDRESS + "db2.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) { //Read till there is something available
                sb.append(line + "\n");     //Reading and saving line by line - not all at once
            }
            line = sb.toString();           //Saving complete data received in string, you can do it differently
            JSONObject jObject  = new JSONObject(line); // json
            JSONObject data = jObject.getJSONObject("languages"); // get data object
            String projectname = data.getString("name"); // get the name from data.
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    private MongoCollection<Document> getCollectionn(String link, String coll){
        MongoClientURI uri  = new MongoClientURI(link);
        MongoClient client = new MongoClient(uri);
        MongoDatabase db =  client.getDatabase(uri.getDatabase());
        return db.getCollection(coll);
    }

    private Document findDocument(String field, String value){
        BasicDBObject query = new BasicDBObject();
        query.put(field, value);
        Document doc = fb_users.find(query).first();
        return doc;
    }

    /**
     * storePostInBackground
     * @param location
     * @param body
     * @param username
     * @param fbID
     * @param timestamp
     * @param ll
     */

    public void storePostInBackground(String placeIcon, String location, String body, String username, String fbID, String timestamp, LatLng ll){

        new StorePostAsyncTask(placeIcon, location, body, username, fbID, timestamp, ll).execute();
    }

    public class StorePostAsyncTask extends AsyncTask<Void, Void, Void>{
        String location,  body,  username,  timestamp, fbID, placeIcon;
        LatLng ll;

        public StorePostAsyncTask(String placeIcon, String location, String body, String username, String fbID, String timestamp, LatLng ll){
            this.location = location;
            this.body = body;
            this.username = username;
            this.fbID = fbID;
            this.timestamp = timestamp;
            this.ll=ll;
            this.placeIcon=placeIcon;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                /*URL url = null;
                BufferedReader reader;
                try {
                    url = new URL(SERVER_ADDRESS + "db2.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    StringBuilder sb = new StringBuilder();
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while((line = reader.readLine()) != null) { //Read till there is something available
                        sb.append(line + "\n");     //Reading and saving line by line - not all at once
                    }
                    line = sb.toString();           //Saving complete data received in string, you can do it differently
                    JSONObject jObject  = new JSONObject(line); // json
                    JSONObject data = jObject.getJSONObject("languages"); // get data object
                    String projectname = data.getString("name"); // get the name from data.
                } catch (Exception e) {
                    e.printStackTrace();
                }*/





                ObjectId id;
                Date dNow = new Date();
                SimpleDateFormat ft =new SimpleDateFormat("MM.dd.yyyy");
                //Create Document
                Document textPost = new Document("location", this.location)
                        .append("placeicon", placeIcon)
                        .append("body", this.body)
                        .append("loc", new Document("type", "Point").append("coordinates", java.util.Arrays.asList(this.ll.longitude, this.ll.latitude)))
                        .append("lng", this.ll.longitude)
                        .append("lat", this.ll.latitude)
                        .append("timestamp", this.timestamp)
                        .append("username", this.username)
                        .append("fbID", this.fbID)
                        .append("date", ft.format(dNow));
                //Insert Document
                texts.insertOne(textPost);
                //Update users History
                id = (ObjectId)textPost.get( "_id" );
                //Find user in fb_users
                Document myDoc = findDocument("fbid", fbID);
                Document listItem = new Document("history", new Document("postid", id.toString()).append("location", this.location));
                Document updateQuery = new Document("$push", listItem);
                fb_users.updateOne(myDoc, updateQuery);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * getPostsInBackground
     * @param loc
     * @param ratio
     */
    public void getPostsInBackground(GoogleMap map, ClusterManager clusterManager, LatLng loc, int ratio) {
        pd.show();
        new GetPostAsyncTask(map, clusterManager, loc, ratio).execute();
    }

    public class GetPostAsyncTask extends AsyncTask<Void, Document, ArrayList<Document>> {
        LatLng loc;
        ArrayList<Document> documents;
        GoogleMap map;
        ClusterManager clusterManager;
        int ratio;
        public GetPostAsyncTask(GoogleMap map, ClusterManager clusterManager, LatLng loc, int ratio) {
            this.loc = loc;
            this.ratio=ratio;
            this.map = map;
            this.clusterManager = clusterManager;
            documents = new ArrayList<>();
        }

        @Override
        protected ArrayList<Document> doInBackground(Void... params) {

           /* try {
                texts.createIndex(Indexes.geo2dsphere("loc"));
                Position position = new Position(loc.longitude,loc.latitude);
                String[] a= {"LEUKOS PIRGOS", "NAVARINOU", "ARISTOTELOUS", "PARALIA", "ROLOI", "MYLOS"};
                try (MongoCursor<Document> cursor = texts.find().iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        Random rand=null;
                        int randomNum = rand.nextInt((5 - 0) + 5) + 0;
                        doc.append("location", a[randomNum]);
                        //documents.add(doc);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            try {
                texts.createIndex(Indexes.geo2dsphere("loc"));
                Position position = new Position(loc.longitude,loc.latitude);
                Point point = new Point(position);
                try (MongoCursor<Document> cursor = texts.find(near("loc", point, ratio * 1000.0, 0.0)).iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        documents.add(doc);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return documents;
        }

        @Override
        protected void onPostExecute(ArrayList<Document> docs) {
            pd.dismiss();
            //Add Cluser items in clusterManager with the help of documents
            for (Document document : docs) {
                String postID = document.get("_id").toString();
                ClusterItems offsetItem = new ClusterItems((Double) document.get("lat"), (Double) document.get("lng"), postID);
                this.clusterManager.addItem(offsetItem);
            }
            this.clusterManager.cluster();
            super.onPostExecute(docs);
        }
    }

    public void getRecommendedFriendsInBackground(ClusterManager clusterManager, LatLng loc, int ratio, GetCallback callback, Context context) {
        pd.show();
        new GetRecommendedFriendsAsyncTask(clusterManager, loc, ratio, callback, context).execute();
    }

    public class GetRecommendedFriendsAsyncTask extends AsyncTask<Void, Void, ArrayList<Document>> {
        ClusterManager clusterManager;
        LatLng loc;
        ArrayList<Document> documents;
        int ratio;
        Context context;
        GetCallback callback;

        public GetRecommendedFriendsAsyncTask(ClusterManager clusterManager, LatLng loc, int ratio, GetCallback callback, Context context) {
            this.clusterManager = clusterManager;
            this.loc = loc;
            this.ratio = ratio;
            this.callback = callback;
            this.context = context;
            documents = new ArrayList<>();
        }

        @Override
        protected ArrayList<Document> doInBackground(Void... params) {


            ArrayList<Document> documents = new ArrayList<>();
            ArrayList<Document> nearUsers = new ArrayList<>();//List of documents with users in the ratio area
            ArrayList<Integer> sim = new ArrayList<>();
            com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
            String myID = profile.getId();
            Document me = findDocument("fbid", myID);//Document me from fb_users

            try {
                //Add users in the ratio area in nearUsers
                fb_users.createIndex(Indexes.geo2dsphere("loc"));
                Position position = new Position(loc.longitude, loc.latitude);
                Point point = new Point(position);
                try (MongoCursor<Document> cursor = fb_users.find(near("loc", point, 1000000.0, 0.0)).iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        nearUsers.add(doc);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<Document> list = (ArrayList<Document>) me.get("history");
            ArrayList<String> myPosts = new ArrayList<>();
            HashSet<String> myLocs = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                String postid = list.get(i).get("postid").toString();
                BasicDBObject query = new BasicDBObject();
                query.put("postid", postid);
                Document myDoc = fb_users.find(query).first();
                myPosts.add(myDoc.get("body").toString());
                myLocs.add(myDoc.get("location").toString());
            }

            HashSet<String> myWords = new HashSet<>();
            for (String post : myPosts) {
                String[] tokens = post.split("\\s+");
                for (int i = 0; i < tokens.length; i++) {
                    myWords.add(tokens[i]);
                }
            }


            for (Document nearUser : nearUsers) {

                ArrayList<Document> userHistory;
                ArrayList<String> userPosts = new ArrayList<>();
                HashSet<String> userLocs = new HashSet<>();
                HashSet<String> userWords = new HashSet<>();

                if (!nearUser.get("fbid").equals(myID)) {
                    userHistory = (ArrayList<Document>) nearUser.get("history");
                    for (int i = 0; i < userHistory.size(); i++) {
                        String npostid = list.get(i).get("postid").toString();
                        BasicDBObject nquery = new BasicDBObject();
                        nquery.put("postid", npostid);
                        Document myDoc = fb_users.find(nquery).first();
                        userPosts.add(myDoc.get("body").toString());
                        userLocs.add(myDoc.get("location").toString());

                    }

                    for (String post : userPosts) {
                        String[] tokens = post.split("\\s+");
                        for (int i = 0; i < tokens.length; i++) {
                            userWords.add(tokens[i]);
                        }
                    }

                    int commonWords = 0;
                    int commonLocs = 0;
                    Iterator<String> it1 = myWords.iterator();
                    Iterator<String> it2 = myLocs.iterator();

                    while (it1.hasNext()) {
                        if (userWords.contains(it1.next())) {
                            commonWords++;
                        }
                    }
                    while (it2.hasNext()) {
                        if (userWords.contains(it2.next())) {
                            commonLocs++;
                        }
                    }

                    int similarity = 0;
                    int simW = commonWords / (myWords.size() + userWords.size());
                    int simL = commonLocs / (myLocs.size() + userLocs.size());
                    similarity = 2 * (simW) + 2 * (simL);
                    sim.add(similarity);


                }
            }
            Collections.sort(sim);
            int topK = 5;
            for(int i = 0; i<topK; i++){
                Document friend = findDocument("fbid", sim.get(i).toString());
                documents.add(friend);
            }


            return documents;
        }

        @Override
        protected void onPostExecute(ArrayList<Document> docs) {
            pd.dismiss();
            callback.done(docs);
            super.onPostExecute(docs);
        }



    }

    public void getRecommendedTextualPostsInBackground(ClusterManager clusterManager, LatLng loc, int ratio, GetCallback callback, Context context) {
        pd.show();
        new GetRecommendedTextualPostsAsyncTask(clusterManager, loc, ratio, callback, context);
    }

    public class GetRecommendedTextualPostsAsyncTask extends  AsyncTask<Void, Void, ArrayList<Document>> {
        ClusterManager clusterManager;
        LatLng loc;
        ArrayList<Document> documents;
        int ratio;
        Context context;
        GetCallback callback;

        public GetRecommendedTextualPostsAsyncTask(ClusterManager clusterManager, LatLng loc, int ratio, GetCallback callback,Context context) {
            this.clusterManager = clusterManager;
            this.loc = loc;
            documents = new ArrayList<>();
            this.ratio = ratio;
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected ArrayList<Document> doInBackground(Void... params) {
            ArrayList<Document> nearUsers = new ArrayList<>();//List of documents with users in the ratio area
            com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
            String userID = profile.getId();
            Document  me = findDocument("fbid", userID);//Document me from fb_users

            try {
                //Add users in the ratio area in nearUsers
                fb_users.createIndex(Indexes.geo2dsphere("loc"));
                Position position = new Position(loc.longitude, loc.latitude);
                Point point = new Point(position);
                try (MongoCursor<Document> cursor = fb_users.find(near("loc", point, 1000000.0, 0.0)).iterator()) {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        nearUsers.add(doc);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<Document> myRatings = (ArrayList<Document>)me.get("ratings");
            HashMap<String, Double> myRatedPosts = new HashMap<>();
            ArrayList<String> muchLikeSuchWow = new ArrayList<>();
            double max = 0;
            for(Document doc : myRatings){
                double rating = doc.getDouble("rating");
                String postid = doc.getString("postid");
                myRatedPosts.put(postid, rating);
                //for later
                if(rating>max){
                    muchLikeSuchWow.clear();
                    muchLikeSuchWow.add(postid);
                }
                else if(rating==max){
                    muchLikeSuchWow.add(postid);
                }
            }

            ArrayList<String> notRatedByMe = new ArrayList<>();
            HashMap<String, Double> ratingsSimilarity = new HashMap<>();
            HashMap<String, HashMap<String, Double>> nearUsersRatings = new HashMap<>();


            for(Document nearUser : nearUsers){
                if(!nearUser.get("fbid").toString().equals(userID)) {
                    HashMap<String, Double> nearUserRatings = new HashMap<>();
                    ArrayList<Document> userRatings = (ArrayList<Document>) nearUser.get("ratings");
                    for(Document userRating: userRatings){
                        String postid = userRating.getString("postid");
                        if(!myRatedPosts.containsKey(postid)){
                            notRatedByMe.add(postid);
                            myRatings.add(userRating);
                        }
                        double r = userRating.getInteger("rating");
                        nearUserRatings.put(postid, r);
                    }
                    nearUsersRatings.put(nearUser.getString("fbid"), nearUserRatings);
                }
            }

            for (String aPost : myRatedPosts.keySet()) {
                for(String fbid : nearUsersRatings.keySet()){
                    HashMap<String, Double> nearUserRatings =  nearUsersRatings.get(fbid);
                    if(!nearUserRatings.containsKey(aPost)){
                        nearUserRatings.put(aPost, 0.0);
                        nearUsersRatings.put(fbid, nearUserRatings);
                    }
                }
            }

            for(String postid: notRatedByMe){
                myRatedPosts.put(postid, 0.0);
            }

            for(String fbid: nearUsersRatings.keySet()){
                HashMap<String, Double> nearUserRatings = nearUsersRatings.get(fbid);
                double sum1 = 0;
                double sum2 = 0;
                double sum3 = 0;
                double result;
                for(String postid: nearUserRatings.keySet()){
                    sum1 += myRatedPosts.get(postid) * nearUserRatings.get(postid);
                    sum2 += Math.pow(myRatedPosts.get(postid), 2);
                    sum3 += Math.pow(nearUserRatings.get(postid), 2);
                }
                sum2 = Math.sqrt(sum2);
                sum3 = Math.sqrt(sum3);
                result = (sum1/(sum2*sum3));
                ratingsSimilarity.put(fbid, result);
            }

            for(String postid: notRatedByMe){
                double sum1 = 0;
                double sum2 = 0;
                double result = 0;
                for(String fbid: nearUsersRatings.keySet()){
                    sum1 += nearUsersRatings.get(fbid).get(postid) * ratingsSimilarity.get(fbid);
                    sum2 += ratingsSimilarity.get(fbid);
                }
                result = sum1/sum2;
                myRatedPosts.put(postid, result);
            }

            /********************************************************************/

            HashMap<String, HashSet<String>> postWords = new HashMap<>();

            for(Document doc: myRatings) {
                String postid = doc.getString("body");
                HashSet<String> words = new HashSet<>();
                String body = doc.getString("body");
                String[] tokens = body.split("\\s+");
                for (int i = 0; i < tokens.length; i++) {
                    words.add(tokens[i]);
                }
                postWords.put(postid, words);
            }
            HashMap<String, HashMap<String, Double>> postSim = new HashMap<>();
            for(String postid: muchLikeSuchWow){
                HashMap<String, Double> sim = new HashMap<>();
                HashSet<String> words1 = postWords.get(postid);
                for(String id: postWords.keySet()){
                    HashSet<String> words2 = postWords.get(id);
                    HashSet<String> allWords = new HashSet<>();
                    allWords.addAll(words1);
                    allWords.addAll(words2);
                    int sum1 = 0;
                    int sum2 = 0;
                    int sum3 = 0;
                    Iterator<String> iterator = allWords.iterator();
                    while(iterator.hasNext()){
                        String word = iterator.next();
                        if(words1.contains(word) && !words2.contains(word)){
                            sum2++;
                        }
                        else if(!words1.contains(word) && words2.contains(word)){
                            sum3++;
                        }
                        else {
                            sum1++;
                            sum2++;
                            sum3++;
                        }
                    }

                    Double sr2 = Math.sqrt(sum2);
                    Double sr3 = Math.sqrt(sum3);
                    Double result = sum1/(sr2*sr3);
                    sim.put(id, result);

                }

                postSim.put(postid, sim);
            }




            return null;
        }
    }

    public void getRecommendedLocationsInBackground(ClusterManager clusterManager, LatLng loc, int ratio, GetCallback callback, Context context) {
        pd.show();
        new GetRecommendedLocationsAsyncTask(clusterManager, loc, ratio, callback, context).execute();
    }

    public class GetRecommendedLocationsAsyncTask extends AsyncTask<Void, Void, ArrayList<Document>> {
        ClusterManager clusterManager;
        LatLng loc;
        ArrayList<Document> documents;
        int ratio;
        Context context;
        GetCallback callback;

        public GetRecommendedLocationsAsyncTask(ClusterManager clusterManager, LatLng loc, int ratio, GetCallback callback, Context context) {
            this.clusterManager=clusterManager;
            this.loc = loc;
            this.ratio=ratio;
            this.callback=callback;
            this.context = context;
            documents = new ArrayList<>();
        }


        @Override
        protected ArrayList<Document> doInBackground(Void... params) {
            String tag="poueisai";
            ArrayList<Document> nearUsers = new ArrayList<>();//List of documents with users in the ratio area
            int i=0;
            Log.d(tag, String.valueOf(i));i++;
            com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
            String userID = profile.getId();
            Document  me = findDocument("fbid", userID);//Document me from fb_users
            //try {
            Log.d(tag, String.valueOf(i));i++;
            //Add users in the ratio area in nearUsers
            fb_users.createIndex(Indexes.geo2dsphere("loc"));
            Position position = new Position(loc.longitude,loc.latitude);
            Point point = new Point(position);
            try (MongoCursor<Document> cursor = fb_users.find(near("loc", point, ratio*1000.0, 0.0)).iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    nearUsers.add(doc);
                }
            }
            ArrayList<Document> myRatings = (ArrayList<Document>) me.get("ratings");
            HashMap<String, Double> myLocRatings = new HashMap<>();
            HashMap<String, Integer> myLocPosts = new HashMap<>();
            for(Document myRating : myRatings){Log.d(tag, String.valueOf(i));i++;
                String location = myRating.get("location").toString();
                Double rating = Double.parseDouble(myRating.get("rating").toString());
                if(!myLocRatings.containsKey(location)){Log.d(tag, String.valueOf(i));i++;
                    myLocRatings.put(location, rating);
                    myLocPosts.put(location, 1);
                }else{
                    int rateCount = myLocPosts.get(location);Log.d(tag, String.valueOf(i));i++;
                    Double rateSum = myLocRatings.get(location);
                    myLocPosts.put(location, ++rateCount);
                    myLocRatings.put(location, rating + rateSum);
                }
            }

            for(String location : myLocRatings.keySet()){Log.d(tag, String.valueOf(i));i++;
                Double rateSum = myLocRatings.get(location);
                int rateCount = myLocPosts.get(location);
                Double middle = rateSum/rateCount;
                myLocRatings.put(location, middle);
            }
            ArrayList<String> notRatedByMe = new ArrayList<>();
            HashMap<String, Double> ratingsSimilarity = new HashMap<>();//Map with key a nearUser and value
            //similarity rating based on ratings
            HashMap<String, HashMap<String, Double>> nearUserIDWithRates = new HashMap<>();
            for(Document nearUser : nearUsers) {Log.d(tag, String.valueOf(i));i++;
                if (!nearUser.get("fbid").toString().equals(userID)) {
                    ArrayList<Document> nearUserRatings = (ArrayList<Document>) nearUser.get("ratings");
                    HashMap<String, Double> nearUserLocRatings = new HashMap<>();
                    HashMap<String, Integer> nearUserLocPosts = new HashMap<>();

                    for (Document nearUserRating : nearUserRatings) {Log.d(tag, String.valueOf(i));i++;
                        String location = nearUserRating.get("location").toString();
                        if (!myLocRatings.containsKey(location)) {Log.d(tag, String.valueOf(i));i++;
                            notRatedByMe.add(location);
                        }
                        Double rating = Double.parseDouble(nearUserRating.get("rating").toString());
                        if (!nearUserLocRatings.containsKey(location)) {Log.d(tag, String.valueOf(i));i++;
                            nearUserLocRatings.put(location, rating);
                            nearUserLocPosts.put(location, 1);
                        } else {Log.d(tag, String.valueOf(i));i++;
                            int rateCount = nearUserLocPosts.get(location);
                            Double rateSum = nearUserLocRatings.get(location);
                            nearUserLocPosts.put(location, ++rateCount);
                            nearUserLocRatings.put(location, rating + rateSum);
                        }
                    }

                    for (String location : nearUserLocRatings.keySet()) {Log.d(tag, String.valueOf(i));i++;
                        Double rateSum = nearUserLocRatings.get(location);
                        int rateCount = nearUserLocPosts.get(location);
                        Double middle = rateSum / rateCount;
                        nearUserLocRatings.put(location, middle);
                    }
                    nearUserIDWithRates.put(nearUser.get("fbid").toString(), nearUserLocRatings);
                }
            }

            for (String aLoc : myLocRatings.keySet()) {
                for(String fbid : nearUserIDWithRates.keySet()){
                    HashMap<String, Double> nearUserLocationsVisited =  nearUserIDWithRates.get(fbid);
                    if(!nearUserLocationsVisited.containsKey(aLoc)){
                        nearUserLocationsVisited.put(aLoc, 0.0);
                        nearUserIDWithRates.put(fbid, nearUserLocationsVisited);
                    }
                }
            }
            for(String fbid : nearUserIDWithRates.keySet()) {
                HashMap<String, Double> nearUserLocationsVisited =  nearUserIDWithRates.get(fbid);
                double sum1 = 0;
                double sum2 = 0;
                double sum3 = 0;
                double multiply;
                double result;
                for (String loc : myLocRatings.keySet()) {
                    sum1 += myLocRatings.get(loc) * nearUserLocationsVisited.get(loc);
                    sum2 += Math.pow(myLocRatings.get(loc), 2);
                    sum3 += Math.pow(nearUserLocationsVisited.get(loc), 2);
                }
                sum2 = Math.sqrt(sum2);
                sum3 = Math.sqrt(sum3);
                multiply = sum2 * sum3;
                result = sum1 / multiply;
                ratingsSimilarity.put(fbid, result);
            }


            for(String notRated : notRatedByMe){Log.d(tag, String.valueOf(i));i++;
                Double sumUp=0.0;
                Double sumDown=0.0;
                for(String fbid : nearUserIDWithRates.keySet()){Log.d(tag, String.valueOf(i));i++;
                    HashMap<String, Double> getIt = nearUserIDWithRates.get(fbid);
                    Double locRate = getIt.get(notRated);
                    Double similarityValue = ratingsSimilarity.get(fbid);
                    sumUp+=locRate*similarityValue;
                    sumDown+=similarityValue;
                }
                myLocRatings.put(notRated, sumUp / sumDown);
            }
            //myLocRatings is Aul

            ArrayList<Document> myHistory = (ArrayList<Document>) me.get("history");
            ArrayList<String> notVisitedByMe = new ArrayList<>();
            HashMap<String, Double> visitedSimilarity = new HashMap<>();
            HashMap<String, Double> myLocationsVisited = new HashMap<>();
            HashMap<String, ArrayList<String>> recommendedPosts = new HashMap<>();
            for(Document post : myHistory){Log.d(tag, String.valueOf(i));i++;
                myLocationsVisited.put(post.get("location").toString(), 1.0);
                Log.d("tivisited1", post.get("location").toString());
            }

            HashMap<String, HashMap<String, Double>> nearUserIDWithVisited = new HashMap<>();
            for(Document nearUser : nearUsers){Log.d(tag, String.valueOf(i));i++;
                if(!nearUser.get("fbid").toString().equals(userID)){
                    ArrayList<Document> nearUserHistory = (ArrayList<Document>) nearUser.get("history");
                    HashMap<String, Double> nearUserLocationsVisited = new HashMap<>();
                    for(Document post : nearUserHistory){
                        if(!myLocationsVisited.containsKey(post.get("location").toString())){
                            Log.d("tivisited2", post.get("location").toString());

                            notVisitedByMe.add(post.get("location").toString());
                            if(recommendedPosts.containsKey(post.get("location").toString())){
                                ArrayList<String> postids =recommendedPosts.get(post.get("location").toString());
                                postids.add(post.get("location").toString());
                                recommendedPosts.put(post.get("location").toString(), postids);
                            }else{
                                ArrayList<String> postids = new ArrayList<>();
                                postids.add(post.get("postid").toString());
                                recommendedPosts.put(post.get("location").toString(), postids);
                            }

                        }
                        nearUserLocationsVisited.put(post.get("location").toString(), 1.0);
                    }
                    nearUserIDWithVisited.put(nearUser.get("fbid").toString(), nearUserLocationsVisited);
                }
            }
            for(String fbid : nearUserIDWithVisited.keySet()){
                HashMap<String, Double> nearUserLocationsVisited =  nearUserIDWithVisited.get(fbid);
                for(String loc : nearUserLocationsVisited.keySet()){
                    if(!myLocationsVisited.containsKey(loc)){
                        myLocationsVisited.put(loc, 0.0);
                    }
                }
            }

            for (String aLoc : myLocationsVisited.keySet()) {
                for(String fbid : nearUserIDWithVisited.keySet()){
                    HashMap<String, Double> nearUserLocationsVisited =  nearUserIDWithVisited.get(fbid);
                    if(!nearUserLocationsVisited.containsKey(aLoc)){
                        nearUserLocationsVisited.put(aLoc, 0.0);
                        nearUserIDWithVisited.put(fbid, nearUserLocationsVisited);
                    }
                }
            }
            for(String fbid : nearUserIDWithVisited.keySet()) {
                HashMap<String, Double> nearUserLocationsVisited =  nearUserIDWithVisited.get(fbid);
                double sum11 = 0;
                double sum22 = 0;
                double sum33 = 0;
                double multiply2;
                double result2;
                for (String loc : myLocationsVisited.keySet()) {
                    Log.d(tag, String.valueOf(i));
                    i++;
                    sum11 += myLocationsVisited.get(loc) * nearUserLocationsVisited.get(loc);
                    sum22 += Math.pow(myLocationsVisited.get(loc), 2);
                    sum33 += Math.pow(nearUserLocationsVisited.get(loc), 2);
                }
                sum22 = Math.sqrt(sum22);
                sum33 = Math.sqrt(sum33);
                multiply2 = sum22 * sum33;
                result2 = sum11 / multiply2;
                visitedSimilarity.put(fbid, result2);
            }



            for(String notVisited : notVisitedByMe){
                Double sumUp=0.0;
                Double sumDown=0.0;
                for(String fbid : nearUserIDWithVisited.keySet()){Log.d(tag, String.valueOf(i));i++;
                    HashMap<String, Double> getIt = nearUserIDWithVisited.get(fbid);
                    for(String a : getIt.keySet()){
                        Log.d("tigbg", a + " "+ getIt.get(a));
                    }
                    Log.d("tigbg", "*********");
                    Double locVisited = getIt.get(notVisited);
                    Double similarityValue = visitedSimilarity.get(fbid);
                    Log.d("tiexeigamw", notVisited);
                    sumUp+=locVisited*similarityValue;
                    sumDown+=similarityValue;
                }
                myLocationsVisited.put(notVisited, sumUp/sumDown);
            }

            //nearUserIDWithRates;
            for(String fbid : nearUserIDWithRates.keySet()){
                HashMap<String, Double> aa = nearUserIDWithRates.get(fbid);
                for(String a : aa.keySet()){
                    Log.d("nearUserIDWithRates", "id: " + fbid + " location: " + a + " rate: " + aa.get(a) );
                }
            }

            //ratingsSimilarity;
            for(String fbid : ratingsSimilarity.keySet()){
                Log.d("ratingsSimilarity", " id: " + fbid + " similarity: " + ratingsSimilarity.get(fbid) );

            }

            //myLocationsRates with predictions;
            for(String loc : myLocRatings.keySet()){
                Log.d("Rateswithpredictions", " location: " + loc + " rate: " + myLocRatings.get(loc) );

            }

            //nearUserIDWithVisited;
            for(String fbid : nearUserIDWithVisited.keySet()){
                HashMap<String, Double> aa = nearUserIDWithVisited.get(fbid);
                for(String a : aa.keySet()){
                    Log.d("nearUserIDWithVisited", "id: " + fbid + " location: " + a + " visited: " + aa.get(a) );
                }
            }

            //visitedSimilarity;
            for(String fbid : visitedSimilarity.keySet()){
                Log.d("visitedSimilarity", " id: " + fbid + " similarity: " + visitedSimilarity.get(fbid) );

            }

            //myLocationsVisited with predictions;
            for(String locVisited : myLocationsVisited.keySet()){
                Log.d("Visitedwithpredicti", " location: " + locVisited + " visited: " + myLocationsVisited.get(locVisited) );

            }

            HashMap<String, Double> finalTable = new HashMap<>();
            for(String location : myLocationsVisited.keySet()){
                //if(notVisitedByMe.contains(location))
                try {
                    finalTable.put(location, 0.8 * myLocationsVisited.get(location) + 0.2 * myLocRatings.get(location));
                }catch (Exception e){
                    finalTable.put(location, 0.8 * myLocationsVisited.get(location));

                }
            }
            for(String locVisited : finalTable.keySet()){
                Log.d("locationsValue", " location: " + locVisited + " value: " + finalTable.get(locVisited));

            }
            texts.createIndex(Indexes.geo2dsphere("loc"));
            //Find all the posts that exists in the ratio area
            MongoCursor<Document> cursor = texts.find(near("loc", point, ratio*1000.0, 0.0)).iterator();
            Log.d("findfind", cursor.toString());
            try {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String location = doc.get("location").toString();
                    Log.d("alocationsValue", " location: " + location);
                    Log.d("locss", " location1: " + location + " location2 " + finalTable.get(location));

                    if(/*finalTable.get(location) > 0.3 (??) &&*/  notVisitedByMe.contains(location)) {
                        String a = doc.get("fbID").toString();
                        if(!a.equals(userID)) {
                            //Log.d("locss2", " location1: " + location);
                            documents.add(doc);
                        }
                    }

                }
            } finally {
                cursor.close();
            }


            //} catch (Exception e) {
            //Log.d("tithes666", e.toString());
            //}


            return documents;
        }
        @Override
        protected void onPostExecute(ArrayList<Document> docs) {
            pd.dismiss();
            callback.done(docs);
            super.onPostExecute(docs);
        }
    }


    /**
     * getRecommendedPostsInBackground
     * @param clusterManager
     * @param loc
     * @param ratio
     * @param callback
     * @param context
     */
    public void getRecommendedPostsInBackground(ClusterManager clusterManager, LatLng loc, int ratio, GetCallback callback, Context context) {
        pd.show();
        new GetRecommendedPostAsyncTask(clusterManager, loc, ratio, callback, context).execute();
    }

    public class GetRecommendedPostAsyncTask extends AsyncTask<Void, Void, ArrayList<Document>> {
        ClusterManager clusterManager;
        LatLng loc;
        ArrayList<Document> documents;
        int ratio;
        Context context;
        GetCallback callback;

        public GetRecommendedPostAsyncTask(ClusterManager clusterManager, LatLng loc, int ratio, GetCallback callback, Context context) {
            this.clusterManager=clusterManager;
            this.loc = loc;
            this.ratio=ratio;
            this.callback=callback;
            this.context = context;
            documents = new ArrayList<>();
        }


        @Override
        protected ArrayList<Document> doInBackground(Void... params) {
            try {
                ArrayList<Document> nearUsers = new ArrayList<>();//List of documents with users in the ratio area

                com.facebook.Profile profile = com.facebook.Profile.getCurrentProfile();
                String userID = profile.getId();
                Document  me = findDocument("fbid", userID);//Document me from fb_users

                try {
                    //Add users in the ratio area in nearUsers
                    fb_users.createIndex(Indexes.geo2dsphere("loc"));
                    Position position = new Position(loc.longitude,loc.latitude);
                    Point point = new Point(position);
                    try (MongoCursor<Document> cursor = fb_users.find(near("loc", point, 1000000.0, 0.0)).iterator()) {
                        while (cursor.hasNext()) {
                            Document doc = cursor.next();
                            nearUsers.add(doc);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Get my rating array from Document me and store them in myRatings
                ArrayList<Document> list = (ArrayList<Document>) me.get("ratings");
                HashMap<String, Integer> myRatings = new HashMap<>();
                for(int i=0; i<list.size(); i++){
                    myRatings.put(list.get(i).get("postid").toString(), Integer.parseInt(list.get(i).get("rating").toString()));
                }

                /**/
                String tag ="ddd";
                for (String id : myRatings.keySet()) {
                    //Log.d(tag, "user me | postid: " + id + " rate: " + rmap.get(id));
                }
                /**/

                ArrayList<String> finalPostIDs = new ArrayList<>();//List with the final post ids that will recommend

                HashMap<String, Double> similarity = new HashMap<>();//Map with key a nearUser and value
                //similarity rating based on ratings

                HashMap<String, ArrayList<String>> usersRatedPosts = new HashMap<>();//Map with key a userFbid and value
                //a List of the posts he rated

                HashMap<String, HashMap<String, Integer>> following = new HashMap<>();
                HashMap<String, HashMap<String, Integer>> followers = new HashMap<>();
                HashMap<String, Integer> followingUsers=null;
                HashMap<String, Integer> followersUsers=null;

                for (Document nearUser : nearUsers) {//For every user in ratio are

                    HashMap<String, Integer> userRatings;
                    if (!nearUser.get("fbid").toString().equals(userID)) {
                        ArrayList<Document> list2  = (ArrayList<Document>) nearUser.get("ratings");
                        userRatings = new HashMap<>();//For every new user build a map with key a postID
                        //and value the rating he made on this post.
                        //Useful to build the similarity

                        ArrayList<String> ratedPosts = new ArrayList<>();//List ONLY with the postIDs he rated.
                        //Useful to have only the posts he rated
                        for(int i=0; i<list2.size(); i++) {
                            userRatings.put(list2.get(i).get("postid").toString(), Integer.parseInt(list2.get(i).get("rating").toString()));
                            ratedPosts.add(list2.get(i).get("postid").toString());
                        }
                        usersRatedPosts.put(nearUser.get("fbid").toString(), ratedPosts);

                        //Add zeros in the posts they didn't rate. Useful to build the similarity
                        for(String postID : myRatings.keySet()){
                            if(!userRatings.containsKey(postID)){
                                userRatings.put(postID, 0);
                            }
                        }
                        for(String postID : userRatings.keySet()){
                            if(!myRatings.containsKey(postID)){
                                myRatings.put(postID, 0);
                            }
                        }
                        //Compute similarity of user Document me and Document nearUser
                        double sum1 = 0;
                        double sum2 = 0;
                        double sum3 = 0;
                        double multiply;
                        double result;
                        for (String postID : myRatings.keySet()) {
                            sum1 += myRatings.get(postID) * userRatings.get(postID);
                            sum2 += Math.pow(myRatings.get(postID), 2);
                            sum3 += Math.pow(userRatings.get(postID), 2);
                        }
                        sum2 = Math.sqrt(sum2);
                        sum3 = Math.sqrt(sum3);
                        multiply = sum2 * sum3;
                        result = sum1 / multiply;






                        similarity.put(nearUser.get("fbid").toString(), result);

                        /**/
                        for(String fbid : similarity.keySet()){
                            Log.d("tithes666", fbid + " " + similarity.get(fbid));
                        }
                        /**/

                    }
                }//End of building similarities
                for(String a : following.keySet()){
                    for(String aa : followingUsers.keySet()){
                        Log.d("ffff", "user " + a + " is following " + aa + " ? " + followingUsers.get(aa));
                    }
                }
                for(String b : followers.keySet()){
                    for(String bb : followersUsers.keySet()){
                        Log.d("ffff", "user " + b + " is followd by " + bb + " ? " + followingUsers.get(bb));
                    }
                }

                HashMap sortedSimilarities = sortHashMapByValuesD(similarity);//Sort similarities
                int topK=0;
                ArrayList<String> recommendedUsers = new ArrayList<>();
                for(Object addTopK : sortedSimilarities.keySet()){
                    Log.d(tag, "dasds6666666" +sortedSimilarities.get(addTopK));
                    recommendedUsers.add(addTopK.toString());//add top similar users in recommendedUsers
                    topK++;
                    //if(topK==4)
                    // break;
                }
                //Add postIDs that recommended users have rated to
                for(String recommendedUser: recommendedUsers){
                    Log.d(tag,recommendedUser );
                    for(String postID : usersRatedPosts.get(recommendedUser)){
                        finalPostIDs.add(postID);//finalPostIDs
                    }
                }
                texts.createIndex(Indexes.geo2dsphere("loc"));
                Position position = new Position(loc.longitude,loc.latitude);
                Point point = new Point(position);
                //Find all the posts that exists in the ratio area
                MongoCursor<Document> cursor = texts.find(near("loc", point, ratio*1000.0, 0.0)).iterator();
                try {
                    while (cursor.hasNext()) {
                        Document doc = cursor.next();
                        if(finalPostIDs.contains(doc.get("_id").toString())) {//if post of area exists in finalPostIDs
                            String a = doc.get("fbID").toString();
                            if(!a.equals(userID))//if this post is not mine
                                documents.add(doc);//add it to final recommended posts documents array
                        }

                    }
                } finally {
                    cursor.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return documents;
        }
        @Override
        protected void onPostExecute(ArrayList<Document> docs) {
            pd.dismiss();
            if(callback!=null) {//this if for the list view in MenuActivity
                Log.d("dqdqdq", docs.toString());
                callback.done(docs);
            }else {//this is for the cluster view in MapsActivity
                for (Document document : docs) {
                    Log.d("oooo", document.toString());
                    String postID = document.get("_id").toString();
                    ClusterItems offsetItem = new ClusterItems((Double) document.get("lat"), (Double) document.get("lng"), postID);
                    clusterManager.addItem(offsetItem);

                }
                Log.d("dqdqdq", docs.toString());
                clusterManager.cluster();
            }

            super.onPostExecute(docs);
        }
    }

    /**
     * storeFbUserInBackground
     * @param fbid
     * @param username
     * @param following
     * @param followers
     * @param favorites
     * @param history
     */
    public void storeFbUserInBackground(String fbid, String username, String following, String followers, String favorites, String history, LatLng loc){
        new StoreFbUserAsyncTask(fbid, username, following, followers, favorites, history, loc).execute();
    }

    public class StoreFbUserAsyncTask extends AsyncTask<Void, Void, Void>{
        String fbid, username, following, followers, favorites, history;
        LatLng loc;
        public StoreFbUserAsyncTask(String fbid, String username, String following, String followers, String favorites, String history, LatLng loc){
            this.fbid = fbid;
            this.username = username;
            this.following = following;
            this.followers = followers;
            this.favorites = favorites;
            this.history = history;
            this.loc=loc;
        }

        @Override
        protected Void doInBackground(Void... params) {
            BasicDBObject query = new BasicDBObject();
            query.put("fbid", this.fbid);
            Document myDoc = fb_users.find(query).first();
            if(myDoc==null) {
                Document fb_user = new Document("fbid", this.fbid)
                        .append("username", this.username)
                        .append("following", java.util.Arrays.asList())
                        .append("followers", java.util.Arrays.asList())
                        .append("favorites", java.util.Arrays.asList())
                        .append("history", java.util.Arrays.asList())
                        .append("ratings", java.util.Arrays.asList())
                        .append("loc", new Document("type", "Point").append("coordinates", java.util.Arrays.asList(loc.longitude, loc.latitude)))
                        .append("online", 1);

                fb_users.insertOne(fb_user);
            }else{
                Document listItem = new Document("loc", new Document("type", "Point").append("coordinates", java.util.Arrays.asList(loc.longitude, loc.latitude)));
                Document updateQuery = new Document("$set", listItem);
                fb_users.updateOne(myDoc, updateQuery);
            }
            return null;
        }
    }

    public void updateOnline(String fbid, boolean login){
        new updateOnlineAsync(fbid, login).execute();
    }

    public class updateOnlineAsync extends AsyncTask<Void, Void, Void>{
        String fbid;
        boolean login;

        public updateOnlineAsync(String fbid, boolean login){
            this.fbid=fbid;
            this.login=login;
        }


        @Override
        protected Void doInBackground(Void... params) {
            BasicDBObject query = new BasicDBObject();
            query.put("fbid", this.fbid);
            Document myDoc = fb_users.find(query).first();

            if(login){
                Log.d("mpike", "eksw");
                Document listItem = new Document("online", "0");
                Document updateQuery = new Document("$set", listItem);
                fb_users.updateOne(myDoc, updateQuery);
            }else{
                Log.d("mpike", "mesa");
                Document listItem = new Document("online", "1");
                Document updateQuery = new Document("$set", listItem);
                fb_users.updateOne(myDoc, updateQuery);
            }
            return null;
        }
    }

    /**
     * getSinglePostInBackground
     * @param id
     * @param callback
     */
    public void getSinglePostInBackground(String id, GetCallbackSinglePost callback){
        pd.show();
        new GetSinglePostAsyncTask(id, callback).execute();
    }
    public class GetSinglePostAsyncTask extends AsyncTask<Void, Void, Document> {
        GetCallbackSinglePost getCallback;
        String id;

        public GetSinglePostAsyncTask(String id, GetCallbackSinglePost getCallback) {
            this.getCallback=getCallback;
            this.id=id;
        }

        @Override
        protected Document doInBackground(Void... params) {
            Document myDoc = null;
            ArrayList<Document> s;
            try {
                BasicDBObject query = new BasicDBObject();
                query.put("_id", new ObjectId(id));
                myDoc = texts.find(query).first();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return myDoc;
        }
        @Override
        protected void onPostExecute(Document doc) {
            pd.dismiss();
            getCallback.done(doc);
            super.onPostExecute(doc);
        }
    }

    /**
     * getUserPostsInBackground
     * @param id
     * @param callback
     */
    public void getUserPostsInBackground(String id, GetCallback callback){
        pd.show();
        new getUserPostsAsyncTask(id, callback).execute();
    }
    public class getUserPostsAsyncTask extends AsyncTask<Void, Void, ArrayList<Document>> {
        GetCallback getCallback;
        String id;

        public getUserPostsAsyncTask(String id, GetCallback getCallback) {
            this.getCallback=getCallback;
            this.id=id;
        }

        @Override
        protected ArrayList<Document> doInBackground(Void... params) {
            ArrayList<Document> history = null;
            ArrayList<Document> posts = new ArrayList<>();
            try {
                BasicDBObject query = new BasicDBObject();
                query.put("fbid", id);
                Log.d("porepousti", query.toString());
                history = (ArrayList<Document>) fb_users.find(query).first().get("history");
                Log.d("porepousti", history.toString());
                for(Document postid : history){
                    Log.d("porepousti", postid.toString());
                    BasicDBObject query2 = new BasicDBObject();
                    query2.put("_id", new ObjectId(postid.get("postid").toString()));
                    Log.d("porepousti", query2.toString());
                    Document post = texts.find(query2).first();
                    //if(post!=null)
                    posts.add(post);
                }
                Log.d("porepousti", history.toString());
            } catch (Exception e) {
                Log.d("porepousti", "oo");
            }
            return posts;
        }
        @Override
        protected void onPostExecute(ArrayList<Document> history) {
            pd.dismiss();
            getCallback.done(history);
            super.onPostExecute(history);
        }
    }

    /**
     * rateInBackground
     * @param rating
     * @param id
     * @param postId
     */
    public void rateInBackground(String location, int rating, String id, String postId){
        //pd.show();
        new rateInBackgroundASyncTask(location, rating, id, postId).execute();
    }

    public class rateInBackgroundASyncTask extends AsyncTask<Void,Void,Void>{
        int rating;
        String id;
        String postId, location;

        public rateInBackgroundASyncTask(String location, int rating, String id, String postId) {
            this.rating=rating;
            this.id = id;
            this.postId = postId;
            this.location = location;
        }


        @Override
        protected Void doInBackground(Void... params) {

            Document myDoc;
            BasicDBObject query = new BasicDBObject();
            query.put("fbid", this.id);
            myDoc = fb_users.find(query).first();
            Document listItem = new Document("ratings", new Document("rating", rating).append("postid", postId).append("location", location));
            boolean exists=false;
            ArrayList<Document> list  = (ArrayList<Document>) myDoc.get("ratings");
            Iterator<Document> iterateList = list.iterator();
            while(iterateList .hasNext()){
                Document pair = iterateList .next();
                for(String element : pair.keySet()){
                    if(pair.get(element).equals(postId)){
                        exists=true;
                    }
                    System.out.println(element + " " + pair.get(element));
                }
            }

            if(!exists){
                Document updateQuery = new Document("$push", listItem);
                fb_users.updateOne(myDoc, updateQuery);
            }else{
                query.put("ratings.postid", postId);
                BasicDBObject data = new BasicDBObject();
                data.put("ratings.$.rating", rating);
                BasicDBObject command = new BasicDBObject();
                command.put("$set", data);
                fb_users.updateOne(query, command);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //pd.dismiss();

            super.onPostExecute(aVoid);
        }
    }

    /**
     * getClusterDocuments
     * @param nearDocs
     * @param postIds
     */
    public void getClusterDocuments(GetCallback nearDocs, ArrayList<String> postIds){
        new getClusterDocumentsAsyncTask(nearDocs, postIds).execute();
    }
    public class getClusterDocumentsAsyncTask extends AsyncTask<Void, Void, ArrayList<Document>>{
        GetCallback callback;
        ArrayList<String> postIds;
        public getClusterDocumentsAsyncTask(GetCallback callback, ArrayList<String> postIds){
            this.callback = callback;
            this.postIds = new ArrayList<>();
            this.postIds.addAll(postIds);
        }

        @Override
        protected ArrayList<Document> doInBackground(Void... params) {
            ArrayList<Document> documentsReturned = new ArrayList<>();
            for(String id : postIds){
                BasicDBObject query = new BasicDBObject();
                query.put("_id", new ObjectId(id));
                Document doc = texts.find(query).first();
                documentsReturned.add(doc);
            }
            return documentsReturned;
        }
        @Override
        protected void onPostExecute(ArrayList<Document> documentsReturned) {
            pd.dismiss();
            callback.done(documentsReturned);
            super.onPostExecute(documentsReturned);
        }
    }

    public void addFollowersInBackground(String userfbID, String postID, boolean notFollowing, Button follow, Context context){
        new addFollowersAsyncTask(userfbID,  postID, notFollowing, follow, context).execute();
    }

    public class addFollowersAsyncTask extends AsyncTask<Void,Void,Boolean[]>{
        String userfbID,  postID;
        boolean notFollowing;
        Button follow;
        Context context;

        public addFollowersAsyncTask(String userfbID, String postID, boolean notFollowing, Button follow, Context context) {
            this.userfbID = userfbID;
            this.postID = postID;
            this.notFollowing = notFollowing;
            this.follow = follow;
            this.context = context.getApplicationContext();
        }

        @Override
        protected Boolean[] doInBackground(Void... params) {
            String action;

            Profile profile = Profile.getCurrentProfile();
            String targetUserfbID = profile.getId();
            Document targetUser;
            BasicDBObject query2 = new BasicDBObject();
            query2.put("fbid", targetUserfbID);
            targetUser = fb_users.find(query2).first();

            ArrayList<Document> list = (ArrayList<Document>) targetUser.get("following");
            Iterator<Document> iterateList = list.iterator();
            while (iterateList.hasNext()) {
                Document pair = iterateList.next();
                if (pair.get("following").equals(userfbID)) {
                    notFollowing = true;
                }else{

                }
            }

            boolean isMe = false;
            if(targetUserfbID.equals(userfbID)){
                isMe=true;
            }
            Boolean[] returnedBool = new Boolean[2];
            returnedBool[0]=notFollowing;
            returnedBool[1]=isMe;

            action = notFollowing ? "$pull" : "$push";

            Document listItem2 = new Document("following", new Document("following", userfbID));
            Document updateQuery2 = new Document(action, listItem2);
            fb_users.updateOne(targetUser, updateQuery2);

            Document otherUser;
            BasicDBObject query = new BasicDBObject();
            query.put("fbid", userfbID);
            otherUser = fb_users.find(query).first();//vriskei apo fb users ton user pou exei kanei to post
            Document listItem = new Document("followers", new Document("follower", targetUserfbID));
            Document updateQuery = new Document(action, listItem);
            fb_users.updateOne(otherUser, updateQuery);

            return returnedBool;
        }

        @Override
        protected void onPostExecute(Boolean[] notFollowing) {
            pd.dismiss();
            if(!notFollowing[1]){
                if(notFollowing[0]) {
                    follow.setText("Follow");
                }else{
                    follow.setText("UnFollow");
                }
            }

            super.onPostExecute(notFollowing);
        }
    }

    public void checkIfFollowingInBackground(String userfbID, Button follow, Context context){
        new checkIfFollowingAsyncTask(userfbID, follow, context).execute();
    }

    public class checkIfFollowingAsyncTask extends AsyncTask<Void,Void,Boolean[]>{
        String userfbID;
        Button follow;
        Context context;

        public checkIfFollowingAsyncTask(String userfbID, Button follow, Context context) {
            this.userfbID=userfbID;
            this.follow=follow;
            this.context = context.getApplicationContext();
        }

        @Override
        protected Boolean[] doInBackground(Void... params) {
            Profile profile = Profile.getCurrentProfile();
            String targetUserfbID = profile.getId();
            Document targetUser;
            BasicDBObject query2 = new BasicDBObject();
            query2.put("fbid", targetUserfbID);
            targetUser = fb_users.find(query2).first();
            boolean isFollowing = false;
            ArrayList<Document> list = (ArrayList<Document>) targetUser.get("following");
            Iterator<Document> iterateList = list.iterator();
            while (iterateList.hasNext()) {
                Document pair = iterateList.next();
                if (pair.get("following").equals(userfbID)) {
                    isFollowing = true;
                }
            }

            boolean isMe = false;
            if(targetUserfbID.equals(userfbID)){
                isMe=true;
            }
            Boolean[] returnedBool = new Boolean[2];
            returnedBool[0]=isFollowing;
            returnedBool[1]=isMe;

            return returnedBool;
        }

        @Override
        protected void onPostExecute(Boolean[] isFollowing) {
            pd.dismiss();
            if(!isFollowing[1]) {
                if (!isFollowing[0]) {
                    follow.setText("Follow");
                } else {
                    follow.setText("UnFollow");
                }
            }else{
                follow.setText("Your Profile");
                follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(context, UserProfile.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(myIntent);
                    }
                });
            }

            super.onPostExecute(isFollowing);
        }
    }


    public LinkedHashMap sortHashMapByValuesD(HashMap passedMap) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap sortedMap = new LinkedHashMap();

        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)) {
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put((String) key, (Double) val);
                    break;
                }

            }

        }
        return sortedMap;
    }

}

/*
register with SQL
        private String getEncodedData(Map<String,String> data) {
            StringBuilder sb = new StringBuilder();
            for(String key : data.keySet()) {
                String value = null;
                try {
                    value = URLEncoder.encode(data.get(key), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(sb.length()>0)
                    sb.append("&");
                sb.append(key + "=" + value);
            }
            return sb.toString();
        }
            Map<String,String> dataToSend = new HashMap<>();
            dataToSend.put("body", this.body);
            dataToSend.put("title", this.title);
            dataToSend.put("username", this.username);
            dataToSend.put("timestamp", this.timestamp);
            dataToSend.put("lng", String.valueOf(this.ll.longitude));
            dataToSend.put("lat", String.valueOf(this.ll.latitude));
                String encodedStr = getEncodedData(dataToSend);
                URL url = new URL(SERVER_ADDRESS + "db1.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
*/


/*
getPost with SQL
                URL url = new URL(SERVER_ADDRESS + "db2.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while((line = reader.readLine()) != null) { //Read till there is something available
                    sb.append(line + "\n");     //Reading and saving line by line - not all at once
                }
                line = sb.toString();           //Saving complete data received in string, you can do it differently
                JSONObject jObject  = new JSONObject(line); // json
                JSONObject data = jObject.getJSONObject("languages"); // get data object
                String projectname = data.getString("name"); // get the name from data.
 */




















/*
                for(int i=0; i<10000; i++){
                    Random r = new Random();
                    int k = r.nextInt(3-0) + 3;
                    double lng = -50.0 + (50.0 + 50.0) * r.nextDouble();
                    double lat = -50.0 + (50.0 + 50.0) * r.nextDouble();
                    String[] kinds = new String[4];
                    kinds[0]="music";
                    kinds[1]="sports";
                    kinds[2]="concert";
                    kinds[3]="party";
                    char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
                    StringBuilder sb = new StringBuilder();
                    Random random = new Random();
                    for (int ii = 0; ii < 10; ii++) {
                        char c = chars[random.nextInt(chars.length)];
                        sb.append(c);
                    }
                    String output = sb.toString();
                    for (int ii = 0; ii < 10; ii++) {
                        char c = chars[random.nextInt(chars.length)];
                        sb.append(c);
                    }
                    String output2 = sb.toString();
                    for (int ii = 0; ii < 10; ii++) {
                        char c = chars[random.nextInt(chars.length)];
                        sb.append(c);
                    }
                    String output3 = sb.toString();
                    Document textPost2 = new Document("title", kinds[k])
                            .append("body", output)
                            .append("loc", new Document("type", "Point").append("coordinates", java.util.Arrays.asList(lng, lat)))
                            .append("lng", lng)
                            .append("lat", lat)
                            .append("timestamp", this.timestamp)
                            .append("username", output2)
                            .append("fbID", output3);
                    texts.insertOne(textPost2);
                }
 */



















                    /*followingUsers = new HashMap<>();
                    ArrayList<Document> dbFollowing = (ArrayList<Document>) nearUser.get("following");

                    Log.d("ffff", dbFollowing.toString());

                    ArrayList<String> dbFollowingID = new ArrayList<>();
                    for(Document followingUser : dbFollowing){
                        Log.d("fffff", followingUser.get("following").toString());
                        dbFollowingID.add(followingUser.get("following").toString());
                    }
                    for (Document nearUser2 : nearUsers){
                        if(dbFollowingID.contains(nearUser2.get("fbid").toString())){
                            followingUsers.put(nearUser2.get("fbid").toString(), 1);
                        }else{
                            followingUsers.put(nearUser2.get("fbid").toString(), 0);
                        }
                    }
                    following.put(nearUser.get("fbid").toString(), followingUsers);

                    followersUsers = new HashMap<>();
                    ArrayList<Document> dbFollowers = (ArrayList<Document>) nearUser.get("followers");
                    Log.d("ffff", dbFollowers.toString());
                    ArrayList<String> dbFollowersID = new ArrayList<>();
                    for(Document followerUser : dbFollowers){
                        dbFollowersID.add(followerUser.get("follower").toString());
                    }



                    for (Document nearUser3 : nearUsers){
                        if(dbFollowersID.contains(nearUser3.get("fbid").toString())){
                            followersUsers.put(nearUser3.get("fbid").toString(), 1);
                        }else{
                            followersUsers.put(nearUser3.get("fbid").toString(), 0);
                        }
                    }
                    followers.put(nearUser.get("fbid").toString(), followersUsers);*/
