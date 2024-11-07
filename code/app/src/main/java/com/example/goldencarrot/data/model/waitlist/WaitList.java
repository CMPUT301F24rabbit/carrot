package com.example.goldencarrot.data.model.waitlist;

import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.user.UserImpl;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

/**
 * The {@code WaitList} class represents a waiting list for an event.
 * It stores the event associated with the waitlist and the list of users
 * who are on the waitlist.
 */
public class WaitList implements WaitListConfigurator{
    private String waitListId;
    private String eventId;
    private int limitNumber;
    private ArrayList<UserImpl> userArrayList;


    public WaitList(){

        // Initialize Array List
        this.userArrayList = new ArrayList<UserImpl>();

    }

    public WaitList(final int limitNumber,
                    final String waitListId,
                    final String eventId,
                    final ArrayList<UserImpl> userArrayList) {
        this.limitNumber = limitNumber;
        this.waitListId = waitListId;
        this.eventId = eventId;
        this.userArrayList = userArrayList;
    }


    @Override
    public ArrayList<UserImpl> getUserArrayList() {
        return userArrayList;
    }


    @Override
    public void setUserArrayList(ArrayList<UserImpl> userArrayList) {
        this.userArrayList = userArrayList;
    }

    @Override
    public void setLimitNumber(int limitNumber) {
        this.limitNumber = limitNumber;
    }

    @Override
    public int getLimitNumber() {
        return limitNumber;
    }

    @Override
    public void setEventId(final String eventId){
        this.eventId = eventId;
    }

    @Override
    public String getEventId(){ return this.eventId; }


    /**
     * Returns True if User was added, False if waitList is Full.
     * @param user to add to the list
     * @return boolean
     */
    @Override
    public boolean addUserToWaitList(final UserImpl user) {
        if (isFull()){
            return Boolean.FALSE;
        } else {
            userArrayList.add(user);
            return Boolean.TRUE;
        }
    }

    @Override
    public String getWaitListId() {
        return waitListId;
    }

    @Override
    public void setWaitListId(String waitListId) {
        this.waitListId = waitListId;
    }

    public boolean isFull(){
        return this.userArrayList.size() == this.limitNumber;
    }

    public void removeUserFromWaitList(UserImpl user){
        this.userArrayList.remove(user);
    }
}
