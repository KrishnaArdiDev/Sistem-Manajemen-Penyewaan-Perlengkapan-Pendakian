package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.*;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.*;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.*;

import java.io.IOException;
import java.math.BigDecimal;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.*;
import java.util.*;
import javafx.fxml.FXML;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


public class LoginController {

//#region
@FXML 
    private HBox loginContainer;
@FXML 
    private VBox sideWelkam;
@FXML 
    private ImageView logoTapak;
@FXML 
    private Label labelSelamatDatang;
@FXML 
    private Label labelDeskripsiSambutan;
@FXML 
    private VBox sideLogin;
@FXML 
    private Label judulMasuk;
@FXML 
    private Label subJudulMasuk;
@FXML 
    private VBox formLogin;
@FXML 
    private Label labelEmail;
@FXML 
    private TextField inputEmail;
@FXML 
    private Label labelPassword;
@FXML 
    private PasswordField inputPassword;
@FXML 
    private Hyperlink linkLupaPassword;
@FXML 
    private Button buttonMasuk;
//#endregion

//#region
    @FXML 
    private void initialize(){
        styleSideWelkam();
        styleSideLogin();
    }
//#endregion
//#region
    private void styleSideWelkam(){
        sideWelkam.setStyle(
            "-fx-background-color: #195338;"
        );
        labelSelamatDatang.setStyle(
            "-fx-text-fill: #FFFFFF;"
        );
        labelDeskripsiSambutan.setStyle(
            "-fx-text-fill: #FFFFFF;"
        );
    }
    
    private void styleSideLogin(){
        judulMasuk.setStyle(
            "-fx-text-fill: #000000;"
        );
        subJudulMasuk.setStyle(
            "-fx-text-fill: #868686;"
        );
        labelEmail.setStyle(
            "-fx-text-fill: #000000;"
        );
        inputEmail.setStyle(
            "-fx-border-color: #444444;"+
            "-fx-border-radius: 14;"+
            "-fx-background-radius: 14;"
        );
        labelPassword.setStyle(
            "-fx-text-fill: #000000;"
        );
        inputPassword.setStyle(
            "-fx-border-color: #444444;"+
            "-fx-border-radius: 14;"+
            "-fx-background-radius: 14;"
        );
        buttonMasuk.setStyle(
            "-fx-background-color: #005A32;"+
            "-fx-text-fill: #FFFFFF;"
        );
    }
//#endregion

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("login");
    }
}
