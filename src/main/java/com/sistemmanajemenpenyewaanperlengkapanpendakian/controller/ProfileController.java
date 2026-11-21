
package com.sistemmanajemenpenyewaanperlengkapanpendakian.controller;

import com.sistemmanajemenpenyewaanperlengkapanpendakian.Service.AuthService;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.dao.VendorDAO;
import com.sistemmanajemenpenyewaanperlengkapanpendakian.model.Vendor;

import java.io.File;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
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

    // DAO untuk menyimpan perubahan profile.
    private final VendorDAO vendorDAO = new VendorDAO();

    // Vendor yang sedang login.
    private Vendor vendorAktif;

    private String savedStoreName;
    private String savedEmail;
    private String savedLocation;

    private String currentPhotoUri;
    private String savedPhotoUri;

    private boolean editing;

    @FXML
    private void initialize() {

        profileImage.setClip(new Circle(56, 56, 56));

        // Mengambil vendor dari sesi login.
        vendorAktif = AuthService.getVendorAktif();

        if (vendorAktif == null) {

            storeNameField.setText("");
            emailField.setText("");
            locationField.setText("");

            currentPhotoUri = null;
            savedPhotoUri = null;

            aturModeEdit(false);
            return;
        }

        // Mengisi halaman berdasarkan data vendor dari database.
        muatDataVendor();

        aturModeEdit(false);
    }

    // Memuat seluruh informasi vendor.
    private void muatDataVendor() {

        storeNameField.setText(
            vendorAktif.getNamaVendor()
        );

        emailField.setText(
            vendorAktif.getEmail()
        );

        locationField.setText(
            vendorAktif.getLokasiToko() == null
                ? ""
                : vendorAktif.getLokasiToko()
        );

        currentPhotoUri = vendorAktif.getFotoProfile();
        savedPhotoUri = currentPhotoUri;

        muatFoto(currentPhotoUri);

        simpanSnapshot();
    }

    @FXML
    private void handleEdit() {

        if (vendorAktif == null) {
            tampilkanPeringatan("Login vendor diperlukan.");
            return;
        }

        simpanSnapshot();
        aturModeEdit(true);
    }

    // Membentuk username berdasarkan kata pertama dan terakhir.
    private String buatUsername(String namaVendor) {

        String[] kata = namaVendor.trim()
            .toLowerCase(Locale.ROOT)
            .split("\\s+");

        if (kata.length < 2) {
            return null;
        }

        String kataPertama = kata[0].replaceAll("[^a-z0-9]", "");
        String kataTerakhir = kata[kata.length - 1]
            .replaceAll("[^a-z0-9]", "");

        if (kataPertama.isEmpty() || kataTerakhir.isEmpty()) {
            return null;
        }

        return kataPertama + "_" + kataTerakhir;
    }

    @FXML
    private void handleSave() {

        if (vendorAktif == null) {
            tampilkanPeringatan("Login vendor diperlukan.");
            return;
        }

        String nama = storeNameField.getText() == null
            ? ""
            : storeNameField.getText().trim();

        String email = emailField.getText() == null
            ? ""
            : emailField.getText().trim();

        String lokasi = locationField.getText() == null
            ? ""
            : locationField.getText().trim();

        // Nama toko wajib mempunyai minimal dua kata.
        String usernameBaru = buatUsername(nama);

        if (usernameBaru == null) {
            tampilkanPeringatan(
                "Nama toko harus memiliki minimal dua kata "
                + "agar username dapat dibentuk."
            );
            return;
        }

        if (email.isEmpty()) {
            tampilkanPeringatan("Email toko wajib diisi.");
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            tampilkanPeringatan(
                "Masukkan alamat email yang valid."
            );
            return;
        }

        // Lokasi boleh kosong dan akan disimpan sebagai SQL NULL.
        if (lokasi.isEmpty()) {
            lokasi = null;
        }

        // Menyiapkan data yang akan dikirim ke DAO.
        Vendor vendorPerbarui = new Vendor();

        vendorPerbarui.setIdVendor(vendorAktif.getIdVendor());
        vendorPerbarui.setNamaVendor(nama);
        vendorPerbarui.setUsernameAdm(usernameBaru);
        vendorPerbarui.setEmail(email);
        vendorPerbarui.setLokasiToko(lokasi);
        vendorPerbarui.setFotoProfile(currentPhotoUri);

        try {

            // Menyimpan seluruh perubahan profile ke MySQL.
            vendorDAO.updateProfileVendor(vendorPerbarui);

            // Memperbarui objek vendor yang sedang aktif.
            vendorAktif.setNamaVendor(nama);
            vendorAktif.setUsernameAdm(usernameBaru);
            vendorAktif.setEmail(email);
            vendorAktif.setLokasiToko(lokasi);
            vendorAktif.setFotoProfile(currentPhotoUri);

            AuthService.perbaruiVendorAktif(vendorAktif);

            simpanSnapshot();
            aturModeEdit(false);

            // Memperbarui informasi pada sidebar.
            perbaruiTampilanSidebar();

            tampilkanInformasi(
                "Profile berhasil disimpan.",
                "Nama toko dan username berhasil disinkronkan.\n"
                + "Username baru: " + usernameBaru
            );

        } catch (SQLException e) {

            tampilkanKesalahan(
                "Gagal menyimpan profile:\n" + e.getMessage()
            );
        }
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

    // Mengatur tampilan mode baca dan mode edit.
    private void aturModeEdit(boolean aktif) {

        editing = aktif;

        storeNameField.setEditable(aktif && vendorAktif != null);
        emailField.setEditable(aktif && vendorAktif != null);
        locationField.setEditable(aktif && vendorAktif != null);

        // Mode tampilan.
        editButton.setVisible(!aktif);
        editButton.setManaged(!aktif);
        editButton.setDisable(vendorAktif == null);

        // Mode edit.
        uploadButton.setVisible(aktif);
        uploadButton.setManaged(aktif);

        deleteButton.setVisible(aktif);
        deleteButton.setManaged(aktif);

        cancelButton.setVisible(aktif);
        cancelButton.setManaged(aktif);

        saveButton.setVisible(aktif);
        saveButton.setManaged(aktif);

        aturGayaField();
    }

    private void aturGayaField() {

        String gaya = editing
            ? "-fx-background-color: white;"
                + "-fx-border-color: #00643D;"
                + "-fx-border-radius: 6;"
                + "-fx-background-radius: 6;"
                + "-fx-padding: 8 10;"
            : "-fx-background-color: #EEEEEE;"
                + "-fx-border-color: #999999;"
                + "-fx-border-radius: 6;"
                + "-fx-background-radius: 6;"
                + "-fx-padding: 8 10;";

        storeNameField.setStyle(gaya);
        emailField.setStyle(gaya);
        locationField.setStyle(gaya);
    }

    @FXML
    private void handleUploadPhoto() {

        if (!editing || vendorAktif == null) {
            return;
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

        Image previewImage = new Image(
            uri, 112, 112, false, true
        );

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

            try {

                // Foto langsung disimpan ke database.
                vendorDAO.updateFotoProfile(
                    vendorAktif.getIdVendor(), uri
                );

                currentPhotoUri = uri;
                savedPhotoUri = uri;

                vendorAktif.setFotoProfile(uri);

                AuthService.perbaruiVendorAktif(vendorAktif);

                profileImage.setImage(previewImage);

                perbaruiTampilanSidebar();

                tampilkanInformasi(
                    "Foto profil berhasil diperbarui.",
                    "Foto baru sudah disimpan."
                );

            } catch (SQLException e) {

                tampilkanKesalahan(
                    "Gagal menyimpan foto profil:\n" + e.getMessage()
                );
            }
        }
    }

    private void muatFoto(String uri) {

        if (uri == null || uri.trim().isEmpty()) {
            profileImage.setImage(null);
            return;
        }

        Image image = new Image(
            uri, 112, 112, false, true
        );

        if (!image.isError()) {
            profileImage.setImage(image);
        } else {
            profileImage.setImage(null);
        }
    }

    @FXML
    private void handleDelete() {

        if (!editing || vendorAktif == null) {
            return;
        }

        ButtonType batal = new ButtonType(
            "Batal", ButtonBar.ButtonData.CANCEL_CLOSE
        );

        ButtonType yakin = new ButtonType(
            "Yakin", ButtonBar.ButtonData.OK_DONE
        );

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);

        dialog.setTitle("Hapus Foto Profile");
        dialog.setHeaderText(
            "Apakah Anda yakin ingin menghapus foto profil?"
        );

        dialog.setContentText(
            "Tindakan ini hanya menghapus foto profil. "
            + "Akun dan data toko Anda tidak akan dihapus."
        );

        dialog.getButtonTypes().setAll(batal, yakin);

        Optional<ButtonType> hasil = dialog.showAndWait();

        if (hasil.isPresent() && hasil.get() == yakin) {

            try {

                // Menghapus foto saja, bukan akun vendor.
                vendorDAO.updateFotoProfile(
                    vendorAktif.getIdVendor(), null
                );

                currentPhotoUri = null;
                savedPhotoUri = null;

                vendorAktif.setFotoProfile(null);

                AuthService.perbaruiVendorAktif(vendorAktif);

                profileImage.setImage(null);

                perbaruiTampilanSidebar();

                tampilkanInformasi(
                    "Foto profil berhasil dihapus.",
                    "Akun dan data toko tetap tersimpan."
                );

            } catch (SQLException e) {

                tampilkanKesalahan(
                    "Gagal menghapus foto profil:\n" + e.getMessage()
                );
            }
        }
    }

    // Memperbarui username dan foto pada sidebar tanpa reload halaman.
    private void perbaruiTampilanSidebar() {

        if (profileImage.getScene() == null || vendorAktif == null) {
            return;
        }

        Node root = profileImage.getScene().getRoot();

        Node nodeUsername = root.lookup("#labelUsername");

        if (nodeUsername instanceof Label) {
            Label label = (Label) nodeUsername;
            label.setText(vendorAktif.getUsernameAdm());
        }

        Node nodeFoto = root.lookup("#fotoProfile");

        if (nodeFoto instanceof ImageView) {
            ImageView foto = (ImageView) nodeFoto;

            String uri = vendorAktif.getFotoProfile();

            if (uri == null || uri.trim().isEmpty()) {
                foto.setImage(null);
            } else {
                Image image = new Image(
                    uri, 48, 48, true, true
                );

                foto.setImage(image.isError() ? null : image);
            }
        }
    }

    private void tampilkanPeringatan(String pesan) {

        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle("Periksa Data");
        alert.setHeaderText(null);
        alert.setContentText(pesan);

        alert.showAndWait();
    }

    private void tampilkanKesalahan(String pesan) {

        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Kesalahan Profile");
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
