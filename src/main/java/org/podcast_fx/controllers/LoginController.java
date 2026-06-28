package org.podcast_fx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.podcast_fx.services.ApiService;
import org.podcast_fx.models.TokenResponse;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.podcast_fx.models.Episode;



public class LoginController {
    @FXML
    private Label welcomeText;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    private final ApiService apiService = new ApiService();

    @FXML
    protected void onLoginButtonClick() {
        String myusername = username.getText();
        String mypassword = password.getText();

        try {
            TokenResponse result = apiService.login(myusername, mypassword);
            welcomeText.setText("Login successful");

        } catch (Exception e) {
            welcomeText.setText("Login failed: " + e.getMessage());
        }
    }

    @FXML
    protected void onLoadEpisodesClick() {
        try {
            Episode[] episodes = apiService.getEpisodeList();
            episodeTable.setItems(FXCollections.observableArrayList(episodes));
            welcomeText.setText("Episodes loaded");
        } catch (Exception e) {
            welcomeText.setText("Load failed: " + e.getMessage());
        }
    }

    @FXML private TableView<Episode> episodeTable;
    @FXML private TableColumn<Episode, Long> idColumn;
    @FXML private TableColumn<Episode, String> titleColumn;
    @FXML private TableColumn<Episode, String> showNameColumn;
    @FXML private TableColumn<Episode, String> categoryColumn;
    @FXML private TableColumn<Episode, String> statusColumn;
    @FXML private TableColumn<Episode, Integer> ratingColumn;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        showNameColumn.setCellValueFactory(new PropertyValueFactory<>("showName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
    }

    @FXML private TextField titleField;
    @FXML private TextField showNameField;
    @FXML private TextField hostsField;
    @FXML private TextField categoryField;
    @FXML private TextField statusField;
    @FXML private TextField ratingField;
    @FXML private TextField idField;
    @FXML private TextField restorePathField;

    @FXML
    protected void onSaveEpisodeClick() {
        try {
            Episode episode = new Episode();
            episode.setTitle(titleField.getText());
            episode.setShowName(showNameField.getText());
            episode.setHosts(hostsField.getText());
            episode.setCategory(categoryField.getText());
            episode.setStatus(statusField.getText());

            episode.setGuests("string");
            episode.setNetwork("Spotify");
            episode.setEpisodeNumber("S1E1");
            episode.setSeasonNumber(1);
            episode.setListeningContext("COMMUTE");
            episode.setPlaybackSpeed("SPEED_0_75X");
            episode.setDurationMinutes(60);
            episode.setMinutesListened(0);
            episode.setContentQuality(5);
            episode.setAudioQuality(5);
            episode.setHostChemistry(5);
            episode.setRewatchValue(5);
            episode.setExplicitContent(false);
            episode.setSubscribed(false);
            episode.setBookmarkedQuote(false);
            episode.setRecommendToFriend(false);
            episode.setReleaseDate("2026-06-28");
            episode.setListenedDate("2026-06-28");
            episode.setAddedDate("2026-06-28");
            episode.setMoodTags("JavaFX test");
            episode.setMainTopic("Test topic");
            episode.setMemorableQuote("Test quote");
            episode.setKeyTakeaway("Test takeaway");
            episode.setReview("Test review");
            episode.setPersonalNotes("Test notes");

            if (!ratingField.getText().isBlank()) {
                episode.setRating(Integer.parseInt(ratingField.getText()));
            }

            apiService.createEpisode(episode);

            Episode[] episodes = apiService.getEpisodeList();
            episodeTable.setItems(FXCollections.observableArrayList(episodes));

            welcomeText.setText("Episode saved");
        } catch (Exception e) {
            welcomeText.setText("Save failed: " + e.getMessage());
        }
    }

    @FXML
    protected void onGetByIdClick() {
        try {
            Long id = Long.parseLong(idField.getText());
            Episode episode = apiService.getEpisodeByIdParsed(id);
            episodeTable.setItems(FXCollections.observableArrayList(episode));
            welcomeText.setText("Episode loaded");
        } catch (Exception e) {
            welcomeText.setText("Get by ID failed: " + e.getMessage());
        }
    }

    @FXML
    protected void onDeleteByIdClick() {
        try {
            Long id = Long.parseLong(idField.getText());

            apiService.deleteEpisode(id);

            Episode[] episodes = apiService.getEpisodeList();
            episodeTable.setItems(FXCollections.observableArrayList(episodes));

            welcomeText.setText("Episode deleted");
        } catch (Exception e) {
            welcomeText.setText("Delete failed: " + e.getMessage());
        }
    }

    @FXML
    protected void onUpdateByIdClick() {
        try {
            Long id = Long.parseLong(idField.getText());

            Episode episode = new Episode();
            episode.setTitle(titleField.getText());
            episode.setShowName(showNameField.getText());
            episode.setHosts(hostsField.getText());
            episode.setCategory(categoryField.getText());
            episode.setStatus(statusField.getText());

            episode.setGuests("string");
            episode.setNetwork("Spotify");
            episode.setEpisodeNumber("S1E1");
            episode.setSeasonNumber(1);
            episode.setListeningContext("COMMUTE");
            episode.setPlaybackSpeed("SPEED_0_75X");
            episode.setDurationMinutes(60);
            episode.setMinutesListened(0);
            episode.setContentQuality(5);
            episode.setAudioQuality(5);
            episode.setHostChemistry(5);
            episode.setRewatchValue(5);
            episode.setExplicitContent(false);
            episode.setSubscribed(false);
            episode.setBookmarkedQuote(false);
            episode.setRecommendToFriend(false);
            episode.setReleaseDate("2026-06-28");
            episode.setListenedDate("2026-06-28");
            episode.setAddedDate("2026-06-28");
            episode.setMoodTags("JavaFX test");
            episode.setMainTopic("Test topic");
            episode.setMemorableQuote("Test quote");
            episode.setKeyTakeaway("Test takeaway");
            episode.setReview("Test review");
            episode.setPersonalNotes("Test notes");

            if (!ratingField.getText().isBlank()) {
                episode.setRating(Integer.parseInt(ratingField.getText()));
            }

            apiService.updateEpisode(id, episode);

            Episode[] episodes = apiService.getEpisodeList();
            episodeTable.setItems(FXCollections.observableArrayList(episodes));

            welcomeText.setText("Episode updated");
        } catch (Exception e) {
            welcomeText.setText("Update failed: " + e.getMessage());
        }
    }

    @FXML
    protected void onBackupDatabaseClick() {
        try {
            String result = apiService.backupDatabase();
            System.out.println("BACKUP RESPONSE = " + result);
            welcomeText.setText("Backup created: " + result);
        } catch (Exception e) {
            welcomeText.setText("Backup failed: " + e.getMessage());
        }
    }

    @FXML
    protected void onRestoreDatabaseClick() {
        try {
            String path = restorePathField.getText();
            String result = apiService.restoreDatabase(path);
            welcomeText.setText("Restore completed: " + result);
        } catch (Exception e) {
            welcomeText.setText("Restore failed: " + e.getMessage());
        }
    }

}
