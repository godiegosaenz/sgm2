/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 *
 * @author Fernando
 */
public class CustomExceptionHandlerFactory extends ExceptionHandlerFactory {
    private ExceptionHandlerFactory parent;
  
  public CustomExceptionHandlerFactory(ExceptionHandlerFactory parent) {
    this.parent = parent;
  }
  
  @Override
  public ExceptionHandler getExceptionHandler() {
    ExceptionHandler result = new CustomExceptionHandler(parent.getExceptionHandler());
    return result;
  }
}
