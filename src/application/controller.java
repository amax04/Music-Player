package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class controller implements Initializable {
	@FXML
	private Pane pane;
	@FXML
	private Label songLabel;
	@FXML
	private Button playButton,pauseButton,resetButton,previousButton,nextButton;
	@FXML
	private Slider volumeSlider;
	@FXML
	private ProgressBar songProgressBar;
	@FXML
	private ComboBox<String> speedBox;
	
	private File directory;
	private File[] files;
	
	private ArrayList<File> songs;
	
	private int songNumber;
	private int[] speeds= {25,50,75,100,125,150,175,200};
	
	private Timer timer;
	private TimerTask task;
	private boolean running;
	
	private Media media;
	private MediaPlayer mediaplayer;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		songs = new ArrayList<File>();
		
		directory =new File("Music");
		
		files = directory.listFiles();
		if(files != null)
		{
			for(File file :files)
			{
				songs.add(file);
				System.out.println(file);
			}
		}
		
		media = new Media(songs.get(songNumber).toURI().toString());
		mediaplayer = new MediaPlayer(media);
		songLabel.setText(songs.get(songNumber).getName());
		
		for(int i=0;i<speeds.length;i++)
		{
			speedBox.getItems().add(Integer.toString(speeds[i])+"%");
		}
		
		speedBox.setOnAction(this::changeSpeed);
		
		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				
				mediaplayer.setVolume(volumeSlider.getValue() * 0.01);
			}
		});	
		
		
	}
	
	public void playMedia()
	{
		beginTimer();
		mediaplayer.setVolume(volumeSlider.getValue() * 0.01);
		changeSpeed(null);
		mediaplayer.play();
	}
	
	public void pauseMedia()
	{
		cancelTimer();
		mediaplayer.pause();
	}
	public void resetMedia()
	{	
		songProgressBar.setProgress(0);
		mediaplayer.seek(Duration.seconds(0));
	}
	public void nextMedia()
	{
		if(songNumber<songs.size()-1)
		{
			songNumber++;
			
			mediaplayer.stop();
			
			if(running) {
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaplayer = new MediaPlayer(media);
			songLabel.setText(songs.get(songNumber).getName());		
			
			playMedia();
		}
		else
		{
			songNumber =0;
			
			mediaplayer.stop();
			
			if(running) {
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaplayer = new MediaPlayer(media);
			songLabel.setText(songs.get(songNumber).getName());	
			
			playMedia();
		}
		
	}
	public void previousMedia()
	{
		if(songNumber > 0)
		{
			songNumber--;
			
			mediaplayer.stop();
			
			if(running) {
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaplayer = new MediaPlayer(media);
			songLabel.setText(songs.get(songNumber).getName());		
			
			playMedia();
		}
		else
		{
			songNumber=songs.size()-1;
			
			mediaplayer.stop();
			
			if(running) {
				cancelTimer();
			}
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaplayer = new MediaPlayer(media);
			songLabel.setText(songs.get(songNumber).getName());		
			
			playMedia();
		}
	}
	public void changeSpeed(ActionEvent even)
	{	
		if(speedBox.getValue()==null)
		{
			mediaplayer.setRate(1);
		}
		else {
		//mediaplayer.setRate(Integer.parseInt(speedBox.getValue())* 0.01);
		mediaplayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
		}
	}
	public void beginTimer()
	{
		timer = new Timer();
		 task = new TimerTask() {
			public void run() {
				running =true;
				double current=mediaplayer.getCurrentTime().toSeconds();
				double end=media.getDuration().toSeconds();
				songProgressBar.setProgress(current/end);
				
				if(current/end ==1) {
					cancelTimer();
				}
			}
		};
		timer.scheduleAtFixedRate(task,0,1000);
		
	}
	public void cancelTimer()
	{
		running=false;
		timer.cancel();
	}
}
