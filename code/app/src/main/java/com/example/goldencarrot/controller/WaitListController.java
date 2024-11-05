package com.example.goldencarrot.controller;

import com.example.goldencarrot.data.model.event.Event;
import com.example.goldencarrot.data.model.waitlist.WaitList;

/**
 * Controller class provides methods to write to Waitlist db and
 * fetch data from events db
 */
public class WaitListController {
    /**
     * WaitList object that will be used to write to firebase
     */
    private WaitList waitList;

    public WaitListController(){
        this.waitList = new WaitList();
    }

    public void InitializeWaitlist(){

    }


}
