
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import java.io.File;
import java.net.URL;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class ProfileController {

    @FXML
    private ImageView profileImage;

    @FXML
    private TextField storeNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField locationField;

    @FXML
    private Button editButton;

    @FXML
    private Button uploadButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button saveButton;

    private String savedStoreName;
    private String savedEmail;
    private String savedLocation;

    private String currentPhotoUri;
    private String savedPhotoUri;

    private boolean editing;

    @FXML
    private void initialize() {
        profileImage.setClip(new Circle(56, 56, 56));

        MainLayoutController.initializeSharedProfileImage();

        currentPhotoUri =
            MainLayoutController.getSharedProfilePhotoUri();

        savedPhotoUri = currentPhotoUri;

        Image sharedImage =
            MainLayoutController.sharedProfileImageProperty().get();

        if (sharedImage != null) {
            profileImage.setImage(sharedImage);
        }

        savedStoreName = storeNameField.getText();
        savedEmail = emailField.getText();
        savedLocation = locationField.getText();

        aturModeEdit(false);
        aturGayaField();
    }

    // ==================== EDIT PROFILE ====================

    @FXML
    private void handleEdit() {
        simpanSnapshot();
        aturModeEdit(true);
    }

    @FXML
    private void handleSave() {
        String nama = storeNameField.getText().trim();
        String email = emailField.getText().trim();
        String lokasi = locationField.getText().trim();

        if (nama.isEmpty() || email.isEmpty() || lokasi.isEmpty()) {
            tampilkanPeringatan(
                "Nama toko, email, dan lokasi wajib diisi."
            );
            return;
        }

        if (!email.contains("@")) {
            tampilkanPeringatan(
                "Masukkan alamat email yang valid."
            );
            return;
        }

        savedStoreName = nama;
        savedEmail = email;
        savedLocation = lokasi;

        // Memperbarui sumber foto bersama untuk sidebar.
        MainLayoutController.setSharedProfilePhoto(currentPhotoUri);

        savedPhotoUri = currentPhotoUri;

        aturModeEdit(false);

        tampilkanInformasi(
            "Perubahan profil berhasil diterapkan.",
            "Perubahan ini masih sementara dan belum disimpan ke database."
        );
    }

    @FXML
    private void handleCancel() {
        storeNameField.setText(savedStoreName);
        emailField.setText(savedEmail);
        locationField.setText(savedLocation);

        currentPhotoUri = savedPhotoUri;
        muatFoto(currentPhotoUri);

        aturModeEdit(false);
    }

    private void simpanSnapshot() {
        savedStoreName = storeNameField.getText();
        savedEmail = emailField.getText();
        savedLocation = locationField.getText();
        savedPhotoUri = currentPhotoUri;
    }

    private void aturModeEdit(boolean aktif) {
        editing = aktif;

        storeNameField.setEditable(aktif);
        emailField.setEditable(aktif);
        locationField.setEditable(aktif);

        editButton.setDisable(aktif);
        uploadButton.setDisable(false);
        deleteButton.setDisable(aktif);

        cancelButton.setVisible(aktif);
        cancelButton.setManaged(aktif);

        saveButton.setVisible(aktif);
        saveButton.setManaged(aktif);

        aturGayaField();
    }

    private void aturGayaField() {
        String gaya = editing
            ? "-fx-background-color: white;" +
              "-fx-border-color: #00643D;" +
              "-fx-border-radius: 6;" +
              "-fx-background-radius: 6;" +
              "-fx-padding: 8 10;"
            : "-fx-background-color: #EEEEEE;" +
              "-fx-border-color: #999999;" +
              "-fx-border-radius: 6;" +
              "-fx-background-radius: 6;" +
              "-fx-padding: 8 10;";

        storeNameField.setStyle(gaya);
        emailField.setStyle(gaya);
        locationField.setStyle(gaya);
    }

    // ==================== UPLOAD FOTO ====================

    @FXML
    private void handleUploadPhoto() {
        if (!editing) {
            handleEdit();
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Upload Foto Profile");

        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter(
                "Gambar", "*.png", "*.jpg", "*.jpeg"
            ),
            new FileChooser.ExtensionFilter(
                "Semua file", "*.*"
            )
        );

        Window owner = profileImage.getScene() == null
            ? null
            : profileImage.getScene().getWindow();

        File file = chooser.showOpenDialog(owner);

        if (file == null) {
            return;
        }

        String uri = file.toURI().toString();

        Image previewImage =
            new Image(uri, 112, 112, false, true);

        if (previewImage.isError()) {
            tampilkanPeringatan(
                "Foto tidak dapat dibuka. Pilih file gambar lain."
            );
            return;
        }

        ImageView preview = new ImageView(previewImage);
        preview.setFitWidth(110);
        preview.setFitHeight(110);
        preview.setPreserveRatio(false);
        preview.setClip(new Circle(55, 55, 55));

        ButtonType batal = new ButtonType(
            "Batal", ButtonBar.ButtonData.CANCEL_CLOSE
        );

        ButtonType konfirmasi = new ButtonType(
            "Konfirmasi", ButtonBar.ButtonData.OK_DONE
        );

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Upload Profile");
        dialog.setHeaderText("Gunakan foto profil ini?");
        dialog.setContentText(file.getName());
        dialog.setGraphic(preview);

        dialog.getButtonTypes().setAll(batal, konfirmasi);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        Optional<ButtonType> hasil = dialog.showAndWait();

        if (hasil.isPresent() && hasil.get() == konfirmasi) {
            currentPhotoUri = uri;
            profileImage.setImage(previewImage);
        }
    }

    private void muatFoto(String uri) {
        if (uri == null || uri.trim().isEmpty()) {
            MainLayoutController.initializeSharedProfileImage();

            Image fotoDefault =
                MainLayoutController.sharedProfileImageProperty().get();

            profileImage.setImage(fotoDefault);
            return;
        }

        Image image = new Image(uri, 112, 112, false, true);

        if (!image.isError()) {
            profileImage.setImage(image);
        }
    }

    // ==================== HAPUS PROFILE ====================

    @FXML
    private void handleDelete() {
        ButtonType batal = new ButtonType(
            "Batal", ButtonBar.ButtonData.CANCEL_CLOSE
        );

        ButtonType yakin = new ButtonType(
            "Yakin", ButtonBar.ButtonData.OK_DONE
        );

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);

        dialog.setTitle("Hapus Profile");
        dialog.setHeaderText(
            "Apakah Anda yakin ingin menghapus profile?"
        );
        dialog.setContentText(
            "Ini masih demonstrasi UI. Tidak ada data profil yang dihapus."
        );

        dialog.getButtonTypes().setAll(batal, yakin);

        Optional<ButtonType> hasil = dialog.showAndWait();

        if (hasil.isPresent() && hasil.get() == yakin) {
            tampilkanInformasi(
                "Hapus Profile",
                "Tidak ada data profil yang dihapus."
            );
        }
    }

    // ==================== DIALOG ====================

    private void tampilkanPeringatan(String pesan) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Periksa Data");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    private void tampilkanInformasi(String judul, String pesan) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
