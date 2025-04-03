/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.mail.daemon;

/**
 *
 * @author Fernando
 */
public class MailWorker extends Thread {

    public MailWorker() {
        this.setDaemon(true);
    }
    
    @Override
    public void run(){
        while(true){
            
            
            
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                // handle exception here
            }
        }
    }
    
}
