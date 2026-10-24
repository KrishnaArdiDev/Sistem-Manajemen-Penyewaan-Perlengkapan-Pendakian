
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class ProfileController {

    @FXML private ImageView profileImage;
    @FXML private TextField storeNameField;
    @FXML private TextField emailField;
    @FXML private TextField locationField;

    @FXML private Button editButton;
    @FXML private Button uploadButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private static Image fotoProfilSaatIni;

    private String originalName;
    private String originalEmail;
    private String originalLocation;
    private Image originalPhoto;

    private Path originalPhotoPath;

    public static Image getFotoProfilSaatIni() {
        return fotoProfilSaatIni;
    }

    @FXML
    private void initialize() {
        originalName = storeNameField.getText();
        originalEmail = emailField.getText();
        originalLocation = locationField.getText();

        loadDefaultPhoto();
        originalPhoto = profileImage.getImage();

        setEditing(false);
    }

    private void loadDefaultPhoto() {
        try {
            Path folder = Path.of("data", "profile");

            if (Files.isDirectory(folder)) {
                try (var files = Files.list(folder)) {
                    Path savedPhoto = files
                        .filter(Files::isRegularFile)
                        .filter(path -> path.getFileName()
                            .toString()
                            .toLowerCase()
                            .matches("profile\\.(png|jpg|jpeg)"))
                        .findFirst()
                        .orElse(null);

                    if (savedPhoto != null) {
                        Image image = new Image(
                            savedPhoto.toUri().toString(),
                            false
                        );

                        if (!image.isError()) {
                            originalPhotoPath =
                                savedPhoto.toAbsolutePath();
                            updatePhoto(image);
                            return;
                        }
                    }
                }
            }

            var resource = getClass().getResource(
                "/com/sistemmanajemenpenyewaanperlengkapanpendakian/images/profile.png"
            );

            if (resource != null) {
                Image image = new Image(
                    resource.toExternalForm(),
                    false
                );

                if (!image.isError()) {
                    updatePhoto(image);
                    return;
                }
            }

            updatePhoto(null);

        } catch (Exception e) {
            System.err.println(
                "Foto profil gagal dimuat: " + e.getMessage()
            );
        }
    }

    private void updatePhoto(Image image) {
        fotoProfilSaatIni = image;

        if (profileImage != null) {
            profileImage.setImage(image);
        }

        // Perbarui ImageView sidebar pada saat yang sama.
        MainLayoutController.perbaruiFotoProfil(image);
    }

    @FXML
    private void handleEdit() {
        originalName = storeNameField.getText();
        originalEmail = emailField.getText();
        originalLocation = locationField.getText();
        originalPhoto = profileImage.getImage();

        setEditing(true);
    }

    private void setEditing(boolean editing) {
        storeNameField.setEditable(editing);
        emailField.setEditable(editing);
        locationField.setEditable(editing);

        uploadButton.setVisible(editing);
        uploadButton.setManaged(editing);

        saveButton.setVisible(editing);
        saveButton.setManaged(editing);

        cancelButton.setVisible(editing);
        cancelButton.setManaged(editing);

        editButton.setVisible(!editing);
        editButton.setManaged(!editing);
    }

    @FXML
    private void handleSave() {
        String name = storeNameField.getText().trim();
        String email = emailField.getText().trim();
        String location = locationField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || location.isEmpty()) {
            showAlert(
                Alert.AlertType.WARNING,
                "Validasi",
                "Semua informasi toko harus diisi."
            );
            return;
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert(
                Alert.AlertType.WARNING,
                "Validasi",
                "Format email tidak valid."
            );
            return;
        }

        originalName = name;
        originalEmail = email;
        originalLocation = location;
        originalPhoto = profileImage.getImage();

        setEditing(false);

        showAlert(
            Alert.AlertType.INFORMATION,
            "Berhasil",
            "Perubahan profil diterapkan."
        );
    }

    @FXML
    private void handleCancel() {
        storeNameField.setText(originalName);
        emailField.setText(originalEmail);
        locationField.setText(originalLocation);

        updatePhoto(originalPhoto);

        setEditing(false);
    }

    @FXML
    private void handleUploadPhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Pilih Foto Profil");

        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(
                "Gambar", "*.png", "*.jpg", "*.jpeg"
            )
        );

        File selectedFile = chooser.showOpenDialog(
            profileImage.getScene().getWindow()
        );

        if (selectedFile == null) {
            return;
        }

        try {
            Image imageBaru = new Image(
                selectedFile.toURI().toString(),
                false
            );

            if (imageBaru.isError()) {
                showAlert(
                    Alert.AlertType.ERROR,
                    "Gagal",
                    "File yang dipilih bukan gambar yang valid."
                );
                return;
            }

            Path folder = Path.of("data", "profile");
            Files.createDirectories(folder);

            String namaFile = selectedFile.getName();
            String ekstensi = namaFile
                .substring(namaFile.lastIndexOf('.') + 1)
                .toLowerCase();

            Path tujuan = folder.resolve("profile." + ekstensi);

            Files.copy(
                selectedFile.toPath(),
                tujuan,
                StandardCopyOption.REPLACE_EXISTING
            );

            originalPhotoPath = tujuan.toAbsolutePath();

            // Langsung memperbarui foto profil dan sidebar.
            updatePhoto(imageBaru);

        } catch (IOException e) {
            showAlert(
                Alert.AlertType.ERROR,
                "Gagal",
                "Foto profil tidak dapat disimpan: "
                    + e.getMessage()
            );
        }
    }

    @FXML
    private void handleDelete() {
        Alert confirmation = new Alert(
            Alert.AlertType.CONFIRMATION,
            "Apakah Anda yakin ingin menghapus profil?",
            ButtonType.CANCEL,
            ButtonType.OK
        );

        confirmation.setTitle("Konfirmasi Hapus Profil");
        confirmation.setHeaderText(null);

        confirmation.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                storeNameField.clear();
                emailField.clear();
                locationField.clear();

                updatePhoto(null);

                originalName = "";
                originalEmail = "";
                originalLocation = "";
                originalPhoto = null;
                originalPhotoPath = null;

                setEditing(false);

                showAlert(
                    Alert.AlertType.INFORMATION,
                    "Profil",
                    "Data profil di tampilan telah dikosongkan."
                );
            }
        });
    }

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message) {

        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
