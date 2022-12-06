package com.example.mymediaplayer;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MediaPlayerController implements Initializable {
    private MediaPlayer mediaPlayer;

    @FXML
    private MediaView mediaView;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Slider timeSlider;

    @FXML
    private Label timeLabel;

    @FXML
    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        String path = file.toURI().toString();

        if (path != null) {
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            if (!FilenameUtils.getExtension(file.getPath()).equals("mp3")) {
                DoubleProperty widthProp = mediaView.fitWidthProperty();
                DoubleProperty heightProp = mediaView.fitHeightProperty();

                widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
                heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
            }

            volumeSlider.setValue(mediaPlayer.getVolume() * 100);
            volumeSlider.valueProperty().addListener(observable -> mediaPlayer.setVolume(volumeSlider.getValue() / 100));

            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                timeSlider.setValue(newValue.toSeconds());
                timeLabel.setText(secondsToDate((int) newValue.toSeconds()) + " / " +
                        secondsToDate((int) media.getDuration().toSeconds()));
            });

            timeSlider.setOnMousePressed(event -> mediaPlayer.seek(Duration.seconds(timeSlider.getValue())));

            timeSlider.setOnMouseDragged(event -> mediaPlayer.seek(Duration.seconds(timeSlider.getValue())));

            mediaPlayer.setOnReady(() -> {
                Duration total = media.getDuration();
                timeSlider.setMax(total.toSeconds());
            });

            mediaPlayer.play();
        }
    }

    public void pauseVideo() {
        mediaPlayer.pause();
    }

    public void stopVideo() {
        mediaPlayer.stop();
    }

    public void playVideo() {
        mediaPlayer.play();
        mediaPlayer.setRate(1);
    }

    public void forward() {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(5)));
    }

    public void backward() {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(-5)));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        openFile();
    }

    private String secondsToDate(int seconds) {
        long hour = seconds / 3600,
                min = seconds / 60 % 60,
                sec = seconds % 60;
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }
}