/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rummy.shared;
import java.io.Serializable;
/**
 *
 * @author pentagonpie
 */

    //Error codes:
    //1-general invalid move
    //2-invalid draw
    //3-trying to discard twice in row
    //4-invalid meld
    //5-not your turn
    //6 -cannot discard
    //7 - cards not same value
    //8 - cards not same suit
    //9 - series not raising values
public class MoveValidationResult implements Serializable{
    private boolean _isValid;
    private int _errorCode;
    
    public MoveValidationResult(boolean isValid, int errorCode){
        _isValid = isValid;
        _errorCode = errorCode;
    }
    
    public boolean isValid(){
        return _isValid;
    }
    
    public int getErrorCode(){
        return _errorCode;
    }
}
