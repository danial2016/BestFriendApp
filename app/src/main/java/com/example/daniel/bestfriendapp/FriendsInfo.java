package com.example.daniel.bestfriendapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Daniel on 2017-10-27.
 * This class serves as a protocl for the information about a friend that is
 * added to the app.
 */

public class FriendsInfo {
    private JSONArray mArray;

    public FriendsInfo() {
        mArray = new JSONArray();
    }

    /*
    This function takes a JSON-array as parameter which contains all the Friend-objects
    and sets it to the global class variable.
     */
    public void setFriends(String jsonArray) {
        try{
            mArray = new JSONArray(jsonArray);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /*
    Add a friend to the JSON-array of friends.
    This method works like a protocol. A friend has the following attributes:
    - name,
    - phoneNumber,
    - email
    This information is utilized by the application.
     */

    public void addFriend(Friend friend){
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", friend.getName());
            obj.put("phoneNumber", friend.getPhoneNumber());
            obj.put("email", friend.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mArray.put(obj);
    }

    /*
    Check if a friend already exists in the list.
    Used to assure that a friend is not added twice.
     */

    public boolean check(String name){
        try{
            for (int i = 0; i < mArray.length(); i++) {
                JSONObject obj = mArray.getJSONObject(i);
                Friend friend = new Friend(obj.getString("name"), obj.getString("phoneNumber"), obj.getString("email"));
                if(friend.getName().equals(name)){
                    return true;
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }

    /*
    Removes a friend from the JSON-array list
    Note: once this method has been invoked make sure to call setFriends() and update the UI
     */
    public void removeFriend(String name){
        try{
            for (int i = 0; i < mArray.length(); i++) {
                JSONObject obj = mArray.getJSONObject(i);
                Friend friend = new Friend(obj.getString("name"), obj.getString("phoneNumber"), obj.getString("email"));
                if(friend.getName().equals(name)){
                    mArray.remove(i);
                    setFriends(mArray.toString());
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String toString(){
        return mArray.toString();
    }

    /*
    Returns an Arraylist of friends
     */
    public ArrayList<Friend> getFriends() {
        ArrayList<Friend> list = new ArrayList<Friend>();
        try{
            for (int i = 0; i < mArray.length(); i++) {
                JSONObject obj = mArray.getJSONObject(i);
                Friend friend = new Friend(obj.getString("name"), obj.getString("phoneNumber"), obj.getString("email"));
                list.add(friend);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return list;
    }
}
