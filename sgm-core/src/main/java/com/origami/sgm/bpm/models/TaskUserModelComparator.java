/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.origami.sgm.bpm.models;

import java.util.Comparator;

/**
 *
 * @author Joao Sanga
 */
public class TaskUserModelComparator implements Comparator<TaskUserModel> {
    
    @Override
    public int compare(TaskUserModel x, TaskUserModel y) {
      // TODO: Handle null x or y values
      int startComparison = compare(x.getNumTramite(), y.getNumTramite());
      return startComparison != 0 ? startComparison
                                  : compare(x.getNumTramite(), y.getNumTramite());
    }
    
    private static int compare(long a, long b) {
    return a < b ? -1
         : a > b ? 1
         : 0;
  }
}
