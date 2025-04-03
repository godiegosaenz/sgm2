/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author CarlosLoorVargas
 */
public class ResTareasUsuarios implements Serializable {

    private String user;
    private String mailUser;
    private long currentTasks;
    private long completedTasks;
    private List<TareaWF> tasks;
    private static final Long serialVersionUID = 1L;

    public ResTareasUsuarios() {
    }

    public ResTareasUsuarios(String user, long currentTasks, long completedTasks, List<TareaWF> tasks) {
        this.user = user;
        this.currentTasks = currentTasks;
        this.completedTasks = completedTasks;
        this.tasks = tasks;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMailUser() {
        return mailUser;
    }

    public void setMailUser(String mailUser) {
        this.mailUser = mailUser;
    }

    public long getCurrentTasks() {
        return currentTasks;
    }

    public void setCurrentTasks(long currentTasks) {
        this.currentTasks = currentTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public List<TareaWF> getTasks() {
        return tasks;
    }

    public void setTasks(List<TareaWF> tasks) {
        this.tasks = tasks;
    }

}
