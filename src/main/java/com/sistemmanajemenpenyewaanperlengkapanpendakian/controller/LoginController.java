package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.App;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.*;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.*;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.exception.*;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.*;

import java.io.IOException;
import java.math.BigDecimal;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.time.*;
import java.util.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;



public class LoginController {
// ini penambahan atribut untuk Auth
private final AuthService authService = new AuthService();

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

        // penambahan ini untuk connect tombol masuk dengan proses login
        buttonMasuk.setOnAction(event -> prosesLogin());
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


//#region Proses login
    private void prosesLogin(){
        try{
            authService.login(inputEmail.getText(), inputPassword.getText());
            App.setRoot("main-layout");

        } catch (LoginGagalException e){
            tampilkanPesan(Alert.AlertType.WARNING, "Login Gagal", e.getMessage());
        } catch (IllegalStateException e){
            tampilkanPesan(Alert.AlertType.ERROR, "Kesalahan database", e.getMessage());
        } catch (IOException e){
            tampilkanPesan(Alert.AlertType.ERROR, "Kesalahan aplikasi", "Login berhasil, tapi halaman utama gagal dibuka\n" + e.getMessage());
        }
    }

// penambahan buat show notif ke user
private void tampilkanPesan(Alert.AlertType tipe, String judul, String pesan){
    Alert alert = new Alert(tipe);
    alert.setTitle(judul);
    alert.setHeaderText(null);
    alert.setContentText(pesan);
    alert.showAndWait();
}
//#endregion
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("login");
    }
}
